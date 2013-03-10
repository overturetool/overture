package org.overture.ide.plugins.combinatorialtesting.store;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.combinatorialtesting.TracesXmlStoreReader;
import org.overture.ide.plugins.combinatorialtesting.TracesXmlStoreReader.TraceInfo;
import org.overture.ide.plugins.combinatorialtesting.TracesXmlStoreReader.TraceStatusXml;
import org.overture.ct.utils.TraceTestResult;
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
	{//project.getLocation().getModelBuildPath().getOutput().
		IProject p = (IProject) project.getAdapter(IProject.class);
		return new File(p.getLocation().toFile().getAbsolutePath(), CT_OUTPUT_DIRECTORY);
	}

	private void initialize() throws IOException
	{
		File classTraceXmlFile = new File(getCtOutputFolder(project), traceDef.getName().module
				+ "-" + traceDef.getName().name + ".xml");
		if (classTraceXmlFile.exists())
		{
			try
			{
				reader = new TracesXmlStoreReader(classTraceXmlFile, traceDef.getName().module);
			} catch (SAXException e)
			{
				// e.printStackTrace();
				// TODO could not parse file. Posible not found
			}

		}
	}

	// public TracesXmlStoreReader getReader() throws IOException
	// {
	// if(reader == null)
	// {
	// initialize();
	// }
	// return reader;
	// }

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
		List<TraceTestResult> testStatus = reader.getTraceTestResults(traceDef.getName().name, 1, reader.getTraceTestCount(traceDef.getName().name));
		return testStatus;
	}

	public Integer getTraceTestCount()
	{
		if (reader == null)
		{
			return 0;
		}
		return reader.getTraceTestCount(traceDef.getName().name);
	}

	public TraceInfo getTraceInfo()
	{
		if (reader == null)
		{
			return null;
		}
		return reader.getTraceInfo(traceDef.getName().name);
	}

	public List<TraceTestResult> getTraceTests(Integer startNumber,
			Integer stopNumber) throws IOException, SAXException
	{

		List<TraceTestResult> list = reader.getTraceTestResults(traceDef.getName().name, startNumber, stopNumber);

		return list;
	}
}
