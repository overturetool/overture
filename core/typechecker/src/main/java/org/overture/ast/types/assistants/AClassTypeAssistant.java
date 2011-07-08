package org.overture.ast.types.assistants;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.assistants.SClassDefinitionAssistant;
import org.overture.ast.types.AClassType;
import org.overture.ast.types.PType;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AClassTypeAssistant {

	public static LexNameToken getMemberName(AClassType cls,
			LexIdentifierToken id) {
		// Note: not explicit
		return new LexNameToken(cls.getName().name, id.name, id.location, false, false);
	}

	public static PDefinition findName(AClassType cls, LexNameToken tag) {
		return  SClassDefinitionAssistant.findName(cls.getClassdef(),tag, NameScope.NAMESANDSTATE);
	}

	public static boolean hasSupertype(AClassType sclass, PType other) {
		return SClassDefinitionAssistant.hasSupertype(sclass.getClassdef(),other);
	}

}
