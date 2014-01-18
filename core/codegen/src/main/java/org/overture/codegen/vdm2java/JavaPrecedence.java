package org.overture.codegen.vdm2java;

import org.overture.codegen.cgast.expressions.ADivideNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASubtractNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.ooast.OoAstOperatorInfo;
import org.overture.codegen.ooast.OoAstOperatorLookup;

public class JavaPrecedence
{
	public OoAstOperatorLookup opLookup;
	
	public JavaPrecedence()
	{
		this.opLookup = new OoAstOperatorLookup();
	}
	
	public boolean mustIsolate(PExpCG parentExp, PExpCG exp, boolean leftChild)
	{
		OoAstOperatorInfo parentOpInfo = opLookup.find(parentExp.getClass());

		if (parentOpInfo == null)
			return false;

		OoAstOperatorInfo expOpInfo = opLookup.find(exp.getClass());

		if (expOpInfo == null)
			return false;

		// Case 1: Protect against cases like "1 / (2*3*4)"
		// Don't care about left children, i.e. "(2*3*4)/1 = 2*3*4/1"

		// Similar for subtract: "1 - (1+2+3)" and "1+2+3-3"

		// We don't need to consider 'mod' and 'rem' operators since these are constructed
		// using other operators and isolated if needed using the isolation expression
		boolean case1 = !leftChild
				&& (parentExp instanceof ADivideNumericBinaryExpCG || parentExp instanceof ASubtractNumericBinaryExpCG)
				&& parentOpInfo.getPrecedence() >= expOpInfo.getPrecedence();

		if(case1)
			return true;
				
		// Case 2: Protect against case like 1 / (1+2+3)
		boolean case2 = parentOpInfo.getPrecedence() > expOpInfo.getPrecedence();
		
		return case2;
	}
}
