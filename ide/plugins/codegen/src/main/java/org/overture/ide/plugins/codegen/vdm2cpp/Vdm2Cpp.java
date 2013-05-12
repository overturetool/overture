package org.overture.ide.plugins.codegen.vdm2cpp;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.eclipse.ui.PartInitException;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.ast.node.INode;
import org.overture.ide.core.IVdmModel;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.plugins.codegen.Activator;
import org.overture.ide.plugins.codegen.CodeGenConsole;
import org.overture.ide.plugins.codegen.visitor.CodeGenContext;
import org.overture.ide.plugins.codegen.visitor.CodeGenVisitor;

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
		String s = Vdm2CppUtil.getPropertiesPath("resources"
				+ File.separatorChar + "velocity.properties");
		Velocity.init(s);
	}

	public CodeGenContext generateCode(IVdmModel model) throws AnalysisException
	{
		try
		{
			console.show();
		} catch (PartInitException e)
		{
			Activator.log(e);
		}
				
		List<IVdmSourceUnit> sources = model.getSourceUnits();
				
		if(sources.size() == 0)
			return null;
		
		CodeGenVisitor codeGenVisitor = new CodeGenVisitor();
		CodeGenContext codeGenContext = new CodeGenContext();
		
		for (IVdmSourceUnit source : sources)
		{
			List<INode> parseList = source.getParseList();
			
			if(parseList.isEmpty())
				continue;
			
			for (INode node : parseList)
			{
				node.apply(codeGenVisitor, codeGenContext);
			}
		}
		
		return codeGenContext;
	}
	
	public void save(IVdmProject vdmProject, CodeGenContext codeGenContext)
	{

//		TODO: This method currently just prints the results. The code below constructs a file representing the output folder
//		java.net.URI absolutePath = vdmProject.getModelBuildPath().getOutput().getLocationURI();// iFile.getLocationURI();
//		URL url;
//		try
//		{
//			url = FileLocator.toFileURL(absolutePath.toURL());
//			File file = new File(url.toURI());			
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//		} catch (URISyntaxException e)
//		{
//			e.printStackTrace();
//		}		
		Set<AClassClassDefinition> set = codeGenContext.getKeys();
		
		Template template = Vdm2CppUtil.getTemplate("resources"
				+ File.separatorChar + "class.vm");
		
		for (AClassClassDefinition classDef : set)
		{
			if (template == null)
			{
				console.out.println("Aborting code generation..");
				return;
			}
			
			template.merge(codeGenContext.getContext(classDef), console.out);
			console.out.flush();
			console.out.println();
		}
	}
}
