package org.overture.typechecker.assistant.pattern;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.patterns.AMapletPatternMaplet;
import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.SMapType;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class AMapletPatternMapletAssistantTC
{
	protected ITypeCheckerAssistantFactory af;

	public AMapletPatternMapletAssistantTC(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	//TODO: Cannot be deleted because it is used in AllDefinitionLocator visitor. 
	public Collection<? extends PDefinition> getDefinitions(
			AMapletPatternMaplet p, SMapType map, NameScope scope)
	{

		List<PDefinition> list = new Vector<PDefinition>();
		list.addAll(af.createPPatternAssistant().getDefinitions(p.getFrom(), map.getFrom(), scope));
		list.addAll(af.createPPatternAssistant().getDefinitions(p.getTo(), map.getTo(), scope));
		return list;
	}

}
