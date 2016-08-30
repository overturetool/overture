package org.overture.codegen.trans;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.ast.statements.ACallStm;
import org.overture.codegen.ir.IRGeneratedTag;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.ASystemClassDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.statements.APlainCallStmIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class ConstructorTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantIR assist;

	// To look up object initializer call names
	private Map<AExplicitOperationDefinition, String> objectInitCallNames;

	// Object initialization call prefix
	private String objectInitCallPrefix;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public ConstructorTrans(TransAssistantIR assist,
			String objectInitCallPrefix)
	{
		this.assist = assist;
		this.objectInitCallPrefix = objectInitCallPrefix;
		this.objectInitCallNames = new HashMap<AExplicitOperationDefinition, String>();
	}

	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
	{
		if (node.parent() instanceof ASystemClassDeclIR)
		{
			return;
		}

		if (node.getIsConstructor())
		{
			String initName = getInitName(node);

			if (initName == null)
			{
				return;
			}

			AMethodDeclIR objInitializer = node.clone();
			objInitializer.setSourceNode(node.getSourceNode());
			objInitializer.setTag(new IRGeneratedTag(getClass().getName()));
			objInitializer.setName(initName);
			objInitializer.getMethodType().setResult(new AVoidTypeIR());
			objInitializer.setIsConstructor(false);
			objInitializer.setPreCond(null);
			objInitializer.setPostCond(null);

			ADefaultClassDeclIR classCg = node.getAncestor(ADefaultClassDeclIR.class);

			if (classCg == null)
			{
				log.error("Could not find enclosing class of constructor "
						+ node.getName());
				return;
			}

			classCg.getMethods().addFirst(objInitializer);

			// Apply transformation recursively
			objInitializer.apply(this);

			APlainCallStmIR initCall = new APlainCallStmIR();
			initCall.setType(objInitializer.getMethodType().getResult().clone());
			initCall.setClassType(null);
			initCall.setName(initName);

			for (AFormalParamLocalParamIR param : node.getFormalParams())
			{
				SPatternIR pattern = param.getPattern();

				if (pattern instanceof AIdentifierPatternIR)
				{
					AIdentifierPatternIR idPattern = (AIdentifierPatternIR) pattern;

					AIdentifierVarExpIR var = new AIdentifierVarExpIR();
					var.setIsLocal(true);
					var.setType(param.getType().clone());
					var.setName(idPattern.getName());
					var.setIsLambda(false);
					var.setSourceNode(pattern.getSourceNode());

					initCall.getArgs().add(var);
				} else
				{
					log.error("Expected all parameters to be identifier patterns by now. Got: "
							+ pattern);
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
	public void caseAPlainCallStmIR(APlainCallStmIR node)
			throws AnalysisException
	{
		String initName = getInitName(node);

		if (initName == null)
		{
			// No 'initName' exists for non-constructor calls
			return;
		}

		APlainCallStmIR callStm = new APlainCallStmIR();
		callStm.setType(new AVoidTypeIR());
		callStm.setClassType(null);
		callStm.setName(initName);

		for (SExpIR a : node.getArgs())
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

	private String getInitName(APlainCallStmIR node)
	{
		if (node.getSourceNode() != null
				&& node.getSourceNode().getVdmNode() != null)
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

	private String getInitName(AMethodDeclIR node)
	{
		if (node.getSourceNode() != null
				&& node.getSourceNode().getVdmNode() != null)
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
