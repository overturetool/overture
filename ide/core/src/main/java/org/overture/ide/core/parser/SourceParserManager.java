/*
 * #%~
 * org.overture.ide.core
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
package org.overture.ide.core.parser;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.overture.ide.core.ICoreConstants;
import org.overture.ide.core.IVdmModel;

import org.overture.ide.core.VdmCore;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;

public class SourceParserManager
{
	/**
	 * A handle to the unique Singleton instance.
	 */
	static private SourceParserManager _instance = null;

	/**
	 * @return The unique instance of this class.
	 */
	static public SourceParserManager getInstance()
	{
		if (null == _instance)
		{
			_instance = new SourceParserManager();
		}
		return _instance;
	}

	/**
	 * Loads a source parser for the given project
	 * 
	 * @param project
	 *            the project to load the source parser for
	 * @return a valid source parser for the current project based on the
	 *         highest priority parser available from the nature, or null if no
	 *         parser could be found
	 * @throws CoreException
	 */
	public ISourceParser getSourceParser(IVdmProject project)
			throws CoreException
	{
		IConfigurationElement[] config = Platform
				.getExtensionRegistry()
				.getConfigurationElementsFor(ICoreConstants.EXTENSION_PARSER_ID);

		IConfigurationElement selectedParser = getParserWithHeighestPriority(
				project.getVdmNature(), config);

		if (selectedParser != null)
		{
			final Object o = selectedParser.createExecutableExtension("class");

			if (o instanceof AbstractParserParticipant)
			{
				AbstractParserParticipant parser = (AbstractParserParticipant) o;

				parser.setProject(project);

				return parser;
			}

		}
		return null;

	}

	/**
	 * Gets the parser available for the given nature having the highest
	 * priority
	 * 
	 * @param natureId
	 *            the nature to lookup a parser for
	 * @param config
	 *            the configuration of the extension point
	 * @return a valid source parser or null
	 */
	private IConfigurationElement getParserWithHeighestPriority(
			String natureId, IConfigurationElement[] config)
	{
		IConfigurationElement selectedParser = null;
		int selectedParserPriority = 0;
		for (IConfigurationElement e : config)
		{
			if (e.getAttribute("nature").equals(natureId))
			{
				if (selectedParser == null)
				{
					selectedParser = e;
					String selectedParserPriorityString = selectedParser
							.getAttribute("priority");
					if (selectedParserPriorityString != null)
						selectedParserPriority = Integer
								.parseInt(selectedParserPriorityString);
				} else
				{
					String parserPriorityString = selectedParser
							.getAttribute("priority");
					if (parserPriorityString != null)
					{
						int parserPriority = Integer
								.parseInt(parserPriorityString);
						if (parserPriority > selectedParserPriority)
						{
							selectedParser = e;
							selectedParserPriority = parserPriority;
						}
					}
				}
			}
		}
		return selectedParser;
	}

	/***
	 * Parses files in a project which has a content type and should be parsed
	 * before a build could be performed
	 * 
	 * @param vdmProject
	 *            the project
	 * @param model
	 *            the model with ast
	 * @param monitor
	 *            an optional monitor, can be null
	 * @throws CoreException
	 * @throws IOException
	 */
	public static void parseMissingFiles(IVdmProject vdmProject,
			IVdmModel model, IProgressMonitor monitor) throws CoreException,
			IOException
	{

		IProject project = (IProject) vdmProject.getAdapter(IProject.class);

		if (monitor != null)
		{
			monitor.subTask("Parsing files for project: " + project.getName());
		}

		Assert.isNotNull(project, "Project could not be adapted");
		if (project != null)
		{
			if (!project.isSynchronized(IResource.DEPTH_INFINITE))
			{
				project.refreshLocal(IResource.DEPTH_INFINITE, null);
			}
			List<IVdmSourceUnit> files = model.getSourceUnits();
			for (IVdmSourceUnit source : files)
			{
				// IVdmModel model = file.getProject().getModel();
				// if (model != null && model.hasFile(file) && file.)
				// return;
				// if(!file.hasParseTree())
				// file.getParseList().clear();
//				System.out.println("Parsing file (build): "+ source);
				parseFile(source);
			}
		}
	}

	/**
	 * Parse a single file from a project
	 * 
	 * @param source
	 *            the file to be parsed
	 * @throws CoreException
	 * @throws IOException
	 */
	public static void parseFile(final IVdmSourceUnit source)
			throws CoreException, IOException
	{
		try
		{
			ISourceParser parser = SourceParserManager.getInstance()
					.getSourceParser(source.getProject());
			Assert.isNotNull(parser, "No parser for file : "
					+ source.toString() + " in project "
					+ source.getProject().toString());
			parser.parse(source);
		} catch (Exception e)
		{
			if (VdmCore.DEBUG)
			{
//				System.err.println("Error in parseFile");
				VdmCore.log("SourceParseManager-Error in parseFile", e);
			}
		}
	}
}
