package org.overture.typecheck;

import org.overture.ast.node.NodeList;
import org.overture.ast.types.PType;
import org.overture.runtime.Environment;
import org.overturetool.vdmj.typechecker.NameScope;


public class TypeCheckInfo
{
	public Environment env;
	public NameScope scope;
	public NodeList<PType> qualifiers;
}
