package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.AnswerAdaptor;
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
public class TypeFinder extends AnswerAdaptor<PDefinition>
{
	
	
	/**
	 * I have no idea what is going wrong in order to fix it!
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ILexNameToken sought;
	private String fromModule;
	
	Newquestion question = new Newquestion(sought,fromModule);
	
	protected ITypeCheckerAssistantFactory af;

	public TypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public PDefinition defaultSClassDefinition(SClassDefinition node) throws AnalysisException
	{
		return SClassDefinitionAssistantTC.findType(node, question.sought, question.fromModule);
	}
	
	public PDefinition caseAImportedDEfinition(AImportedDefinition node) throws AnalysisException
	{
		return AImportedDefinitionAssistantTC.findType(node, question.sought, question.fromModule);
	}
	
	public PDefinition caseAInheritedDefinition(AInheritedDefinition node) throws AnalysisException
	{
		return AInheritedDefinitionAssistantTC.findType(node, question.sought, question.fromModule);
	}
	
	public PDefinition caseARenameDefinition(ARenamedDefinition node) throws AnalysisException
	{
		return ARenamedDefinitionAssistantTC.findType(node, question.sought, question.fromModule);
	}
	
	public PDefinition caseAStateDefinition(AStateDefinition node)
			throws AnalysisException
	{
		return AStateDefinitionAssistantTC.findType(node, question.sought, question.fromModule);
	}
	
	
	public PDefinition caseATypeDefinition(ATypeDefinition node)
			throws AnalysisException
	{
		return ATypeDefinitionAssistantTC.findType(node, question.sought, question.fromModule);
	}
	
	public PDefinition DefaultPDefinition(PDefinition node)
			throws AnalysisException
	{
		return null;
	}
	
	public PDefinition defaultINode(INode node) throws AnalysisException
	{
		assert false : "default case should never happen in TypeFinder";
		return null;
	}

	
	public PDefinition defaultIToken(IToken node) throws AnalysisException
	{
		assert false : "default case should never happen in TypeFinder";
		return null;
	}
	
	class Newquestion
	{
		final ILexNameToken sought;
		final String fromModule;
		
		public Newquestion(ILexNameToken s,String Module)
		{
			this.fromModule = Module;
			this.sought = s;
		}

	} 

}
