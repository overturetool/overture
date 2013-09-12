package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AExternalDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.AInstanceVariableDefinition;
import org.overture.ast.definitions.AMultiBindListDefinition;
import org.overture.ast.definitions.AMutexSyncDefinition;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ast.definitions.APerSyncDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.AEqualsDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AExplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AExplicitOperationDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AExternalDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImplicitFunctionDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImplicitOperationDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AImportedDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AInheritedDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AInstanceVariableDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AMultiBindListDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AMutexSyncDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ANamedTraceDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.APerSyncDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ARenamedDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AStateDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AThreadDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ATypeDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AValueDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;

/**
 * This class implements a way to find type from a node in the AST
 * 
 * @author kel
 */
public class NameFinder extends QuestionAnswerAdaptor<NameFinder.Newquestion, PDefinition>
{
	public static class Newquestion
	{
		final ILexNameToken sought;
		final NameScope scope;
		
		public Newquestion(ILexNameToken sought,NameScope scope)
		{
			this.scope = scope;
			this.sought = sought;
		}

	} 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected ITypeCheckerAssistantFactory af;

	public NameFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	@Override
	public PDefinition defaultSClassDefinition(SClassDefinition node,
			Newquestion question) throws AnalysisException
	{
		return SClassDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition caseAEqualsDefinition(AEqualsDefinition node,
			Newquestion question) throws AnalysisException
	{
		return AEqualsDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node, Newquestion question)
			throws AnalysisException
	{
		return AExplicitFunctionDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, Newquestion question)
			throws AnalysisException
	{
		return AExplicitOperationDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition caseAExternalDefinition(AExternalDefinition node,
			Newquestion question) throws AnalysisException
	{
		return AExternalDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node, Newquestion question)
			throws AnalysisException
	{
		return AImplicitFunctionDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node, Newquestion question)
			throws AnalysisException
	{
		return AImplicitOperationDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition caseAImportedDefinition(AImportedDefinition node,
			Newquestion question) throws AnalysisException
	{
		return AImportedDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}

	@Override
	public PDefinition caseAInheritedDefinition(AInheritedDefinition node,
			Newquestion question) throws AnalysisException
	{
		return AInheritedDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	@Override
	public PDefinition caseAInstanceVariableDefinition(
			AInstanceVariableDefinition node, Newquestion question)
			throws AnalysisException
	{
		return AInstanceVariableDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition caseAMultiBindListDefinition(
			AMultiBindListDefinition node, Newquestion question)
			throws AnalysisException
	{
		return AMultiBindListDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition caseAMutexSyncDefinition(AMutexSyncDefinition node,
			Newquestion question) throws AnalysisException
	{
		return AMutexSyncDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition caseANamedTraceDefinition(ANamedTraceDefinition node,
			Newquestion question) throws AnalysisException
	{
		return ANamedTraceDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition caseAPerSyncDefinition(APerSyncDefinition node,
			Newquestion question) throws AnalysisException
	{
		return APerSyncDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition caseARenamedDefinition(ARenamedDefinition node,
			Newquestion question) throws AnalysisException
	{
		return ARenamedDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition caseAStateDefinition(AStateDefinition node,
			Newquestion question) throws AnalysisException
	{
		return AStateDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition caseAThreadDefinition(AThreadDefinition node,
			Newquestion question) throws AnalysisException
	{
		return AThreadDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition caseATypeDefinition(ATypeDefinition node,
			Newquestion question) throws AnalysisException
	{
		return ATypeDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}

	@Override
	public PDefinition caseAValueDefinition(AValueDefinition node,
			Newquestion question) throws AnalysisException
	{
		return AValueDefinitionAssistantTC.findName(node, question.sought, question.scope);
	}
	
	@Override
	public PDefinition defaultPDefinition(PDefinition node, Newquestion question)
			throws AnalysisException
	{
		return PDefinitionAssistantTC.findNameBaseCase(node, question.sought, question.scope);
	}
}
