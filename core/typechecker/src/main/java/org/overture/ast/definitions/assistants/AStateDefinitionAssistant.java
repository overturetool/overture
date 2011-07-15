package org.overture.ast.definitions.assistants;

import java.util.List;

import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AStateDefinitionAssistant {

	public static PDefinition findType(AStateDefinition d, LexNameToken sought,
			String fromModule) {
		
		if (PDefinitionAssistant.findName(d,sought, NameScope.STATE) != null)
		{
			return d;
		}

		return null;
	}

	public static PDefinition findName(AStateDefinition definition, LexNameToken sought,
			NameScope scope) {
		
		if (scope.matches(NameScope.NAMES))
		{
			PDefinition invdef = definition.getInvdef();
			
    		if (invdef != null && PDefinitionAssistant.findName(invdef, sought, scope) != null)
    		{
    			return invdef;
    		}

    		PDefinition initdef = definition.getInitdef();
    		if (initdef != null && PDefinitionAssistant.findName(initdef,sought, scope) != null)
    		{
    			return initdef;
    		}
		}
		
		if ( PDefinitionAssistant.findName(definition.getRecordDefinition(), sought, scope) != null)
		{
			return definition.getRecordDefinition();
		}

		for (PDefinition d: definition.getStateDefs())
		{
			PDefinition def = PDefinitionAssistant.findName(d, sought, scope);

			if (def != null)
			{
				return def;
			}
		}

		return null;
	}

	public static void unusedCheck(AStateDefinition d) {

		PDefinitionListAssistant.unusedCheck(d.getStateDefs());
	}

	public static List<PDefinition> getDefinitions(AStateDefinition d) {
		return d.getStateDefs();
	}

	public static LexNameList getVariableNames(AStateDefinition d) {
		return PDefinitionListAssistant.getVariableNames(d.getStateDefs());
	}

}
