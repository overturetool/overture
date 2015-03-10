package org.overturetool.cgisa.ir;

import java.util.Set;

import org.overture.cgisa.extast.declarations.AExtClassDeclCG;
import org.overture.codegen.ir.IRClassDeclStatus;
import org.overture.codegen.ir.VdmNodeInfo;
import org.overturetool.cgisa.transformations.ExtendClass;

public class ExtIrClassDeclStatus extends IRClassDeclStatus
{

	private AExtClassDeclCG eClassCg;

	public ExtIrClassDeclStatus(Set<VdmNodeInfo> unsupportedInIr,
			AExtClassDeclCG classCg, String className)
	{
		super(className, null, unsupportedInIr);
		this.eClassCg = classCg;
	}

	public ExtIrClassDeclStatus(IRClassDeclStatus status)
	{
		super(status.getClassName(), null, status.getUnsupportedInIr());
		this.eClassCg = ExtendClass.transform(status.getClassCg());
	}

	public String getClassName()
	{
		return className;
	}

	public AExtClassDeclCG getEClassCg()
	{
		return eClassCg;
	}

}
