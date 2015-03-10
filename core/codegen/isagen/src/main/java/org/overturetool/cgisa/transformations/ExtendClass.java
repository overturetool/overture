package org.overturetool.cgisa.transformations;

import java.util.LinkedList;

import org.overture.cgisa.extast.declarations.AExtClassDeclCG;
import org.overture.cgisa.extast.declarations.AMrFuncGroupDeclCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;

public class ExtendClass
{

	public static AExtClassDeclCG transform(AClassDeclCG clss)
	{
		AExtClassDeclCG extClass;
		extClass = new AExtClassDeclCG();
		extClass.setBaseClass(clss);
		extClass.setMutrecfuncs(new LinkedList<AMrFuncGroupDeclCG>());
		return extClass;
	}

}
