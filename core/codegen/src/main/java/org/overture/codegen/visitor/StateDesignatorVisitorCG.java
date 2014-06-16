package org.overture.codegen.visitor;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.PExp;
import org.overture.ast.statements.AFieldStateDesignator;
import org.overture.ast.statements.AIdentifierStateDesignator;
import org.overture.ast.statements.AMapSeqStateDesignator;
import org.overture.ast.statements.PStateDesignator;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStateDesignatorCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.statements.AFieldStateDesignatorCG;
import org.overture.codegen.cgast.statements.AMapSeqStateDesignatorCG;
import org.overture.codegen.ir.IRInfo;

public class StateDesignatorVisitorCG extends AbstractVisitorCG<IRInfo, SStateDesignatorCG>
{
	@Override
	public SStateDesignatorCG caseAFieldStateDesignator(
			AFieldStateDesignator node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PStateDesignator stateDesignator = node.getObject();
		String fieldName = node.getField().getName();
		
		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SStateDesignatorCG stateDesignatorCg = stateDesignator.apply(question.getStateDesignatorVisitor(), question);
		
		AFieldStateDesignatorCG field = new AFieldStateDesignatorCG();
		field.setType(typeCg);
		field.setObject(stateDesignatorCg);
		field.setField(fieldName);
		
		return field;
	}

	@Override
	public SStateDesignatorCG caseAIdentifierStateDesignator(
			AIdentifierStateDesignator node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		String name = node.getName().getName();
		
		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		
		return question.getDesignatorAssistant().consMember(typeCg, name);
	}
	
	@Override
	public SStateDesignatorCG caseAMapSeqStateDesignator(
			AMapSeqStateDesignator node, IRInfo question)
			throws AnalysisException
	{
		PType type = node.getType();
		PStateDesignator mapSeq = node.getMapseq();
		PExp exp = node.getExp();

		STypeCG typeCg = type.apply(question.getTypeVisitor(), question);
		SStateDesignatorCG mapSeqCg = mapSeq.apply(question.getStateDesignatorVisitor(), question);
		SExpCG expCg = exp.apply(question.getExpVisitor(), question);
		
		AMapSeqStateDesignatorCG mapSeqStateDesignator = new AMapSeqStateDesignatorCG();
		mapSeqStateDesignator.setType(typeCg);
		mapSeqStateDesignator.setMapseq(mapSeqCg);
		mapSeqStateDesignator.setExp(expCg);
		
		return mapSeqStateDesignator;
	}
}
