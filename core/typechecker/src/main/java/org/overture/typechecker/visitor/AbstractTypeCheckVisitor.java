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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.annotations.PAnnotation;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.statements.PStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.PType;
import org.overture.ast.util.PTypeSet;
import org.overture.config.Settings;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.FlatEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.annotations.TCAnnotation;
import org.overture.typechecker.utilities.type.QualifiedDefinition;

public class AbstractTypeCheckVisitor extends
		QuestionAnswerAdaptor<TypeCheckInfo, PType>
{

	public AbstractTypeCheckVisitor(
			IQuestionAnswer<TypeCheckInfo, PType> visitor)
	{
		super(visitor);
	}

	public AbstractTypeCheckVisitor()
	{
		super();
	}

	@Override
	public PType createNewReturnValue(INode node, TypeCheckInfo question)
	{
		return null;
	}

	@Override
	public PType createNewReturnValue(Object node, TypeCheckInfo question)
	{
		return null;
	}

	@Override
	public PType defaultINode(INode node, TypeCheckInfo question)
			throws AnalysisException
	{
		return THIS.defaultINode(node, question);
	}

	protected PType typeCheckIf(ILexLocation ifLocation, PExp testExp,
			INode thenNode, List<? extends INode> elseIfNodeList,
			INode elseNode, TypeCheckInfo question) throws AnalysisException
	{
		boolean isExpression = testExp.parent() instanceof PExp;

		question.qualifiers = null;

		PType test = testExp.apply(THIS, question.newConstraint(null));

		if (!question.assistantFactory.createPTypeAssistant().isType(test, ABooleanBasicType.class))
		{
			TypeCheckerErrors.report((isExpression ? 3108 : 3224), "If expression is not boolean", testExp.getLocation(), testExp);
		}

		List<QualifiedDefinition> qualified = testExp.apply(question.assistantFactory.getQualificationVisitor(), question);

		for (QualifiedDefinition qdef : qualified)
		{
			qdef.qualifyType();
		}

		PTypeSet rtypes = new PTypeSet(question.assistantFactory);
		question.qualifiers = null;
		rtypes.add(thenNode.apply(THIS, question));

		for (QualifiedDefinition qdef : qualified)
		{
			qdef.resetType();
		}

		if (elseIfNodeList != null)
		{
			for (INode stmt : elseIfNodeList)
			{
				question.qualifiers = null;
				rtypes.add(stmt.apply(THIS, question));
			}
		}

		if (elseNode != null)
		{
			question.qualifiers = null;
			rtypes.add(elseNode.apply(THIS, question));
		} else
		{
			// If the else case is empty then it is a statement and its type is void
			rtypes.add(AstFactory.newAVoidType(ifLocation));
			question.assistantFactory.createPTypeAssistant().checkReturnType(question.returnType, rtypes.getType(ifLocation), question.mandatory, ifLocation);
		}

		return rtypes.getType(ifLocation);

	}

	/**
	 * Type checks a AElseIf node
	 * 
	 * @param elseIfNode
	 * @param elseIfLocation
	 * @param test
	 * @param thenNode
	 * @param question
	 * @return
	 * @throws AnalysisException
	 */
	PType typeCheckAElseIf(INode elseIfNode, ILexLocation elseIfLocation,
			INode test, INode thenNode, TypeCheckInfo question)
			throws AnalysisException
	{
		if (!question.assistantFactory.createPTypeAssistant().isType(test.apply(THIS, question.newConstraint(null)), ABooleanBasicType.class))
		{
			boolean isExpression = elseIfNode.parent() instanceof PExp;
			TypeCheckerErrors.report((isExpression ? 3086 : 3218), "Expression is not boolean", elseIfLocation, elseIfNode);
		}

		List<QualifiedDefinition> qualified = test.apply(question.assistantFactory.getQualificationVisitor(), question);

		for (QualifiedDefinition qdef : qualified)
		{
			qdef.qualifyType();
		}

		PType type = thenNode.apply(THIS, question);

		for (QualifiedDefinition qdef : qualified)
		{
			qdef.resetType();
		}

		return type;
	}

	PType typeCheckANotYetSpecifiedExp(INode node, ILexLocation location)
	{
		return AstFactory.newAUnknownType(location);// Because we terminate anyway
	}

	/**
	 * Type checks a let node
	 * 
	 * @param node
	 * @param localDefs
	 * @param body
	 * @param question
	 * @return
	 * @throws AnalysisException
	 */
	protected PType typeCheckLet(INode node, LinkedList<PDefinition> localDefs,
			INode body, TypeCheckInfo question, boolean statement) throws AnalysisException
	{
		Environment local = null;

		if (statement && Settings.strict)
		{
			local = new FlatEnvironment(question.assistantFactory, question.env, true, false);
		}
		else
		{
			local = question.env;
		}

		for (PDefinition d : localDefs)
		{
			if (d instanceof AExplicitFunctionDefinition)
			{
				// Functions' names are in scope in their bodies, whereas
				// simple variable declarations aren't

				local = new FlatCheckedEnvironment(question.assistantFactory, d, local, question.scope); // cumulative
				question.assistantFactory.createPDefinitionAssistant().implicitDefinitions(d, local);

				question.assistantFactory.createPDefinitionAssistant().typeResolve(d, THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope, question.qualifiers));

				if (question.env.isVDMPP())
				{
					SClassDefinition cdef = question.env.findClassDefinition();
					// question.assistantFactory.createPDefinitionAssistant().setClassDefinition(d, cdef);
					d.setClassDefinition(cdef);
					d.setAccess(question.assistantFactory.createPAccessSpecifierAssistant().getStatic(d, true));
				}

				d.apply(THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope, question.qualifiers));
			} else
			{
				question.assistantFactory.createPDefinitionAssistant().implicitDefinitions(d, local);
				question.assistantFactory.createPDefinitionAssistant().typeResolve(d, THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope, question.qualifiers));
				d.apply(THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope).newModule(question.fromModule));
				local = new FlatCheckedEnvironment(question.assistantFactory, d, local, question.scope); // cumulative
			}
		}
		
		if (statement)
		{
			local = new FlatEnvironment(question.assistantFactory, local, false, false);
		}

		PType r = body.apply(THIS, new TypeCheckInfo(question.assistantFactory, local, question.scope, null, question.constraint, null, question.fromModule, question.mandatory));
		local.unusedCheck(question.env);
		return r;
	}

	/**
	 * Type check method for let be such that
	 * @param node
	 * @param nodeLocation
	 * @param bind
	 * @param suchThat
	 * @param body
	 * @param question
	 * @return a pair of the type and definition
	 * @throws AnalysisException
	 */
	protected Map.Entry<PType, AMultiBindListDefinition> typecheckLetBeSt(
			INode node, ILexLocation nodeLocation, PMultipleBind bind,
			PExp suchThat, INode body, TypeCheckInfo question, boolean statement)
			throws AnalysisException
	{
		final PDefinition def = AstFactory.newAMultiBindListDefinition(nodeLocation, question.assistantFactory.createPMultipleBindAssistant().getMultipleBindList((PMultipleBind) bind));

		Environment funcEnv = null;

		if (statement && Settings.strict)
		{
			funcEnv = new FlatEnvironment(question.assistantFactory, question.env, true, false);
		}
		else
		{
			funcEnv = question.env;
		}

		def.apply(THIS, question.newConstraint(null).newInfo(funcEnv));

		List<PDefinition> qualified = new Vector<PDefinition>();

		for (PDefinition d: question.assistantFactory.createPDefinitionAssistant().getDefinitions(def))
		{
			PDefinition copy = d.clone();
			copy.setNameScope(NameScope.LOCAL);
			qualified.add(copy);
		}
		
		Environment local = new FlatCheckedEnvironment(question.assistantFactory, qualified, question.env, question.scope);

		TypeCheckInfo newInfo = new TypeCheckInfo(question.assistantFactory, local, question.scope, question.qualifiers, question.constraint, null, question.fromModule, question.mandatory);

		if (suchThat != null
				&& !question.assistantFactory.createPTypeAssistant().isType(suchThat.apply(THIS, newInfo.newConstraint(null)), ABooleanBasicType.class))
		{
			boolean isExpression = node instanceof PExp;
			TypeCheckerErrors.report((isExpression ? 3117 : 3225), "Such that clause is not boolean", nodeLocation, node);
		}

		newInfo.qualifiers = null;
		final PType r = body.apply(THIS, newInfo);
		local.unusedCheck();

		return new Map.Entry<PType, AMultiBindListDefinition>()
		{

			@Override
			public AMultiBindListDefinition setValue(
					AMultiBindListDefinition value)
			{
				return null;
			}

			@Override
			public AMultiBindListDefinition getValue()
			{
				return (AMultiBindListDefinition) def;
			}

			@Override
			public PType getKey()
			{
				return r;
			}
		};
	}
	
	/**
	 * Annotation before processing.
	 */
	
	protected void beforeAnnotations(List<PAnnotation> annotations, INode node, TypeCheckInfo question) throws AnalysisException
	{
		for (PAnnotation annotation: annotations)
		{
			beforeAnnotation(annotation, node, question);
		}
	}
	
	protected void beforeAnnotation(PAnnotation annotation, INode node, TypeCheckInfo question)
	{
		if (annotation.getImpl() instanceof TCAnnotation)
		{
			TCAnnotation impl = (TCAnnotation)annotation.getImpl();
			
			// This is not as ugly as multiple overloaded beforeAnotation and beforeAnnotations!
			if (node instanceof PDefinition)
			{
				impl.tcBefore((PDefinition)node, question);
			}
			else if (node instanceof PExp)
			{
				impl.tcBefore((PExp)node, question);
			}
			else if (node instanceof PStm)
			{
				impl.tcBefore((PStm)node, question);
			}
			else if (node instanceof AModuleModules)
			{
				impl.tcBefore((AModuleModules)node, question);
			}
			else if (node instanceof SClassDefinition)
			{
				impl.tcBefore((SClassDefinition)node, question);
			}
			else
			{
				System.err.println("Cannot apply annoation to " + node.getClass().getSimpleName());
			}
		}
	}
	
	/**
	 * After annotation processing.
	 */
	
	protected void afterAnnotations(List<PAnnotation> annotations, INode node, TypeCheckInfo question) throws AnalysisException
	{
		for (PAnnotation annotation: annotations)
		{
			afterAnnotation(annotation, node, question);
		}
	}

	protected void afterAnnotation(PAnnotation annotation, INode node, TypeCheckInfo question)
	{
		if (annotation.getImpl() instanceof TCAnnotation)
		{
			TCAnnotation impl = (TCAnnotation)annotation.getImpl();
			
			// This is not as ugly as multiple overloaded beforeAnotation and beforeAnnotations!
			if (node instanceof PDefinition)
			{
				impl.tcAfter((PDefinition)node, question);
			}
			else if (node instanceof PExp)
			{
				impl.tcAfter((PExp)node, question);
			}
			else if (node instanceof PStm)
			{
				impl.tcAfter((PStm)node, question);
			}
			else if (node instanceof AModuleModules)
			{
				impl.tcAfter((AModuleModules)node, question);
			}
			else if (node instanceof SClassDefinition)
			{
				impl.tcAfter((SClassDefinition)node, question);
			}
			else
			{
				System.err.println("Cannot apply annoation to " + node.getClass().getSimpleName());
			}
		}
	}
}
