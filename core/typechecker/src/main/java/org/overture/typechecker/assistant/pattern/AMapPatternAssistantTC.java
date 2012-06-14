package org.overture.typechecker.assistant.pattern;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.AMapPattern;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;
import org.overture.ast.types.SMapType;
import org.overture.typechecker.TypeCheckException;
import org.overture.typechecker.TypeCheckInfo;
import org.overture.typechecker.TypeCheckerErrors;
import org.overture.typechecker.assistant.type.PTypeAssistantTC;

public class AMapPatternAssistantTC {

	public static void unResolve(AMapPattern pattern) {

		for (AMapletPatternMaplet mp : pattern.getMaplets()) {
			AMapletPatternMapletAssistantTC.unResolve(mp);
		}
		
		pattern.setResolved(false);
	}

	public static void typeResolve(AMapPattern pattern, QuestionAnswerAdaptor<TypeCheckInfo, PType> rootVisitor, TypeCheckInfo question) throws Throwable {
		
		if(pattern.getResolved())return; else { pattern.setResolved(true);}
		
		try
		{
			for (AMapletPatternMaplet mp : pattern.getMaplets()) {
				AMapletPatternMapletAssistantTC.typeResolve(mp,rootVisitor,question);
			}
		}
		catch (TypeCheckException e) {
			unResolve(pattern);
			throw e;
		}
		
	}

	public static List<PDefinition> getAllDefinitions(AMapPattern rp,
			PType ptype, NameScope scope) {

		List<PDefinition> defs = new Vector<PDefinition>();
		
		if(!PTypeAssistantTC.isMap(ptype))
		{
			TypeCheckerErrors.report(3314, "Map pattern is not matched against map type",rp.getLocation(),rp);
			TypeCheckerErrors.detail("Actual type", ptype);
		}
		else
		{
			SMapType map = PTypeAssistantTC.getMap(ptype);
			
			if(!map.getEmpty())
			{
				for (AMapletPatternMaplet p : rp.getMaplets()) {
					defs.addAll(AMapletPatternMapletAssistantTC.getDefinitions(p,map,scope));
				}
			}
		}
		
		return defs;
	}

}
