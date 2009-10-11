package org.overture.ide.plugins.proofsupport.views.actions;

public class Data
{
	String traceDef;
	String description;
	
	public Data(String traceDef, String Description)
	{
		this.description = Description;
		this.traceDef = traceDef;
		
	}

	public String GetDescription()
	{
		return this.description;
	}

}