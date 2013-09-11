package org.overture.typechecker.assistant.definition;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ARenamedDefinitionAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ARenamedDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	public static PDefinition findType(ARenamedDefinition d,
			ILexNameToken sought, String fromModule)
	{

		// We can only find an import if it is being sought from the module that
		// imports it.

		if (fromModule != null
				&& !d.getLocation().getModule().equals(fromModule))
		{
			return null; // Someone else's import
		}

		PDefinition renamed = PDefinitionAssistantTC.findName(d, sought, NameScope.TYPENAME);

		if (renamed != null && d.getDef() instanceof ATypeDefinition)
		{
			PDefinitionAssistantTC.markUsed(d.getDef());
			return renamed;
		} else
		{
			return PDefinitionAssistantTC.findType(d.getDef(), sought, fromModule);
		}
	}

	public static PDefinition findName(ARenamedDefinition d,
			ILexNameToken sought, NameScope scope)
	{

		PDefinition renamed = PDefinitionAssistantTC.findNameBaseCase(d, sought, scope);

		if (renamed != null)
		{
			PDefinitionAssistantTC.markUsed(d.getDef());
			return renamed;
		} else
		{
			// Renamed definitions hide the original name
			return null;// PDefinitionAssistantTC.findName(d.getDef(),sought, scope);
		}
	}

	public static void markUsed(ARenamedDefinition d)
	{
		d.setUsed(true);
		PDefinitionAssistantTC.markUsed(d.getDef());

	}

	public static void typeResolve(ARenamedDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException
	{
		PDefinitionAssistantTC.typeResolve(d.getDef(), rootVisitor, question);
	}

	// public static boolean isUsed(ARenamedDefinition u) {
	// return PDefinitionAssistantTC.isUsed(u.getDef());
	// }

}
