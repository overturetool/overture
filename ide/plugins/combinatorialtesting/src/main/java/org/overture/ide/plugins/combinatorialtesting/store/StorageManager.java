/*
 * #%~
 * Combinatorial Testing
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
package org.overture.ide.plugins.combinatorialtesting.store;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ct.utils.TraceTestResult;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.combinatorialtesting.TracesXmlStoreReader;
import org.overture.ide.plugins.combinatorialtesting.TracesXmlStoreReader.TraceInfo;
import org.overture.ide.plugins.combinatorialtesting.TracesXmlStoreReader.TraceStatusXml;
import org.xml.sax.SAXException;

public class StorageManager
{
	final static String CT_OUTPUT_DIRECTORY = "Combinatorial_Testing";
	final IVdmProject project;
	TracesXmlStoreReader reader = null;
	ANamedTraceDefinition traceDef = null;

	public StorageManager(IVdmProject project, ANamedTraceDefinition traceDef)
			throws IOException
	{
		this.project = project;
		this.traceDef = traceDef;

		initialize();
	}

	public static File getCtOutputFolder(IVdmProject project)
	{
		return new File(project.getModelBuildPath().getOutput().getLocation().toFile().getAbsolutePath(), CT_OUTPUT_DIRECTORY);
	}

	private void initialize() throws IOException
	{
		File classTraceXmlFile = new File(getCtOutputFolder(project), traceDef.getName().getModule()
				+ "-" + traceDef.getName().getName() + ".xml");
		if (classTraceXmlFile.exists())
		{
			try
			{
				IProject p = (IProject) project.getAdapter(IProject.class);
				reader = new TracesXmlStoreReader(classTraceXmlFile, traceDef.getName().getModule(), p.getDefaultCharset());
			} catch (SAXException e)
			{
				// e.printStackTrace();
				// TODO could not parse file. Posible not found
			} catch (CoreException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public int getSkippedCount(String traceName)
	{
		if (reader != null)
		{
			Map<String, TraceStatusXml> traceStatus = reader.getTraceStatus();
			if (traceStatus != null && traceStatus.containsKey(traceName))
			{
				return traceStatus.get(traceName).getSkippedTestCount();
			}
		}

		return 0;
	}

	public List<TraceTestResult> getTraceTests() throws IOException,
			SAXException

	{
		List<TraceTestResult> testStatus = reader.getTraceTestResults(traceDef.getName().getName(), 1, reader.getTraceTestCount(traceDef.getName().getName()));
		return testStatus;
	}

	public Integer getTraceTestCount()
	{
		if (reader == null)
		{
			return 0;
		}
		return reader.getTraceTestCount(traceDef.getName().getName());
	}

	public TraceInfo getTraceInfo()
	{
		if (reader == null)
		{
			return null;
		}
		return reader.getTraceInfo(traceDef.getName().getName());
	}

	public List<TraceTestResult> getTraceTests(Integer startNumber,
			Integer stopNumber) throws IOException, SAXException
	{

		List<TraceTestResult> list = reader.getTraceTestResults(traceDef.getName().getName(), startNumber, stopNumber);

		return list;
	}
}
