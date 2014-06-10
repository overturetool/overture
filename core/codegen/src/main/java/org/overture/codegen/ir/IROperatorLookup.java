package org.overture.codegen.ir;

import java.util.HashMap;

import org.overture.codegen.cgast.expressions.AAddrEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AAddrNotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.ADivNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ADivideNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AGreaterEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AGreaterNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ALessEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ALessNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AMinusUnaryExpCG;
import org.overture.codegen.cgast.expressions.AModNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ANotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.AOrBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusUnaryExpCG;
import org.overture.codegen.cgast.expressions.APowerNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ARemNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASubtractNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ATernaryIfExpCG;
import org.overture.codegen.cgast.expressions.ATimesNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AXorBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;

public class IROperatorLookup
{
	//TODO: Operators must be added as they come. Are there more to be added?

	//In the VDM Languge Reference Manual the "ternary if" fits the "Constructor"
	//class of operators. (see p. 203)
	private static final int TERNARY_IF = -1;
	
	//Arithmetic
	private static final int PLUS = 1;
	private static final int SUB = 1;
	private static final int TIMES = 2;
	private static final int DIVIDE = 2;
	private static final int REM = 2;
	private static final int MOD = 2;
	private static final int DIV = 2;
	
	//Relation
	private static final int EQUALS = 1;
	private static final int NOT_EQUALS = 1;
	private static final int GREATER_EQUAL = 1;
	private static final int GREATER = 1;
	private static final int LESS_EQUAL = 1;
	private static final int LESS = 1;
	
	//TODO: This is not the way to do it! Fix operator precedence!
	private static final int POWER = 7;

	private static final int OR = 10;
	private static final int AND = 11;
	private static final int XOR = 12;
	private static final int NOT = 13;

	//Unary
	private static final int UNARY_PLUS = 13;
	private static final int UNARY_MINUS = 13;
	
	private HashMap<Class<? extends PExpCG>, IROperatorInfo> lookup;
	
	public IROperatorInfo find(Class<? extends PExpCG> key)
	{
		return lookup.get(key);
	}
	
	public IROperatorLookup()
	{
		this.lookup = new HashMap<Class<? extends PExpCG>, IROperatorInfo>();

		lookup.put(APlusNumericBinaryExpCG.class, new IROperatorInfo(PLUS, "+"));
		lookup.put(ASubtractNumericBinaryExpCG.class, new IROperatorInfo(SUB, "-"));
		lookup.put(ATimesNumericBinaryExpCG.class, new IROperatorInfo(TIMES, "*"));
		lookup.put(ADivNumericBinaryExpCG.class, new IROperatorInfo(DIVIDE, "/"));
		lookup.put(ARemNumericBinaryExpCG.class, new IROperatorInfo(REM, "%")); 
		lookup.put(AModNumericBinaryExpCG.class, new IROperatorInfo(MOD, "%")); //FIXME: Mod is special
		lookup.put(ADivideNumericBinaryExpCG.class, new IROperatorInfo(DIV, "/"));//FIXME: Divider med / er speciel

		lookup.put(AEqualsBinaryExpCG.class, new IROperatorInfo(EQUALS, "="));
		lookup.put(ANotEqualsBinaryExpCG.class, new IROperatorInfo(NOT_EQUALS, "<>"));
		lookup.put(AAddrEqualsBinaryExpCG.class, new IROperatorInfo(EQUALS, "=="));
		lookup.put(AAddrNotEqualsBinaryExpCG.class, new IROperatorInfo(NOT_EQUALS, "!="));
		
		lookup.put(AGreaterEqualNumericBinaryExpCG.class, new IROperatorInfo(GREATER_EQUAL, ">="));
		lookup.put(AGreaterNumericBinaryExpCG.class, new IROperatorInfo(GREATER, ">"));
		lookup.put(ALessEqualNumericBinaryExpCG.class, new IROperatorInfo(LESS_EQUAL, "<="));
		lookup.put(ALessNumericBinaryExpCG.class, new IROperatorInfo(LESS, "<"));

		lookup.put(APowerNumericBinaryExpCG.class, new IROperatorInfo(POWER, "**"));
		
		lookup.put(AOrBoolBinaryExpCG.class, new IROperatorInfo(OR, "or"));
		lookup.put(AAndBoolBinaryExpCG.class, new IROperatorInfo(AND, "and"));
		lookup.put(AXorBoolBinaryExpCG.class, new IROperatorInfo(XOR, "*^"));
		lookup.put(ANotUnaryExpCG.class, new IROperatorInfo(NOT, "not"));
		
		lookup.put(AMinusUnaryExpCG.class, new IROperatorInfo(UNARY_MINUS, "-"));
		lookup.put(APlusUnaryExpCG.class, new IROperatorInfo(UNARY_PLUS, "+"));
		
		lookup.put(ATernaryIfExpCG.class, new IROperatorInfo(TERNARY_IF, "?:"));
	}
}
