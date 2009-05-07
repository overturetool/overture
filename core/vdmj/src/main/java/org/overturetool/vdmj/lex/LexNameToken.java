/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj.lex;

import java.io.Serializable;

import org.overturetool.vdmj.messages.MessageException;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.TypeList;

public class LexNameToken extends LexToken implements Serializable
{
	private static final long serialVersionUID = 1L;

	public final String module;
	public final String name;
	public final boolean old;
	public final boolean explicit;	// Name has an explicit module/class

	public TypeList typeQualifier = null;

	private int hashcode = 0;

	public LexNameToken(
		String module, String name, LexLocation location,
		boolean old, boolean explicit)
	{
		super(location, Token.NAME);
		this.module = module;
		this.name = name;
		this.old = old;
		this.explicit = explicit;
	}

	public LexNameToken(String module, String name, LexLocation location)
	{
		this(module, name, location, false, false);
	}

	public LexNameToken(String module, LexIdentifierToken id)
	{
		super(id.location, Token.NAME);
		this.module = module;
		this.name = id.name;
		this.old = id.old;
		this.explicit = false;
	}

	public LexIdentifierToken getIdentifier()
	{
		return new LexIdentifierToken(name, old, location);
	}

	public LexNameToken getExplicit(boolean ex)
	{
		return new LexNameToken(module, name, location, old, ex);
	}

	public LexNameToken getOldName()
	{
		return new LexNameToken(module,
			new LexIdentifierToken(name, true, location));
	}

	public String getName()
	{
		// Flat specifications have blank module names
		return (explicit ? (module.length() > 0 ? module + "`" : "") : "") +
				name + (old ? "~" : "");	// NB. No qualifier
	}

	public LexNameToken getPreName(LexLocation l)
	{
		return new LexNameToken(module, "pre_" + name, l);
	}

	public LexNameToken getPostName(LexLocation l)
	{
		return new LexNameToken(module, "post_" + name, l);
	}

	public LexNameToken getInvName(LexLocation l)
	{
		return new LexNameToken(module, "inv_" + name, l);
	}

	public LexNameToken getInitName(LexLocation l)
	{
		return new LexNameToken(module, "init_" + name, l);
	}

	public LexNameToken getModifiedName(String classname)
	{
		LexNameToken mod = new LexNameToken(classname, name, location);
		mod.setTypeQualifier(typeQualifier);
		return mod;
	}

	public LexNameToken getSelfName()
	{
		if (module.equals("CLASS"))
		{
			return new LexNameToken(name, "self", location);
		}
		else
		{
			return new LexNameToken(module, "self", location);
		}
	}

	public LexNameToken getThreadName()
	{
		if (module.equals("CLASS"))
		{
			return new LexNameToken(name, "thread", location);
		}
		else
		{
			return new LexNameToken(module, "thread", location);
		}
	}

	public static LexNameToken getThreadName(LexLocation loc)
	{
		return new LexNameToken(loc.module, "thread", loc);
	}

	public LexNameToken getPerName(LexLocation loc)
	{
		return new LexNameToken(module, "per_" + name, loc);
	}

	public LexNameToken getClassName()
	{
		return new LexNameToken("CLASS", name, location);
	}

	public void setTypeQualifier(TypeList types)
	{
		if (hashcode != 0)
		{
			if ((typeQualifier == null && types != null) ||
				(typeQualifier != null && !typeQualifier.equals(types)))
			{
				throw new MessageException(
					"Internal 0002: Cannot change type qualifier: " + this + " to " + types);
			}
		}

		typeQualifier = types;
	}

	@Override
	public boolean equals(Object other)
	{
		if (!(other instanceof LexNameToken))
		{
			return false;
		}

		LexNameToken lother = (LexNameToken)other;

		if (typeQualifier != null && lother.typeQualifier != null)
		{
			if (!TypeComparator.compatible(typeQualifier, lother.typeQualifier))
			{
				return false;
			}
		}
		else if ((typeQualifier != null && lother.typeQualifier == null) ||
				 (typeQualifier == null && lother.typeQualifier != null))
		{
			return false;
		}

		return matches(lother);
	}

	public boolean matches(LexNameToken other)
	{
		return module.equals(other.module) &&
				name.equals(other.name) &&
				old == other.old;
	}

	@Override
	public int hashCode()
	{
		if (hashcode == 0)
		{
			hashcode = module.hashCode() + name.hashCode() + (old ? 1 : 0) +
				(typeQualifier == null ? 0 : typeQualifier.hashCode());
		}

		return hashcode;
	}

	@Override
	public String toString()
	{
		return getName() + (typeQualifier == null ? "" : typeQualifier);
	}

	public LexNameToken copy()
	{
		LexNameToken c = new LexNameToken(module, name, location, old, explicit);
		c.setTypeQualifier(typeQualifier);
		return c;
	}
}
