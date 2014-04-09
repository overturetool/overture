package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AFieldStateDesignator;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.AMapSeqStateDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.statements.AFieldStateDesignatorCG;
import org.overture.codegen.cgast.statements.AMapSeqStateDesignatorCG;
import org.overture.codegen.cgast.statements.PStateDesignatorCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.ooast.OoAstInfo;

public class StateDesignatorVisitorCG extends AbstractVisitorCG<OoAstInfo, PStateDesignatorCG>
{
	@Override
	public PStateDesignatorCG caseAFieldStateDesignator(
			AFieldStateDesignator node, OoAstInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PStateDesignator stateDesignator = node.getObject();
		String fieldName = node.getField().getName();
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PStateDesignatorCG stateDesignatorCg = stateDesignator.apply(question.getStateDesignatorVisitor(), question);
		
		AFieldStateDesignatorCG field = new AFieldStateDesignatorCG();
		field.setType(typeCg);
		field.setObject(stateDesignatorCg);
		field.setField(fieldName);
		
		return field;
	}

	@Override
	public PStateDesignatorCG caseAIdentifierStateDesignator(
			AIdentifierStateDesignator node, OoAstInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		String name = node.getName().getName();
		
		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		
		return question.getDesignatorAssistant().consMember(typeCg, name);
	}
	
	@Override
	public PStateDesignatorCG caseAMapSeqStateDesignator(
			AMapSeqStateDesignator node, OoAstInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PStateDesignator mapSeq = node.getMapseq();
		PExp exp = node.getExp();

		PTypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		PStateDesignatorCG mapSeqCg = mapSeq.apply(question.getStateDesignatorVisitor(), question);
		PExpCG expCg = exp.apply(question.getExpVisitor(), question);
		
		AMapSeqStateDesignatorCG mapSeqStateDesignator = new AMapSeqStateDesignatorCG();
		mapSeqStateDesignator.setType(typeCg);
		mapSeqStateDesignator.setMapseq(mapSeqCg);
		mapSeqStateDesignator.setExp(expCg);
		
		return mapSeqStateDesignator;
	}
}
