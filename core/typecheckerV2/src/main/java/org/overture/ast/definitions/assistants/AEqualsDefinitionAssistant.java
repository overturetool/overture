package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AEqualsDefinitionAssistant {

	public static PDefinition findName(AEqualsDefinition d,
			LexNameToken sought, NameScope scope) {
		
		List<PDefinition> defs = d.getDefs();
		
		if (defs != null)
		{
			PDefinition def = PDefinitionListAssistant.findName(defs, sought, scope);

			if (def != null)
			{
				return def;
			}
		}
		return null;
	}

	public static void unusedCheck(AEqualsDefinition d) {
		
		if (d.getDefs() != null)
		{
			PDefinitionListAssistant.unusedCheck(d.getDefs());
		}
		
	}

	public static List<PDefinition> getDefinitions(AEqualsDefinition d) {
		
		return d.getDefs() == null ? new Vector<PDefinition>() : d.getDefs();
	}

	public static LexNameList getVariableNames(AEqualsDefinition d) {
		
		return d.getDefs() == null ? new LexNameList() : PDefinitionListAssistant.getVariableNames(d.getDefs());
	}

	public static PType getType(AEqualsDefinition def) {
		return def.getDefType() != null ? def.getDefType() : new AUnknownType(def.getLocation(),false);
	}

}
