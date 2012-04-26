package org.overture.ast.patterns.assistants;

import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmjV2.typechecker.NameScope;


public class AConcatenationPatternAssistantTC extends AConcatenationPatternAssistant{

	public static void typeResolve(AConcatenationPattern pattern, QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question) {
		
		if (pattern.getResolved()) return; else { pattern.setResolved(true); }

		try
		{
			PPatternAssistantTC.typeResolve(pattern.getLeft(),rootVisitor,question);
			PPatternAssistantTC.typeResolve(pattern.getRight(),rootVisitor,question);
		}
		catch (TypeCheckException e)
		{
			unResolve(pattern);
			throw e;
		}
		
	}

	public static void unResolve(AConcatenationPattern pattern) {
		PPatternAssistantTC.unResolve(pattern.getLeft());
		PPatternAssistantTC.unResolve(pattern.getRight());
		pattern.setResolved(false);
		
	}

	public static List<PDefinition> getDefinitions(AConcatenationPattern rp, PType ptype,
			NameScope scope) {
		List<PDefinition> list = PPatternAssistantTC.getDefinitions(rp.getLeft(),ptype, scope);
		list.addAll(PPatternAssistantTC.getDefinitions(rp.getRight(),ptype, scope));
		return list;
		
	}

}
