package org.overture.ide.plugins.codegen.commands;

import java.io.File;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.Bundle;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.ide.plugins.codegen.Activator;
import org.overture.ide.plugins.codegen.ICodeGenConstants;

public class AboutCommand extends AbstractHandler
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Bundle bundle = Platform.getBundle(ICodeGenConstants.PLUGIN_ID);
		URL fileURL = bundle.getEntry("src\\main\\resources\\AboutMessage.txt");
		
		File file = null;
		try {
			
		    file = new File(FileLocator.resolve(fileURL).toURI());
		    
		    String message = GeneralUtils.readFromFile(file);
		    
		    MessageDialog.openInformation(HandlerUtil.getActiveWorkbenchWindow(event).getShell(), "VDM++ to Java Code Generator (Experimental)", message);
		    
		    return Status.OK_STATUS;
		    
		} catch (Exception ex) {
			
			Activator.log("Could VDM++ to Java Code Generator About Message", ex);
			
		}
		
		return Status.ERROR;
	}
}
