package org.overturetool.vdmj.lex;

import java.io.Serializable;
import java.util.List;

import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.types.PType;
import org.overturetool.util.Utils;
import org.overturetool.vdmj.messages.InternalException;

public class LexNameToken extends LexToken implements Serializable
{
	private static final long serialVersionUID = 1L;

	public final String module;
	public final String name;
	public final boolean old;
	public final boolean explicit; // Name has an explicit module/class

	public List<PType> typeQualifier = null;

	private int hashcode = 0;

	public LexNameToken(String module, String name, LexLocation location,
			boolean old, boolean explicit)
	{
		super(location, VDMToken.NAME);
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
		super(id.getLocation(), VDMToken.NAME);
		this.module = module;
		this.name = id.getName();
		this.old = id.isOld();
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
		return new LexNameToken(module, new LexIdentifierToken(name, true, location));
	}

	public String getName()
	{
		// Flat specifications have blank module names
		return (explicit ? (module.length() > 0 ? module + "`" : "") : "")
				+ name + (old ? "~" : ""); // NB. No qualifier
	}
	
	public String getSimpleName()
	{
		return name;
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
		} else
		{
			return new LexNameToken(module, "self", location);
		}
	}

	public LexNameToken getThreadName()
	{
		if (module.equals("CLASS"))
		{
			return new LexNameToken(name, "thread", location);
		} else
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

	public void setTypeQualifier(List<PType> types)
	{
		if (hashcode != 0)
		{
			if ((typeQualifier == null && types != null)
					|| (typeQualifier != null && !typeQualifier.equals(types)))
			{
				throw new InternalException(2, "Cannot change type qualifier: "
						+ this + " to " + types);
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

		LexNameToken lother = (LexNameToken) other;

		if (typeQualifier != null && lother.getTypeQualifier() != null)
		{
			throw new InternalException(-1, "Use HelpLexNameToken.isEqual to compare");
			// if (!TypeComparator.compatible(typeQualifier, lother.getTypeQualifier()))
			// {
			// return false;
			// }
		} else if ((typeQualifier != null && lother.getTypeQualifier() == null)
				|| (typeQualifier == null && lother.getTypeQualifier() != null))
		{
			return false;
		}

		return matches(lother);
	}

	public boolean matches(LexNameToken other)
	{
		return module.equals(other.module) && name.equals(other.name)
				&& old == other.old;
	}

	@Override
	public int hashCode()
	{
		if (hashcode == 0)
		{
			hashcode = module.hashCode() + name.hashCode() + (old ? 1 : 0)
					+ (typeQualifier == null ? 0 : typeQualifier.hashCode());
		}

		return hashcode;
	}

	@Override
	public String toString()
	{
		return getName()
				+ (typeQualifier == null ? "" : "("
						+ Utils.listToString(typeQualifier) + ")");
	}

	public LexNameToken copy()
	{
		LexNameToken c = new LexNameToken(module, name, location, old, explicit);
		c.setTypeQualifier(typeQualifier);
		return c;
	}

	public int compareTo(LexNameToken o)
	{
		return toString().compareTo(o.toString());
	}

	public LexLocation getLocation()
	{
		return location;
	}

	public String getModule()
	{
		return module;
	}

	@Override
	public LexNameToken clone()
	{
		return copy();
	}

	public List<PType> getTypeQualifier()
	{
		return typeQualifier;
	}

	public boolean isOld()
	{
		return old;
	}

	@Override
	public void apply(IAnalysis analysis)
	{
		analysis.caseLexNameToken(this);
	}

	@Override
	public <A> A apply(IAnswer<A> caller)
	{
		return caller.caseLexNameToken(this);
	}

	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question)
	{
		caller.caseLexNameToken(this, question);
	}

	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question)
	{
		return caller.caseLexNameToken(this, question);
	}
}
