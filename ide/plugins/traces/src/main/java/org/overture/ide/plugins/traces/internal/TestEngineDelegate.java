package org.overture.ide.plugins.traces.internal;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.core.utility.ClasspathUtils;
import org.overture.ide.plugins.traces.ITracesConstants;
import org.overture.util.Base64;
import org.overturetool.traces.vdmj.server.common.Utils;

public class TestEngineDelegate
{
	static int sessionId = 0;

	public Process launch(TraceExecutionSetup texe, IPreferenceStore preferences,
			 File traceFolder, 
			Integer port) throws CoreException, IOException
	{
		ProcessBuilder pb = new ProcessBuilder(initializeLaunch(texe, preferences,  traceFolder, port));

		IProject project = (IProject)texe.project.getAdapter(IProject.class);

		pb.directory(project.getLocation().toFile());

		if (useRemoteDebug(preferences))
		{
			return null;
		} 
			
		Process process = pb.start();
		
		//Redirect streams from the trace runner to the streams of this process. If not the trace runner dies on stream write.
		Utils.inheritOutput(process); //Instead of pb.inheritIO() which is Java7 specific;
		
		
		return process;
	}

	private List<String> initializeLaunch(TraceExecutionSetup texe,
			IPreferenceStore preferences,
			File traceFolder, Integer port)
			throws CoreException, UnsupportedEncodingException
	{
		List<String> commandList = null;
		Integer debugSessionId = new Integer(getSessionId());
		if (useRemoteDebug(preferences) )//|| ITracesConstants.DEBUG)
		{
			debugSessionId = 1;
			// debugComm.removeSession(debugSessionId.toString());
		}

		commandList = new ArrayList<String>();

		IProject project = (IProject)texe.project.getAdapter(IProject.class);

		String charSet = project.getDefaultCharset();

		// -h localhost -p 9009 -k 1 -w -q -vdmpp -r classic -c Cp1252
		// -e UseTree -default64 VHJlZQ== -consoleName
		// LaunchConfigurationExpression
		// -tracefolder file:/C:/overture/runtime-ide.productIMpor/treePP/generated/traces
		// -coverage file:/C:/overture/runtime-ide.productIMpor/treePP/generated/coverage
		// file:/C:/overture/runtime-ide.productIMpor/treePP/avl.vdmpp
		// file:/C:/overture/runtime-ide.productIMpor/treePP/bst.vdmpp
		// file:/C:/overture/runtime-ide.productIMpor/treePP/queue.vdmpp
		// file:/C:/overture/runtime-ide.productIMpor/treePP/tree.vdmpp
		// file:/C:/overture/runtime-ide.productIMpor/treePP/usetree.vdmpp

		commandList.add("-h");
		commandList.add("localhost");
		commandList.add("-p");

		commandList.add(port.toString());
		commandList.add("-k");
		commandList.add(debugSessionId.toString());
		commandList.add("-w");
		commandList.add("-q");
		commandList.add(texe.project.getDialect().getArgstring());
		commandList.add("-r");
		commandList.add(texe.project.getLanguageVersionName());
		commandList.add("-c");
		commandList.add(charSet);

		commandList.add("-e64");
		commandList.add(Base64.encode(texe.container.getBytes(charSet)).toString());
		commandList.add("-default64");
		commandList.add(Base64.encode(texe.container.getBytes(charSet)).toString());

		commandList.add("-consoleName");
		commandList.add("LaunchConfigurationExpression");

		commandList.add("-t");
		commandList.add(texe.traceName);

		commandList.add("-tracefolder");
		commandList.add(traceFolder.toURI().toASCIIString());

		if (texe.coverageFolder!= null)
		{
			commandList.add("-coverage");
			commandList.add(texe.coverageFolder.toURI().toASCIIString());
		}
		
		if(texe.customReduction)
		{
			commandList.add("-traceReduction");
			commandList.add("{"+texe.subset+","+texe.reductionType+","+texe.seed+"}");
		}
		// commandList.addAll(getExtendedCommands(vdmProject, configuration));

		commandList.addAll(getSpecFiles(texe.project));
		if (useRemoteDebug(preferences))//|| ITracesConstants.DEBUG)
		{
			System.out.println("Debugger Arguments:\n"
					+ getArgumentString(commandList));
		}
		commandList.add(0, "java");

		commandList.addAll(1, getClassPath(project));
		commandList.add(3, ITracesConstants.TEST_ENGINE_CLASS);
		commandList.addAll(1, getVmArguments(preferences));

		if (useRemoteDebug(preferences))
		{
			System.out.println("Full Debugger Arguments:\n"
					+ getArgumentString(commandList));
		}
		
		return commandList;
	}

	private Collection<? extends String> getVmArguments(
			IPreferenceStore preferences)
	{
		return new Vector<String>();
	}

	private synchronized int getSessionId()
	{

		return (sessionId++);
	}

	private boolean useRemoteDebug(IPreferenceStore preferences)
			throws CoreException
	{
		return preferences.getBoolean(ITracesConstants.REMOTE_DEBUG_PREFERENCE);
	}
	
//	private boolean useDebugInfo(IPreferenceStore preferences)
//			throws CoreException
//	{
//		return preferences.getBoolean(ITracesConstants.ENABLE_DEBUGGING_INFO_PREFERENCE);
//	}

	private List<String> getSpecFiles(IVdmProject project) throws CoreException
	{
		List<String> files = new Vector<String>();

		for (IVdmSourceUnit unit : project.getSpecFiles())
		{
			files.add(unit.getSystemFile().toURI().toASCIIString());
		}

		return files;
	}

	private String getArgumentString(List<String> args)
	{
		String executeString = "";
		for (String string : args)
		{
			executeString += string + " ";
		}
		return executeString.trim();

	}

	private List<String> getClassPath(IProject project) throws CoreException
	{
		List<String> commandList = new Vector<String>();
		List<String> entries = new Vector<String>();
		//FIXME Array of all of the bundles containing jars that we depend on (HACK)
		String[] requiredBundles = new String[] {
					ITracesConstants.TEST_ENGINE_BUNDLE_ID,
					"org.overture.ide.core" //FIXME i.e. this is the HACK: need to generalise
					};
		// get the bundled class path of the debugger
		ClasspathUtils.collectClasspath(requiredBundles, entries);
		// get the class path for all jars in the project lib folder
		File lib = new File(project.getLocation().toFile(), "lib");
		if (lib.exists() && lib.isDirectory())
		{
			for (File f : getAllFiles(lib))
			{
				if (f.getName().toLowerCase().endsWith(".jar"))
				{
					entries.add(toPlatformPath(f.getAbsolutePath()));
				}
			}
		}

		if (entries.size() > 0)
		{
			commandList.add("-cp");
			String classPath = " ";
			for (String cp : entries)
			{
				if (cp.toLowerCase().replace("\"", "").trim().endsWith(".jar"))
				{
					classPath += toPlatformPath(cp) + getCpSeperator();
				}
			}
			classPath = classPath.substring(0, classPath.length() - 1);
			commandList.add(classPath.trim());

		}
		return commandList;
	}

	private static List<File> getAllFiles(File file)
	{
		List<File> files = new Vector<File>();
		if (file.isDirectory())
		{
			for (File f : file.listFiles())
			{
				files.addAll(getAllFiles(f));
			}

		} else
		{
			files.add(file);
		}
		return files;
	}

	private String getCpSeperator()
	{
		if (isWindowsPlatform())
			return ";";
		else
			return ":";
	}

	public static boolean isWindowsPlatform()
	{
		return System.getProperty("os.name").toLowerCase().contains("win");
	}

	protected static String toPlatformPath(String path)
	{
		if (isWindowsPlatform())
		{
			return "\"" + path + "\"";
		} else
		{
			return path.replace(" ", "\\ ");
		}
	}

	/**
	 * Returns a free port number on localhost, or -1 if unable to find a free port.
	 * 
	 * @return a free port number on localhost, or -1 if unable to find a free port
	 * @since 3.0
	 */
	public static int findFreePort()
	{
		ServerSocket socket = null;
		try
		{
			socket = new ServerSocket(0);
			return socket.getLocalPort();
		} catch (IOException e)
		{
		} finally
		{
			if (socket != null)
			{
				try
				{
					socket.close();
				} catch (IOException e)
				{
				}
			}
		}
		return -1;
	}

}
