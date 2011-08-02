package org.overture.ast.patterns.assistants;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;
import org.overture.typecheck.TypeCheckInfo;


public class PatternList extends Vector<PPattern>{

	
	public void typeResolve(
			QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor,
			TypeCheckInfo question) {

		for (PPattern pPattern : this) {
			PPatternTCAssistant.typeResolve(pPattern, rootVisitor, question);
		}		
	}

	public void unResolve() {
		
		for (PPattern pPattern : this) {
			PPatternTCAssistant.unResolve(pPattern);
		}	
	}
	
}
