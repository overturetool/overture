/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.ui;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.console.ConsoleColorProvider;
import org.eclipse.debug.ui.console.IConsoleColorProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.IOConsole;
import org.overture.ide.debug.core.IDebugConstants;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.model.IVdmDebugTarget;

import com.ibm.icu.text.MessageFormat;

public class DebugConsoleManager implements ILaunchesListener2
{

	private static DebugConsoleManager instance;

	public static synchronized DebugConsoleManager getInstance()
	{
		if (instance == null)
		{
			instance = new DebugConsoleManager();
		}
		return instance;
	}

	private final Map<ILaunch, VdmDebugConsole> launchToConsoleMap = Collections.synchronizedMap(new HashMap<ILaunch, VdmDebugConsole>());

	protected boolean acceptLaunch(ILaunch launch)
	{
		if (launch == null)
		{
			return false;
		}
		// TODO we allow both debug and run here...
		if (!(ILaunchManager.DEBUG_MODE.equals(launch.getLaunchMode()) || ILaunchManager.RUN_MODE.equals(launch.getLaunchMode())))
		{
			return false;
		}
		// Do not interfere with tools launched using External Tools
		try {
			if (launch.getLaunchConfiguration() != null
					&& launch.getLaunchConfiguration().getCategory() != null
					   && launch.getLaunchConfiguration().getCategory().equals("org.eclipse.ui.externaltools"))			
			{
				return false;
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return launch.getProcesses().length != 0
				&& true // DLTKDebugLaunchConstants.isDebugConsole(launch)
				|| launch.getDebugTarget() instanceof IVdmDebugTarget
				&& ((IVdmDebugTarget) launch.getDebugTarget()).isRemote();
	}

	/**
	 * @since 2.0
	 */
	protected VdmDebugConsole createConsole(ILaunch launch)
	{
		final String encoding = selectEncoding(launch);
		final IProcess[] processes = launch.getProcesses();
		final IProcess process = processes.length != 0 ? processes[0] : null;
		final IConsoleColorProvider colorProvider = getColorProvider(process != null ? process.getAttribute(IProcess.ATTR_PROCESS_TYPE)
				: null);
		final VdmDebugConsole console = new VdmDebugConsole(launch, computeName(launch), null, encoding, colorProvider);

		DebugPlugin.getDefault().addDebugEventListener(console);

		if (process != null)
		{
			console.setAttribute(IDebugUIConstants.ATTR_CONSOLE_PROCESS, process);
			// if (process instanceof IProcess) {
			// console.connect((IProcess) process);
			// }
		}
		final IConsoleManager manager = getConsoleManager();
		manager.addConsoles(new IConsole[] { console });
		console.activate();

		return console;
	}

	public void displayConsoleView(boolean pin)
	{
		IWorkbenchWindow[] activeWorkbenchWindow = PlatformUI.getWorkbench().getWorkbenchWindows();
		if (activeWorkbenchWindow.length > 0)
		{
			IWorkbenchPage activePage = activeWorkbenchWindow[0].getActivePage();
			if (activePage != null)
			{
				IViewPart part = activePage.findView(IConsoleConstants.ID_CONSOLE_VIEW);
				if (part != null && part instanceof IConsoleView)
				{
					IConsoleView consoleView = (IConsoleView) part;
					consoleView.setPinned(pin);
				}
			}
		}
	}

	private String selectEncoding(ILaunch launch)
	{
		String encoding = launch.getAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING);
		if (encoding != null)
		{
			return encoding;
		}
		final ILaunchConfiguration configuration = launch.getLaunchConfiguration();
		if (configuration != null)
		{
			try
			{
				return DebugPlugin.getDefault().getLaunchManager().getEncoding(configuration);
			} catch (CoreException e)
			{
				VdmDebugPlugin.log(e);
			}
		}
		return ResourcesPlugin.getEncoding();
	}

	protected void destroyConsole(IOConsole console)
	{
		getConsoleManager().removeConsoles(new IConsole[] { console });
	}

	private IConsoleManager getConsoleManager()
	{
		return ConsolePlugin.getDefault().getConsoleManager();
	}

	protected DebugConsoleManager()
	{
	}

	/**
	 * @since 2.0
	 */
	protected String computeName(ILaunch launch)
	{
		final IProcess[] processes = launch.getProcesses();
		String consoleName;
		if (processes.length != 0)
		{
			final IProcess process = processes[0];
			ILaunchConfiguration config = process.getLaunch().getLaunchConfiguration();
			consoleName = process.getAttribute(IProcess.ATTR_PROCESS_LABEL);
			if (consoleName == null)
			{
				if (config == null || DebugUITools.isPrivate(config))
				{
					// No config or PRIVATE
					consoleName = process.getLabel();
				} else
				{
					consoleName = computeName(config, process);
				}
			}
		} else
		{
			final ILaunchConfiguration config = launch.getLaunchConfiguration();
			if (config != null)
			{
				consoleName = computeName(config, null);
			} else
			{
				consoleName = "";// Util.EMPTY_STRING;
			}
		}
		consoleName = Messages.DebugConsoleManager_debugConsole
				+ " " + consoleName; //$NON-NLS-1$
		if (launch.isTerminated())
		{
			consoleName = NLS.bind(Messages.DebugConsoleManager_terminated, consoleName);
		}
		return consoleName;
	}

	/**
	 * @since 2.0
	 */
	protected String computeName(ILaunchConfiguration config, IProcess process)
	{
		String type = null;
		try
		{
			type = config.getType().getName();
		} catch (CoreException e)
		{
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append(config.getName());
		if (type != null)
		{
			buffer.append(" ["); //$NON-NLS-1$
			buffer.append(type);
			buffer.append("]"); //$NON-NLS-1$
		}
		if (process != null)
		{
			buffer.append(" "); //$NON-NLS-1$
			buffer.append(process.getLabel());
		}
		return buffer.toString();
	}

	/**
	 * @since 2.0
	 */
	public void launchesAdded(ILaunch[] launches)
	{
		launchesChanged(launches);
	}

	/**
	 * @since 2.0
	 */
	public void launchesChanged(ILaunch[] launches)
	{
		for (ILaunch launch : launches)
		{
			if (acceptLaunch(launch))
			{
				VdmDebugConsole console = launchToConsoleMap.get(launch);
				if (console == null)
				{
					console = createConsole(launch);
					launchToConsoleMap.put(launch, console);
				}
				final IProcess[] processes = launch.getProcesses();
				if (processes.length != 0 && processes[0] instanceof IProcess)
				{
					console.connect(processes[0]);
				}
				if (launch.getDebugTarget() instanceof IVdmDebugTarget)
				{
					IVdmDebugTarget target = (IVdmDebugTarget) launch.getDebugTarget();
					if (target != null && target.getStreamProxy() == null)
					{
						boolean interactiveConsoleMode = false;
						try
						{
							interactiveConsoleMode = launch.getLaunchConfiguration().getAttribute(IDebugConstants.VDM_LAUNCH_CONFIG_CONSOLE_ENTRY, false);
						} catch (CoreException e)
						{
							e.printStackTrace();
						}
						target.setStreamProxy(new VdmStreamProxy(console, interactiveConsoleMode));
					}
				}
			}
		}
	}

	/**
	 * @since 2.0
	 */
	public void launchesRemoved(ILaunch[] launches)
	{
		for (ILaunch launch : launches)
		{
			final VdmDebugConsole console = launchToConsoleMap.get(launch);
			if (console != null)
			{
				destroyConsole(console);
				launchToConsoleMap.remove(launch);
			}
		}
	}

	/**
	 * @since 2.0
	 */
	public void launchesTerminated(ILaunch[] launches)
	{
		for (ILaunch launch : launches)
		{

			final VdmDebugConsole console = launchToConsoleMap.get(launch);
			if (console != null)
			{
				final String newName = computeName(launch);
				if (!newName.equals(console.getName()))
				{
					final Runnable r = new Runnable()
					{
						public void run()
						{
							console.setName(newName);
						}
					};
					VdmDebugPlugin.getStandardDisplay().asyncExec(r);
				}
			}
		}
	}

	/**
	 * Console document content provider extensions, keyed by extension id
	 */
	private Map<String, IConfigurationElement> fColorProviders = null;

	/**
	 * The default color provider. Used if no color provider is contributed for the given process type.
	 */
	private IConsoleColorProvider fDefaultColorProvider;

	/**
	 * Returns a new console document color provider extension for the given process type, or <code>null</code> if none.
	 * 
	 * @param type
	 *            corresponds to <code>IProcess.ATTR_PROCESS_TYPE</code>
	 * @return IConsoleColorProvider
	 */
	private IConsoleColorProvider getColorProvider(String type)
	{
		if (fColorProviders == null)
		{
			fColorProviders = new HashMap<String, IConfigurationElement>();
			IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(IDebugUIConstants.PLUGIN_ID, IDebugUIConstants.EXTENSION_POINT_CONSOLE_COLOR_PROVIDERS);
			IConfigurationElement[] elements = extensionPoint.getConfigurationElements();
			for (int i = 0; i < elements.length; i++)
			{
				IConfigurationElement extension = elements[i];
				fColorProviders.put(extension.getAttribute("processType"), extension); //$NON-NLS-1$
			}
		}
		IConfigurationElement extension = fColorProviders.get(type);
		if (extension != null)
		{
			try
			{
				Object colorProvider = extension.createExecutableExtension("class"); //$NON-NLS-1$
				if (colorProvider instanceof IConsoleColorProvider)
				{
					return (IConsoleColorProvider) colorProvider;
				}
				VdmDebugPlugin.logError(MessageFormat.format("Extension {0} must specify an instanceof IConsoleColorProvider for class attribute.", //$NON-NLS-1$
						new Object[] { extension.getDeclaringExtension().getUniqueIdentifier() }));
			} catch (CoreException e)
			{
				VdmDebugPlugin.log(e);
			}
		}
		// no color provider found of specified type, return default color
		// provider.
		if (fDefaultColorProvider == null)
		{
			fDefaultColorProvider = new ConsoleColorProvider();
		}
		return fDefaultColorProvider;
	}



}
