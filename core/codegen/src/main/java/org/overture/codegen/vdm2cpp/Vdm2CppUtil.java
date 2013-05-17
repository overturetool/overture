package org.overture.codegen.vdm2cpp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;

public class Vdm2CppUtil
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
