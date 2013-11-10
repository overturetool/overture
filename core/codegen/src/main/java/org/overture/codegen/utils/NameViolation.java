package org.overture.codegen.utils;

import org.overture.ast.intf.lex.ILexLocation;

public class NameViolation
{
	private String name;
	private ILexLocation location;
	
	public NameViolation(String name, ILexLocation location)
	{
		super();
		this.name = name;
		this.location = location;
	}

	public ILexLocation getLocation()
	{
		return location;
	}

	public String getName()
	{
		return name;
	}
	
	@Override
	public String toString()
	{
		return "[Name violation: '" + name + "'. Location: line " + location.getStartLine() + " at position: " + location.getStartPos() + " in " + location.getFile().getName() + "]";
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof NameViolation))
			return false;
		
		NameViolation other = (NameViolation) obj;
		
		return this.location.equals(other.location);
	}
	
}
