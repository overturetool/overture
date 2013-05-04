package org.overture.ide.plugins.codegen.vdm2cpp;

import org.eclipse.ui.PartInitException;
import org.overture.ide.plugins.codegen.CodeGenConsole;

public class Vdm2Cpp
{
	private CodeGenConsole console;
	
	public Vdm2Cpp()
	{
		console = new CodeGenConsole();
	}
	
	public void generateCode()
	{
		try
		{
			console.show();
			console.out.println("CodeGenInvoked!");
		} catch (PartInitException e)
		{
			e.printStackTrace();
			return;
		}
	}

}
