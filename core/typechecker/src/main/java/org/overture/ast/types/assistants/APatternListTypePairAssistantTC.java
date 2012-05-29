package org.overture.ast.types.assistants;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.APatternListTypePair;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.patterns.assistants.PPatternAssistantTC;
import org.overture.ast.patterns.assistants.PPatternListAssistantTC;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;

import org.overturetool.vdmj.typechecker.NameScope;

public class APatternListTypePairAssistantTC {

	public static Collection<? extends PDefinition> getDefinitions(
			APatternListTypePair pltp, NameScope scope) {
		List<PDefinition> list = new Vector<PDefinition>();

		for (PPattern p: pltp.getPatterns())
		{
			list.addAll(PPatternAssistantTC.getAllDefinitions(p, pltp.getType(), scope));
		}

		return list;
	}

	public static void typeResolve(APatternListTypePair pltp,
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {
		
		PPatternListAssistantTC.typeResolve(pltp.getPatterns(), rootVisitor, question);
		PType type = PTypeAssistantTC.typeResolve(pltp.getType(),null,rootVisitor,question);
		pltp.setType(type);
		
	}

	
	
}
