package org.overture.ast.factory;

import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;

public class AstExpressionFactory
{

	public static AEqualsBinaryExp newAEqualsBinaryExp(PExp left, PExp right)
	{
		AEqualsBinaryExp result = new AEqualsBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.EQUALS);
		initExpressionBinary(result, left, op, right);
		return result;
	}

	public static AAndBooleanBinaryExp newAAndBooleanBinaryExp(PExp left,
			PExp right)
	{
		AAndBooleanBinaryExp result = new AAndBooleanBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.AND);
		initExpressionBinary(result, left, op, right);
		return result;
	}

	public static APlusNumericBinaryExp newAPlusNumericBinaryExp(PExp left,
			PExp right)
	{
		APlusNumericBinaryExp result = new APlusNumericBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.PLUS);
		initExpressionBinary(result, left, op, right);
		return result;
	}

	public static ALessEqualNumericBinaryExp newALessEqualNumericBinaryExp(
			PExp left, PExp right)
	{
		ALessEqualNumericBinaryExp result = new ALessEqualNumericBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.LE);
		initExpressionBinary(result, left, op, right);
		return result;
	}

	public static ANotEqualBinaryExp newANotEqualBinaryExp(PExp left, PExp right)
	{
		ANotEqualBinaryExp result = new ANotEqualBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.NE);
		initExpressionBinary(result, left, op, right);
		return result;
	}

	public static ALessNumericBinaryExp newALessNumericBinaryExp(PExp left,
			PExp right)
	{
		ALessNumericBinaryExp result = new ALessNumericBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.LT);
		initExpressionBinary(result, left, op, right);
		return result;
	}

	public static AInSetBinaryExp newAInSetBinaryExp(PExp left, PExp right)
	{
		AInSetBinaryExp result = new AInSetBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.INSET);
		initExpressionBinary(result, left, op, right);
		return result;
	}

	public static AOrBooleanBinaryExp newAOrBooleanBinaryExp(PExp left,
			PExp right)
	{
		AOrBooleanBinaryExp result = new AOrBooleanBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.OR);
		initExpressionBinary(result, left, op, right);

		return result;
	}

	public static AImpliesBooleanBinaryExp newAImpliesBooleanBinaryExp(
			PExp left, PExp right)
	{
		AImpliesBooleanBinaryExp result = new AImpliesBooleanBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.IMPLIES);
		initExpressionBinary(result, left, op, right);

		return result;
	}

	public static AGreaterNumericBinaryExp newAGreaterNumericBinaryExp(
			PExp left, PExp right)
	{
		AGreaterNumericBinaryExp result = new AGreaterNumericBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.GT);
		initExpressionBinary(result, left, op, right);

		return result;
	}

	static void initExpressionBinary(SBinaryExp result, PExp left,
			ILexToken op, PExp right)
	{
		initExpression(result, op.getLocation());
		result.setLeft(left);
		result.setOp(op);
		result.setRight(right);
	}

	private static void initExpression(PExp result, ILexLocation location)
	{
		result.setLocation(location);
		if (location != null)
		{
			location.executable(true);
		}
	}

}
