package org.overture.codegen.analysis.vdm;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.overture.ast.definitions.PDefinition;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

public class DefinitionInfo
{
	private List<? extends PDefinition> nodeDefs;
	private Map<PDefinition, List<? extends PDefinition>> localDefsMap;
	private ITypeCheckerAssistantFactory af;
	
	public DefinitionInfo(List<? extends PDefinition> nodeDefs, ITypeCheckerAssistantFactory af)
	{
		this.nodeDefs = nodeDefs;
		this.localDefsMap = new HashMap<PDefinition, List<? extends PDefinition>>();
		this.af = af;
		
		for(PDefinition d : nodeDefs)
		{
			localDefsMap.put(d, collectDefs(d));
		}
	}
	
	public List<? extends PDefinition> getNodeDefs()
	{
		return nodeDefs;
	}
	
	public List<PDefinition> getAllLocalDefs()
	{
		return getLocalDefs(nodeDefs);
	}
	
	public List<ILexNameToken> getAllLocalDefNames()
	{
		List<PDefinition> allLocalDefs = getAllLocalDefs();
		
		List<ILexNameToken> names = new LinkedList<>();
		
		for(PDefinition def : allLocalDefs)
		{
			names.add(def.getName());
		}
		
		return names;
	}
	
	public List<PDefinition> getLocalDefs(List<? extends PDefinition> defs)
	{
		List<PDefinition> localDefs = new LinkedList<PDefinition>();
		
		for(PDefinition d : defs)
		{
			List<? extends PDefinition> dd = getLocalDefs(d);
			localDefs.addAll(dd);
		}
		
		return localDefs;
	}
	
	public List<? extends PDefinition> getLocalDefs(PDefinition def)
	{
		return localDefsMap.get(def);
	}
	
	private List<PDefinition> collectDefs(PDefinition d)
	{
		return af.createPDefinitionAssistant().getDefinitions(d);
	}
}
