package org.overture.ide.ui.internal.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.overture.ide.ui.VdmUIPlugin;

public class ConsoleWriter extends PrintWriter
{
	private String consoleName = "Overture";

	public ConsoleWriter() {
		super(System.out);
	}

	public ConsoleWriter(String consoleName) {
		super(System.out);
		this.consoleName = consoleName;
	}

	@Override
	public void println(String x)
	{
		ConsolePrint(x);
	}

	public void Show()
	{
		MessageConsole myConsole = findConsole(consoleName);
		myConsole.activate();
	}

	public void ConsolePrint(final String message)
	{
		getDisplay().asyncExec(new Runnable() {

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
	
	public void clear(){
		getDisplay().asyncExec(new Runnable() {

			public void run()
			{
				try
				{
					MessageConsole myConsole = findConsole(consoleName);
					myConsole.clearConsole();
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public void ConsolePrint(final String message, final int color)
	{
		getDisplay().asyncExec(new Runnable() {
			public void run()
			{
				try
				{
					MessageConsole myConsole = findConsole(consoleName);
					MessageConsoleStream out = myConsole.newMessageStream();
					out.setColor(Display.getCurrent().getSystemColor(color));
					out.println(message);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});

	}

	public MessageConsole findConsole(String name)
	{
		ConsolePlugin plugin = ConsolePlugin.getDefault();
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

	public void ConsolePrint(final Exception exception)
	{
		ConsolePrint(getExceptionStackTraceAsString(exception));
	}

	public static String getExceptionStackTraceAsString(Exception exception)
	{
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	public static Display getDisplay()
	{
		return VdmUIPlugin.getDefault().getWorkbench().getDisplay();
	}
}
