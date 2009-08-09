package org.overture.tools.vdmt;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.overture.tools.vdmt.VDMToolsProxy.VdmProject;


/**
 * Says "Hi" to the user.
 * 
 * @goal speclog
 * @phase process-resources
 * @requiresDependencyResolution compile
 */
public class CreateProjectFileLog extends VdmBaseMojo {

	public void execute() throws MojoExecutionException, MojoFailureException {
		super.execute();

		VdmProject p = new VdmProject(getLog(), vdmToolsCmd, project.getFile().getParentFile(), dependedVppLocations);
		p.createSpecfileParameter(project.getArtifactId());

	}

}
