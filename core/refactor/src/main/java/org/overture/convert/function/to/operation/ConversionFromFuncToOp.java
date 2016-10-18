package org.overture.convert.function.to.operation;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.refactoring.BasicRefactoringType;
import org.overture.rename.Renaming;

public class ConversionFromFuncToOp extends BasicRefactoringType implements Comparable<ConversionFromFuncToOp> 
{
	private ILexLocation loc;
	private String name;
	
	public ConversionFromFuncToOp(ILexLocation loc, String name)
	{
		this.loc = loc;
		this.name = name;

	}

	public ILexLocation getLoc()
	{
		return loc;
	}

	public String getName()
	{
		return name;
	}


	@Override
	public String toString()
	{
		return String.format("'%s' converted to operation at %s", name, loc);
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
	public int compareTo(ConversionFromFuncToOp other)
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