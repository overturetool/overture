package org.overture.codegen.traces;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.AnswerAdaptor;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.AApplyObjectDesignatorCG;
import org.overture.codegen.cgast.statements.AFieldObjectDesignatorCG;
import org.overture.codegen.cgast.statements.AIdentifierObjectDesignatorCG;
import org.overture.codegen.cgast.statements.ANewObjectDesignatorCG;
import org.overture.codegen.cgast.statements.ASelfObjectDesignatorCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class EnsureLocalObjDesignatorAnalysis extends AnswerAdaptor<AIdentifierObjectDesignatorCG>
{
	private TransAssistantCG transAssistant;
	private TraceNames tracePrefixes;
	private String traceEnclosingClass;
	
	public EnsureLocalObjDesignatorAnalysis(TransAssistantCG transAssistant, TraceNames tracePrefixes, String traceEnclosingClass)
	{
		this.transAssistant = transAssistant;
		this.traceEnclosingClass = traceEnclosingClass;
		this.tracePrefixes = tracePrefixes;
	}
	
	@Override
	public AIdentifierObjectDesignatorCG caseAApplyObjectDesignatorCG(AApplyObjectDesignatorCG node)
			throws AnalysisException
	{
		return node.getObject().apply(this);
	}

	@Override
	public AIdentifierObjectDesignatorCG caseAFieldObjectDesignatorCG(AFieldObjectDesignatorCG node)
			throws AnalysisException
	{
		return node.getObject().apply(this);
	}

	@Override
	public AIdentifierObjectDesignatorCG caseAIdentifierObjectDesignatorCG(
			AIdentifierObjectDesignatorCG node) throws AnalysisException
	{
		SExpCG exp = node.getExp();

		if (exp instanceof SVarExpCG)
		{
			SVarExpCG varExp = (SVarExpCG) exp;
			Boolean isLocal = varExp.getIsLocal();

			if (isLocal != null)
			{
				if (!isLocal)
				{
					AIdentifierObjectDesignatorCG objId = consObjId();

					AFieldObjectDesignatorCG fieldObj = new AFieldObjectDesignatorCG();
					fieldObj.setFieldModule(traceEnclosingClass);
					fieldObj.setFieldName(varExp.getName());
					fieldObj.setObject(objId);

					transAssistant.replaceNodeWith(node, fieldObj);
					
					
					return objId;
				}
				return null;
			}
		}

		Logger.getLog().printErrorln("Could not determine if variable declaration "
				+ "was local or not (in IsLocalObjDesignatorAnalysis)");
		
		return null;
	}

	@Override
	public AIdentifierObjectDesignatorCG caseANewObjectDesignatorCG(ANewObjectDesignatorCG node)
			throws AnalysisException
	{
		// Nothing to be done
		return null;
	}

	@Override
	public AIdentifierObjectDesignatorCG caseASelfObjectDesignatorCG(ASelfObjectDesignatorCG node)
			throws AnalysisException
	{
		AIdentifierObjectDesignatorCG objId = consObjId();
		transAssistant.replaceNodeWith(node, objId);
		
		return objId;
	}
	
	private AIdentifierObjectDesignatorCG consObjId()
	{
		String name = tracePrefixes.callStmMethodParamName();
		AClassTypeCG classType = transAssistant.consClassType(traceEnclosingClass);
		AIdentifierVarExpCG idVar = transAssistant.getInfo().getExpAssistant().consIdVar(name, classType);
		
		AIdentifierObjectDesignatorCG objId = new AIdentifierObjectDesignatorCG();
		objId.setExp(idVar);
		
		return objId;
	}

	@Override
	public AIdentifierObjectDesignatorCG createNewReturnValue(INode node)
			throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}

	@Override
	public AIdentifierObjectDesignatorCG createNewReturnValue(Object node)
			throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}
}
