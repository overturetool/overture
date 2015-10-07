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
package org.overture.typechecker.utilities;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAdaptor;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.ALocalDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.ANotYetSpecifiedExp;
import org.overture.ast.expressions.ASubclassResponsibilityExp;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.FlatEnvironment;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class implements a way to resolve types from a node in the AST
 * 
 * @author kel
 */

public class DefinitionTypeResolver extends
		QuestionAdaptor<DefinitionTypeResolver.NewQuestion>
{
	public static class NewQuestion
	{
		public final IQuestionAnswer<TypeCheckInfo, PType> rootVisitor;
		public final TypeCheckInfo question;

		public NewQuestion(IQuestionAnswer<TypeCheckInfo, PType> rootVisitor,
				TypeCheckInfo question)
		{
			this.rootVisitor = rootVisitor;
			this.question = question;
		}
	}

	protected ITypeCheckerAssistantFactory af;

	public DefinitionTypeResolver(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public void defaultSClassDefinition(SClassDefinition node,
			NewQuestion question) throws AnalysisException
	{
		Environment cenv = new FlatEnvironment(question.question.assistantFactory, node.getDefinitions(), question.question.env);
		af.createPDefinitionListAssistant().typeResolve(node.getDefinitions(), question.rootVisitor, new TypeCheckInfo(question.question.assistantFactory, cenv));
	}

	@Override
	public void caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node, NewQuestion question)
			throws AnalysisException
	{
		if (node.getTypeParams().size() != 0)
		{
			FlatCheckedEnvironment params = new FlatCheckedEnvironment(question.question.assistantFactory, af.createAExplicitFunctionDefinitionAssistant().getTypeParamDefinitions(node), question.question.env, NameScope.NAMES);

			TypeCheckInfo newQuestion = new TypeCheckInfo(question.question.assistantFactory, params, question.question.scope);

			node.setType(af.createPTypeAssistant().typeResolve(question.question.assistantFactory.createPDefinitionAssistant().getType(node), null, question.rootVisitor, newQuestion));
		} else
		{
			node.setType(af.createPTypeAssistant().typeResolve(node.getType(), null, question.rootVisitor, question.question));
		}

		if (question.question.env.isVDMPP())
		{
			AFunctionType fType = (AFunctionType) question.question.assistantFactory.createPDefinitionAssistant().getType(node);
			node.getName().setTypeQualifier(fType.getParameters());
		}

		if (node.getBody() instanceof ASubclassResponsibilityExp
				|| node.getBody() instanceof ANotYetSpecifiedExp)
		{
			node.setIsUndefined(true);
		}

		if (node.getPrecondition() != null)
		{
			// PDefinitionAssistantTC.typeResolve(node.getPredef(), rootVisitor, question);
			node.getPredef().apply(this, question);
		}

		if (node.getPostcondition() != null)
		{
			// PDefinitionAssistantTC.typeResolve(node.getPostdef(), rootVisitor, question);
			node.getPostdef().apply(this, question);
		}

		for (List<PPattern> pp : node.getParamPatternList())
		{
			af.createPPatternListAssistant().typeResolve(pp, question.rootVisitor, question.question);
		}

	}

	@Override
	public void caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, NewQuestion question)
			throws AnalysisException
	{
		node.setType(af.createPTypeAssistant().typeResolve(node.getType(), null, question.rootVisitor, question.question));

		if (question.question.env.isVDMPP())
		{
			node.getName().setTypeQualifier(((AOperationType) node.getType()).getParameters());

			if (node.getName().getName().equals(node.getClassDefinition().getName().getName()))
			{
				node.setIsConstructor(true);
				node.getClassDefinition().setHasContructors(true);
			}
		}

		if (node.getPrecondition() != null)
		{
			node.getPredef().apply(this, question);
		}

		if (node.getPostcondition() != null)
		{
			node.getPostdef().apply(this, question);
		}

		for (PPattern p : node.getParameterPatterns())
		{
			af.createPPatternAssistant().typeResolve(p, question.rootVisitor, question.question);
		}
	}

	@Override
	public void caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node, NewQuestion question)
			throws AnalysisException
	{
		if (node.getTypeParams().size() > 0)
		{
			FlatCheckedEnvironment params = new FlatCheckedEnvironment(af, af.createAImplicitFunctionDefinitionAssistant().getTypeParamDefinitions(node), question.question.env, NameScope.NAMES);
			node.setType(af.createPTypeAssistant().typeResolve(af.createPDefinitionAssistant().getType(node), null, question.rootVisitor, new TypeCheckInfo(question.question.assistantFactory, params, question.question.scope, question.question.qualifiers)));
		} else
		{
			question.question.qualifiers = null;
			node.setType(af.createPTypeAssistant().typeResolve(af.createPDefinitionAssistant().getType(node), null, question.rootVisitor, question.question));
		}

		if (node.getResult() != null)
		{
			af.createAPatternTypePairAssistant().typeResolve(node.getResult(), question.rootVisitor, question.question);
			
		}

		if (question.question.env.isVDMPP())
		{
			AFunctionType fType = (AFunctionType) af.createPDefinitionAssistant().getType(node);
			node.getName().setTypeQualifier(fType.getParameters());
		}

		if (node.getBody() instanceof ASubclassResponsibilityExp
				|| node.getBody() instanceof ANotYetSpecifiedExp)
		{
			node.setIsUndefined(true);
		}

		if (node.getPrecondition() != null)
		{
			node.getPredef().apply(this, question);
		}

		if (node.getPostcondition() != null)
		{
			node.getPostdef().apply(this, question);
		}

		for (APatternListTypePair pltp : node.getParamPatterns())
		{
			pltp.apply(THIS, question);
		}
	}

	@Override
	public void caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node, NewQuestion question)
			throws AnalysisException
	{
		node.setType(af.createPTypeAssistant().typeResolve(node.getType(), null, question.rootVisitor, question.question));

		if (node.getResult() != null)
		{
			af.createAPatternTypePairAssistant().typeResolve(node.getResult(), question.rootVisitor, question.question);
		}

		if (question.question.env.isVDMPP())
		{
			node.getName().setTypeQualifier(((AOperationType) node.getType()).getParameters());

			if (node.getName().getName().equals(node.getClassDefinition().getName().getName()))
			{
				node.setIsConstructor(true);
				node.getClassDefinition().setHasContructors(true);
			}
		}

		if (node.getPrecondition() != null)
		{
			node.getPredef().apply(this, question);
		}

		if (node.getPostcondition() != null)
		{
			node.getPostdef().apply(this, question);
		}

		for (APatternListTypePair ptp : node.getParameterPatterns())
		{
			ptp.apply(THIS, question);
		}
	}

	@Override
	public void caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node, NewQuestion question)
			throws AnalysisException
	{

		try
		{
			node.setType(af.createPTypeAssistant().typeResolve(node.getType(), null, question.rootVisitor, question.question));
		} catch (TypeCheckException e)
		{
			af.createPTypeAssistant().unResolve(node.getType());
			throw e;
		}
	}

	@Override
	public void caseALocalDefinition(ALocalDefinition node, NewQuestion question)
			throws AnalysisException
	{
		if (node.getType() != null)
		{
			node.setType(af.createPTypeAssistant().typeResolve(question.question.assistantFactory.createPDefinitionAssistant().getType(node), null, question.rootVisitor, question.question));
		}

	}

	@Override
	public void caseARenamedDefinition(ARenamedDefinition node,
			NewQuestion question) throws AnalysisException
	{
		node.getDef().apply(this, question);
	}

	@Override
	public void caseAStateDefinition(AStateDefinition node, NewQuestion question)
			throws AnalysisException
	{
		for (AFieldField f : node.getFields())
		{
			try
			{
				f.apply(THIS, new NewQuestion(question.rootVisitor, question.question));
			} catch (TypeCheckException e)
			{
				question.question.assistantFactory.createPTypeAssistant().unResolve(f.getType());
				throw e;
			}
		}

		node.setRecordType(af.createPTypeAssistant().typeResolve(node.getRecordType(), null, question.rootVisitor, question.question));

		if (node.getInvPattern() != null)
		{
			node.getInvdef().apply(this, question);

			ARecordInvariantType rtype = (ARecordInvariantType) node.getRecordType();
			rtype.setInvDef(node.getInvdef());
		}

		if (node.getInitPattern() != null)
		{
			node.getInitdef().apply(this, question);
		}

	}

	@Override
	public void caseATypeDefinition(ATypeDefinition node, NewQuestion question)
			throws AnalysisException
	{
		try
		{
			node.setInfinite(false);
			node.setInvType((SInvariantType) af.createPTypeAssistant().typeResolve((SInvariantType) node.getInvType(), node, question.rootVisitor, question.question));

			if (node.getInfinite())
			{
				TypeCheckerErrors.report(3050, "Type '" + node.getName()
						+ "' is infinite", node.getLocation(), node);
			}

			// set type before in case the invdef uses a type defined in this one
			node.setType(node.getInvType());

			if (node.getInvdef() != null)
			{
				node.getInvdef().apply(this, question);
				af.createPPatternAssistant().typeResolve(node.getInvPattern(), question.rootVisitor, question.question);
			}

			node.setType(node.getInvType());

			if (!node.getComposeDefinitions().isEmpty())
			{
				for (PDefinition compose : node.getComposeDefinitions())
				{
					compose.apply(this, question);
				}
			}

		} catch (TypeCheckException e)
		{
			af.createPTypeAssistant().unResolve(node.getInvType());
			throw e;
		}
	}

	@Override
	public void caseAValueDefinition(AValueDefinition node, NewQuestion question)
			throws AnalysisException
	{
		if (node.getType() != null)
		{
			node.setType(af.createPTypeAssistant().typeResolve(node.getType(), null, question.rootVisitor, question.question));
			af.createPPatternAssistant().typeResolve(node.getPattern(), question.rootVisitor, question.question);
			// af.createAValueDefinitionAssistant().updateDefs(node, question.question);
			updateDefs(node, question.question);
		}
	}

	public void updateDefs(AValueDefinition node, TypeCheckInfo question)
	{
		PType type = node.getType();
		PPattern pattern = node.getPattern();

		List<PDefinition> newdefs = af.createPPatternAssistant().getDefinitions(pattern, type, node.getNameScope());

		// The untyped definitions may have had "used" markers, so we copy
		// those into the new typed definitions, lest we get warnings. We
		// also mark the local definitions as "ValueDefintions" (proxies),
		// so that classes can be constructed correctly (values are statics).

		for (PDefinition d : newdefs)
		{
			for (PDefinition u : node.getDefs())
			{
				if (u.getName().equals(d.getName()))
				{
					if (af.createPDefinitionAssistant().isUsed(u))
					{
						af.createPDefinitionAssistant().markUsed(d);
					}

					break;
				}
			}

			ALocalDefinition ld = (ALocalDefinition) d;
			setValueDefinition(ld);
		}

		node.setDefs(newdefs);
		List<PDefinition> defs = node.getDefs();
		af.createPDefinitionListAssistant().setAccessibility(defs, node.getAccess().clone());
		af.createPDefinitionListAssistant().setClassDefinition(defs, node.getClassDefinition());
	}

	@Override
	public void caseAPatternListTypePair(APatternListTypePair pltp,
			NewQuestion question) throws AnalysisException
	{
		af.createPPatternListAssistant().typeResolve(pltp.getPatterns(), question.rootVisitor, question.question);
		PType type = af.createPTypeAssistant().typeResolve(pltp.getType(), null, question.rootVisitor, question.question);
		pltp.setType(type);
	}

	@Override
	public void defaultPDefinition(PDefinition node, NewQuestion question)
			throws AnalysisException
	{
		return;
	}
	
	public void setValueDefinition(ALocalDefinition ld)
	{
		ld.setValueDefinition(true);

	}


}
