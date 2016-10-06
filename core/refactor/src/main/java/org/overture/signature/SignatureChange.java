package org.overture.signature;

import org.overture.ast.intf.lex.ILexLocation;

public class SignatureChange implements Comparable<SignatureChange> {

	private ILexLocation oldLoc;
	private ILexLocation newLoc;
	private String name;

	private String oldModule;
	private String newModule;

	public SignatureChange(ILexLocation oldLoc, ILexLocation newLoc, String oldModule, String newModule, String name)
	{
		this.oldLoc = oldLoc;
		this.newLoc = newLoc;
		this.oldModule = oldModule;
		this.newModule = newModule;
		this.name = name;
	}

	public ILexLocation getOldLoc()
	{
		return oldLoc;
	}

	public ILexLocation getNewLoc()
	{
		return newLoc;
	}
	
	public String getName()
	{
		return name;
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
		return String.format("'%s' changed to '%s' %s",name ,oldLoc.getStartLine(), newLoc.getStartLine());
	}

	@Override
	public int hashCode()
	{
		return newLoc.hashCode();
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

		return oldLoc.equals(other.oldLoc) && newLoc.equals(other.newLoc);
	}

	@Override
	public int compareTo(SignatureChange other)
	{
		if (newLoc.getModule() != null && other.newLoc.getModule() != null)
		{
			if (!newLoc.getModule().equals(other.newLoc.getModule()))
			{
				return other.newLoc.getModule().compareTo(newLoc.getModule());
			}
		}

		ILexLocation otherLoc = other.getNewLoc();

		return otherLoc.getStartOffset() - newLoc.getStartOffset();
	}

}
	