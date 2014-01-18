package org.overture.codegen.ooast;

import java.util.Set;

import org.overture.ast.node.INode;
import org.overture.codegen.cgast.declarations.AClassDeclCG;

public class ClassDeclStatus extends OoStatus
{
	private AClassDeclCG classCg;
	
	public ClassDeclStatus(AClassDeclCG classCg, Set<INode> unsupportedNodes)
	{
		super(unsupportedNodes);
		this.classCg = classCg;
	}

	public AClassDeclCG getClassCg()
	{
		return classCg;
	}
}
