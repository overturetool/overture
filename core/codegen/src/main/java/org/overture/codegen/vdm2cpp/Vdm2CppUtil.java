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
	public static Template getTemplate(String relativePath)
	{
		Template template = null;

		try
		{
			StringBuffer buffer = readFromFile(relativePath);
			RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
			StringReader reader = new StringReader(buffer.toString());
			SimpleNode node = runtimeServices.parse(reader, "Template name");

			template = new Template();
			template.setRuntimeServices(runtimeServices);
			template.setData(node);
			template.initDocument();

		} catch (Exception e)
		{
		}

		return template;
	}

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

	private static StringBuffer readFromFile(String relativepath)
			throws IOException
	{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream(relativepath);
		StringBuffer data = new StringBuffer();
		int c = 0;
		while ((c = input.read()) != -1)
		{
			data.append((char) c);
		}
		input.close();

		return data;
	}
}
