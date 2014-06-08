package org.overture.codegen.trans.assistants;

import java.util.List;

import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AVarLocalDeclCG;
import org.overture.codegen.cgast.declarations.SLocalDeclCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.ACompSeqExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.ALessNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.expressions.ANullExpCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.cgast.statements.AAssignmentStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AForLoopStmCG;
import org.overture.codegen.cgast.statements.AIdentifierStateDesignatorCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.AIncrementStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.SMapTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.trans.IIterationStrategy;
import org.overture.codegen.trans.TempVarPrefixes;

public class TransformationAssistantCG extends BaseTransformationAssistant
{
	protected IRInfo info;
	protected TempVarPrefixes varPrefixes;

	public TransformationAssistantCG(IRInfo info, TempVarPrefixes varPrefixes)
	{
		this.info = info;
		this.varPrefixes = varPrefixes;
	}

	public IRInfo getInfo()
	{
		return info;
	}

	public TempVarPrefixes getVarPrefixes()
	{
		return varPrefixes;
	}

	public SSetTypeCG getSetTypeCloned(SExpCG set) throws AnalysisException
	{
		STypeCG typeCg = set.getType();

		return getSetTypeCloned(typeCg);
	}

	public SSetTypeCG getSetTypeCloned(STypeCG typeCg) throws AnalysisException
	{
		if (!(typeCg instanceof SSetTypeCG))
			throw new AnalysisException("Exptected set type. Got: " + typeCg);

		SSetTypeCG setTypeCg = (SSetTypeCG) typeCg;

		return setTypeCg.clone();
	}

	public SSeqTypeCG getSeqTypeCloned(SExpCG seq) throws AnalysisException
	{
		STypeCG typeCg = seq.getType();

		return getSeqTypeCloned(typeCg);
	}

	public SSeqTypeCG getSeqTypeCloned(STypeCG typeCg) throws AnalysisException
	{
		if (!(typeCg instanceof SSeqTypeCG))
			throw new AnalysisException("Exptected sequence type. Got: "
					+ typeCg);

		SSeqTypeCG seqTypeCg = (SSeqTypeCG) typeCg;

		return seqTypeCg.clone();
	}

	public SMapTypeCG getMapTypeCloned(SExpCG map) throws AnalysisException
	{
		STypeCG typeCg = map.getType();

		return getMapTypeCloned(typeCg);
	}

	public SMapTypeCG getMapTypeCloned(STypeCG typeCg) throws AnalysisException
	{
		if (!(typeCg instanceof SMapTypeCG))
			throw new AnalysisException("Exptected map type. Got: " + typeCg);

		SMapTypeCG mapTypeCg = (SMapTypeCG) typeCg;

		return mapTypeCg.clone();
	}

	public AVarLocalDeclCG consBoolVarDecl(String boolVarName, boolean initValue)
	{
		AVarLocalDeclCG boolVarDecl = new AVarLocalDeclCG();

		boolVarDecl.setType(new ABoolBasicTypeCG());
		
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(boolVarName);
		
		boolVarDecl.setPattern(idPattern);
		boolVarDecl.setExp(info.getExpAssistant().consBoolLiteral(initValue));

		return boolVarDecl;
	}

	public SExpCG consAndExp(SExpCG left, SExpCG right)
	{
		AAndBoolBinaryExpCG andExp = new AAndBoolBinaryExpCG();
		andExp.setType(new ABoolBasicTypeCG());
		andExp.setLeft(left);
		andExp.setRight(right);

		return andExp;
	}

	public SExpCG consLessThanCheck(String varName, long value)
	{
		AIdentifierVarExpCG left = new AIdentifierVarExpCG();
		left.setType(new AIntNumericBasicTypeCG());
		left.setOriginal(varName);

		AIntLiteralExpCG right = info.getExpAssistant().consIntLiteral(value);

		ALessNumericBinaryExpCG less = new ALessNumericBinaryExpCG();
		less.setType(new ABoolBasicTypeCG());
		less.setLeft(left);
		less.setRight(right);

		return less;
	}

	public SExpCG consBoolCheck(String boolVarName, boolean negate)
	{
		AIdentifierVarExpCG boolVarExp = new AIdentifierVarExpCG();
		boolVarExp.setType(new ABoolBasicTypeCG());
		boolVarExp.setOriginal(boolVarName);

		if (negate)
		{
			ANotUnaryExpCG negated = new ANotUnaryExpCG();
			negated.setType(new ABoolBasicTypeCG());
			negated.setExp(boolVarExp);

			return negated;
		} else
		{
			return boolVarExp;
		}
	}

	public AAssignmentStmCG consBoolVarAssignment(SExpCG predicate,
			String boolVarName)
	{
		AAssignmentStmCG boolVarAssignment = new AAssignmentStmCG();

		boolVarAssignment.setTarget(consIdentifier(boolVarName));
		boolVarAssignment.setExp(predicate != null ? predicate.clone()
				: info.getExpAssistant().consBoolLiteral(true));

		return boolVarAssignment;
	}

	public AVarLocalDeclCG consSetBindDecl(String setBindName, SExpCG set)
			throws AnalysisException
	{
		AVarLocalDeclCG setBindDecl = new AVarLocalDeclCG();

		setBindDecl.setType(getSetTypeCloned(set));
		
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(setBindName);
		
		setBindDecl.setPattern(idPattern);
		setBindDecl.setExp(set.clone());

		return setBindDecl;
	}

	public AVarLocalDeclCG consIdDecl(STypeCG setType, String id)
			throws AnalysisException
	{
		AVarLocalDeclCG idDecl = new AVarLocalDeclCG();

		idDecl.setType(getSetTypeCloned(setType).getSetOf());

		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(id);
		
		idDecl.setPattern(idPattern);
		idDecl.setExp(new ANullExpCG());

		return idDecl;
	}

	public AVarLocalDeclCG consDecl(String varName, SExpCG exp)
	{
		AVarLocalDeclCG resultDecl = new AVarLocalDeclCG();

		resultDecl.setType(exp.getType().clone());
		
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(varName);
		
		resultDecl.setPattern(idPattern);
		resultDecl.setExp(exp);

		return resultDecl;
	}

	public AIdentifierStateDesignatorCG consIdentifier(String name)
	{
		AIdentifierStateDesignatorCG identifier = new AIdentifierStateDesignatorCG();
		identifier.setName(name);

		return identifier;
	}

	public AClassTypeCG consClassType(String classTypeName)
	{
		AClassTypeCG iteratorType = new AClassTypeCG();
		iteratorType.setName(classTypeName);

		return iteratorType;
	}

	public SExpCG consInstanceCall(STypeCG instanceType, String instanceName,
			STypeCG returnType, String memberName, SExpCG arg)
	{
		AIdentifierVarExpCG instance = new AIdentifierVarExpCG();
		instance.setOriginal(instanceName);
		instance.setType(instanceType.clone());

		AFieldExpCG fieldExp = new AFieldExpCG();
		fieldExp.setMemberName(memberName);
		fieldExp.setObject(instance);
		
		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(returnType.clone());

		AApplyExpCG instanceCall = new AApplyExpCG();
		
		instanceCall.setType(returnType.clone());

		if (arg != null)
		{
			methodType.getParams().add(arg.getType().clone());
			instanceCall.getArgs().add(arg);
		}
		
		fieldExp.setType(methodType.clone());
		
		instanceCall.setRoot(fieldExp);

		return instanceCall;
	}

	public AVarLocalDeclCG consNextElementDeclared(String iteratorTypeName,
			STypeCG elementType, String id, String iteratorName,
			String nextElementMethod) throws AnalysisException
	{
		ACastUnaryExpCG cast = consNextElementCall(iteratorTypeName, iteratorName, elementType, nextElementMethod);
		AVarLocalDeclCG decl = new AVarLocalDeclCG();

		decl.setType(elementType);
		
		AIdentifierPatternCG idPattern = new AIdentifierPatternCG();
		idPattern.setName(id);
		
		decl.setPattern(idPattern);
		decl.setExp(cast);

		return decl;
	}

	public AAssignmentStmCG consNextElementAssignment(String iteratorTypeName,
			STypeCG elementType, String id, String iteratorName,
			String nextElementMethod) throws AnalysisException
	{
		ACastUnaryExpCG cast = consNextElementCall(iteratorTypeName, iteratorName, elementType, nextElementMethod);

		AAssignmentStmCG assignment = new AAssignmentStmCG();
		assignment.setTarget(consIdentifier(id));
		assignment.setExp(cast);

		return assignment;
	}

	public ACastUnaryExpCG consNextElementCall(String iteratorType,
			String iteratorName, STypeCG elementType, String nextElementMethod)
	{
		ACastUnaryExpCG cast = new ACastUnaryExpCG();
		cast.setType(elementType.clone());
		cast.setExp(consInstanceCall(consClassType(iteratorType), iteratorName, elementType.clone(), nextElementMethod, null));
		return cast;
	}

	public SStmCG consConditionalIncrement(String counterName, SExpCG predicate)
	{
		AIdentifierVarExpCG col = new AIdentifierVarExpCG();
		col.setType(new AIntNumericBasicTypeCG());
		col.setOriginal(counterName);

		AIncrementStmCG inc = new AIncrementStmCG();
		inc.setVar(col);

		AIfStmCG ifStm = new AIfStmCG();
		ifStm.setIfExp(predicate);
		ifStm.setThenStm(inc);

		return ifStm;
	}

	public ABlockStmCG consIterationBlock(List<AIdentifierPatternCG> ids,
			SExpCG set, ITempVarGen tempGen, IIterationStrategy strategy)
			throws AnalysisException
	{
		ABlockStmCG outerBlock = new ABlockStmCG();

		consIterationBlock(outerBlock, ids, set, tempGen, strategy);

		return outerBlock;
	}

	public AIdentifierVarExpCG consSetVar(String setName, SExpCG set)
	{
		if (set == null)
			return null;

		AIdentifierVarExpCG setVar = new AIdentifierVarExpCG();

		STypeCG setType = set.getType().clone();

		setVar.setOriginal(setName);
		setVar.setType(setType);

		return setVar;
	}

	public ABlockStmCG consIterationBlock(ABlockStmCG outerBlock,
			List<AIdentifierPatternCG> ids, SExpCG set, ITempVarGen tempGen,
			IIterationStrategy strategy) throws AnalysisException
	{
		// Variable names
		String setName = tempGen.nextVarName(varPrefixes.getSetNamePrefix());
		AIdentifierVarExpCG setVar = consSetVar(setName, set);

		ABlockStmCG forBody = null;
		List<? extends SLocalDeclCG> extraDecls = strategy.getOuterBlockDecls(setVar, ids);

		if (extraDecls != null)
		{
			outerBlock.getLocalDefs().addAll(extraDecls);
		}

		if (setVar != null)
		{
			outerBlock.getLocalDefs().add(consSetBindDecl(setName, set));

			ABlockStmCG nextBlock = outerBlock;

			for (int i = 0;;)
			{
				AIdentifierPatternCG id = ids.get(i);

				// Construct next for loop
				AForLoopStmCG forLoop = new AForLoopStmCG();

				forLoop.setInit(strategy.getForLoopInit(setVar, ids, id));
				forLoop.setCond(strategy.getForLoopCond(setVar, ids, id));
				forLoop.setInc(strategy.getForLoopInc(setVar, ids, id));

				ABlockStmCG stmCollector = new ABlockStmCG();

				AVarLocalDeclCG nextElementDeclared = strategy.getNextElementDeclared(setVar, ids, id);

				if (nextElementDeclared != null)
					stmCollector.getLocalDefs().add(nextElementDeclared);

				AAssignmentStmCG assignment = strategy.getNextElementAssigned(setVar, ids, id);

				if (assignment != null)
					stmCollector.getStatements().add(assignment);

				forBody = stmCollector;

				forLoop.setBody(forBody);

				nextBlock.getStatements().add(forLoop);

				if (++i < ids.size())
				{
					nextBlock = forBody;
				} else
				{
					List<SStmCG> extraForLoopStatements = strategy.getForLoopStms(setVar, ids, id);

					if (extraForLoopStatements != null)
					{
						forBody.getStatements().addAll(extraForLoopStatements);
					}

					break;
				}
			}
		}

		List<SStmCG> extraOuterBlockStms = strategy.getOuterBlockStms(setVar, ids);

		if (extraOuterBlockStms != null)
		{
			outerBlock.getStatements().addAll(extraOuterBlockStms);
		}

		return forBody;
	}

	public ABlockStmCG consComplexCompIterationBlock(
			List<ASetMultipleBindCG> multipleSetBinds, ITempVarGen tempGen,
			IIterationStrategy strategy) throws AnalysisException
	{
		ABlockStmCG outerBlock = new ABlockStmCG();

		ABlockStmCG nextMultiBindBlock = outerBlock;

		for (ASetMultipleBindCG bind : multipleSetBinds)
		{
			SSetTypeCG setType = getSetTypeCloned(bind.getSet());

			if (setType.getEmpty())
			{
				multipleSetBinds.clear();
				return outerBlock;
			}
		}

		strategy.setFirstBind(true);

		for (int i = 0; i < multipleSetBinds.size(); i++)
		{
			strategy.setLastBind(i == multipleSetBinds.size() - 1);

			ASetMultipleBindCG mb = multipleSetBinds.get(i);
			nextMultiBindBlock = consIterationBlock(nextMultiBindBlock, mb.getPatterns(), mb.getSet(), tempGen, strategy);

			strategy.setFirstBind(false);
		}

		return outerBlock;
	}

	public ACastUnaryExpCG consNextElementCall(String iteratorTypeName,
			String instance, String member, ACompSeqExpCG seqComp)
			throws AnalysisException
	{

		STypeCG elementType = getSeqTypeCloned(seqComp).getSeqOf();

		SExpCG nextCall = consInstanceCall(consClassType(iteratorTypeName), instance, elementType.clone(), member, null);
		ACastUnaryExpCG cast = new ACastUnaryExpCG();
		cast.setType(elementType.clone());
		cast.setExp(nextCall);

		return cast;
	}

	public Boolean hasEmptySet(ASetMultipleBindCG binding)
			throws AnalysisException
	{
		return isEmptySet(binding.getSet());
	}

	public Boolean isEmptySet(SExpCG set) throws AnalysisException
	{
		return getSetTypeCloned(set).getEmpty();
	}

	public void cleanUpBinding(ASetMultipleBindCG binding)
	{
		binding.setSet(null);
		binding.getPatterns().clear();
	}
}
