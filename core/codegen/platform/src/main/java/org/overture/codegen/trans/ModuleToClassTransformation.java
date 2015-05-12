package org.overture.codegen.trans;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SImportCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AAllImportCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFromModuleImportsCG;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.cgast.declarations.AFunctionValueImportCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.cgast.declarations.AModuleImportsCG;
import org.overture.codegen.cgast.declarations.ANamedTraceDeclCG;
import org.overture.codegen.cgast.declarations.AOperationValueImportCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AStateDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.declarations.ATypeImportCG;
import org.overture.codegen.cgast.declarations.AValueValueImportCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.AUndefinedExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.statements.AFieldStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class ModuleToClassTransformation extends DepthFirstAnalysisAdaptor
		implements ITotalTransformation
{
	private AClassDeclCG clazz = null;
	
	private IRInfo info;
	private TransAssistantCG transAssistant;
	private List<AModuleDeclCG> allModules;

	public ModuleToClassTransformation(IRInfo info, TransAssistantCG transAssistant, List<AModuleDeclCG> allModules)
	{
		this.info = info;
		this.transAssistant = transAssistant;
		this.allModules = allModules;
	}

	@Override
	public void caseAModuleDeclCG(AModuleDeclCG node) throws AnalysisException
	{
		clazz = new AClassDeclCG();
		clazz.setSourceNode(node.getSourceNode());
		clazz.setAccess(IRConstants.PUBLIC);
		clazz.setName(node.getName());

		makeStateAccessExplicit(node);
		handleImports(node.getImport(), clazz);
		
		// Wrap declarations in a new list to avoid a concurrent modifications
		// exception when moving the module declarations to the class
		for (SDeclCG decl : new LinkedList<>(node.getDecls()))
		{
			if (decl instanceof AMethodDeclCG)
			{
				AMethodDeclCG method = (AMethodDeclCG) decl;
				method.setAccess(IRConstants.PUBLIC);
				method.setStatic(true);

				clazz.getMethods().add(method);

			} else if (decl instanceof AFuncDeclCG)
			{
				// Functions are static by definition
				AFuncDeclCG func = (AFuncDeclCG) decl;
				func.setAccess(IRConstants.PUBLIC);

				clazz.getFunctions().add(func);

			} else if (decl instanceof ATypeDeclCG)
			{
				ATypeDeclCG typeDecl = (ATypeDeclCG) decl;
				typeDecl.setAccess(IRConstants.PUBLIC);

				clazz.getTypeDecls().add(typeDecl);

			} else if (decl instanceof AStateDeclCG)
			{
				// Handle this as the last thing since it may depend on value definitions
				continue;
			} else if (decl instanceof ANamedTraceDeclCG)
			{
				clazz.getTraces().add((ANamedTraceDeclCG) decl);

			} else if (decl instanceof AFieldDeclCG)
			{
				AFieldDeclCG field = (AFieldDeclCG) decl;
				field.setAccess(IRConstants.PUBLIC);
				field.setStatic(true);

				clazz.getFields().add(field);
			} else
			{
				Logger.getLog().printErrorln("Got unexpected declaration: "
						+ decl + " in '" + this.getClass().getSimpleName()
						+ "'");
			}
		}
		
		AStateDeclCG stateDecl = getStateDecl(node);

		if (stateDecl != null)
		{
			ARecordDeclCG record = new ARecordDeclCG();
			record.setSourceNode(stateDecl.getSourceNode());
			record.setName(stateDecl.getName());

			for (AFieldDeclCG field : stateDecl.getFields())
			{
				record.getFields().add(field.clone());
			}

			ATypeDeclCG typeDecl = new ATypeDeclCG();
			typeDecl.setAccess(IRConstants.PUBLIC);
			typeDecl.setDecl(record);

			clazz.getTypeDecls().add(typeDecl);

			ATypeNameCG typeName = new ATypeNameCG();
			typeName.setName(stateDecl.getName());
			typeName.setDefiningClass(clazz.getName());

			ARecordTypeCG stateType = new ARecordTypeCG();
			stateType.setName(typeName);
			
			if(stateDecl.getInvDecl() != null)
			{
				clazz.setInvariant(stateDecl.getInvDecl().clone());
			}

			// The state field can't be final since you are allow to assign to it in
			// VDM-SL, e.g. St := mk_St(...)
			clazz.getFields().add(transAssistant.consField(IRConstants.PRIVATE, stateType, stateDecl.getName(), getInitExp(stateDecl)));
		}
	}

	private void handleImports(final AModuleImportsCG moduleImports, final AClassDeclCG clazz) throws AnalysisException
	{
		//name = moduleImports.getName();
		
		if(moduleImports == null)
		{
			return;
		}
		
		for(AFromModuleImportsCG fromImports : moduleImports.getImports())
		{
			//String fromName = fromImports.getName();
			
			for(List<SImportCG> sig : fromImports.getSignatures())
			{
				for(SImportCG imp : sig)
				{
					// TODO Implement the import analysis cases
					imp.apply(new DepthFirstAnalysisAdaptor()
					{
						@Override
						public void caseAAllImportCG(AAllImportCG node)
								throws AnalysisException
						{
						}
						
						@Override
						public void caseATypeImportCG(ATypeImportCG node)
								throws AnalysisException
						{
						}
						
						@Override
						public void caseAFunctionValueImportCG(
								AFunctionValueImportCG node)
								throws AnalysisException
						{
						}
						
						@Override
						public void caseAOperationValueImportCG(
								AOperationValueImportCG node)
								throws AnalysisException
						{
						}
						
						@Override
						public void caseAValueValueImportCG(
								AValueValueImportCG node)
								throws AnalysisException
						{
							/*
							String renamed = node.getRenamed();
							
							if (renamed != null)
							{
								//STypeCG impType = node.getImportType();
								String from = node.getFromModuleName();
								String name = node.getName();

								AFieldDeclCG impFieldCopy = getValue(name, from).clone();
								impFieldCopy.setAccess(IRConstants.PUBLIC);
								impFieldCopy.setName(renamed);

								clazz.getFields().add(impFieldCopy);
								
								//clazz.getFields().add(transAssistant.consConstField(access, type, fromName, initExp));

							}*/
						}
					});
				}
			}
		}
	}

	private void makeStateAccessExplicit(final AModuleDeclCG module)
			throws AnalysisException
	{
		final AStateDeclCG stateDecl = getStateDecl(module);

		if (stateDecl == null)
		{
			// Nothing to do
			return;
		}

		module.apply(new DepthFirstAnalysisAdaptor()
		{
			private boolean inPreOrPost = false;

			@Override
			public void caseAFuncDeclCG(AFuncDeclCG node)
					throws AnalysisException
			{
				handleMethodOrFunc(node.getBody(), node.getPreCond(), node.getPostCond());
			}
			
			@Override
			public void caseAMethodDeclCG(AMethodDeclCG node)
					throws AnalysisException
			{
				handleMethodOrFunc(node.getBody(), node.getPreCond(), node.getPostCond());
			}

			private void handleMethodOrFunc(INode body, SDeclCG preCond,
					SDeclCG postCond) throws AnalysisException
			{
				if(body != null)
				{
					body.apply(this);
				}
				
				handleCond(preCond);
				handleCond(postCond);
			}

			private void handleCond(SDeclCG cond) throws AnalysisException
			{
				if(cond != null)
				{
					inPreOrPost = true;
					cond.apply(this);
					inPreOrPost = false;
				}
			}
			
			@Override
			public void caseAIdentifierVarExpCG(AIdentifierVarExpCG node)
					throws AnalysisException
			{
				if (isOldFieldRead(stateDecl, node) /*1*/
						|| isFieldRead(stateDecl, node) /*2*/
						|| isFieldReadInPreOrPost(stateDecl, node) /*3*/)
				{
					// Given a state named 'St' with a field named 'f'
					// Note: If a variable is 'old' then it is local since state is passed
					// as an argument to the post condition function
					
					// Also note that the IR uses underscore to denote old names: _St corresponds to St~ (in VDM)
					// and _f corresponds to f~ (in VDM)
					
					// /*1*/ In case we are in a post condition the variable expression may represent a
					// field of the old state, i.e._f (or f~ in VDM)
					//
					// /*3*/ Another possibility is that the variable expression represents
					// a field of the current state in a pre or post condition of a function or
					// an operation. This is a special case since the state field can be
					// read as 'f' although it is only the entire state that is being passed as an argument
					// to the function. Below is a Java example showin this (result, old state,
					// current state):
					//
					// public static Boolean post_op(final Number RESULT, final St _St, final St St)
					//
					// /*2*/ Any other place in the model:
					// The variable expression represents a field of the current state
					//
					// If /*1*/ or /*2*/ or /*3*/ is true we make the field access explicit since IR modules
					// use a single field to represent state.
					//
					AFieldExpCG fieldExp = new AFieldExpCG();
					fieldExp.setSourceNode(node.getSourceNode());
					fieldExp.setTag(node.getTag());
					fieldExp.setType(node.getType().clone());
					setFieldNames(fieldExp, stateDecl, node.getName());

					transAssistant.replaceNodeWith(node, fieldExp);
				}
			}

			private boolean isFieldReadInPreOrPost(
					final AStateDeclCG stateDecl, AIdentifierVarExpCG node)
			{
				if(!inPreOrPost)
				{
					return false;
				}
				
				boolean matches = false;
				for(AFieldDeclCG f : stateDecl.getFields())
				{
					if(f.getName().equals(node.getName()))
					{
						matches = true;
						break;
					}
				}
				
				return matches;
			}
			
			private boolean isFieldRead(
					final AStateDeclCG stateDecl, AIdentifierVarExpCG node)
			{
				return !node.getIsLocal()
						&& !node.getName().equals(stateDecl.getName());
			}

			private boolean isOldFieldRead(final AStateDeclCG stateDecl,
					AIdentifierVarExpCG node)
			{
				return info.getExpAssistant().isOld(node.getName())
						&& !node.getName().equals("_" + stateDecl.getName());
			}

			private void setFieldNames(AFieldExpCG field, AStateDeclCG stateDecl, String name)
			{
				ARecordTypeCG recType = getRecType(stateDecl);
				String stateName = stateDecl.getName();
				
				if(info.getExpAssistant().isOld(name))
				{
					field.setObject(transAssistant.consIdentifierVar("_" + stateName, recType));
					field.setMemberName(info.getExpAssistant().oldNameToCurrentName(name));	
				}
				else
				{
					field.setObject(transAssistant.consIdentifierVar(stateName, recType));
					field.setMemberName(name);
				}
			}
		});

		module.apply(new DepthFirstAnalysisAdaptor()
		{
			@Override
			public void caseAIdentifierStateDesignatorCG(
					AIdentifierStateDesignatorCG node) throws AnalysisException
			{
				if (!node.getIsLocal()
						&& !node.getName().equals(stateDecl.getName()))
				{
					ARecordTypeCG stateType = getRecType(stateDecl);

					AIdentifierStateDesignatorCG idState = new AIdentifierStateDesignatorCG();
					idState.setClassName(null);
					idState.setExplicit(false);
					idState.setIsLocal(false);
					idState.setName(stateDecl.getName());
					idState.setType(stateType);

					AFieldStateDesignatorCG field = new AFieldStateDesignatorCG();
					field.setField(node.getName());
					field.setObject(idState);
					for (AFieldDeclCG f : stateDecl.getFields())
					{
						if (f.getName().equals(node.getName()))
						{
							field.setType(f.getType().clone());
						}
					}

					transAssistant.replaceNodeWith(node, field);
				}
			}
		});
	}

	public AStateDeclCG getStateDecl(AModuleDeclCG module)
	{
		for (SDeclCG decl : module.getDecls())
		{
			if (decl instanceof AStateDeclCG)
			{
				return (AStateDeclCG) decl;
			}
		}

		return null;
	}

	private SExpCG getInitExp(AStateDeclCG stateDecl)
	{
		if (stateDecl.getInitExp() instanceof AEqualsBinaryExpCG)
		{
			AEqualsBinaryExpCG eqExp = (AEqualsBinaryExpCG) stateDecl.getInitExp();

			return eqExp.getRight().clone();
		} else
		{
			ANewExpCG defaultRecInit = new ANewExpCG();
			defaultRecInit.setName(getTypeName(stateDecl));
			defaultRecInit.setType(getRecType(stateDecl));

			for (int i = 0; i < stateDecl.getFields().size(); i++)
			{
				defaultRecInit.getArgs().add(new AUndefinedExpCG());
			}

			return defaultRecInit;
		}
	}

	private ARecordTypeCG getRecType(final AStateDeclCG stateDecl)
	{
		ARecordTypeCG stateType = new ARecordTypeCG();
		stateType.setName(getTypeName(stateDecl));

		return stateType;
	}

	private ATypeNameCG getTypeName(final AStateDeclCG stateDecl)
	{
		ATypeNameCG stateName = new ATypeNameCG();
		stateName.setDefiningClass(getEnclosingModuleName(stateDecl));
		stateName.setName(stateDecl.getName());

		return stateName;
	}

	private String getEnclosingModuleName(AStateDeclCG stateDecl)
	{
		AModuleDeclCG module = stateDecl.getAncestor(AModuleDeclCG.class);

		if (module != null)
		{
			return module.getName();
		} else
		{
			Logger.getLog().printErrorln("Could not find enclosing module name of state declaration "
					+ stateDecl.getName()
					+ " in '"
					+ this.getClass().getSimpleName() + "'");
			return null;
		}
	}
	
	@SuppressWarnings("unused")
	private AFieldDeclCG getValue(String fieldName, String moduleName)
	{
		for (AModuleDeclCG module : allModules)
		{
			if (module.getName().equals(moduleName))
			{
				for (SDeclCG decl : module.getDecls())
				{
					if (decl instanceof AFieldDeclCG)
					{
						AFieldDeclCG fieldDecl = (AFieldDeclCG) decl;
						if (fieldDecl.getName().equals(fieldName))
						{
							return fieldDecl;
						}
					}
				}
			}
		}

		Logger.getLog().printErrorln("Could not find field " + fieldName
				+ " in module " + moduleName + " in '"
				+ this.getClass().getSimpleName() + "'");

		return null;
	}

	@Override
	public INode getResult()
	{
		return clazz;
	}
}
