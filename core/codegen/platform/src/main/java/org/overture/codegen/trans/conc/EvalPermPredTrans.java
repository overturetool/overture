package org.overture.codegen.trans.conc;

import org.overture.codegen.ir.SStmCG;
import org.overture.codegen.ir.STypeCG;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclCG;
import org.overture.codegen.ir.declarations.AMethodDeclCG;
import org.overture.codegen.ir.expressions.AIdentifierVarExpCG;
import org.overture.codegen.ir.expressions.SVarExpCG;
import org.overture.codegen.ir.statements.AAssignToExpStmCG;
import org.overture.codegen.ir.statements.AAssignmentStmCG;
import org.overture.codegen.ir.statements.ABlockStmCG;
import org.overture.codegen.ir.statements.ACallObjectExpStmCG;
import org.overture.codegen.ir.statements.AMapSeqUpdateStmCG;
import org.overture.codegen.ir.types.AVoidTypeCG;
import org.overture.codegen.ir.IRGeneratedTag;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;

/**
 * This transformation generates a "state change" call to the Sentinel class to make it re-evaluate permission
 * predicates. It assumes all state updates to come from the local assignment statement, the assignment statement or the
 * "map put statement".
 * 
 * @author pvj
 */
public class EvalPermPredTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantCG transAssistant;
	private ConcPrefixes concPrefixes;

	public EvalPermPredTrans(TransAssistantCG transAssistant, ConcPrefixes concPrefixes)
	{
		this.transAssistant = transAssistant;
		this.concPrefixes = concPrefixes;
	}

	@Override
	public void caseAAssignmentStmCG(AAssignmentStmCG node) throws AnalysisException
	{
		handleStateUpdate(node);
	}

	@Override
	public void caseAAssignToExpStmCG(AAssignToExpStmCG node) throws AnalysisException
	{
		if (node.getTarget() instanceof SVarExpCG)
		{
			SVarExpCG var = (SVarExpCG) node.getTarget();
			if (var.getIsLocal())
			{
				return;
			}
		}

		handleStateUpdate(node);
	}

	@Override
	public void caseAMapSeqUpdateStmCG(AMapSeqUpdateStmCG node) throws AnalysisException
	{
		handleStateUpdate(node);
	}

	private void handleStateUpdate(SStmCG node)
	{
		if (!transAssistant.getInfo().getSettings().generateConc())
		{
			return;
		}

		AMethodDeclCG enclosingMethod = node.getAncestor(AMethodDeclCG.class);

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

		STypeCG fieldType = getSentinelFieldType(node);

		AIdentifierVarExpCG sentinelVar = new AIdentifierVarExpCG();
		sentinelVar.setIsLocal(true);
		sentinelVar.setIsLambda(false);
		sentinelVar.setName(concPrefixes.sentinelInstanceName());
		sentinelVar.setType(fieldType);

		ACallObjectExpStmCG callSentinel = new ACallObjectExpStmCG();
		callSentinel.setObj(sentinelVar);
		callSentinel.setFieldName(concPrefixes.stateChangedMethodName());
		callSentinel.setType(new AVoidTypeCG());

		ABlockStmCG replacementBlock = new ABlockStmCG();

		transAssistant.replaceNodeWith(node, replacementBlock);

		replacementBlock.getStatements().add(node);
		replacementBlock.getStatements().add(callSentinel);
	}

	private STypeCG getSentinelFieldType(SStmCG node)
	{
		ADefaultClassDeclCG enclosingClass = node.getAncestor(ADefaultClassDeclCG.class);

		STypeCG fieldType = null;

		if (enclosingClass != null)
		{
			fieldType = transAssistant.getInfo().getTypeAssistant().getFieldType(enclosingClass, concPrefixes.sentinelInstanceName(), transAssistant.getInfo().getClasses());
		} else
		{
			Logger.getLog().printErrorln("Could not find enclosing class of assignment statement in InstanceVarPPEvalTransformation");
		}
		return fieldType;
	}

	private boolean isIRGenerated(AMethodDeclCG method)
	{
		return method.getTag() instanceof IRGeneratedTag;
	}
}
