package org.overture.typechecker.assistant.definition;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;

public class ARenamedDefinitionAssistantTC {

	public static PDefinition findType(ARenamedDefinition d,
			ILexNameToken sought, String fromModule) {
		
		// We can only find an import if it is being sought from the module that
		// imports it.

		if (fromModule != null && !d.getLocation().module.equals(fromModule))
		{
			return null;	// Someone else's import
		}

		PDefinition renamed = PDefinitionAssistantTC.findName(d,sought, NameScope.TYPENAME);

		if (renamed != null && d.getDef() instanceof ATypeDefinition)
		{
			PDefinitionAssistantTC.markUsed(d.getDef());
			return renamed;
		}
		else
		{
			return  PDefinitionAssistantTC.findType(d.getDef(),sought, fromModule);
		}
	}

	public static PDefinition findName(ARenamedDefinition d,
			ILexNameToken sought, NameScope scope) {
		
		PDefinition renamed = PDefinitionAssistantTC.findNameBaseCase(d, sought, scope);

		if (renamed != null)
		{
			PDefinitionAssistantTC.markUsed(d.getDef());
			return renamed;
		}
		else
		{
			// Renamed definitions hide the original name
			return  null;//PDefinitionAssistantTC.findName(d.getDef(),sought, scope);
		}
	}

	public static void markUsed(ARenamedDefinition d) {
		d.setUsed(true);
		PDefinitionAssistantTC.markUsed(d.getDef());
		
	}

	public static List<PDefinition> getDefinitions(ARenamedDefinition d) {
		List<PDefinition> result = new Vector<PDefinition>();
		result.add(d);
		return result;
	}

	public static LexNameList getVariableNames(ARenamedDefinition d) {
		LexNameList both = new LexNameList(d.getName());
		both.add(d.getDef().getName());
		return both;
	}

	public static void typeResolve(ARenamedDefinition d,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException {
		PDefinitionAssistantTC.typeResolve(d.getDef(), rootVisitor, question);		
	}

//	public static boolean isUsed(ARenamedDefinition u) {
//		return PDefinitionAssistantTC.isUsed(u.getDef());
//	}

}
