package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.AImportedDefinition;
import org.overture.ast.definitions.AInheritedDefinition;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.node.INode;
import org.overture.ast.node.IToken;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.AImportedDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AInheritedDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ARenamedDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.AStateDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.ATypeDefinitionAssistantTC;
import org.overture.typechecker.assistant.definition.SClassDefinitionAssistantTC;

/**
 * This class implements a way to find type from a node in the AST
 * 
 * @author kel
 */
public class TypeFinder extends QuestionAnswerAdaptor<TypeFinder.Newquestion,PDefinition>
{
	public static class Newquestion
	{
		final ILexNameToken sought;
		final String fromModule;
		
		public Newquestion(ILexNameToken sought,String fromModule)
		{
			this.fromModule = fromModule;
			this.sought = sought;
		}

	} 
	
	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected ITypeCheckerAssistantFactory af;

	public TypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	
	@Override
	public PDefinition defaultSClassDefinition(SClassDefinition node, Newquestion question) throws AnalysisException
	{
		return SClassDefinitionAssistantTC.findType(node, question.sought, question.fromModule);
	}
	
	@Override
	public PDefinition caseAImportedDefinition(AImportedDefinition node, Newquestion question) throws AnalysisException
	{
		return AImportedDefinitionAssistantTC.findType(node, question.sought, question.fromModule);
	}
	@Override
	public PDefinition caseAInheritedDefinition(AInheritedDefinition node, Newquestion question) throws AnalysisException
	{
		return AInheritedDefinitionAssistantTC.findType(node, question.sought, question.fromModule);
	}
	@Override
	public PDefinition caseARenamedDefinition(ARenamedDefinition node, Newquestion question) throws AnalysisException
	{
		return ARenamedDefinitionAssistantTC.findType(node, question.sought, question.fromModule);
	}
	@Override
	public PDefinition caseAStateDefinition(AStateDefinition node, Newquestion question)
			throws AnalysisException
	{
		return AStateDefinitionAssistantTC.findType(node, question.sought, question.fromModule);
	}
	
	@Override
	public PDefinition caseATypeDefinition(ATypeDefinition node, Newquestion question)
			throws AnalysisException
	{
		return ATypeDefinitionAssistantTC.findType(node, question.sought, question.fromModule);
	}
	
	
	@Override
	public PDefinition defaultPDefinition(PDefinition node, Newquestion question)
			throws AnalysisException
	{
		return null;
	}
	@Override
	public PDefinition defaultINode(INode node, Newquestion question) throws AnalysisException
	{
		assert false : "default case should never happen in TypeFinder";
		return null;
	}

	@Override
	public PDefinition defaultIToken(IToken node, Newquestion question) throws AnalysisException
	{
		assert false : "default case should never happen in TypeFinder";
		return null;
	}
	
	

}
