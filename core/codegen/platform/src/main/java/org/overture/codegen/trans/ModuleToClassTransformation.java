package org.overture.codegen.trans;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.ir.PCG;
import org.overture.codegen.ir.SDeclCG;
import org.overture.codegen.ir.SExpCG;
import org.overture.codegen.ir.SImportCG;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AAllImportCG;
import org.overture.codegen.ir.declarations.ADefaultClassDeclCG;
import org.overture.codegen.ir.declarations.AFieldDeclCG;
import org.overture.codegen.ir.declarations.AFromModuleImportsCG;
import org.overture.codegen.ir.declarations.AFuncDeclCG;
import org.overture.codegen.ir.declarations.AFunctionValueImportCG;
import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.declarations.AModuleDeclCG;
import org.overture.codegen.ir.declarations.AModuleImportsCG;
import org.overture.codegen.ir.declarations.ANamedTraceDeclCG;
import org.overture.codegen.ir.declarations.AOperationValueImportCG;
import org.overture.codegen.ir.declarations.ARecordDeclCG;
import org.overture.codegen.ir.declarations.AStateDeclCG;
import org.overture.codegen.ir.declarations.ATypeDeclCG;
import org.overture.codegen.ir.declarations.ATypeImportCG;
import org.overture.codegen.ir.declarations.AValueValueImportCG;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.ir.expressions.ANewExpCG;
import org.overture.codegen.ir.expressions.AUndefinedExpCG;
import org.overture.codegen.ir.name.ATypeNameCG;
import org.overture.codegen.ir.types.ARecordTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class ModuleToClassTransformation extends DepthFirstAnalysisAdaptor
		implements ITotalTransformation
{
	private ADefaultClassDeclCG clazz = null;
	
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
		clazz = new ADefaultClassDeclCG();
		clazz.setSourceNode(node.getSourceNode());
		clazz.setAccess(IRConstants.PUBLIC);
		clazz.setName(node.getName());

		// Prevent instantiation of the class
		AMethodDeclCG privConstructor = info.getDeclAssistant().consDefaultContructor(node.getName());
		privConstructor.setAccess(IRConstants.PRIVATE);
		clazz.getMethods().add(privConstructor);
		
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

			if(stateDecl.getInvDecl() != null)
			{
				// The state invariant constrains the type of the state
				// see https://github.com/overturetool/overture/issues/459 
				record.setInvariant(stateDecl.getInvDecl().clone());
			}

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

			// The state field can't be final since you are allow to assign to it in
			// VDM-SL, e.g. St := mk_St(...)
			clazz.getFields().add(transAssistant.consField(IRConstants.PRIVATE, stateType, stateDecl.getName(), getInitExp(stateDecl)));
		}
		
		info.removeModule(node.getName());
		info.addClass(clazz);
	}

	private void handleImports(final AModuleImportsCG moduleImports, final ADefaultClassDeclCG clazz) throws AnalysisException
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

		module.apply(new SlStateAccessTrans(stateDecl, info, transAssistant));
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
			defaultRecInit.setName(transAssistant.getTypeName(stateDecl));
			defaultRecInit.setType(transAssistant.getRecType(stateDecl));

			for (int i = 0; i < stateDecl.getFields().size(); i++)
			{
				defaultRecInit.getArgs().add(new AUndefinedExpCG());
			}

			return defaultRecInit;
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
	public PCG getResult()
	{
		return clazz;
	}
}
