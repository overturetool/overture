package org.overture.codegen.trans.uniontypes;

import java.util.LinkedList;

import org.overture.ast.node.INode;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AElemsUnaryExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AMapDomainUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.SNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.SUnaryExpCG;
import org.overture.codegen.cgast.statements.AElseIfStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.cgast.types.SMapTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;

public class UnionTypeTransformation extends DepthFirstAnalysisAdaptor
{
	private BaseTransformationAssistant baseAssistant;
	private IRInfo info;
	
	public UnionTypeTransformation(BaseTransformationAssistant baseAssistant, IRInfo info)
	{
		this.baseAssistant = baseAssistant;
		this.info = info;
	}
	
	private SExpCG wrap(SExpCG exp, STypeCG castedType)
	{
		if(exp.getType() instanceof AUnionTypeCG && !(exp instanceof ACastUnaryExpCG))
		{
			ACastUnaryExpCG casted = new ACastUnaryExpCG();
			casted.setType(castedType.clone());
			casted.setExp(exp.clone());
			
			return casted;
		}
		
		return exp;
	}
	
	private boolean handleUnaryExp(SUnaryExpCG exp) throws AnalysisException
	{
		STypeCG type = exp.getExp().getType();
		
		if(type instanceof AUnionTypeCG)
		{
			INode vdmNode = type.getSourceNode().getVdmNode();
			
			if(vdmNode instanceof PType)
			{
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void defaultInSNumericBinaryExpCG(SNumericBinaryExpCG node)
			throws AnalysisException
	{
		STypeCG expectedType = node.getType();
		
		SExpCG newLeft = wrap(node.getLeft(), expectedType);
		SExpCG newRight = wrap(node.getRight(), expectedType);

		baseAssistant.replaceNodeWithRecursively(node.getLeft(), newLeft, this);
		baseAssistant.replaceNodeWithRecursively(node.getRight(), newRight, this);
	}
	
	@Override
	public void caseANotUnaryExpCG(ANotUnaryExpCG node)
			throws AnalysisException
	{
		ABoolBasicTypeCG expectedType = new ABoolBasicTypeCG();
		SExpCG newExp = wrap(node.getExp(), expectedType);
		baseAssistant.replaceNodeWithRecursively(node.getExp(), newExp, this);
	}
	
	@Override
	public void caseAEqualsBinaryExpCG(AEqualsBinaryExpCG node)
			throws AnalysisException
	{
		STypeCG leftType = node.getLeft().getType();
		STypeCG rightType = node.getRight().getType();
		
		SExpCG unionTypedExp = null;
		SExpCG notUnionTypedExp = null;
		
		if(leftType instanceof AUnionTypeCG && !(rightType instanceof AUnionTypeCG))
		{
			unionTypedExp = node.getLeft();
			notUnionTypedExp = node.getRight();
		}
		else if(rightType instanceof AUnionTypeCG && !(leftType instanceof AUnionTypeCG))
		{
			unionTypedExp = node.getRight();
			notUnionTypedExp = node.getLeft();
		}
		else
		{
			return;
		}
		
		STypeCG expectedType = notUnionTypedExp.getType();
		SExpCG newUnionTypedExp = wrap(unionTypedExp, expectedType);
		baseAssistant.replaceNodeWithRecursively(unionTypedExp, newUnionTypedExp, this);
	}

	@Override
	public void inAIfStmCG(AIfStmCG node) throws AnalysisException
	{
		ABoolBasicTypeCG expectedType = new ABoolBasicTypeCG();
		
		SExpCG newIfExp = wrap(node.getIfExp(), expectedType);
		baseAssistant.replaceNodeWithRecursively(node.getIfExp(), newIfExp, this);
		
		LinkedList<AElseIfStmCG> elseIfs = node.getElseIf();
		
		for(AElseIfStmCG currentElseIf : elseIfs)
		{
			SExpCG newExp = wrap(currentElseIf.getElseIf(), expectedType);
			baseAssistant.replaceNodeWithRecursively(currentElseIf.getElseIf(), newExp, this);
		}
	}
	
	@Override
	public void inAVarLocalDeclCG(AVarLocalDeclCG node)
			throws AnalysisException
	{
		SExpCG newExp = wrap(node.getExp(), node.getType());
		baseAssistant.replaceNodeWithRecursively(node.getExp(), newExp, this);
	}
	
	@Override
	public void caseAElemsUnaryExpCG(AElemsUnaryExpCG node)
			throws AnalysisException
	{
		if(handleUnaryExp(node))
		{
			SExpCG exp = node.getExp();
			PType vdmType = (PType) exp.getType().getSourceNode().getVdmNode();
			SSeqType seqType = info.getTcFactory().createPTypeAssistant().getSeq(vdmType);
			
			try
			{
				STypeCG typeCg = seqType.apply(info.getTypeVisitor(), info);
				
				if(typeCg instanceof SSeqTypeCG)
				{
					SExpCG newExp = wrap(exp, typeCg);
					baseAssistant.replaceNodeWithRecursively(exp, newExp, this);
				}
				
			} catch (org.overture.ast.analysis.AnalysisException e)
			{
			}
		}
	}
	
	@Override
	public void caseAMapDomainUnaryExpCG(AMapDomainUnaryExpCG node)
			throws AnalysisException
	{
		if(handleUnaryExp(node))
		{
			SExpCG exp = node.getExp();
			PType vdmType = (PType) exp.getType().getSourceNode().getVdmNode();
			SMapType mapType = info.getTcFactory().createPTypeAssistant().getMap(vdmType);
			
			try
			{
				STypeCG typeCg = mapType.apply(info.getTypeVisitor(), info);
				
				if(typeCg instanceof SMapTypeCG)
				{
					SExpCG newExp = wrap(exp, typeCg);
					baseAssistant.replaceNodeWithRecursively(exp, newExp, this);
				}
				
			} catch (org.overture.ast.analysis.AnalysisException e)
			{
			}
		}
	}
}
