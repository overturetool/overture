package org.overture.ast.definitions.assistants;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PPatternAssistant;
import org.overture.ast.patterns.assistants.PPatternListAssistant;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;

public class PMultipleBindAssistant {

	public static Collection<? extends PDefinition> getDefinitions(
			PMultipleBind mb, PType type, TypeCheckInfo question) {
		
		List<PDefinition> defs = new Vector<PDefinition>();

		for (PPattern p: mb.getPlist())
		{
			defs.addAll(PPatternAssistant.getDefinitions(p, type, question.scope));
		}

		return defs;
	}

	public static List<? extends PMultipleBind> getMultipleBindList(
			PMultipleBind bind) {
		List<PMultipleBind> list = new Vector<PMultipleBind>();
		list.add(bind);
		return list;
	}

	public static PType getPossibleType(ASetMultipleBind node) {
		return PPatternListAssistant.getPossibleType(node.getPlist(),node.getLocation());
	}

}
