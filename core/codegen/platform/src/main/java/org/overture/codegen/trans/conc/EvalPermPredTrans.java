package org.overture.codegen.trans.conc;

import org.apache.log4j.Logger;
import org.overture.codegen.ir.IRGeneratedTag;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.AAssignmentStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.AMapSeqUpdateStmIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

/**
 * This transformation generates a "state change" call to the Sentinel class to make it re-evaluate permission
 * predicates. It assumes all state updates to come from the local assignment statement, the assignment statement or the
 * "map put statement".
 * 
 * @author pvj
 */
public class EvalPermPredTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantIR transAssistant;
	private ConcPrefixes concPrefixes;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public EvalPermPredTrans(TransAssistantIR transAssistant,
			ConcPrefixes concPrefixes)
	{
		this.transAssistant = transAssistant;
		this.concPrefixes = concPrefixes;
	}

	@Override
	public void caseAAssignmentStmIR(AAssignmentStmIR node)
			throws AnalysisException
	{
		if(transAssistant.getInfo().getDeclAssistant().isTest(node.getAncestor(ADefaultClassDeclIR.class)))
		{
			return;
		}
		
		handleStateUpdate(node);
	}

	@Override
	public void caseAAssignToExpStmIR(AAssignToExpStmIR node)
			throws AnalysisException
	{
		if(transAssistant.getInfo().getDeclAssistant().isTest(node.getAncestor(ADefaultClassDeclIR.class)))
		{
			return;
		}
		
		if (node.getTarget() instanceof SVarExpIR)
		{
			SVarExpIR var = (SVarExpIR) node.getTarget();
			if (var.getIsLocal())
			{
				return;
			}
		}

		handleStateUpdate(node);
	}

	@Override
	public void caseAMapSeqUpdateStmIR(AMapSeqUpdateStmIR node)
			throws AnalysisException
	{
		if(transAssistant.getInfo().getDeclAssistant().isTest(node.getAncestor(ADefaultClassDeclIR.class)))
		{
			return;
		}
		
		handleStateUpdate(node);
	}

	private void handleStateUpdate(SStmIR node)
	{
		if (!transAssistant.getInfo().getSettings().generateConc())
		{
			return;
		}

		AMethodDeclIR enclosingMethod = node.getAncestor(AMethodDeclIR.class);

		if (enclosingMethod != null)
		{
			Boolean isStatic = enclosingMethod.getStatic();

			if (isStatic != null && isStatic)
			{
				return;
			}

			if (enclosingMethod.getIsConstructor())
			{
				return;
			}

			if (isIRGenerated(enclosingMethod))
			{
				return;
			}
		} else
		{
			// Can in fact be okay since the IR construction of the thread definition skips the
			// explicit operation definition implicitly associated with the thread definition.
			//
			// Example:
			// thread
			// (x := 2;)
			//
		}

		STypeIR fieldType = getSentinelFieldType(node);

		AIdentifierVarExpIR sentinelVar = new AIdentifierVarExpIR();
		sentinelVar.setIsLocal(true);
		sentinelVar.setIsLambda(false);
		sentinelVar.setName(concPrefixes.sentinelInstanceName());
		sentinelVar.setType(fieldType);

		ACallObjectExpStmIR callSentinel = new ACallObjectExpStmIR();
		callSentinel.setObj(sentinelVar);
		callSentinel.setFieldName(concPrefixes.stateChangedMethodName());
		callSentinel.setType(new AVoidTypeIR());

		ABlockStmIR replacementBlock = new ABlockStmIR();

		transAssistant.replaceNodeWith(node, replacementBlock);

		replacementBlock.getStatements().add(node);
		replacementBlock.getStatements().add(callSentinel);
	}

	private STypeIR getSentinelFieldType(SStmIR node)
	{
		ADefaultClassDeclIR enclosingClass = node.getAncestor(ADefaultClassDeclIR.class);

		STypeIR fieldType = null;

		if (enclosingClass != null)
		{
			fieldType = transAssistant.getInfo().getTypeAssistant().getFieldType(enclosingClass, concPrefixes.sentinelInstanceName(), transAssistant.getInfo().getClasses());
		} else
		{
			log.error("Could not find enclosing class of assignment statement");
		}
		return fieldType;
	}

	private boolean isIRGenerated(AMethodDeclIR method)
	{
		return method.getTag() instanceof IRGeneratedTag;
	}
}
