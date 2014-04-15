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

public class AboutCommand extends AbstractHandler
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		InputStream fileStream = AboutCommand.class.getResourceAsStream("/textfiles/AboutMessage.txt");
		
		try {
			
			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			String title = "VDM++ to Java Code Generator (Experimental)";
			String about = GeneralUtils.readFromInputStream(fileStream).toString();

			MessageDialog.openInformation(shell, title, about);
		    
		    return Status.OK_STATUS;
		    
		} catch (Exception ex) {
			
			Activator.log("Could VDM++ to Java Code Generator About Message", ex);
			
		}
		
		return Status.ERROR;
	}
}
