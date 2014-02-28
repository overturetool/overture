package org.overture.codegen.transform;

import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.ALocalVarDeclCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.AExplicitVariableExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.expressions.AVariableExpCG;
import org.overture.codegen.cgast.expressions.PExpCG;
import org.overture.codegen.cgast.pattern.AIdentifierPatternCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectStmCG;
import org.overture.codegen.cgast.statements.AForLoopStmCG;
import org.overture.codegen.cgast.statements.AIdentifierObjectDesignatorCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.PStmCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.cgast.types.PTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.constants.JavaTempVarPrefixes;
import org.overture.codegen.javalib.VDMSeq;
import org.overture.codegen.utils.TempVarNameGen;

public class TransformationAssistantCG
{
	public void replaceNodeWith(INode original, INode replacement)
	{
		INode parent = original.parent();
		parent.replaceChild(original, replacement);
		original.parent(null);
	}
	
	public SSetTypeCG getSetTypeCloned(PExpCG set)
			throws AnalysisException
	{
		PTypeCG typeCg = set.getType();

		return getSetTypeCloned(typeCg);
	}
	
	public SSetTypeCG getSetTypeCloned(PTypeCG typeCg) throws AnalysisException
	{
		if(!(typeCg instanceof SSetTypeCG))
			throw new AnalysisException("Exptected set type for set expression. Got: " + typeCg);
		
		SSetTypeCG setTypeCg = (SSetTypeCG) typeCg;
		
		return setTypeCg.clone();
	}
	
	public ALocalVarDeclCG consSetBindDecl(String setBindName, PExpCG set) throws AnalysisException
	{
		ALocalVarDeclCG setBindDecl = new ALocalVarDeclCG();
		
		setBindDecl.setType(getSetTypeCloned(set));
		setBindDecl.setName(setBindName);
		setBindDecl.setExp(set.clone());
		
		return setBindDecl;
	}
	
	public ALocalVarDeclCG consIdDecl(PTypeCG setType, String id) throws AnalysisException
	{
		ALocalVarDeclCG idDecl = new ALocalVarDeclCG();
		
		idDecl.setType(getSetTypeCloned(setType).getSetOf());
		idDecl.setName(id);
		idDecl.setExp(new ANullExpCG());
		
		return idDecl;
	}

	public ALocalVarDeclCG consDecl(String varName, PExpCG exp)
	{
		ALocalVarDeclCG resultDecl = new ALocalVarDeclCG();
		
		resultDecl.setType(exp.getType().clone());
		resultDecl.setName(varName);
		resultDecl.setExp(exp);
		
		return resultDecl;
	}
	
	public AIdentifierStateDesignatorCG consIdentifier(String name)
	{
		AIdentifierStateDesignatorCG identifier = new AIdentifierStateDesignatorCG();
		identifier.setName(name);

		return identifier;
	}
	
	public AClassTypeCG consIteratorType()
	{
		return consClassType(IJavaCodeGenConstants.ITERATOR_TYPE);
	}
	
	public AClassTypeCG consClassType(String classTypeName)
	{
		AClassTypeCG iteratorType = new AClassTypeCG();
		iteratorType.setName(classTypeName);
		
		return iteratorType;
	}
	
	public PExpCG consInstanceCall(PTypeCG instanceType, String instanceName, PTypeCG returnType, String memberName, PExpCG arg)
	{
		AVariableExpCG instance = new AVariableExpCG();
		instance.setOriginal(instanceName);
		instance.setType(instanceType.clone());

		AFieldExpCG fieldExp = new AFieldExpCG();
		fieldExp.setMemberName(memberName);
		fieldExp.setObject(instance);
		fieldExp.setType(returnType.clone());
		
		AApplyExpCG instanceCall = new AApplyExpCG();
		instanceCall.setRoot(fieldExp);
		instanceCall.setType(returnType.clone());
		
		if(arg != null)
		{
			instanceCall.getArgs().add(arg);
		}
			
		return instanceCall;
	}
	
	public ALocalVarDeclCG consIteratorDecl(String iteratorName, String collectionName)
	{
		ALocalVarDeclCG iterator = new ALocalVarDeclCG();
		iterator.setName(iteratorName);
		iterator.setType(consIteratorType());
		iterator.setExp(consInstanceCall(consClassType(VDMSeq.class.getName()), collectionName, consIteratorType(), IJavaCodeGenConstants.GET_ITERATOR , null));
		
		return iterator;
	}
	
	public ABlockStmCG consForBody(PTypeCG setType, String id, String iteratorName) throws AnalysisException
	{
		ABlockStmCG forBody = new ABlockStmCG();
		
		forBody.getStatements().add(consNextElement(setType, id, iteratorName));
		
		return forBody;
	}

	private AAssignmentStmCG consNextElement(PTypeCG setType, String id, String iteratorName)
			throws AnalysisException
	{
		PTypeCG elementType = getSetTypeCloned(setType).getSetOf();

		ACastUnaryExpCG cast = new ACastUnaryExpCG();
		cast.setType(elementType.clone());
		cast.setExp(consInstanceCall(consIteratorType(), iteratorName, elementType.clone(), IJavaCodeGenConstants.NEXT_ELEMENT_ITERATOR, null));
		
		AAssignmentStmCG assignment = new AAssignmentStmCG();
		assignment.setTarget(consIdentifier(id));
		assignment.setExp(cast);

		return assignment;
	}
	
	public ALocalVarDeclCG consCompResultDecl(PTypeCG collectionType, String varDeclName, String className, String memberName) throws AnalysisException
	{
		AExplicitVariableExpCG member = new AExplicitVariableExpCG();
		member.setType(collectionType);
		member.setClassType(consClassType(className));
		member.setName(memberName);

		AApplyExpCG call = new AApplyExpCG();
		call.setType(collectionType.clone());
		call.setRoot(member);
		
		ALocalVarDeclCG resCollection = new ALocalVarDeclCG();
		resCollection.setType(collectionType.clone());
		resCollection.setName(varDeclName);
		resCollection.setExp(call);
		
		return resCollection; 
	}
	
	public PStmCG consConditionalAdd(String resCollectionName, PExpCG first, PExpCG predicate)
	{
		AVariableExpCG col = new AVariableExpCG();
		col.setOriginal(resCollectionName);
		
		AIdentifierObjectDesignatorCG identifier = new AIdentifierObjectDesignatorCG();
		identifier.setExp(col);
		
		ACallObjectStmCG callStm = new ACallObjectStmCG();
		callStm.setClassName(null);
		callStm.setFieldName(IJavaCodeGenConstants.ADD_ELEMENT_TO_COLLECTION);
		callStm.setDesignator(identifier);
		callStm.getArgs().add(first);
		callStm.setType(new AVoidTypeCG());
		
		if(predicate != null)
		{
			AIfStmCG ifStm = new AIfStmCG();
			ifStm.setIfExp(predicate);
			ifStm.setThenStm(callStm);
			
			return ifStm;
		}
		
		return callStm;
	}
	
	public ABlockStmCG consBlock(List<AIdentifierPatternCG> ids, PExpCG set, PExpCG suchThat, TempVarNameGen tempGen, AbstractIterationStrategy strategy)
			throws AnalysisException
	{
		PTypeCG setType = set.getType();
		
		//Variable names
		String setName = tempGen.nextVarName(JavaTempVarPrefixes.SET_NAME_PREFIX);

		ABlockStmCG outerBlock = new ABlockStmCG();
		LinkedList<ALocalVarDeclCG> outerBlockDecls = outerBlock.getLocalDefs();

		outerBlockDecls.add(consSetBindDecl(setName, set));
		
		List<ALocalVarDeclCG> extraDecls = strategy.getOuterBlockDecls();
		
		if(extraDecls != null)
		{
			outerBlockDecls.addAll(extraDecls);
		}
		
		ABlockStmCG nextBlock = outerBlock;
		
		int numberOfIds = ids.size();
		
		for (int i = 0;;)
		{
			AIdentifierPatternCG id = ids.get(i);

			//Add next id to outer block
			outerBlockDecls.add(consIdDecl(setType, id.getName()));

			//Construct next for loop
			String iteratorName = tempGen.nextVarName(JavaTempVarPrefixes.ITERATOR_NAME_PREFIX);
			
			AForLoopStmCG forLoop = new AForLoopStmCG();
			forLoop.setInit(consIteratorDecl(iteratorName, setName));
			forLoop.setCond(strategy.getForLoopCond(iteratorName));
			forLoop.setInc(null);
			
			ABlockStmCG forBody = consForBody(setType, id.getName(), iteratorName);
			forLoop.setBody(forBody);

			nextBlock.getStatements().add(forLoop);
			
			if (++i < numberOfIds) 
			{
				nextBlock = forBody;
			}
			else
			{
				List<PStmCG> extraForLoopStatements = strategy.getLastForLoopStms();
				
				if(extraForLoopStatements != null)
				{
					forBody.getStatements().addAll(extraForLoopStatements);
				}
				
				break;
			}
		}

		List<PStmCG> extraOuterBlockStms = strategy.getOuterBlockStms();
		
		if(extraOuterBlockStms != null)
		{
			outerBlock.getStatements().addAll(extraOuterBlockStms);
		}
		
		return outerBlock;
	}
}
