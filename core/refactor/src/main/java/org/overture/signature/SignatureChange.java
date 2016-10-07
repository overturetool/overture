package org.overture.signature;

import org.overture.ast.intf.lex.ILexLocation;

public class SignatureChange implements Comparable<SignatureChange> {

	private ILexLocation loc;

	private String oldName;

	private String oldModule;
	private String newModule;

	public SignatureChange(ILexLocation loc, String oldName,
			String oldModule, String newModule)
	{
		this.loc = loc;
		this.oldName = oldName;
		this.oldModule = oldModule;
		this.newModule = newModule;
	}

	public ILexLocation getLoc()
	{
		return loc;
	}

	public String getOldName()
	{
		return oldName;
	}

	public String getOldModule()
	{
		return oldModule;
	}

	public String getNewModule()
	{
		return newModule;
	}

	@Override
	public String toString()
	{
		return String.format("'%s' changed to '%s' %s", oldName, loc);
	}

	@Override
	public int hashCode()
	{
		return loc.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (!(obj instanceof SignatureChange))
		{
			return false;
		}

		SignatureChange other = (SignatureChange) obj;

		return loc.equals(other.loc);
	}

	@Override
	public int compareTo(SignatureChange other)
	{
		if (loc.getModule() != null && other.loc.getModule() != null)
		{
			if (!loc.getModule().equals(other.loc.getModule()))
			{
				return other.loc.getModule().compareTo(loc.getModule());
			}
		}

		ILexLocation otherLoc = other.getLoc();

		return otherLoc.getStartOffset() - loc.getStartOffset();
	}
}
	