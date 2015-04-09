package org.overture.codegen.analysis.vdm;

import org.overture.ast.intf.lex.ILexLocation;

public class Renaming implements Comparable<Renaming>
{
	private ILexLocation loc;
	private String oldName;
	private String newName;
	
	public Renaming(ILexLocation loc, String oldName, String newName)
	{
		if(loc == null)
		{
			throw new IllegalArgumentException("Location cannot be null in Renaming");
		}
		
		if(oldName == null || oldName.isEmpty())
		{
			throw new IllegalArgumentException("The old name of a renaming cannot be null or the empty String");
		}
		
		if(newName == null || newName.isEmpty())
		{
			throw new IllegalArgumentException("The new name of a renaming cannot be null or the empty String");
		}
		
		this.loc = loc;
		this.oldName = oldName;
		this.newName = newName;
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
	
	@Override
	public String toString()
	{
		return  String.format("'%s' changed to '%s' %s", oldName, newName, loc);
	}
	
	@Override
	public int hashCode()
	{
		return loc.hashCode();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
		{
			return true;
		}
		
		if(!(obj instanceof Renaming))
		{
			return false;
		}
		
		Renaming other = (Renaming) obj;
		
		return loc.equals(other.loc);
	}

	@Override
	public int compareTo(Renaming other)
	{
		if(loc.getModule() != null && other.loc.getModule() != null)
		{
			if(!loc.getModule().equals(other.loc.getModule()))
			{
				return other.loc.getModule().compareTo(loc.getModule());
			}
		}
		
		ILexLocation otherLoc = other.getLoc();
		
		return otherLoc.getStartOffset() - loc.getStartOffset();
	}
}
