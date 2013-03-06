package org.overture.typechecker.assistant.pattern;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.ast.util.PTypeSet;
import org.overture.typechecker.TypeCheckInfo;

public class PPatternListAssistantTC {

	public static void typeResolve(List<PPattern> pp,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) throws AnalysisException {
		
		for (PPattern pattern : pp) {
			PPatternAssistantTC.typeResolve(pattern, rootVisitor, question);
		}
		
	}


	public static void unResolve(List<PPattern> pp) {
		
		for (PPattern pPattern : pp) {
			PPatternAssistantTC.unResolve(pPattern);
		}	
	}

	public static PType getPossibleType(LinkedList<PPattern> plist,
			LexLocation location) {
		
		switch (plist.size())
		{
			case 0:
				return AstFactory.newAUnknownType(location);

			case 1:
				return PPatternAssistantTC.getPossibleType(plist.get(0));

			default:
        		PTypeSet list = new PTypeSet();

        		for (PPattern p: plist)
        		{
        			list.add(PPatternAssistantTC.getPossibleType(p));
        		}

        		return list.getType(location);		// NB. a union of types
		}
	}

	public static List<PExp> getMatchingExpressionList(List<PPattern> pl) {

		List<PExp> list = new ArrayList<PExp>();

		for (PPattern p : pl) {
			list.add(PPatternAssistantTC.getMatchingExpression(p));
		}

		return list;
	}
	
	public static boolean isSimple(LinkedList<PPattern> p)
	{
		for (PPattern pattern: p)
		{
			if (!PPatternAssistantTC.isSimple(pattern)) return false;		// NB. AND
		}

		return true;
	}
	
}
