package org.overture.typechecker;

import java.util.LinkedList;

import org.overture.ast.typechecker.NameScope;
import org.overture.ast.types.PType;

public class TypeCheckInfo
{
	final public Environment env;
	public NameScope scope;
	public LinkedList<PType> qualifiers;

	public TypeCheckInfo(Environment env, NameScope scope,
			LinkedList<PType> qualifiers)
	{
		this.env = env;
		this.scope = scope;
		this.qualifiers = qualifiers;
	}

	public TypeCheckInfo(Environment env, NameScope scope)
	{
		this.env = env;
		this.scope = scope;
	}

	public TypeCheckInfo(Environment env)
	{
		this.env = env;
	}

	public TypeCheckInfo()
	{
		env = null;
	}
}
