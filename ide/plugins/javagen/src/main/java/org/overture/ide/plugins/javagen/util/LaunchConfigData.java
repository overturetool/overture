package org.overture.ide.plugins.javagen.util;

public class LaunchConfigData
{
	private String name;
	private String exp;
	
	public LaunchConfigData(String name, String exp)
	{
		this.name = name;
		this.exp = exp;
	}

	public String getName()
	{
		return name;
	}

	public String getExp()
	{
		return exp;
	}

	@Override
	public String toString()
	{
		return name + " - " + exp;
	}
}
