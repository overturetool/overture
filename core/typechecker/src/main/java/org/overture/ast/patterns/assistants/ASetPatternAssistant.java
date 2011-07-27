package org.overture.ast.patterns.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.ASeqPattern;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.typechecker.NameScope;

public class ASetPatternAssistant {

	public static void typeResolve(ASetPattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (pattern.getResolved()) return; else { pattern.setResolved(true); }

		try
		{
			PPatternListAssistant.typeResolve(pattern.getPlist(), rootVisitor, question);
		}
		catch (TypeCheckException e)
		{
			unResolve(pattern);
			throw e;
		}
		
	}

	public static void unResolve(ASetPattern pattern) {
		PPatternListAssistant.unResolve(pattern.getPlist());
		pattern.setResolved(false);
		
	}

	public static LexNameList getVariableNames(ASetPattern pattern) {
		LexNameList list = new LexNameList();

		for (PPattern p: pattern.getPlist())
		{
			list.addAll(PPatternAssistant.getVariableNames(p));
		}

		return list;
	}

	public static List<PDefinition> getDefinitions(ASeqPattern rp, PType type,
			NameScope scope) {
		
		List<PDefinition> defs = new Vector<PDefinition>();

		if (!PTypeAssistant.isSet(type))
		{
			TypeCheckerErrors.report(3204, "Set pattern is not matched against set type",rp.getLocation(),rp);
			TypeCheckerErrors.detail("Actual type", type);
		}
		else
		{
			ASetType set = PTypeAssistant.getSet(type);

			if (!set.getEmpty())
			{
        		for (PPattern p: rp.getPlist())
        		{
        			defs.addAll(PPatternAssistant.getDefinitions(p, set.getSetof(), scope));
        		}
			}
		}

		return defs;
	}

}
