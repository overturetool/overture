package org.overture.codegen.vdm2cpp;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import org.apache.velocity.Template;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.node.INode;
import org.overture.codegen.visitor.CodeGenContextMap;
import org.overture.codegen.visitor.CodeGenVisitor;


public class Vdm2Cpp
{
	// private CodeGenConsole console;

	public Vdm2Cpp()
	{
		// console = CodeGenConsole.GetInstance();
		initVelocity();
	}

	private void initVelocity()
	{
		// String s = Vdm2CppUtil.getPropertiesPath("resources"
		// + File.separatorChar + "velocity.properties");
		// Velocity.init(s);
	}

	public CodeGenContextMap generateCode(List<List<INode>> parseLists)
			throws AnalysisException
	{
		// try
		// {
		// console.show();
		// } catch (PartInitException e)
		// {
		// Activator.log(e);
		// }
		//
		// List<IVdmSourceUnit> sources = model.getSourceUnits();
		//
		// if(sources.size() == 0)
		// return null;

		CodeGenVisitor codeGenVisitor = new CodeGenVisitor();
		CodeGenContextMap codeGenContextMap = new CodeGenContextMap();

		for (List<INode> list : parseLists)
		{
			// List<INode> parseList = source.getParseList();

			if (list.isEmpty())
				continue;

			for (INode node : list)
			{
				node.apply(codeGenVisitor, codeGenContextMap);
			}
		}

		// Now register the analysis at the apache velocity engine
		codeGenContextMap.commit();

		return codeGenContextMap;
	}

	public void save(CodeGenContextMap codeGenContext)
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
		

        
		

// ********** COMMENT BELOW BACK IN:
		
		Set<String> set = codeGenContext.getContextKeys();
		
		Template template = Vdm2CppUtil.getTemplate("class.vm");
		
		if (template == null)
		{
			return;
		}
		PrintWriter out = new PrintWriter(System.out);
		for (String classDef : set)
		{			
			template.merge(codeGenContext.getContext(classDef).getVelocityContext(), out);
			out.flush();
			out.println();
		}
	}
}
