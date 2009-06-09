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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Manifest;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * <ul>
 * <li>Title: PomModifierMojo</li>
 * <li>Description: The class <code>PomModifierMojo</code> is a Maven Mojo
 * that updates the in-memory POM with entries in a Manifest file. This Mojo
 * expects that the Manifest file lives in under a META-INF directory which will
 * be a sibling to the pom.xml file. If the Manifest lives elsewhere it can be
 * configured with a path relative to the pom.xml file. The Mojo's primary use
 * is for Princeton Softech's Eclipse Plugin projects.</li>
 * <li>Created: Aug 2, 2006 by: prisgupt01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.14 $
 * @goal update
 * @phase process-resources
 * @requiresProject true
 */
public class PomModifierMojo extends AbstractEclipseMojo {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved."; //$NON-NLS-1$

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/PomModifierMojo.java,v 1.14 2007/02/08 22:02:30 prippete01 Exp $"; //$NON-NLS-1$

    /**
     * Name of log file for original pom
     */
    public static final String ORIGINAL_POM_LOG_FILE = "orig.pom.log";

    /**
     * Name of log file for modified pom
     */
    public static final String MODIFIED_POM_LOG_FILE = "mod.pom.log";

    /**
     * Name of 'target' directory.
     */
    public static final String TARGET_DIRECTORY = "target";

    /**
     * Name of the 'pst-log's directory.
     */
    public static final String LOGS_DIRECTORY = "pst-logs";

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
     * @component
     */
    private ArtifactResolver artifactResolver;

    /**
     * @component
     */
    private ArtifactFactory artifactFactory;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository artifactRepository;

    /**
     * Manifest file
     * @parameter expression="${manifest}"
     */
    private File manifest;

    /**
     * Flag to set whether the Mojo should complain if the Manifest file is not
     * found
     * @parameter expression="${requireManifest}"
     */
    private boolean requireManifest;

    /**
     * Flag to control logging of POM files before and after modifications
     * @parameter expression="${logMods}"
     */
    private boolean logModifications;

    /**
     * The prefixes.
     * @parameter expression="${prefixes}"
     */
    private ArrayList prefixes;

    /**
     * Returns the base directory.
     * @return the base directoryr.
     */
    public File getBaseDirectory() {
        return baseDirectory;
    }

    /**
     * Sets the base directory.
     * @param baseDirectory the base directory.
     */
    public void setBaseDirectory(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Returns the mavenProject.
     * @return the mavenProject.
     */
    public MavenProject getMavenProject() {
        return mavenProject;
    }

    /**
     * Sets the mavenProject.
     * @param mavenProject the mavenProject.
     */
    public void setMavenProject(MavenProject mavenProject) {
        this.mavenProject = mavenProject;
    }

    /**
     * Returns the logModifications.
     * @return the logModifications.
     */
    public boolean isLogModifications() {
        return logModifications;
    }

    /**
     * Sets the logModifications.
     * @param logModifications the logModifications.
     */
    public void setLogModifications(boolean logModifications) {
        this.logModifications = logModifications;
    }

    /**
     * Returns the manifest.
     * @return the manifest.
     */
    public File getManifest() {
        return manifest;
    }

    /**
     * Sets the manifest.
     * @param manifest the manifest.
     */
    public void setManifest(File manifest) {
        this.manifest = manifest;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractEclipseMojo#doExecute()
     */
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        if (logModifications) {
            logOriginalPom();
        }
        if ((manifest == null) || !manifest.exists()) {
            String path = baseDirectory.getAbsolutePath() + File.separator + ManifestConstants.MANIFEST_DIRECTORY + File.separator + ManifestConstants.MANIFEST_FILE_NAME;
            manifest = new File(path);
        }
        if (manifest.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(manifest);
                Manifest manifest = new Manifest(fileInputStream);
                ManifestParser parser = new ManifestParser(manifest);
                parser.setGroupId(mavenProject.getGroupId());
                parser.parse();
                DependencyManagement parsedManagement = parser.getDependencyManagement();
                List dependencies = parsedManagement.getDependencies();
                if (dependencies != null && dependencies.size() > 0) {
                    Iterator iterator = dependencies.iterator();
                    while (iterator.hasNext()) {
                        Dependency dependency = (Dependency) iterator.next();
                        if (dependency.getVersion() == null) {
                            dependency.setVersion(parser.getVersion());
                        }
                        convertToSnapshotVersion(dependency);
                        if (ManifestConstants.SCOPE_SYSTEM.equals(dependency.getScope())) {
                            String systemPath = dependency.getSystemPath();
                            File file = new File(systemPath);
                            if (!file.exists()) {
                                file = new File(baseDirectory, systemPath);
                                dependency.setSystemPath(file.getAbsolutePath());
                            }
                        } else {
                            String type = mavenProject.getProperties().getProperty(dependency.getArtifactId());
                            if (type != null) {
                                dependency.setType(type);
                            } else {
                                Artifact artifact = artifactFactory.createBuildArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), "jar");
                                try {
                                    artifactResolver.resolve(artifact, mavenProject.getRemoteArtifactRepositories(), artifactRepository);
                                    dependency.setType("jar");
                                } catch (Exception e) {
                                    // Do Nothing since the default is 'pom'
                                }
                            }
                        }
                        mavenProject.getDependencies().add(dependency);
                    }
                }
            } catch (IOException e) {
                throw new MojoExecutionException("Failed to update POM from Eclipse Manifest", e);
            }
        } else if (requireManifest) {
            throw new MojoExecutionException("The Manifest file cannot be found.");
        }
        if (logModifications) {
            logModifiedPom();
        }
    }

    /**
     * Check to see if the dependency is a module we build and change the
     * version to be a SNAPSHOT version.
     * @param dependency
     */
    private void convertToSnapshotVersion(Dependency dependency) {
        if (isInModuleLists(dependency.getArtifactId())) {
            dependency.setVersion(dependency.getVersion() + "-SNAPSHOT");
        }
    }

    /**
     * Check if the artifact Id is in the configured prefix list. If no prefixes
     * are defined the module list is checked to see if the artifact is one we
     * built. This method checks only upto two levels up for module ownership.
     * If no parents are found the default prefix is checked.
     * @param artifactId the artifact id.
     * @return <code>true</code> if the artifact id was found;
     * <code>false</code> otherwise.
     */
    private boolean isInModuleLists(String artifactId) {
        if (prefixes != null && prefixes.size() > 0) {
            return isMatchForPrefix(artifactId);
        }
        MavenProject parent = mavenProject.getParent();
        if (parent != null) {
            List modules = parent.getModules();

            if (modules != null) {
                if (modules.contains(artifactId)) {
                    return true;
                }
            }
            parent = parent.getParent();
            if (parent != null) {
                modules = parent.getModules();
                if (modules != null) {
                    if (modules.contains(artifactId)) {
                        return true;
                    }
                }
            }
        }
        setDefaultPrefix();
        return isMatchForPrefix(artifactId);
    }

    /**
     * Logs theoriginal POM
     */
    private void logOriginalPom() {
        try {
            Writer writer = getWriter(ORIGINAL_POM_LOG_FILE);
            if (writer != null) {
                mavenProject.writeModel(writer);
                writer.close();
            }
        } catch (IOException e) {
            getLog().warn("Could not log original POM");
        }
    }

    /**
     * Logs the modified POM
     */
    private void logModifiedPom() {
        try {
            Writer writer = getWriter(MODIFIED_POM_LOG_FILE);
            if (writer != null) {
                mavenProject.writeModel(writer);
                writer.close();
            }
        } catch (IOException e) {
            getLog().warn("Could not log modified POM");
        }
    }

    /**
     * Sets the default prefix
     */
    public void setDefaultPrefix() {
        if ((prefixes == null) || (prefixes.size() == 0)) {
            if (prefixes == null) {
                prefixes = new ArrayList();
            }
            String artifactId = mavenProject.getArtifactId();
            String[] segments = artifactId.split("\\.");
            String prefix;
            if (segments.length > 1) {
                prefix = segments[0] + "." + segments[1] + ".";
            } else {
                prefix = segments[0];
            }
            getLog().info("Defaulting prefixes to the single prefix '" + prefix + "'.");
            prefixes.add(prefix);
        }
    }

    /**
     * Returns a boolean flag indicating whether or not the specified artifact
     * id is a match for any of the prefixes.
     * @param artifactId the artifact id to check.
     * @return <code>true</code> if a match is found; <code>false</code>
     * otherwise.
     */
    protected boolean isMatchForPrefix(String artifactId) {
        Iterator iterator = prefixes.iterator();
        while (iterator.hasNext()) {
            String prefix = (String) iterator.next();
            if (artifactId.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates and returns a writer for logging a POM.
     * @param logFileName name of the log file
     * @return the writer.
     * @throws IOException if the writer cannot be created.
     */
    private Writer getWriter(String logFileName) throws IOException {
        File targetDir = new File(baseDirectory, TARGET_DIRECTORY);
        File logDir = new File(targetDir, LOGS_DIRECTORY);
        if (!logDir.exists()) {
            logDir.mkdirs();
        }
        File pomLog = new File(logDir, logFileName);
        if (!pomLog.exists()) {
            pomLog.createNewFile();
        }
        return new FileWriter(pomLog);
    }
}
