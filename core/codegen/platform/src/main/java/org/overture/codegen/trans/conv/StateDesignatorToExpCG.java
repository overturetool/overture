package org.overture.codegen.trans.conv;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.AnswerAdaptor;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AMapSeqGetExpCG;
import org.overture.codegen.cgast.statements.AFieldStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.statements.AMapSeqStateDesignatorCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

/**
 * Converts a state designator into an equivalent expression. Please note that this converter assumes map sequence state
 * designators to be "map readings" and not "map modifications". More explicitly, this means that the parent of a map
 * sequence state designator is assumed to be a state designator and not an assignment statement.
 * 
 * @author pvj
 */
public class StateDesignatorToExpCG extends AnswerAdaptor<SExpCG>
{
	private TransAssistantCG transAssistant;
	
	public StateDesignatorToExpCG(TransAssistantCG transAssistant)
	{
		this.transAssistant = transAssistant;
	}
	
	@Override
	public SExpCG caseAIdentifierStateDesignatorCG(
			AIdentifierStateDesignatorCG node) throws AnalysisException
	{
		return transAssistant.getInfo().getExpAssistant().idStateDesignatorToExp(node);
	}
	
	@Override
	public SExpCG caseAFieldStateDesignatorCG(AFieldStateDesignatorCG node)
			throws AnalysisException
	{
		SExpCG objExp = node.getObject().apply(this);
		
		AFieldExpCG fieldExp = new AFieldExpCG();
		fieldExp.setMemberName(node.getField());
		fieldExp.setObject(objExp);
		fieldExp.setType(node.getType().clone());
		fieldExp.setTag(node.getTag());
		fieldExp.setSourceNode(node.getSourceNode());
		
		return fieldExp;
	}
	
	@Override
	public SExpCG caseAMapSeqStateDesignatorCG(AMapSeqStateDesignatorCG node)
			throws AnalysisException
	{
		// Reading a map or a sequence on the left hand
		// side of an assignment, e.g. m(1).field := 5;
		
		SExpCG index = node.getExp();
		SExpCG col = node.getMapseq().apply(this);

		AMapSeqGetExpCG mapSeqGet = new AMapSeqGetExpCG();
		mapSeqGet.setType(node.getType().clone());
		mapSeqGet.setIndex(index.clone());
		mapSeqGet.setCol(col);
		mapSeqGet.setSourceNode(node.getSourceNode());
		mapSeqGet.setTag(node.getTag());

		return mapSeqGet;
	}
	
	@Override
	public SExpCG createNewReturnValue(INode node) throws AnalysisException
	{
		assert false : "This should never happen";
		return null;
	}

	@Override
	public SExpCG createNewReturnValue(Object node) throws AnalysisException
	{
		assert false : "This should never happen";
		return null;
	}
}
