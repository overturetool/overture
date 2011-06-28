package com.lausdahl.ast.test2;

import generated.node.ATrueBoolean;

import org.overture.ast.analysis.AnalysisAdaptor;
import org.overture.ast.expressions.ABinopExp;
import org.overture.ast.expressions.ABooleanConstExp;
import org.overture.ast.expressions.AIntConstExp;

public class PrettyPrint extends AnalysisAdaptor
{
	public PrettyPrint()
	{
		System.out.println();
	}
	
	@Override
	public void caseABinopExp(ABinopExp node)
	{
		node.getLeft().apply(this);
		System.out.print(node.getBinop().toString().trim());
		node.getRight().apply(this);
	}


	@Override
	public void caseABooleanConstExp(ABooleanConstExp node)
	{
		if(node.getBoolean() instanceof ATrueBoolean)
		{
			System.out.print("true");
			return;
		}
		System.out.print("false");
	}

	@Override
	public void caseAIntConstExp(AIntConstExp node)
	{
		System.out.print(node.getNumbersLiteral().getText());
	}
}