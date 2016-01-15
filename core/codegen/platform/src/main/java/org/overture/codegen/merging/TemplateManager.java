/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.merging;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import org.apache.velocity.Template;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.expressions.ACardUnaryExpCG;
import org.overture.codegen.cgast.expressions.ALenUnaryExpCG;
import org.overture.codegen.utils.GeneralUtils;

public class TemplateManager
{
	public static final String TEMPLATE_FILE_EXTENSION = ".vm";
	
	protected HashMap<Class<? extends INode>, Class<? extends INode>> reuses;

	protected String root;

	private boolean isBase;
	private Class<?> templateLoadRef = null;

	/**
	 * Not for use with extensions. Use {@link #TemplateManager(TemplateStructure, Class)} instead.
	 * 
	 * @param root The template root folder
	 */
	public TemplateManager(String root)
	{
		this.root = root;
		initNodeTemplateFileNames();
		isBase = true;
	}

	/**
	 * This is an extensibility version of the template manager constructor for the benefit of OSGi extensions. <br>
	 * <br>
	 * Because of the way classloaders work in OSGi, the template lookups fail if the templates are located in another
	 * bundle. This provides a workaround by using templateLoadRef to provide a specific class. Just pass it any class
	 * that is native to the extension bundle and it should work.
	 * 
	 * @param root The template root folder
	 * @param templateLoadRef
	 */
	public TemplateManager(String root, Class<?> templateLoadRef)
	{
		this.root = root;
		initNodeTemplateFileNames();
		isBase = false;
		this.templateLoadRef = templateLoadRef;
	}

	/**
	 * Initialize the mapping of IR nodes {@link TemplateStructure}
	 */
	protected void initNodeTemplateFileNames()
	{
		this.reuses = new HashMap<>();
	}

	public Template getTemplate(Class<? extends INode> nodeClass) throws ParseException
	{
		try
		{
			StringBuffer buffer;
			if (isBase)
			{
				buffer = GeneralUtils.readFromFile(getTemplateFileRelativePath(nodeClass));
			} else
			{
				buffer = GeneralUtils.readFromFile(getTemplateFileRelativePath(nodeClass), templateLoadRef);
			}

			if (buffer == null)
			{
				return null;
			}

			return constructTemplate(buffer);

		} catch (IOException e)
		{
			return null;
		}
	}

	protected Template constructTemplate(StringBuffer buffer) throws ParseException
	{
		Template template = new Template();
		RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
		StringReader reader = new StringReader(buffer.toString());

		SimpleNode simpleNode = runtimeServices.parse(reader, "Template name");
		template.setRuntimeServices(runtimeServices);
		template.setData(simpleNode);
		template.initDocument();

		return template;

	}
	
	/**
	 * Enables an IR node to reuse the template of another IR node
	 * 
	 * @param templateOwner The IR node which owns the template subject to reuse
	 * @param reuser The IR node that reuses the template
	 */
	protected void reuseTemplate(Class<ACardUnaryExpCG> templateOwner, Class<ALenUnaryExpCG> reuser)
	{
		reuses.put(reuser, templateOwner);
	}
	
	protected boolean reuses(Class<? extends INode> nodeClass)
	{
		return reuses.containsKey(nodeClass);
	}
	
	protected Class<? extends INode> getTemplateOwner(Class<? extends INode> nodeClass)
	{
		return reuses.get(nodeClass);
	}

	protected String getTemplateFileRelativePath(Class<? extends INode> nodeClass)
	{
		if (reuses(nodeClass))
		{
			nodeClass = getTemplateOwner(nodeClass);
		}

		return root + File.separatorChar + nodeClass.getName().replaceAll("\\.", File.separator)
				+ TEMPLATE_FILE_EXTENSION;
	}
}
