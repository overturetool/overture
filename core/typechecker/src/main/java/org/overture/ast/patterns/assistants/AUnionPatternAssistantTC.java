package org.overture.ast.patterns.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistantTC;
import org.overture.ast.utils.PTypeSet;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexKeywordToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.VDMToken;
import org.overturetool.vdmj.typechecker.NameScope;

public class AUnionPatternAssistantTC {

	public static void typeResolve(AUnionPattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
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

	public static void unResolve(AUnionPattern pattern) {
		PPatternAssistantTC.unResolve(pattern.getLeft());
		PPatternAssistantTC.unResolve(pattern.getRight());
		pattern.setResolved(false);
		
	}

	public static List<PDefinition> getAllDefinitions(AUnionPattern rp,
			PType type, NameScope scope) {
		
		List<PDefinition> defs = new Vector<PDefinition>();

		if (!PTypeAssistantTC.isSet(type))
		{
			TypeCheckerErrors.report(3206, "Matching expression is not a set type",rp.getLocation(),rp);
		}

		defs.addAll(PPatternAssistantTC.getAllDefinitions(rp.getLeft(),type, scope));
		defs.addAll(PPatternAssistantTC.getAllDefinitions(rp.getRight(),type, scope));

		return defs;
	}

	public static PType getPossibleTypes(AUnionPattern unionPattern) {
		PTypeSet set = new PTypeSet();

		set.add(PPatternAssistantTC.getPossibleType(unionPattern.getLeft()));
		set.add(PPatternAssistantTC.getPossibleType(unionPattern.getRight()));

		PType s = set.getType(unionPattern.getLocation());

		return PTypeAssistantTC.isUnknown(s) ? 
				AstFactory.newASetType(unionPattern.getLocation(), AstFactory.newAUnknownType(unionPattern.getLocation())) : s;
	}

	public static PExp getMatchingExpression(AUnionPattern up) {
		LexToken op = new LexKeywordToken(VDMToken.UNION, up.getLocation());
		return AstFactory.newASetUnionBinaryExp(PPatternAssistantTC.getMatchingExpression(up.getLeft()), op, PPatternAssistantTC.getMatchingExpression(up.getRight()));
	}

}
