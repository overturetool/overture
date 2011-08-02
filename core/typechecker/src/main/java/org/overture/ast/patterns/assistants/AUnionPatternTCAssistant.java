package org.overture.ast.patterns.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.typechecker.NameScope;

public class AUnionPatternTCAssistant {

	public static void typeResolve(AUnionPattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		if (pattern.getResolved()) return; else { pattern.setResolved(true); }

		try
		{
			PPatternTCAssistant.typeResolve(pattern.getLeft(), rootVisitor, question);
			PPatternTCAssistant.typeResolve(pattern.getRight(), rootVisitor, question);
		}
		catch (TypeCheckException e)
		{
			unResolve(pattern);
			throw e;
		}
		
	}

	public static void unResolve(AUnionPattern pattern) {
		PPatternTCAssistant.unResolve(pattern.getLeft());
		PPatternTCAssistant.unResolve(pattern.getRight());
		pattern.setResolved(false);
		
	}

//	public static LexNameList getVariableNames(AUnionPattern pattern) {
//		LexNameList list = new LexNameList();
//
//		list.addAll(PPatternTCAssistant.getVariableNames(pattern.getLeft()));
//		list.addAll(PPatternTCAssistant.getVariableNames(pattern.getRight()));
//
//		return list;
//	}

	public static List<PDefinition> getDefinitions(AUnionPattern rp,
			PType type, NameScope scope) {
		
		List<PDefinition> defs = new Vector<PDefinition>();

		if (!PTypeAssistant.isSet(type))
		{
			TypeCheckerErrors.report(3206, "Matching expression is not a set type",rp.getLocation(),rp);
		}

		defs.addAll(PPatternTCAssistant.getDefinitions(rp.getLeft(),type, scope));
		defs.addAll(PPatternTCAssistant.getDefinitions(rp.getRight(),type, scope));

		return defs;
	}

}
