package org.overture.typechecker.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexKeywordToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.patterns.AUnionPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class AUnionPatternAssistantTC {

	public static void typeResolve(AUnionPattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException {
		
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

		defs.addAll(PPatternAssistantTC.getDefinitions(rp.getLeft(),type, scope));
		defs.addAll(PPatternAssistantTC.getDefinitions(rp.getRight(),type, scope));

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
	
	public static boolean isSimple(AUnionPattern p)
	{
		return PPatternAssistantTC.isSimple(p.getLeft()) && PPatternAssistantTC.isSimple(p.getRight());
	}
	
}
