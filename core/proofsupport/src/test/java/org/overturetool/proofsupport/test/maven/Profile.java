package org.overturetool.proofsupport.test.maven;

import java.util.HashMap;

public class Profile
{
	public final static String USER_VDMTOOLS_CMD_PATH = "proofsupport.vppde.bin";
	public final static String USER_MOS_ML_DIR = "proofsupport.mosml.dir";
	public final static String USER_HOL_DIR = "proofsupport.hol.dir";

	HashMap<String, String> properties = new HashMap<String, String>();
	String id;
	boolean isActive;

	public String getProperty(String name)
	{
		if (properties.containsKey(name))
			return properties.get(name);
		else
			return null;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public boolean isActive()
	{
		return isActive;
	}

	public void setIsActive(boolean isActive)
	{
		this.isActive = isActive;
	}

	public void addProperty(String name, String value)
	{
		if (!properties.containsKey(name))
			properties.put(name, value);
	}
}
