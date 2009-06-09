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

import org.apache.maven.archiver.MavenArchiveConfiguration;
import org.apache.maven.archiver.MavenArchiver;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;

/**
 * <ul>
 * <li>Title: AbstractPluginPackagingMojo</li>
 * <li>Description: The class <code>AbstractPluginPackagingMojo</code> is an
 * abstract base class for creating plugin packaging Mojos.</li>
 * <li>Created: Aug 29, 2006 by: prisgupt01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.7 $
 */
public abstract class AbstractPluginPackagingMojo extends AbstractEclipseMojo implements ManifestConstants {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/AbstractPluginPackagingMojo.java,v 1.7 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * The default excludes.
     */
    private static final String[] DEFAULT_EXCLUDES = new String[] { "**/package.html" };

    /**
     * The default includes.
     */
    private static final String[] DEFAULT_INCLUDES = new String[] { "**/**" };

    /**
     * The excludes for the base directory.
     */
    private static final String[] BASE_EXCLUDES = new String[] { ".*", ".settings/", "pom.xml", "src/", "target/", "META-INF/", "**/CVS/", "**/SVN/" };

    /**
     * The maven project.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${basedir}"
     * @required
     */
    private File baseDirectory;

    /**
     * Name of the generated JAR.
     * @parameter alias="jarName" expression="${project.build.finalName}"
     * @required
     */
    private String finalName;

    /**
     * The Jar archiver.
     * @parameter expression="${component.org.codehaus.plexus.archiver.Archiver#jar}"
     * @required
     */
    private JarArchiver jarArchiver;

    /**
     * The maven archive configuration to use. See <a
     * href="http://maven.apache.org/ref/current/maven-archiver/apidocs/org/apache/maven/archiver/MavenArchiveConfiguration.html">the
     * Javadocs for MavenArchiveConfiguration</a>.
     * @parameter
     */
    private MavenArchiveConfiguration archive = new MavenArchiveConfiguration();

    /**
     * @component
     */
    private MavenProjectHelper projectHelper;

    /**
     * Whether creating the archive should be forced.
     * @parameter expression="${jar.forceCreation}" default-value="false"
     */
    private boolean forceCreation;

    /**
     * Constructs a new <code>AbstractPluginPackagingMojo</code> instance.
     */
    public AbstractPluginPackagingMojo() {
        super();
    }

    /**
     * Returns the base directory.
     * @return the base directory.
     */
    protected File getBaseDirectory() {
        return baseDirectory;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractEclipseMojo#doExecute()
     */
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        String packaging = project.getPackaging();
        if ((packaging.equals("pom")) || (packaging.equals("binary-plugin"))) {
            getLog().debug("Nothing to do for packaging '" + packaging + "'");
            return;
        }
        File jarFile = createArchive();
        String classifier = getClassifier();
        if (classifier != null) {
            projectHelper.attachArtifact(getProject(), "jar", classifier, jarFile);
        } else {
            getProject().getArtifact().setFile(jarFile);
        }
    }

    /**
     * Creates the archive.
     * @return the archive file.
     * @throws MojoExecutionException
     */
    protected File createArchive() throws MojoExecutionException {
        File jarFile = getJarFile(getOutputDirectory(), finalName, getClassifier());
        project.getArtifact().setFile(jarFile);
        if (getManifest() != null) {
            try {
                jarArchiver.setManifest(getManifest());
            } catch (ArchiverException e) {
                e.printStackTrace();
            }
        }
        MavenArchiver archiver = new MavenArchiver();
        archiver.setArchiver(jarArchiver);
        archiver.setOutputFile(jarFile);
        archive.setForced(forceCreation);
        try {
            File[] contentDirectories = getClassesDirectories();
            for (int i = 0; i < contentDirectories.length; i++) {
                File contentDirectory = contentDirectories[i];
                if (!contentDirectory.exists()) {
                    getLog().warn("Content Directory " + contentDirectory.getAbsolutePath() + "not found");
                } else {
                    archiver.getArchiver().addDirectory(contentDirectory, DEFAULT_INCLUDES, DEFAULT_EXCLUDES);
                }
            }
            archiver.getArchiver().addDirectory(baseDirectory, DEFAULT_INCLUDES, BASE_EXCLUDES);
            archiver.createArchive(project, archive);
            return jarFile;
        } catch (Exception e) {
            throw new MojoExecutionException("Error assembling JAR", e);
        }
    }

    /**
     * Return the specific output directories to serve as the roots for the
     * archive.
     * @return the classes directory.
     */
    protected abstract File[] getClassesDirectories();

    /**
     * Returns the file for the manifest.
     * @return the manifest file.
     * @throws MojoExecutionException
     */
    protected abstract File getManifest() throws MojoExecutionException;

    /**
     * Overload this to produce a test-jar, for example.
     * @return the classifier.
     */
    protected abstract String getClassifier();

    /**
     * Returns the Maven project.
     * @return the project.
     */
    protected final MavenProject getProject() {
        return project;
    }

    /**
     * Returns the jar file.
     * @param directory the directory.
     * @param finalName the final name.
     * @param classifier the classifier.
     * @return the jar file.
     */
    protected File getJarFile(File directory, String finalName, String classifier) {
        if (classifier == null) {
            classifier = "";
        } else if ((classifier.trim().length() > 0) && (!classifier.startsWith("-"))) {
            classifier = "-" + classifier;
        }
        return new File(directory, finalName + classifier + ".jar");
    }

    /**
     * Returns the Plugin's manifest file.
     * @return the manifest file.
     * @throws MojoExecutionException
     */
    protected File getPluginManifestFile() throws MojoExecutionException {
        File manifestDirectory = new File(baseDirectory, MANIFEST_DIRECTORY);
        if (!manifestDirectory.exists()) {
            throw new MojoExecutionException("No META-INF to be found");
        }
        File manifestFile = new File(manifestDirectory, MANIFEST_FILE_NAME);
        if (!manifestFile.exists()) {
            throw new MojoExecutionException("No MANIFEST.MF to be found");
        }
        return manifestFile;
    }

    /**
     * Returns the outputDirectory.
     * @return the outputDirectory.
     */
    protected abstract File getOutputDirectory();
}
