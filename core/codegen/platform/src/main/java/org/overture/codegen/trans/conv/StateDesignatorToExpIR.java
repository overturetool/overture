package org.overture.codegen.trans.conv;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.AnswerAdaptor;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AMapSeqGetExpIR;
import org.overture.codegen.ir.statements.AFieldStateDesignatorIR;
import org.overture.codegen.ir.statements.AIdentifierStateDesignatorIR;
import org.overture.codegen.ir.statements.AMapSeqStateDesignatorIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

/**
 * Converts a state designator into an equivalent expression. Please note that this converter assumes map sequence state
 * designators to be "map readings" and not "map modifications". More explicitly, this means that the parent of a map
 * sequence state designator is assumed to be a state designator and not an assignment statement.
 * 
 * @author pvj
 */
public class StateDesignatorToExpIR extends AnswerAdaptor<SExpIR>
{
	private TransAssistantIR transAssistant;
	
	public StateDesignatorToExpIR(TransAssistantIR transAssistant)
	{
		this.transAssistant = transAssistant;
	}
	
	@Override
	public SExpIR caseAIdentifierStateDesignatorIR(
			AIdentifierStateDesignatorIR node) throws AnalysisException
	{
		return transAssistant.getInfo().getExpAssistant().idStateDesignatorToExp(node);
	}
	
	@Override
	public SExpIR caseAFieldStateDesignatorIR(AFieldStateDesignatorIR node)
			throws AnalysisException
	{
		SExpIR objExp = node.getObject().apply(this);
		
		AFieldExpIR fieldExp = new AFieldExpIR();
		fieldExp.setMemberName(node.getField());
		fieldExp.setObject(objExp);
		fieldExp.setType(node.getType().clone());
		fieldExp.setTag(node.getTag());
		fieldExp.setSourceNode(node.getSourceNode());
		
		return fieldExp;
	}
	
	@Override
	public SExpIR caseAMapSeqStateDesignatorIR(AMapSeqStateDesignatorIR node)
			throws AnalysisException
	{
		// Reading a map or a sequence on the left hand
		// side of an assignment, e.g. m(1).field := 5;
		
		SExpIR index = node.getExp();
		SExpIR col = node.getMapseq().apply(this);

		AMapSeqGetExpIR mapSeqGet = new AMapSeqGetExpIR();
		mapSeqGet.setType(node.getType().clone());
		mapSeqGet.setIndex(index.clone());
		mapSeqGet.setCol(col);
		mapSeqGet.setSourceNode(node.getSourceNode());
		mapSeqGet.setTag(node.getTag());

		return mapSeqGet;
	}
	
	@Override
	public SExpIR createNewReturnValue(INode node) throws AnalysisException
	{
		assert false : "This should never happen";
		return null;
	}

	@Override
	public SExpIR createNewReturnValue(Object node) throws AnalysisException
	{
		assert false : "This should never happen";
		return null;
	}
}
