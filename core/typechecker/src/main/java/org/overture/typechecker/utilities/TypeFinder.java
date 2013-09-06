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
	 * Added serial Number
	 */
	private static final long serialVersionUID = 1L;
	
	protected ITypeCheckerAssistantFactory af;

	public TypeFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	public PDefinition defaultSClassDefinition(SClassDefinition node,ILexNameToken sought, String fromModule) throws AnalysisException
	{
		return SClassDefinitionAssistantTC.findType(node, sought, fromModule);
	}
	
	public PDefinition caseAImportedDEfinition(AImportedDefinition node, ILexNameToken sought, String fromModule) throws AnalysisException
	{
		return AImportedDefinitionAssistantTC.findType(node, sought, fromModule);
	}
	
	public PDefinition caseAInheritedDefinition(AInheritedDefinition node, ILexNameToken sought, String fromModule) throws AnalysisException
	{
		return AInheritedDefinitionAssistantTC.findType(node, sought, fromModule);
	}
	
	public PDefinition caseARenameDefinition(ARenamedDefinition node, ILexNameToken sought, String fromModule) throws AnalysisException
	{
		return ARenamedDefinitionAssistantTC.findType(node, sought, fromModule);
	}
	
	public PDefinition caseAStateDefinition(AStateDefinition node, ILexNameToken sought, String fromModule)
			throws AnalysisException
	{
		return AStateDefinitionAssistantTC.findType(node, sought, fromModule);
	}
	
	
	public PDefinition caseATypeDefinition(ATypeDefinition node, ILexNameToken sought, String fromModule)
			throws AnalysisException
	{
		return ATypeDefinitionAssistantTC.findType(node, sought, fromModule);
	}
	
	public PDefinition DefaultPDefinition(PDefinition node, ILexNameToken sought, String fromModule)
			throws AnalysisException
	{
		return null;
	}
	
	public PDefinition defaultINode(INode node, ILexNameToken Sought, String fromModule) throws AnalysisException
	{
		assert false : "default case should never happen in TypeFinder";
		return null;
	}

	
	public PDefinition defaultIToken(IToken node, ILexNameToken Sought, String fromModule) throws AnalysisException
	{
		assert false : "default case should never happen in TypeFinder";
		return null;
	}

}
