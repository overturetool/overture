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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * <ul>
 * <li>Title: TestPluginPackagingMojo</li>
 * <li>Description: The class <code>TestPluginPackagingMojo</code> is a
 * packaging Mojo for packaging Eclipse source plugins for testing.</li>
 * <li>Created: Aug 29, 2006 by: prisgupt01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.5 $
 * @goal testPackage
 * @requiresProject true
 */
public class TestPluginPackagingMojo extends AbstractPluginPackagingMojo implements ManifestConstants, PDETestConstants {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/TestPluginPackagingMojo.java,v 1.5 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * Directory containing the classes.
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File classesDirectory;

    /**
     * Directory containing the test classes.
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     */
    private File testClassesDirectory;

    /**
     * Directory containing the generated JAR.
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File outputDirectory;

    /**
     * Classifier to add to the artifact generated. If given, the artifact will
     * be an attachment instead.
     * @parameter
     */
    private String classifier;

    /**
     * Constructs a new <code>PackagingMojo</code> instance.
     */
    public TestPluginPackagingMojo() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractPackagingMojo#execute()
     */
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        File targetDirectory = new File(getBaseDirectory(), TARGET_DIRECTORY);
        outputDirectory = new File(targetDirectory, PDE_TEST_DIRECTORY);
        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                throw new MojoExecutionException("Unable to create directory '" + outputDirectory + "'");
            }
        }
        super.doExecute();
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractPackagingMojo#getClassesDirectories()
     */
    protected File[] getClassesDirectories() {
        return new File[] { classesDirectory, testClassesDirectory };
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractPackagingMojo#getClassifier()
     */
    protected String getClassifier() {
        return classifier;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractPackagingMojo#getOutputDirectory()
     */
    protected File getOutputDirectory() {
        return outputDirectory;
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractPackagingMojo#getManifest()
     */
    protected File getManifest() throws MojoExecutionException {
        File manifestFile = getPluginManifestFile();
        File newManifest = getModifiedFileHandle();
        try {
            FileInputStream fileInputStream = new FileInputStream(manifestFile);
            Manifest manifest = new Manifest(fileInputStream);
            addJunitDependency(manifest);
            writeOutModifiedManifest(newManifest, manifest);
        } catch (Exception e) {
            throw new MojoExecutionException("Error updating Manifest file");
        }
        return newManifest;
    }

    /**
     * @param newManifest
     * @param manifest
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void writeOutModifiedManifest(File newManifest, Manifest manifest) throws FileNotFoundException, IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(newManifest);
        manifest.write(fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    /**
     * @param manifest
     */
    private void addJunitDependency(Manifest manifest) {
        Attributes mainAttributes = manifest.getMainAttributes();
        String requiredBundlesString = mainAttributes.getValue(REQUIRE_BUNDLE);
        String junitDependency = "org.junit;bundle-version=\"3.8.1\"";
        if (requiredBundlesString == null) {
            requiredBundlesString = junitDependency;
        } else {
            requiredBundlesString = requiredBundlesString + "," + junitDependency;
        }
        mainAttributes.put(new Attributes.Name(REQUIRE_BUNDLE), requiredBundlesString);
    }

    /**
     * Returns the modified file handle.
     * @return the modified file handle.
     * @throws MojoExecutionException
     */
    private File getModifiedFileHandle() throws MojoExecutionException {
        File newManifest = new File(outputDirectory, MANIFEST_FILE_NAME);
        try {
            if (!newManifest.exists()) {
                newManifest.createNewFile();
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Failed during updating of manifest file for testing", e);
        }
        return newManifest;
    }
}
