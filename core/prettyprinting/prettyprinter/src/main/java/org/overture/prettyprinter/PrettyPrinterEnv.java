package org.overture.prettyprinter;

public class PrettyPrinterEnv
{
	StringBuilder sb = new StringBuilder();
	
	public void increaseIdent()
	{
		sb.append("  ");
	}
	
	public void decreaseIdent()
	{
		if(sb.length() > 0)
		{
			sb.setLength(sb.length() - 2);			
		}
	}
	
	public String getIdent()
	{
		return sb.toString();
	}
}
