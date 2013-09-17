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
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.PType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.definition.PDefinitionAssistantTC;

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
		if ((!question.sought.getExplicit() && question.sought.getName().equals(node.getName().getName()))
				|| question.sought.equals(node.getName().getClassName()))
		{
			return node; // Class referred to as "A" or "CLASS`A"
		}

		PDefinition def = PDefinitionAssistantTC.findType(node.getDefinitions(), question.sought, null);

		if (def == null)
		{
			for (PDefinition d : node.getAllInheritedDefinitions())
			{
				PDefinition indef = PDefinitionAssistantTC.findType(d, question.sought, null);

				if (indef != null)
				{
					def = indef;
					break;
				}
			}
		}

		return def;
	}
	
	@Override
	public PDefinition caseAImportedDefinition(AImportedDefinition node, Newquestion question) throws AnalysisException
	{
		// We can only find an import if it is being sought from the module that
		// imports it.

		if (question.fromModule != null
				&& !node.getLocation().getModule().equals(question.fromModule))
		{
			return null; // Someone else's import
		}

		PDefinition def = PDefinitionAssistantTC.findType(node.getDef(), question.sought, question.fromModule);

		if (def != null)
		{
			PDefinitionAssistantTC.markUsed(node);
		}

		return def;
	}
	@Override
	public PDefinition caseAInheritedDefinition(AInheritedDefinition node, Newquestion question) throws AnalysisException
	{
		if (node.getSuperdef() instanceof ATypeDefinition
				&& question.sought.equals(node.getName()))
		{
			return node;
		}

		return null;
	}
	@Override
	public PDefinition caseARenamedDefinition(ARenamedDefinition node, Newquestion question) throws AnalysisException
	{
		// We can only find an import if it is being sought from the module that
		// imports it.

		if (question.fromModule != null
				&& !node.getLocation().getModule().equals(question.fromModule))
		{
			return null; // Someone else's import
		}

		PDefinition renamed = PDefinitionAssistantTC.findName(node, question.sought, NameScope.TYPENAME);

		if (renamed != null && node.getDef() instanceof ATypeDefinition)
		{
			PDefinitionAssistantTC.markUsed(node.getDef());
			return renamed;
		} else
		{
			return PDefinitionAssistantTC.findType(node.getDef(), question.sought, question.fromModule);
		}
	}
	@Override
	public PDefinition caseAStateDefinition(AStateDefinition node, Newquestion question)
			throws AnalysisException
	{
		if (PDefinitionAssistantTC.findName(node, question.sought, NameScope.STATE) != null)
		{
			return node;
		}

		return null;
	}
	
	@Override
	public PDefinition caseATypeDefinition(ATypeDefinition node, Newquestion question)
			throws AnalysisException
	{
		PType type = node.getType();

		if (type instanceof ANamedInvariantType)
		{
			ANamedInvariantType nt = (ANamedInvariantType) type;

			if (nt.getType() instanceof ARecordInvariantType)
			{
				ARecordInvariantType rt = (ARecordInvariantType) nt.getType();

				if (rt.getName().equals(question.sought))
				{
					return node; // T1 = compose T2 x:int end;
				}
			}
		}

		return PDefinitionAssistantTC.findNameBaseCase(node, question.sought, NameScope.TYPENAME);
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
