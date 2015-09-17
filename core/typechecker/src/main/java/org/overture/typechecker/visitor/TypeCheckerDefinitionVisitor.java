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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.definitions.AAssignmentDefinition;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AUntypedDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.traces.AApplyExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ABracketedExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.AConcurrentExpressionTraceCoreDefinition;
import org.overture.ast.definitions.traces.ALetBeStBindingTraceDefinition;
import org.overture.ast.definitions.traces.ALetDefBindingTraceDefinition;
import org.overture.ast.definitions.traces.ARepeatTraceDefinition;
import org.overture.ast.definitions.traces.ATraceDefinitionTerm;
import org.overture.ast.definitions.traces.PTraceDefinition;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.expressions.AUndefinedExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.node.NodeList;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.ATypeBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.AErrorCase;
import org.overture.ast.statements.AExternalClause;
import org.overture.ast.statements.ANotYetSpecifiedStm;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.typechecker.Pass;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatNumericBasicType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.ExcludedDefinitions;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.FlatEnvironment;
import org.overture.typechecker.PrivateClassEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeChecker;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PAccessSpecifierAssistantTC;
import org.overture.typechecker.utilities.DefinitionTypeResolver;
import org.overture.typechecker.utilities.type.QualifiedDefinition;

public class TypeCheckerDefinitionVisitor extends AbstractTypeCheckVisitor
{

	public TypeCheckerDefinitionVisitor(
			IQuestionAnswer<TypeCheckInfo, PType> typeCheckVisitor)
	{
		super(typeCheckVisitor);
	}

	@Override
	public PType caseAAssignmentDefinition(AAssignmentDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{
		question.qualifiers = null;

		question.assistantFactory.getTypeComparator().checkComposeTypes(node.getType(), question.env, false);

		ExcludedDefinitions.setExcluded(node);
		node.setExpType(node.getExpression().apply(THIS, question));
		ExcludedDefinitions.clearExcluded();
		node.setType(question.assistantFactory.createPTypeAssistant().typeResolve(question.assistantFactory.createPDefinitionAssistant().getType(node), null, THIS, question));

		if (node.getExpType() instanceof AVoidType)
		{
			TypeCheckerErrors.report(3048, "Expression does not return a value", node.getExpression().getLocation(), node.getExpression());
		}

		if (!question.assistantFactory.getTypeComparator().compatible(node.getType(), node.getExpType()))
		{
			TypeCheckerErrors.report(3000, "Expression does not match declared type", node.getExpression().getLocation(), node);
			TypeCheckerErrors.detail2("Declared", node.getType(), "Expression", node.getExpType());
		}

		return node.getType();
	}

	@Override
	public PType caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node, TypeCheckInfo question)
			throws AnalysisException
	{

		if (node.getExpression() instanceof AUndefinedExp)
		{
			if (question.assistantFactory.createPAccessSpecifierAssistant().isStatic(node.getAccess()))
			{
				TypeCheckerErrors.report(3037, "Static instance variable is not initialized: "
						+ node.getName(), node.getLocation(), node);
			}
		}

		// Initializers can reference class members, so create a new env.
		// We set the type qualifier to unknown so that type-based name
		// resolution will succeed.

		Environment cenv = new PrivateClassEnvironment(question.assistantFactory, node.getClassDefinition(), question.env);

		// TODO: This should be a call to the assignment definition typecheck
		// but instance is not an subclass of
		// assignment in our tree
		ExcludedDefinitions.setExcluded(node);
		node.setExpType(node.getExpression().apply(THIS, new TypeCheckInfo(question.assistantFactory, cenv, NameScope.NAMESANDSTATE, question.qualifiers)));
		ExcludedDefinitions.clearExcluded();
		node.setType(question.assistantFactory.createPTypeAssistant().typeResolve(question.assistantFactory.createPDefinitionAssistant().getType(node), null, THIS, question));

		if (node.getExpType() instanceof AVoidType)
		{
			TypeCheckerErrors.report(3048, "Expression does not return a value", node.getExpression().getLocation(), node.getExpression());
		}

		if (!question.assistantFactory.getTypeComparator().compatible(question.assistantFactory.createPDefinitionAssistant().getType(node), node.getExpType()))
		{
			TypeCheckerErrors.report(3000, "Expression does not match declared type", node.getLocation(), node);
			TypeCheckerErrors.detail2("Declared", question.assistantFactory.createPDefinitionAssistant().getType(node), "Expression", node.getExpType());
		}

		return node.getType();

	}

	@Override
	public PType caseAClassInvariantDefinition(AClassInvariantDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{
		Environment newEnv = new FlatEnvironment(question.assistantFactory, question.env, true);
		newEnv.setEnclosingDefinition(node);
		TypeCheckInfo functional = question.newInfo(newEnv);
		functional.qualifiers = null;
		functional.scope = NameScope.NAMESANDSTATE;
		
		PType type = node.getExpression().apply(THIS, functional);

		if (!question.assistantFactory.createPTypeAssistant().isType(type, ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3013, "Class invariant is not a boolean expression", node.getLocation(), node);
		}

		node.setType(type);
		return node.getType();
	}

	@Override
	public PType caseAEqualsDefinition(AEqualsDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{

		question.qualifiers = null;

		node.setExpType(node.getTest().apply(THIS, question));
		PPattern pattern = node.getPattern();

		if (pattern != null)
		{
			question.assistantFactory.createPPatternAssistant().typeResolve(pattern, THIS, question);
			node.setDefs(question.assistantFactory.createPPatternAssistant().getDefinitions(pattern, node.getExpType(), question.scope));
			node.setDefType(node.getExpType());
		} else if (node.getTypebind() != null)
		{
			question.assistantFactory.createATypeBindAssistant().typeResolve(node.getTypebind(), THIS, question);
			ATypeBind typebind = node.getTypebind();

			if (!question.assistantFactory.getTypeComparator().compatible(typebind.getType(), node.getExpType()))
			{
				TypeCheckerErrors.report(3014, "Expression is not compatible with type bind", typebind.getLocation(), typebind);
			}

			node.setDefType(typebind.getType()); // Effectively a cast
			node.setDefs(question.assistantFactory.createPPatternAssistant().getDefinitions(typebind.getPattern(), node.getDefType(), question.scope));
		} else
		{
			question.qualifiers = null;
			PType st = node.getSetbind().getSet().apply(THIS, question);

			if (!question.assistantFactory.createPTypeAssistant().isSet(st))
			{
				TypeCheckerErrors.report(3015, "Set bind is not a set type?", node.getLocation(), node);
				node.setDefType(node.getExpType());
			} else
			{
				PType setof = question.assistantFactory.createPTypeAssistant().getSet(st).getSetof();

				if (!question.assistantFactory.getTypeComparator().compatible(node.getExpType(), setof))
				{
					TypeCheckerErrors.report(3016, "Expression is not compatible with set bind", node.getSetbind().getLocation(), node.getSetbind());
				}

				node.setDefType(setof); // Effectively a cast
			}

			question.assistantFactory.createPPatternAssistant().typeResolve(node.getSetbind().getPattern(), THIS, question);
			node.setDefs(question.assistantFactory.createPPatternAssistant().getDefinitions(node.getSetbind().getPattern(), node.getDefType(), question.scope));
		}

		question.assistantFactory.createPDefinitionListAssistant().typeCheck(node.getDefs(), THIS, question);
		return node.getType();
	}

	@Override
	public PType caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node, TypeCheckInfo question)
			throws AnalysisException
	{

		NodeList<PDefinition> defs = new NodeList<PDefinition>(node);
		question.assistantFactory.getTypeComparator().checkComposeTypes(node.getType(), question.env, false);

		if (node.getTypeParams() != null)
		{
			defs.addAll(question.assistantFactory.createAExplicitFunctionDefinitionAssistant().getTypeParamDefinitions(node));
		}

		PType expectedResult = question.assistantFactory.createAExplicitFunctionDefinitionAssistant().checkParams(node, node.getParamPatternList().listIterator(), (AFunctionType) node.getType());
		node.setExpectedResult(expectedResult);
		List<List<PDefinition>> paramDefinitionList = question.assistantFactory.createSFunctionDefinitionAssistant().getParamDefinitions(node, (AFunctionType) node.getType(), node.getParamPatternList(), node.getLocation());

		Collections.reverse(paramDefinitionList);

		for (List<PDefinition> pdef : paramDefinitionList)
		{
			defs.addAll(pdef); // All definitions of all parameter lists
		}

		FlatCheckedEnvironment local = new FlatCheckedEnvironment(question.assistantFactory, defs, question.env, question.scope);

		local.setStatic(question.assistantFactory.createPAccessSpecifierAssistant().isStatic(node.getAccess()));
		local.setEnclosingDefinition(node);
		local.setFunctional(true);


		// building the new scope for subtypechecks

		question.assistantFactory.createPDefinitionListAssistant().typeCheck(defs, this, new TypeCheckInfo(question.assistantFactory, local, question.scope, question.qualifiers)); // can
																																													// be
																																													// this
																																													// because
																																													// its
																																													// a
																																													// definition
																																													// list

		if (question.env.isVDMPP()
				&& !question.assistantFactory.createPAccessSpecifierAssistant().isStatic(node.getAccess()))
		{
			local.add(question.assistantFactory.createPDefinitionAssistant().getSelfDefinition(node));
		}

		List<QualifiedDefinition> qualified = new Vector<QualifiedDefinition>();

		if (node.getPredef() != null)
		{
			// building the new scope for subtypechecks

			PType b = node.getPredef().getBody().apply(THIS, new TypeCheckInfo(question.assistantFactory, local, NameScope.NAMES));
			ABooleanBasicType expected = AstFactory.newABooleanBasicType(node.getLocation());

			if (!question.assistantFactory.createPTypeAssistant().isType(b, ABooleanBasicType.class))
			{
				TypeChecker.report(3018, "Precondition returns unexpected type", node.getLocation());
				TypeChecker.detail2("Actual", b, "Expected", expected);
			}

			qualified = node.getPredef().getBody().apply(question.assistantFactory.getQualificationVisitor(), new TypeCheckInfo(question.assistantFactory, local, NameScope.NAMES));

			for (QualifiedDefinition qdef : qualified)
			{
				qdef.qualifyType();
			}
		}

		if (node.getPostdef() != null)
		{
			LexNameToken result = new LexNameToken(node.getName().getModule(), "RESULT", node.getLocation());
			PPattern rp = AstFactory.newAIdentifierPattern(result);
			List<PDefinition> rdefs = question.assistantFactory.createPPatternAssistant().getDefinitions(rp, expectedResult, NameScope.NAMES);
			FlatCheckedEnvironment post = new FlatCheckedEnvironment(question.assistantFactory, rdefs, local, NameScope.NAMES);
			post.setFunctional(true);

			// building the new scope for subtypechecks
			PType b = node.getPostdef().getBody().apply(THIS, new TypeCheckInfo(question.assistantFactory, post, NameScope.NAMES));
			ABooleanBasicType expected = AstFactory.newABooleanBasicType(node.getLocation());

			if (!question.assistantFactory.createPTypeAssistant().isType(b, ABooleanBasicType.class))
			{
				TypeChecker.report(3018, "Postcondition returns unexpected type", node.getLocation());
				TypeChecker.detail2("Actual", b, "Expected", expected);
			}
		}

		// This check returns the type of the function body in the case where
		// all of the curried parameter sets are provided.

		PType actualResult = node.getBody().apply(THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope, null, expectedResult, null));

		node.setActualResult(actualResult);

		for (QualifiedDefinition qdef : qualified)
		{
			qdef.resetType();
		}

		if (!question.assistantFactory.getTypeComparator().compatible(expectedResult, node.getActualResult()))
		{
			TypeChecker.report(3018, "Function returns unexpected type", node.getLocation());
			TypeChecker.detail2("Actual", node.getActualResult(), "Expected", expectedResult);
		}

		if (question.assistantFactory.createPTypeAssistant().narrowerThan(node.getType(), node.getAccess()))
		{
			TypeCheckerErrors.report(3019, "Function parameter visibility less than function definition", node.getLocation(), node);
		}

		if (question.env.isVDMPP())
		{
			PAccessSpecifierAssistantTC assist = question.assistantFactory.createPAccessSpecifierAssistant();

			if (assist.isPrivate(node.getAccess())
					&& node.getBody() instanceof ASubclassResponsibilityExp)
			{
				TypeCheckerErrors.report(3329, "Abstract function/operation must be public or protected", node.getLocation(), node);
			}
		}

		if (node.getMeasure() == null && node.getRecursive())
		{
			TypeCheckerErrors.warning(5012, "Recursive function has no measure", node.getLocation(), node);
		} else if (node.getMeasure() != null)
		{
			if (question.env.isVDMPP())
			{
				node.getMeasure().setTypeQualifier(question.assistantFactory.createAExplicitFunctionDefinitionAssistant().getMeasureParams(node));
			}
			node.setMeasureDef(question.env.findName(node.getMeasure(), question.scope));

			if (node.getMeasureDef() == null)
			{
				TypeCheckerErrors.report(3270, "Measure " + node.getMeasure()
						+ " is not in scope", node.getMeasure().getLocation(), node.getMeasure());
			} else if (!(node.getMeasureDef() instanceof AExplicitFunctionDefinition))
			{
				TypeCheckerErrors.report(3271, "Measure " + node.getMeasure()
						+ " is not an explicit function", node.getMeasure().getLocation(), node.getMeasure());
			} else if (node.getMeasureDef() == node)
			{
				TypeCheckerErrors.report(3304, "Recursive function cannot be its own measure", node.getMeasure().getLocation(), node.getMeasure());
			} else
			{
				AExplicitFunctionDefinition efd = (AExplicitFunctionDefinition) node.getMeasureDef();

				if (node.getTypeParams() == null && efd.getTypeParams() != null)
				{
					TypeCheckerErrors.report(3309, "Measure must not be polymorphic", node.getMeasure().getLocation(), node.getMeasure());
				} else if (node.getTypeParams() != null
						&& efd.getTypeParams() == null)
				{
					TypeCheckerErrors.report(3310, "Measure must also be polymorphic", node.getMeasure().getLocation(), node.getMeasure());
				} else if (node.getTypeParams() != null
						&& efd.getTypeParams() != null
						&& !node.getTypeParams().equals(efd.getTypeParams()))
				{
					TypeCheckerErrors.report(3318, "Measure's type parameters must match function's", node.getMeasure().getLocation(), node.getMeasure());
					TypeChecker.detail2("Actual", efd.getTypeParams(), "Expected", node.getTypeParams());
				}

				AFunctionType mtype = (AFunctionType) efd.getType();

				if (!question.assistantFactory.getTypeComparator().compatible(mtype.getParameters(), question.assistantFactory.createAExplicitFunctionDefinitionAssistant().getMeasureParams(node)))
				{
					TypeCheckerErrors.report(3303, "Measure parameters different to function", node.getMeasure().getLocation(), node.getMeasure());
					TypeChecker.detail2(node.getMeasure().getFullName(), mtype.getParameters(), "Expected", question.assistantFactory.createAExplicitFunctionDefinitionAssistant().getMeasureParams(node));
				}

				if (!(mtype.getResult() instanceof ANatNumericBasicType))
				{
					if (mtype.getResult() instanceof AProductType)
					{
						AProductType pt = question.assistantFactory.createPTypeAssistant().getProduct(mtype.getResult());

						for (PType t : pt.getTypes())
						{
							if (!(t instanceof ANatNumericBasicType))
							{
								TypeCheckerErrors.report(3272, "Measure range is not a nat, or a nat tuple", node.getMeasure().getLocation(), node.getMeasure());
								TypeCheckerErrors.detail("Actual", mtype.getResult());
								break;
							}
						}

						node.setMeasureLexical(pt.getTypes().size());
					} else
					{
						TypeCheckerErrors.report(3272, "Measure range is not a nat, or a nat tuple", node.getMeasure().getLocation(), node.getMeasure());
						TypeCheckerErrors.detail("Actual", mtype.getResult());
					}
				}
			}
		}

		if (!(node.getBody() instanceof ANotYetSpecifiedExp)
				&& !(node.getBody() instanceof ASubclassResponsibilityExp))
		{
			local.unusedCheck();
		}

		node.setType(node.getType());
		return node.getType();
	}

	@Override
	public PType caseAExternalDefinition(AExternalDefinition node,
			TypeCheckInfo question)
	{
		// Nothing to do - state is type checked separately
		return null;
	}

	@Override
	public PType caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node, TypeCheckInfo question)
			throws AnalysisException
	{
		question.assistantFactory.getTypeComparator().checkComposeTypes(node.getType(), question.env, false);
		List<PDefinition> defs = new Vector<PDefinition>();

		if (node.getTypeParams() != null)
		{
			defs.addAll(question.assistantFactory.createAImplicitFunctionDefinitionAssistant().getTypeParamDefinitions(node));
		}

		List<PDefinition> argdefs = new Vector<PDefinition>();

		for (APatternListTypePair pltp : node.getParamPatterns())
		{
			argdefs.addAll(getDefinitions(pltp, NameScope.LOCAL, question.assistantFactory));
		}

		defs.addAll(question.assistantFactory.createPDefinitionAssistant().checkDuplicatePatterns(node, argdefs));
		FlatCheckedEnvironment local = new FlatCheckedEnvironment(question.assistantFactory, defs, question.env, question.scope);
		local.setStatic(question.assistantFactory.createPAccessSpecifierAssistant().isStatic(node.getAccess()));
		local.setEnclosingDefinition(node);
		local.setFunctional(true);

		question.assistantFactory.createPDefinitionListAssistant().typeCheck(defs, THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope, question.qualifiers));

		List<QualifiedDefinition> qualified = new Vector<QualifiedDefinition>();

		if (node.getPredef() != null)
		{
			PType b = node.getPredef().getBody().apply(THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope));
			ABooleanBasicType expected = AstFactory.newABooleanBasicType(node.getLocation());

			if (!question.assistantFactory.createPTypeAssistant().isType(b, ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3018, "Precondition returns unexpected type", node.getLocation(), node);
				TypeCheckerErrors.detail2("Actual", b, "Expected", expected);
			}

			qualified = node.getPredef().getBody().apply(question.assistantFactory.getQualificationVisitor(), new TypeCheckInfo(question.assistantFactory, local, question.scope));

			for (QualifiedDefinition qdef : qualified)
			{
				qdef.qualifyType();
			}
		}

		if (node.getBody() != null)
		{
			if (node.getClassDefinition() != null
					&& !question.assistantFactory.createPAccessSpecifierAssistant().isStatic(node.getAccess()))
			{
				local.add(question.assistantFactory.createPDefinitionAssistant().getSelfDefinition(node));
			}

			node.setActualResult(node.getBody().apply(THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope, question.qualifiers, node.getResult().getType(), null)));

			if (!question.assistantFactory.getTypeComparator().compatible(node.getResult().getType(), node.getActualResult()))
			{
				TypeCheckerErrors.report(3029, "Function returns unexpected type", node.getLocation(), node);
				TypeCheckerErrors.detail2("Actual", node.getActualResult(), "Expected", node.getResult().getType());
			}
		}

		for (QualifiedDefinition qdef : qualified)
		{
			qdef.resetType();
		}

		if (question.assistantFactory.createPTypeAssistant().narrowerThan(question.assistantFactory.createPDefinitionAssistant().getType(node), node.getAccess()))
		{
			TypeCheckerErrors.report(3030, "Function parameter visibility less than function definition", node.getLocation(), node);
		}

		if (question.env.isVDMPP())
		{
			PAccessSpecifierAssistantTC assist = question.assistantFactory.createPAccessSpecifierAssistant();

			if (assist.isPrivate(node.getAccess())
					&& node.getBody() instanceof ASubclassResponsibilityExp)
			{
				TypeCheckerErrors.report(3329, "Abstract function/operation must be public or protected", node.getLocation(), node);
			}
		}

		// The result variables are in scope for the post condition

		if (node.getPostdef() != null)
		{
			PType b = null;

			if (node.getResult() != null)
			{
				List<PDefinition> postdefs = question.assistantFactory.createAPatternTypePairAssistant().getDefinitions(node.getResult());
				FlatCheckedEnvironment post = new FlatCheckedEnvironment(question.assistantFactory, postdefs, local, NameScope.NAMES);
				post.setStatic(question.assistantFactory.createPAccessSpecifierAssistant().isStatic(node.getAccess()));
				post.setEnclosingDefinition(node);
				post.setFunctional(true);
				b = node.getPostdef().getBody().apply(THIS, new TypeCheckInfo(question.assistantFactory, post, NameScope.NAMES));
				post.unusedCheck();
			} else
			{
				b = node.getPostdef().getBody().apply(THIS, new TypeCheckInfo(question.assistantFactory, local, NameScope.NAMES));
			}

			ABooleanBasicType expected = AstFactory.newABooleanBasicType(node.getLocation());

			if (!question.assistantFactory.createPTypeAssistant().isType(b, ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3018, "Postcondition returns unexpected type", node.getLocation(), node);
				TypeCheckerErrors.detail2("Actual", b, "Expected", expected);
			}
		}

		if (node.getMeasure() == null && node.getRecursive())
		{
			TypeCheckerErrors.warning(5012, "Recursive function has no measure", node.getLocation(), node);
		} else if (node.getMeasure() != null)
		{
			if (question.env.isVDMPP())
			{
				node.getMeasure().setTypeQualifier(((AFunctionType) node.getType()).getParameters());
			}
			node.setMeasureDef(question.env.findName(node.getMeasure(), question.scope));

			if (node.getBody() == null)
			{
				TypeCheckerErrors.report(3273, "Measure not allowed for an implicit function", node.getMeasure().getLocation(), node);
			} else if (node.getMeasureDef() == null)
			{
				TypeCheckerErrors.report(3270, "Measure " + node.getMeasure()
						+ " is not in scope", node.getMeasure().getLocation(), node.getMeasure());
			} else if (!(node.getMeasureDef() instanceof AExplicitFunctionDefinition))
			{
				TypeCheckerErrors.report(3271, "Measure " + node.getMeasure()
						+ " is not an explicit function", node.getMeasure().getLocation(), node.getMeasure());
			} else
			{
				AExplicitFunctionDefinition efd = (AExplicitFunctionDefinition) node.getMeasureDef();

				if (node.getTypeParams() == null && efd.getTypeParams() != null)
				{
					TypeCheckerErrors.report(3309, "Measure must not be polymorphic", node.getMeasure().getLocation(), node.getMeasure());
				} else if (node.getTypeParams() != null
						&& efd.getTypeParams() == null)
				{
					TypeCheckerErrors.report(3310, "Measure must also be polymorphic", node.getMeasure().getLocation(), node.getMeasure());
				} else if (node.getTypeParams() != null
						&& efd.getTypeParams() != null
						&& !node.getTypeParams().equals(efd.getTypeParams()))
				{
					TypeCheckerErrors.report(3318, "Measure's type parameters must match function's", node.getMeasure().getLocation(), node.getMeasure());
					TypeCheckerErrors.detail2("Actual", efd.getTypeParams(), "Expected", node.getTypeParams());
				}

				AFunctionType mtype = (AFunctionType) node.getMeasureDef().getType();

				if (!question.assistantFactory.getTypeComparator().compatible(mtype.getParameters(), ((AFunctionType) node.getType()).getParameters()))
				{
					TypeCheckerErrors.report(3303, "Measure parameters different to function", node.getMeasure().getLocation(), node.getMeasure());
					TypeCheckerErrors.detail2(node.getMeasure().getName(), mtype.getParameters(), node.getName().getName(), ((AFunctionType) node.getType()).getParameters());
				}

				if (!(mtype.getResult() instanceof ANatNumericBasicType))
				{
					if (question.assistantFactory.createPTypeAssistant().isProduct(mtype.getResult()))
					{
						AProductType pt = question.assistantFactory.createPTypeAssistant().getProduct(mtype.getResult());

						for (PType t : pt.getTypes())
						{
							if (!(t instanceof ANatNumericBasicType))
							{
								TypeCheckerErrors.report(3272, "Measure range is not a nat, or a nat tuple", node.getMeasure().getLocation(), node.getMeasure());
								TypeCheckerErrors.detail("Actual", mtype.getResult());
							}
						}

						node.setMeasureLexical(pt.getTypes().size());
					} else
					{
						TypeCheckerErrors.report(3272, "Measure range is not a nat, or a nat tuple", node.getMeasure().getLocation(), node.getMeasure());
						TypeCheckerErrors.detail("Actual", mtype.getResult());
					}
				}
			}
		}

		if (!(node.getBody() instanceof ANotYetSpecifiedExp)
				&& !(node.getBody() instanceof ASubclassResponsibilityExp))
		{
			local.unusedCheck();
		}

		node.setType(node.getType());
		return node.getType();
	}

	@Override
	public PType caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, TypeCheckInfo question)
			throws AnalysisException
	{
		question.assistantFactory.getTypeComparator().checkComposeTypes(node.getType(), question.env, false);
		List<PType> ptypes = ((AOperationType) node.getType()).getParameters();

		if (node.getParameterPatterns().size() > ptypes.size())
		{
			TypeCheckerErrors.report(3023, "Too many parameter patterns", node.getLocation(), node);
			TypeCheckerErrors.detail2("Type params", ptypes.size(), "Patterns", node.getParameterPatterns().size());
			return null;
		} else if (node.getParameterPatterns().size() < ptypes.size())
		{
			TypeCheckerErrors.report(3024, "Too few parameter patterns", node.getLocation(), node);
			TypeCheckerErrors.detail2("Type params", ptypes.size(), "Patterns", node.getParameterPatterns().size());
			return null;
		}

		node.setParamDefinitions(question.assistantFactory.createAExplicitOperationDefinitionAssistant().getParamDefinitions(node));
		question.assistantFactory.createPDefinitionListAssistant().typeCheck(node.getParamDefinitions(), THIS, new TypeCheckInfo(question.assistantFactory, question.env, NameScope.NAMESANDSTATE, question.qualifiers));

		FlatCheckedEnvironment local = new FlatCheckedEnvironment(question.assistantFactory, node.getParamDefinitions(), question.env, NameScope.NAMESANDSTATE);
		local.setStatic(question.assistantFactory.createPAccessSpecifierAssistant().isStatic(node.getAccess()));
		local.setEnclosingDefinition(node);
		local.setFunctional(false);

		if (question.env.isVDMPP())
		{
			if (!question.assistantFactory.createPAccessSpecifierAssistant().isStatic(node.getAccess()))
			{
				local.add(question.assistantFactory.createPDefinitionAssistant().getSelfDefinition(node));
			}

			if (node.getIsConstructor())
			{
				if (question.assistantFactory.createPAccessSpecifierAssistant().isAsync(node.getAccess()) ||
					question.assistantFactory.createPAccessSpecifierAssistant().isStatic(node.getAccess()) ||
					node.getAccess().getPure())
				{
					TypeCheckerErrors.report(3286, "Constructor cannot be 'async', 'static' or 'pure'", node.getLocation(), node);
				}

				if (question.assistantFactory.createPTypeAssistant().isClass(((AOperationType) node.getType()).getResult(), question.env))
				{
					AClassType ctype = question.assistantFactory.createPTypeAssistant().getClassType(((AOperationType) node.getType()).getResult(), question.env);

					if (ctype.getClassdef() != node.getClassDefinition())
					{
						// FIXME: This is a TEST, it should be tried to see if
						// it is valid
						TypeCheckerErrors.report(3025, "Constructor operation must have return type "
								+ node.getClassDefinition().getName().getName(), node.getLocation(), node);
					}
					// TODO: THIS COULD BE A HACK to code (ctype.getClassdef()
					// != node.getClassDefinition())
					if (!question.assistantFactory.getLexNameTokenAssistant().isEqual(ctype.getClassdef().getName(), node.getClassDefinition().getName()))
					{
						TypeCheckerErrors.report(3025, "Constructor operation must have return type "
								+ node.getClassDefinition().getName().getName(), node.getLocation(), node);
					}
				} else
				{
					TypeCheckerErrors.report(3026, "Constructor operation must have return type "
							+ node.getClassDefinition().getName().getName(), node.getLocation(), node);
				}
			}
		}

		List<QualifiedDefinition> qualified = new Vector<QualifiedDefinition>();

		if (node.getPredef() != null)
		{
			FlatEnvironment pre = new FlatEnvironment(question.assistantFactory, new Vector<PDefinition>(), local);
			pre.setEnclosingDefinition(node.getPredef());
			pre.setFunctional(true);

			PType b = node.getPredef().getBody().apply(THIS, new TypeCheckInfo(question.assistantFactory, pre, NameScope.NAMESANDSTATE));

			ABooleanBasicType expected = AstFactory.newABooleanBasicType(node.getLocation());

			if (!question.assistantFactory.createPTypeAssistant().isType(b, ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3018, "Precondition returns unexpected type", node.getLocation(), node);
				TypeCheckerErrors.detail2("Actual", b, "Expected", expected);
			}

			qualified = node.getPredef().getBody().apply(question.assistantFactory.getQualificationVisitor(), new TypeCheckInfo(question.assistantFactory, pre, NameScope.NAMESANDSTATE));

			for (QualifiedDefinition qdef : qualified)
			{
				qdef.qualifyType();
			}
		}

		if (node.getPostdef() != null)
		{
			LexNameToken result = new LexNameToken(node.getName().getModule(), "RESULT", node.getLocation());
			PPattern rp = AstFactory.newAIdentifierPattern(result);
			List<PDefinition> rdefs = question.assistantFactory.createPPatternAssistant().getDefinitions(rp, ((AOperationType) node.getType()).getResult(), NameScope.NAMESANDANYSTATE);
			FlatEnvironment post = new FlatEnvironment(question.assistantFactory, rdefs, local);
			post.setEnclosingDefinition(node.getPostdef());
			post.setFunctional(true);
			PType b = node.getPostdef().getBody().apply(THIS, new TypeCheckInfo(question.assistantFactory, post, NameScope.NAMESANDANYSTATE));
			ABooleanBasicType expected = AstFactory.newABooleanBasicType(node.getLocation());

			if (!question.assistantFactory.createPTypeAssistant().isType(b, ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3018, "Postcondition returns unexpected type", node.getLocation(), node);
				TypeCheckerErrors.detail2("Actual", b, "Expected", expected);
			}
		}

		PType expectedResult = ((AOperationType) node.getType()).getResult();
		PType actualResult = node.getBody().apply(THIS, new TypeCheckInfo(question.assistantFactory, local, NameScope.NAMESANDSTATE, null, null, expectedResult));
		node.setActualResult(actualResult);
		boolean compatible = question.assistantFactory.getTypeComparator().compatible(expectedResult, node.getActualResult());

		for (QualifiedDefinition qdef : qualified)
		{
			qdef.resetType();
		}

		if (node.getIsConstructor()
				&& !question.assistantFactory.createPTypeAssistant().isType(node.getActualResult(), AVoidType.class)
				&& !compatible || !node.getIsConstructor() && !compatible)
		{
			TypeCheckerErrors.report(3027, "Operation returns unexpected type", node.getLocation(), node);
			TypeCheckerErrors.detail2("Actual", node.getActualResult(), "Expected", ((AOperationType) node.getType()).getResult());
		} else if (!node.getIsConstructor()
				&& !question.assistantFactory.createPTypeAssistant().isUnknown(actualResult))
		{
			if (question.assistantFactory.createPTypeAssistant().isVoid(((AOperationType) node.getType()).getResult())
					&& !question.assistantFactory.createPTypeAssistant().isVoid(actualResult))
			{
				TypeCheckerErrors.report(3312, "Void operation returns non-void value", node.getLocation(), node);
				TypeCheckerErrors.detail2("Actual", actualResult, "Expected", ((AOperationType) node.getType()).getResult());
			} else if (!question.assistantFactory.createPTypeAssistant().isVoid(((AOperationType) node.getType()).getResult())
					&& question.assistantFactory.createPTypeAssistant().hasVoid(actualResult))
			{
				TypeCheckerErrors.report(3313, "Operation returns void value", node.getLocation(), node);
				TypeCheckerErrors.detail2("Actual", actualResult, "Expected", ((AOperationType) node.getType()).getResult());
			}
		}

		if (question.assistantFactory.createPAccessSpecifierAssistant().isAsync(node.getAccess())
				&& !question.assistantFactory.createPTypeAssistant().isType(((AOperationType) node.getType()).getResult(), AVoidType.class))
		{
			TypeCheckerErrors.report(3293, "Asynchronous operation '"
					+ node.getName() + "' cannot return a value", node.getLocation(), node);
		}

		if (node.getAccess().getPure() &&
			question.assistantFactory.createPTypeAssistant().isType(((AOperationType) node.getType()).getResult(), AVoidType.class) &&
			!question.assistantFactory.createPTypeAssistant().isUnknown(((AOperationType) node.getType()).getResult()))
		{
			TypeCheckerErrors.report(3344, "Pure operation '" + node.getName() + "' must return a value", node.getLocation(), node);
		}

		if (node.getAccess().getPure() && question.assistantFactory.createPAccessSpecifierAssistant().isAsync(node.getAccess()))
		{
			TypeCheckerErrors.report(3345, "Pure operation '" + node.getName() + "' cannot also be async", node.getLocation(), node);
		}

		if (question.assistantFactory.createPTypeAssistant().narrowerThan(node.getType(), node.getAccess()))
		{
			TypeCheckerErrors.report(3028, "Operation parameter visibility less than operation definition", node.getLocation(), node);
		}

		if (question.env.isVDMPP())
		{
			PAccessSpecifierAssistantTC assist = question.assistantFactory.createPAccessSpecifierAssistant();

			if (assist.isPrivate(node.getAccess())
					&& node.getBody() instanceof ASubclassResponsibilityStm)
			{
				TypeCheckerErrors.report(3329, "Abstract function/operation must be public or protected", node.getLocation(), node);
			}
		}

		if (!(node.getBody() instanceof ANotYetSpecifiedStm)
				&& !(node.getBody() instanceof ASubclassResponsibilityStm))
		{
			local.unusedCheck();
		}
		node.setType(node.getType());
		return node.getType();
	}

	@Override
	public PType caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node, TypeCheckInfo question)
			throws AnalysisException
	{
		question.assistantFactory.getTypeComparator().checkComposeTypes(node.getType(), question.env, false);
		question = new TypeCheckInfo(question.assistantFactory, question.env, NameScope.NAMESANDSTATE, question.qualifiers);
		List<PDefinition> defs = new Vector<PDefinition>();
		List<PDefinition> argdefs = new Vector<PDefinition>();

		if (question.env.isVDMPP())
		{
			node.setStateDefinition(question.env.findClassDefinition());
		} else
		{
			node.setStateDefinition(question.env.findStateDefinition());
		}

		for (APatternListTypePair ptp : node.getParameterPatterns())
		{
			argdefs.addAll(getDefinitions(ptp, NameScope.LOCAL, question.assistantFactory));
		}

		defs.addAll(question.assistantFactory.createPDefinitionAssistant().checkDuplicatePatterns(node, argdefs));

		if (node.getResult() != null)
		{
			defs.addAll(question.assistantFactory.createPPatternAssistant().getDefinitions(node.getResult().getPattern(), ((AOperationType) node.getType()).getResult(), NameScope.STATE));
		}

		// Now we build local definitions for each of the externals, so
		// that they can be added to the local environment, while the
		// global state is made inaccessible - but only if we have
		// an "ext" clause

		boolean limitStateScope = false;

		if (node.getExternals().size() != 0)
		{
			for (AExternalClause clause : node.getExternals())
			{
				question.assistantFactory.getTypeComparator().checkComposeTypes(clause.getType(), question.env, false);

				for (ILexNameToken exname : clause.getIdentifiers())
				{
					PDefinition sdef = question.env.findName(exname, NameScope.STATE);
					typeResolve(clause, THIS, question);

					if (sdef == null)
					{
						TypeCheckerErrors.report(3031, "Unknown state variable "
								+ exname, exname.getLocation(), exname);
					} else
					{
						if (!(clause.getType() instanceof AUnknownType)
								&& !question.assistantFactory.createPTypeAssistant().equals(sdef.getType(), clause.getType()))
						{
							TypeCheckerErrors.report(3032, "State variable "
									+ exname + " is not this type", node.getLocation(), node);
							TypeCheckerErrors.detail2("Declared", sdef.getType(), "ext type", clause.getType());
						} else
						{
							defs.add(AstFactory.newAExternalDefinition(sdef, clause.getMode()));

							// VDM++ "ext wr" clauses in a constructor
							// effectively
							// initialize the instance variable concerned.

							if (clause.getMode().getType() == VDMToken.WRITE
									&& sdef instanceof AInstanceVariableDefinition
									&& node.getName().getName().equals(node.getClassDefinition().getName().getName()))
							{
								AInstanceVariableDefinition iv = (AInstanceVariableDefinition) sdef;
								iv.setInitialized(true);
							}
						}
					}
				}
			}

			// All relevant globals are now in defs (local), so we
			// limit the state searching scope

			limitStateScope = true;
		}

		question.assistantFactory.createPDefinitionListAssistant().typeCheck(defs, THIS, question);

		FlatCheckedEnvironment local = new FlatCheckedEnvironment(question.assistantFactory, defs, question.env, question.scope);
		local.setLimitStateScope(limitStateScope);
		local.setStatic(question.assistantFactory.createPAccessSpecifierAssistant().isStatic(node.getAccess()));
		local.setEnclosingDefinition(node);
		local.setFunctional(false);

		if (question.env.isVDMPP())
		{
			if (node.getIsConstructor())
			{
				if (question.assistantFactory.createPAccessSpecifierAssistant().isAsync(node.getAccess()) ||
					question.assistantFactory.createPAccessSpecifierAssistant().isStatic(node.getAccess()) ||
					node.getAccess().getPure())
				{
					TypeCheckerErrors.report(3286, "Constructor cannot be 'async', 'static' or 'pure'", node.getLocation(), node);
				}

				if (question.assistantFactory.createPTypeAssistant().isClass(((AOperationType) node.getType()).getResult(), question.env))
				{
					AClassType ctype = question.assistantFactory.createPTypeAssistant().getClassType(((AOperationType) node.getType()).getResult(), question.env);

					if (ctype.getClassdef() != node.getClassDefinition())
					{
						TypeCheckerErrors.report(3025, "Constructor operation must have return type "
								+ node.getClassDefinition().getName().getName(), node.getLocation(), node);
					}
				} else
				{
					TypeCheckerErrors.report(3026, "Constructor operation must have return type "
							+ node.getClassDefinition().getName().getName(), node.getLocation(), node);

				}
			}
		}

		List<QualifiedDefinition> qualified = new Vector<QualifiedDefinition>();

		if (node.getPredef() != null)
		{
			FlatEnvironment pre = new FlatEnvironment(question.assistantFactory, new Vector<PDefinition>(), local);
			pre.setEnclosingDefinition(node.getPredef());
			pre.setFunctional(true);
			PType b = node.getPredef().getBody().apply(THIS, new TypeCheckInfo(question.assistantFactory, pre, NameScope.NAMESANDSTATE));
			ABooleanBasicType expected = AstFactory.newABooleanBasicType(node.getLocation());

			if (!question.assistantFactory.createPTypeAssistant().isType(b, ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3018, "Precondition returns unexpected type", node.getLocation(), node);
				TypeCheckerErrors.detail2("Actual", b, "Expected", expected);
			}

			qualified = node.getPredef().getBody().apply(question.assistantFactory.getQualificationVisitor(), new TypeCheckInfo(question.assistantFactory, pre, NameScope.NAMESANDSTATE));

			for (QualifiedDefinition qdef : qualified)
			{
				qdef.qualifyType();
			}
		}

		if (node.getBody() != null)
		{
			if (node.getClassDefinition() != null
					&& !question.assistantFactory.createPAccessSpecifierAssistant().isStatic(node.getAccess()))
			{
				local.add(question.assistantFactory.createPDefinitionAssistant().getSelfDefinition(node));
			}

			PType expectedResult = ((AOperationType) node.getType()).getResult();
			node.setActualResult(node.getBody().apply(THIS, new TypeCheckInfo(question.assistantFactory, local, NameScope.NAMESANDSTATE, null, null, expectedResult)));

			boolean compatible = question.assistantFactory.getTypeComparator().compatible(expectedResult, node.getActualResult());

			if (node.getIsConstructor()
					&& !question.assistantFactory.createPTypeAssistant().isType(node.getActualResult(), AVoidType.class)
					&& !compatible || !node.getIsConstructor() && !compatible)
			{
				TypeCheckerErrors.report(3035, "Operation returns unexpected type", node.getLocation(), node);
				TypeCheckerErrors.detail2("Actual", node.getActualResult(), "Expected", ((AOperationType) node.getType()).getResult());
			} else if (!node.getIsConstructor()
					&& !question.assistantFactory.createPTypeAssistant().isUnknown(node.getActualResult()))
			{
				if (question.assistantFactory.createPTypeAssistant().isVoid(((AOperationType) node.getType()).getResult())
						&& !question.assistantFactory.createPTypeAssistant().isVoid(node.getActualResult()))
				{
					TypeCheckerErrors.report(3312, "Void operation returns non-void value", node.getLocation(), node);
					TypeCheckerErrors.detail2("Actual", node.getActualResult(), "Expected", ((AOperationType) node.getType()).getResult());
				} else if (!question.assistantFactory.createPTypeAssistant().isVoid(((AOperationType) node.getType()).getResult())
						&& question.assistantFactory.createPTypeAssistant().hasVoid(node.getActualResult()))
				{
					TypeCheckerErrors.report(3313, "Operation returns void value", node.getLocation(), node);
					TypeCheckerErrors.detail2("Actual", node.getActualResult(), "Expected", ((AOperationType) node.getType()).getResult());
				}
			}
		}

		for (QualifiedDefinition qdef : qualified)
		{
			qdef.resetType();
		}

		if (question.assistantFactory.createPAccessSpecifierAssistant().isAsync(node.getAccess())
				&& !question.assistantFactory.createPTypeAssistant().isType(((AOperationType) node.getType()).getResult(), AVoidType.class))
		{
			TypeCheckerErrors.report(3293, "Asynchronous operation "
					+ node.getName() + " cannot return a value", node.getLocation(), node);
		}

		if (node.getAccess().getPure() &&
			question.assistantFactory.createPTypeAssistant().isType(((AOperationType) node.getType()).getResult(), AVoidType.class) &&
			!question.assistantFactory.createPTypeAssistant().isUnknown(((AOperationType) node.getType()).getResult()))
		{
			TypeCheckerErrors.report(3344, "Pure operation '" + node.getName() + "' must return a value", node.getLocation(), node);
		}

		if (node.getAccess().getPure() &&
			question.assistantFactory.createPAccessSpecifierAssistant().isAsync(node.getAccess()))
		{
			TypeCheckerErrors.report(3345, "Pure operation '" + node.getName() + "' cannot also be async", node.getLocation(), node);
		}

		if (question.assistantFactory.createPTypeAssistant().narrowerThan(node.getType(), node.getAccess()))
		{
			TypeCheckerErrors.report(3036, "Operation parameter visibility less than operation definition", node.getLocation(), node);
		}

		if (question.env.isVDMPP())
		{
			PAccessSpecifierAssistantTC assist = question.assistantFactory.createPAccessSpecifierAssistant();

			if (assist.isPrivate(node.getAccess())
					&& node.getBody() instanceof ASubclassResponsibilityStm)
			{
				TypeCheckerErrors.report(3329, "Abstract function/operation must be public or protected", node.getLocation(), node);
			}
		}

		// The result variables are in scope for the post condition

		if (node.getPostdef() != null)
		{
			PType b = null;

			if (node.getResult() != null)
			{
				List<PDefinition> postdefs = question.assistantFactory.createAPatternTypePairAssistant().getDefinitions(node.getResult());
				FlatCheckedEnvironment post = new FlatCheckedEnvironment(question.assistantFactory, postdefs, local, NameScope.NAMESANDANYSTATE);
				post.setStatic(question.assistantFactory.createPAccessSpecifierAssistant().isStatic(node.getAccess()));
				post.setEnclosingDefinition(node.getPostdef());
				post.setFunctional(true);
				b = node.getPostdef().getBody().apply(THIS, new TypeCheckInfo(question.assistantFactory, post, NameScope.NAMESANDANYSTATE));
				post.unusedCheck();
			} else
			{
				FlatEnvironment post = new FlatEnvironment(question.assistantFactory, new Vector<PDefinition>(), local);
				post.setEnclosingDefinition(node.getPostdef());
				post.setFunctional(true);
				b = node.getPostdef().getBody().apply(THIS, new TypeCheckInfo(question.assistantFactory, post, NameScope.NAMESANDANYSTATE));
			}

			ABooleanBasicType expected = AstFactory.newABooleanBasicType(node.getLocation());

			if (!question.assistantFactory.createPTypeAssistant().isType(b, ABooleanBasicType.class))
			{
				TypeCheckerErrors.report(3018, "Postcondition returns unexpected type", node.getLocation(), node);
				TypeCheckerErrors.detail2("Actual", b, "Expected", expected);
			}
		}

		if (node.getErrors() != null)
		{
			for (AErrorCase error : node.getErrors())
			{
				TypeCheckInfo newQuestion = new TypeCheckInfo(question.assistantFactory, local, NameScope.NAMESANDSTATE);
				PType a = error.getLeft().apply(THIS, newQuestion);

				if (!question.assistantFactory.createPTypeAssistant().isType(a, ABooleanBasicType.class))
				{
					TypeCheckerErrors.report(3307, "Errs clause is not bool -> bool", error.getLeft().getLocation(), error.getLeft());
				}

				newQuestion.scope = NameScope.NAMESANDANYSTATE;
				PType b = error.getRight().apply(THIS, newQuestion);

				if (!question.assistantFactory.createPTypeAssistant().isType(b, ABooleanBasicType.class))
				{
					TypeCheckerErrors.report(3307, "Errs clause is not bool -> bool", error.getRight().getLocation(), error.getRight());
				}
			}
		}

		if (!(node.getBody() instanceof ANotYetSpecifiedStm)
				&& !(node.getBody() instanceof ASubclassResponsibilityStm))
		{
			local.unusedCheck();
		}
		// node.setType(node.getActualResult());
		return node.getType();
	}

	@Override
	public PType caseAImportedDefinition(AImportedDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{
		node.setType(node.getDef().apply(THIS, question));

		return node.getType();
	}

	@Override
	public PType caseAInheritedDefinition(AInheritedDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{
		node.setType(node.getSuperdef().apply(THIS, question));
		return node.getType();
	}

	@Override
	public PType caseALocalDefinition(ALocalDefinition node,
			TypeCheckInfo question)
	{
		if (node.getType() != null)
		{
			node.setType(question.assistantFactory.createPTypeAssistant().typeResolve(node.getType(), null, THIS, question));
		}

		return node.getType();
	}

	@Override
	public PType caseAMultiBindListDefinition(AMultiBindListDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{
		if (node.getType() != null)
		{
			question.assistantFactory.getTypeComparator().checkComposeTypes(node.getType(), question.env, false);
		}

		List<PDefinition> defs = new Vector<PDefinition>();

		for (PMultipleBind mb : node.getBindings())
		{
			PType type = mb.apply(THIS, question);
			defs.addAll(question.assistantFactory.createPMultipleBindAssistant().getDefinitions(mb, type, question));
		}

		question.assistantFactory.createPDefinitionListAssistant().typeCheck(defs, THIS, question);
		node.setDefs(defs);
		return null;
	}

	@Override
	public PType caseAMutexSyncDefinition(AMutexSyncDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{

		SClassDefinition classdef = question.env.findClassDefinition();

		if (node.getOperations().isEmpty())
		{
			// Add all locally visibly callable operations for mutex(all)

			for (PDefinition def : node.getClassDefinition().apply(question.assistantFactory.getDefinitionCollector()))
			// SClassDefinitionAssistantTC.getLocalDefinitions(node.getClassDefinition()))
			{
				if (question.assistantFactory.createPDefinitionAssistant().isCallableOperation(def)
						&& !def.getName().getName().equals(classdef.getName().getName()))
				{
					node.getOperations().add(def.getName());
				}
			}
		}

		for (ILexNameToken opname : node.getOperations())
		{
			int found = 0;

			List<PDefinition> definitions = question.assistantFactory.createPDefinitionAssistant().getDefinitions(classdef);

			for (PDefinition def : definitions)
			{
				if (def.getName() != null && def.getName().matches(opname))
				{
					found++;

					if (!question.assistantFactory.createPDefinitionAssistant().isCallableOperation(def))
					{
						TypeCheckerErrors.report(3038, opname
								+ " is not an explicit operation", opname.getLocation(), opname);
					}
					
					if (def.getAccess().getPure())
					{
						TypeCheckerErrors.report(3343, "Cannot have a mutex with pure operations", opname.getLocation(), opname);
					}
				}
			}

			if (found == 0)
			{
				TypeCheckerErrors.report(3039, opname + " is not in scope", opname.getLocation(), opname);
			} else if (found > 1)
			{
				TypeCheckerErrors.warning(5002, "Mutex of overloaded operation", opname.getLocation(), opname);
			}

			if (opname.getName().equals(classdef.getName().getName()))
			{
				TypeCheckerErrors.report(3040, "Cannot put mutex on a constructor", opname.getLocation(), opname);
			}

			for (ILexNameToken other : node.getOperations())
			{
				if (opname != other
						&& question.assistantFactory.getLexNameTokenAssistant().isEqual(opname, other))
				{
					TypeCheckerErrors.report(3041, "Duplicate mutex name", opname.getLocation(), opname);
				}
			}

		}
		return null;
	}

	@Override
	public PType caseANamedTraceDefinition(ANamedTraceDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{

		if (question.env.isVDMPP())
		{
			question = new TypeCheckInfo(question.assistantFactory, new FlatEnvironment(question.assistantFactory, question.assistantFactory.createPDefinitionAssistant().getSelfDefinition(node), question.env), question.scope, question.qualifiers);
		}

		for (ATraceDefinitionTerm term : node.getTerms())
		{
			typeCheck(term.getList(), THIS, new TypeCheckInfo(question.assistantFactory, question.env, NameScope.NAMESANDSTATE));
		}

		return null;
	}

	@Override
	public PType caseAPerSyncDefinition(APerSyncDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{

		Environment base = question.env;

		SClassDefinition classdef = base.findClassDefinition();
		int opfound = 0;
		int perfound = 0;
		Boolean isStatic = null;

		List<PDefinition> definitions = question.assistantFactory.createPDefinitionAssistant().getDefinitions(classdef);

		for (PDefinition def : definitions)
		{
			if (def.getName() != null
					&& def.getName().matches(node.getOpname()))
			{
				opfound++;

				if (!question.assistantFactory.createPDefinitionAssistant().isCallableOperation(def))
				{
					TypeCheckerErrors.report(3042, node.getOpname()
							+ " is not an explicit operation", node.getOpname().getLocation(), node.getOpname());
				}

				if (isStatic != null
						&& isStatic != question.assistantFactory.createPDefinitionAssistant().isStatic(def))
				{
					TypeCheckerErrors.report(3323, "Overloaded operation cannot mix static and non-static", node.getLocation(), node.getOpname());
				}

				if (def.getAccess().getPure())
				{
					TypeCheckerErrors.report(3340, "Pure operation cannot have permission predicate",
						node.getOpname().getLocation(), node.getOpname());
				}

				isStatic = question.assistantFactory.createPDefinitionAssistant().isStatic(def);
			}

			if (def instanceof APerSyncDefinition)
			{
				APerSyncDefinition psd = (APerSyncDefinition) def;

				if (psd.getOpname().equals(node.getOpname()))
				{
					perfound++;
				}
			}
		}

		ILexNameToken opname = node.getOpname();

		if (opfound == 0)
		{
			TypeCheckerErrors.report(3043, opname + " is not in scope", opname.getLocation(), opname);
		} else if (opfound > 1)
		{
			TypeCheckerErrors.warning(5003, "Permission guard of overloaded operation", opname.getLocation(), opname);
		}

		if (perfound != 1)
		{
			TypeCheckerErrors.report(3044, "Duplicate permission guard found for "
					+ opname, opname.getLocation(), opname);
		}

		if (opname.getName().equals(classdef.getName().getName()))
		{
			TypeCheckerErrors.report(3045, "Cannot put guard on a constructor", opname.getLocation(), opname);
		}

		FlatCheckedEnvironment local = new FlatCheckedEnvironment(question.assistantFactory, node, base, NameScope.NAMESANDSTATE);
		local.setEnclosingDefinition(node); // Prevent op calls

		if (isStatic != null)
		{
			local.setStatic(isStatic);
		}

		PType rt = node.getGuard().apply(THIS, new TypeCheckInfo(question.assistantFactory, local, NameScope.NAMESANDSTATE));

		if (!question.assistantFactory.createPTypeAssistant().isType(rt, ABooleanBasicType.class))
		{
			TypeCheckerErrors.report(3046, "Guard is not a boolean expression", node.getGuard().getLocation(), node.getGuard());
		}

		node.setType(rt);
		return node.getType();
	}

	@Override
	public PType caseARenamedDefinition(ARenamedDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{

		node.setType(node.getDef().apply(THIS, question));
		return node.getType();
	}

	@Override
	public PType caseAStateDefinition(AStateDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{

		Environment base = question.env;

		if (base.findStateDefinition() != node)
		{
			TypeCheckerErrors.report(3047, "Only one state definition allowed per module", node.getLocation(), node);
			return null;
		}

		for (PDefinition def : node.getStateDefs())
		{
			if (!def.getName().getOld()) // Don't check old names
			{
				question.assistantFactory.getTypeComparator().checkComposeTypes(def.getType(), question.env, false);
			}
		}

		question.assistantFactory.createPDefinitionListAssistant().typeCheck(node.getStateDefs(), THIS, question);

		if (node.getInvdef() != null)
		{
			node.getInvdef().apply(THIS, question);
		}

		if (node.getInitdef() != null)
		{
			node.getInitdef().apply(THIS, question);
		}

		return null;
	}

	@Override
	public PType caseAThreadDefinition(AThreadDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{

		question.scope = NameScope.NAMESANDSTATE;
		FlatEnvironment local = new FlatEnvironment(question.assistantFactory, question.assistantFactory.createPDefinitionAssistant().getSelfDefinition(node), question.env);

		PType rt = node.getStatement().apply(THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope));

		if (!(rt instanceof AVoidType) && !(rt instanceof AUnknownType))
		{
			TypeCheckerErrors.report(3049, "Thread statement/operation must not return a value", node.getLocation(), node);
		}

		node.setType(rt);
		node.getOperationDef().setBody(node.getStatement().clone());// This
																	// operation
																	// is a
																	// wrapper
																	// for the
																	// thread
		return rt;
	}

	@Override
	public PType caseATypeDefinition(ATypeDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{
		if (node.getInvdef() != null)
		{
			question.scope = NameScope.NAMES;
			node.getInvdef().apply(THIS, question);
		}

		PType type = question.assistantFactory.createPDefinitionAssistant().getType(node);
		node.setType(type);

		// We have to do the "top level" here, rather than delegating to the types
		// because the definition pointer from these top level types just refers
		// to the definition we are checking, which is never "narrower" than itself.
		// See the narrowerThan method in NamedType and RecordType.

		if (type instanceof ANamedInvariantType)
		{
			ANamedInvariantType ntype = (ANamedInvariantType) type;

			// Rebuild the compose definitions, after we check whether they already exist
			node.getComposeDefinitions().clear();

			for (PType compose : question.assistantFactory.getTypeComparator().checkComposeTypes(ntype.getType(), question.env, true))
			{
				ARecordInvariantType rtype = (ARecordInvariantType) compose;
				PDefinition cdef = AstFactory.newATypeDefinition(rtype.getName(), rtype, null, null);
				cdef.setAccess(node.getAccess().clone());
				node.getComposeDefinitions().add(cdef);
				rtype.getDefinitions().get(0).setAccess(node.getAccess().clone());
			}

			if (question.assistantFactory.createPTypeAssistant().narrowerThan(ntype.getType(), node.getAccess()))
			{
				TypeCheckerErrors.report(3321, "Type component visibility less than type's definition", node.getLocation(), node);
			}
		} else if (type instanceof ARecordInvariantType)
		{
			ARecordInvariantType rtype = (ARecordInvariantType) type;

			for (AFieldField field : rtype.getFields())
			{
				question.assistantFactory.getTypeComparator().checkComposeTypes(field.getType(), question.env, false);

				if (question.assistantFactory.createPTypeAssistant().narrowerThan(field.getType(), node.getAccess()))
				{
					TypeCheckerErrors.report(3321, "Field type visibility less than type's definition", field.getTagname().getLocation(), field.getTagname());
				}
			}
		}

		return node.getType();

	}

	@Override
	public PType caseAUntypedDefinition(AUntypedDefinition node,
			TypeCheckInfo question)
	{

		assert false : "Can't type check untyped definition?";
		return null;
	}

	@Override
	public PType caseAValueDefinition(AValueDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{
		if (node.getType() != null)
		{
			question.assistantFactory.getTypeComparator().checkComposeTypes(node.getType(), question.env, false);
		}

		// Enable constraint checking
		question = question.newConstraint(node.getType());

		question.qualifiers = null;
		ExcludedDefinitions.setExcluded(node.getDefs());
		PType expType = node.getExpression().apply(THIS, question);
		ExcludedDefinitions.clearExcluded();
		node.setExpType(expType);
		PType type = node.getType(); // PDefinitionAssistant.getType(node);
		
		if (expType instanceof AUnknownType)
		{
			node.setPass(Pass.FINAL);	// Do it again later
		}
		
		if (expType instanceof AVoidType)
		{
			TypeCheckerErrors.report(3048, "Expression does not return a value", node.getExpression().getLocation(), node.getExpression());
		} else if (type != null && !(type instanceof AUnknownType))
		{
			if (!question.assistantFactory.getTypeComparator().compatible(type, expType))
			{
				TypeCheckerErrors.report(3051, "Expression does not match declared type", node.getLocation(), node);
				TypeCheckerErrors.detail2("Declared", type, "Expression", expType);
			}
		} else
		{
			type = expType;
			node.setType(expType);
		}

		Environment base = question.env;

		if (base.isVDMPP() && type instanceof ANamedInvariantType)
		{
			ANamedInvariantType named = (ANamedInvariantType) type;
			PDefinition typedef = base.findType(named.getName(), node.getLocation().getModule());

			if (typedef == null)
			{
				TypeCheckerErrors.report(2048, "Cannot find symbol "
						+ named.getName().toString(), named.getLocation(), named);
				return node.getType();
			}

			if (question.assistantFactory.createPAccessSpecifierAssistant().narrowerThan(typedef.getAccess(), node.getAccess()))
			{
				TypeCheckerErrors.report(3052, "Value type visibility less than value definition", node.getLocation(), node);
			}
		}

		node.apply(question.assistantFactory.getDefinitionTypeResolver(), new DefinitionTypeResolver.NewQuestion(THIS, question));
		// PPatternAssistantTC.typeResolve(pattern, THIS, question);
		// question.assistantFactory.getTypeResolver().updateDefs(node, question);
		question.qualifiers = null;
		question.assistantFactory.createPDefinitionListAssistant().typeCheck(node.getDefs(), THIS, question);
		return node.getType();
	}

	@Override
	public PType caseALetDefBindingTraceDefinition(
			ALetDefBindingTraceDefinition node, TypeCheckInfo question)
			throws AnalysisException
	{
		return typeCheckLet(node, node.getLocalDefs(), node.getBody(), question);
	}

	@Override
	public PType caseALetBeStBindingTraceDefinition(
			ALetBeStBindingTraceDefinition node, TypeCheckInfo question)
			throws AnalysisException
	{
		Entry<PType, AMultiBindListDefinition> res = typecheckLetBeSt(node, node.getLocation(), node.getBind(), node.getStexp(), node.getBody(), question);
		node.setDef(res.getValue());
		return res.getKey();
	}

	@Override
	public PType caseARepeatTraceDefinition(ARepeatTraceDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{
		if (node.getFrom() > node.getTo())
		{
			TypeCheckerErrors.report(3277, "Trace repeat illegal values", node.getLocation(), node);
		}

		// Environment local = question.env;
		return node.getCore().apply(THIS, question);

	}

	@Override
	public PType caseAConcurrentExpressionTraceCoreDefinition(
			AConcurrentExpressionTraceCoreDefinition node,
			TypeCheckInfo question) throws AnalysisException
	{
		for (PTraceDefinition d : node.getDefs())
		{
			d.apply(THIS, question);
		}

		return null;
	}

	@Override
	public PType caseABracketedExpressionTraceCoreDefinition(
			ABracketedExpressionTraceCoreDefinition node, TypeCheckInfo question)
			throws AnalysisException
	{
		for (ATraceDefinitionTerm term : node.getTerms())
		{
			for (PTraceDefinition def : term.getList())
			{
				def.apply(THIS, question);
			}
		}

		return null;
	}

	@Override
	public PType caseAApplyExpressionTraceCoreDefinition(
			AApplyExpressionTraceCoreDefinition node, TypeCheckInfo question)
			throws AnalysisException
	{
		return node.getCallStatement().apply(THIS, question);

	}
	
	public void typeCheck(List<PTraceDefinition> term,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{

		for (PTraceDefinition def : term)
		{
			def.apply(rootVisitor, question);
		}

	}
	
	public Collection<? extends PDefinition> getDefinitions(
			APatternListTypePair pltp, NameScope scope, ITypeCheckerAssistantFactory assistantFactory)
	{
		List<PDefinition> list = new Vector<PDefinition>();

		for (PPattern p : pltp.getPatterns())
		{
			list.addAll(assistantFactory.createPPatternAssistant().getDefinitions(p, pltp.getType(), scope));
		}

		return list;
	}
	
	public void typeResolve(AExternalClause clause,
			IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question)
	{
		clause.setType(question.assistantFactory.createPTypeAssistant().typeResolve(clause.getType(), null, rootVisitor, question));

	}

}
