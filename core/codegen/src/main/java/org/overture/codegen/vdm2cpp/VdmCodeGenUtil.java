package org.overture.codegen.vdm2cpp;

import java.io.File;
import java.net.URL;

public class VdmCodeGenUtil
{
	public static String getVelocityPropertiesPath(String relativePath)
	{
		String path = null;

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoader.getResource(relativePath);

		File file;
		try
		{
			if (url != null)
			{
				file = new File(url.toURI());
				path = file.getAbsolutePath();
			}
		} catch (Exception e)
		{
		}

		return path;
	}
}
