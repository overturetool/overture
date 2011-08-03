package org.overture.ast.patterns.assistants;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.ATuplePattern;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeAssistant;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overture.typecheck.TypeCheckerErrors;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.typechecker.NameScope;

public class ATuplePatternTCAssistant extends ATuplePatternAssistant{

	public static void typeResolve(ATuplePattern pattern,
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

	public static void unResolve(ATuplePattern pattern) {

		PPatternListAssistant.unResolve(pattern.getPlist());
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

	public static List<PDefinition> getDefinitions(ATuplePattern rp,
			PType type, NameScope scope) {
		
		List<PDefinition> defs = new Vector<PDefinition>();

		if (!PTypeAssistant.isProduct(type, rp.getPlist().size()))
		{
			TypeCheckerErrors.report(3205, "Matching expression is not a product of cardinality " + rp.getPlist().size(),rp.getLocation(),rp);
			TypeCheckerErrors.detail("Actual", type);
			return defs;
		}

		AProductType product = PTypeAssistant.getProduct(type, rp.getPlist().size());
		Iterator<PType> ti = product.getTypes().iterator();

		for (PPattern p: rp.getPlist())
		{
			defs.addAll(PPatternTCAssistant.getDefinitions(p,ti.next(), scope));
		}

		return defs;
	}

}
