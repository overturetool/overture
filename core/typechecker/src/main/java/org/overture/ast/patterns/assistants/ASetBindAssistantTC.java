package org.overture.ast.patterns.assistants;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.expressions.assistants.PExpAssistantTC;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.ASetMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overturetool.vdmj.lex.LexNameList;


public class ASetBindAssistantTC {

	public static List<PMultipleBind> getMultipleBindList(ASetBind bind) {
		
		List<PPattern> plist = new ArrayList<PPattern>();
		plist.add(bind.getPattern().clone());
		List<PMultipleBind> mblist = new Vector<PMultipleBind>();
		mblist.add(new ASetMultipleBind(plist.get(0).getLocation(), plist, bind.getSet().clone()));
		return mblist;
	}

	public static LexNameList getOldNames(ASetBind bind) {
		return PExpAssistantTC.getOldNames(bind.getSet());
	}

}
