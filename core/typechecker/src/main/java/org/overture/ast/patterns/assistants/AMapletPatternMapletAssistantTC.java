package org.overture.ast.patterns.assistants;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.typecheck.TypeCheckException;
import org.overture.typecheck.TypeCheckInfo;
import org.overturetool.vdmj.typechecker.NameScope;

public class AMapletPatternMapletAssistantTC {

	public static void unResolve(AMapletPatternMaplet mp) {
		PPatternAssistantTC.unResolve(mp.getFrom());
		PPatternAssistantTC.unResolve(mp.getTo());
		mp.setResolved(false);
		
	}

	public static void typeResolve(AMapletPatternMaplet mp, QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question) {
		if (mp.getResolved()) return; else { mp.setResolved(true); }

		try
		{
			PPatternAssistantTC.typeResolve(mp.getFrom(),rootVisitor, question);
			PPatternAssistantTC.typeResolve(mp.getTo(),rootVisitor, question);
		}
		catch (TypeCheckException e)
		{
			unResolve(mp);
			throw e;
		}
	}

	public static Collection<? extends PDefinition> getDefinitions(
			AMapletPatternMaplet p, SMapType map, NameScope scope) {
		
		List<PDefinition> list = new Vector<PDefinition>();
		list.addAll(PPatternAssistantTC.getAllDefinitions(p.getFrom(), map.getFrom(), scope));
		list.addAll(PPatternAssistantTC.getAllDefinitions(p.getTo(), map.getTo(), scope));
		return list;
	}

}
