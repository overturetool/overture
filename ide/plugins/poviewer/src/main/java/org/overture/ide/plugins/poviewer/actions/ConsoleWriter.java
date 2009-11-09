package org.overture.ide.plugins.poviewer.actions;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleWriter extends PrintWriter
{
	private Shell shell;
	public ConsoleWriter(Shell s)
	{
		super(System.out);
		this.shell = s;
		
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void println(String x)
	{
		ConsolePrint(shell, x);
	}
	
	public static void Show()
	{
		MessageConsole myConsole = findConsole("ProofsupportConsole");
		myConsole.activate();
	}
	
	public static void ConsolePrint(final Shell shell, final String message)
	{
		shell.getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				try
				{
					MessageConsole myConsole = findConsole("ProofsupportConsole");
					MessageConsoleStream out = myConsole.newMessageStream();
					out.println(message);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

	}

	public static MessageConsole findConsole(String name)
	{
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (MessageConsole) existing[i];
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	public static void ConsolePrint(final Shell shell, final Exception exception)
	{
		ConsolePrint(shell, getExceptionStackTraceAsString(exception));
	}

	public static String getExceptionStackTraceAsString(Exception exception)
	{
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
