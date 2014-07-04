package org.overture.codegen.trans.uniontypes;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SSeqType;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AElemsUnaryExpCG;
import org.overture.codegen.cgast.expressions.AEqualsBinaryExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AInstanceofExpCG;
import org.overture.codegen.cgast.expressions.AMapDomainUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.SNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.SUnaryExpCG;
import org.overture.codegen.cgast.expressions.SVarExpBase;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AElseIfStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.ALocalAssignmentStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.cgast.types.SMapTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.trans.assistants.BaseTransformationAssistant;
import org.overture.typechecker.TypeComparator;

public class UnionTypeTransformation extends DepthFirstAnalysisAdaptor
{
	private BaseTransformationAssistant baseAssistant;
	private IRInfo info;
	private List<AClassDeclCG> classes;

	private String objExpPrefix;
	private String applyExpResulPrefix;
	
	private ITempVarGen nameGen;
	
	public UnionTypeTransformation(BaseTransformationAssistant baseAssistant, IRInfo info, List<AClassDeclCG> classes, String applyExpResultPrefix, String objExpPrefix, ITempVarGen nameGen)
	{
		this.baseAssistant = baseAssistant;
		this.info = info;
		this.classes = classes;
		this.nameGen = nameGen;
		
		this.applyExpResulPrefix = applyExpResultPrefix;
		this.objExpPrefix = objExpPrefix;
	}
	
	private interface TypeFinder<T extends STypeCG>
	{
		public T findType(PType type) throws org.overture.ast.analysis.AnalysisException;
	}
	
	public <T extends STypeCG> T getMapType(SExpCG exp, TypeFinder<T> typeFinder)
	{
		if (exp == null || exp.getType() == null)
			return null;

		SourceNode sourceNode = exp.getType().getSourceNode();

		if (sourceNode == null)
			return null;

		org.overture.ast.node.INode vdmTypeNode = sourceNode.getVdmNode();

		if (vdmTypeNode instanceof PType)
		{
			try
			{
				PType vdmType = (PType) vdmTypeNode;
				
				return typeFinder.findType(vdmType);

			} catch (org.overture.ast.analysis.AnalysisException e)
			{
			}
		}

		return null;
	}
	
	private SExpCG correctTypes(SExpCG exp, STypeCG castedType) throws AnalysisException
	{
		if(exp.getType() instanceof AUnionTypeCG && !(exp instanceof ACastUnaryExpCG))
		{
			ACastUnaryExpCG casted = new ACastUnaryExpCG();
			casted.setType(castedType.clone());
			casted.setExp(exp.clone());
			
			baseAssistant.replaceNodeWith(exp, casted);
			
			return casted;
		}
		
		return exp;
	}
	
	private boolean handleUnaryExp(SUnaryExpCG exp) throws AnalysisException
	{
		STypeCG type = exp.getExp().getType();
		
		if(type instanceof AUnionTypeCG)
		{
			org.overture.ast.node.INode vdmNode = type.getSourceNode().getVdmNode();
			
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
	public void caseAFieldExpCG(AFieldExpCG node) throws AnalysisException
	{
		node.getObject().apply(this);
		
		INode parent = node.parent();
		
		if (parent instanceof AApplyExpCG)
		{
			//TODO: Deflatten structure
			
			STypeCG objectType = node.getObject().getType();
			
			if (!(objectType instanceof AUnionTypeCG))
			{
				return;
			}

			SStmCG enclosingStatement = baseAssistant.getEnclosingStm(node, "field expression");

			String applyResultName = nameGen.nextVarName(applyExpResulPrefix);
			AVarLocalDeclCG resultDecl = new AVarLocalDeclCG();
			resultDecl.setSourceNode(node.getSourceNode());
			resultDecl.setExp(new ANullExpCG());
			resultDecl.setType(((AApplyExpCG) parent).getType().clone());
			AIdentifierPatternCG id = new AIdentifierPatternCG();
			id.setName(applyResultName);
			resultDecl.setPattern(id);

			AIdentifierVarExpCG resultVar = new AIdentifierVarExpCG();
			resultVar.setSourceNode(node.getSourceNode());
			resultVar.setIsLambda(false);
			resultVar.setOriginal(applyResultName);
			resultVar.setType(resultDecl.getType().clone());
			
			ABlockStmCG replacementBlock = new ABlockStmCG();
			SExpCG obj = null;
			if (!(node.getObject() instanceof SVarExpBase))
			{
				String objName = nameGen.nextVarName(objExpPrefix);
				AVarLocalDeclCG objectDecl = new AVarLocalDeclCG();
				objectDecl.setExp(node.getObject().clone());
				objectDecl.setType(node.getObject().getType().clone());
				AIdentifierPatternCG objectVarId = new AIdentifierPatternCG();
				objectVarId.setName(objName);
				objectDecl.setPattern(objectVarId);

				replacementBlock.getLocalDefs().add(objectDecl);

				AIdentifierVarExpCG objectVar = new AIdentifierVarExpCG();
				objectVar.setIsLambda(false);
				objectVar.setOriginal(objName);
				objectVar.setType(objectDecl.getType().clone());
				obj = objectVar;
			} else
			{
				obj = node.getObject().clone();
			}

			LinkedList<STypeCG> possibleTypes = ((AUnionTypeCG) objectType).getTypes();

			AIfStmCG ifChecks = new AIfStmCG();

			for (int i = 0; i < possibleTypes.size(); i++)
			{
				AApplyExpCG apply = (AApplyExpCG) parent.clone();
				AFieldExpCG fieldExp = (AFieldExpCG) apply.getRoot();

				STypeCG currentType = possibleTypes.get(i);

				ACastUnaryExpCG castedFieldExp = new ACastUnaryExpCG();
				castedFieldExp.setType(currentType.clone());
				castedFieldExp.setExp(obj.clone());

				fieldExp.setObject(castedFieldExp);

				ALocalAssignmentStmCG assignment = new ALocalAssignmentStmCG();
				assignment.setTarget(resultVar.clone());
				assignment.setExp(apply);

				if (i == 0)
				{
					ifChecks.setIfExp(consInstanceCheck(obj, currentType));
					ifChecks.setThenStm(assignment);
				} else if (i < possibleTypes.size() - 1)
				{
					AElseIfStmCG elseIf = new AElseIfStmCG();
					elseIf.setElseIf(consInstanceCheck(obj, currentType));
					elseIf.setThenStm(assignment);

					ifChecks.getElseIf().add(elseIf);
				} else
				{
					ifChecks.setElseStm(assignment);
				}
			}

			baseAssistant.replaceNodeWith(parent, resultVar);
			replacementBlock.getLocalDefs().add(resultDecl);
			replacementBlock.getStatements().add(ifChecks);

			baseAssistant.replaceNodeWith(enclosingStatement, replacementBlock);
			replacementBlock.getStatements().add(enclosingStatement);
		}
	}

	private AInstanceofExpCG consInstanceCheck(SExpCG copy, STypeCG type)
	{
		AInstanceofExpCG check = new AInstanceofExpCG();
		check.setType(new ABoolBasicTypeCG());
		check.setCheckedType(type.clone());
		check.setExp(copy.clone());
		return check;
	}
	
	@Override
	public void caseAApplyExpCG(AApplyExpCG node) throws AnalysisException
	{
		for(SExpCG arg : node.getArgs())
		{
			arg.apply(this);
		}
		SExpCG root = node.getRoot();
		root.apply(this);
		
		if(root.getType() instanceof AUnionTypeCG)
		{
			SMapTypeCG mapType = getMapType(root, new TypeFinder<SMapTypeCG>(){

				@Override
				public SMapTypeCG findType(PType type) throws org.overture.ast.analysis.AnalysisException
				{
					SMapType mapType = info.getTcFactory().createPTypeAssistant().getMap(type);

					return mapType != null ? (SMapTypeCG) mapType.apply(info.getTypeVisitor(), info) : null; 
				}});
			
			if(mapType != null && node.getArgs().size() == 1)
			{
				correctTypes(root, mapType);
				return;
			}
		}
		else if(root.getType() instanceof AMethodTypeCG)
		{
			AMethodTypeCG methodType = (AMethodTypeCG) root.getType();
			
			LinkedList<STypeCG> paramTypes = methodType.getParams();
			
			LinkedList<SExpCG> args = node.getArgs();
			
			for(int i = 0; i < args.size(); i++)
			{
				SExpCG currentArg = args.get(i);						
				
				if(currentArg.getType() instanceof AUnionTypeCG)
				{
					correctTypes(currentArg, paramTypes.get(i));
				}
			}
		}
	}
	
	@Override
	public void inANotUnaryExpCG(ANotUnaryExpCG node)
			throws AnalysisException
	{
		correctTypes(node.getExp(), new ABoolBasicTypeCG());
	}
	
	@Override
	public void inAEqualsBinaryExpCG(AEqualsBinaryExpCG node)
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
	public void inANewExpCG(ANewExpCG node) throws AnalysisException
	{
		LinkedList<SExpCG> args = node.getArgs();
		
		boolean hasUnionTypes = false;
		
		for(SExpCG arg : args)
		{
			if(arg.getType() instanceof AUnionTypeCG)
			{
				hasUnionTypes = true;
				break;
			}
		}
		
		if(!hasUnionTypes)
		{
			return;
		}
		
		STypeCG type = node.getType();

		if (type instanceof AClassTypeCG)
		{
			for (AClassDeclCG classCg : classes)
			{
				for (AMethodDeclCG method : classCg.getMethods())
				{
					if (!method.getIsConstructor())
					{
						continue;
					}

					LinkedList<STypeCG> paramTypes = method.getMethodType().getParams();

					if (paramTypes.size() != args.size())
					{
						continue;
					}
					
					if(checkArgTypes(args, paramTypes))
					{
						return;
					}
				}
			}
		}
		else if(type instanceof ARecordTypeCG)
		{
			ARecordTypeCG recordType = (ARecordTypeCG) type;
			String definingClassName = recordType.getName().getDefiningClass();
			String recordName = recordType.getName().getName();
			
			for(AClassDeclCG classCg : classes)
			{
				for(ARecordDeclCG recordCg : classCg.getRecords())
				{
					if(definingClassName.equals(classCg.getName()) && recordName.equals(recordCg.getName()))
					{
						List<AFieldDeclCG> fields = recordCg.getFields();
						List<STypeCG> fieldTypes = new LinkedList<STypeCG>();
						
						for(AFieldDeclCG field : fields)
						{
							fieldTypes.add(field.getType());
						}
						if(checkArgTypes(args, fieldTypes))
						{
							return;
						}
					}
				}
			}
		}
	}

	private boolean checkArgTypes(List<SExpCG> args, List<STypeCG> paramTypes)
			throws AnalysisException
	{
		for (int i = 0; i < paramTypes.size(); i++)
		{
			SourceNode paramSourceNode = paramTypes.get(i).getSourceNode();
			SourceNode argTypeSourceNode = args.get(i).getType().getSourceNode();

			if (paramSourceNode == null || argTypeSourceNode == null)
			{
				return false;
			}

			org.overture.ast.node.INode paramTypeNode = paramSourceNode.getVdmNode();
			org.overture.ast.node.INode argTypeNode = argTypeSourceNode.getVdmNode();

			if (!(paramTypeNode instanceof PType) || !(argTypeNode instanceof PType))
			{
				return false;
			}
			
			if (!TypeComparator.compatible((PType) paramTypeNode, (PType) argTypeNode))
			{
				return false;
			}
		}
		
		for (int k = 0; k < paramTypes.size(); k++)
		{
			correctTypes(args.get(k), paramTypes.get(k));
		}
		
		return true;
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
		STypeCG expectedType = node.getType();

		if (!(expectedType instanceof AUnionTypeCG))
		{
			correctTypes(node.getExp(), expectedType);
		}
	}
	
	@Override
	public void inAElemsUnaryExpCG(AElemsUnaryExpCG node)
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
	public void inAMapDomainUnaryExpCG(AMapDomainUnaryExpCG node)
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
