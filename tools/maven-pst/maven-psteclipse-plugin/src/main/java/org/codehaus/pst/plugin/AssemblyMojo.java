/*
 * Copyright (C) 2006 Princeton Softech, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.pst.plugin;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.profiles.DefaultProfileManager;
import org.apache.maven.profiles.ProfileManager;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * <ul>
 * <li>Title: AssemblyMojo</li>
 * <li>Description: The class <code>AssemblyMojo</code> is a Mojo that assembles a distribution 
 * of the all the eclipse plugins that are created in a hierarchy.
 * If run on the command line, it must follow a "mvn package".</li>
 * <li>Created:</li>
 * </ul>
 * @author $Author: prisgupt01 $
 * @version $Revision: 1.3 $
 * @goal assemble
 * @phase package
 * @requiresProject true
 */
public class AssemblyMojo extends AbstractEclipseMojo {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/AssemblyMojo.java,v 1.3 2007/02/14 05:41:41 prisgupt01 Exp $";

    /**
     * The target directory
     */
    public static final String TARGET_DIR = "target";

    /**
     * The work directory for assemby
     */
    public static final String WORK_DIR = "eclipse";

    /**
     * The plugins directory
     */
    public static final String PLUGINS_DIR = "plugins";
    
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
     * @parameter expression="${reactorProjects}"
     */
    private List reactors;

    /**
     * The Jar archiver.
     * @parameter expression="${component.org.codehaus.plexus.archiver.Archiver#zip}"
     * @required
     */
    private ZipArchiver zipArchiver;

    /**
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    private MavenSession session;

    /**
     * @parameter expression="${outputDir}"
     * @optional
     */
    private File outputDirectory;

    /**
     * Constructs a new <code>AssemblyMojo</code> instance.
     */
    public AssemblyMojo() {
        super();
    }

    /**
     * Performs an assembly of all Eclipse plugins that are modules of the
     * project
     */
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        // TODO get Eclipse sdk and unzip
        if (mavenProject.getPackaging().equals("pom") && reactors != null) {
            Iterator modules = reactors.iterator();
            File pluginsDirectory = new File(baseDirectory, TARGET_DIR + File.separator + WORK_DIR + File.separator + PLUGINS_DIR);
            if (!pluginsDirectory.exists()) {
                pluginsDirectory.mkdirs();
            }
            while (modules.hasNext()) {
                MavenProject project = (MavenProject) modules.next();
                String packaging = project.getPackaging();
                if (packaging.equals("source-plugin")) {
                    assembleSourcePlugin(project, pluginsDirectory);
                } else if (packaging.equals("binary-plugin")) {
                    assembleBinaryPlugin(project, pluginsDirectory);
                }
            }
            File eclipseDir = pluginsDirectory.getParentFile();

            DateFormat format = new SimpleDateFormat(DATE_FORMAT);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));

            String distFileName = PLUGINS_DIR + format.format(new Date()) + ".zip";
            File distFile = new File(eclipseDir.getParent(), distFileName);
            try {
                zipArchiver.addDirectory(eclipseDir);
                zipArchiver.setDestFile(distFile);
                zipArchiver.createArchive();
            } catch (Exception e) {
                throw new MojoExecutionException("Could not create distribution zip", e);
            }
            if (outputDirectory != null && outputDirectory.exists()) {
                try {
                    copyFile(distFile, new File(outputDirectory, distFileName));
                } catch (IOException e) {
                    throw new MojoExecutionException("Could not copy the assembled distribution to the output directory " + outputDirectory, e);
                }
            }
        }

    }

    /**
     * Create a binary plugin in the work directory
     * 
     * @param project
     * @param pluginsDirectory
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    private void assembleBinaryPlugin(MavenProject project, File pluginsDirectory) throws MojoExecutionException, MojoFailureException {
        try {
            ProfileManager profileManager = new DefaultProfileManager(session.getContainer());
            MavenProjectBuilder builder = (MavenProjectBuilder) session.lookup(MavenProjectBuilder.class.getName());
            project = builder.buildWithDependencies(project.getFile(), session.getLocalRepository(), profileManager);
        } catch (ComponentLookupException e) {
            throw new MojoExecutionException("Unable to lookup project builder", e);
        } catch (ProjectBuildingException e) {
            throw new MojoExecutionException("Unable to build dependent project", e);
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to resolve", e);
        }
        ArrayList buddies = new ArrayList();
        Plugin plugin = (Plugin) project.getBuild().getPluginsAsMap().get("org.overturetool.tools.maven-pst:maven-psteclipse-plugin");
        if (plugin != null) {
            Xpp3Dom configuration = (Xpp3Dom) plugin.getConfiguration();
            if (configuration != null) {
                Xpp3Dom buddiesDom = configuration.getChild("buddies");
                if (buddiesDom != null) {
                    Xpp3Dom[] buddyDoms = buddiesDom.getChildren("buddy");
                    if (buddyDoms != null) {
                        for (int i = 0; i < buddyDoms.length; i++) {
                            buddies.add(buddyDoms[i].getValue());
                        }
                    }
                }
            }
        }

        String artifactName = project.getArtifactId() + "-" + project.getVersion();
        getLog().info("Deploying 'pom' artifact '" + artifactName + "'...");
        File pluginDirectory = new File(pluginsDirectory, artifactName);
        if (!pluginDirectory.exists()) {
            pluginDirectory.mkdir();
        }
        ManifestGenerator generator = new ManifestGenerator(getLog(), project.getBasedir(), project, buddies, pluginDirectory,doNotExportPackagePrefixes);
        generator.execute();

    }

    /**
     * Move the source plugin to the work directory
     * @param project
     * @param pluginsDirectory
     * @throws MojoExecutionException
     */
    private void assembleSourcePlugin(MavenProject project, File pluginsDirectory) throws MojoExecutionException {
        String fileName = project.getBuild().getFinalName() + ".jar";
        File pluginJar = new File(project.getBasedir(), "target" + File.separator + fileName);
        if (pluginJar.exists()) {
            try {
                copyFile(pluginJar, new File(pluginsDirectory, fileName));
            } catch (IOException e) {
                throw new MojoExecutionException("Could not copy over plugin jar", e);
            }
        }
    }

}
