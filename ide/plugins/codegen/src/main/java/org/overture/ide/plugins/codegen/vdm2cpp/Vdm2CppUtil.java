package org.overture.ide.plugins.codegen.vdm2cpp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import org.apache.velocity.Template;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.overture.ast.lex.Dialect;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.plugins.codegen.CodeGenConsole;
import org.overture.ide.plugins.codegen.ICodeGenConstants;

public class Vdm2CppUtil
{	
	private static CodeGenConsole console;
	
	static
	{
		console = CodeGenConsole.GetInstance();
	}
	
	private Vdm2CppUtil()
	{
		
	}
	
	public static String getPropertiesPath(String relativePath)
	{
		return getAbsolutePath(relativePath);
	}
	
	public static boolean isSupportedVdmDialect(IVdmProject vdmProject)
	{
		return vdmProject.getDialect() == Dialect.VDM_PP || vdmProject.getDialect() == Dialect.VDM_RT;
	}
	
	public static IVdmProject getVdmProject(ExecutionEvent event)
	{
		ISelection selection = HandlerUtil.getCurrentSelection(event);

		if (!(selection instanceof IStructuredSelection))
		{
			return null;
		}

		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		Object firstElement = structuredSelection.getFirstElement();

		if (!(firstElement instanceof IProject))
		{
			return null;
		}

		IProject project = ((IProject) firstElement);
		IVdmProject vdmProject = (IVdmProject) project.getAdapter(IVdmProject.class);

		return vdmProject;
	}
	
	public static Template getTemplate(String relativePath){
		
		Template template = new Template();
		
		try
        {
        	StringBuffer buffer = readFromFile(relativePath);
            RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();            
            StringReader reader = new StringReader(buffer.toString());
            SimpleNode node = runtimeServices.parse(reader, "Template name");
            
            template.setRuntimeServices(runtimeServices);
            template.setData(node);
            template.initDocument();
        	
        	return template;
            
        }
		catch(IOException ioEx)
		{	
			console.out.println("Could not find template file: " + getAbsolutePath(relativePath));
			return null;
		}
		catch (ParseException parseEx)
		{
			console.out.println("Template file was found but could not be parsed.");
			return null;
		}
		catch(TemplateInitException initEx)
		{
			console.out.println("Template file was found but could not be initialized.");
			return null;
		}		
	}
		
	private static String getAbsolutePath(String relativePath)
	{
		URL iconUrl = FileLocator.find(Platform.getBundle(ICodeGenConstants.PLUGIN_ID), new Path(relativePath), null);
		URL fileUrl;
		File file;

		try
		{
			fileUrl = FileLocator.toFileURL(iconUrl);
			file = new File(fileUrl.toURI());
			
			return file.getAbsolutePath();
			
		} catch (Exception e)
		{
			return null;
		}
	}
	
	private static StringBuffer readFromFile(String pFilename) throws IOException {  
        BufferedReader in = new BufferedReader(new FileReader(Vdm2CppUtil.getAbsolutePath(pFilename)));  
        StringBuffer data = new StringBuffer();  
        int c = 0;  
        while ((c = in.read()) != -1) {  
            data.append((char)c);  
        }  
        in.close();  
        return data;  
    }  
    
}
