package org.overture.typechecker.assistant.pattern;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckInfo;

public class PMultipleBindAssistantTC {

	public static Collection<? extends PDefinition> getDefinitions(
			PMultipleBind mb, PType type, TypeCheckInfo question) {
		
		List<PDefinition> defs = new Vector<PDefinition>();

		for (PPattern p: mb.getPlist())
		{
			defs.addAll(PPatternAssistantTC.getDefinitions(p, type, question.scope));
		}

		return defs;
	}

	public static List<PMultipleBind> getMultipleBindList(
			PMultipleBind bind) {
		List<PMultipleBind> list = new Vector<PMultipleBind>();
		list.add(bind);
		return list;
	}

	public static PType getPossibleType(PMultipleBind mb) {
		switch (mb.kindPMultipleBind()) {
		case SET:
			return ASetMultipleBindAssistantTC
					.getPossibleType((ASetMultipleBind) mb);
		case TYPE:
			return ATypeMultipleBindAssistantTC
					.getPossibleType((ATypeMultipleBind) mb);
		default:
			assert false : "Should not happen";
			return null;
		}

	}

	public static LexNameList getOldNames(
			PMultipleBind mb) {
		switch (mb.kindPMultipleBind()) {
		case SET:
			return ASetMultipleBindAssistantTC.getOldNames((ASetMultipleBind) mb);
		case TYPE:
			return ATypeMultipleBindAssistantTC.getOldNames((ATypeMultipleBind) mb);
		default:
			assert false : "Should not happen";
			return null;
		}
	}

}
