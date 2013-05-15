package org.overture.codegen.vdm2cpp;

import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.codegen.logging.DefaultLogger;
import org.overture.codegen.logging.ILogger;
import org.overture.codegen.visitor.CodeGenContextMap;
import org.overture.codegen.visitor.CodeGenVisitor;

public class Vdm2Cpp
{
	private ILogger log;
	
	public Vdm2Cpp()
	{
		init(null);
	}
	
	public Vdm2Cpp(ILogger log)
	{
		init(log);
	}
	
	private void init(ILogger log)
	{
		initVelocity();
		
		if(log == null)
			this.log = new DefaultLogger();
		else
			this.log = log;	
	}


	private void initVelocity()
	{
		String propertyPath = Vdm2CppUtil.getVelocityPropertiesPath("velocity.properties");
		Velocity.init(propertyPath);
	}

	public CodeGenContextMap generateCode(
			List<SClassDefinition> mergedParseLists) throws AnalysisException
	{
		CodeGenVisitor codeGenVisitor = new CodeGenVisitor(log);
		CodeGenContextMap codeGenContextMap = new CodeGenContextMap();

		for (INode node : mergedParseLists)
		{
			node.apply(codeGenVisitor, codeGenContextMap);
		}

		// Now register the analysis at the apache velocity engine
		codeGenContextMap.commit();

		return codeGenContextMap;
	}

	public void save(CodeGenContextMap contextMap)
	{

		// java.net.URI absolutePath = vdmProject.getModelBuildPath().getOutput().getLocationURI();//
		// iFile.getLocationURI();
		// URL url;
		// try
		// {
		// url = FileLocator.toFileURL(absolutePath.toURL());
		// File file = new File(url.toURI());
		// } catch (IOException e)
		// {
		// e.printStackTrace();
		// } catch (URISyntaxException e)
		// {
		// e.printStackTrace();
		// }

		// ********** COMMENT BELOW BACK IN:

		Set<String> set = contextMap.getContextKeys();

		Template template = Vdm2CppUtil.getTemplate("class.vm");

		if (template == null)
		{
			return;
		}
		PrintWriter out = new PrintWriter(System.out);
		for (String classDef : set)
		{
			template.merge(contextMap.getContext(classDef).getVelocityContext(), out);
			out.flush();
			out.println();
		}
	}
	
}
