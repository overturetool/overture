/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overture.ide.debug.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.preferences.IDebugPreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchEncoding;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleInputStream;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.debug.core.model.internal.IVdmStreamProxy;

@SuppressWarnings("restriction")
public class VdmStreamProxy implements IVdmStreamProxy {
	private IOConsoleInputStream input;
	private IOConsoleOutputStream stdOut;
	private IOConsoleOutputStream stdErr;

	private boolean closed = false;
	private boolean interactiveMode = false;

	public VdmStreamProxy(IOConsole console, boolean interactiveMode) {
		input = console.getInputStream();
		stdOut = console.newOutputStream();
		stdErr = console.newOutputStream();
		this.interactiveMode = interactiveMode;

		// TODO is there a better way to access these internal preferences??
		final IPreferenceStore debugUIStore = DebugUIPlugin.getDefault()
				.getPreferenceStore();
		stdOut.setActivateOnWrite(debugUIStore
				.getBoolean(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT));
		stdErr.setActivateOnWrite(debugUIStore
				.getBoolean(IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR));

		getDisplay().asyncExec(new Runnable() {
			public void run() {
				final VdmDebugPlugin colors = VdmDebugPlugin.getDefault();
				stdOut.setColor(colors.getColor(PreferenceConverter.getColor(
						debugUIStore,
						IDebugPreferenceConstants.CONSOLE_SYS_OUT_COLOR)));
				stdErr.setColor(colors.getColor(PreferenceConverter.getColor(
						debugUIStore,
						IDebugPreferenceConstants.CONSOLE_SYS_ERR_COLOR)));
			}
		});
	}

	private Display getDisplay() {
		// If we are in the UI Thread use that
		if (Display.getCurrent() != null) {
			return Display.getCurrent();
		}

		if (PlatformUI.isWorkbenchRunning()) {
			return PlatformUI.getWorkbench().getDisplay();
		}

		return Display.getDefault();
	}

	public OutputStream getStderr() {
		return stdErr;
	}

	public OutputStream getStdout() {
		return stdOut;
	}

	public InputStream getStdin() {
		return input;
	}

	public synchronized void close() {
		if (!closed) {
			try {
				stdOut.close();
				stdErr.close();
				input.close();
				closed = true;
			} catch (IOException e) {
				if (VdmDebugPlugin.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean needsEncoding = false;
	private String encoding = null;

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
		needsEncoding = encoding != null
				&& !encoding.equals(WorkbenchEncoding
						.getWorkbenchDefaultEncoding());
	}

	public void writeStdout(String value) {
		if(interactiveMode)
		{
			if(value !=null)
			{
				try{
				value = value.substring(value.indexOf(' ')+1);
				}catch(Exception e)
				{
					//Don't care
				}
			}
		}
		write(stdOut, value);
		if(interactiveMode)
		{
			write(stdOut, "> ");
		}
	}

	public void writeStderr(String value) {
		write(stdErr, value);
	}

	private void write(IOConsoleOutputStream stream, String value) {
		try {
			if (needsEncoding) {
				stream.write(value.getBytes(encoding));
			} else {
				stream.write(value);
			}
			stream.flush();
		} catch (IOException e) {
			if (VdmDebugPlugin.DEBUG) {
				e.printStackTrace();
			}
			VdmDebugPlugin.log(e);
		}
	}

}
