package org.overture.typechecker.assistant.pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.overture.ast.factory.AstFactory;
import org.overture.ast.patterns.ASetBind;
import org.overture.ast.patterns.PMultipleBind;
import org.overture.ast.patterns.PPattern;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class ASetBindAssistantTC
{
	protected static ITypeCheckerAssistantFactory af;

	@SuppressWarnings("static-access")
	public ASetBindAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}

	//TODO: used in the File POForAllContext. I don't know what to do.
	//need to ask immediately.
	public List<PMultipleBind> getMultipleBindList(ASetBind bind)
	{

		List<PPattern> plist = new ArrayList<PPattern>();
		plist.add(bind.getPattern());
		List<PMultipleBind> mblist = new Vector<PMultipleBind>();
		mblist.add(AstFactory.newASetMultipleBind(plist, bind.getSet()));
		return mblist;
	}

}
