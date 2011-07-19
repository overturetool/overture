package org.overture.typecheck;

import java.util.LinkedList;

import org.overture.ast.types.PType;
import org.overture.runtime.Environment;
import org.overturetool.vdmj.typechecker.NameScope;


public class TypeCheckInfo
{
	public Environment env;
	public NameScope scope;
	public LinkedList<PType> qualifiers;
}
