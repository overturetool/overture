package org.overture.typechecker.assistant.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexNameList;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.typechecker.assistant.expression.PExpAssistantTC;


public class ASetBindAssistantTC {

	public static List<PMultipleBind> getMultipleBindList(ASetBind bind) {
		
		List<PPattern> plist = new ArrayList<PPattern>();
		plist.add(bind.getPattern());
		List<PMultipleBind> mblist = new Vector<PMultipleBind>();
		mblist.add(AstFactory.newASetMultipleBind(plist, bind.getSet()));
		return mblist;
	}

	public static LexNameList getOldNames(ASetBind bind) {
		return PExpAssistantTC.getOldNames(bind.getSet());
	}

}
