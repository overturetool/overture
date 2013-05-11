package org.overture.ide.plugins.codegen.vdm2cpp;

import java.io.File;
import java.util.ArrayList;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.ui.PartInitException;
import org.overture.ide.plugins.codegen.CodeGenConsole;


public class Vdm2Cpp
{
	private CodeGenConsole console;
		
	public Vdm2Cpp()
	{
		console = CodeGenConsole.GetInstance();
		initVelocity();		
	}
	
	private void initVelocity()
	{
		String s = Vdm2CppUtil.getPropertiesPath("resources" + File.separatorChar + "velocity.properties");
		Velocity.init(s);
	}
	
	public void generateCode()
	{
		try
		{
			console.show();
			console.out.println("Code generator invoked!");
			
			VelocityContext context = new VelocityContext();
	        context.put("list", getNames());
	        
	        Template template = Vdm2CppUtil.getTemplate("resources" + File.separatorChar + "example.vm");
	        
	        if(template == null)
	        {
	        	console.out.println("Aborting code generation..");
	        	return;
	        }
	        
	        template.merge(context, console.out);
	        
	        console.out.flush();
			
		} catch (PartInitException e)
		{
			//Problems showing console
			e.printStackTrace();
			return;
		}
	}
	
	private ArrayList<String> getNames()
    {
        ArrayList<String> list = new ArrayList<String>();

        list.add("Element 1");
        list.add("Element 2");
        list.add("Element 3");
        list.add("Element 4");

        return list;
    }
	
}
