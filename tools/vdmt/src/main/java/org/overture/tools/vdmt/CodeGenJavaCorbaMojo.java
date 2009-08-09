package org.overture.tools.vdmt;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.overture.tools.vdmt.VDMToolsProxy.VdmToolsProxyProject;


/**
 * Says "Hi" to the user.
 * 
 * @goal codec
 * @phase process-resources
 * @requiresDependencyResolution compile
 */
public class CodeGenJavaCorbaMojo extends VdmBaseMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		VdmToolsProxyProject p = new VdmToolsProxyProject(getLog(), vdmToolsCmd, project.getFile().getParentFile(), dependedVppLocations);
		p.typeCheck(excludePackages,excludeClasses,importPackages);

	}

}
