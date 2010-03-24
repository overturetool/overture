package org.overture.ide.debug.core.launching;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.overture.ide.core.IVdmProject;
import org.overture.ide.core.IVdmSourceUnit;
import org.overture.ide.core.VdmProject;
import org.overture.ide.core.utility.ClasspathUtils;
import org.overture.ide.debug.core.Activator;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.model.VdmDebugTarget;
import org.overture.ide.debug.utils.ProcessConsolePrinter;
import org.overture.ide.ui.internal.util.ConsoleWriter;
import org.overturetool.vdmj.util.Base64;

/**
 * @see http://www.eclipse.org/articles/Article-Debugger/how-to.html
 * @author ari
 * 
 */
public class VdmLaunchConfigurationDelegate implements
		ILaunchConfigurationDelegate
{
	class SocketAcceptor implements Runnable
	{
		private ServerSocket server = null;
		private Socket socket = null;

		public SocketAcceptor(InetSocketAddress address) {
			try
			{
				server = new ServerSocket(address.getPort());// ,
				// 0,
				// address.getAddress());
				server.setSoTimeout(50000);
			} catch (IOException e)
			{
				try
				{
					abort("A debugger is already running, please terminate it first",
							null);
				} catch (CoreException e1)
				{

				}
			}
		}

		synchronized private void listen()
		{
			try
			{
				this.socket = server.accept();
			} catch (IOException e)
			{
				try
				{
					server.close();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}

		synchronized public Socket getSocket()
		{
			return this.socket;
		}

		public void run()
		{
			listen();

		}

	}

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException
	{
		InetSocketAddress address = new InetSocketAddress("localhost",
				findFreePort());

		List<String> commandList = new ArrayList<String>();

		IVdmProject project = getProject(configuration);
		String charSet = project.getDefaultCharset();

		commandList.add("-h");
		commandList.add(address.getAddress().getHostAddress());
		commandList.add("-p");
		commandList.add(new Integer(address.getPort()).toString());
		commandList.add("-k");
		commandList.add("dbgp_1265361483486");
		commandList.add("-w");
		commandList.add("-q");
		commandList.add(project.getDialect().getArgstring());
		commandList.add("-r");
		commandList.add(project.getLanguageVersionName());
		commandList.add("-c");
		commandList.add(charSet);
		if (!isRemoteControllerEnabled(configuration))
		{
			commandList.add("-e64");
			commandList.add(getExpressionBase64(configuration, charSet));
			commandList.add("-default64");
			commandList.add(getDefaultBase64(configuration, charSet));
		}else{
			//temp fix for commanline args of dbgreader
			commandList.add("-e64");
			commandList.add(Base64.encode("A".getBytes()).toString());
		}
		
		if(isCoverageEnabled(configuration)){
			commandList.add("-coverage");
			commandList.add(getCoverageDir(project));
		}
		
		if(isRemoteControllerEnabled(configuration))
		{
			commandList.add("-remote");
			commandList.add(getRemoteControllerName(configuration));
		}
		

		commandList.addAll(getSpecFiles(project));
		if (useRemoteDebug(configuration))
		{
			System.out.println("Debugger Arguments:\n"
					+ getArgumentString(commandList));
		}
		commandList.add(0, "java");
		commandList.addAll(1, getClassPath());
		commandList.add(3, IDebugConstants.DEBUG_ENGINE_CLASS);

		VdmDebugTarget target = null;
		if (mode.equals(ILaunchManager.DEBUG_MODE))
		{
			System.out.println("Accepting debugger on: "
					+ address.getHostName() + " " + address.getPort());
			SocketAcceptor socketAcceptor = new SocketAcceptor(address);
			Thread acceptorThread = new Thread(socketAcceptor);
			acceptorThread.setName("Socket Acceptor");
			acceptorThread.start();

			try
			{
				Thread.sleep(200);
			} catch (InterruptedException e1)
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			IProcess p = launchExternalProcess(launch, commandList, project);

			Socket s = socketAcceptor.getSocket();

			if (s == null)
				abort("Failed to connect to debugger", null);

			target = new VdmDebugTarget(launch, p, s);
			launch.addDebugTarget(target);

		}
	}

	private String getRemoteControllerName(ILaunchConfiguration configuration) throws CoreException
	{
		return configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_CONTROL,
		"");
	}

	private String getCoverageDir(IVdmProject project)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		File coverageDir = new File(new File(getOutputFolder(project), "coverage"),dateFormat.format(new Date()));
		coverageDir.mkdirs();
		return coverageDir.toURI().toASCIIString();
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

	private boolean useRemoteDebug(ILaunchConfiguration configuration)
			throws CoreException
	{
		return configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_DEBUG,
				false);
	}

	private boolean isRemoteControllerEnabled(ILaunchConfiguration configuration)
			throws CoreException
	{
		return configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_REMOTE_CONTROL,
				"")
				.length() > 0;
	}
	
	private boolean isCoverageEnabled(ILaunchConfiguration configuration)
	throws CoreException
{
return configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CREATE_COVERAGE,
		false)
		;
}
	
	protected File getOutputFolder(IVdmProject project) {
		File outputDir = new File(project.getLocation().toFile().toURI().toASCIIString(),"generated");
		outputDir.mkdirs();
		return outputDir;
	}

	private IProcess launchExternalProcess(ILaunch launch,
			List<String> commandList, IVdmProject project)
	{

		String executeString = getArgumentString(commandList);

		Process process = null;
		System.out.println(executeString);
		try
		{
			if (!useRemoteDebug(launch.getLaunchConfiguration()))
			{
				process = Runtime.getRuntime().exec(executeString,
						null,
						project.getLocation().toFile());

				ConsoleWriter cw = new ConsoleWriter("Launch Console");
				new ProcessConsolePrinter("interpreter",
						cw,
						process.getInputStream()).start();
				new ProcessConsolePrinter("interpreter error",
						cw,
						process.getErrorStream()).start();
			} else
			{
				process = Runtime.getRuntime().exec("help");
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return DebugPlugin.newProcess(launch, process, "VDMJ debugger");
	}

	private String getDefaultBase64(ILaunchConfiguration configuration,
			String charset)
	{
		String defaultModule;
		try
		{
			defaultModule = configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_DEFAULT,
					"");

			return Base64.encode(defaultModule.getBytes(charset)).toString();
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private String getExpressionBase64(ILaunchConfiguration configuration,
			String charset)
	{
		String expression;
		try
		{

			expression = configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_EXPRESSION,
					"");
			return Base64.encode(expression.getBytes(charset)).toString();
		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	private List<String> getClassPath()
	{
		List<String> commandList = new Vector<String>();
		List<String> entries = new Vector<String>();
		ClasspathUtils.collectClasspath(new String[] { IDebugConstants.DEBUG_ENGINE_BUNDLE_ID },
				entries);
		if (entries.size() > 0)
		{
			commandList.add("-cp");
			String classPath = " ";
			for (String cp : entries)
			{
				if (cp.toLowerCase().endsWith(".jar"))
				{
					classPath += cp + getCpSeperator();
				}
			}
			classPath = classPath.substring(0, classPath.length() - 1);
			commandList.add(classPath.trim());

		}
		return commandList;
	}

	private String getCpSeperator()
	{
		if (System.getProperty("os.name").toLowerCase().contains("win"))
			return ";";
		else
			return ":";
	}

	/**
	 * Throws an exception with a new status containing the given message and optional exception.
	 * 
	 * @param message
	 *            error message
	 * @param e
	 *            underlying exception
	 * @throws CoreException
	 */
	private void abort(String message, Throwable e) throws CoreException
	{
		// TODO: the plug-in code should be the example plug-in, not Perl debug model id
		throw new CoreException((IStatus) new Status(IStatus.ERROR,
				IDebugConstants.ID_VDM_DEBUG_MODEL,
				0,
				message,
				e));
	}

	/**
	 * Returns a free port number on localhost, or -1 if unable to find a free port.
	 * 
	 * @return a free port number on localhost, or -1 if unable to find a free port
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

	private List<String> getSpecFiles(IVdmProject project)
	{
		List<String> files = new Vector<String>();
		try
		{

			for (IVdmSourceUnit unit : project.getSpecFiles())
			{
				files.add(unit.getSystemFile().toURI().toASCIIString());
			}

		} catch (CoreException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return files;
	}

	private IVdmProject getProject(ILaunchConfiguration configuration)
	{
		IProject project = null;
		try
		{
			project = ResourcesPlugin.getWorkspace()
					.getRoot()
					.getProject(configuration.getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_PROJECT,
							""));
		} catch (CoreException e)
		{
			if (Activator.DEBUG)
			{
				e.printStackTrace();
			}
		}
		if (project != null && VdmProject.isVdmProject(project))
		{
			return VdmProject.createProject(project);
		}
		return null;
	}

}
