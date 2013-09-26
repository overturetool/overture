package org.overture.typechecker.utilities;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAdaptor;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
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
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.PType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.FlatEnvironment;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.AExplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AExplicitOperationDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImplicitOperationDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AInstanceVariableDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ALocalDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ARenamedDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AStateDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ATypeDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AValueDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionListAssistantTC;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternListAssistantTC;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

/**
 * This class implements a way to resolve types from a node in the AST
 * 
 * @author kel
 */

public class TypeResolver extends QuestionAdaptor<TypeResolver.NewQuestion>
{
	public static class NewQuestion
	{
		final QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor;
		final TypeCheckInfo question;
		
		public NewQuestion(QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,TypeCheckInfo question)
		{
			this.rootVisitor = rootVisitor;
			this.question = question;
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	protected ITypeCheckerAssistantFactory af;

	public TypeResolver(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public void defaultSClassDefinition(SClassDefinition node,
			NewQuestion question) throws AnalysisException
	{
		Environment cenv = new FlatEnvironment(question.question.assistantFactory, node.getDefinitions(), question.question.env);
		PDefinitionListAssistantTC.typeResolve(node.getDefinitions(), question.rootVisitor, new TypeCheckInfo(question.question.assistantFactory, cenv));
	}
	
	@Override
	public void caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node, NewQuestion question)
			throws AnalysisException
	{
		//AExplicitFunctionDefinitionAssistantTC.typeResolve(node, question.rootVisitor, question.question);
		if (node.getTypeParams().size() != 0)
		{
			FlatCheckedEnvironment params = new FlatCheckedEnvironment(question.question.assistantFactory, AExplicitFunctionDefinitionAssistantTC.getTypeParamDefinitions(node), question.question.env, NameScope.NAMES);

			TypeCheckInfo newQuestion = new TypeCheckInfo(question.question.assistantFactory, params, question.question.scope);

			node.setType(PTypeAssistantTC.typeResolve(question.question.assistantFactory.createPDefinitionAssistant().getType(node), null, question.rootVisitor, newQuestion));
		} else
		{
			//node.setType(PTypeAssistantTC.typeResolve(question.question.assistantFactory.createPDefinitionAssistant().getType(node), null, question.rootVisitor, question));
			node.setType(af.createPTypeAssistant().typeResolve(node.getType(), null, question.rootVisitor, question.question));
		}

		if (question.question.env.isVDMPP())
		{
			AFunctionType fType = (AFunctionType) question.question.assistantFactory.createPDefinitionAssistant().getType(node);
			node.getName().setTypeQualifier(fType.getParameters());

			if (node.getBody() instanceof ASubclassResponsibilityExp)
			{
				node.getClassDefinition().setIsAbstract(true);
			}
		}

		if (node.getBody() instanceof ASubclassResponsibilityExp
				|| node.getBody() instanceof ANotYetSpecifiedExp)
		{
			node.setIsUndefined(true);
		}

		if (node.getPrecondition() != null)
		{
			//PDefinitionAssistantTC.typeResolve(node.getPredef(), rootVisitor, question);
			node.getPredef().apply(this, question);
		}

		if (node.getPostcondition() != null)
		{
			//PDefinitionAssistantTC.typeResolve(node.getPostdef(), rootVisitor, question);
			node.getPostdef().apply(this, question);
		}

		for (List<PPattern> pp : node.getParamPatternList())
		{
			PPatternListAssistantTC.typeResolve(pp, question.rootVisitor, question.question);
		}

	}
	
	@Override
	public void caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, NewQuestion question)
			throws AnalysisException
	{
		AExplicitOperationDefinitionAssistantTC.typeResolve(node, question.rootVisitor, question.question);
	}
	
	@Override
	public void caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node, NewQuestion question)
			throws AnalysisException
	{
		AImplicitFunctionDefinitionAssistantTC.typeResolve(node, question.rootVisitor, question.question);
	}
	
	@Override
	public void caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node, NewQuestion question)
			throws AnalysisException
	{
		AImplicitOperationDefinitionAssistantTC.typeResolve(node, question.rootVisitor, question.question);
	}
	
	@Override
	public void caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node, NewQuestion question)
			throws AnalysisException
	{
		AInstanceVariableDefinitionAssistantTC.typeResolve(node, question.rootVisitor, question.question);
	}
	
	@Override
	public void caseALocalDefinition(ALocalDefinition node, NewQuestion question)
			throws AnalysisException
	{
		ALocalDefinitionAssistantTC.typeResolve(node, question.rootVisitor, question.question);
	}
	
	@Override
	public void caseARenamedDefinition(ARenamedDefinition node,
			NewQuestion question) throws AnalysisException
	{
		ARenamedDefinitionAssistantTC.typeResolve(node, question.rootVisitor, question.question);
	}

	@Override
	public void caseAStateDefinition(AStateDefinition node, NewQuestion question)
			throws AnalysisException
	{
		AStateDefinitionAssistantTC.typeResolve(node, question.rootVisitor, question.question);
	}
	
	@Override
	public void caseATypeDefinition(ATypeDefinition node, NewQuestion question)
			throws AnalysisException
	{
		ATypeDefinitionAssistantTC.typeResolve(node, question.rootVisitor,question.question);
	}
	@Override
	public void caseAValueDefinition(AValueDefinition node, NewQuestion question)
			throws AnalysisException
	{
		AValueDefinitionAssistantTC.typeResolve(node, question.rootVisitor, question.question);
	}
	@Override
	public void defaultPDefinition(PDefinition node, NewQuestion question)
			throws AnalysisException
	{
		return;
	}

}
