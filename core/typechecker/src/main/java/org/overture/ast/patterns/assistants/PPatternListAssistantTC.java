package org.overture.ast.patterns.assistants;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.AUnknownType;
import org.overture.ast.types.PType;
import org.overture.ast.types.assistants.PTypeSet;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.lex.LexLocation;

public class PPatternListAssistantTC {

	public static void typeResolve(List<PPattern> pp,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
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
				return new AUnknownType(location,false);

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
	
}
