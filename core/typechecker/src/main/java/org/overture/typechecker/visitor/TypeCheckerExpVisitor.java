/*
 * #%~
 * The VDM Type Checker
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
package org.overture.typechecker.visitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.definitions.ABusClassDefinition;
import org.overture.ast.definitions.ACpuClassDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.*;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.intf.lex.ILexRealToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.patterns.AExpressionPattern;
import org.overture.ast.patterns.AIdentifierPattern;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AIntNumericBasicType;
import org.overture.ast.types.AMapMapType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARealNumericBasicType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASeq1SeqType;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.ATokenBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.ast.types.SSeqType;
import org.overture.ast.util.PTypeSet;
import org.overture.ast.util.Utils;
import org.overture.config.Release;
import org.overture.config.Settings;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;
import org.overture.typechecker.assistant.type.AClassTypeAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;
import org.overture.typechecker.utilities.type.QualifiedDefinition;

public class TypeCheckerExpVisitor extends AbstractTypeCheckVisitor
{

	public TypeCheckerExpVisitor(
			IQuestionAnswer<TypeCheckInfo, PType> typeCheckVisitor)
	{
		super(typeCheckVisitor);
	}

	@Override
	public PType caseAApplyExp(AApplyExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		TypeCheckInfo noConstraint = question.newConstraint(null);
		noConstraint.qualifiers = null;
		node.setArgtypes(new ArrayList<PType>());

		for (PExp a : node.getArgs())
		{
			question.qualifiers = null;
			node.getArgtypes().add(a.apply(THIS, noConstraint));
		}

		node.setType(node.getRoot().apply(THIS, new TypeCheckInfo(question.assistantFactory, question.env, question.scope, node.getArgtypes())));

		if (question.assistantFactory.createPTypeAssistant().isUnknown(node.getType()))
		{
			return node.getType();
		}

		PDefinition func = question.env.getEnclosingDefinition();
		boolean inFunction = question.env.isFunctional();
		boolean inOperation = !inFunction;
		boolean inReserved = (func == null || func.getName() == null) ? false : func.getName().isReserved();

		if (inFunction)
		{
			PDefinition called = getRecursiveDefinition(node, question);

			if (called instanceof AExplicitFunctionDefinition)
			{

				AExplicitFunctionDefinition def = (AExplicitFunctionDefinition) called;

				if (def.getIsCurried())
				{
					// Only recursive if this apply is the last - so our type is not a function.

					if (node.getType() instanceof AFunctionType
							&& ((AFunctionType) node.getType()).getResult() instanceof AFunctionType)
					{
						called = null;
					}
				}

			}

			if (called != null)
			{
				if (func instanceof AExplicitFunctionDefinition)
				{
					AExplicitFunctionDefinition def = (AExplicitFunctionDefinition) func;

					if (called == def)
					{
						node.setRecursive(def);
						def.setRecursive(true);
					}
				} else if (func instanceof AImplicitFunctionDefinition)
				{
					AImplicitFunctionDefinition def = (AImplicitFunctionDefinition) func;

					if (called == def)
					{
						node.setRecursive(def);
						def.setRecursive(true);
					}
				}
			}
		}

		boolean isSimple = !question.assistantFactory.createPTypeAssistant().isUnion(node.getType());
		PTypeSet results = new PTypeSet(question.assistantFactory);

		if (question.assistantFactory.createPTypeAssistant().isFunction(node.getType()))
		{
			AFunctionType ft = question.assistantFactory.createPTypeAssistant().getFunction(node.getType());
			question.assistantFactory.createPTypeAssistant().typeResolve(ft, null, THIS, question);
			results.add(functionApply(node, isSimple, ft, question));
		}

		if (question.assistantFactory.createPTypeAssistant().isOperation(node.getType()))
		{
			if (node.getRoot() instanceof AVariableExp)
			{
				AVariableExp exp = (AVariableExp)node.getRoot();
				PDefinition opdef = question.env.findName(exp.getName(), question.scope);
				AClassTypeAssistantTC assist = question.assistantFactory.createAClassTypeAssistant();

				if (opdef != null && assist.isConstructor(opdef) && !assist.inConstructor(question.env))
				{
					TypeCheckerErrors.report(3337, "Cannot call a constructor from here", node.getLocation(), node);
					results.add(AstFactory.newAUnknownType(node.getLocation()));
				}
			}
			
			AOperationType ot = question.assistantFactory.createPTypeAssistant().getOperation(node.getType());
			question.assistantFactory.createPTypeAssistant().typeResolve(ot, null, THIS, question);

			if (inFunction && Settings.release == Release.VDM_10 && !ot.getPure())
			{
				TypeCheckerErrors.report(3300, "Impure operation '" + node.getRoot()
						+ "' cannot be called from here", node.getLocation(), node);
				results.add(AstFactory.newAUnknownType(node.getLocation()));
			}
			else if (inOperation && Settings.release == Release.VDM_10 && func != null && func.getAccess().getPure() && !ot.getPure())
			{
				TypeCheckerErrors.report(3339, "Cannot call impure operation '" + node.getRoot()
						+ "' from a pure operation", node.getLocation(), node);
				results.add(AstFactory.newAUnknownType(node.getLocation()));
			}
			else
			{
				results.add(operationApply(node, isSimple, ot, question));
			}
			
			if (inFunction && Settings.release == Release.VDM_10 && ot.getPure() && !inReserved)
			{
				TypeCheckerErrors.warning(5017, "Pure operation call may not be referentially transparent", node.getLocation(), node);
			}
		}

		if (question.assistantFactory.createPTypeAssistant().isSeq(node.getType()))
		{
			SSeqType seq = question.assistantFactory.createPTypeAssistant().getSeq(node.getType());
			results.add(sequenceApply(node, isSimple, seq, question));
		}

		if (question.assistantFactory.createPTypeAssistant().isMap(node.getType()))
		{
			SMapType map = question.assistantFactory.createPTypeAssistant().getMap(node.getType());
			results.add(mapApply(node, isSimple, map, question));
		}

		if (results.isEmpty())
		{
			TypeCheckerErrors.report(3054, "Type " + node.getType()
					+ " cannot be applied", node.getLocation(), node);
			return AstFactory.newAUnknownType(node.getLocation());
		}

		node.setType(results.getType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().possibleConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType defaultSBooleanBinaryExp(SBooleanBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		node.setType(binaryCheck(node, AstFactory.newABooleanBasicType(node.getLocation()), THIS, question));

		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAAndBooleanBinaryExp(AAndBooleanBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		List<QualifiedDefinition> qualified = node.getLeft().apply(question.assistantFactory.getQualificationVisitor(), question);

		for (QualifiedDefinition qdef : qualified)
		{
			qdef.qualifyType();
		}

		PType result = defaultSBooleanBinaryExp(node, question);

		for (QualifiedDefinition qdef : qualified)
		{
			qdef.resetType();
		}

		return result;
	}

	@Override
	public PType caseACompBinaryExp(ACompBinaryExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		TypeCheckInfo noConstraint = question.newConstraint(null);
		noConstraint.qualifiers = null;

		node.getLeft().apply(THIS, noConstraint);
		node.getRight().apply(THIS, noConstraint);

		PTypeSet results = new PTypeSet(question.assistantFactory);

		if (question.assistantFactory.createPTypeAssistant().isMap(node.getLeft().getType()))
		{
			if (!question.assistantFactory.createPTypeAssistant().isMap(node.getRight().getType()))
			{
				TypeCheckerErrors.report(3068, "Right hand of map 'comp' is not a map", node.getLocation(), node);
				TypeCheckerErrors.detail("Type", node.getRight().getType());
				node.setType(AstFactory.newAMapMapType(node.getLocation())); // Unknown
																				// types
																				// types
				return node.getType();
			}

			SMapType lm = question.assistantFactory.createPTypeAssistant().getMap(node.getLeft().getType());
			SMapType rm = question.assistantFactory.createPTypeAssistant().getMap(node.getRight().getType());

			if (!question.assistantFactory.getTypeComparator().compatible(lm.getFrom(), rm.getTo()))
			{
				TypeCheckerErrors.report(3069, "Domain of left should equal range of right in map 'comp'", node.getLocation(), node);
				TypeCheckerErrors.detail2("Dom", lm.getFrom(), "Rng", rm.getTo());
			}

			results.add(AstFactory.newAMapMapType(node.getLocation(), rm.getFrom(), lm.getTo()));
		}

		if (question.assistantFactory.createPTypeAssistant().isFunction(node.getLeft().getType()))
		{
			if (!question.assistantFactory.createPTypeAssistant().isFunction(node.getRight().getType()))
			{
				TypeCheckerErrors.report(3070, "Right hand of function 'comp' is not a function", node.getLocation(), node);
				TypeCheckerErrors.detail("Type", node.getRight().getType());
				node.setType(AstFactory.newAUnknownType(node.getLocation()));
				return node.getType();
			} else
			{
				AFunctionType lf = question.assistantFactory.createPTypeAssistant().getFunction(node.getLeft().getType());
				AFunctionType rf = question.assistantFactory.createPTypeAssistant().getFunction(node.getRight().getType());

				if (lf.getParameters().size() != 1)
				{
					TypeCheckerErrors.report(3071, "Left hand function must have a single parameter", node.getLocation(), node);
					TypeCheckerErrors.detail("Type", lf);
				} else if (rf.getParameters().size() != 1)
				{
					TypeCheckerErrors.report(3072, "Right hand function must have a single parameter", node.getLocation(), node);
					TypeCheckerErrors.detail("Type", rf);
				} else if (!question.assistantFactory.getTypeComparator().compatible(lf.getParameters().get(0), rf.getResult()))
				{
					TypeCheckerErrors.report(3073, "Parameter of left should equal result of right in function 'comp'", node.getLocation(), node);
					TypeCheckerErrors.detail2("Parameter", lf.getParameters().get(0), "Result", rf.getResult());
				}

				results.add(AstFactory.newAFunctionType(node.getLocation(), true, rf.getParameters(), lf.getResult()));

			}
		}

		if (results.isEmpty())
		{
			TypeCheckerErrors.report(3074, "Left hand of 'comp' is neither a map nor a function", node.getLocation(), node);
			TypeCheckerErrors.detail("Type", node.getLeft().getType());
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setType(results.getType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseADomainResByBinaryExp(ADomainResByBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		TypeCheckInfo domConstraint = question;

		if (question.constraint != null
				&& question.assistantFactory.createPTypeAssistant().isMap(question.constraint))
		{
			PType stype = question.assistantFactory.createPTypeAssistant().getMap(question.constraint).getFrom();
			domConstraint = question.newConstraint(AstFactory.newASetType(node.getLocation(), stype));
		}

		node.getLeft().apply(THIS, domConstraint);
		node.getRight().apply(THIS, question);

		if (!question.assistantFactory.createPTypeAssistant().isSet(node.getLeft().getType()))
		{
			TypeCheckerErrors.report(3079, "Left of '<-:' is not a set", node.getLocation(), node);
		} else if (!question.assistantFactory.createPTypeAssistant().isMap(node.getRight().getType()))
		{
			TypeCheckerErrors.report(3080, "Right of '<-:' is not a map", node.getLocation(), node);
		} else
		{
			ASetType set = question.assistantFactory.createPTypeAssistant().getSet(node.getLeft().getType());
			SMapType map = question.assistantFactory.createPTypeAssistant().getMap(node.getRight().getType());

			if (!question.assistantFactory.getTypeComparator().compatible(set.getSetof(), map.getFrom()))
			{
				TypeCheckerErrors.report(3081, "Restriction of map should be set of "
						+ map.getFrom(), node.getLocation(), node);
			}
		}

		node.setType(node.getRight().getType());
		return node.getType();
	}

	@Override
	public PType caseADomainResToBinaryExp(ADomainResToBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		TypeCheckInfo domConstraint = question;

		if (question.constraint != null
				&& question.assistantFactory.createPTypeAssistant().isMap(question.constraint))
		{
			PType stype = question.assistantFactory.createPTypeAssistant().getMap(question.constraint).getFrom();
			domConstraint = question.newConstraint(AstFactory.newASetType(node.getLocation(), stype));
		}

		node.getLeft().apply(THIS, domConstraint);
		node.getRight().apply(THIS, question);

		if (!question.assistantFactory.createPTypeAssistant().isSet(node.getLeft().getType()))
		{
			TypeCheckerErrors.report(3082, "Left of '<:' is not a set", node.getLocation(), node);
			TypeCheckerErrors.detail("Actual", node.getLeft().getType());
		} else if (!question.assistantFactory.createPTypeAssistant().isMap(node.getRight().getType()))
		{
			TypeCheckerErrors.report(3083, "Right of '<:' is not a map", node.getLocation(), node);
			TypeCheckerErrors.detail("Actual", node.getRight().getType());
		} else
		{
			ASetType set = question.assistantFactory.createPTypeAssistant().getSet(node.getLeft().getType());
			SMapType map = question.assistantFactory.createPTypeAssistant().getMap(node.getRight().getType());

			if (!question.assistantFactory.getTypeComparator().compatible(set.getSetof(), map.getFrom()))
			{
				TypeCheckerErrors.report(3084, "Restriction of map should be set of "
						+ map.getFrom(), node.getLocation(), node);
			}
		}

		node.setType(node.getRight().getType());
		return node.getType();
	}

	@Override
	public PType caseAEqualsBinaryExp(AEqualsBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		TypeCheckInfo noConstraint = question.newConstraint(null);

		node.getLeft().apply(THIS, noConstraint);
		node.getRight().apply(THIS, noConstraint);

		if (!question.assistantFactory.getTypeComparator().compatible(node.getLeft().getType(), node.getRight().getType()))
		{
			TypeCheckerErrors.report(3087, "Left and right of '=' are incompatible types", node.getLocation(), node);
			TypeCheckerErrors.detail2("Left", node.getLeft().getType(), "Right", node.getRight().getType());
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAInSetBinaryExp(AInSetBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		TypeCheckInfo noConstraint = question.newConstraint(null);

		PType ltype = node.getLeft().apply(THIS, noConstraint);
		PType rtype = node.getRight().apply(THIS, noConstraint);

		if (!question.assistantFactory.createPTypeAssistant().isSet(node.getRight().getType()))
		{
			TypeCheckerErrors.report(3110, "Argument of 'in set' is not a set", node.getLocation(), node);
			TypeCheckerErrors.detail("Actual", rtype);
		} else
		{
			ASetType stype = question.assistantFactory.createPTypeAssistant().getSet(rtype);

			if (!question.assistantFactory.getTypeComparator().compatible(stype.getSetof(), ltype))
			{
				TypeCheckerErrors.report(3319, "'in set' expression is always false", node.getLocation(), node);
				TypeCheckerErrors.detail2("Element", ltype, "Set", stype);
			}
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAMapUnionBinaryExp(AMapUnionBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		node.getLeft().apply(THIS, question);
		node.getRight().apply(THIS, question);

		if (!question.assistantFactory.createPTypeAssistant().isMap(node.getLeft().getType()))
		{
			TypeCheckerErrors.report(3123, "Left hand of 'munion' is not a map", node.getLocation(), node);
			TypeCheckerErrors.detail("Type", node.getLeft().getType());
			node.setType(AstFactory.newAMapMapType(node.getLocation())); // Unknown
																			// types
			return node.getType();
		} else if (!question.assistantFactory.createPTypeAssistant().isMap(node.getRight().getType()))
		{
			TypeCheckerErrors.report(3124, "Right hand of 'munion' is not a map", node.getLocation(), node);
			TypeCheckerErrors.detail("Type", node.getRight().getType());
			node.setType(node.getLeft().getType());
			return node.getType();
		} else
		{
			SMapType ml = question.assistantFactory.createPTypeAssistant().getMap(node.getLeft().getType());
			SMapType mr = question.assistantFactory.createPTypeAssistant().getMap(node.getRight().getType());

			PTypeSet from = new PTypeSet(question.assistantFactory);
			from.add(ml.getFrom());
			from.add(mr.getFrom());
			PTypeSet to = new PTypeSet(question.assistantFactory);
			to.add(ml.getTo());
			to.add(mr.getTo());

			node.setType(AstFactory.newAMapMapType(node.getLocation(), from.getType(node.getLocation()), to.getType(node.getLocation())));
			return node.getType();
		}
	}

	@Override
	public PType caseANotEqualBinaryExp(ANotEqualBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		node.getLeft().apply(THIS, question.newConstraint(null));
		node.getRight().apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.getTypeComparator().compatible(node.getLeft().getType(), node.getRight().getType()))
		{
			TypeCheckerErrors.report(3136, "Left and right of '<>' different types", node.getLocation(), node);
			TypeCheckerErrors.detail2("Left", node.getLeft().getType(), "Right", node.getRight().getType());
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseANotInSetBinaryExp(ANotInSetBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		TypeCheckInfo noConstraint = question.newConstraint(null);

		PType ltype = node.getLeft().apply(THIS, noConstraint);
		PType rtype = node.getRight().apply(THIS, noConstraint);

		if (!question.assistantFactory.createPTypeAssistant().isSet(node.getRight().getType()))
		{
			TypeCheckerErrors.report(3138, "Argument of 'not in set' is not a set", node.getLocation(), node);
			TypeCheckerErrors.detail("Actual", node.getRight().getType());
		} else
		{
			ASetType stype = question.assistantFactory.createPTypeAssistant().getSet(rtype);

			if (!question.assistantFactory.getTypeComparator().compatible(stype.getSetof(), ltype))
			{
				TypeCheckerErrors.report(3320, "'not in set' expression is always true", node.getLocation(), node);
				TypeCheckerErrors.detail2("Element", ltype, "Set", stype);
			}
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseADivNumericBinaryExp(ADivNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		checkNumeric(node, THIS, question.newConstraint(null));
		node.setType(AstFactory.newAIntNumericBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseADivideNumericBinaryExp(ADivideNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		checkNumeric(node, THIS, question.newConstraint(null));
		node.setType(AstFactory.newARealNumericBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAGreaterEqualNumericBinaryExp(
			AGreaterEqualNumericBinaryExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		checkNumeric(node, THIS, question.newConstraint(null));
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAGreaterNumericBinaryExp(AGreaterNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		checkNumeric(node, THIS, question.newConstraint(null));
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAModNumericBinaryExp(AModNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		checkNumeric(node, THIS, question.newConstraint(null));
		node.setType(AstFactory.newAIntNumericBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAPlusNumericBinaryExp(APlusNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		checkNumeric(node, THIS, question.newConstraint(null));

		SNumericBasicType ln = question.assistantFactory.createPTypeAssistant().getNumeric(node.getLeft().getType());
		SNumericBasicType rn = question.assistantFactory.createPTypeAssistant().getNumeric(node.getRight().getType());

		if (ln instanceof ARealNumericBasicType)
		{
			node.setType(ln);
			return ln;
		} else if (rn instanceof ARealNumericBasicType)
		{
			node.setType(rn);
			return rn;
		} else if (ln instanceof AIntNumericBasicType)
		{
			node.setType(ln);
			return ln;
		} else if (rn instanceof AIntNumericBasicType)
		{
			node.setType(rn);
			return rn;
		} else if (ln instanceof ANatNumericBasicType
				&& rn instanceof ANatNumericBasicType)
		{
			node.setType(ln);
			return ln;
		} else
		{
			node.setType(AstFactory.newANatOneNumericBasicType(ln.getLocation()));
			return node.getType();
		}
	}

	@Override
	public PType caseARemNumericBinaryExp(ARemNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		checkNumeric(node, THIS, question.newConstraint(null));
		node.setType(AstFactory.newAIntNumericBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseASubtractNumericBinaryExp(ASubtractNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		checkNumeric(node, THIS, question.newConstraint(null));

		if (node.getLeft().getType() instanceof ARealNumericBasicType
				|| node.getRight().getType() instanceof ARealNumericBasicType)
		{
			node.setType(AstFactory.newARealNumericBasicType(node.getLocation()));
			return node.getType();
		} else
		{
			node.setType(AstFactory.newAIntNumericBasicType(node.getLocation()));
			return node.getType();
		}
	}

	@Override
	public PType caseATimesNumericBinaryExp(ATimesNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		checkNumeric(node, THIS, question.newConstraint(null));

		SNumericBasicType ln = question.assistantFactory.createPTypeAssistant().getNumeric(node.getLeft().getType());
		SNumericBasicType rn = question.assistantFactory.createPTypeAssistant().getNumeric(node.getRight().getType());

		if (ln instanceof ARealNumericBasicType)
		{
			node.setType(ln);
			return ln;
		} else if (rn instanceof ARealNumericBasicType)
		{
			node.setType(rn);
			return rn;
		} else if (ln instanceof AIntNumericBasicType)
		{
			node.setType(ln);
			return ln;
		} else if (rn instanceof AIntNumericBasicType)
		{
			node.setType(rn);
			return rn;
		} else if (ln instanceof ANatNumericBasicType)
		{
			node.setType(ln);
			return ln;
		} else if (rn instanceof ANatNumericBasicType)
		{
			node.setType(rn);
			return rn;
		} else
		{
			node.setType(AstFactory.newANatOneNumericBasicType(ln.getLocation()));
			return node.getType();
		}
	}

	@Override
	public PType caseAPlusPlusBinaryExp(APlusPlusBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		TypeCheckInfo leftcons = question.newConstraint(null);
		TypeCheckInfo mapcons = question.newConstraint(null);
		
		if (question.constraint != null && question.assistantFactory.createPTypeAssistant().isSeq(question.constraint))
		{
			SSeqType st = question.assistantFactory.createPTypeAssistant().getSeq(question.constraint);
			mapcons = question.newConstraint(AstFactory.newAMapMapType(node.getLocation(),
				AstFactory.newANatOneNumericBasicType(node.getLocation()), st.getSeqof()));
			leftcons = question.newConstraint(AstFactory.newASeqSeqType(node.getLocation()));
		}
		else if (question.constraint != null && question.assistantFactory.createPTypeAssistant().isMap(question.constraint))
		{
			SMapType mt = question.assistantFactory.createPTypeAssistant().getMap(question.constraint);
			mapcons = question.newConstraint(mt);
			leftcons = question.newConstraint(AstFactory.newAMapMapType(node.getLocation(),
				mt.getFrom(), AstFactory.newAUnknownType(node.getLocation())));
		}
		
		node.getLeft().apply(THIS, leftcons);
		node.getRight().apply(THIS, mapcons);

		PTypeSet result = new PTypeSet(question.assistantFactory);

		boolean unique = !question.assistantFactory.createPTypeAssistant().isUnion(node.getLeft().getType())
				&& !question.assistantFactory.createPTypeAssistant().isUnion(node.getRight().getType());

		if (question.assistantFactory.createPTypeAssistant().isMap(node.getLeft().getType()))
		{
			if (!question.assistantFactory.createPTypeAssistant().isMap(node.getRight().getType()))
			{
				TypeCheckerErrors.concern(unique, 3141, "Right hand of '++' is not a map", node.getLocation(), node);
				TypeCheckerErrors.detail(unique, "Type", node.getRight().getType());
				node.setType(AstFactory.newAMapMapType(node.getLocation())); // Unknown
																				// types
				return node.getType();
			}

			SMapType lm = question.assistantFactory.createPTypeAssistant().getMap(node.getLeft().getType());
			SMapType rm = question.assistantFactory.createPTypeAssistant().getMap(node.getRight().getType());

			PTypeSet domain = new PTypeSet(question.assistantFactory);
			domain.add(lm.getFrom());
			domain.add(rm.getFrom());
			PTypeSet range = new PTypeSet(question.assistantFactory);
			range.add(lm.getTo());
			range.add(rm.getTo());

			result.add(AstFactory.newAMapMapType(node.getLocation(), domain.getType(node.getLocation()), range.getType(node.getLocation())));
		}

		if (question.assistantFactory.createPTypeAssistant().isSeq(node.getLeft().getType()))
		{
			SSeqType st = question.assistantFactory.createPTypeAssistant().getSeq(node.getLeft().getType());

			if (!question.assistantFactory.createPTypeAssistant().isMap(node.getRight().getType()))
			{
				TypeCheckerErrors.concern(unique, 3142, "Right hand of '++' is not a map", node.getLocation(), node);
				TypeCheckerErrors.detail(unique, "Type", node.getRight().getType());
				result.add(st);
			}
			else
			{
				SMapType mr = question.assistantFactory.createPTypeAssistant().getMap(node.getRight().getType());

				if (!question.assistantFactory.createPTypeAssistant().isType(mr.getFrom(), SNumericBasicType.class))
				{
					TypeCheckerErrors.concern(unique, 3143, "Domain of right hand of '++' must be nat1", node.getLocation(), node);
					TypeCheckerErrors.detail(unique, "Type", mr.getFrom());
				}
				
				PTypeSet type = new PTypeSet(question.assistantFactory);
				type.add(st.getSeqof());
				type.add(mr.getTo());
				result.add(AstFactory.newASeqSeqType(node.getLocation(), type.getType(node.getLocation())));
			}
		}

		if (result.isEmpty())
		{
			TypeCheckerErrors.report(3144, "Left of '++' is neither a map nor a sequence", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setType(result.getType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAProperSubsetBinaryExp(AProperSubsetBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		TypeCheckInfo noConstraint = question.newConstraint(null);

		node.getLeft().apply(THIS, noConstraint);
		node.getRight().apply(THIS, noConstraint);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (question.assistantFactory.createPTypeAssistant().isSet(ltype) &&
			question.assistantFactory.createPTypeAssistant().isSet(rtype) &&
			!question.assistantFactory.getTypeComparator().compatible(ltype, rtype))
		{
			TypeCheckerErrors.report(3335, "Subset will only be true if the LHS set is empty", node.getLocation(), node);
			TypeCheckerErrors.detail("Left", ltype);
			TypeCheckerErrors.detail("Right", rtype);
		}

		if (!question.assistantFactory.createPTypeAssistant().isSet(ltype))
		{
			TypeCheckerErrors.report(3146, "Left hand of " + node.getOp()
					+ " is not a set", node.getLocation(), node);
		}

		if (!question.assistantFactory.createPTypeAssistant().isSet(rtype))
		{
			TypeCheckerErrors.report(3147, "Right hand of " + node.getOp()
					+ " is not a set", node.getLocation(), node);
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseARangeResByBinaryExp(ARangeResByBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		TypeCheckInfo rngConstraint = question;

		if (question.constraint != null
				&& question.assistantFactory.createPTypeAssistant().isMap(question.constraint))
		{
			PType stype = question.assistantFactory.createPTypeAssistant().getMap(question.constraint).getTo();
			rngConstraint = question.newConstraint(AstFactory.newASetType(node.getLocation(), stype));
		}

		node.getLeft().apply(THIS, question);
		node.getRight().apply(THIS, rngConstraint);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!question.assistantFactory.createPTypeAssistant().isMap(ltype))
		{
			TypeCheckerErrors.report(3148, "Left of ':->' is not a map", node.getLocation(), node);
		} else if (!question.assistantFactory.createPTypeAssistant().isSet(rtype))
		{
			TypeCheckerErrors.report(3149, "Right of ':->' is not a set", node.getLocation(), node);
		} else
		{
			SMapType map = question.assistantFactory.createPTypeAssistant().getMap(ltype);
			ASetType set = question.assistantFactory.createPTypeAssistant().getSet(rtype);

			if (!question.assistantFactory.getTypeComparator().compatible(set.getSetof(), map.getTo()))
			{
				TypeCheckerErrors.report(3150, "Restriction of map should be set of "
						+ map.getTo(), node.getLocation(), node);
			}
		}

		node.setType(ltype);
		return ltype;
	}

	@Override
	public PType caseARangeResToBinaryExp(ARangeResToBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		TypeCheckInfo rngConstraint = question;

		if (question.constraint != null
				&& question.assistantFactory.createPTypeAssistant().isMap(question.constraint))
		{
			PType stype = question.assistantFactory.createPTypeAssistant().getMap(question.constraint).getTo();
			rngConstraint = question.newConstraint(AstFactory.newASetType(node.getLocation(), stype));
		}

		node.getLeft().apply(THIS, question);
		node.getRight().apply(THIS, rngConstraint);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!question.assistantFactory.createPTypeAssistant().isMap(ltype))
		{
			TypeCheckerErrors.report(3151, "Left of ':>' is not a map", node.getLocation(), node);
		} else if (!question.assistantFactory.createPTypeAssistant().isSet(rtype))
		{
			TypeCheckerErrors.report(3152, "Right of ':>' is not a set", node.getLocation(), node);
		} else
		{
			SMapType map = question.assistantFactory.createPTypeAssistant().getMap(ltype);
			ASetType set = question.assistantFactory.createPTypeAssistant().getSet(rtype);

			if (!question.assistantFactory.getTypeComparator().compatible(set.getSetof(), map.getTo()))
			{
				TypeCheckerErrors.report(3153, "Restriction of map should be set of "
						+ map.getTo(), node.getLocation(), node);
			}
		}
		node.setType(ltype);
		return ltype;
	}

	@Override
	public PType caseASeqConcatBinaryExp(ASeqConcatBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		node.getLeft().apply(THIS, question);
		node.getRight().apply(THIS, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!question.assistantFactory.createPTypeAssistant().isSeq(ltype))
		{
			TypeCheckerErrors.report(3157, "Left hand of '^' is not a sequence", node.getLocation(), node);
			ltype = AstFactory.newASeqSeqType(node.getLocation(), AstFactory.newAUnknownType(node.getLocation()));
		}

		if (!question.assistantFactory.createPTypeAssistant().isSeq(rtype))
		{
			TypeCheckerErrors.report(3158, "Right hand of '^' is not a sequence", node.getLocation(), node);
			rtype = AstFactory.newASeqSeqType(node.getLocation(), AstFactory.newAUnknownType(node.getLocation()));
		}

		PType lof = question.assistantFactory.createPTypeAssistant().getSeq(ltype);
		PType rof = question.assistantFactory.createPTypeAssistant().getSeq(rtype);
		boolean seq1 = lof instanceof ASeq1SeqType
				|| rof instanceof ASeq1SeqType;

		lof = ((SSeqType) lof).getSeqof();
		rof = ((SSeqType) rof).getSeqof();
		PTypeSet ts = new PTypeSet(question.assistantFactory);
		ts.add(lof);
		ts.add(rof);

		node.setType(seq1 ? AstFactory.newASeq1SeqType(node.getLocation(), ts.getType(node.getLocation()))
				: AstFactory.newASeqSeqType(node.getLocation(), ts.getType(node.getLocation())));
		return node.getType();
	}

	@Override
	public PType caseASetDifferenceBinaryExp(ASetDifferenceBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		TypeCheckInfo noConstraint = question.newConstraint(null);

		node.getLeft().apply(THIS, noConstraint);
		node.getRight().apply(THIS, noConstraint);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!question.assistantFactory.createPTypeAssistant().isSet(ltype))
		{
			TypeCheckerErrors.report(3160, "Left hand of '\\' is not a set", node.getLocation(), node);
		}

		if (!question.assistantFactory.createPTypeAssistant().isSet(rtype))
		{
			TypeCheckerErrors.report(3161, "Right hand of '\\' is not a set", node.getLocation(), node);
		}

		if (!question.assistantFactory.getTypeComparator().compatible(ltype, rtype))
		{
			TypeCheckerErrors.report(3162, "Left and right of '\\' are different types", node.getLocation(), node);
			TypeCheckerErrors.detail2("Left", ltype, "Right", rtype);
		}

		node.setType(ltype);
		return ltype;
	}

	@Override
	public PType caseASetIntersectBinaryExp(ASetIntersectBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		TypeCheckInfo noConstraint = question.newConstraint(null);

		node.getLeft().apply(THIS, noConstraint);
		node.getRight().apply(THIS, noConstraint);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		PType lset = null;
		PType rset = null;

		PTypeAssistantTC assistant = question.assistantFactory.createPTypeAssistant();

		if (!assistant.isSet(ltype))
		{
			TypeCheckerErrors.report(3163, "Left hand of " + node.getLocation()
					+ " is not a set", node.getLocation(), node);
		} else
		{
			lset = assistant.getSet(ltype).getSetof();
		}

		if (!assistant.isSet(rtype))
		{
			TypeCheckerErrors.report(3164, "Right hand of "
					+ node.getLocation() + " is not a set", node.getLocation(), node);
		} else
		{
			rset = assistant.getSet(rtype).getSetof();
		}

		PType result = ltype; // A guess

		if (lset != null && !assistant.isUnknown(lset) && rset != null
				&& !assistant.isUnknown(rset))
		{
			PType interTypes = question.assistantFactory.getTypeComparator().intersect(lset, rset);

			if (interTypes == null)
			{
				TypeCheckerErrors.report(3165, "Left and right of intersect are different types", node.getLocation(), node);
				TypeCheckerErrors.detail2("Left", ltype, "Right", rtype);
			} else
			{
				result = AstFactory.newASetType(node.getLocation(), interTypes);
			}
		}

		node.setType(result);
		return result;
	}

	@Override
	public PType caseASetUnionBinaryExp(ASetUnionBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		node.getLeft().apply(THIS, question);
		node.getRight().apply(THIS, question);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (!question.assistantFactory.createPTypeAssistant().isSet(ltype))
		{
			TypeCheckerErrors.report(3168, "Left hand of " + node.getOp()
					+ " is not a set", node.getLocation(), node);
		}

		if (!question.assistantFactory.createPTypeAssistant().isSet(rtype))
		{
			TypeCheckerErrors.report(3169, "Right hand of " + node.getOp()
					+ " is not a set", node.getLocation(), node);
		}

		PTypeSet result = new PTypeSet(question.assistantFactory);
		result.add(ltype);
		result.add(rtype);
		node.setType(result.getType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAStarStarBinaryExp(AStarStarBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		TypeCheckInfo noConstraint = question.newConstraint(null);

		node.getLeft().apply(THIS, noConstraint);
		node.getRight().apply(THIS, noConstraint);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (question.assistantFactory.createPTypeAssistant().isMap(ltype))
		{
			question.assistantFactory.createPTypeAssistant();
			if (!question.assistantFactory.createPTypeAssistant().isNumeric(rtype))
			{
				// rtype.report(3170,
				// "Map iterator expects nat as right hand arg");
				TypeCheckerErrors.report(3170, "Map iterator expects nat as right hand arg", rtype.getLocation(), rtype);
			}
		} else if (question.assistantFactory.createPTypeAssistant().isFunction(ltype))
		{
			question.assistantFactory.createPTypeAssistant();
			if (!question.assistantFactory.createPTypeAssistant().isNumeric(rtype))
			{
				TypeCheckerErrors.report(3171, "Function iterator expects nat as right hand arg", rtype.getLocation(), rtype);
			}
		} else
		{
			question.assistantFactory.createPTypeAssistant();
			if (question.assistantFactory.createPTypeAssistant().isNumeric(ltype))
			{
				question.assistantFactory.createPTypeAssistant();
				if (!question.assistantFactory.createPTypeAssistant().isNumeric(rtype))
				{
					TypeCheckerErrors.report(3172, "'**' expects number as right hand arg", rtype.getLocation(), rtype);
				}
			} else
			{
				TypeCheckerErrors.report(3173, "First arg of '**' must be a map, function or number", node.getLocation(), node);
				node.setType(AstFactory.newAUnknownType(node.getLocation()));
				return node.getType();
			}
		}

		node.setType(ltype);
		return ltype;
	}

	@Override
	public PType caseASubsetBinaryExp(ASubsetBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		TypeCheckInfo noConstraint = question.newConstraint(null);

		node.getLeft().apply(THIS, noConstraint);
		node.getRight().apply(THIS, noConstraint);

		PType ltype = node.getLeft().getType();
		PType rtype = node.getRight().getType();

		if (question.assistantFactory.createPTypeAssistant().isSet(ltype) &&
			question.assistantFactory.createPTypeAssistant().isSet(rtype) &&
			!question.assistantFactory.getTypeComparator().compatible(ltype, rtype))
		{
			TypeCheckerErrors.report(3335, "Subset will only be true if the LHS set is empty", node.getLocation(), node);
			TypeCheckerErrors.detail("Left", ltype);
			TypeCheckerErrors.detail("Right", rtype);
		}

		if (!question.assistantFactory.createPTypeAssistant().isSet(ltype))
		{
			TypeCheckerErrors.report(3177, "Left hand of " + node.getOp()
					+ " is not a set", node.getLocation(), node);
			TypeCheckerErrors.detail("Type", ltype);
		}

		if (!question.assistantFactory.createPTypeAssistant().isSet(rtype))
		{
			TypeCheckerErrors.report(3178, "Right hand of " + node.getOp()
					+ " is not a set", node.getLocation(), node);
			TypeCheckerErrors.detail("Type", rtype);
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseABooleanConstExp(ABooleanConstExp node,
			TypeCheckInfo question)
	{
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseACasesExp(ACasesExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		TypeCheckInfo noConstraint = question.newConstraint(null);
		question.qualifiers = null;

		PType expType = node.getExpression().apply(THIS, noConstraint);

		PTypeSet rtypes = new PTypeSet(question.assistantFactory);

		for (ACaseAlternative c : node.getCases())
		{
			rtypes.add(typeCheck(c, THIS, question, expType));
		}

		if (node.getOthers() != null)
		{
			rtypes.add(node.getOthers().apply(THIS, question));
		}

		node.setType(rtypes.getType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseACharLiteralExp(ACharLiteralExp node,
			TypeCheckInfo question)
	{
		node.setType(AstFactory.newACharBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAElseIfExp(AElseIfExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		node.setType(typeCheckAElseIf(node, node.getLocation(), node.getElseIf(), node.getThen(), question));
		return node.getType();
	}

	@Override
	public PType caseAExists1Exp(AExists1Exp node, TypeCheckInfo question)
			throws AnalysisException
	{
		node.setDef(AstFactory.newAMultiBindListDefinition(node.getBind().getLocation(), question.assistantFactory.createPBindAssistant().getMultipleBindList(node.getBind())));
		node.getDef().apply(THIS, question.newConstraint(null));
		Environment local = new FlatCheckedEnvironment(question.assistantFactory, node.getDef(), question.env, question.scope);

		if (node.getBind() instanceof ATypeBind)
		{
			ATypeBind tb = (ATypeBind) node.getBind();
			question.assistantFactory.createATypeBindAssistant().typeResolve(tb, THIS, question);
		}

		question.qualifiers = null;
		if (!question.assistantFactory.createPTypeAssistant().isType(node.getPredicate().apply(THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope, null, AstFactory.newABooleanBasicType(node.getLocation()), null)), ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3088, "Predicate is not boolean", node.getPredicate().getLocation(), node.getPredicate());
		}

		local.unusedCheck();
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAExistsExp(AExistsExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), node.getBindList());
		def.apply(THIS, question.newConstraint(null));
		def.setNameScope(NameScope.LOCAL);
		Environment local = new FlatCheckedEnvironment(question.assistantFactory, def, question.env, question.scope);
		question = new TypeCheckInfo(question.assistantFactory, local, question.scope, null, AstFactory.newABooleanBasicType(node.getLocation()), null);
		if (!question.assistantFactory.createPTypeAssistant().isType(node.getPredicate().apply(THIS, question), ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3089, "Predicate is not boolean", node.getPredicate().getLocation(), node.getPredicate());
		}

		local.unusedCheck();
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAFieldExp(AFieldExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PType root = node.getObject().apply(THIS, new TypeCheckInfo(question.assistantFactory, question.env, question.scope));

		if (question.assistantFactory.createPTypeAssistant().isUnknown(root))
		{
			node.setMemberName(new LexNameToken("?", node.getField()));
			node.setType(root);
			return root;
		}

		PTypeSet results = new PTypeSet(question.assistantFactory);
		boolean recOrClass = false;
		boolean unique = !question.assistantFactory.createPTypeAssistant().isUnion(root);

		if (question.assistantFactory.createPTypeAssistant().isRecord(root))
		{
			ARecordInvariantType rec = question.assistantFactory.createPTypeAssistant().getRecord(root);
			AFieldField cf = question.assistantFactory.createARecordInvariantTypeAssistant().findField(rec, node.getField().getName());

			if (cf != null)
			{
				results.add(cf.getType());
			} else
			{
				TypeCheckerErrors.concern(unique, 3090, "Unknown field "
						+ node.getField().getName() + " in record "
						+ rec.getName(), node.getField().getLocation(), node.getField());
			}

			recOrClass = true;
		}

		if (question.env.isVDMPP()
				&& question.assistantFactory.createPTypeAssistant().isClass(root, question.env))
		{
			AClassType cls = question.assistantFactory.createPTypeAssistant().getClassType(root, question.env);
			ILexNameToken memberName = node.getMemberName();

			if (memberName == null)
			{
				memberName = question.assistantFactory.createAClassTypeAssistant().getMemberName(cls, node.getField());
				node.setMemberName(memberName);
			}

			memberName.setTypeQualifier(question.qualifiers);
    		PDefinition encl = question.env.getEnclosingDefinition();
    		NameScope findScope = question.scope;
	
    		if (encl != null && question.assistantFactory.createPDefinitionAssistant().isFunction(encl))
    		{
    			findScope = NameScope.VARSANDNAMES;		// Allow fields as well in functions
    		}

    		PDefinition fdef = question.assistantFactory.createAClassTypeAssistant().findName(cls, memberName, findScope);

			if (fdef == null)
			{
				// The field may be a map or sequence, which would not
				// have the type qualifier of its arguments in the name...

				List<PType> oldq = memberName.getTypeQualifier();
				memberName.setTypeQualifier(null);
				fdef = // cls.apply(question.assistantFactory.getNameFinder(), new NameFinder.Newquestion(memberName,
						// question.scope));

				question.assistantFactory.createAClassTypeAssistant().findName(cls, memberName, question.scope);
				memberName.setTypeQualifier(oldq); // Just for error text!
			}

			if (fdef == null && memberName.getTypeQualifier() == null)
			{
				// We might be selecting a bare function or operation, without
				// applying it (ie. no qualifiers). In this case, if there is
				// precisely one possibility, we choose it.

				for (PDefinition possible : question.env.findMatches(memberName))
				{
					if (question.assistantFactory.createPDefinitionAssistant().isFunctionOrOperation(possible))
					{
						if (fdef != null)
						{
							fdef = null; // Alas, more than one
							break;
						} else
						{
							fdef = possible;
						}
					}
				}
			}

			if (fdef == null)
			{
				TypeCheckerErrors.concern(unique, 3091, "Unknown member "
						+ memberName + " of class " + cls.getName().getName(), node.getField().getLocation(), node.getField());

				if (unique)
				{
					question.env.listAlternatives(memberName);
				}
			} else if (question.assistantFactory.createSClassDefinitionAssistant().isAccessible(question.env, fdef, false))
			{
				// The following gives lots of warnings for self.value access
				// to values as though they are fields of self in the CSK test
				// suite, so commented out for now.

				if (question.assistantFactory.createPDefinitionAssistant().isStatic(fdef))// && !env.isStatic())
				{
					// warning(5005, "Should access member " + field +
					// " from a static context");
				}

				results.add(question.assistantFactory.createPDefinitionAssistant().getType(fdef));
				// At runtime, type qualifiers must match exactly
				memberName.setTypeQualifier(fdef.getName().getTypeQualifier());
			} else
			{
				TypeCheckerErrors.concern(unique, 3092, "Inaccessible member "
						+ memberName + " of class " + cls.getName().getName(), node.getField().getLocation(), node.getField());
			}

			recOrClass = true;
		}

		if (results.isEmpty())
		{
			if (!recOrClass)
			{
				TypeCheckerErrors.report(3093, "Field '"
						+ node.getField().getName()
						+ "' applied to non-aggregate type", node.getObject().getLocation(), node.getObject());
			}

			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		PType resType = results.getType(node.getLocation());
		node.setType(resType);
		return node.getType();
	}

	@Override
	public PType caseAFieldNumberExp(AFieldNumberExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		PExp tuple = node.getTuple();
		question.qualifiers = null;
		PType type = tuple.apply(THIS, question.newConstraint(null));
		node.setType(type);

		if (!question.assistantFactory.createPTypeAssistant().isProduct(type))
		{
			TypeCheckerErrors.report(3094, "Field '#" + node.getField()
					+ "' applied to non-tuple type", tuple.getLocation(), tuple);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		AProductType product = question.assistantFactory.createPTypeAssistant().getProduct(type);
		long fn = node.getField().getValue();

		if (fn > product.getTypes().size() || fn < 1)
		{
			TypeCheckerErrors.report(3095, "Field number does not match tuple size", node.getField().getLocation(), node.getField());
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setType(product.getTypes().get((int) fn - 1));
		return node.getType();
	}

	@Override
	public PType caseAForAllExp(AForAllExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), node.getBindList());
		def.apply(THIS, question.newConstraint(null));
		Environment local = new FlatCheckedEnvironment(question.assistantFactory, def, question.env, question.scope);
		if (!question.assistantFactory.createPTypeAssistant().isType(node.getPredicate().apply(THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope, null, AstFactory.newABooleanBasicType(node.getLocation()), null)), ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3097, "Predicate is not boolean", node.getPredicate().getLocation(), node.getPredicate());
		}

		local.unusedCheck();
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAFuncInstatiationExp(AFuncInstatiationExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		// If there are no type qualifiers passed because the poly function
		// value
		// is being accessed alone (not applied). In this case, the null
		// qualifier
		// will cause VariableExpression to search for anything that matches the
		// name alone. If there is precisely one, it is selected; if there are
		// several, this is an ambiguity error.
		//
		// Note that a poly function is hard to identify from the actual types
		// passed here because the number of parameters may not equal the number
		// of type parameters.

		PType ftype = node.getFunction().apply(THIS, question.newConstraint(null));

		if (question.assistantFactory.createPTypeAssistant().isUnknown(ftype))
		{
			node.setType(ftype);
			return ftype;
		}

		if (question.assistantFactory.createPTypeAssistant().isFunction(ftype))
		{
			AFunctionType t = question.assistantFactory.createPTypeAssistant().getFunction(ftype);
			PTypeSet set = new PTypeSet(question.assistantFactory);

			if (t.getDefinitions().size() == 0)
			{
				TypeCheckerErrors.report(3098, "Function value is not polymorphic", node.getLocation(), node);
				set.add(AstFactory.newAUnknownType(node.getLocation()));
			} else
			{
				boolean serious = t.getDefinitions().size() == 1;

				for (PDefinition def : t.getDefinitions()) // Possibly a union
															// of several
				{
					List<ILexNameToken> typeParams = null;
					def = question.assistantFactory.createPDefinitionAssistant().deref(def);

					if (def instanceof AExplicitFunctionDefinition)
					{
						node.setExpdef((AExplicitFunctionDefinition) def);
						typeParams = node.getExpdef().getTypeParams();
					} else if (def instanceof AImplicitFunctionDefinition)
					{
						node.setImpdef((AImplicitFunctionDefinition) def);
						typeParams = node.getImpdef().getTypeParams();
					} else
					{
						TypeCheckerErrors.report(3099, "Polymorphic function is not in scope", node.getLocation(), node);
						continue;
					}

					if (typeParams.size() == 0)
					{
						TypeCheckerErrors.concern(serious, 3100, "Function has no type parameters", node.getLocation(), node);
						continue;
					}

					if (node.getActualTypes().size() != typeParams.size())
					{
						TypeCheckerErrors.concern(serious, 3101, "Expecting "
								+ typeParams.size() + " type parameters", node.getLocation(), node);
						continue;
					}

					List<PType> fixed = new Vector<PType>();

					for (PType ptype : node.getActualTypes())
					{
						if (ptype instanceof AParameterType) // Recursive
																// polymorphism
						{
							AParameterType pt = (AParameterType) ptype;
							PDefinition d = question.env.findName(pt.getName(), question.scope);

							if (d == null)
							{
								TypeCheckerErrors.report(3102, "Parameter name "
										+ pt + " not defined", node.getLocation(), node);
								ptype = AstFactory.newAUnknownType(node.getLocation());
							} else
							{
								ptype = d.getType();
							}
						}

						ptype = question.assistantFactory.createPTypeAssistant().typeResolve(ptype, null, THIS, question);
						fixed.add(ptype);
						question.assistantFactory.getTypeComparator().checkComposeTypes(ptype, question.env, false);
					}

					node.setActualTypes(fixed);

					node.setType(node.getExpdef() == null ? question.assistantFactory.createAImplicitFunctionDefinitionAssistant().getType(node.getImpdef(), node.getActualTypes())
							: question.assistantFactory.createAExplicitFunctionDefinitionAssistant().getType(node.getExpdef(), node.getActualTypes()));

					// type = expdef == null ?
					// impdef.getType(actualTypes) :
					// expdef.getType(actualTypes);

					set.add(node.getType());
				}
			}

			if (!set.isEmpty())
			{
				node.setType(set.getType(node.getLocation()));
				return node.getType();
			}
		} else
		{
			TypeCheckerErrors.report(3103, "Function instantiation does not yield a function", node.getLocation(), node);
		}

		node.setType(AstFactory.newAUnknownType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAHistoryExp(AHistoryExp node, TypeCheckInfo question)
	{
		SClassDefinition classdef = question.env.findClassDefinition();

		for (ILexNameToken opname : node.getOpnames())
		{
			int found = 0;

			List<PDefinition> allDefs = (List<PDefinition>) classdef.getDefinitions().clone();
			allDefs.addAll(classdef.getAllInheritedDefinitions());

			for (PDefinition def : allDefs)
			{
				if (def.getName() != null && def.getName().matches(opname))
				{
					found++;

					if (!question.assistantFactory.createPDefinitionAssistant().isCallableOperation(def))
					{
						TypeCheckerErrors.report(3105, opname
								+ " is not an explicit operation", opname.getLocation(), opname);
					}

					if (def.getAccess().getPure())
    				{
						TypeCheckerErrors.report(3342, "Cannot use history counters for pure operations", opname.getLocation(), opname);
    				}
    				
    				if (def.getAccess().getStatic() == null && question.env.isStatic())
    				{
    					TypeCheckerErrors.report(3349,
    						"Cannot see non-static operation from static context", opname.getLocation(), opname);
    				}
				}
			}

			if (found == 0)
			{
				TypeCheckerErrors.report(3106, opname + " is not in scope", opname.getLocation(), opname);
			} else if (found > 1)
			{
				TypeCheckerErrors.warning(5004, "History expression of overloaded operation", opname.getLocation(), opname);
			}

			if (opname.getName().equals(classdef.getName().getName()))
			{
				TypeCheckerErrors.report(3107, "Cannot use history of a constructor", opname.getLocation(), opname);
			}
		}

		node.setType(AstFactory.newANatNumericBasicType(node.getLocation()));

		return node.getType();
	}

	@Override
	public PType caseAIfExp(AIfExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		node.setType(typeCheckIf(node.getLocation(), node.getTest(), node.getThen(), node.getElseList(), node.getElse(), question));// rtypes.getType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAIntLiteralExp(AIntLiteralExp node, TypeCheckInfo question)
	{
		if (node.getValue().getValue() < 0)
		{
			node.setType(AstFactory.newAIntNumericBasicType(node.getLocation()));
		} else if (node.getValue().getValue() == 0)
		{
			node.setType(AstFactory.newANatNumericBasicType(node.getLocation()));
		} else
		{
			node.setType(AstFactory.newANatOneNumericBasicType(node.getLocation()));
		}

		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAIotaExp(AIotaExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), question.assistantFactory.createPBindAssistant().getMultipleBindList(node.getBind()));

		def.apply(THIS, question.newConstraint(null));

		PType rt = null;
		PBind bind = node.getBind();

		if (bind instanceof ASetBind)
		{
			ASetBind sb = (ASetBind) bind;
			question.qualifiers = null;
			rt = sb.getSet().apply(THIS, question.newConstraint(null));

			if (question.assistantFactory.createPTypeAssistant().isSet(rt))
			{
				rt = question.assistantFactory.createPTypeAssistant().getSet(rt).getSetof();
			} else
			{
				TypeCheckerErrors.report(3112, "Iota set bind is not a set", node.getLocation(), node);
			}
		} else
		{
			ATypeBind tb = (ATypeBind) bind;
			question.assistantFactory.createATypeBindAssistant().typeResolve(tb, THIS, question);
			rt = tb.getType();
		}

		Environment local = new FlatCheckedEnvironment(question.assistantFactory, def, question.env, question.scope);
		question.qualifiers = null;
		if (!question.assistantFactory.createPTypeAssistant().isType(node.getPredicate().apply(THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope, null, AstFactory.newABooleanBasicType(node.getLocation()), null)), ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3088, "Predicate is not boolean", node.getPredicate().getLocation(), node.getPredicate());
		}

		local.unusedCheck();
		node.setType(rt);
		return rt;
	}

	@Override
	public PType caseAIsExp(AIsExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		question.qualifiers = null;
		node.getTest().apply(THIS, question.newConstraint(null));

		PType basictype = node.getBasicType();

		if (basictype != null)
		{
			basictype = question.assistantFactory.createPTypeAssistant().typeResolve(basictype, null, THIS, question);
			question.assistantFactory.getTypeComparator().checkComposeTypes(basictype, question.env, false);
		}

		ILexNameToken typename = node.getTypeName();

		if (typename != null)
		{
			PDefinition typeFound = question.env.findType(typename, node.getLocation().getModule());
			if (typeFound == null)
			{
				TypeCheckerErrors.report(3113, "Unknown type name '" + typename
						+ "'", node.getLocation(), node);
				node.setType(node.getTest().getType());
				return node.getType();
			}
			node.setTypedef(typeFound.clone());

		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAIsOfBaseClassExp(AIsOfBaseClassExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		if (question.env.findType(node.getBaseClass(), null) == null)
		{
			TypeCheckerErrors.report(3114, "Undefined base class type: "
					+ node.getBaseClass().getName(), node.getLocation(), node);
		}

		question.qualifiers = null;
		PType rt = node.getExp().apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isClass(rt, question.env))
		{
			TypeCheckerErrors.report(3266, "Argument is not an object", node.getExp().getLocation(), node.getExp());
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAIsOfClassExp(AIsOfClassExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		ILexNameToken classname = node.getClassName();
		PDefinition cls = question.env.findType(classname, null);

		if (cls == null || !(cls instanceof SClassDefinition))
		{
			TypeCheckerErrors.report(3115, "Undefined class type: "
					+ classname.getName(), node.getLocation(), node);
		} else
		{
			node.setClassType((AClassType) cls.getType());
		}

		question.qualifiers = null;
		PType rt = node.getExp().apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isClass(rt, question.env))
		{
			TypeCheckerErrors.report(3266, "Argument is not an object", node.getExp().getLocation(), node.getExp());
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseALambdaExp(ALambdaExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		List<PMultipleBind> mbinds = new Vector<PMultipleBind>();
		List<PType> ptypes = new Vector<PType>();

		List<PPattern> paramPatterns = new Vector<PPattern>();
		List<PDefinition> paramDefinitions = new Vector<PDefinition>();

		// node.setParamPatterns(paramPatterns);
		for (ATypeBind tb : node.getBindList())
		{
			// mbinds.addAll(ATypeBindAssistantTC.getMultipleBindList(tb));
			// FIXME: I am not sure if this is the way.
			mbinds.addAll(tb.apply(question.assistantFactory.getMultipleBindLister()));
			paramDefinitions.addAll(question.assistantFactory.createPPatternAssistant().getDefinitions(tb.getPattern(), tb.getType(), NameScope.LOCAL));
			paramPatterns.add(tb.getPattern());
			ptypes.add(question.assistantFactory.createPTypeAssistant().typeResolve(tb.getType(), null, THIS, question));
		}

		node.setParamPatterns(paramPatterns);

		question.assistantFactory.createPDefinitionListAssistant().implicitDefinitions(paramDefinitions, question.env);
		question.assistantFactory.createPDefinitionListAssistant().typeCheck(paramDefinitions, THIS, question);

		node.setParamDefinitions(paramDefinitions);

		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), mbinds);
		def.apply(THIS, question.newConstraint(null));
		Environment local = new FlatCheckedEnvironment(question.assistantFactory, def, question.env, question.scope);
		local.setFunctional(true);
		local.setEnclosingDefinition(def); // Prevent recursive checks
		TypeCheckInfo newInfo = new TypeCheckInfo(question.assistantFactory, local, question.scope);

		PType result = node.getExpression().apply(THIS, newInfo);
		local.unusedCheck();

		node.setType(AstFactory.newAFunctionType(node.getLocation(), true, ptypes, result));
		return node.getType();
	}

	@Override
	public PType caseALetBeStExp(ALetBeStExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		Entry<PType, AMultiBindListDefinition> res = typecheckLetBeSt(node, node.getLocation(), node.getBind(), node.getSuchThat(), node.getValue(), question);
		node.setDef(res.getValue());
		node.setType(res.getKey());
		return node.getType();
	}

	@Override
	public PType caseALetDefExp(ALetDefExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		node.setType(typeCheckLet(node, node.getLocalDefs(), node.getExpression(), question));
		return node.getType();
	}

	@Override
	public PType caseADefExp(ADefExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		// Each local definition is in scope for later local definitions...
		Environment local = question.env;

		for (PDefinition d : node.getLocalDefs())
		{
			if (d instanceof AExplicitFunctionDefinition)
			{
				// Functions' names are in scope in their bodies, whereas
				// simple variable declarations aren't

				local = new FlatCheckedEnvironment(question.assistantFactory, d, local, question.scope); // cumulative
				question.assistantFactory.createPDefinitionAssistant().implicitDefinitions(d, local);
				TypeCheckInfo newQuestion = new TypeCheckInfo(question.assistantFactory, local, question.scope);

				question.assistantFactory.createPDefinitionAssistant().typeResolve(d, THIS, question);

				if (question.env.isVDMPP())
				{
					SClassDefinition cdef = question.env.findClassDefinition();
					d.setClassDefinition(cdef);
					d.setAccess(question.assistantFactory.createPAccessSpecifierAssistant().getStatic(d, true));
				}

				d.apply(THIS, newQuestion);
			} else
			{
				question.assistantFactory.createPDefinitionAssistant().implicitDefinitions(d, local);
				question.assistantFactory.createPDefinitionAssistant().typeResolve(d, THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope, question.qualifiers));
				d.apply(THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope, question.qualifiers));
				local = new FlatCheckedEnvironment(question.assistantFactory, d, local, question.scope); // cumulative
			}
		}

		PType r = node.getExpression().apply(THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope, null, question.constraint, null));
		local.unusedCheck(question.env);
		node.setType(r);
		return r;
	}

	@Override
	public PType caseAMapCompMapExp(AMapCompMapExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), node.getBindings());
		def.apply(THIS, question.newConstraint(null));
		Environment local = new FlatCheckedEnvironment(question.assistantFactory, def, question.env, question.scope);

		PExp predicate = node.getPredicate();
		TypeCheckInfo pquestion = new TypeCheckInfo(question.assistantFactory, local, question.scope, null, AstFactory.newABooleanBasicType(node.getLocation()), null);

		if (predicate != null
				&& !question.assistantFactory.createPTypeAssistant().isType(predicate.apply(THIS, pquestion), ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3118, "Predicate is not boolean", predicate.getLocation(), predicate);
		}

		node.setType(node.getFirst().apply(THIS, question.newInfo(local)));
		local.unusedCheck();
		return node.getType();
	}

	@Override
	public PType caseAMapEnumMapExp(AMapEnumMapExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		node.setDomTypes(new Vector<PType>());
		node.setRngTypes(new Vector<PType>());

		if (node.getMembers().isEmpty())
		{
			node.setType(AstFactory.newAMapMapType(node.getLocation()));
			return node.getType();
		}

		PTypeSet dom = new PTypeSet(question.assistantFactory);
		PTypeSet rng = new PTypeSet(question.assistantFactory);

		for (AMapletExp ex : node.getMembers())
		{
			PType mt = ex.apply(THIS, question);

			if (!question.assistantFactory.createPTypeAssistant().isMap(mt))
			{
				TypeCheckerErrors.report(3121, "Element is not of maplet type", node.getLocation(), node);
			} else
			{
				SMapType maplet = question.assistantFactory.createPTypeAssistant().getMap(mt);
				dom.add(maplet.getFrom());
				node.getDomTypes().add(maplet.getFrom());
				rng.add(maplet.getTo());
				node.getRngTypes().add(maplet.getTo());
			}
		}
		node.setType(AstFactory.newAMapMapType(node.getLocation(), dom.getType(node.getLocation()), rng.getType(node.getLocation())));
		return node.getType();
	}

	@Override
	public PType caseAMapletExp(AMapletExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		TypeCheckInfo domConstraint = question;
		TypeCheckInfo rngConstraint = question;

		if (question.constraint != null
				&& question.assistantFactory.createPTypeAssistant().isMap(question.constraint))
		{
			PType dtype = question.assistantFactory.createPTypeAssistant().getMap(question.constraint).getFrom();
			domConstraint = question.newConstraint(dtype);
			PType rtype = question.assistantFactory.createPTypeAssistant().getMap(question.constraint).getTo();
			rngConstraint = question.newConstraint(rtype);
		}

		PType ltype = node.getLeft().apply(THIS, domConstraint);
		PType rtype = node.getRight().apply(THIS, rngConstraint);
		node.setType(AstFactory.newAMapMapType(node.getLocation(), ltype, rtype));
		return node.getType();
	}

	@Override
	public PType caseAMkBasicExp(AMkBasicExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PType argtype = node.getArg().apply(THIS, question.newConstraint(null));

		if (!(node.getType() instanceof ATokenBasicType)
				&& !question.assistantFactory.createPTypeAssistant().equals(argtype, node.getType()))
		{
			TypeCheckerErrors.report(3125, "Argument of mk_" + node.getType()
					+ " is the wrong type", node.getLocation(), node);
		}

		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAMkTypeExp(AMkTypeExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PDefinition typeDef = question.env.findType(node.getTypeName(), node.getLocation().getModule());

		if (typeDef == null)
		{
			TypeCheckerErrors.report(3126, "Unknown type '"
					+ node.getTypeName() + "' in constructor", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		PType rec = null;
		if (typeDef instanceof ATypeDefinition)
		{
			rec = ((ATypeDefinition) typeDef).getType();
		} else if (typeDef instanceof AStateDefinition)
		{
			rec = ((AStateDefinition) typeDef).getRecordType();
		} else
		{
			rec = question.assistantFactory.createPDefinitionAssistant().getType(typeDef);
		}

		if (!(rec instanceof ARecordInvariantType))
		{
			TypeCheckerErrors.report(3127, "Type '" + node.getTypeName()
					+ "' is not a record type", node.getLocation(), node);
			node.setType(rec);
			return rec;
		}

		node.setRecordType((ARecordInvariantType) rec);

		if (node.getRecordType().getOpaque())
		{
			TypeCheckerErrors.report(3127, "Type '" + node.getTypeName()
					+ "' is not a record type", node.getLocation(), node);
			node.setType(rec);
			return rec;
		}

		if (node.getTypeName().getExplicit())
		{
			// If the type name is explicit, the Type ought to have an explicit
			// name. This only really affects trace expansion.

			ARecordInvariantType recordType = node.getRecordType();

			AExplicitFunctionDefinition inv = recordType.getInvDef();

			recordType = AstFactory.newARecordInvariantType(recordType.getName().getExplicit(true), recordType.getFields());
			recordType.setInvDef(inv);
			node.setRecordType(recordType);
		}

		if (node.getRecordType().getFields().size() != node.getArgs().size())
		{
			TypeCheckerErrors.report(3128, "Record and constructor do not have same number of fields", node.getLocation(), node);
			node.setType(rec);
			return rec;
		}

		int i = 0;
		Iterator<AFieldField> fiter = node.getRecordType().getFields().iterator();
		node.setArgTypes(new LinkedList<PType>());
		List<PType> argTypes = node.getArgTypes();

		for (PExp arg : node.getArgs())
		{
			PType fieldType = fiter.next().getType();
			PType argType = arg.apply(THIS, question.newConstraint(fieldType));
			i++;

			if (!question.assistantFactory.getTypeComparator().compatible(fieldType, argType))
			{
				TypeCheckerErrors.report(3129, "Constructor field " + i
						+ " is of wrong type", node.getLocation(), node);
				TypeCheckerErrors.detail2("Expected", fieldType, "Actual", argType);
			}

			argTypes.add(argType);
		}

		node.setType(node.getRecordType().clone());
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getRecordType(), node.getLocation());
	}

	@Override
	public PType caseAMuExp(AMuExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PType rtype = node.getRecord().apply(THIS, question.newConstraint(null));

		if (question.assistantFactory.createPTypeAssistant().isUnknown(rtype))
		{
			node.setType(rtype);
			return rtype;
		}

		if (question.assistantFactory.createPTypeAssistant().isRecord(rtype))
		{
			node.setRecordType(question.assistantFactory.createPTypeAssistant().getRecord(rtype));
			node.setModTypes(new LinkedList<PType>());

			List<PType> modTypes = node.getModTypes();

			for (ARecordModifier rm : node.getModifiers())
			{
				PType mtype = rm.getValue().apply(THIS, question.newConstraint(null));
				modTypes.add(mtype);
				AFieldField f = question.assistantFactory.createARecordInvariantTypeAssistant().findField(node.getRecordType(), rm.getTag().getName());

				if (f != null)
				{
					if (!question.assistantFactory.getTypeComparator().compatible(f.getType(), mtype))
					{
						TypeCheckerErrors.report(3130, "Modifier for "
								+ f.getTag() + " should be " + f.getType(), node.getLocation(), node);
						TypeCheckerErrors.detail("Actual", mtype);
					}
				} else
				{
					TypeCheckerErrors.report(3131, "Modifier tag "
							+ rm.getTag() + " not found in record", node.getLocation(), node);
				}
			}
		} else
		{
			TypeCheckerErrors.report(3132, "mu operation on non-record type", node.getLocation(), node);
		}

		node.setType(rtype);
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, rtype, node.getLocation());
	}

	@Override
	public PType caseANarrowExp(ANarrowExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		node.getTest().setType(node.getTest().apply(THIS, question.newConstraint(null)));

		PType result = null;

		if (node.getBasicType() != null)
		{

			node.setBasicType(question.assistantFactory.createPTypeAssistant().typeResolve(node.getBasicType(), null, THIS, question));
			result = node.getBasicType();
			question.assistantFactory.getTypeComparator().checkComposeTypes(result, question.env, false);
		} else
		{
			node.setTypedef(question.env.findType(node.getTypeName(), node.getLocation().getModule()));

			if (node.getTypedef() == null)
			{
				TypeCheckerErrors.report(3113, "Unknown type name '"
						+ node.getTypeName() + "'", node.getLocation(), node);
				result = AstFactory.newAUnknownType(node.getLocation());
			} else
			{
				result = question.assistantFactory.createPDefinitionAssistant().getType(node.getTypedef());
			}

		}

		if (!question.assistantFactory.getTypeComparator().compatible(result, node.getTest().getType()))
		{
			TypeCheckerErrors.report(3317, "Expression can never match narrow type", node.getLocation(), node);
		}

		return question.assistantFactory.createPTypeAssistant().possibleConstraint(question.constraint, result, node.getLocation());
	}

	@Override
	public PType caseANewExp(ANewExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PDefinition cdef = question.env.findType(node.getClassName().getClassName(), null);

		if (cdef == null || !(cdef instanceof SClassDefinition))
		{
			TypeCheckerErrors.report(3133, "Class name " + node.getClassName()
					+ " not in scope", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		if (Settings.release == Release.VDM_10 && question.env.isFunctional())
		{
			TypeCheckerErrors.report(3348, "Cannot use 'new' in a functional context", node.getLocation(), node);
		}

		node.setClassdef((SClassDefinition) cdef);

		SClassDefinition classdef = node.getClassdef();

		if (classdef instanceof ASystemClassDefinition)
		{
			TypeCheckerErrors.report(3279, "Cannot instantiate system class "
					+ classdef.getName(), node.getLocation(), node);
		}

		if (classdef.getIsAbstract())
		{
			TypeCheckerErrors.report(3330, "Cannot instantiate abstract class "
					+ classdef.getName(), node.getLocation(), node);

			PDefinitionAssistantTC assistant = question.assistantFactory.createPDefinitionAssistant();
			List<PDefinition> localDefs = new LinkedList<PDefinition>();
			localDefs.addAll(classdef.getDefinitions());
			localDefs.addAll(classdef.getLocalInheritedDefinitions());

			for (PDefinition d : localDefs)
			{
				if (assistant.isSubclassResponsibility(d))
				{
					TypeCheckerErrors.detail("Unimplemented", d.getName().getName()
							+ d.getType());
				}
			}
		}

		List<PType> argtypes = new LinkedList<PType>();

		for (PExp a : node.getArgs())
		{
			argtypes.add(a.apply(THIS, question.newConstraint(null)));
		}

		PDefinition opdef = question.assistantFactory.createSClassDefinitionAssistant().findConstructor(classdef, argtypes);

		if (opdef == null)
		{
			if (!node.getArgs().isEmpty()) // Not having a default ctor is OK
			{
				TypeCheckerErrors.report(3134, "Class has no constructor with these parameter types", node.getLocation(), node);
				question.assistantFactory.createSClassDefinitionAssistant();
				TypeCheckerErrors.detail("Called", SClassDefinitionAssistantTC.getCtorName(classdef, argtypes));
			} else if (classdef instanceof ACpuClassDefinition
					|| classdef instanceof ABusClassDefinition)
			{
				TypeCheckerErrors.report(3297, "Cannot use default constructor for this class", node.getLocation(), node);
			}
		} else
		{
			if (!question.assistantFactory.createPDefinitionAssistant().isCallableOperation(opdef))
			{
				TypeCheckerErrors.report(3135, "Class has no constructor with these parameter types", node.getLocation(), node);
				question.assistantFactory.createSClassDefinitionAssistant();
				TypeCheckerErrors.detail("Called", SClassDefinitionAssistantTC.getCtorName(classdef, argtypes));
			} else if (!question.assistantFactory.createSClassDefinitionAssistant().isAccessible(question.env, opdef, false))
			{
				TypeCheckerErrors.report(3292, "Constructor is not accessible", node.getLocation(), node);
				question.assistantFactory.createSClassDefinitionAssistant();
				TypeCheckerErrors.detail("Called", SClassDefinitionAssistantTC.getCtorName(classdef, argtypes));
			} else
			{
				node.setCtorDefinition(opdef);
			}
		}

		PType type = question.assistantFactory.createPDefinitionAssistant().getType(classdef);
		node.setType(type);
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, type, node.getLocation());
	}

	@Override
	public PType caseANilExp(ANilExp node, TypeCheckInfo question)
	{
		node.setType(AstFactory.newAOptionalType(node.getLocation(), AstFactory.newAUnknownType(node.getLocation())));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseANotYetSpecifiedExp(ANotYetSpecifiedExp node,
			TypeCheckInfo question)
	{
		node.setType(typeCheckANotYetSpecifiedExp(node, node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAPostOpExp(APostOpExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		node.setType(node.getPostexpression().apply(THIS, question.newConstraint(null)));
		return node.getType();
	}

	@Override
	public PType caseAPreExp(APreExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		node.getFunction().apply(THIS, question.newConstraint(null));

		for (PExp a : node.getArgs())
		{
			question.qualifiers = null;
			a.apply(THIS, question.newConstraint(null));
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAPreOpExp(APreOpExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		node.setType(node.getExpression().apply(THIS, question.newConstraint(null)));
		return node.getType();
	}

	@Override
	public PType caseAQuoteLiteralExp(AQuoteLiteralExp node,
			TypeCheckInfo question)
	{
		node.setType(AstFactory.newAQuoteType(node.getValue().clone()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseARealLiteralExp(ARealLiteralExp node,
			TypeCheckInfo question)
	{
		ILexRealToken value = node.getValue();

		if (Math.round(value.getValue()) == value.getValue())
		{
			if (value.getValue() < 0)
			{
				node.setType(AstFactory.newAIntNumericBasicType(node.getLocation()));
			} else if (value.getValue() == 0)
			{
				node.setType(AstFactory.newANatNumericBasicType(node.getLocation()));
			} else
			{
				node.setType(AstFactory.newANatOneNumericBasicType(node.getLocation()));
			}
		} else
		{
			node.setType(AstFactory.newARealNumericBasicType(node.getLocation()));
		}

		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseASameBaseClassExp(ASameBaseClassExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		PExp left = node.getLeft();
		PExp right = node.getRight();

		question.qualifiers = null;
		PType lt = left.apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isClass(lt, question.env))
		{
			TypeCheckerErrors.report(3266, "Argument is not an object", left.getLocation(), left);
		}

		question.qualifiers = null;
		PType rt = right.apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isClass(rt, question.env))
		{
			TypeCheckerErrors.report(3266, "Argument is not an object", right.getLocation(), right);
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseASameClassExp(ASameClassExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PExp left = node.getLeft();
		PExp right = node.getRight();

		question.qualifiers = null;
		PType lt = left.apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isClass(lt, question.env))
		{
			TypeCheckerErrors.report(3266, "Argument is not an object", left.getLocation(), left);
		}

		question.qualifiers = null;
		PType rt = right.apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isClass(rt, question.env))
		{
			TypeCheckerErrors.report(3266, "Argument is not an object", right.getLocation(), right);
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseASelfExp(ASelfExp node, TypeCheckInfo question)
	{
		PDefinition cdef = question.env.findName(node.getName(), question.scope);

		if (cdef == null)
		{
			TypeCheckerErrors.report(3154, node.getName() + " not in scope", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setType(cdef.getType());
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseASeqCompSeqExp(ASeqCompSeqExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		// TODO: check if this is still needed?!
		// save these so we can clone them after they have been type checked
		// PExp setBindSet = node.getSetBind().getSet();
		// PPattern setBindPattern = node.getSetBind().getPattern();
		//
		// List<PPattern> plist = new ArrayList<PPattern>();
		// plist.add(setBindPattern);
		// List<PMultipleBind> mblist = new Vector<PMultipleBind>();
		// mblist.add(new ASetMultipleBind(plist.get(0).getLocation(), plist,
		// setBindSet));

		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getLocation(), question.assistantFactory.createPBindAssistant().getMultipleBindList(node.getSetBind()));
		def.apply(THIS, question.newConstraint(null));

		// now they are typechecked, add them again
		// node.getSetBind().setSet(setBindSet.clone());
		// node.getSetBind().setPattern(setBindPattern.clone());

		if (question.assistantFactory.createPPatternAssistant().getVariableNames(node.getSetBind().getPattern()).size() != 1
				|| !question.assistantFactory.createPTypeAssistant().isNumeric(question.assistantFactory.createPDefinitionAssistant().getType(def)))
		{
			TypeCheckerErrors.report(3155, "List comprehension must define one numeric bind variable", node.getLocation(), node);
		}

		Environment local = new FlatCheckedEnvironment(question.assistantFactory, def, question.env, question.scope);
		PType etype = node.getFirst().apply(THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope, question.qualifiers));

		PExp predicate = node.getPredicate();

		if (predicate != null)
		{
			TypeCheckInfo pquestion = new TypeCheckInfo(question.assistantFactory, local, question.scope, null, AstFactory.newABooleanBasicType(node.getLocation()), null);

			question.qualifiers = null;
			if (!question.assistantFactory.createPTypeAssistant().isType(predicate.apply(THIS, pquestion), ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3156, "Predicate is not boolean", predicate.getLocation(), predicate);
			}
		}

		local.unusedCheck();
		node.setType(AstFactory.newASeqSeqType(node.getLocation(), etype));
		return node.getType();
	}

	@Override
	public PType caseASeqEnumSeqExp(ASeqEnumSeqExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PTypeSet ts = new PTypeSet(question.assistantFactory);
		node.setTypes(new LinkedList<PType>());
		List<PType> types = node.getTypes();
		TypeCheckInfo elemConstraint = question;

		if (question.constraint != null
				&& question.assistantFactory.createPTypeAssistant().isSeq(question.constraint))
		{
			PType stype = question.assistantFactory.createPTypeAssistant().getSeq(question.constraint).getSeqof();
			elemConstraint = question.newConstraint(stype);
		}

		for (PExp ex : node.getMembers())
		{
			question.qualifiers = null;
			PType mt = ex.apply(THIS, elemConstraint);
			ts.add(mt);
			types.add(mt);
		}

		node.setType(ts.isEmpty() ? AstFactory.newASeqSeqType(node.getLocation())
				: AstFactory.newASeq1SeqType(node.getLocation(), ts.getType(node.getLocation())));

		return node.getType();
	}

	@Override
	public PType caseASetCompSetExp(ASetCompSetExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PDefinition def = AstFactory.newAMultiBindListDefinition(node.getFirst().getLocation(), node.getBindings());
		def.apply(THIS, question.newConstraint(null));

		Environment local = new FlatCheckedEnvironment(question.assistantFactory, def, question.env, question.scope);
		question = new TypeCheckInfo(question.assistantFactory, local, question.scope);

		PType etype = node.getFirst().apply(THIS, question.newConstraint(null));
		PExp predicate = node.getPredicate();

		if (predicate != null)
		{
			TypeCheckInfo pquestion = new TypeCheckInfo(question.assistantFactory, local, question.scope, null, AstFactory.newABooleanBasicType(node.getLocation()), null);

			if (!question.assistantFactory.createPTypeAssistant().isType(predicate.apply(THIS, pquestion), ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3159, "Predicate is not boolean", predicate.getLocation(), predicate);
			}
		}

		local.unusedCheck();
		ASetType setType = AstFactory.newASetType(node.getLocation(), etype);
		node.setType(setType);
		node.setSetType(setType);
		return setType;
	}

	@Override
	public PType caseASetEnumSetExp(ASetEnumSetExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PTypeSet ts = new PTypeSet(question.assistantFactory);
		node.setTypes(new LinkedList<PType>());
		List<PType> types = node.getTypes();
		TypeCheckInfo elemConstraint = question;

		if (question.constraint != null
				&& question.assistantFactory.createPTypeAssistant().isSet(question.constraint))
		{
			PType setType = question.assistantFactory.createPTypeAssistant().getSet(question.constraint).getSetof();
			elemConstraint = question.newConstraint(setType);
		}

		for (PExp ex : node.getMembers())
		{
			question.qualifiers = null;
			PType mt = ex.apply(THIS, elemConstraint);
			ts.add(mt);
			types.add(mt);
		}

		node.setType(ts.isEmpty() ? AstFactory.newASetType(node.getLocation())
				: AstFactory.newASetType(node.getLocation(), ts.getType(node.getLocation())));

		return node.getType();
	}

	@Override
	public PType caseASetRangeSetExp(ASetRangeSetExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		PExp first = node.getFirst();
		PExp last = node.getLast();

		question.qualifiers = null;
		node.setFtype(first.apply(THIS, question.newConstraint(null)));
		question.qualifiers = null;
		node.setLtype(last.apply(THIS, question.newConstraint(null)));

		PType ftype = node.getFtype();
		PType ltype = node.getLtype();

		if (!question.assistantFactory.createPTypeAssistant().isNumeric(ftype))
		{
			TypeCheckerErrors.report(3166, "Set range type must be an number", ftype.getLocation(), ftype);
			ftype = AstFactory.newAIntNumericBasicType(node.getLocation());
		}
		
		SNumericBasicType ntype = question.assistantFactory.createPTypeAssistant().getNumeric(ftype);
		
		if (question.assistantFactory.createSNumericBasicTypeAssistant().getWeight(ntype) > 1)
		{
			ftype = AstFactory.newAIntNumericBasicType(node.getLocation());	// Caused by ceiling/floor
		}

		if (!question.assistantFactory.createPTypeAssistant().isNumeric(ltype))
		{
			TypeCheckerErrors.report(3167, "Set range type must be an number", ltype.getLocation(), ltype);
		}

		node.setType(AstFactory.newASetType(first.getLocation(), ftype));
		return question.assistantFactory.createPTypeAssistant().possibleConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAStateInitExp(AStateInitExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		TypeCheckInfo noConstraint = question.newConstraint(null);
		PPattern pattern = node.getState().getInitPattern();
		PExp exp = node.getState().getInitExpression();
		boolean canBeExecuted = false;

		if (pattern instanceof AIdentifierPattern
				&& exp instanceof AEqualsBinaryExp)
		{
			AEqualsBinaryExp ee = (AEqualsBinaryExp) exp;
			question.qualifiers = null;
			ee.getLeft().apply(THIS, noConstraint);

			if (ee.getLeft() instanceof AVariableExp)
			{
				question.qualifiers = null;
				PType rhs = ee.getRight().apply(THIS, noConstraint);

				if (question.assistantFactory.createPTypeAssistant().isTag(rhs))
				{
					ARecordInvariantType rt = question.assistantFactory.createPTypeAssistant().getRecord(rhs);
					canBeExecuted = rt.getName().getName().equals(node.getState().getName().getName());
				}
			}
		} else
		{
			question.qualifiers = null;
			exp.apply(THIS, noConstraint);
		}

		if (!canBeExecuted)
		{
			TypeCheckerErrors.warning(5010, "State init expression cannot be executed", node.getLocation(), node);
			TypeCheckerErrors.detail("Expected", "p == p = mk_"
					+ node.getState().getName().getName() + "(...)");
		}

		node.getState().setCanBeExecuted(canBeExecuted);
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAStringLiteralExp(AStringLiteralExp node,
			TypeCheckInfo question)
	{
		if (node.getValue().getValue().isEmpty())
		{
			ASeqSeqType tt = AstFactory.newASeqSeqType(node.getLocation(), AstFactory.newACharBasicType(node.getLocation()));
			node.setType(tt);
		} else
		{
			node.setType(AstFactory.newASeq1SeqType(node.getLocation(), AstFactory.newACharBasicType(node.getLocation())));
		}

		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseASubclassResponsibilityExp(
			ASubclassResponsibilityExp node, TypeCheckInfo question)
	{
		node.setType(AstFactory.newAUnknownType(node.getLocation()));
		return node.getType(); // Because we terminate anyway
	}

	@Override
	public PType caseASubseqExp(ASubseqExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		TypeCheckInfo noConstraint = question.newConstraint(null);
		question.qualifiers = null;
		PType stype = node.getSeq().apply(THIS, noConstraint);
		question.qualifiers = null;
		node.setFtype(node.getFrom().apply(THIS, noConstraint));
		PType ftype = node.getFtype();
		question.qualifiers = null;
		node.setTtype(node.getTo().apply(THIS, noConstraint));
		PType ttype = node.getTtype();

		if (!question.assistantFactory.createPTypeAssistant().isSeq(stype))
		{
			TypeCheckerErrors.report(3174, "Subsequence is not of a sequence type", node.getLocation(), node);
		}

		question.assistantFactory.createPTypeAssistant();
		if (!question.assistantFactory.createPTypeAssistant().isNumeric(ftype))
		{
			TypeCheckerErrors.report(3175, "Subsequence range start is not a number", node.getLocation(), node);
		}

		question.assistantFactory.createPTypeAssistant();
		if (!question.assistantFactory.createPTypeAssistant().isNumeric(ttype))
		{
			TypeCheckerErrors.report(3176, "Subsequence range end is not a number", node.getLocation(), node);
		}
		node.setType(stype);
		return stype;
	}

	@Override
	public PType caseAThreadIdExp(AThreadIdExp node, TypeCheckInfo question)
	{
		PDefinition encl = question.env.getEnclosingDefinition();
		
		if (encl != null && encl.getAccess().getPure())
		{
			TypeCheckerErrors.report(3346, "Cannot use 'threadid' in pure operations", node.getLocation(), node);
		}

		if (Settings.release == Release.VDM_10 && question.env.isFunctional())
		{
			TypeCheckerErrors.report(3348, "Cannot use 'threadid' in a functional context", node.getLocation(), node);
		}

		node.setType(AstFactory.newANatNumericBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseATimeExp(ATimeExp node, TypeCheckInfo question)
	{
		PDefinition encl = question.env.getEnclosingDefinition();
		
		if (encl != null && encl.getAccess().getPure())
		{
			TypeCheckerErrors.report(3346, "Cannot use 'time' in pure operations", node.getLocation(), node);
		}

		if (Settings.release == Release.VDM_10 && question.env.isFunctional())
		{
			TypeCheckerErrors.report(3348, "Cannot use 'time' in a functional context", node.getLocation(), node);
		}
		
		node.setType(AstFactory.newANatNumericBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseATupleExp(ATupleExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		node.setTypes(new LinkedList<PType>());
		List<PType> types = node.getTypes();
		List<PType> elemConstraints = null;

		if (question.constraint != null
				&& question.assistantFactory.createPTypeAssistant().isProduct(question.constraint))
		{
			elemConstraints = question.assistantFactory.createPTypeAssistant().getProduct(question.constraint).getTypes();

			if (elemConstraints.size() != node.getArgs().size())
			{
				elemConstraints = null;
			}
		}

		int i = 0;

		for (PExp arg : node.getArgs())
		{
			question.qualifiers = null;

			if (elemConstraints == null)
			{
				types.add(arg.apply(THIS, question.newConstraint(null)));
			} else
			{
				types.add(arg.apply(THIS, question.newConstraint(elemConstraints.get(i++))));
			}
		}

		node.setType(AstFactory.newAProductType(node.getLocation(), types));
		return question.assistantFactory.createPTypeAssistant().possibleConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAUndefinedExp(AUndefinedExp node, TypeCheckInfo question)
	{
		node.setType(AstFactory.newAUnknownType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAVariableExp(AVariableExp node, TypeCheckInfo question)
	{
		Environment env = question.env;
		ILexNameToken name = node.getName();

		if (env.isVDMPP())
		{

			name.setTypeQualifier(question.qualifiers);
			node.setVardef(env.findName(name, question.scope));
			PDefinition vardef = node.getVardef();

			if (vardef != null)
			{
				if (vardef.getClassDefinition() != null)
				{
					SClassDefinition sd = vardef.getClassDefinition();
					if (sd != null && node.getName().getModule().equals(""))
					{
						node.setName(name.getModifiedName(sd.getName().getName()));
					}

					if (!question.assistantFactory.createSClassDefinitionAssistant().isAccessible(env, vardef, true))
					{
						TypeCheckerErrors.report(3180, "Inaccessible member "
								+ name
								+ " of class "
								+ vardef.getClassDefinition().getName().getName(), node.getLocation(), node);
						node.setType(AstFactory.newAUnknownType(node.getLocation()));
						return node.getType();
					} else if (!question.assistantFactory.createPAccessSpecifierAssistant().isStatic(vardef.getAccess())
							&& env.isStatic())
					{
						TypeCheckerErrors.report(3181, "Cannot access " + name
								+ " from a static context", node.getLocation(), node);
						node.setType(AstFactory.newAUnknownType(node.getLocation()));
						return node.getType();
					}
					//AKM: a little test
					// if(vardef.getClassDefinition().getName().getName().startsWith("$actionClass"))
					// node.setName(name.getModifiedName(vardef.getClassDefinition().getName().getName()));
				}
			} else if (question.qualifiers != null)
			{
				// It may be an apply of a map or sequence, which would not
				// have the type qualifier of its arguments in the name. Or
				// it might be an apply of a function via a function variable
				// which would not be qualified.

				name.setTypeQualifier(null);
				vardef = env.findName(name, question.scope);

				if (vardef == null)
				{
					name.setTypeQualifier(question.qualifiers); // Just for
																// error text!
				} else
				{
					node.setVardef(vardef);
				}

			} else
			{
				// We may be looking for a bare function/op "x", when in fact
				// there is one with a qualified name "x(args)". So we check
				// the possible matches - if there is precisely one, we pick it,
				// else we raise an ambiguity error.

				for (PDefinition possible : env.findMatches(name))
				{
					if (question.assistantFactory.createPDefinitionAssistant().isFunctionOrOperation(possible))
					{
						if (vardef != null)
						{
							TypeCheckerErrors.report(3269, "Ambiguous function/operation name: "
									+ name.getName(), node.getLocation(), node);
							env.listAlternatives(name);
							break;
						}

						vardef = possible;
						node.setVardef(vardef);
						// Set the qualifier so that it will find it at runtime.

						PType pt = possible.getType();

						if (pt instanceof AFunctionType)
						{
							AFunctionType ft = (AFunctionType) pt;
							name.setTypeQualifier(ft.getParameters());
						} else
						{
							AOperationType ot = (AOperationType) pt;
							name.setTypeQualifier(ot.getParameters());
						}
					}
				}
			}
		} else
		{
			PDefinition temp = env.findName(name, question.scope);
			node.setVardef(temp == null ? null : temp);
		}

		if (node.getVardef() == null)
		{
			TypeCheckerErrors.report(3182, "Name '" + name
					+ "' is not in scope", node.getLocation(), node);
			env.listAlternatives(name);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		} else
		{
			// Note that we perform an extra typeResolve here. This is
			// how forward referenced types are resolved, and is the reason
			// we don't need to retry at the top level (assuming all names
			// are in the environment).
			node.setType(question.assistantFactory.createPTypeAssistant().typeResolve(question.assistantFactory.createPDefinitionAssistant().getType(node.getVardef()), null, THIS, question));

			// If a constraint is passed in, we can raise an error if it is
			// not possible for the type to match the constraint (rather than
			// certain, as checkConstraint would).

			return question.assistantFactory.createPTypeAssistant().possibleConstraint(question.constraint, node.getType(), node.getLocation());
		}
	}

	/**
	 * BINARY Expressions
	 * 
	 * @throws AnalysisException
	 */
	@Override
	public PType caseALessEqualNumericBinaryExp(
			ALessEqualNumericBinaryExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		checkNumeric(node, THIS, question);
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseALessNumericBinaryExp(ALessNumericBinaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		checkNumeric(node, THIS, question);
		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	/**
	 * UNARY Expressions
	 * 
	 * @throws AnalysisException
	 */
	@Override
	public PType caseAAbsoluteUnaryExp(AAbsoluteUnaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		question.qualifiers = null;
		TypeCheckInfo absConstraint = question.newConstraint(null);

		if (question.constraint != null
				&& question.assistantFactory.createPTypeAssistant().isNumeric(question.constraint))
		{
			if (question.constraint instanceof AIntNumericBasicType
					|| question.constraint instanceof ANatOneNumericBasicType)
			{
				absConstraint = question.newConstraint(AstFactory.newAIntNumericBasicType(node.getLocation()));
			} else
			{
				absConstraint = question;
			}
		}

		PType t = node.getExp().apply(THIS, absConstraint);

		if (!question.assistantFactory.createPTypeAssistant().isNumeric(t))
		{
			TypeCheckerErrors.report(3053, "Argument of 'abs' is not numeric", node.getLocation(), node);
		} else if (t instanceof AIntNumericBasicType)
		{
			t = AstFactory.newANatNumericBasicType(t.getLocation());
		}

		node.setType(t);
		return t;
	}

	@Override
	public PType caseACardinalityUnaryExp(ACardinalityUnaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;

		if (!question.assistantFactory.createPTypeAssistant().isSet(exp.apply(THIS, question.newConstraint(null))))
		{
			TypeCheckerErrors.report(3067, "Argument of 'card' is not a set", exp.getLocation(), exp);
		}

		node.setType(AstFactory.newANatNumericBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().possibleConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseADistConcatUnaryExp(ADistConcatUnaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;
		TypeCheckInfo expConstraint = question;

		if (question.constraint != null)
		{
			PType stype = AstFactory.newASeqSeqType(node.getLocation(), question.constraint);
			expConstraint = question.newConstraint(stype);
		}

		PType result = exp.apply(THIS, expConstraint);

		if (question.assistantFactory.createPTypeAssistant().isSeq(result))
		{
			PType inner = question.assistantFactory.createPTypeAssistant().getSeq(result).getSeqof();

			if (question.assistantFactory.createPTypeAssistant().isSeq(inner))
			{
				node.setType(question.assistantFactory.createPTypeAssistant().getSeq(inner));
				return node.getType();
			}
		}

		TypeCheckerErrors.report(3075, "Argument of 'conc' is not a seq of seq", node.getLocation(), node);
		node.setType(AstFactory.newAUnknownType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseADistIntersectUnaryExp(ADistIntersectUnaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;
		PType arg = exp.apply(THIS, question.newConstraint(null));

		if (question.assistantFactory.createPTypeAssistant().isSet(arg))
		{
			ASetType set = question.assistantFactory.createPTypeAssistant().getSet(arg);

			if (set.getEmpty()
					|| question.assistantFactory.createPTypeAssistant().isSet(set.getSetof()))
			{
				node.setType(set.getSetof());
				return set.getSetof();
			}
		}

		TypeCheckerErrors.report(3076, "Argument of 'dinter' is not a set of sets", node.getLocation(), node);
		node.setType(AstFactory.newAUnknownType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseADistMergeUnaryExp(ADistMergeUnaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;
		TypeCheckInfo expConstraint = question;

		if (question.constraint != null)
		{
			PType stype = AstFactory.newASetType(node.getLocation(), question.constraint);
			expConstraint = question.newConstraint(stype);
		}

		PType arg = exp.apply(THIS, expConstraint);

		if (question.assistantFactory.createPTypeAssistant().isSet(arg))
		{
			ASetType set = question.assistantFactory.createPTypeAssistant().getSet(arg);

			if (!set.getEmpty()
					&& question.assistantFactory.createPTypeAssistant().isMap(set.getSetof()))
			{
				node.setType(set.getSetof());
				return set.getSetof();
			}
		}

		TypeCheckerErrors.report(3077, "Merge argument is not a set of maps", node.getLocation(), node);
		return AstFactory.newAMapMapType(node.getLocation()); // Unknown types
	}

	@Override
	public PType caseADistUnionUnaryExp(ADistUnionUnaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;
		TypeCheckInfo expConstraint = question;

		if (question.constraint != null)
		{
			PType stype = AstFactory.newASetType(node.getLocation(), question.constraint);
			expConstraint = question.newConstraint(stype);
		}

		PType type = exp.apply(THIS, expConstraint);

		if (question.assistantFactory.createPTypeAssistant().isSet(type))
		{
			ASetType set = question.assistantFactory.createPTypeAssistant().getSet(type);

			if (question.assistantFactory.createPTypeAssistant().isSet(set.getSetof()))
			{
				node.setType(set.getSetof());
				return set.getSetof();
			}
		}

		TypeCheckerErrors.report(3078, "dunion argument is not a set of sets", node.getLocation(), node);
		node.setType(AstFactory.newASetType(node.getLocation(), AstFactory.newAUnknownType(node.getLocation())));
		return node.getType();
	}

	@Override
	public PType caseAFloorUnaryExp(AFloorUnaryExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;

		if (!question.assistantFactory.createPTypeAssistant().isNumeric(exp.apply(THIS, question.newConstraint(null))))

		{
			TypeCheckerErrors.report(3096, "Argument to floor is not numeric", node.getLocation(), node);
		}

		node.setType(AstFactory.newAIntNumericBasicType(node.getLocation()));
		return node.getType();
	}

	@Override
	public PType caseAHeadUnaryExp(AHeadUnaryExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isSeq(etype))
		{
			TypeCheckerErrors.report(3104, "Argument to 'hd' is not a sequence", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setType(question.assistantFactory.createPTypeAssistant().getSeq(etype).getSeqof());
		return node.getType();
	}

	@Override
	public PType caseAIndicesUnaryExp(AIndicesUnaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isSeq(etype))
		{
			TypeCheckerErrors.report(3109, "Argument to 'inds' is not a sequence", node.getLocation(), node);
			TypeCheckerErrors.detail("Actual type", etype);
		}

		node.setType(AstFactory.newASetType(node.getLocation(), AstFactory.newANatOneNumericBasicType(node.getLocation())));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseALenUnaryExp(ALenUnaryExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isSeq(etype))
		{
			TypeCheckerErrors.report(3116, "Argument to 'len' is not a sequence", node.getLocation(), node);
		}

		node.setType(AstFactory.newANatNumericBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().possibleConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAMapDomainUnaryExp(AMapDomainUnaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isMap(etype))
		{
			TypeCheckerErrors.report(3120, "Argument to 'dom' is not a map", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		SMapType mt = question.assistantFactory.createPTypeAssistant().getMap(etype);
		node.setType(AstFactory.newASetType(node.getLocation(), mt.getFrom()));
		return node.getType();
	}

	@Override
	public PType caseAMapInverseUnaryExp(AMapInverseUnaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isMap(etype))
		{
			TypeCheckerErrors.report(3111, "Argument to 'inverse' is not a map", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setMapType(question.assistantFactory.createPTypeAssistant().getMap(etype));
		AMapMapType mm = AstFactory.newAMapMapType(node.getLocation(), node.getMapType().getTo(), node.getMapType().getFrom());
		node.setType(mm);

		return node.getType();
	}

	@Override
	public PType caseAMapRangeUnaryExp(AMapRangeUnaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isMap(etype))
		{
			TypeCheckerErrors.report(3122, "Argument to 'rng' is not a map", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		SMapType mt = question.assistantFactory.createPTypeAssistant().getMap(etype);
		node.setType(AstFactory.newASetType(node.getLocation(), mt.getTo()));
		return node.getType();
	}

	@Override
	public PType caseANotUnaryExp(ANotUnaryExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;

		PType t = exp.apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isType(t, ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3137, "Not expression is not a boolean", node.getLocation(), node);
		}

		node.setType(AstFactory.newABooleanBasicType(node.getLocation()));
		return question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, node.getType(), node.getLocation());
	}

	@Override
	public PType caseAPowerSetUnaryExp(APowerSetUnaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;
		TypeCheckInfo argConstraint = question.newConstraint(null);

		if (question.constraint != null
				&& question.assistantFactory.createPTypeAssistant().isSet(question.constraint))
		{
			PType stype = question.assistantFactory.createPTypeAssistant().getSet(question.constraint).getSetof();
			argConstraint = question.newConstraint(stype);
		}

		PType etype = exp.apply(THIS, argConstraint);

		if (!question.assistantFactory.createPTypeAssistant().isSet(etype))
		{
			TypeCheckerErrors.report(3145, "Argument to 'power' is not a set", node.getLocation(), node);
			node.setType(AstFactory.newAUnknownType(node.getLocation()));
			return node.getType();
		}

		node.setType(AstFactory.newASetType(node.getLocation(), etype));
		return node.getType();
	}

	@Override
	public PType caseAReverseUnaryExp(AReverseUnaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(THIS, question);

		if (!question.assistantFactory.createPTypeAssistant().isSeq(etype))
		{
			TypeCheckerErrors.report(3295, "Argument to 'reverse' is not a sequence", node.getLocation(), node);
			ASeqSeqType tt = AstFactory.newASeqSeqType(node.getLocation(), AstFactory.newAUnknownType(node.getLocation()));
			node.setType(tt);
			return node.getType();
		}

		node.setType(etype);
		return etype;
	}

	@Override
	public PType caseATailUnaryExp(ATailUnaryExp node, TypeCheckInfo question)
			throws AnalysisException
	{
		PExp exp = node.getExp();
		question.qualifiers = null;

		PType etype = exp.apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isSeq(etype))
		{
			TypeCheckerErrors.report(3179, "Argument to 'tl' is not a sequence", node.getLocation(), node);
			node.setType(AstFactory.newASeqSeqType(node.getLocation(), AstFactory.newAUnknownType(node.getLocation())));
			return node.getType();
		}

		node.setType(etype);
		return etype;
	}

	@Override
	public PType caseAUnaryMinusUnaryExp(AUnaryMinusUnaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		question.qualifiers = null;
		PType t = node.getExp().apply(THIS, question);

		if (t instanceof ANatNumericBasicType
				|| t instanceof ANatOneNumericBasicType)
		{
			t = AstFactory.newAIntNumericBasicType(node.getLocation());
			question.assistantFactory.createPTypeAssistant().checkConstraint(question.constraint, t, node.getLocation());
		}

		node.setType(t);
		return t;
	}

	@Override
	public PType caseAUnaryPlusUnaryExp(AUnaryPlusUnaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		question.qualifiers = null;
		node.setType(node.getExp().apply(THIS, question));
		return node.getType();
	}

	@Override
	public PType caseAElementsUnaryExp(AElementsUnaryExp node,
			TypeCheckInfo question) throws AnalysisException
	{
		PExp etype = node.getExp();
		question.qualifiers = null;
		TypeCheckInfo argConstraint = question;

		if (question.constraint != null
				&& question.assistantFactory.createPTypeAssistant().isSet(question.constraint))
		{
			PType stype = question.assistantFactory.createPTypeAssistant().getSet(question.constraint).getSetof();
			stype = AstFactory.newASeqSeqType(node.getLocation(), stype);
			argConstraint = question.newConstraint(stype);
		}

		PType arg = etype.apply(THIS, argConstraint);

		if (!question.assistantFactory.createPTypeAssistant().isSeq(arg))
		{
			TypeCheckerErrors.report(3085, "Argument of 'elems' is not a sequence", node.getLocation(), node);
			node.setType(AstFactory.newASetType(node.getLocation(), AstFactory.newAUnknownType(node.getLocation())));
			return node.getType();
		}

		SSeqType seq = question.assistantFactory.createPTypeAssistant().getSeq(arg);
		node.setType(seq.getEmpty() ? AstFactory.newASetType(node.getLocation())
				: AstFactory.newASetType(node.getLocation(), seq.getSeqof()));
		return node.getType();
	}

	private void checkNumeric(SNumericBinaryExp node,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{
		node.getLeft().apply(rootVisitor, question.newConstraint(null));
		node.getRight().apply(rootVisitor, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isNumeric(node.getLeft().getType()))
		{
			TypeCheckerErrors.report(3139, "Left hand of " + node.getOp()
					+ " is not numeric", node.getLocation(), node);
			TypeCheckerErrors.detail("Actual", node.getLeft().getType());
			node.getLeft().setType(AstFactory.newARealNumericBasicType(node.getLocation()));
		}

		if (!question.assistantFactory.createPTypeAssistant().isNumeric(node.getRight().getType()))
		{
			TypeCheckerErrors.report(3140, "Right hand of " + node.getOp()
					+ " is not numeric", node.getLocation(), node);
			TypeCheckerErrors.detail("Actual", node.getRight().getType());
			node.getRight().setType(AstFactory.newARealNumericBasicType(node.getLocation()));
		}

	}
	
	public PType functionApply(AApplyExp node, boolean isSimple,
			AFunctionType ft,TypeCheckInfo question)
	{
		List<PType> ptypes = ft.getParameters();

		if (node.getArgs().size() > ptypes.size())
		{
			TypeCheckerErrors.concern(isSimple, 3059, "Too many arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(isSimple, "Args", node.getArgs(), "Params", ptypes);
			return ft.getResult();
		} else if (node.getArgs().size() < ptypes.size())
		{
			TypeCheckerErrors.concern(isSimple, 3060, "Too few arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(isSimple, "Args", node.getArgs(), "Params", ptypes);
			return ft.getResult();
		}

		int i = 0;

		for (PType at : node.getArgtypes())
		{
			PType pt = ptypes.get(i++);

			if (!question.assistantFactory.getTypeComparator().compatible(pt, at))
			{
				// TypeCheckerErrors.concern(isSimple, 3061, "Inappropriate type for argument " + i +
				// ". (Expected: "+pt+" Actual: "+at+")",node.getLocation(),node);
				TypeCheckerErrors.concern(isSimple, 3061, "Inappropriate type for argument "
						+ i, node.getLocation(), node);
				TypeCheckerErrors.detail2(isSimple, "Expect", pt, "Actual", at);
			}
		}

		return ft.getResult();
	}
	
	public PType operationApply(AApplyExp node, boolean isSimple,
			AOperationType ot, TypeCheckInfo question)
	{
		List<PType> ptypes = ot.getParameters();

		if (node.getArgs().size() > ptypes.size())
		{
			TypeCheckerErrors.concern(isSimple, 3062, "Too many arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(isSimple, "Args", node.getArgs(), "Params", ptypes);
			return ot.getResult();
		} else if (node.getArgs().size() < ptypes.size())
		{
			TypeCheckerErrors.concern(isSimple, 3063, "Too few arguments", node.getLocation(), node);
			TypeCheckerErrors.detail2(isSimple, "Args", node.getArgs(), "Params", ptypes);
			return ot.getResult();
		}

		int i = 0;

		for (PType at : node.getArgtypes())
		{
			PType pt = ptypes.get(i++);

			if (!question.assistantFactory.getTypeComparator().compatible(pt, at))
			{
				// TypeCheckerErrors.concern(isSimple, 3064, "Inappropriate type for argument " + i
				// +". (Expected: "+pt+" Actual: "+at+")",node.getLocation(),node);
				TypeCheckerErrors.concern(isSimple, 3064, "Inappropriate type for argument "
						+ i, node.getLocation(), node);
				TypeCheckerErrors.detail2(isSimple, "Expect", pt, "Actual", at);
			}
		}

		return ot.getResult();
	}

	public PType sequenceApply(AApplyExp node, boolean isSimple, SSeqType seq, TypeCheckInfo question)
	{
		if (node.getArgs().size() != 1)
		{
			TypeCheckerErrors.concern(isSimple, 3055, "Sequence selector must have one argument", node.getLocation(), node);
		} else if (!question.assistantFactory.createPTypeAssistant().isNumeric(node.getArgtypes().get(0)))
		{
			TypeCheckerErrors.concern(isSimple, 3056, "Sequence application argument must be numeric", node.getLocation(), node);
		} else if (seq.getEmpty())
		{
			TypeCheckerErrors.concern(isSimple, 3268, "Empty sequence cannot be applied", node.getLocation(), node);
		}

		return seq.getSeqof();
	}

	public PType mapApply(AApplyExp node, boolean isSimple, SMapType map, TypeCheckInfo question)
	{
		if (map.getEmpty())
		{
			TypeCheckerErrors.concern(isSimple, 3267, "Empty map cannot be applied", node.getLocation(), node);
		}

		if (node.getArgs().size() != 1)
		{
			TypeCheckerErrors.concern(isSimple, 3057, "Map application must have one argument", node.getLocation(), node);
		}
		else
		{
			PType argtype = node.getArgtypes().get(0);
    
    		if (!question.assistantFactory.getTypeComparator().compatible(map.getFrom(), argtype))
    		{
    			TypeCheckerErrors.concern(isSimple, 3058, "Map application argument is incompatible type", node.getLocation(), node);
    			TypeCheckerErrors.detail2(isSimple, "Map domain", map.getFrom(), "Argument", argtype);
    		}
		}

		return map.getTo();
	}

	public PDefinition getRecursiveDefinition(AApplyExp node,
			TypeCheckInfo question)
	{
		ILexNameToken fname = null;
		PExp root = node.getRoot();

		if (root instanceof AApplyExp)
		{
			AApplyExp aexp = (AApplyExp) root;
			return getRecursiveDefinition(aexp, question);
		} else if (root instanceof AVariableExp)
		{
			AVariableExp var = (AVariableExp) root;
			fname = var.getName();
		} else if (root instanceof AFuncInstatiationExp)
		{
			AFuncInstatiationExp fie = (AFuncInstatiationExp) root;

			if (fie.getExpdef() != null)
			{
				fname = fie.getExpdef().getName();
			} else if (fie.getImpdef() != null)
			{
				fname = fie.getImpdef().getName();
			}
		}

		if (fname != null)
		{
			return question.env.findName(fname, question.scope);
		} else
		{
			return null;
		}
	}

	/**
	 * Create a measure application string from this apply, turning the root function name into the measure name passed,
	 * and collapsing curried argument sets into one.
	 * 
	 * @param node
	 * @param measure
	 * @param close
	 * @return
	 */
	public String getMeasureApply(AApplyExp node, ILexNameToken measure,
			boolean close)
	{
		String start = null;
		PExp root = node.getRoot();

		if (root instanceof AApplyExp)
		{
			AApplyExp aexp = (AApplyExp) root;
			start = getMeasureApply(aexp, measure, false);
		} else if (root instanceof AVariableExp)
		{
			start = measure.getFullName() + "(";
		} else if (root instanceof AFuncInstatiationExp)
		{
			AFuncInstatiationExp fie = (AFuncInstatiationExp) root;
			start = measure.getFullName() + "["
					+ Utils.listToString(fie.getActualTypes()) + "](";
		} else
		{
			start = root.toString() + "(";
		}

		return start + Utils.listToString(node.getArgs())
				+ (close ? ")" : ", ");
	}
	
	public PType typeCheck(ACaseAlternative c,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question, PType expType) throws AnalysisException
	{

		if (c.getDefs().size() == 0)
		{
			// c.setDefs(new ArrayList<PDefinition>());
			question.assistantFactory.createPPatternAssistant().typeResolve(c.getPattern(), rootVisitor, new TypeCheckInfo(question.assistantFactory, question.env));

			if (c.getPattern() instanceof AExpressionPattern)
			{
				// Only expression patterns need type checking...
				AExpressionPattern ep = (AExpressionPattern) c.getPattern();
				PType ptype = ep.getExp().apply(rootVisitor, new TypeCheckInfo(question.assistantFactory, question.env, question.scope));

				if (!question.assistantFactory.getTypeComparator().compatible(ptype, expType))
				{
					TypeCheckerErrors.report(3311, "Pattern cannot match", c.getPattern().getLocation(), c.getPattern());
				}
			}

			try
			{
				question.assistantFactory.createPPatternAssistant().typeResolve(c.getPattern(), rootVisitor, new TypeCheckInfo(question.assistantFactory, question.env));
				c.getDefs().addAll(question.assistantFactory.createPPatternAssistant().getDefinitions(c.getPattern(), expType, NameScope.LOCAL));
			}
			catch (TypeCheckException e)
			{
				c.getDefs().clear();
				throw e;
			}
		}

		question.assistantFactory.createPPatternAssistant().typeCheck(c.getPattern(), question, rootVisitor);
		question.assistantFactory.createPDefinitionListAssistant().typeCheck(c.getDefs(), rootVisitor, new TypeCheckInfo(question.assistantFactory, question.env, question.scope));

		if (!question.assistantFactory.createPPatternAssistant().matches(c.getPattern(), expType))
		{
			TypeCheckerErrors.report(3311, "Pattern cannot match", c.getPattern().getLocation(), c.getPattern());
		}

		Environment local = new FlatCheckedEnvironment(question.assistantFactory, c.getDefs(), question.env, question.scope);
		question = question.newInfo(local);
		c.setType(c.getResult().apply(rootVisitor, question));
		local.unusedCheck();
		return c.getType();
	}
	
	public ABooleanBasicType binaryCheck(SBooleanBinaryExp node,
			ABooleanBasicType expected,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{

		node.getLeft().apply(rootVisitor, question);
		node.getRight().apply(rootVisitor, question);

		if (!question.assistantFactory.createPTypeAssistant().isType(node.getLeft().getType(), expected.getClass()))
		{
			TypeCheckerErrors.report(3065, "Left hand of " + node.getOp()
					+ " is not " + expected, node.getLocation(), node);
		}

		if (!question.assistantFactory.createPTypeAssistant().isType(node.getRight().getType(), expected.getClass()))
		{
			TypeCheckerErrors.report(3066, "Right hand of " + node.getOp()
					+ " is not " + expected, node.getLocation(), node);
		}

		node.setType(expected);
		return (ABooleanBasicType) node.getType();

	}
}
