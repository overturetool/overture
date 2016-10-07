package org.overture.signature;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.refactoring.BasicRefactoringType;

public class SignatureChange extends BasicRefactoringType implements Comparable<SignatureChange> {

	private ILexLocation loc;
	private String paramName;
	private boolean isAddParam;
	private String operationName;

	public SignatureChange(ILexLocation loc, String paramName, String operationName, boolean isAddParam)
	{
		this.loc = loc;
		this.operationName = operationName;
		this.paramName = paramName;
		this.isAddParam = isAddParam;
	}

	public ILexLocation getLoc()
	{
		return loc;
	}

	public String getParamName() {
		return paramName;
	}

	public String getOperationName() {
		return operationName;
	}
	
	public boolean isAddParam() {
		return isAddParam;
	}

	@Override
	public String toString()
	{
		StringBuilder stb = new StringBuilder();
		
		if(isAddParam){
			stb.append("'" + paramName + "'");
			stb.append(" added to ");
			stb.append(operationName);
			stb.append(" in ");
			stb.append(loc);
		}
		
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
	