package org.overturetool.tools.astgen;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Says "Hi" to the user.
 * 
 * @goal gen
 * @phase process-resources
 * @requiresDependencyResolution compile
 */
public class GenerateSourceMojo extends AstGenBaseMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		GenerateSource p = new GenerateSource(getLog(), project.getFile().getParentFile());
		p.generate( prefix,packageName,top);

	}

}
