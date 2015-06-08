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
package org.overture.ide.plugins.codegen.commands;

import java.io.InputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.ide.plugins.codegen.Activator;
import org.overture.ide.plugins.codegen.util.PluginVdm2JavaUtil;

public class AboutCommand extends AbstractHandler
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		InputStream fileStream = AboutCommand.class.getResourceAsStream("/textfiles/AboutMessage.txt");

		try
		{

			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			String title = "VDM to Java Code Generator";
			String about = GeneralUtils.readFromInputStream(fileStream).toString();
			about = String.format(about, PluginVdm2JavaUtil.CODEGEN_RUNTIME_BIN_FILE,
					PluginVdm2JavaUtil.CODEGEN_RUNTIME_SOURCES_FILE,
					PluginVdm2JavaUtil.CODEGEN_RUNTIME_LIB_FOLDER);

			MessageDialog.openInformation(shell, title, about);

			return Status.OK_STATUS;

		} catch (Exception ex)
		{

			Activator.log("Could not load the VDM to Java Code Generator About Message", ex);

		}

		return Status.ERROR;
	}
}
