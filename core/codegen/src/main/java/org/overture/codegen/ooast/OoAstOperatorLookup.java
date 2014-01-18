package org.overture.codegen.ooast;

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

public class OoAstOperatorLookup
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
	
	private HashMap<Class<? extends PExpCG>, OoAstOperatorInfo> lookup;
	
	public OoAstOperatorInfo find(Class<? extends PExpCG> key)
	{
		return lookup.get(key);
	}
	
	public OoAstOperatorLookup()
	{
		this.lookup = new HashMap<Class<? extends PExpCG>, OoAstOperatorInfo>();

		lookup.put(APlusNumericBinaryExpCG.class, new OoAstOperatorInfo(PLUS, "+"));
		lookup.put(ASubtractNumericBinaryExpCG.class, new OoAstOperatorInfo(SUB, "-"));
		lookup.put(ATimesNumericBinaryExpCG.class, new OoAstOperatorInfo(TIMES, "*"));
		lookup.put(ADivNumericBinaryExpCG.class, new OoAstOperatorInfo(DIVIDE, "/"));
		lookup.put(ARemNumericBinaryExpCG.class, new OoAstOperatorInfo(REM, "%")); 
		lookup.put(AModNumericBinaryExpCG.class, new OoAstOperatorInfo(MOD, "%")); //FIXME: Mod is special
		lookup.put(ADivideNumericBinaryExpCG.class, new OoAstOperatorInfo(DIV, "/"));//FIXME: Divider med / er speciel

		lookup.put(AEqualsBinaryExpCG.class, new OoAstOperatorInfo(EQUALS, "="));
		lookup.put(ANotEqualsBinaryExpCG.class, new OoAstOperatorInfo(NOT_EQUALS, "<>"));
		lookup.put(AAddrEqualsBinaryExpCG.class, new OoAstOperatorInfo(EQUALS, "=="));
		lookup.put(AAddrNotEqualsBinaryExpCG.class, new OoAstOperatorInfo(NOT_EQUALS, "!="));
		
		lookup.put(AGreaterEqualNumericBinaryExpCG.class, new OoAstOperatorInfo(GREATER_EQUAL, ">="));
		lookup.put(AGreaterNumericBinaryExpCG.class, new OoAstOperatorInfo(GREATER, ">"));
		lookup.put(ALessEqualNumericBinaryExpCG.class, new OoAstOperatorInfo(LESS_EQUAL, "<="));
		lookup.put(ALessNumericBinaryExpCG.class, new OoAstOperatorInfo(LESS, "<"));

		lookup.put(APowerNumericBinaryExpCG.class, new OoAstOperatorInfo(POWER, "**"));
		
		lookup.put(AOrBoolBinaryExpCG.class, new OoAstOperatorInfo(OR, "or"));
		lookup.put(AAndBoolBinaryExpCG.class, new OoAstOperatorInfo(AND, "and"));
		lookup.put(AXorBoolBinaryExpCG.class, new OoAstOperatorInfo(XOR, "*^"));
		lookup.put(ANotUnaryExpCG.class, new OoAstOperatorInfo(NOT, "not"));
		
		lookup.put(AMinusUnaryExpCG.class, new OoAstOperatorInfo(UNARY_MINUS, "-"));
		lookup.put(APlusUnaryExpCG.class, new OoAstOperatorInfo(UNARY_PLUS, "+"));
		
		lookup.put(ATernaryIfExpCG.class, new OoAstOperatorInfo(TERNARY_IF, "?:"));
	}
}
