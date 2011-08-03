package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.ARenamedDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class ARenamedDefinitionAssistant {

	public static PDefinition findType(ARenamedDefinition d,
			LexNameToken sought, String fromModule) {
		
		// We can only find an import if it is being sought from the module that
		// imports it.

		if (fromModule != null && !d.getLocation().module.equals(fromModule))
		{
			return null;	// Someone else's import
		}

		PDefinition renamed = PDefinitionAssistant.findName(d,sought, NameScope.TYPENAME);

		if (renamed != null && d.getDef() instanceof ATypeDefinition)
		{
			PDefinitionAssistant.markUsed(d.getDef());
			return renamed;
		}
		else
		{
			return  PDefinitionAssistant.findType(d.getDef(),sought, fromModule);
		}
	}

	public static PDefinition findName(ARenamedDefinition d,
			LexNameToken sought, NameScope scope) {
		
		PDefinition renamed = PDefinitionAssistant.findNameBaseCase(d, sought, scope);

		if (renamed != null)
		{
			PDefinitionAssistant.markUsed(d.getDef());
			return renamed;
		}
		else
		{
			return  PDefinitionAssistant.findName(d.getDef(),sought, scope);
		}
	}

	public static void markUsed(ARenamedDefinition d) {
		d.setUsed(true);
		PDefinitionAssistant.markUsed(d.getDef());
		
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
			TypeCheckInfo question) {
		PDefinitionAssistant.typeResolve(d.getDef(), rootVisitor, question);		
	}

	public static boolean isUsed(ARenamedDefinition u) {
		return PDefinitionAssistant.isUsed(u.getDef());
	}

}
