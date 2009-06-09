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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.profiles.DefaultProfileManager;
import org.apache.maven.profiles.ProfileManager;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * <ul>
 * <li>Title: PDETestDeployer</li>
 * <li>Description: The class <code>PDETestDeployer</code> is a Mojo helper
 * that deploys a plug-in, packaged with all tests, along with all required
 * dependencies for test execution.</li>
 * <li>Created: Aug 29, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.12 $
 */
public class PDETestDeployer extends AbstractEclipseMojoHelper {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/PDETestDeployer.java,v 1.12 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * The project.
     */
    private MavenProject project;
    
    /**
     * The session.
     */
    private MavenSession session;

    /**
     * Constructs a new <code>PDETestDeployer</code> instance.
     * @param log the Mojo's log.
     * @param baseDirectory the base directory.
     * @param eclipseDirectory the Eclipse directory.
     * @param prefixes the plugin artifact id prefixes.
     * @param project the Maven project.
     * @param session the Maven session.
     */
    public PDETestDeployer(Log log, File baseDirectory, File eclipseDirectory, List prefixes, MavenProject project, MavenSession session) {
        super(log, baseDirectory, eclipseDirectory, prefixes);
        this.project = project;
        this.session = session;
    }

    /* 
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractMojoHelper#doExecute()
     */
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Deploying test artifacts and dependencies to target Eclipse environment in '" + getEclipseDirectory() + "'...");
        deployPluginArtifact();
        deployDependencies();
    }

    /**
     * Deploys the plugin artifact itself.
     * @throws MojoExecutionException
     */
    private void deployPluginArtifact() throws MojoExecutionException {
        getLog().debug("Deploying the plugin artifact...");
        File targetDirectory = new File(getBaseDirectory(), PDETestConstants.TARGET_DIRECTORY);
        File testDirectory = new File(targetDirectory, PDETestConstants.PDE_TEST_DIRECTORY);
        String artifactName = project.getArtifactId() + "-" + project.getVersion() + DeployConstants.EXTENSION_JAR;
        File sourceFile = new File(testDirectory, artifactName);
        File pluginsDirectory = getPluginsDirectory();
        File destinationFile = new File(pluginsDirectory, artifactName);
        try {
            copyFile(sourceFile, destinationFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to deploy plugin artifact", e);
        }
    }

    /**
     * Deploys the plugin artifact's dependencies.
     * @throws MojoExecutionException 
     * @throws MojoFailureException 
     */
    private void deployDependencies() throws MojoExecutionException, MojoFailureException {
        getLog().debug("Deploying the dependent artifacts...");
        Iterator artifacts = project.getDependencyArtifacts().iterator();
        while (artifacts.hasNext()) {
            Artifact artifact = (Artifact) artifacts.next();
            getLog().debug("Processing artifact: " + artifact);
            String scope = artifact.getScope();
            if (!Artifact.SCOPE_COMPILE.equals(scope)) {
                getLog().debug("Skipping artifact with '" + scope + "' scope.");
                continue;
            }
            if (isMatchForPrefix(artifact.getArtifactId())) {
                String type = artifact.getType();
                if (DeployConstants.PACKAGING_JAR.equals(type)) {
                    deployJar(artifact);
                } else if (DeployConstants.PACKAGING_POM.equals(type)) {
                    deployPom(artifact);
                } else {
                    getLog().warn("Don't know how to deploy artifact with type '" + type + "'.");
                }
            }
        }
    }

    /**
     * Deploys the specified jar-based artifact to the Eclipse plugins
     * directory.
     * @param artifact the artifact.
     * @throws MojoExecutionException
     */
    private void deployJar(Artifact artifact) throws MojoExecutionException {
        String artifactName = artifact.getArtifactId() + "-" + artifact.getVersion() + DeployConstants.EXTENSION_JAR;
        getLog().info("Deploying 'jar' artifact '" + artifactName + "'...");
        File pluginsDirectory = getPluginsDirectory();
        File destinationFile = new File(pluginsDirectory, artifactName);
        try {
            copyFile(artifact.getFile(), destinationFile);
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to deploy dependent plugin jar", e);
        }
    }

    /**
     * Deploys the specified pom-based artifact to the Eclipse plugins
     * directory.
     * @param artifact the artifact.
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    private void deployPom(Artifact artifact) throws MojoExecutionException, MojoFailureException {
        MavenProject project;
        try {
            ProfileManager profileManager = new DefaultProfileManager(session.getContainer());
            MavenProjectBuilder builder = (MavenProjectBuilder) session.lookup(MavenProjectBuilder.class.getName());
            project = builder.buildWithDependencies(artifact.getFile(), session.getLocalRepository(), profileManager);
        } catch (ComponentLookupException e) {
            throw new MojoExecutionException("Unable to lookup project builder", e);
        } catch (ProjectBuildingException e) {
            throw new MojoExecutionException("Unable to build dependent project", e);
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to resolve", e);
        }
        ArrayList buddies = new ArrayList();
        Plugin plugin = (Plugin) project.getBuild().getPluginsAsMap().get("org.apache.maven.plugins:maven-psteclipse-plugin");
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
        String artifactName = artifact.getArtifactId() + "-" + artifact.getVersion();
        getLog().info("Deploying 'pom' artifact '" + artifactName + "'...");
        File pluginsDirectory = getPluginsDirectory();
        File pluginDirectory = new File(pluginsDirectory, artifactName);
        if (!pluginDirectory.mkdir()) {
            throw new MojoExecutionException("Unable to create directory '" + pluginDirectory + "'");
        }
        ManifestGenerator generator = new ManifestGenerator(getLog(), getBaseDirectory(), project, buddies, pluginDirectory);
        generator.execute();
    }
}
