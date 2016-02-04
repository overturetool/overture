package org.overture.codegen.trans;

import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.AFieldNumberExpIR;
import org.overture.codegen.ir.expressions.ANotEqualsBinaryExpIR;
import org.overture.codegen.ir.expressions.ASeqToStringUnaryExpIR;
import org.overture.codegen.ir.expressions.AStringToSeqUnaryExpIR;
import org.overture.codegen.ir.expressions.SBinaryExpIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.AForAllStmIR;
import org.overture.codegen.ir.statements.AReturnStmIR;
import org.overture.codegen.ir.types.ACharBasicTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.ir.types.AStringTypeIR;
import org.overture.codegen.ir.types.SSeqTypeIR;
import org.overture.codegen.trans.assistants.TransAssistantIR;

public class SeqConvTrans extends DepthFirstAnalysisAdaptor
{
	private TransAssistantIR transformationAssistant;

	public SeqConvTrans(
			TransAssistantIR transformationAssistant)
	{
		this.transformationAssistant = transformationAssistant;
	}

	@Override
	public void caseAForAllStmIR(AForAllStmIR node) throws AnalysisException
	{
		if(node.getExp().getType() instanceof AStringTypeIR)
		{
			ASeqSeqTypeIR seqType = new ASeqSeqTypeIR();
			seqType.setEmpty(false);
			seqType.setSeq1(false);
			seqType.setOptional(false);
			seqType.setSeqOf(new ACharBasicTypeIR());
			
			correctExpToSeq(node.getExp(), seqType);
		}

		node.getBody().apply(this);
	}
	
	@Override
	public void inAFieldDeclIR(AFieldDeclIR node) throws AnalysisException
	{
		STypeIR nodeType = node.getType();
		SExpIR initial = node.getInitial();

		handleVarExp(nodeType, initial);

		if (initial == null)
		{
			return;
		}

		handleExp(initial, nodeType);
	}
	
	@Override
	public void inAFieldNumberExpIR(AFieldNumberExpIR node)
			throws AnalysisException
	{
		node.getTuple().apply(this);
		
		if(node.getType() instanceof AStringTypeIR)
		{
			correctExpToString(node);
		}
		else if(node.getType() instanceof SSeqTypeIR)
		{
			correctExpToSeq(node, node.getType());
		}
	}

	@Override
	public void inAVarDeclIR(AVarDeclIR node)
			throws AnalysisException
	{
		STypeIR nodeType = node.getType();
		SExpIR exp = node.getExp();

		handleVarExp(nodeType, exp);

		if (exp == null)
		{
			return;
		}

		handleExp(exp, nodeType);
	}
	
	@Override
	public void caseAAssignToExpStmIR(AAssignToExpStmIR node) throws AnalysisException
	{
		if(node.getExp() != null)
		{
			node.getExp().apply(this);
			handleExp(node.getExp(), node.getTarget().getType());
		}
	}

	private void handleExp(SExpIR exp, STypeIR nodeType)
	{
		if (exp.getType() instanceof AStringTypeIR
				&& nodeType instanceof SSeqTypeIR)
		{
			correctExpToSeq(exp, nodeType);
		} else if (exp.getType() instanceof SSeqTypeIR
				&& nodeType instanceof AStringTypeIR)
		{
			correctExpToString(exp);
		}
	}

	private void handleVarExp(STypeIR nodeType, SExpIR exp)
			throws AnalysisException
	{
		if (!(nodeType instanceof SSeqTypeIR))
		{
			return;
		}

		if (exp != null)
		{
			exp.apply(this);
		}
	}

	@Override
	public void defaultInSBinaryExpIR(SBinaryExpIR node)
			throws AnalysisException
	{
		SExpIR left = node.getLeft();
		SExpIR right = node.getRight();

		left.apply(this);
		right.apply(this);

		if (left.getType() instanceof AStringTypeIR
				&& right.getType() instanceof AStringTypeIR)
		{
			node.setType(new AStringTypeIR());
			return;
		}

		if (node.getType() instanceof SSeqTypeIR || node instanceof AEqualsBinaryExpIR || node instanceof ANotEqualsBinaryExpIR)
		{
			if (left.getType() instanceof AStringTypeIR
					&& right.getType() instanceof SSeqTypeIR)
			{
				correctExpToString(right);
			} else if (right.getType() instanceof AStringTypeIR
					&& left.getType() instanceof SSeqTypeIR)
			{
				correctExpToString(left);
			} else
			{
				return;
			}

			node.setType(new AStringTypeIR());
		}
	}

	@Override
	public void inAReturnStmIR(AReturnStmIR node) throws AnalysisException
	{
		SExpIR exp = node.getExp();

		if (exp != null)
		{
			exp.apply(this);
		} else
		{
			return;
		}

		AMethodDeclIR method = node.getAncestor(AMethodDeclIR.class);
		AMethodTypeIR methodType = method.getMethodType();

		if (methodType.getResult() instanceof AStringTypeIR)
		{
			if (!(exp.getType() instanceof SSeqTypeIR))
			{
				return;
			}

			correctExpToString(exp);
			;
		} else if (methodType.getResult() instanceof SSeqTypeIR)
		{
			if (!(exp.getType() instanceof AStringTypeIR))
			{
				return;
			}

			correctExpToSeq(exp, exp.getType());
		}
	}

	private void correctExpToSeq(SExpIR toCorrect, STypeIR type)
	{
		AStringToSeqUnaryExpIR conversion = new AStringToSeqUnaryExpIR();

		transformationAssistant.replaceNodeWith(toCorrect, conversion);

		conversion.setType(type.clone());
		conversion.setExp(toCorrect);
	}

	private void correctExpToString(SExpIR toCorrect)
	{
		ASeqToStringUnaryExpIR conversion = new ASeqToStringUnaryExpIR();

		transformationAssistant.replaceNodeWith(toCorrect, conversion);

		conversion.setType(new AStringTypeIR());
		conversion.setExp(toCorrect);
	}
}
