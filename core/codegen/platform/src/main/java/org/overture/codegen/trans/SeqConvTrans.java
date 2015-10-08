package org.overture.codegen.trans;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AFieldNumberExpCG;
import org.overture.codegen.cgast.expressions.ANotEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.ASeqToStringUnaryExpCG;
import org.overture.codegen.cgast.expressions.AStringToSeqUnaryExpCG;
import org.overture.codegen.cgast.expressions.SBinaryExpCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.AStringTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.trans.assistants.TransAssistantCG;

public class SeqConvTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantCG transformationAssistant;

	public SeqConvTrans(
			TransAssistantCG transformationAssistant)
	{
		this.transformationAssistant = transformationAssistant;
	}

	@Override
	public void inAFieldDeclCG(AFieldDeclCG node) throws AnalysisException
	{
		STypeCG nodeType = node.getType();
		SExpCG initial = node.getInitial();

		handleVarExp(nodeType, initial);

		if (initial == null)
		{
			return;
		}

		handleExp(initial, nodeType);
	}
	
	@Override
	public void inAFieldNumberExpCG(AFieldNumberExpCG node)
			throws AnalysisException
	{
		node.getTuple().apply(this);
		
		if(node.getType() instanceof AStringTypeCG)
		{
			correctExpToString(node);
		}
		else if(node.getType() instanceof SSeqTypeCG)
		{
			correctExpToSeq(node, node.getType());
		}
	}

	@Override
	public void inAVarDeclCG(AVarDeclCG node)
			throws AnalysisException
	{
		STypeCG nodeType = node.getType();
		SExpCG exp = node.getExp();

		handleVarExp(nodeType, exp);

		if (exp == null)
		{
			return;
		}

		handleExp(exp, nodeType);
	}
	
	@Override
	public void caseAAssignToExpStmCG(AAssignToExpStmCG node) throws AnalysisException
	{
		if(node.getExp() != null)
		{
			node.getExp().apply(this);
			handleExp(node.getExp(), node.getTarget().getType());
		}
	}

	private void handleExp(SExpCG exp, STypeCG nodeType)
	{
		if (exp.getType() instanceof AStringTypeCG
				&& nodeType instanceof SSeqTypeCG)
		{
			correctExpToSeq(exp, nodeType);
		} else if (exp.getType() instanceof SSeqTypeCG
				&& nodeType instanceof AStringTypeCG)
		{
			correctExpToString(exp);
		}
	}

	private void handleVarExp(STypeCG nodeType, SExpCG exp)
			throws AnalysisException
	{
		if (!(nodeType instanceof SSeqTypeCG))
		{
			return;
		}

		if (exp != null)
		{
			exp.apply(this);
		}
	}

	@Override
	public void defaultInSBinaryExpCG(SBinaryExpCG node)
			throws AnalysisException
	{
		SExpCG left = node.getLeft();
		SExpCG right = node.getRight();

		left.apply(this);
		right.apply(this);

		if (left.getType() instanceof AStringTypeCG
				&& right.getType() instanceof AStringTypeCG)
		{
			node.setType(new AStringTypeCG());
			return;
		}

		if (node.getType() instanceof SSeqTypeCG || node instanceof AEqualsBinaryExpCG || node instanceof ANotEqualsBinaryExpCG)
		{
			if (left.getType() instanceof AStringTypeCG
					&& right.getType() instanceof SSeqTypeCG)
			{
				correctExpToString(right);
			} else if (right.getType() instanceof AStringTypeCG
					&& left.getType() instanceof SSeqTypeCG)
			{
				correctExpToString(left);
			} else
			{
				return;
			}

			node.setType(new AStringTypeCG());
		}
	}

	@Override
	public void inAReturnStmCG(AReturnStmCG node) throws AnalysisException
	{
		SExpCG exp = node.getExp();

		if (exp != null)
		{
			exp.apply(this);
		} else
		{
			return;
		}

		AMethodDeclCG method = node.getAncestor(AMethodDeclCG.class);
		AMethodTypeCG methodType = method.getMethodType();

		if (methodType.getResult() instanceof AStringTypeCG)
		{
			if (!(exp.getType() instanceof SSeqTypeCG))
			{
				return;
			}

			correctExpToString(exp);
			;
		} else if (methodType.getResult() instanceof SSeqTypeCG)
		{
			if (!(exp.getType() instanceof AStringTypeCG))
			{
				return;
			}

			correctExpToSeq(exp, exp.getType());
		}
	}

	private void correctExpToSeq(SExpCG toCorrect, STypeCG type)
	{
		AStringToSeqUnaryExpCG conversion = new AStringToSeqUnaryExpCG();

		transformationAssistant.replaceNodeWith(toCorrect, conversion);

		conversion.setType(type.clone());
		conversion.setExp(toCorrect);
	}

	private void correctExpToString(SExpCG toCorrect)
	{
		ASeqToStringUnaryExpCG conversion = new ASeqToStringUnaryExpCG();

		transformationAssistant.replaceNodeWith(toCorrect, conversion);

		conversion.setType(new AStringTypeCG());
		conversion.setExp(toCorrect);
	}
}
