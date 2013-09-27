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
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.ASubclassResponsibilityStm;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AFunctionType;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SInvariantType;
import org.overture.typechecker.Environment;
import org.overture.typechecker.FlatCheckedEnvironment;
import org.overture.typechecker.FlatEnvironment;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.AExplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AValueDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionListAssistantTC;
import org.overture.typechecker.assistant.pattern.APatternTypePairAssistant;
import org.overture.typechecker.assistant.pattern.PPatternAssistantTC;
import org.overture.typechecker.assistant.pattern.PPatternListAssistantTC;
import org.overture.typechecker.assistant.type.AFieldFieldAssistantTC;
import org.overture.typechecker.assistant.type.APatternListTypePairAssistantTC;
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
		if (node.getTypeParams().size() != 0)
		{
			FlatCheckedEnvironment params = new FlatCheckedEnvironment(question.question.assistantFactory, AExplicitFunctionDefinitionAssistantTC.getTypeParamDefinitions(node), question.question.env, NameScope.NAMES);

			TypeCheckInfo newQuestion = new TypeCheckInfo(question.question.assistantFactory, params, question.question.scope);

			node.setType(af.createPTypeAssistant().typeResolve(question.question.assistantFactory.createPDefinitionAssistant().getType(node), null, question.rootVisitor, newQuestion));
		} else
		{
			//node.setType(PTypeAssistantTC.typeResolve(question.question.assistantFactory.createPDefinitionAssistant().getType(node), null, question.rootVisitor, question));
			node.setType(af.createPTypeAssistant().typeResolve(node.getType(), null, question.rootVisitor, question.question)); //FIXME: my way to rewrite the above line. Test shows that it is ok <- George Kanakis
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
		node.setType(af.createPTypeAssistant().typeResolve(node.getType(), null, question.rootVisitor, question.question));

		if (question.question.env.isVDMPP())
		{
			node.getName().setTypeQualifier(((AOperationType) node.getType()).getParameters());

			if (node.getBody() instanceof ASubclassResponsibilityStm)
			{
				node.getClassDefinition().setIsAbstract(true);
			}
		}

		if (node.getPrecondition() != null)
		{
			//PDefinitionAssistantTC.typeResolve(node.getPredef(), question.rootVisitor, question.question);
			node.getPredef().apply(this, question);
		}

		if (node.getPostcondition() != null)
		{
			//PDefinitionAssistantTC.typeResolve(node.getPostdef(), question.rootVisitor, question.question);
			node.getPostdef().apply(this, question);
		}

		for (PPattern p : node.getParameterPatterns())
		{
			PPatternAssistantTC.typeResolve(p, question.rootVisitor, question.question);
		}
	}
	
	@Override
	public void caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node, NewQuestion question)
			throws AnalysisException
	{
		if (node.getTypeParams().size() > 0)
		{
			FlatCheckedEnvironment params = new FlatCheckedEnvironment(af, AImplicitFunctionDefinitionAssistantTC.getTypeParamDefinitions(node), question.question.env, NameScope.NAMES);
			node.setType(af.createPTypeAssistant().typeResolve(af.createPDefinitionAssistant().getType(node), null, question.rootVisitor, new TypeCheckInfo(question.question.assistantFactory, params, question.question.scope, question.question.qualifiers)));
		} else
		{
			question.question.qualifiers = null;
			node.setType(af.createPTypeAssistant().typeResolve(af.createPDefinitionAssistant().getType(node), null, question.rootVisitor, question.question));
		}

		if (node.getResult() != null)
		{
			APatternTypePairAssistant.typeResolve(node.getResult(), question.rootVisitor, question.question);
		}

		if (question.question.env.isVDMPP())
		{
			AFunctionType fType = (AFunctionType) af.createPDefinitionAssistant().getType(node);
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
			//PDefinitionAssistantTC.typeResolve(d.getPredef(), rootVisitor, question);
			node.getPredef().apply(this, question);
		}

		if (node.getPostcondition() != null)
		{
			//PDefinitionAssistantTC.typeResolve(d.getPostdef(), rootVisitor, question);
			node.getPostdef().apply(this, question);
		}

		for (APatternListTypePair pltp : node.getParamPatterns())
		{
			APatternListTypePairAssistantTC.typeResolve(pltp, question.rootVisitor, question.question);
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
			APatternTypePairAssistant.typeResolve(node.getResult(), question.rootVisitor, question.question);
		}

		if (question.question.env.isVDMPP())
		{
			node.getName().setTypeQualifier(((AOperationType) node.getType()).getParameters());

			if (node.getBody() instanceof ASubclassResponsibilityStm)
			{
				node.getClassDefinition().setIsAbstract(true);
			}
		}

		if (node.getPrecondition() != null)
		{
			//PDefinitionAssistantTC.typeResolve(d.getPredef(), rootVisitor, question);
			node.getPredef().apply(this, question);
		}

		if (node.getPostcondition() != null)
		{
			//PDefinitionAssistantTC.typeResolve(d.getPostdef(), rootVisitor, question);
			node.getPostdef().apply(this, question);
		}

		for (APatternListTypePair ptp : node.getParameterPatterns())
		{
			APatternListTypePairAssistantTC.typeResolve(ptp, question.rootVisitor, question.question);
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
			PTypeAssistantTC.unResolve(node.getType());
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
		//PDefinitionAssistantTC.typeResolve(d.getDef(), rootVisitor, question);
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
				AFieldFieldAssistantTC.typeResolve(f, null, question.rootVisitor, question.question);
			} catch (TypeCheckException e)
			{
				AFieldFieldAssistantTC.unResolve(f);
				throw e;
			}
		}

		node.setRecordType(af.createPTypeAssistant().typeResolve(node.getRecordType(), null, question.rootVisitor, question.question));

		if (node.getInvPattern() != null)
		{
			//PDefinitionAssistantTC.typeResolve(node.getInvdef(), question.rootVisitor, question.question);
			node.getInvdef().apply(this, question);
		}

		if (node.getInitPattern() != null)
		{
			//PDefinitionAssistantTC.typeResolve(d.getInitdef(), rootVisitor, question);
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
				//PDefinitionAssistantTC.typeResolve(d.getInvdef(), rootVisitor, question);
				node.getInvdef().apply(this, question);
				PPatternAssistantTC.typeResolve(node.getInvPattern(), question.rootVisitor, question.question);
			}

			node.setType(node.getInvType());
		} catch (TypeCheckException e)
		{
			PTypeAssistantTC.unResolve(node.getInvType());
			throw e;
		}
	}
	@Override
	public void caseAValueDefinition(AValueDefinition node, NewQuestion question)
			throws AnalysisException
	{
		// d.setType(getType(d));
		if (node.getType() != null)
		{
			node.setType(af.createPTypeAssistant().typeResolve(node.getType(), null, question.rootVisitor, question.question));
			PPatternAssistantTC.typeResolve(node.getPattern(), question.rootVisitor, question.question);
			AValueDefinitionAssistantTC.updateDefs(node, question.question);
		}
	}
	@Override
	public void defaultPDefinition(PDefinition node, NewQuestion question)
			throws AnalysisException
	{
		return;
	}

}
