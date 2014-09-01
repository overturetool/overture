/*
 * #%~
 * org.overture.ide.ui
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

public class ConsoleWriter extends PrintWriter {
	private String consoleName = "Overture";

	public ConsoleWriter() {
		super(System.out);
	}

	public ConsoleWriter(String consoleName) {
		super(System.out);
		this.consoleName = consoleName;
	}

	@Override
	public void println(String x) {
		consolePrint(x);
	}

	public void show() {
		MessageConsole myConsole = findConsole(consoleName);
		myConsole.activate();

		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(
				myConsole);

	}

	public void consolePrint(final String message) {
		getDisplay().asyncExec(new Runnable() {

			public void run() {
				try {
					MessageConsole myConsole = findConsole(consoleName);
					MessageConsoleStream out = myConsole.newMessageStream();
					out.println(message);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void clear() {
		getDisplay().asyncExec(new Runnable() {

			public void run() {
				try {
					MessageConsole myConsole = findConsole(consoleName);
					myConsole.clearConsole();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void consolePrint(final String message, final int color) {
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				try {
					MessageConsole myConsole = findConsole(consoleName);
					MessageConsoleStream out = myConsole.newMessageStream();
					out.setColor(Display.getCurrent().getSystemColor(color));
					out.println(message);
					out.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public synchronized MessageConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++) {
			if (name.equals(existing[i].getName())) {
				return (MessageConsole) existing[i];
			}
		}
		// no console found, so create a new one
		MessageConsole myConsole = new MessageConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}

	public void consolePrint(final Exception exception) {
		consolePrint(getExceptionStackTraceAsString(exception));
	}

	public static String getExceptionStackTraceAsString(Exception exception) {
		StringWriter sw = new StringWriter();
		exception.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

	public static Display getDisplay() {
		return VdmUIPlugin.getDefault().getWorkbench().getDisplay();
	}
}
