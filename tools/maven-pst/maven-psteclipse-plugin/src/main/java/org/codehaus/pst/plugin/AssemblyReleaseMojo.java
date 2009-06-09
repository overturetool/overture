/*
 * $Id: AssemblyReleaseMojo.java,v 1.1 2007/02/14 05:41:41 prisgupt01 Exp $
 * Copyright (c) 2006, Princeton Softech Inc. All rights reserved. 
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.codehaus.pst.plugin;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * <ul>
 * <li>Title: </li>
 * <li>Description: </li>
 * <li>Created: Feb 13, 2007 by: prisgupt01</li>
 * </ul>
 * @author $Author: prisgupt01 $
 * @version $Revision: 1.1 $
 * @goal release
 * @requiresProject true
 */
public class AssemblyReleaseMojo extends AbstractEclipseMojo {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/AssemblyReleaseMojo.java,v 1.1 2007/02/14 05:41:41 prisgupt01 Exp $";

    /**
     * The target directory
     */
    public static final String TARGET_DIR = "target";

    /**
     * Date format used to create a date string as part of archive name
     */
    public static final String DATE_FORMAT = "yyyyMMdd.HHmmss";

    /**
     * @parameter expression="${basedir}"
     * @required
     */
    private File baseDirectory;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    /**
     * @parameter expression="${outputDir}"
     * @required
     */
    private File outputDirectory;
    
    /**
     * @parameter expression="${releaseName}"
     * @optional
     */
    private String releaseName;

    /**
     * Constructs a new <code>AssemblyReleaseMojo</code> instance.
     */
    public AssemblyReleaseMojo() {
        super();
    }

    /* 
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractEclipseMojo#doExecute()
     */
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        File targetDirectory = new File(baseDirectory, TARGET_DIR);
        if (!targetDirectory.exists()) {
            throw new MojoExecutionException("Could not find target directory");
        }
        String assemblyName = mavenProject.getArtifactId() + "-" + mavenProject.getVersion() + ".zip";
        File assembly = new File(targetDirectory, assemblyName);
        if (!assembly.exists()) {
            throw new MojoExecutionException("Could not find assembly " + assemblyName);
        }
        if (releaseName == null || releaseName.length() == 0) {
            releaseName = mavenProject.getArtifactId();
        }
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String releaseFileName = releaseName + "_" + format.format(new Date()) + ".zip";
        if (outputDirectory != null && outputDirectory.exists()) {
            try {
                copyFile(assembly, new File(outputDirectory, releaseFileName));
            } catch (IOException e) {
                throw new MojoExecutionException("Could not copy the assembled distribution to the output directory " + outputDirectory, e);
            }
        } else {
            throw new MojoExecutionException("No output directory defined");
        }
    }

}
