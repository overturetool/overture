/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.trans.assistants;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.types.ASetType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SSeqType;
import org.overture.codegen.cgast.SExpCG;
import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AFormalParamLocalParamCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.AModuleDeclCG;
import org.overture.codegen.cgast.declarations.AStateDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AAndBoolBinaryExpCG;
import org.overture.codegen.cgast.expressions.AApplyExpCG;
import org.overture.codegen.cgast.expressions.ACastUnaryExpCG;
import org.overture.codegen.cgast.expressions.ACompSeqExpCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.AIntLiteralExpCG;
import org.overture.codegen.cgast.expressions.ALessNumericBinaryExpCG;
import org.overture.codegen.cgast.expressions.ANewExpCG;
import org.overture.codegen.cgast.expressions.ANotUnaryExpCG;
import org.overture.codegen.cgast.name.ATypeNameCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.ASetMultipleBindCG;
import org.overture.codegen.cgast.statements.AAssignToExpStmCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.ACallObjectExpStmCG;
import org.overture.codegen.cgast.statements.AForLoopStmCG;
import org.overture.codegen.cgast.statements.AIfStmCG;
import org.overture.codegen.cgast.statements.AIncrementStmCG;
import org.overture.codegen.cgast.statements.ALocalPatternAssignmentStmCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.AClassTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AMethodTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.cgast.types.SSeqTypeCG;
import org.overture.codegen.cgast.types.SSetTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.trans.IIterationStrategy;
import org.overture.codegen.trans.IterationVarPrefixes;

public class TransAssistantCG extends BaseTransformationAssistant
{
	protected IRInfo info;

	public TransAssistantCG(IRInfo info)
	{
		this.info = info;
	}

	public IRInfo getInfo()
	{
		return info;
	}

	public SSetTypeCG getSetTypeCloned(SExpCG set) throws AnalysisException
	{
		STypeCG typeCg = set.getType();

		return getSetTypeCloned(typeCg);
	}

	public SSetTypeCG getSetTypeCloned(STypeCG typeCg) throws AnalysisException
	{
		if (typeCg instanceof SSetTypeCG)
		{
			SSetTypeCG setTypeCg = (SSetTypeCG) typeCg;

			return setTypeCg.clone();
		} else
		{
			SourceNode sourceNode = typeCg.getSourceNode();

			if (sourceNode != null && sourceNode.getVdmNode() instanceof PType)
			{
				PType vdmType = (PType) sourceNode.getVdmNode();
				ASetType setType = info.getTcFactory().createPTypeAssistant().getSet(vdmType);
				try
				{
					typeCg = setType.apply(info.getTypeVisitor(), info);
					return (SSetTypeCG) typeCg;

				} catch (org.overture.ast.analysis.AnalysisException e)
				{
				}
			}

			throw new AnalysisException("Exptected set type. Got: " + typeCg);
		}
	}

	public SSeqTypeCG getSeqTypeCloned(SExpCG seq) throws AnalysisException
	{
		STypeCG typeCg = seq.getType();

		return getSeqTypeCloned(typeCg);
	}

	public SSeqTypeCG getSeqTypeCloned(STypeCG typeCg) throws AnalysisException
	{
		if (typeCg instanceof SSeqTypeCG)
		{
			SSeqTypeCG seqTypeCg = (SSeqTypeCG) typeCg;

			return seqTypeCg.clone();
		} else
		{
			SourceNode sourceNode = typeCg.getSourceNode();

			if (sourceNode != null && sourceNode.getVdmNode() instanceof PType)
			{
				PType vdmType = (PType) sourceNode.getVdmNode();
				SSeqType seqType = info.getTcFactory().createPTypeAssistant().getSeq(vdmType);
				try
				{
					typeCg = seqType.apply(info.getTypeVisitor(), info);
					return (SSeqTypeCG) typeCg;

				} catch (org.overture.ast.analysis.AnalysisException e)
				{
				}
			}

			throw new AnalysisException("Exptected sequence type. Got: "
					+ typeCg);
		}
	}

	public AIdentifierVarExpCG consSuccessVar(String successVarName)
	{
		AIdentifierVarExpCG successVar = new AIdentifierVarExpCG();
		successVar.setIsLambda(false);
		successVar.setIsLocal(true);
		successVar.setName(successVarName);
		successVar.setType(new ABoolBasicTypeCG());

		return successVar;
	}

	public AVarDeclCG consBoolVarDecl(String boolVarName, boolean initValue)
	{
		return info.getDeclAssistant().consLocalVarDecl(new ABoolBasicTypeCG(),
				info.getPatternAssistant().consIdPattern(boolVarName), info.getExpAssistant().consBoolLiteral(initValue));
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
		left.setIsLocal(true);
		left.setName(varName);

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
		boolVarExp.setIsLocal(true);
		boolVarExp.setName(boolVarName);

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

	public AAssignToExpStmCG consBoolVarAssignment(SExpCG predicate,
			String boolVarName)
	{
		AAssignToExpStmCG boolVarAssignment = new AAssignToExpStmCG();
		boolVarAssignment.setTarget(consBoolCheck(boolVarName, false));
		boolVarAssignment.setExp(predicate != null ? predicate.clone()
				: info.getExpAssistant().consBoolLiteral(true));

		return boolVarAssignment;
	}

	public AVarDeclCG consSetBindDecl(String setBindName, SExpCG set)
			throws AnalysisException
	{
		return info.getDeclAssistant().consLocalVarDecl(getSetTypeCloned(set),
				info.getPatternAssistant().consIdPattern(setBindName), set.clone());
	}

	public AVarDeclCG consDecl(String varName, STypeCG type, SExpCG exp)
	{
		return info.getDeclAssistant().consLocalVarDecl(type, info.getPatternAssistant().consIdPattern(varName), exp);
	}

	public AClassTypeCG consClassType(String classTypeName)
	{
		AClassTypeCG classType = new AClassTypeCG();
		classType.setName(classTypeName);

		return classType;
	}

	public SExpCG consInstanceCall(STypeCG instanceType, String instanceName,
			STypeCG returnType, String memberName, SExpCG... args)
	{
		AIdentifierVarExpCG instance = new AIdentifierVarExpCG();
		instance.setType(instanceType.clone());
		instance.setName(instanceName);
		instance.setIsLocal(true);

		AFieldExpCG fieldExp = new AFieldExpCG();
		fieldExp.setMemberName(memberName);
		fieldExp.setObject(instance);

		AMethodTypeCG methodType = new AMethodTypeCG();
		methodType.setResult(returnType.clone());

		AApplyExpCG instanceCall = new AApplyExpCG();

		instanceCall.setType(returnType.clone());

		if (args != null)
		{
			for (SExpCG arg : args)
			{
				methodType.getParams().add(arg.getType().clone());
				instanceCall.getArgs().add(arg);
			}
		}

		fieldExp.setType(methodType.clone());

		instanceCall.setRoot(fieldExp);

		return instanceCall;
	}

	// TODO: This actually forces the return type to be 'void'. Maybe generalise?
	public ACallObjectExpStmCG consInstanceCallStm(STypeCG instanceType,
			String instanceName, String memberName, SExpCG... args)
	{
		AIdentifierVarExpCG instance = new AIdentifierVarExpCG();
		instance.setName(instanceName);
		instance.setType(instanceType.clone());

		ACallObjectExpStmCG call = new ACallObjectExpStmCG();
		call.setType(new AVoidTypeCG());
		call.setFieldName(memberName);
		call.setObj(instance);

		for (SExpCG arg : args)
		{
			call.getArgs().add(arg);
		}

		return call;
	}

	public AVarDeclCG consNextElementDeclared(String iteratorTypeName,
			STypeCG elementType, SPatternCG id, String iteratorName,
			String nextElementMethod) throws AnalysisException
	{
		ACastUnaryExpCG cast = consNextElementCall(iteratorTypeName, iteratorName, elementType, nextElementMethod);

		return info.getDeclAssistant().consLocalVarDecl(elementType, id.clone(), cast);
	}

	public ALocalPatternAssignmentStmCG consNextElementAssignment(
			String iteratorTypeName, STypeCG elementType, SPatternCG id,
			String iteratorName, String nextElementMethod,
			AVarDeclCG nextElementDecl) throws AnalysisException
	{
		ACastUnaryExpCG cast = consNextElementCall(iteratorTypeName, iteratorName, elementType, nextElementMethod);

		ALocalPatternAssignmentStmCG assignment = new ALocalPatternAssignmentStmCG();
		assignment.setTarget(id.clone());
		assignment.setExp(cast);

		// Associate the pattern assignment with its declaration and
		// the corresponding success variable (both are graph fields)
		assignment.setTag(nextElementDecl.getTag());
		assignment.setNextElementDecl(nextElementDecl);
		// assignment.setSuccessVarDecl(successVarDecl);

		return assignment;
	}

	public ANewExpCG consDefaultConsCall(String className)
	{
		return consDefaultConsCall(consClassType(className));
	}

	public ANewExpCG consDefaultConsCall(AClassTypeCG classType)
	{
		ANewExpCG initAltNode = new ANewExpCG();
		initAltNode.setType(classType.clone());
		initAltNode.setName(consTypeNameForClass(classType.getName()));

		return initAltNode;
	}

	public ATypeNameCG consTypeNameForClass(String classTypeName)
	{
		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setDefiningClass(null);
		typeName.setName(classTypeName);

		return typeName;
	}

	public ACastUnaryExpCG consNextElementCall(String iteratorType,
			String iteratorName, STypeCG elementType, String nextElementMethod)
	{
		ACastUnaryExpCG cast = new ACastUnaryExpCG();
		cast.setType(elementType.clone());
		cast.setExp(consInstanceCall(consClassType(iteratorType), iteratorName, elementType.clone(), nextElementMethod));
		return cast;
	}

	public SStmCG consConditionalIncrement(String counterName, SExpCG predicate)
	{
		AIdentifierVarExpCG col = new AIdentifierVarExpCG();
		col.setType(new AIntNumericBasicTypeCG());
		col.setIsLambda(false);
		col.setIsLocal(true);
		col.setName(counterName);

		AIncrementStmCG inc = new AIncrementStmCG();
		inc.setVar(col);

		AIfStmCG ifStm = new AIfStmCG();
		ifStm.setIfExp(predicate);
		ifStm.setThenStm(inc);

		return ifStm;
	}

	public ABlockStmCG consIterationBlock(List<SPatternCG> ids, SExpCG set,
			ITempVarGen tempGen, IIterationStrategy strategy, IterationVarPrefixes iteVarPrefixes)
			throws AnalysisException
	{
		ABlockStmCG outerBlock = new ABlockStmCG();

		consIterationBlock(outerBlock, ids, set, tempGen, strategy, iteVarPrefixes);

		return outerBlock;
	}

	public AIdentifierVarExpCG consSetVar(String setName, SExpCG set)
	{
		if (set == null)
		{
			return null;
		}

		AIdentifierVarExpCG setVar = new AIdentifierVarExpCG();

		STypeCG setType = set.getType().clone();

		setVar.setType(setType);
		setVar.setName(setName);
		setVar.setIsLocal(true);

		return setVar;
	}

	private ABlockStmCG consIterationBlock(ABlockStmCG outerBlock,
			List<SPatternCG> patterns, SExpCG set, ITempVarGen tempGen,
			IIterationStrategy strategy, IterationVarPrefixes iteVarPrefixes) throws AnalysisException
	{
		// Variable names
		String setName = tempGen.nextVarName(iteVarPrefixes.set());
		AIdentifierVarExpCG setVar = consSetVar(setName, set);

		ABlockStmCG forBody = null;
		List<AVarDeclCG> extraDecls = strategy.getOuterBlockDecls(setVar, patterns);

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
				SPatternCG pattern = patterns.get(i);

				List<SStmCG> stms = strategy.getPreForLoopStms(setVar, patterns, pattern);

				if (stms != null)
				{
					nextBlock.getStatements().addAll(stms);
				}

				// Construct next for loop
				AForLoopStmCG forLoop = new AForLoopStmCG();

				forLoop.setInit(strategy.getForLoopInit(setVar, patterns, pattern));
				forLoop.setCond(strategy.getForLoopCond(setVar, patterns, pattern));
				forLoop.setInc(strategy.getForLoopInc(setVar, patterns, pattern));

				ABlockStmCG stmCollector = new ABlockStmCG();

				AVarDeclCG nextElementDeclared = strategy.getNextElementDeclared(setVar, patterns, pattern);

				if (nextElementDeclared != null)
				{
					stmCollector.getLocalDefs().add(nextElementDeclared);
				}

				ALocalPatternAssignmentStmCG assignment = strategy.getNextElementAssigned(setVar, patterns, pattern);

				if (assignment != null)
				{
					stmCollector.getStatements().add(assignment);
				}

				forBody = stmCollector;

				forLoop.setBody(forBody);

				nextBlock.getStatements().add(forLoop);

				if (++i < patterns.size())
				{
					nextBlock = forBody;
				} else
				{
					List<SStmCG> extraForLoopStatements = strategy.getForLoopStms(setVar, patterns, pattern);

					if (extraForLoopStatements != null)
					{
						forBody.getStatements().addAll(extraForLoopStatements);
					}

					break;
				}
			}
		}

		List<SStmCG> extraOuterBlockStms = strategy.getPostOuterBlockStms(setVar, patterns);

		if (extraOuterBlockStms != null)
		{
			outerBlock.getStatements().addAll(extraOuterBlockStms);
		}

		return forBody;
	}

	//FIXME make this method work on generic PMUltipleBinds
	public ABlockStmCG consComplexCompIterationBlock(
			List<ASetMultipleBindCG> multipleSetBinds, ITempVarGen tempGen,
			IIterationStrategy strategy, IterationVarPrefixes iteVarPrefixes) throws AnalysisException
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
			nextMultiBindBlock = consIterationBlock(nextMultiBindBlock, mb.getPatterns(), mb.getSet(), tempGen, strategy, iteVarPrefixes);

			strategy.setFirstBind(false);
		}

		return outerBlock;
	}

	public ACastUnaryExpCG consNextElementCall(String iteratorTypeName,
			String instance, String member, ACompSeqExpCG seqComp)
			throws AnalysisException
	{

		STypeCG elementType = getSeqTypeCloned(seqComp).getSeqOf();

		SExpCG nextCall = consInstanceCall(consClassType(iteratorTypeName), instance, elementType.clone(), member);
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

	public AFieldDeclCG consField(String access, STypeCG type, String name, SExpCG initExp)
	{
		AFieldDeclCG stateField = new AFieldDeclCG();
		stateField.setAccess(access);
		stateField.setType(type);
		stateField.setStatic(true);
		stateField.setFinal(false);
		stateField.setVolatile(false);
		stateField.setName(name);
		stateField.setInitial(initExp);
		
		return stateField;
	}

	public AApplyExpCG consConditionalCall(AMethodDeclCG node,
			AMethodDeclCG predMethod)
	{
		AIdentifierVarExpCG condVar = new AIdentifierVarExpCG();
		condVar.setType(predMethod.getMethodType().clone());
		condVar.setName(predMethod.getName());
		condVar.setIsLambda(false);
		condVar.setIsLocal(true);

		AApplyExpCG condCall = new AApplyExpCG();
		condCall.setType(new ABoolBasicTypeCG());
		condCall.setRoot(condVar);

		LinkedList<AFormalParamLocalParamCG> params = node.getFormalParams();

		for (AFormalParamLocalParamCG p : params)
		{
			SPatternCG paramPattern = p.getPattern();

			if (!(paramPattern instanceof AIdentifierPatternCG))
			{
				Logger.getLog().printErrorln("Expected parameter pattern to be an identifier pattern at this point. Got: "
						+ paramPattern);
				return null;
			}

			AIdentifierPatternCG paramId = (AIdentifierPatternCG) paramPattern;

			AIdentifierVarExpCG paramArg = new AIdentifierVarExpCG();
			paramArg.setType(p.getType().clone());
			paramArg.setIsLocal(true);
			paramArg.setIsLambda(false);
			paramArg.setName(paramId.getName());

			condCall.getArgs().add(paramArg);
		}

		return condCall;
	}

	public AVarDeclCG consClassVarDeclDefaultCtor(String className,
			String varName)
	{
		AClassTypeCG classType = consClassType(className);
		ANewExpCG init = consDefaultConsCall(className);

		AVarDeclCG classDecl = consDecl(varName, classType, init);
		classDecl.setFinal(true);

		return classDecl;
	}

	public ABlockStmCG wrap(AVarDeclCG decl)
	{
		ABlockStmCG block = new ABlockStmCG();
		block.getLocalDefs().add(decl);

		return block;
	}

	public ARecordTypeCG consRecType(String definingModule, String  recName)
	{
		ATypeNameCG typeName = new ATypeNameCG();
		typeName.setDefiningClass(definingModule);
		typeName.setName(recName);
		
		ARecordTypeCG recType = new ARecordTypeCG();
		recType.setName(typeName);

		return recType;
	}
	
	public ARecordTypeCG getRecType(final AStateDeclCG stateDecl)
	{
		ARecordTypeCG stateType = new ARecordTypeCG();
		stateType.setName(getTypeName(stateDecl));

		return stateType;
	}

	public ATypeNameCG getTypeName(final AStateDeclCG stateDecl)
	{
		ATypeNameCG stateName = new ATypeNameCG();
		stateName.setDefiningClass(getEnclosingModuleName(stateDecl));
		stateName.setName(stateDecl.getName());

		return stateName;
	}

	public String getEnclosingModuleName(AStateDeclCG stateDecl)
	{
		AModuleDeclCG module = stateDecl.getAncestor(AModuleDeclCG.class);

		if (module != null)
		{
			return module.getName();
		} else
		{
			Logger.getLog().printErrorln("Could not find enclosing module name of state declaration "
					+ stateDecl.getName()
					+ " in '"
					+ this.getClass().getSimpleName() + "'");
			return null;
		}
	}
	
	public ABlockStmCG wrap(SStmCG stm)
	{
		ABlockStmCG block = new ABlockStmCG();
		block.getStatements().add(stm);

		return block;
	}
}
