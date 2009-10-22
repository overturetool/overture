package org.overture.ide.utility;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleWriter extends PrintWriter
{
	private String consoleName="Overture.IDE.DefaultConsole";
	public static Shell shell;
	
	private Shell getShell()
	{
		if(shell==null)
			try{
			shell = new Shell();
			}catch(SWTException e)
			{
				
			}
		return shell;
	}
	
	public ConsoleWriter()
	{
		super(System.out);
		
	}
	
	public ConsoleWriter(String consoleName)
	{
		super(System.out);
	this.consoleName = consoleName;
	}
	
	@Override
	public void println(String x)
	{
		Shell s = getShell();
		if(s==null)
			System.out.println(x);
		else
			ConsolePrint(s, x);
	}
	
	public void Show()
	{
		MessageConsole myConsole = findConsole(consoleName);
		myConsole.activate();
	}
	
	public  void ConsolePrint(final Shell shell, final String message)
	{
		getShell().getDisplay().asyncExec(new Runnable()
		{

			public void run()
			{
				try
				{
					MessageConsole myConsole = findConsole(consoleName);
					MessageConsoleStream out = myConsole.newMessageStream();
					out.println(message);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

	}

	public  MessageConsole findConsole(String name)
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

	public  void ConsolePrint(final Shell shell, final Exception exception)
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
