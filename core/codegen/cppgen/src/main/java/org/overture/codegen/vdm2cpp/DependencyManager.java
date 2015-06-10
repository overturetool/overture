package org.overture.codegen.vdm2cpp;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DependencyManager 
{
	private Map<String, List<String>> mapping;
	private Map<String, ExtDependency> ext_map;
	private String self_name;
	public DependencyManager(String class_name)
	{
		self_name = class_name;
		mapping = new HashMap<String ,List<String> >();
		ext_map = new HashMap<String, ExtDependency>();
	}
	
	public void addTargetLanguageType(String name,String namespace, String include)
	{
		if( !ext_map.containsKey(name))
		{
			ext_map.put(name, new ExtDependency(name, namespace, include));
		}
	}
	
	public void addClassType(String name,String type)
	{
		if(name.equals(self_name))
		{
			return;
		}
		
		if(mapping.containsKey(name))
		{
			mapping.get(name).add(type);
		}
		else
		{
			LinkedList<String> e =  new LinkedList<String>();
			e.add(type);
			mapping.put(name,e);
		}
	};
	
	public Set<String> getDependeciesVDM()
	{
		return mapping.keySet();
	}
	
	public Collection<ExtDependency> getDependenciesTargetLanguage()
	{
		return ext_map.values();
	}

}
