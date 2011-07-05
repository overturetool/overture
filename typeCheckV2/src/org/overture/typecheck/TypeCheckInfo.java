package org.overture.typecheck;

import org.overture.runtime.Environment;
import org.overture.runtime.TypeList;
import org.overturetool.vdmj.typechecker.NameScope;


public class TypeCheckInfo
{
	public Environment env;
	public NameScope scope;
	public TypeList qualifiers;
}
