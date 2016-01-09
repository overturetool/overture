/*
 * #%~
 * The Overture Abstract Syntax Tree
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ast.factory;

import java.util.List;

import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.AEqualsBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.AInSetBinaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.AMkTypeExp;
import org.overture.ast.expressions.ANotEqualBinaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.intf.lex.ILexToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.PType;

public class AstExpressionFactory
{

	public static AMkTypeExp newAMkTypeExp(ILexNameToken typeName, PType type, List<PExp> arglist)
	{
		AMkTypeExp mktype = new AMkTypeExp();
		mktype.setType(type);
		mktype.setTypeName(typeName);
		mktype.setArgs(arglist);
		return mktype;
	}

	public static AEqualsBinaryExp newAEqualsBinaryExp(PExp left, PExp right)
	{
		AEqualsBinaryExp result = new AEqualsBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.EQUALS);
		initExpressionBinary(result, left, op, right);
		result.setType(new ABooleanBasicType());
		return result;
	}

	public static AAndBooleanBinaryExp newAAndBooleanBinaryExp(PExp left,
			PExp right)
	{
		AAndBooleanBinaryExp result = new AAndBooleanBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.AND);
		initExpressionBinary(result, left, op, right);
		result.setType(new ABooleanBasicType());
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
		result.setType(new ABooleanBasicType());
		return result;
	}

	public static ANotEqualBinaryExp newANotEqualBinaryExp(PExp left, PExp right)
	{
		ANotEqualBinaryExp result = new ANotEqualBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.NE);
		initExpressionBinary(result, left, op, right);
		result.setType(new ABooleanBasicType());
		return result;
	}

	public static ALessNumericBinaryExp newALessNumericBinaryExp(PExp left,
			PExp right)
	{
		ALessNumericBinaryExp result = new ALessNumericBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.LT);
		initExpressionBinary(result, left, op, right);
		result.setType(new ABooleanBasicType());
		return result;
	}

	public static AInSetBinaryExp newAInSetBinaryExp(PExp left, PExp right)
	{
		AInSetBinaryExp result = new AInSetBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.INSET);
		initExpressionBinary(result, left, op, right);
		result.setType(new ABooleanBasicType());
		return result;
	}

	public static AOrBooleanBinaryExp newAOrBooleanBinaryExp(PExp left,
			PExp right)
	{
		AOrBooleanBinaryExp result = new AOrBooleanBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.OR);
		initExpressionBinary(result, left, op, right);
		result.setType(new ABooleanBasicType());
		return result;
	}

	public static AImpliesBooleanBinaryExp newAImpliesBooleanBinaryExp(
			PExp left, PExp right)
	{
		AImpliesBooleanBinaryExp result = new AImpliesBooleanBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.IMPLIES);
		initExpressionBinary(result, left, op, right);
		result.setType(new ABooleanBasicType());
		return result;
	}

	public static AGreaterNumericBinaryExp newAGreaterNumericBinaryExp(
			PExp left, PExp right)
	{
		AGreaterNumericBinaryExp result = new AGreaterNumericBinaryExp();
		ILexToken op = new LexToken(null, VDMToken.GT);
		initExpressionBinary(result, left, op, right);
		result.setType(new ABooleanBasicType());
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
