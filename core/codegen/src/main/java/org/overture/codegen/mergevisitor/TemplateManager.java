package org.overture.codegen.mergevisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.overture.codegen.cgast.INode;

public class TemplateManager
{
	private static final String TEMPLATE_FILE_EXTENSION = ".vm";
	
	private String fileNameSuffix;
	
	public TemplateManager(String fileNameSuffix)
	{
		this.fileNameSuffix = fileNameSuffix;
	}
	
	public Template getTemplate(Class<? extends INode> nodeClass)
	{
		Template template = null;

		try
		{
			StringBuffer buffer = readFromFile(getTemplateFileRelativePath(nodeClass));
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
	
	private String getTemplateFileRelativePath(Class<? extends INode> nodeClass)
	{
		return nodeClass.getSimpleName().toLowerCase() + fileNameSuffix + TEMPLATE_FILE_EXTENSION;
	}
	
	private StringBuffer readFromFile(String relativepath)
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
