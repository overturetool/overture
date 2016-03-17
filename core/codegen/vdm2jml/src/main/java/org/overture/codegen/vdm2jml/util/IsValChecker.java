package org.overture.codegen.vdm2jml.util;

import org.overture.codegen.ir.INode;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.AnswerAdaptor;
import org.overture.codegen.ir.expressions.AEnumMapExpIR;
import org.overture.codegen.ir.expressions.AEnumSeqExpIR;
import org.overture.codegen.ir.expressions.AEnumSetExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AMapletExpIR;
import org.overture.codegen.ir.expressions.AMinusUnaryExpIR;
import org.overture.codegen.ir.expressions.AMkBasicExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.expressions.ANullExpIR;
import org.overture.codegen.ir.expressions.APlusUnaryExpIR;
import org.overture.codegen.ir.expressions.SLiteralExpIR;
import org.overture.codegen.ir.expressions.SNumericBinaryExpIR;

public class IsValChecker extends AnswerAdaptor<Boolean>
{
	@Override
	public Boolean defaultINode(INode node) throws AnalysisException
	{
		// Return false for all other cases
		return false;
	}
	
	@Override
	public Boolean caseAIdentifierVarExpIR(AIdentifierVarExpIR node) throws AnalysisException {

		return true;
	}
	
	@Override
	public Boolean defaultSLiteralExpIR(SLiteralExpIR node) throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean caseAMkBasicExpIR(AMkBasicExpIR node) throws AnalysisException
	{
		// Token value
		return true;
	}
	
	@Override
	public Boolean caseANullExpIR(ANullExpIR node) throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean caseANewExpIR(ANewExpIR node) throws AnalysisException
	{
		return true;
	}
	
	@Override
	public Boolean caseAEnumMapExpIR(AEnumMapExpIR node) throws AnalysisException {

		for(AMapletExpIR m : node.getMembers())
		{
			if(!m.apply(this))
			{
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public Boolean caseAEnumSeqExpIR(AEnumSeqExpIR node) throws AnalysisException {

		for(SExpIR m : node.getMembers())
		{
			if(!m.apply(this))
			{
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public Boolean caseAEnumSetExpIR(AEnumSetExpIR node) throws AnalysisException {

		for(SExpIR m : node.getMembers())
		{
			if(!m.apply(this))
			{
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public Boolean caseAMapletExpIR(AMapletExpIR node) throws AnalysisException {

		return node.getLeft().apply(this) && node.getRight().apply(this);
	}
	
	@Override
	public Boolean defaultSNumericBinaryExpIR(SNumericBinaryExpIR node) throws AnalysisException
	{
		if(node.getLeft() == null || node.getRight() == null)
		{
			return false;
		}
		
		return node.getLeft().apply(this) && node.getRight().apply(this);
	}
	
	@Override
	public Boolean caseAMinusUnaryExpIR(AMinusUnaryExpIR node) throws AnalysisException
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
	public Boolean caseAPlusUnaryExpIR(APlusUnaryExpIR node) throws AnalysisException
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
