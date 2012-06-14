package org.overture.typechecker.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.pattern.ASetPatternAssistant;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.ASetPattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class ASetPatternAssistantTC extends ASetPatternAssistant {

	public static void typeResolve(ASetPattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws Throwable {
		
		if (pattern.getResolved()) return; else { pattern.setResolved(true); }

		try
		{
			PPatternListAssistantTC.typeResolve(pattern.getPlist(), rootVisitor, question);
		}
		catch (TypeCheckException e)
		{
			unResolve(pattern);
			throw e;
		}
		
	}

	public static void unResolve(ASetPattern pattern) {
		PPatternListAssistantTC.unResolve(pattern.getPlist());
		pattern.setResolved(false);
		
	}

//	public static LexNameList getVariableNames(ASetPattern pattern) {
//		LexNameList list = new LexNameList();
//
//		for (PPattern p: pattern.getPlist())
//		{
//			list.addAll(PPatternTCAssistant.getVariableNames(p));
//		}
//
//		return list;
//	}

	public static List<PDefinition> getAllDefinitions(ASetPattern rp, PType type,
			NameScope scope) {
		
		List<PDefinition> defs = new Vector<PDefinition>();

		if (!PTypeAssistantTC.isSet(type))
		{
			TypeCheckerErrors.report(3204, "Set pattern is not matched against set type",rp.getLocation(),rp);
			TypeCheckerErrors.detail("Actual type", type);
		}
		else
		{
			ASetType set = PTypeAssistantTC.getSet(type);

			if (!set.getEmpty())
			{
        		for (PPattern p: rp.getPlist())
        		{
        			defs.addAll(PPatternAssistantTC.getDefinitions(p, set.getSetof(), scope));
        		}
			}
		}

		return defs;
	}

	public static PType getPossibleTypes(ASetPattern pattern) {
		return AstFactory.newASetType(pattern.getLocation(), AstFactory.newAUnknownType(pattern.getLocation()));
	}

	public static PExp getMatchingExpression(ASetPattern sp) {
		return AstFactory.newASetEnumSetExp(sp.getLocation(), PPatternListAssistantTC.getMatchingExpressionList(sp.getPlist()));
	} 

}
