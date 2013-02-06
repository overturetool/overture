package org.overture.prettyprinter;

public class PrettyPrinterEnv
{
	StringBuilder sb = new StringBuilder();
	String className = "";
	
	public void setClassName(String name)
	{
		this.className = name;
	}
	
	public String getClassName()
	{
		return this.className ;
	}
	
	public String increaseIdent()
	{
		sb.append("  ");
		return getIdent();
	}
	
	public String decreaseIdent()
	{
		if(sb.length() > 0)
		{
			sb.setLength(sb.length() - 2);			
		}
		return getIdent();
	}
	
	public String getIdent()
	{
		return sb.toString();
	}
}
