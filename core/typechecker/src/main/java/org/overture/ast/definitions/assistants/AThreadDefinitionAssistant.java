package org.overture.ast.definitions.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AOperationType;
import org.overture.ast.types.AVoidType;
import org.overture.ast.types.assistants.PTypeList;
import org.overture.typecheck.Environment;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AThreadDefinitionAssistant {

	public static PDefinition findName(AThreadDefinition definition,
			LexNameToken sought, NameScope scope) {
		
		return PDefinitionAssistant.findName(definition.getOperationDef(),sought, scope);
	}

	public static List<PDefinition> getDefinitions(AThreadDefinition d) {
		List<PDefinition> result = new Vector<PDefinition>();
		result.add(d.getOperationDef());
		return result;
	}

	public static LexNameList getVariableNames(AThreadDefinition d) {
		return new LexNameList(d.getOperationDef().getName());
	}

	public static void implicitDefinitions(AThreadDefinition d, Environment env) {
		d.setOperationDef(getThreadDefinition(d));
		
	}

	private static AExplicitOperationDefinition getThreadDefinition(AThreadDefinition d) {
		PTypeList parameters = new PTypeList();
		AVoidType result = new AVoidType(d.getLocation(),false);
		AOperationType type = new AOperationType(d.getLocation(),false,parameters,result);	// () ==> ()

		AExplicitOperationDefinition def = new AExplicitOperationDefinition(
				d.getLocation(),
				d.getOperationName(), 
				NameScope.GLOBAL,
				false,
				PAccessSpecifierAssistant.getDefault(),
				new Vector<PPattern>(),
				d.getStatement(),
				null, null, type,
				null, false);

		def.setAccess(d.getAccess());
		def.setClassDefinition(d.getClassDefinition());
		return def;
	}

}
