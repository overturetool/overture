package org.overture.codegen.lookup;

import java.util.HashMap;

import org.overture.ast.expressions.ADivNumericBinaryExp;
import org.overture.ast.expressions.ADivideNumericBinaryExp;
import org.overture.ast.expressions.AGreaterEqualNumericBinaryExp;
import org.overture.ast.expressions.AGreaterNumericBinaryExp;
import org.overture.ast.expressions.ALessEqualNumericBinaryExp;
import org.overture.ast.expressions.ALessNumericBinaryExp;
import org.overture.ast.expressions.AModNumericBinaryExp;
import org.overture.ast.expressions.APlusNumericBinaryExp;
import org.overture.ast.expressions.ARemNumericBinaryExp;
import org.overture.ast.expressions.ASubtractNumericBinaryExp;
import org.overture.ast.expressions.ATimesNumericBinaryExp;
import org.overture.ast.expressions.SBinaryExp;
import org.overture.codegen.cgast.expressions.ADivNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ADivideNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AGreaterEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AGreaterNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ALessEqualNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ALessNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.AModNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.APlusNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ARemNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASubtractNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ATimesNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;

public class OperatorLookup
{
	//TODO: Get rid of static??
	private static OperatorLookup instance;
	
	
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
	
	public static OperatorLookup GetInstance()
	{
		if(instance == null)
			instance = new OperatorLookup();
		
		return instance;
	}
	
	private HashMap<Class<? extends SBinaryExp>,OperatorInfo> lookup;

	public OperatorInfo find(Class<? extends SBinaryExp> key)
	{
		return lookup.get(key);
	}
	
	private OperatorLookup()
	{
		lookup = new HashMap<Class<? extends SBinaryExp>, OperatorInfo>();

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
		
//		lookup.put(ADivNumericBinaryExpCG.class, new OperatorInfo(DIVIDE, "/"));
//		lookup.put(ADivideNumericBinaryExpCG.class, new OperatorInfo(DIV, "/"));//FIXME: Divider med / er speciel
//		lookup.put(AGreaterEqualNumericBinaryExpCG.class, new OperatorInfo(GREATER_EQUAL, ">="));
//		lookup.put(AGreaterNumericBinaryExpCG.class, new OperatorInfo(GREATER, ">"));
//		lookup.put(ALessEqualNumericBinaryExpCG.class, new OperatorInfo(LESS_EQUAL, "<="));
//		lookup.put(ALessNumericBinaryExpCG.class, new OperatorInfo(LESS, "<"));
//		lookup.put(AModNumericBinaryExpCG.class, new OperatorInfo(MOD, "%")); //FIXME: Mod is special
//		lookup.put(APlusNumericBinaryExpCG.class, new OperatorInfo(PLUS, "+"));
//		lookup.put(ASubtractNumericBinaryExpCG.class, new OperatorInfo(SUB, "-"));
//		lookup.put(ARemNumericBinaryExpCG.class, new OperatorInfo(REM, "%")); 
//		lookup.put(ATimesNumericBinaryExpCG.class, new OperatorInfo(TIMES, "*"));
		
//		lookup.put(ADivNumericBinaryExp.kindSNumericBinaryExp, new OperatorInfo(DIVIDE, "/"));
//		lookup.put(ADivideNumericBinaryExp.kindSNumericBinaryExp, new OperatorInfo(DIV, "/"));//FIXME: Divider med / er speciel
//		lookup.put(AGreaterEqualNumericBinaryExp.kindSNumericBinaryExp, new OperatorInfo(GREATER_EQUAL, ">="));
//		lookup.put(AGreaterNumericBinaryExp.kindSNumericBinaryExp, new OperatorInfo(GREATER, ">"));
//		lookup.put(ALessEqualNumericBinaryExp.kindSNumericBinaryExp, new OperatorInfo(LESS_EQUAL, "<="));
//		lookup.put(ALessNumericBinaryExp.kindSNumericBinaryExp, new OperatorInfo(LESS, "<"));
//		lookup.put(AModNumericBinaryExp.kindSNumericBinaryExp, new OperatorInfo(MOD, "%")); //FIXME: Mod is special
//		lookup.put(APlusNumericBinaryExp.kindSNumericBinaryExp, new OperatorInfo(PLUS, "+"));
//		lookup.put(ASubtractNumericBinaryExp.kindSNumericBinaryExp, new OperatorInfo(SUB, "-"));
//		lookup.put(ARemNumericBinaryExp.kindSNumericBinaryExp, new OperatorInfo(REM, "%")); 
//		lookup.put(ATimesNumericBinaryExp.kindSNumericBinaryExp, new OperatorInfo(TIMES, "*"));
	}
	
}
