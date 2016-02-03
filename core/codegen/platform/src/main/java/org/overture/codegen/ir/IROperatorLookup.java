/*
 * #%~
 * VDM Code Generator
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
package org.overture.codegen.ir;

import java.util.HashMap;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.expressions.AAddrEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.AAddrNotEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.AAndBoolBinaryExpIR;
import org.overture.codegen.ir.expressions.ACastUnaryExpIR;
import org.overture.codegen.ir.expressions.AIntDivNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ADivideNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.AGreaterEqualNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.AGreaterNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ALessEqualNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ALessNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.AMinusUnaryExpIR;
import org.overture.codegen.ir.expressions.AModNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ANotEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.ANotUnaryExpIR;
import org.overture.codegen.ir.expressions.AOrBoolBinaryExpIR;
import org.overture.codegen.ir.expressions.APlusNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.APlusUnaryExpIR;
import org.overture.codegen.ir.expressions.APowerNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ARemNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ASubtractNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ATernaryIfExpIR;
import org.overture.codegen.ir.expressions.ATimesNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.AXorBoolBinaryExpIR;

public class IROperatorLookup
{
	// TODO: Operators must be added as they come. Are there more to be added?

	// In the VDM Languge Reference Manual the "ternary if" fits the "Constructor"
	// class of operators. (see p. 203)
	private static final int TERNARY_IF = -1;

	// Arithmetic
	private static final int PLUS = 1;
	private static final int SUB = 1;
	private static final int TIMES = 2;
	private static final int DIVIDE = 2;
	private static final int REM = 2;
	private static final int MOD = 2;
	private static final int DIV = 2;

	// Relation
	private static final int EQUALS = 1;
	private static final int NOT_EQUALS = 1;
	private static final int GREATER_EQUAL = 1;
	private static final int GREATER = 1;
	private static final int LESS_EQUAL = 1;
	private static final int LESS = 1;

	// TODO: This is not the way to do it! Fix operator precedence!
	private static final int POWER = 7;

	private static final int CAST = 9;

	private static final int OR = 10;
	private static final int AND = 11;
	private static final int XOR = 12;
	private static final int NOT = 13;

	// Unary
	private static final int UNARY_PLUS = 13;
	private static final int UNARY_MINUS = 13;

	private HashMap<Class<? extends SExpIR>, IROperatorInfo> lookup;

	public IROperatorInfo find(Class<? extends SExpIR> key)
	{
		return lookup.get(key);
	}

	public IROperatorLookup()
	{
		this.lookup = new HashMap<Class<? extends SExpIR>, IROperatorInfo>();

		lookup.put(APlusNumericBinaryExpIR.class, new IROperatorInfo(PLUS, "+"));
		lookup.put(ASubtractNumericBinaryExpIR.class, new IROperatorInfo(SUB, "-"));
		lookup.put(ATimesNumericBinaryExpIR.class, new IROperatorInfo(TIMES, "*"));
		lookup.put(AIntDivNumericBinaryExpIR.class, new IROperatorInfo(DIV, "/"));
		lookup.put(ARemNumericBinaryExpIR.class, new IROperatorInfo(REM, "%"));
		lookup.put(AModNumericBinaryExpIR.class, new IROperatorInfo(MOD, "%"));
		lookup.put(ADivideNumericBinaryExpIR.class, new IROperatorInfo(DIVIDE, "/"));

		lookup.put(AEqualsBinaryExpIR.class, new IROperatorInfo(EQUALS, "="));
		lookup.put(ANotEqualsBinaryExpIR.class, new IROperatorInfo(NOT_EQUALS, "<>"));
		lookup.put(AAddrEqualsBinaryExpIR.class, new IROperatorInfo(EQUALS, "=="));
		lookup.put(AAddrNotEqualsBinaryExpIR.class, new IROperatorInfo(NOT_EQUALS, "!="));

		lookup.put(AGreaterEqualNumericBinaryExpIR.class, new IROperatorInfo(GREATER_EQUAL, ">="));
		lookup.put(AGreaterNumericBinaryExpIR.class, new IROperatorInfo(GREATER, ">"));
		lookup.put(ALessEqualNumericBinaryExpIR.class, new IROperatorInfo(LESS_EQUAL, "<="));
		lookup.put(ALessNumericBinaryExpIR.class, new IROperatorInfo(LESS, "<"));

		lookup.put(APowerNumericBinaryExpIR.class, new IROperatorInfo(POWER, "**"));

		lookup.put(ACastUnaryExpIR.class, new IROperatorInfo(CAST, "()"));

		lookup.put(AOrBoolBinaryExpIR.class, new IROperatorInfo(OR, "or"));
		lookup.put(AAndBoolBinaryExpIR.class, new IROperatorInfo(AND, "and"));
		lookup.put(AXorBoolBinaryExpIR.class, new IROperatorInfo(XOR, "*^"));
		lookup.put(ANotUnaryExpIR.class, new IROperatorInfo(NOT, "not"));

		lookup.put(AMinusUnaryExpIR.class, new IROperatorInfo(UNARY_MINUS, "-"));
		lookup.put(APlusUnaryExpIR.class, new IROperatorInfo(UNARY_PLUS, "+"));

		lookup.put(ATernaryIfExpIR.class, new IROperatorInfo(TERNARY_IF, "?:"));
	}
}
