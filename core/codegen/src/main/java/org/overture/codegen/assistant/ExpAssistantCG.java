package org.overture.codegen.assistant;

import org.apache.commons.lang.StringEscapeUtils;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.expressions.ARealLiteralExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.expressions.SUnaryExp;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.PType;
import org.overture.codegen.cgast.expressions.ABoolLiteralExpCG;
import org.overture.codegen.cgast.expressions.ACharLiteralExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.AIsolationUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.expressions.AStringLiteralExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;
import org.overture.codegen.cgast.expressions.SUnaryExpCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.visitor.OoAstInfo;

public class ExpAssistantCG
{
	public static PExpCG isolateExpression(PExpCG exp)
	{
		AIsolationUnaryExpCG isolationExp = new AIsolationUnaryExpCG();
		isolationExp.setExp(exp);
		isolationExp.setType(exp.getType());
		return isolationExp;
	}
	
	public PExpCG handleUnaryExp(SUnaryExp vdmExp, SUnaryExpCG codeGenExp, OoAstInfo question) throws AnalysisException
	{
		PExpCG expCg = vdmExp.getExp().apply(question.getExpVisitor(), question);
		PTypeCG typeCg = vdmExp.getType().apply(question.getTypeVisitor(), question);
		
		codeGenExp.setType(typeCg);
		codeGenExp.setExp(expCg);
		
		return codeGenExp;
	}
	
	public PExpCG handleBinaryExp(SBinaryExp vdmExp, SBinaryExpCG codeGenExp, OoAstInfo question) throws AnalysisException
	{	
		PTypeCG typeCg = vdmExp.getType().apply(question.getTypeVisitor(), question);
		codeGenExp.setType(typeCg);
		
		PExp vdmExpLeft = vdmExp.getLeft();
		PExp vdmExpRight = vdmExp.getRight();
		
		PExpCG leftExpCg = vdmExpLeft.apply(question.getExpVisitor(), question);
		PExpCG rightExpCg = vdmExpRight.apply(question.getExpVisitor(), question);
		
		codeGenExp.setLeft(leftExpCg);
		codeGenExp.setRight(rightExpCg);

		PTypeCG leftTypeCg = vdmExpLeft.getType().apply(question.getTypeVisitor(), question);
		codeGenExp.getLeft().setType(leftTypeCg);
		PTypeCG rightTypeCg = vdmExpRight.getType().apply(question.getTypeVisitor(), question);
		codeGenExp.getRight().setType(rightTypeCg);
		
		return codeGenExp;
	}
	
	public static boolean isIntegerType(PExp exp)
	{	
		PType type = exp.getType();

		//Expressions like 1.0 are considered real literal expressions
		//of type NatOneNumericBasicType
		
		return (type instanceof ANatOneNumericBasicType 
				|| type instanceof ANatNumericBasicType
				|| type instanceof AIntNumericBasicType) 
				&& !(exp instanceof ARealLiteralExp);
	}
	
	public static ABoolLiteralExpCG consBoolLiteral(boolean val)
	{
		ABoolLiteralExpCG boolLiteral = new ABoolLiteralExpCG();
		boolLiteral.setType(new ABoolBasicTypeCG());
		boolLiteral.setValue(val);
		
		return boolLiteral;
	}
	
	public static AIntLiteralExpCG consIntLiteral(long value)
	{
		AIntLiteralExpCG intLiteral = new AIntLiteralExpCG();
		intLiteral.setType(new AIntNumericBasicTypeCG());
		intLiteral.setValue(value);
		
		return intLiteral;
	}
	
	public static ARealLiteralExpCG consRealLiteral(double value)
	{
		ARealLiteralExpCG realLiteral = new ARealLiteralExpCG();
		realLiteral.setType(new ARealNumericBasicTypeCG());
		realLiteral.setValue(value);
		
		return realLiteral;
	}
	
	public static ACharLiteralExpCG consCharLiteral(char value)
	{
		ACharLiteralExpCG charLiteral = new ACharLiteralExpCG();
		charLiteral.setType(new ACharBasicTypeCG());
		charLiteral.setValue(value);
		
		return charLiteral;
	}
	
	public static AStringLiteralExpCG consStringLiteral(String value, boolean isNull)
	{
		AStringLiteralExpCG stringLiteral = new AStringLiteralExpCG();

		stringLiteral.setType(new AStringTypeCG());
		stringLiteral.setIsNull(isNull);
		stringLiteral.setValue(StringEscapeUtils.escapeJava(value));
		
		return stringLiteral;
	}
	
	public static AIntLiteralExpCG getDefaultIntValue()
	{
		return consIntLiteral(0L);
	}
	
	public static ARealLiteralExpCG getDefaultRealValue()
	{
		return consRealLiteral(0.0);
	}
	
	public static ABoolLiteralExpCG getDefaultBoolValue()
	{
		return consBoolLiteral(false);
	}
	
	public static ACharLiteralExpCG getDefaultCharlValue()
	{
		return consCharLiteral('0');
	}
	
	public static AStringLiteralExpCG getDefaultStringlValue()
	{
		return consStringLiteral("", true);
	}
	
	public static ANullExpCG getDefaultClassValue()
	{
		return new ANullExpCG();
	}
}
