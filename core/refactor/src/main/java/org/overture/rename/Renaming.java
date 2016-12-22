package org.overture.rename;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.refactoring.BasicRefactoringType;

public class Renaming extends BasicRefactoringType implements Comparable<Renaming> 
{
	private ILexLocation loc;

	private String oldName;
	private String newName;

	private String oldModule;
	private String newModule;

	public Renaming(ILexLocation loc, String oldName, String newName,
			String oldModule, String newModule)
	{
		this.loc = loc;
		this.oldName = oldName;
		this.newName = newName;
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

	public String getNewName()
	{
		return newName;
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
		return String.format("'%s' changed to '%s' %s", oldName, newName, loc);
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

		if (!(obj instanceof Renaming))
		{
			return false;
		}

		Renaming other = (Renaming) obj;
		return loc.equals(other.getLoc());
	}

	@Override
	public int compareTo(Renaming other)
	{
		if (loc.getModule() != null && other.getLoc().getModule() != null)
		{
			if (!loc.getModule().equals(other.getLoc().getModule()))
			{
				return other.getLoc().getModule().compareTo(loc.getModule());
			}
		}

		ILexLocation otherLoc = other.getLoc();
		return otherLoc.getStartOffset() - loc.getStartOffset();
	}
}