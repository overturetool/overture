/*
 * #%~
 * Code Generator Plugin
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
package org.overture.ide.plugins.javagen;

import java.io.PrintWriter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.overture.codegen.printer.IPrinter;

public class CodeGenConsole implements IPrinter
{
	private final PrintWriter out;
	private final PrintWriter err;

	private static CodeGenConsole Instance;

	private MessageConsole codeGenConsole;

	public static CodeGenConsole GetInstance()
	{
		if (Instance == null)
		{
			Instance = new CodeGenConsole();
		}

		return Instance;
	}

	private CodeGenConsole()
	{
		codeGenConsole = findConsole(ICodeGenConstants.CONSOLE_NAME);
		if (codeGenConsole != null)
		{
			out = new PrintWriter(codeGenConsole.newMessageStream(), true);
			MessageConsoleStream errConsole = codeGenConsole.newMessageStream();

			errConsole.setColor(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
			err = new PrintWriter(errConsole, true);
		} else
		{
			out = new PrintWriter(System.out, true);
			err = new PrintWriter(System.err, true);
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
			// No console found, so create a new one
			MessageConsole myConsole = new MessageConsole(name, null);
			consoleManager.addConsoles(new IConsole[] { myConsole });
			return myConsole;
		}
		return null;
	}

	@Override
	public void print(String msg)
	{
		out.print(msg);
	}

	@Override
	public void println(String msg)
	{
		out.println(msg);
	}

	@Override
	public void errorln(String msg)
	{
		err.println(msg);
	}

	@Override
	public void error(String msg)
	{
		err.print(msg);
	}
	
	public void activate()
	{
		if(codeGenConsole != null)
		{
			codeGenConsole.activate();
		}
	}

	public void clearConsole()
	{
		if (codeGenConsole != null)
		{
			codeGenConsole.clearConsole();
		}
	}

	@Override
	public void setSilent(boolean arg0)
	{
		// Do nothing..
	}
}
