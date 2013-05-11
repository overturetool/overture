package org.overture.ide.plugins.codegen;

import java.io.PrintWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class CodeGenConsole
{
	public final PrintWriter out;
	public final PrintWriter err;
	boolean hasConsole = false;

	private static CodeGenConsole Instance;
	
	public static CodeGenConsole GetInstance()
	{
		if(Instance == null)
			Instance = new CodeGenConsole();
		
		return Instance;
	}
	
	private CodeGenConsole()
	{
		MessageConsole codeGenConsole = findConsole(ICodeGenConstants.CONSOLE_NAME);
		if (codeGenConsole != null)
		{
			out = new PrintWriter(codeGenConsole.newMessageStream(), true);
			MessageConsoleStream errConsole = codeGenConsole.newMessageStream();

			errConsole.setColor(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			err = new PrintWriter(errConsole, true);
			hasConsole = true;
		} else
		{
			out = new PrintWriter(System.out, true);
			err = new PrintWriter(System.err, true);
		}
	}

	public void show() throws PartInitException
	{
		if (hasConsole)
		{
			IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (activeWorkbenchWindow != null)
			{
				IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
				if (activePage != null)
				{
					activePage.showView(IConsoleConstants.ID_CONSOLE_VIEW, null, IWorkbenchPage.VIEW_ACTIVATE);
				}
			}
		}
	}

	private MessageConsole findConsole(String name)
	{
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		if (plugin != null)
		{
			IConsoleManager consoleManager = plugin.getConsoleManager();
			IConsole[] existingConsoles = consoleManager.getConsoles();
			for (int i = 0; i < existingConsoles.length; i++)
			{
				if (name.equals(existingConsoles[i].getName()))
				{
					return (MessageConsole) existingConsoles[i];
				}
			}
			//No console found, so create a new one
			MessageConsole myConsole = new MessageConsole(name, null);
			consoleManager.addConsoles(new IConsole[] { myConsole });
			return myConsole;
		}
		return null;
	}

}
