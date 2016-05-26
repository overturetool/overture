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
package org.overture.ide.plugins.combinatorialtesting.internal;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.console.MessageConsole;
import org.overture.combinatorialtesting.vdmj.server.common.Utils;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.IDebugPreferenceConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.launching.VdmLaunchConfigurationDelegate;
import org.overture.ide.debug.utils.VdmProjectClassPathCollector;
import org.overture.ide.plugins.combinatorialtesting.ITracesConstants;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
import org.overture.util.Base64;

public class TestEngineDelegate
{
	static int sessionId = 0;

	public Process launch(TraceExecutionSetup texe,
			IPreferenceStore preferences, File traceFolder, Integer port, IProgressMonitor monitor)
			throws CoreException, IOException
	{
		ProcessBuilder pb = new ProcessBuilder(initializeLaunch(texe, preferences, traceFolder, port));
		
		IProject project = (IProject) texe.project.getAdapter(IProject.class);
		
		IVdmProject vdmProject = (IVdmProject) texe.project.getAdapter(IVdmProject.class);
		
		if(!vdmProject.getModel().isTypeChecked())
		{
			VdmTypeCheckerUi.typeCheck(vdmProject, monitor);
		}
		
		if(!vdmProject.getModel().isTypeCorrect())
		{
			MessageConsole console = VdmjTracesHelper.findConsole(ITracesConstants.TRACES_CONSOLE);
			console.newMessageStream().println(vdmProject.getName() + ": Model is not type correct!");
			monitor.setCanceled(true);
			return null;
		}
		
		File overturePropertiesFile = VdmLaunchConfigurationDelegate.prepareCustomOvertureProperties(vdmProject, null);
		
		String classpath =VdmProjectClassPathCollector.toCpEnvString( VdmProjectClassPathCollector.getClassPath(project, getTraceDebugEngineBundleIds(), overturePropertiesFile));

		Map<String, String> env = pb.environment();
		env.put("CLASSPATH", classpath);
		
		pb.directory(project.getLocation().toFile());

		if (useRemoteDebug(preferences))
		{
			System.out.println("CLASSPATH = " + classpath);
			return null;
		}

		Process process = pb.start();

		// Redirect streams from the trace runner to the streams of this process. If not the trace runner dies on stream
		// write.
		Utils.inheritOutput(process); // Instead of pb.inheritIO() which is Java7 specific;

		return process;
	}
	
	private String[] getTraceDebugEngineBundleIds()
	{
		List<String> ids = new ArrayList<String>(Arrays.asList(ITracesConstants.TEST_ENGINE_BUNDLE_IDs));

		if (VdmDebugPlugin.getDefault().getPreferenceStore().getBoolean(IDebugPreferenceConstants.PREF_DBGP_ENABLE_EXPERIMENTAL_MODELCHECKER))
		{
			ids.add(VdmLaunchConfigurationDelegate.ORG_OVERTURE_IDE_PLUGINS_PROBRUNTIME);
		}
		return ids.toArray(new String[] {});
	}

	private List<String> initializeLaunch(TraceExecutionSetup texe,
			IPreferenceStore preferences, File traceFolder, Integer port)
			throws CoreException, UnsupportedEncodingException
	{
		List<String> commandList = null;
		Integer debugSessionId = new Integer(getSessionId());
		if (useRemoteDebug(preferences))// || ITracesConstants.DEBUG)
		{
			debugSessionId = 1;
			// debugComm.removeSession(debugSessionId.toString());
		}

		commandList = new ArrayList<String>();

		IProject project = (IProject) texe.project.getAdapter(IProject.class);

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

		commandList.add("-t64");
		commandList.add(Base64.encode(texe.traceName.getBytes(charSet)).toString());

		commandList.add("-tracefolder");
		commandList.add(traceFolder.toURI().toASCIIString());

		if (texe.coverageFolder != null)
		{
			commandList.add("-coverage");
			commandList.add(texe.coverageFolder.toURI().toASCIIString());
		}

		if (texe.customReduction)
		{
			commandList.add("-traceReduction");
			commandList.add("{" + texe.subset + "," + texe.reductionType.name()
					+ "," + texe.seed + "}");
		}
		// commandList.addAll(getExtendedCommands(vdmProject, configuration));

		commandList.addAll(getSpecFiles(texe.project));
		if (useRemoteDebug(preferences))// || ITracesConstants.DEBUG)
		{
			System.out.println("Debugger Arguments:\n"
					+ getArgumentString(commandList));
		}
		commandList.add(0, "java");

//		String classPath =VdmProjectClassPathCollector.toCpCliArgument( VdmProjectClassPathCollector.getClassPath(project, ITracesConstants.TEST_ENGINE_BUNDLE_IDs, new String[] {}));
//		commandList.addAll(1,Arrays.asList(new String[]{"-cp", classPath}));
		commandList.add(1, ITracesConstants.TEST_ENGINE_CLASS);
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
		List<String> options = new Vector<String>();
		String opt = preferences.getString(IDebugConstants.VDM_LAUNCH_CONFIG_VM_MEMORY_OPTION);
		if (opt.trim().length() != 0)
		{
			String[] opts = opt.split(" ");
			for (String o : opts)
			{
				o = o.trim();
				if (o.startsWith("-"))
				{
					options.add(o);
				}
			}
		}

		return options;
	}

	private synchronized int getSessionId()
	{

		return sessionId++;
	}

	private boolean useRemoteDebug(IPreferenceStore preferences)
			throws CoreException
	{
		return preferences.getBoolean(ITracesConstants.REMOTE_DEBUG_PREFERENCE);
	}

	// private boolean useDebugInfo(IPreferenceStore preferences)
	// throws CoreException
	// {
	// return preferences.getBoolean(ITracesConstants.ENABLE_DEBUGGING_INFO_PREFERENCE);
	// }

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
