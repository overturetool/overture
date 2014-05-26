package org.overture.codegen.ooast;

import java.util.Set;

import org.overture.codegen.cgast.declarations.AClassDeclCG;

public class ClassDeclStatus extends OoStatus
{
	private String className;
	private AClassDeclCG classCg;
	
	public ClassDeclStatus(String className, AClassDeclCG classCg, Set<NodeInfo> unsupportedNodes)
	{
		super(unsupportedNodes);
		this.className = className;
		this.classCg = classCg;
	}

	public String getClassName()
	{
		return className;
	}
	
	public AClassDeclCG getClassCg()
	{
		return classCg;
	}
}
