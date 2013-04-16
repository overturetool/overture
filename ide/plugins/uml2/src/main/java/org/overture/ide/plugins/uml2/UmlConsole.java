package org.overture.ide.plugins.uml2;

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

public class UmlConsole
{
	public final PrintWriter out;
	public final PrintWriter err;
	boolean hasConsole = false;

	public UmlConsole()
	{
		MessageConsole myConsole = findConsole(IUml2Constants.concoleName);
		if (myConsole != null)
		{
			out = new PrintWriter(myConsole.newMessageStream(), true);
			MessageConsoleStream errConsole = myConsole.newMessageStream();

			errConsole.setColor(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			err = new PrintWriter(errConsole, true);
			hasConsole = true;
		} else
		{
			out = new PrintWriter(System.out, true);
			err = new PrintWriter(System.err, true);
		}
		// out.println(message);

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
					activePage.showView(IConsoleConstants.ID_CONSOLE_VIEW, null, IWorkbenchPage.VIEW_VISIBLE);
				}
			}
		}
	}

	private MessageConsole findConsole(String name)
	{
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		if (plugin != null)
		{
			IConsoleManager conMan = plugin.getConsoleManager();
			IConsole[] existing = conMan.getConsoles();
			for (int i = 0; i < existing.length; i++)
			{
				if (name.equals(existing[i].getName()))
				{
					return (MessageConsole) existing[i];
				}
			}
			// no console found, so create a new one
			MessageConsole myConsole = new MessageConsole(name, null);
			conMan.addConsoles(new IConsole[] { myConsole });
			return myConsole;
		}
		return null;
	}

}
