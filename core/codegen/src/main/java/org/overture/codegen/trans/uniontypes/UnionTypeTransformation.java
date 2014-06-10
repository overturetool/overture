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
	
	private SExpCG correctTypes(SExpCG exp, STypeCG castedType) throws AnalysisException
	{
		if(exp.getType() instanceof AUnionTypeCG && !(exp instanceof ACastUnaryExpCG))
		{
			ACastUnaryExpCG casted = new ACastUnaryExpCG();
			casted.setType(castedType.clone());
			casted.setExp(exp.clone());
			
			baseAssistant.replaceNodeWithRecursively(exp, casted, this);
			
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
		
		correctTypes(node.getLeft(), expectedType);
		correctTypes(node.getRight(), expectedType);
	}
	
	@Override
	public void caseANotUnaryExpCG(ANotUnaryExpCG node)
			throws AnalysisException
	{
		correctTypes(node.getExp(), new ABoolBasicTypeCG());
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
		correctTypes(unionTypedExp, expectedType);
	}

	@Override
	public void inAIfStmCG(AIfStmCG node) throws AnalysisException
	{
		ABoolBasicTypeCG expectedType = new ABoolBasicTypeCG();
		
		correctTypes(node.getIfExp(), expectedType);
		
		LinkedList<AElseIfStmCG> elseIfs = node.getElseIf();
		
		for(AElseIfStmCG currentElseIf : elseIfs)
		{
			correctTypes(currentElseIf.getElseIf(), expectedType);
		}
	}
	
	@Override
	public void inAVarLocalDeclCG(AVarLocalDeclCG node)
			throws AnalysisException
	{
		correctTypes(node.getExp(), node.getType());
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
					correctTypes(exp, typeCg);
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
					correctTypes(exp, typeCg);
				}
				
			} catch (org.overture.ast.analysis.AnalysisException e)
			{
			}
		}
	}
}
