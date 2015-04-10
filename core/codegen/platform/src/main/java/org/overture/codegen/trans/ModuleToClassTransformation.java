package org.overture.codegen.trans;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SDeclCG;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFuncDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.cgast.declarations.ANamedTraceDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AStateDeclCG;
import org.overture.codegen.cgast.declarations.ATypeDeclCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVarExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.AUndefinedExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.statements.AFieldStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.ir.IRConstants;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class ModuleToClassTransformation extends DepthFirstAnalysisAdaptor
		implements ITotalTransformation
{
	private AClassDeclCG clazz = null;
	private TransAssistantCG transAssistant;

	public ModuleToClassTransformation(TransAssistantCG transAssistant)
	{
		this.transAssistant = transAssistant;
	}

	@Override
	public void caseAModuleDeclCG(AModuleDeclCG node) throws AnalysisException
	{
		clazz = new AClassDeclCG();
		clazz.setSourceNode(node.getSourceNode());
		clazz.setAccess(IRConstants.PUBLIC);
		clazz.setName(node.getName());

		makeStateAccessExplicit(node);

		for (int i = 0; i < node.getDecls().size(); i++)
		{
			// Note that this declaration is disconnected from the IR
			SDeclCG declCopy = node.getDecls().get(i).clone();

			if (declCopy instanceof AMethodDeclCG)
			{
				AMethodDeclCG method = (AMethodDeclCG) declCopy;
				method.setAccess(IRConstants.PUBLIC);
				method.setStatic(true);

				clazz.getMethods().add(method);

			} else if (declCopy instanceof AFuncDeclCG)
			{
				// Functions are static by definition
				AFuncDeclCG func = (AFuncDeclCG) declCopy;
				func.setAccess(IRConstants.PUBLIC);

				clazz.getFunctions().add(func);

			} else if (declCopy instanceof ATypeDeclCG)
			{
				ATypeDeclCG typeDecl = (ATypeDeclCG) declCopy;
				typeDecl.setAccess(IRConstants.PUBLIC);

				clazz.getTypeDecls().add(typeDecl);

			} else if (declCopy instanceof AStateDeclCG)
			{
				AStateDeclCG stateDecl = (AStateDeclCG) declCopy;

				ARecordDeclCG record = new ARecordDeclCG();
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

				AFieldDeclCG stateField = new AFieldDeclCG();
				stateField.setAccess(IRConstants.PRIVATE);
				stateField.setFinal(true);
				// We need the context the declaration appears in so we cannot use the copy
				stateField.setInitial(getInitExp((AStateDeclCG) node.getDecls().get(i)));
				stateField.setName(stateDecl.getName());
				stateField.setStatic(true);
				stateField.setType(stateType);
				stateField.setVolatile(false);

				clazz.getFields().add(stateField);

			} else if (declCopy instanceof ANamedTraceDeclCG)
			{
				clazz.getTraces().add((ANamedTraceDeclCG) declCopy);

			} else if (declCopy instanceof AFieldDeclCG)
			{
				AFieldDeclCG field = (AFieldDeclCG) declCopy;
				field.setStatic(true);

				clazz.getFields().add(field);
			} else
			{
				Logger.getLog().printErrorln("Got unexpected declaration: "
						+ declCopy + " in '" + this.getClass().getSimpleName()
						+ "'");
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
			@Override
			public void caseAIdentifierVarExpCG(AIdentifierVarExpCG node)
					throws AnalysisException
			{
				if (!node.getIsLocal()
						&& !node.getName().equals(stateDecl.getName()))
				{
					// First condition: 'not local' means we are accessing state
					// Second condition: if the variable represents a field of the state then it must be explicit
					// TODO: This assumes hiding to be removed?
					AExplicitVarExpCG eVar = new AExplicitVarExpCG();
					eVar.setClassType(transAssistant.consClassType(stateDecl.getName()));
					eVar.setIsLambda(false);
					eVar.setIsLocal(node.getIsLocal());
					eVar.setName(node.getName());
					eVar.setSourceNode(node.getSourceNode());
					eVar.setTag(node.getTag());
					eVar.setType(node.getType().clone());

					transAssistant.replaceNodeWith(node, eVar);
				}
			}
		});

		module.apply(new DepthFirstAnalysisAdaptor()
		{
			@Override
			public void caseAIdentifierStateDesignatorCG(
					AIdentifierStateDesignatorCG node) throws AnalysisException
			{
				// 'not local' means we are accessing state
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

	@Override
	public INode getResult()
	{
		return clazz;
	}
}
