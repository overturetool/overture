package org.overture.codegen.lookup;

import java.util.HashMap;

import org.overture.ast.expressions.AAndBooleanBinaryExp;
import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.AEquivalentBooleanBinaryExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.AImpliesBooleanBinaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.ANotUnaryExp;
import org.overture.ast.expressions.AOrBooleanBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.AStarStarBinaryExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.PExp;

public class OperatorLookup
{
	//Arithmetic
	private static final int PLUS = 1;
	private static final int SUB = 1;
	
	private static final int TIMES = 2;
	private static final int DIVIDE = 2;
	private static final int REM = 2;
	private static final int MOD = 2;
	private static final int DIV = 2;
	
	//Relation
	private static final int GREATER_EQUAL = 1;
	private static final int GREATER = 1;
	private static final int LESS_EQUAL = 1;
	private static final int LESS = 1;
	
	//TODO: This is not the way to do it! Fix operator precedence!
	private static final int POWER = 7;

	private static final int IMPLIES = 8;
	private static final int EQUIVALENCE = 9;
	private static final int OR = 10;
	private static final int AND = 11;
	private static final int NOT = 12;
	
	private HashMap<Class<? extends PExp>,OperatorInfo> lookup;

	public OperatorInfo find(Class<? extends PExp> key)
	{
		return lookup.get(key);
	}
	
	public OperatorLookup()
	{
		lookup = new HashMap<Class<? extends PExp>, OperatorInfo>();

		lookup.put(ADivNumericBinaryExp.class, new OperatorInfo(DIVIDE, "/"));
		lookup.put(ADivideNumericBinaryExp.class, new OperatorInfo(DIV, "/"));//FIXME: Divider med / er speciel
		lookup.put(AGreaterEqualNumericBinaryExp.class, new OperatorInfo(GREATER_EQUAL, ">="));
		lookup.put(AGreaterNumericBinaryExp.class, new OperatorInfo(GREATER, ">"));
		lookup.put(ALessEqualNumericBinaryExp.class, new OperatorInfo(LESS_EQUAL, "<="));
		lookup.put(ALessNumericBinaryExp.class, new OperatorInfo(LESS, "<"));
		lookup.put(AModNumericBinaryExp.class, new OperatorInfo(MOD, "%")); //FIXME: Mod is special
		lookup.put(APlusNumericBinaryExp.class, new OperatorInfo(PLUS, "+"));
		lookup.put(ASubtractNumericBinaryExp.class, new OperatorInfo(SUB, "-"));
		lookup.put(ARemNumericBinaryExp.class, new OperatorInfo(REM, "%")); 
		lookup.put(ATimesNumericBinaryExp.class, new OperatorInfo(TIMES, "*"));
		lookup.put(AStarStarBinaryExp.class, new OperatorInfo(POWER, "**"));
		lookup.put(AImpliesBooleanBinaryExp.class, new OperatorInfo(IMPLIES, "=>"));
		lookup.put(AEquivalentBooleanBinaryExp.class, new OperatorInfo(EQUIVALENCE, "<=>"));
		lookup.put(AOrBooleanBinaryExp.class, new OperatorInfo(OR, "or"));
		lookup.put(AAndBooleanBinaryExp.class, new OperatorInfo(AND, "and"));
		lookup.put(ANotUnaryExp.class, new OperatorInfo(NOT, "not"));
		
		//All VDM operators must be listed otherwise checks for operator precedence can fail
		//since this is currently based on nodes in the VDM AST
	}
	
}
