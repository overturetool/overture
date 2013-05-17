package org.overture.codegen.newstuff;

import java.util.HashMap;
import java.util.Set;

import org.overture.codegen.cgast.AClassCG;
import org.overture.codegen.cgast.INode;

public class CodeGenTree
{
	private HashMap<String, AClassCG> classes;

	public CodeGenTree()
	{
		super();
		this.classes = new HashMap<String, AClassCG>();
	}
	
	public void registerClass(AClassCG classCg)
	{
		classes.put(classCg.getName(), classCg);
	}
	
	public AClassCG getClass(String className)
	{
		return classes.get(className);
	}
	
	public AClassCG[] getClasses()
	{
		return classes.values().toArray(new AClassCG[0]);
	}
	
}
