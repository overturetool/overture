package org.overture.codegen.trans;

import org.apache.log4j.Logger;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.node.INode;
import org.overture.codegen.ir.SDeclIR;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AStringLiteralExpIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class PostCheckTrans extends DepthFirstAnalysisAdaptor
{
	private IPostCheckCreator postCheckCreator;
	private TransAssistantIR transAssistant;
	private String funcResultNamePrefix;
	private Object conditionalCallTag;
	
	private Logger log = Logger.getLogger(this.getClass().getName());

	public PostCheckTrans(IPostCheckCreator postCheckCreator,
			TransAssistantIR transAssistant, String funcResultNamePrefix,
			Object conditionalCallTag)
	{
		this.postCheckCreator = postCheckCreator;
		this.transAssistant = transAssistant;
		this.funcResultNamePrefix = funcResultNamePrefix;
		this.conditionalCallTag = conditionalCallTag;
	}

	@Override
	public void caseAMethodDeclIR(AMethodDeclIR node) throws AnalysisException
	{
		if (!transAssistant.getInfo().getSettings().generatePostCondChecks())
		{
			return;
		}

		SDeclIR postCond = node.getPostCond();

		if (postCond == null)
		{
			return;
		}

		if (!(postCond instanceof AMethodDeclIR))
		{
			log.error("Expected post condition to be a method declaration at this point. Got: "
					+ postCond);
			return;
		}

		SourceNode sourceNode = postCond.getSourceNode();

		if (sourceNode == null)
		{
			log.error("Could not find source node for method declaration");
			return;
		}

		INode vdmNode = sourceNode.getVdmNode();

		if (vdmNode == null)
		{
			log.error("Could not find VDM source node for method declaration");
			return;
		}

		if (!(vdmNode instanceof AExplicitFunctionDefinition))
		{
			// Generation of post conditions is not supported for operations
			return;
		}

		node.getBody().apply(this);
	}

	@Override
	public void caseAReturnStmIR(AReturnStmIR node) throws AnalysisException
	{
		SExpIR result = node.getExp();

		if (result == null)
		{
			log.error("Expected a value to be returned");
			return;
		}

		AMethodDeclIR method = node.getAncestor(AMethodDeclIR.class);

		if (method == null)
		{
			log.error("Could not find enclosing method for a return statement");
			return;
		}
		
		if(method.getStatic() == null || !method.getStatic())
		{
			// Generation of a post condition is only supported for static operations
			// where no 'self' and '~self' are being passed
			return;
		}

		SDeclIR postCond = method.getPostCond();
		
		if (!(postCond instanceof AMethodDeclIR))
		{
			log.error("Expected post condition to be a method declaration at this point. Got: "
					+ postCond);
			return;
		}
		
		AApplyExpIR postCondCall = transAssistant.consConditionalCall(method, (AMethodDeclIR) method.getPostCond());
		postCondCall.setTag(conditionalCallTag);

		String funcResultVarName = transAssistant.getInfo().getTempVarNameGen().nextVarName(funcResultNamePrefix);
		AVarDeclIR resultDecl = transAssistant.consDecl(funcResultVarName, method.getMethodType().getResult().clone(), node.getExp().clone());
		AIdentifierVarExpIR resultVar = transAssistant.getInfo().getExpAssistant().consIdVar(funcResultVarName, resultDecl.getType().clone());

		postCondCall.getArgs().add(resultVar.clone());
		AStringLiteralExpIR methodName = transAssistant.getInfo().getExpAssistant().consStringLiteral(method.getName(), false);
		AApplyExpIR postCheckCall = postCheckCreator.consPostCheckCall(method, postCondCall, resultVar, methodName);

		ABlockStmIR replacementBlock = new ABlockStmIR();
		replacementBlock.getLocalDefs().add(resultDecl);

		transAssistant.replaceNodeWith(node.getExp(), postCheckCall);
		transAssistant.replaceNodeWith(node, replacementBlock);

		replacementBlock.getStatements().add(node);
	}
}
