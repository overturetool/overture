package org.overture.pog.utility;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.patterns.ATypeMultipleBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.PType;

public abstract class ContextHelper
{

	public static List<PMultipleBind> bindListFromPattern(PPattern pattern,
			PType type)
	{
		List<PMultipleBind> bindList = new LinkedList<PMultipleBind>();
		ATypeMultipleBind tmBind = new ATypeMultipleBind();
		List<PPattern> plist = new LinkedList<PPattern>();
		plist.add(pattern.clone());
		tmBind.setPlist(plist);
		tmBind.setType(type.clone());
		bindList.add(tmBind);
		return bindList;
	}

}
