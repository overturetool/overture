package org.overture.codegen.vdm2jml.util;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.AnswerAdaptor;
import org.overture.codegen.cgast.expressions.AMinusUnaryExpCG;
import org.overture.codegen.cgast.expressions.AMkBasicExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.APlusUnaryExpCG;
import org.overture.codegen.cgast.expressions.SLiteralExpCG;
import org.overture.codegen.cgast.expressions.SNumericBinaryExpCG;

public class IsValChecker extends AnswerAdaptor<Boolean>
{
	@Override
	public Boolean defaultINode(INode node) throws AnalysisException
	{
		// Return false for all other cases
		return false;
	}
	
	@Override
	public Boolean defaultSLiteralExpCG(SLiteralExpCG node) throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean caseAMkBasicExpCG(AMkBasicExpCG node) throws AnalysisException
	{
		// Token value
		return true;
	}
	
	@Override
	public Boolean caseANullExpCG(ANullExpCG node) throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean caseANewExpCG(ANewExpCG node) throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean defaultSNumericBinaryExpCG(SNumericBinaryExpCG node) throws AnalysisException
	{
		if(node.getLeft() == null || node.getRight() == null)
		{
			return false;
		}
		
		return node.getLeft().apply(this) && node.getRight().apply(this);
	}
	
	@Override
	public Boolean caseAMinusUnaryExpCG(AMinusUnaryExpCG node) throws AnalysisException
	{
		if(node.getExp() == null)
		{
			return false;
		}
		else
		{
			return node.getExp().apply(this);
		}
	}
	
	@Override
	public Boolean caseAPlusUnaryExpCG(APlusUnaryExpCG node) throws AnalysisException
	{
		if(node.getExp() == null)
		{
			return false;
		}
		else
		{
			return node.getExp().apply(this);
		}
	}

	// Do not expect to hit the cases below:
	
	@Override
	public Boolean createNewReturnValue(INode node) throws AnalysisException
	{
		return false;
	}

	@Override
	public Boolean createNewReturnValue(Object node) throws AnalysisException
	{
		return false;
	}
	
}
