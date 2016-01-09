package org.overture.codegen.trans;

import java.util.HashMap;
import java.util.Map;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ACallStm;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.APlainCallStmCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.ir.IRGeneratedTag;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class ConstructorTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantCG assist;
	
	// To look up object initializer call names
	private Map<AExplicitOperationDefinition, String> objectInitCallNames;

	// Object initialization call prefix
	private String objectInitCallPrefix;

	public ConstructorTrans(TransAssistantCG assist, String objectInitCallPrefix)
	{
		this.assist = assist;
		this.objectInitCallPrefix = objectInitCallPrefix;
		this.objectInitCallNames = new HashMap<AExplicitOperationDefinition, String>();
	}

	@Override
	public void caseAMethodDeclCG(AMethodDeclCG node) throws AnalysisException
	{
		if (node.getIsConstructor())
		{
			String initName = getInitName(node);

			if (initName == null)
			{
				return;
			}

			AMethodDeclCG objInitializer = node.clone();
			objInitializer.setSourceNode(node.getSourceNode());
			objInitializer.setTag(new IRGeneratedTag(getClass().getName()));
			objInitializer.setName(initName);
			objInitializer.getMethodType().setResult(new AVoidTypeCG());
			objInitializer.setIsConstructor(false);
			objInitializer.setPreCond(null);
			objInitializer.setPostCond(null);

			ADefaultClassDeclCG classCg = node.getAncestor(ADefaultClassDeclCG.class);

			if (classCg == null)
			{
				Logger.getLog().printErrorln("Could not find enclosing class of constructor " + node.getName() + " in '"
						+ this.getClass().getSimpleName() + "'");
				return;
			}

			classCg.getMethods().addFirst(objInitializer);

			// Apply transformation recursively
			objInitializer.apply(this);

			APlainCallStmCG initCall = new APlainCallStmCG();
			initCall.setType(objInitializer.getMethodType().getResult().clone());
			initCall.setClassType(null);
			initCall.setName(initName);

			for (AFormalParamLocalParamCG param : node.getFormalParams())
			{
				SPatternCG pattern = param.getPattern();

				if (pattern instanceof AIdentifierPatternCG)
				{
					AIdentifierPatternCG idPattern = (AIdentifierPatternCG) pattern;

					AIdentifierVarExpCG var = new AIdentifierVarExpCG();
					var.setIsLocal(true);
					var.setType(param.getType().clone());
					var.setName(idPattern.getName());
					var.setIsLambda(false);
					var.setSourceNode(pattern.getSourceNode());

					initCall.getArgs().add(var);
				} else
				{
					Logger.getLog().printErrorln("Expected all parameters to be identifier patterns by now in '"
							+ this.getClass().getSimpleName() + "'. Got: " + pattern);
				}
			}

			node.setBody(initCall);
		}

		if (node.getBody() != null)
		{
			node.getBody().apply(this);
		}
	}

	@Override
	public void caseAPlainCallStmCG(APlainCallStmCG node) throws AnalysisException
	{
		String initName = getInitName(node);

		if (initName == null)
		{
			// No 'initName' exists for non-constructor calls
			return;
		}

		APlainCallStmCG callStm = new APlainCallStmCG();
		callStm.setType(new AVoidTypeCG());
		callStm.setClassType(null);
		callStm.setName(initName);

		for (SExpCG a : node.getArgs())
		{
			callStm.getArgs().add(a.clone());
		}

		assist.replaceNodeWith(node, callStm);
	}
	
	public String getObjectInitializerCall(AExplicitOperationDefinition vdmOp)
	{
		if (objectInitCallNames.containsKey(vdmOp))
		{
			return objectInitCallNames.get(vdmOp);
		} else
		{
			String enclosingClassName = vdmOp.getAncestor(SClassDefinition.class).getName().getName();
			String initName = assist.getInfo().getTempVarNameGen().nextVarName(objectInitCallPrefix
					+ enclosingClassName + "_");
			objectInitCallNames.put(vdmOp, initName);

			return initName;
		}
	}

	private String getInitName(APlainCallStmCG node)
	{
		if (node.getSourceNode() != null && node.getSourceNode().getVdmNode() != null)
		{
			INode vdmNode = node.getSourceNode().getVdmNode();

			if (vdmNode instanceof ACallStm)
			{
				ACallStm c = (ACallStm) vdmNode;

				PDefinition rootDef = c.getRootdef();

				while (rootDef instanceof AInheritedDefinition)
				{
					rootDef = ((AInheritedDefinition) rootDef).getSuperdef();
				}

				if (rootDef instanceof AExplicitOperationDefinition)
				{
					AExplicitOperationDefinition op = (AExplicitOperationDefinition) rootDef;

					if (op.getIsConstructor())
					{
						return getObjectInitializerCall(op);
					}
				}
			}
		}

		return null;
	}

	private String getInitName(AMethodDeclCG node)
	{
		if (node.getSourceNode() != null && node.getSourceNode().getVdmNode() != null)
		{
			INode vdmNode = node.getSourceNode().getVdmNode();

			if (vdmNode instanceof AExplicitOperationDefinition)
			{
				AExplicitOperationDefinition op = (AExplicitOperationDefinition) vdmNode;

				return getObjectInitializerCall(op);
			}
		}

		return null;
	}
}
