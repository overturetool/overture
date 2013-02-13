package org.overturetool.tools.astg;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.overturetool.astgen.ASTgen;
import org.overturetool.astgen.Kind;

/**
 * Generate Java source form an AST file
 * 
 * @goal java
 * @phase process-resources
 * @requiresDependencyResolution compile
 */
public class GenerateJava extends AstGenBaseMojo
{

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException
	{
		getLog().info("Generating Java Interfaces");
		generate(Kind.INTF);
		getLog().info("Generating Java Implementation");
		generate(Kind.IMPL);
	}

	

	private void generate(Kind kind) throws MojoExecutionException,
			MojoFailureException
	{
		boolean ok = true;
		try{
			ok =(ASTgen.generate("java", kind, getProjectJavaSrcDirectory(),getGrammas() , names)==0);
			
		}catch(Exception e)
		{
			throw new MojoExecutionException("Error generating "+kind, e);
		}
		if(!ok)
			throw new MojoFailureException("Faild generating "+ kind);
	}
	
	

}
