package org.overture.typechecker.assistant.pattern;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.PType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class ATuplePatternAssistantTC {

	public static void typeResolve(ATuplePattern pattern,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException {
		
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

	public static void unResolve(ATuplePattern pattern) {

		PPatternListAssistantTC.unResolve(pattern.getPlist());
		pattern.setResolved(false);
		
	}

//	public static LexNameList getVariableNames(ATuplePattern pattern) {
//		LexNameList list = new LexNameList();
//
//		for (PPattern p: pattern.getPlist())
//		{
//			list.addAll(PPatternTCAssistant.getVariableNames(p));
//		}
//
//		return list;
//	}

	public static List<PDefinition> getAllDefinitions(ATuplePattern rp,
			PType type, NameScope scope) {
		
		List<PDefinition> defs = new Vector<PDefinition>();

		if (!PTypeAssistantTC.isProduct(type, rp.getPlist().size()))
		{
			TypeCheckerErrors.report(3205, "Matching expression is not a product of cardinality " + rp.getPlist().size(),rp.getLocation(),rp);
			TypeCheckerErrors.detail("Actual", type);
			return defs;
		}

		AProductType product = PTypeAssistantTC.getProduct(type, rp.getPlist().size());
		Iterator<PType> ti = product.getTypes().iterator();

		for (PPattern p: rp.getPlist())
		{
			defs.addAll(PPatternAssistantTC.getDefinitions(p,ti.next(), scope));
		}

		return defs;
	}

	public static PType getPossibleTypes(ATuplePattern tupplePattern) {		
		PTypeList list = new PTypeList();

		for (PPattern p : tupplePattern.getPlist())
		{
			list.add(PPatternAssistantTC.getPossibleType(p));
		}

		return list.getType(tupplePattern.getLocation());
	}

	public static PExp getMatchingExpression(ATuplePattern tp) {
		return AstFactory.newATupleExp(tp.getLocation(), PPatternListAssistantTC.getMatchingExpressionList(tp.getPlist()));				
	}
	
	public static boolean isSimple(ATuplePattern p)
	{
		return PPatternListAssistantTC.isSimple(p.getPlist());
	}
	
}
