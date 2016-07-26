package org.overture.codegen.traces;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.AnswerAdaptor;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.ACastUnaryExpIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.expressions.ASelfExpIR;
import org.overture.codegen.ir.expressions.SVarExpIR;
import org.overture.codegen.ir.types.AObjectTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class CallObjTraceLocalizer extends AnswerAdaptor<SExpIR>
{
	private TransAssistantIR assist;
	private TraceNames tracePrefixes;
	private String traceEnclosingClass;
	
	private Logger log = Logger.getLogger(this.getClass().getName());

	public CallObjTraceLocalizer(TransAssistantIR transAssistant, TraceNames tracePrefixes, String traceEnclosingClass)
	{
		this.assist = transAssistant;
		this.tracePrefixes = tracePrefixes;
		this.traceEnclosingClass = traceEnclosingClass;
	}

	@Override
	public SExpIR caseAApplyExpIR(AApplyExpIR node) throws AnalysisException
	{
		return node.getRoot().apply(this);
	}

	@Override
	public SExpIR caseAFieldExpIR(AFieldExpIR node) throws AnalysisException
	{
		return node.getObject().apply(this);
	}

	@Override
	public SExpIR caseAIdentifierVarExpIR(AIdentifierVarExpIR node) throws AnalysisException
	{
		if (node instanceof SVarExpIR)
		{
			SVarExpIR varExp = (SVarExpIR) node;

			if (BooleanUtils.isFalse(varExp.getIsLocal()))
			{
				ACastUnaryExpIR objId = consObjId();

				AFieldExpIR fieldObj = new AFieldExpIR();
				fieldObj.setType(node.getType().clone());
				fieldObj.setMemberName(varExp.getName());
				fieldObj.setObject(objId);

				assist.replaceNodeWith(node, fieldObj);

				return objId;
			}

			return node;
		}

		log.error("Expected variable expression at this point. Got: " + node);

		return null;
	}

	@Override
	public SExpIR caseASelfExpIR(ASelfExpIR node) throws AnalysisException
	{
		ACastUnaryExpIR objId = consObjId();
		assist.replaceNodeWith(node, objId);

		return objId;
	}

	@Override
	public SExpIR caseANewExpIR(ANewExpIR node) throws AnalysisException
	{
		return node;
	}

	private ACastUnaryExpIR consObjId()
	{
		String paramName = tracePrefixes.callStmMethodParamName();
		
		AIdentifierVarExpIR idVar = assist.getInfo().getExpAssistant().consIdVar(paramName, new AObjectTypeIR());

		ACastUnaryExpIR castVar = new ACastUnaryExpIR();

		castVar.setType(assist.consClassType(traceEnclosingClass));
		castVar.setExp(idVar);

		return castVar;
	}

	@Override
	public SExpIR createNewReturnValue(INode node) throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}

	@Override
	public SExpIR createNewReturnValue(Object node) throws AnalysisException
	{
		assert false : "Should not happen";
		return null;
	}

}
