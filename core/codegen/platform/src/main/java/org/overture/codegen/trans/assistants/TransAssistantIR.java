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

import org.apache.log4j.Logger;
import org.overture.ast.lex.Dialect;
import org.overture.ast.types.PType;
import org.overture.ast.types.SSeqType;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.ir.ITempVarGen;
import org.overture.codegen.ir.SExpIR;
import org.overture.codegen.ir.SMultipleBindIR;
import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.SStmIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.SourceNode;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;
import org.overture.codegen.ir.declarations.AFieldDeclIR;
import org.overture.codegen.ir.declarations.AFormalParamLocalParamIR;
import org.overture.codegen.ir.declarations.AMethodDeclIR;
import org.overture.codegen.ir.declarations.AModuleDeclIR;
import org.overture.codegen.ir.declarations.AStateDeclIR;
import org.overture.codegen.ir.declarations.AVarDeclIR;
import org.overture.codegen.ir.expressions.AAndBoolBinaryExpIR;
import org.overture.codegen.ir.expressions.AApplyExpIR;
import org.overture.codegen.ir.expressions.ACastUnaryExpIR;
import org.overture.codegen.ir.expressions.ACompSeqExpIR;
import org.overture.codegen.ir.expressions.AFieldExpIR;
import org.overture.codegen.ir.expressions.AIdentifierVarExpIR;
import org.overture.codegen.ir.expressions.AIntLiteralExpIR;
import org.overture.codegen.ir.expressions.ALessNumericBinaryExpIR;
import org.overture.codegen.ir.expressions.ANewExpIR;
import org.overture.codegen.ir.expressions.ANotUnaryExpIR;
import org.overture.codegen.ir.name.ATypeNameIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.patterns.ASeqMultipleBindIR;
import org.overture.codegen.ir.patterns.ASetMultipleBindIR;
import org.overture.codegen.ir.statements.AAssignToExpStmIR;
import org.overture.codegen.ir.statements.ABlockStmIR;
import org.overture.codegen.ir.statements.ACallObjectExpStmIR;
import org.overture.codegen.ir.statements.AForLoopStmIR;
import org.overture.codegen.ir.statements.AIfStmIR;
import org.overture.codegen.ir.statements.AIncrementStmIR;
import org.overture.codegen.ir.statements.ALocalPatternAssignmentStmIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.AClassTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.AMethodTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.ASeqSeqTypeIR;
import org.overture.codegen.ir.types.ASetSetTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.ir.types.AVoidTypeIR;
import org.overture.codegen.ir.types.SSeqTypeIR;
import org.overture.codegen.ir.types.SSetTypeIR;
import org.overture.codegen.trans.IIterationStrategy;
import org.overture.codegen.trans.IterationVarPrefixes;
import org.overture.config.Settings;

public class TransAssistantIR extends BaseTransformationAssistant
{
	protected IRInfo info;

	private Logger log = Logger.getLogger(this.getClass().getName());

	public TransAssistantIR(IRInfo info)
	{
		this.info = info;
	}

	public IRInfo getInfo()
	{
		return info;
	}

	public SSetTypeIR getSetTypeCloned(SExpIR set) throws AnalysisException
	{
		STypeIR typeCg = set.getType();

		return getSetTypeCloned(typeCg);
	}

	public SSetTypeIR getSetTypeCloned(STypeIR typeCg) throws AnalysisException
	{
		if (typeCg instanceof SSetTypeIR)
		{
			SSetTypeIR setTypeCg = (SSetTypeIR) typeCg;

			return setTypeCg.clone();
		}

		return null;
	}

	public SSeqTypeIR getSeqTypeCloned(SExpIR seq) throws AnalysisException
	{
		STypeIR typeCg = seq.getType();

		return getSeqTypeCloned(typeCg);
	}

	public SSeqTypeIR getSeqTypeCloned(STypeIR typeCg) throws AnalysisException
	{
		if (typeCg instanceof SSeqTypeIR)
		{
			SSeqTypeIR seqTypeCg = (SSeqTypeIR) typeCg;

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
					return (SSeqTypeIR) typeCg;

				} catch (org.overture.ast.analysis.AnalysisException e)
				{
				}
			}

			throw new AnalysisException("Exptected sequence type. Got: "
					+ typeCg);
		}
	}

	public STypeIR getElementType(STypeIR t)
	{
		STypeIR elementType;

		if (t instanceof ASetSetTypeIR)
		{
			elementType = ((ASetSetTypeIR) t).getSetOf().clone();
		} else if (t instanceof ASeqSeqTypeIR)
		{
			elementType = ((ASeqSeqTypeIR) t).getSeqOf().clone();
		} else
		{
			log.error("Expected set or sequence type. Got: " + t);
			elementType = new AUnknownTypeIR();
			elementType.setSourceNode(t.getSourceNode());
		}

		return elementType;
	}

	public AIdentifierVarExpIR consSuccessVar(String successVarName)
	{
		AIdentifierVarExpIR successVar = new AIdentifierVarExpIR();
		successVar.setIsLambda(false);
		successVar.setIsLocal(true);
		successVar.setName(successVarName);
		successVar.setType(new ABoolBasicTypeIR());

		return successVar;
	}

	public AVarDeclIR consBoolVarDecl(String boolVarName, boolean initValue)
	{
		return info.getDeclAssistant().consLocalVarDecl(new ABoolBasicTypeIR(), info.getPatternAssistant().consIdPattern(boolVarName), info.getExpAssistant().consBoolLiteral(initValue));
	}

	public SExpIR consAndExp(SExpIR left, SExpIR right)
	{
		AAndBoolBinaryExpIR andExp = new AAndBoolBinaryExpIR();
		andExp.setType(new ABoolBasicTypeIR());
		andExp.setLeft(left);
		andExp.setRight(right);

		return andExp;
	}

	public SExpIR consLessThanCheck(String varName, long value)
	{
		AIdentifierVarExpIR left = new AIdentifierVarExpIR();
		left.setType(new AIntNumericBasicTypeIR());
		left.setIsLocal(true);
		left.setName(varName);

		AIntLiteralExpIR right = info.getExpAssistant().consIntLiteral(value);

		ALessNumericBinaryExpIR less = new ALessNumericBinaryExpIR();
		less.setType(new ABoolBasicTypeIR());
		less.setLeft(left);
		less.setRight(right);

		return less;
	}

	public SExpIR consBoolCheck(String boolVarName, boolean negate)
	{
		AIdentifierVarExpIR boolVarExp = new AIdentifierVarExpIR();
		boolVarExp.setType(new ABoolBasicTypeIR());
		boolVarExp.setIsLocal(true);
		boolVarExp.setName(boolVarName);

		if (negate)
		{
			ANotUnaryExpIR negated = new ANotUnaryExpIR();
			negated.setType(new ABoolBasicTypeIR());
			negated.setExp(boolVarExp);

			return negated;
		} else
		{
			return boolVarExp;
		}
	}

	public AAssignToExpStmIR consBoolVarAssignment(SExpIR predicate,
			String boolVarName)
	{
		AAssignToExpStmIR boolVarAssignment = new AAssignToExpStmIR();
		boolVarAssignment.setTarget(consBoolCheck(boolVarName, false));
		boolVarAssignment.setExp(predicate != null ? predicate.clone()
				: info.getExpAssistant().consBoolLiteral(true));

		return boolVarAssignment;
	}

	public AVarDeclIR consSetBindDecl(String setBindName, SExpIR col)
			throws AnalysisException
	{
		return info.getDeclAssistant().consLocalVarDecl(col.getType().clone(), info.getPatternAssistant().consIdPattern(setBindName), col.clone());
	}

	public AVarDeclIR consDecl(String varName, STypeIR type, SExpIR exp)
	{
		return info.getDeclAssistant().consLocalVarDecl(type, info.getPatternAssistant().consIdPattern(varName), exp);
	}

	public AClassTypeIR consClassType(String classTypeName)
	{
		AClassTypeIR classType = new AClassTypeIR();
		classType.setName(classTypeName);

		return classType;
	}

	public SExpIR consInstanceCall(STypeIR instanceType, String instanceName,
			STypeIR returnType, String memberName, SExpIR... args)
	{
		AIdentifierVarExpIR instance = new AIdentifierVarExpIR();
		instance.setType(instanceType.clone());
		instance.setName(instanceName);
		instance.setIsLocal(true);

		AFieldExpIR fieldExp = new AFieldExpIR();
		fieldExp.setMemberName(memberName);
		fieldExp.setObject(instance);

		AMethodTypeIR methodType = new AMethodTypeIR();
		methodType.setResult(returnType.clone());

		AApplyExpIR instanceCall = new AApplyExpIR();

		instanceCall.setType(returnType.clone());

		if (args != null)
		{
			for (SExpIR arg : args)
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
	public ACallObjectExpStmIR consInstanceCallStm(STypeIR instanceType,
			String instanceName, String memberName, SExpIR... args)
	{
		AIdentifierVarExpIR instance = new AIdentifierVarExpIR();
		instance.setName(instanceName);
		instance.setType(instanceType.clone());

		ACallObjectExpStmIR call = new ACallObjectExpStmIR();
		call.setType(new AVoidTypeIR());
		call.setFieldName(memberName);
		call.setObj(instance);

		for (SExpIR arg : args)
		{
			call.getArgs().add(arg);
		}

		return call;
	}

	public AVarDeclIR consNextElementDeclared(STypeIR iteratorType,
			STypeIR elementType, SPatternIR id, String iteratorName,
			String nextElementMethod) throws AnalysisException
	{
		ACastUnaryExpIR cast = consNextElementCall(iteratorType, iteratorName, elementType, nextElementMethod);

		return info.getDeclAssistant().consLocalVarDecl(elementType, id.clone(), cast);
	}

	public ALocalPatternAssignmentStmIR consNextElementAssignment(
			STypeIR iteratorType, STypeIR elementType, SPatternIR id,
			String iteratorName, String nextElementMethod,
			AVarDeclIR nextElementDecl) throws AnalysisException
	{
		ACastUnaryExpIR cast = consNextElementCall(iteratorType, iteratorName, elementType, nextElementMethod);

		ALocalPatternAssignmentStmIR assignment = new ALocalPatternAssignmentStmIR();
		assignment.setTarget(id.clone());
		assignment.setExp(cast);

		// Associate the pattern assignment with its declaration and
		// the corresponding success variable (both are graph fields)
		assignment.setTag(nextElementDecl.getTag());
		assignment.setNextElementDecl(nextElementDecl);
		// assignment.setSuccessVarDecl(successVarDecl);

		return assignment;
	}

	public ANewExpIR consDefaultConsCall(String className)
	{
		return consDefaultConsCall(consClassType(className));
	}

	public ANewExpIR consDefaultConsCall(AClassTypeIR classType)
	{
		ANewExpIR initAltNode = new ANewExpIR();
		initAltNode.setType(classType.clone());
		initAltNode.setName(consTypeNameForClass(classType.getName()));

		return initAltNode;
	}

	public ATypeNameIR consTypeNameForClass(String classTypeName)
	{
		ATypeNameIR typeName = new ATypeNameIR();
		typeName.setDefiningClass(null);
		typeName.setName(classTypeName);

		return typeName;
	}

	public ACastUnaryExpIR consNextElementCall(STypeIR iteratorType,
			String iteratorName, STypeIR elementType, String nextElementMethod)
	{
		ACastUnaryExpIR cast = new ACastUnaryExpIR();
		cast.setType(elementType.clone());
		cast.setExp(consInstanceCall(iteratorType, iteratorName, elementType.clone(), nextElementMethod));
		return cast;
	}

	public SStmIR consConditionalIncrement(String counterName, SExpIR predicate)
	{
		AIdentifierVarExpIR col = new AIdentifierVarExpIR();
		col.setType(new AIntNumericBasicTypeIR());
		col.setIsLambda(false);
		col.setIsLocal(true);
		col.setName(counterName);

		AIncrementStmIR inc = new AIncrementStmIR();
		inc.setVar(col);

		AIfStmIR ifStm = new AIfStmIR();
		ifStm.setIfExp(predicate);
		ifStm.setThenStm(inc);

		return ifStm;
	}

	public ABlockStmIR consIterationBlock(List<SPatternIR> ids, SExpIR set,
			ITempVarGen tempGen, IIterationStrategy strategy,
			IterationVarPrefixes iteVarPrefixes) throws AnalysisException
	{
		ABlockStmIR outerBlock = new ABlockStmIR();

		consIterationBlock(outerBlock, ids, set, tempGen, strategy, iteVarPrefixes);

		return outerBlock;
	}

	public AIdentifierVarExpIR consSetVar(String setName, SExpIR set)
	{
		if (set == null)
		{
			return null;
		}

		AIdentifierVarExpIR setVar = new AIdentifierVarExpIR();

		STypeIR setType = set.getType().clone();

		setVar.setType(setType);
		setVar.setName(setName);
		setVar.setIsLocal(true);

		return setVar;
	}

	private ABlockStmIR consIterationBlock(ABlockStmIR outerBlock,
			List<SPatternIR> patterns, SExpIR set, ITempVarGen tempGen,
			IIterationStrategy strategy, IterationVarPrefixes iteVarPrefixes)
			throws AnalysisException
	{
		// Variable names
		String setName = tempGen.nextVarName(iteVarPrefixes.set());
		AIdentifierVarExpIR setVar = consSetVar(setName, set);

		ABlockStmIR forBody = null;
		List<AVarDeclIR> extraDecls = strategy.getOuterBlockDecls(setVar, patterns);

		if (extraDecls != null)
		{
			outerBlock.getLocalDefs().addAll(extraDecls);
		}

		if (setVar != null)
		{
			outerBlock.getLocalDefs().add(consSetBindDecl(setName, set));

			ABlockStmIR nextBlock = outerBlock;

			for (int i = 0;;)
			{
				SPatternIR pattern = patterns.get(i);

				List<SStmIR> stms = strategy.getPreForLoopStms(setVar, patterns, pattern);

				if (stms != null)
				{
					nextBlock.getStatements().addAll(stms);
				}

				// Construct next for loop
				AForLoopStmIR forLoop = new AForLoopStmIR();

				forLoop.setInit(strategy.getForLoopInit(setVar, patterns, pattern));
				forLoop.setCond(strategy.getForLoopCond(setVar, patterns, pattern));
				forLoop.setInc(strategy.getForLoopInc(setVar, patterns, pattern));

				ABlockStmIR stmCollector = new ABlockStmIR();

				AVarDeclIR nextElementDeclared = strategy.getNextElementDeclared(setVar, patterns, pattern);

				if (nextElementDeclared != null)
				{
					stmCollector.getLocalDefs().add(nextElementDeclared);
				}

				ALocalPatternAssignmentStmIR assignment = strategy.getNextElementAssigned(setVar, patterns, pattern);

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
					List<SStmIR> extraForLoopStatements = strategy.getForLoopStms(setVar, patterns, pattern);

					if (extraForLoopStatements != null)
					{
						forBody.getStatements().addAll(extraForLoopStatements);
					}

					break;
				}
			}
		}

		List<SStmIR> extraOuterBlockStms = strategy.getPostOuterBlockStms(setVar, patterns);

		if (extraOuterBlockStms != null)
		{
			outerBlock.getStatements().addAll(extraOuterBlockStms);
		}

		return forBody;
	}

	// FIXME make this method work on generic PMUltipleBinds
	public ABlockStmIR consComplexCompIterationBlock(
			List<SMultipleBindIR> multipleSetBinds, ITempVarGen tempGen,
			IIterationStrategy strategy, IterationVarPrefixes iteVarPrefixes)
			throws AnalysisException
	{
		ABlockStmIR outerBlock = new ABlockStmIR();

		ABlockStmIR nextMultiBindBlock = outerBlock;

		for (SMultipleBindIR bind : multipleSetBinds)
		{
			if (hasEmptySet(bind))
			{
				multipleSetBinds.clear();
				return outerBlock;
			}
		}

		strategy.setFirstBind(true);

		for (int i = 0; i < multipleSetBinds.size(); i++)
		{
			strategy.setLastBind(i == multipleSetBinds.size() - 1);

			SMultipleBindIR mb = multipleSetBinds.get(i);

			if (mb instanceof ASetMultipleBindIR)
			{
				nextMultiBindBlock = consIterationBlock(nextMultiBindBlock, mb.getPatterns(), ((ASetMultipleBindIR) mb).getSet(), tempGen, strategy, iteVarPrefixes);
			} else if (mb instanceof ASeqMultipleBindIR)
			{
				nextMultiBindBlock = consIterationBlock(nextMultiBindBlock, mb.getPatterns(), ((ASeqMultipleBindIR) mb).getSeq(), tempGen, strategy, iteVarPrefixes);
			} else
			{
				log.error("Expected set multiple bind or sequence multiple bind. Got: "
						+ mb);
			}

			strategy.setFirstBind(false);
		}

		return outerBlock;
	}

	public ACastUnaryExpIR consNextElementCall(STypeIR instanceType,
			String instance, String member, ACompSeqExpIR seqComp)
			throws AnalysisException
	{

		STypeIR elementType = getSeqTypeCloned(seqComp).getSeqOf();

		SExpIR nextCall = consInstanceCall(instanceType, instance, elementType.clone(), member);
		ACastUnaryExpIR cast = new ACastUnaryExpIR();
		cast.setType(elementType.clone());
		cast.setExp(nextCall);

		return cast;
	}

	public Boolean hasEmptySet(SMultipleBindIR binding) throws AnalysisException
	{
		if (binding instanceof ASetMultipleBindIR)
		{
			return isEmptySetSeq(((ASetMultipleBindIR) binding).getSet());
		} else if (binding instanceof ASeqMultipleBindIR)
		{
			return isEmptySetSeq(((ASeqMultipleBindIR) binding).getSeq());
		}

		return false;
	}

	public Boolean isEmptySetSeq(SExpIR set) throws AnalysisException
	{
		if (set.getType() instanceof SSetTypeIR)
		{
			return ((SSetTypeIR) set.getType()).getEmpty();
		} else if (set.getType() instanceof SSeqTypeIR)
		{
			return ((SSeqTypeIR) set.getType()).getEmpty();
		}

		return false;
	}

	public void cleanUpBinding(SMultipleBindIR binding)
	{
		if (binding instanceof ASetMultipleBindIR)
		{
			ASetMultipleBindIR sb = (ASetMultipleBindIR) binding;

			sb.setSet(null);
			sb.getPatterns().clear();
		} else if (binding instanceof ASeqMultipleBindIR)
		{
			ASeqMultipleBindIR sb = (ASeqMultipleBindIR) binding;

			sb.setSeq(null);
			sb.getPatterns().clear();
		} else
		{
			log.error("Expected multiple set bind or multiple sequence bind. Got: "
					+ binding);
		}
	}

	public AFieldDeclIR consField(String access, STypeIR type, String name,
			SExpIR initExp)
	{
		AFieldDeclIR stateField = new AFieldDeclIR();
		stateField.setAccess(access);
		stateField.setType(type);
		stateField.setStatic(true);
		stateField.setFinal(false);
		stateField.setVolatile(false);
		stateField.setName(name);
		stateField.setInitial(initExp);

		return stateField;
	}

	public AApplyExpIR consConditionalCall(AMethodDeclIR node,
			AMethodDeclIR predMethod)
	{
		AIdentifierVarExpIR condVar = new AIdentifierVarExpIR();
		condVar.setType(predMethod.getMethodType().clone());
		condVar.setName(predMethod.getName());
		condVar.setIsLambda(false);
		condVar.setIsLocal(true);

		AApplyExpIR condCall = new AApplyExpIR();
		condCall.setType(new ABoolBasicTypeIR());
		condCall.setRoot(condVar);

		LinkedList<AFormalParamLocalParamIR> params = node.getFormalParams();

		for (AFormalParamLocalParamIR p : params)
		{
			SPatternIR paramPattern = p.getPattern();

			if (!(paramPattern instanceof AIdentifierPatternIR))
			{
				log.error("Expected parameter pattern to be an identifier pattern at this point. Got: "
						+ paramPattern);
				return null;
			}

			AIdentifierPatternIR paramId = (AIdentifierPatternIR) paramPattern;

			AIdentifierVarExpIR paramArg = new AIdentifierVarExpIR();
			paramArg.setType(p.getType().clone());
			paramArg.setIsLocal(true);
			paramArg.setIsLambda(false);
			paramArg.setName(paramId.getName());

			condCall.getArgs().add(paramArg);
		}

		if (Settings.dialect == Dialect.VDM_SL)
		{
			ADefaultClassDeclIR encClass = node.getAncestor(ADefaultClassDeclIR.class);

			if (encClass != null)
			{
				for (AFieldDeclIR f : encClass.getFields())
				{
					if (!f.getFinal())
					{
						// It's the state component
						AIdentifierVarExpIR stateArg = info.getExpAssistant().consIdVar(f.getName(), f.getType().clone());
						condCall.getArgs().add(stateArg);
						break;
					}
				}
			} else
			{
				log.error("Could not find enclosing class of " + node);
			}
		}

		return condCall;
	}

	public AVarDeclIR consClassVarDeclDefaultCtor(String className,
			String varName)
	{
		AClassTypeIR classType = consClassType(className);
		ANewExpIR init = consDefaultConsCall(className);

		AVarDeclIR classDecl = consDecl(varName, classType, init);
		classDecl.setFinal(true);

		return classDecl;
	}

	public ABlockStmIR wrap(AVarDeclIR decl)
	{
		ABlockStmIR block = new ABlockStmIR();
		block.getLocalDefs().add(decl);

		return block;
	}

	public ARecordTypeIR consRecType(String definingModule, String recName)
	{
		ATypeNameIR typeName = new ATypeNameIR();
		typeName.setDefiningClass(definingModule);
		typeName.setName(recName);

		ARecordTypeIR recType = new ARecordTypeIR();
		recType.setName(typeName);

		return recType;
	}

	public ARecordTypeIR getRecType(final AStateDeclIR stateDecl)
	{
		ARecordTypeIR stateType = new ARecordTypeIR();
		stateType.setName(getTypeName(stateDecl));

		return stateType;
	}

	public ATypeNameIR getTypeName(final AStateDeclIR stateDecl)
	{
		ATypeNameIR stateName = new ATypeNameIR();
		stateName.setDefiningClass(getEnclosingModuleName(stateDecl));
		stateName.setName(stateDecl.getName());

		return stateName;
	}

	public String getEnclosingModuleName(AStateDeclIR stateDecl)
	{
		AModuleDeclIR module = stateDecl.getAncestor(AModuleDeclIR.class);

		if (module != null)
		{
			return module.getName();
		} else
		{
			log.error("Could not find enclosing module name of state declaration "
					+ stateDecl.getName());
			return null;
		}
	}

	public ABlockStmIR wrap(SStmIR stm)
	{
		ABlockStmIR block = new ABlockStmIR();
		block.getStatements().add(stm);

		return block;
	}
}
