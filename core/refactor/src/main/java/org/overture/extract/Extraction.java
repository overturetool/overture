package org.overture.extract;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.refactoring.BasicRefactoringType;

public class Extraction  extends BasicRefactoringType implements Comparable<Extraction>
{

	private ILexLocation removedLoc;
	private String removedName;
	private String extractedName;

	public Extraction(ILexLocation removedLoc, String removedName, String extractedName)
	{
		this.removedLoc = removedLoc;
		this.removedName = removedName;
		this.extractedName = extractedName;
	}

	public ILexLocation getRemovedLoc()
	{
		return removedLoc;
	}
	
	public String getName()
	{
		return removedName;
	}

	public String getExtractedName(){
		return extractedName;
	}
	

	@Override
	public String toString()
	{
		StringBuilder stb = new StringBuilder();
		
		stb.append("'" + removedName + "'");
		stb.append(" removed from ");
		stb.append(removedLoc);		
		if(extractedName != null && !extractedName.isEmpty()){
			stb.append(" replaced with ");
			stb.append("'" + extractedName + "'");
		}
		return stb.toString();
	}

	@Override
	public int hashCode()
	{
		return removedLoc.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}

		if (!(obj instanceof Extraction))
		{
			return false;
		}

		Extraction other = (Extraction) obj;

		return removedLoc.equals(other.removedLoc);
	}

	@Override
	public int compareTo(Extraction other)
	{
		if (removedLoc.getModule() != null && other.removedLoc.getModule() != null)
		{
			if (!removedLoc.getModule().equals(other.removedLoc.getModule()))
			{
				return other.removedLoc.getModule().compareTo(removedLoc.getModule());
			}
		}

		ILexLocation otherLoc = other.getRemovedLoc();

		return otherLoc.getStartOffset() - removedLoc.getStartOffset();
	}
}
