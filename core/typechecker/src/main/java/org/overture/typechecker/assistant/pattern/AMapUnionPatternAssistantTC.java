package org.overture.typechecker.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.AMapUnionPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class AMapUnionPatternAssistantTC {

	public static void unResolve(AMapUnionPattern pattern) {
		
		PPatternAssistantTC.unResolve(pattern.getLeft());
		PPatternAssistantTC.unResolve(pattern.getRight());
		pattern.setResolved(false);
	}

	public static void typeResolve(AMapUnionPattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws Throwable {
		
		if (pattern.getResolved()) return; else { pattern.setResolved(true); }

		try
		{
			PPatternAssistantTC.typeResolve(pattern.getLeft(), rootVisitor, question);
			PPatternAssistantTC.typeResolve(pattern.getRight(), rootVisitor, question);
		}
		catch (TypeCheckException e)
		{
			unResolve(pattern);
			throw e;
		}
		
	}

	public static List<PDefinition> getAllDefinitions(AMapUnionPattern rp,
			PType ptype, NameScope scope) {
		
		List<PDefinition> defs = new Vector<PDefinition>();
		
		if(!PTypeAssistantTC.isMap(ptype))
		{
			TypeCheckerErrors.report(3315, "Matching expression is not a map type",rp.getLocation(),rp);
		}
		
		defs.addAll(PPatternAssistantTC.getDefinitions(rp.getLeft(), ptype, scope));
		defs.addAll(PPatternAssistantTC.getDefinitions(rp.getRight(), ptype, scope));
		
		return defs;
	}

}
