package org.overture.codegen.visitor;

import java.util.LinkedList;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.ANewExp;
import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.statements.AApplyObjectDesignator;
import org.overture.ast.statements.AFieldObjectDesignator;
import org.overture.ast.statements.AIdentifierObjectDesignator;
import org.overture.ast.statements.ANewObjectDesignator;
import org.overture.ast.statements.ASelfObjectDesignator;
import org.overture.ast.statements.PObjectDesignator;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.expressions.SVarExpCG;
import org.overture.codegen.cgast.statements.AApplyObjectDesignatorCG;
import org.overture.codegen.cgast.statements.AFieldObjectDesignatorCG;
import org.overture.codegen.cgast.statements.AIdentifierObjectDesignatorCG;
import org.overture.codegen.cgast.statements.ANewObjectDesignatorCG;
import org.overture.codegen.cgast.statements.ASelfObjectDesignatorCG;
import org.overture.codegen.cgast.statements.PObjectDesignatorCG;
import org.overture.codegen.ooast.OoAstInfo;
import org.overture.codegen.utils.AnalysisExceptionCG;

public class ObjectDesignatorVisitorCG extends AbstractVisitorCG<OoAstInfo, PObjectDesignatorCG>
{
	@Override
	public PObjectDesignatorCG caseAApplyObjectDesignator(
			AApplyObjectDesignator node, OoAstInfo question)
			throws AnalysisException
	{
		PObjectDesignator obj = node.getObject();
		PObjectDesignatorCG objCg = obj.apply(question.getObjectDesignatorVisitor(), question);
		
		AApplyObjectDesignatorCG applyObjDesignator = new AApplyObjectDesignatorCG();
		applyObjDesignator.setObject(objCg);
		
		LinkedList<PExpCG> newExpArgs = applyObjDesignator.getArgs();
		for (PExp arg : node.getArgs())
		{
			PExpCG argCg = arg.apply(question.getExpVisitor(), question);
			newExpArgs.add(argCg);
		}

		return applyObjDesignator;
	}
	
	@Override
	public PObjectDesignatorCG caseAFieldObjectDesignator(
			AFieldObjectDesignator node, OoAstInfo question)
			throws AnalysisException
	{
		ILexNameToken field = node.getField();
		PObjectDesignator obj = node.getObject();
		
		String fieldCg = field.getName();
		PObjectDesignatorCG objCg = obj.apply(question.getObjectDesignatorVisitor(), question);
		
		AFieldObjectDesignatorCG fieldObjDesignator = new AFieldObjectDesignatorCG();
		fieldObjDesignator.setFieldName(fieldCg);
		fieldObjDesignator.setObject(objCg);
		
		return fieldObjDesignator;
	}
	
	@Override
	public PObjectDesignatorCG caseAIdentifierObjectDesignator(
			AIdentifierObjectDesignator node, OoAstInfo question)
			throws AnalysisException
	{
		AVariableExp exp = node.getExpression();

		PExpCG expCg = exp.apply(question.getExpVisitor(), question);

		AIdentifierObjectDesignatorCG idObjDesignator = new AIdentifierObjectDesignatorCG();
		
		if(!(expCg instanceof SVarExpCG))
		{
			question.addUnsupportedNode(node, "Expected variable expression for identifier object designator. Got: " + expCg);
			return null;
		}
		
		idObjDesignator.setExp((SVarExpCG) expCg);

		return idObjDesignator;
	}
	
	@Override
	public PObjectDesignatorCG caseANewObjectDesignator(
			ANewObjectDesignator node, OoAstInfo question)
			throws AnalysisException
	{
		ANewExp exp = node.getExpression();

		PExpCG expCg = exp.apply(question.getExpVisitor(), question);

		ANewObjectDesignatorCG newObjDesignator = new ANewObjectDesignatorCG();

		if (!(expCg instanceof ANewExpCG))
			throw new AnalysisExceptionCG("Expected expression of new object designator to be a 'new expression' but got: "
					+ expCg.getClass().getName(), node.getLocation());

		newObjDesignator.setExp((ANewExpCG) expCg);
		return newObjDesignator;
	}
	
	@Override
	public PObjectDesignatorCG caseASelfObjectDesignator(
			ASelfObjectDesignator node, OoAstInfo question)
			throws AnalysisException
	{
		return new ASelfObjectDesignatorCG();
	}
	
}
