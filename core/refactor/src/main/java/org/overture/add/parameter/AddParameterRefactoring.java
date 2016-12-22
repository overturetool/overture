package org.overture.add.parameter;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.refactoring.BasicRefactoringType;

public class AddParameterRefactoring extends BasicRefactoringType implements Comparable<AddParameterRefactoring> {

	private ILexLocation loc;
	private String paramName;

	private String parentName;
	private String paramType;

	public AddParameterRefactoring(ILexLocation loc, String paramName, String parentName, String paramType)
	{
		this.loc = loc;
		this.parentName = parentName;
		this.paramName = paramName;
		this.paramType = paramType;
	}

	public ILexLocation getLoc()
	{
		return loc;
	}

	public String getParamName() {
		return paramName;
	}

	public String getOperationName() {
		return parentName;
	}
	
	public String getParamType(){
		return paramType;
	}

	@Override
	public String toString()
	{
		StringBuilder stb = new StringBuilder();
		stb.append("Added ");
		stb.append("'" + paramName + "'");
		stb.append(" of type " + "'" + paramType + "'");
		stb.append(" in parent node ");
		stb.append("'" + parentName + "'");
		stb.append(" ");
		stb.append(loc);
		return stb.toString();
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

		if (!(obj instanceof AddParameterRefactoring))
		{
			return false;
		}

		AddParameterRefactoring other = (AddParameterRefactoring) obj;

		return loc.equals(other.loc);
	}

	@Override
	public int compareTo(AddParameterRefactoring other)
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
	