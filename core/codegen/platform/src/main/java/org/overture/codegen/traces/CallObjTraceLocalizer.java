package org.overture.codegen.traces;

import org.apache.commons.lang.BooleanUtils;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.AnswerAdaptor;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ASelfExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.types.AObjectTypeCG;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class CallObjTraceLocalizer extends AnswerAdaptor<SExpCG>
{
	private TransAssistantCG assist;
	private TraceNames tracePrefixes;
	private String traceEnclosingClass;

	public CallObjTraceLocalizer(TransAssistantCG transAssistant, TraceNames tracePrefixes, String traceEnclosingClass)
	{
		this.assist = transAssistant;
		this.tracePrefixes = tracePrefixes;
		this.traceEnclosingClass = traceEnclosingClass;
	}

	@Override
	public SExpCG caseAApplyExpCG(AApplyExpCG node) throws AnalysisException
	{
		return node.getRoot().apply(this);
	}

	@Override
	public SExpCG caseAFieldExpCG(AFieldExpCG node) throws AnalysisException
	{
		return node.getObject().apply(this);
	}

	@Override
	public SExpCG caseAIdentifierVarExpCG(AIdentifierVarExpCG node) throws AnalysisException
	{
		if (node instanceof SVarExpCG)
		{
			SVarExpCG varExp = (SVarExpCG) node;

			if (BooleanUtils.isFalse(varExp.getIsLocal()))
			{
				ACastUnaryExpCG objId = consObjId();

				AFieldExpCG fieldObj = new AFieldExpCG();
				fieldObj.setType(node.getType().clone());
				fieldObj.setMemberName(varExp.getName());
				fieldObj.setObject(objId);

				assist.replaceNodeWith(node, fieldObj);

				return objId;
			}

			return node;
		}

		Logger.getLog().printErrorln("Expected variable expression at this point in '" + this.getClass().getSimpleName()
				+ "'. Got: " + node);

		return null;
	}

	@Override
	public SExpCG caseASelfExpCG(ASelfExpCG node) throws AnalysisException
	{
		ACastUnaryExpCG objId = consObjId();
		assist.replaceNodeWith(node, objId);

		return objId;
	}

	@Override
	public SExpCG caseANewExpCG(ANewExpCG node) throws AnalysisException
	{
		return node;
	}

	private ACastUnaryExpCG consObjId()
	{
		String paramName = tracePrefixes.callStmMethodParamName();
		
		AIdentifierVarExpCG idVar = assist.getInfo().getExpAssistant().consIdVar(paramName, new AObjectTypeCG());

		ACastUnaryExpCG castVar = new ACastUnaryExpCG();

		castVar.setType(assist.consClassType(traceEnclosingClass));
		castVar.setExp(idVar);

		return castVar;
	}

	@Override
	public SExpCG createNewReturnValue(INode node) throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}

	@Override
	public SExpCG createNewReturnValue(Object node) throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}

}
