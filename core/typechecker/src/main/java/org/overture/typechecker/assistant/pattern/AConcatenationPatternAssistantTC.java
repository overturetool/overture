package org.overture.typechecker.assistant.pattern;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.pattern.AConcatenationPatternAssistant;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexKeywordToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;


public class AConcatenationPatternAssistantTC extends AConcatenationPatternAssistant{

	public static void typeResolve(AConcatenationPattern pattern, QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question) throws AnalysisException {
		
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

	public static List<PDefinition> getAllDefinitions(AConcatenationPattern rp, PType ptype,
			NameScope scope) {
		List<PDefinition> list = PPatternAssistantTC.getDefinitions(rp.getLeft(),ptype, scope);
		list.addAll(PPatternAssistantTC.getDefinitions(rp.getRight(),ptype, scope));
		return list;
		
	}

	public static PType getPossibleType(AConcatenationPattern pattern) {
		return AstFactory.newASeqSeqType(pattern.getLocation(), AstFactory.newAUnknownType(pattern.getLocation()));
	}

	public static PExp getMatchingExpression(AConcatenationPattern ccp) {
		LexToken op = new LexKeywordToken(VDMToken.CONCATENATE, ccp.getLocation());
		PExp le = PPatternAssistantTC.getMatchingExpression(ccp.getLeft());
		PExp re = PPatternAssistantTC.getMatchingExpression(ccp.getRight());
		return  AstFactory.newASeqConcatBinaryExp(le, op, re);
	}
	
	public static boolean isSimple(AConcatenationPattern p)
	{
		return PPatternAssistantTC.isSimple(p.getLeft()) && PPatternAssistantTC.isSimple(p.getRight());
	}
	
}
