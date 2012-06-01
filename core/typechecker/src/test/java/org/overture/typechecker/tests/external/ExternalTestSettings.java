package org.overture.typechecker.tests.external;

import java.io.File;

public class ExternalTestSettings
{
	public static File getBasePath(String string)
	{
		String path = System.getProperty("externalTestsPath"); 
		if(path != null)
		{	
			File f = new File(path + string);			
			return f;
		}
		else
		{
			System.out.println("ExternalTestsPath not found");
			return null;
		}
	}
	
	//public static final String basePath = "C:\\Users\\ari\\Desktop\\csktests\\CSKTests\\cskjunit\\";
}
