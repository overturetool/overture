package org.overture.ast.patterns.assistants;

import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.ASeqConcatBinaryExp;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.AConcatenationPattern;
import org.overture.ast.types.ASeqSeqType;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexKeywordToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.VDMToken;
import org.overturetool.vdmj.typechecker.NameScope;


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

	public static List<PDefinition> getAllDefinitions(AConcatenationPattern rp, PType ptype,
			NameScope scope) {
		List<PDefinition> list = PPatternAssistantTC.getAllDefinitions(rp.getLeft(),ptype, scope);
		list.addAll(PPatternAssistantTC.getAllDefinitions(rp.getRight(),ptype, scope));
		return list;
		
	}

	public static PType getPossibleType(AConcatenationPattern pattern) {
		ASeqSeqType t = new ASeqSeqType(pattern.getLocation(), false, false);
		t.setSeqof( new AUnknownType(pattern.getLocation(), false));
		return t;
	}

	public static PExp getMatchingExpression(AConcatenationPattern ccp) {
		LexToken op = new LexKeywordToken(VDMToken.CONCATENATE, ccp.getLocation());
		PExp le = PPatternAssistantTC.getMatchingExpression(ccp.getLeft());
		PExp re = PPatternAssistantTC.getMatchingExpression(ccp.getRight());
		return new ASeqConcatBinaryExp(null, ccp.getLocation(), le, op, re);
	}

}
