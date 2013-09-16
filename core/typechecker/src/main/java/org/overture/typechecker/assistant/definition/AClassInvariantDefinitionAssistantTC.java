package org.overture.typechecker.assistant.definition;

import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.lex.LexNameList;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AClassInvariantDefinitionAssistantTC {
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public AClassInvariantDefinitionAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	public static List<PDefinition> getDefinitions(AClassInvariantDefinition d) {
		
		return new Vector<PDefinition>();
	}

	public static LexNameList getVariableNames(
			AClassInvariantDefinition d) {
		
		return new LexNameList(d.getName());
	}
	

}
