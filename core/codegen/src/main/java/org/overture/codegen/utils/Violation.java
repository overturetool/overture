package org.overture.codegen.utils;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.codegen.assistant.LocationAssistantCG;

public class Violation implements Comparable<Violation>
{
	protected ILexLocation location;
	protected String description;
	
	public Violation(String description, ILexLocation location)
	{
		super();
		this.description = description;
		this.location = location;
	}

	public String getDescripton()
	{
		return description;
	}

	public ILexLocation getLocation()
	{
		return location;
	}
	
	@Override
	public String toString()
	{
		return "[Violation in module " + location.getModule() + ": '" + description + "'. Location: line " + location.getStartLine() + " at position: " + location.getStartPos() + " in " + location.getFile().getName() + "]";
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof Violation))
			return false;
		
		Violation other = (Violation) obj;
		
		return this.description.equals(other.description) && this.location.equals(other.location);
	}

	@Override
	public int compareTo(Violation other)
	{
		return LocationAssistantCG.compareLocations(this.location, other.location);
	}
}
