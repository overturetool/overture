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
import java.util.ArrayList;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * <ul>
 * <li>Title: PDETestMojo</li>
 * <li>Description: The class <code>PDETestMojo</code> is a Maven Mojo that
 * sets up a PDE test for Eclipse.</li>
 * <li>Created: Aug 28, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.14 $
 * @goal test
 * @phase test
 * @requiresProject true
 */
public class PDETestMojo extends AbstractEclipseMojo {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/PDETestMojo.java,v 1.14 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * Set this to 'true' to skip the tests.
     * @parameter expression="${maven.test.skip}"
     */
    private boolean skip;

    /**
     * Set this to 'true' to ignore test failures. The primary use case for this
     * is cobertura, which needs to run regardless of whether the tests pass or
     * not.
     * @parameter expression="${testFailureIgnore}"
     */
    private boolean testFailureIgnore;

    /**
     * The base directory.
     * @parameter expression="${basedir}"
     * @required
     */
    private File baseDirectory;

    /**
     * The directory containing generated test classes of the project being
     * tested.
     * @parameter expression="${project.build.testOutputDirectory}"
     * @required
     */
    private File testOutputDirectory;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    protected ArtifactRepository localRepository;

    /**
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    protected MavenSession session;

    /**
     * The Eclipse directory.
     * @parameter expression="${eclipseDirectory}"
     */
    private File eclipseDirectory;

    /**
     * The Eclipse PDE test framework version.
     * @parameter expression="${testFrameworkVersion}"
     * @required
     */
    private String testFrameworkVersion;

    /**
     * The prefixes.
     * @parameter expression="${prefixes}"
     */
    private ArrayList prefixes;

    /**
     * Constructs a new <code>TestMojo</code> instance.
     */
    public PDETestMojo() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractEclipseMojo#doExecute()
     */
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        if (!needsToRunTests()) {
            return;
        }
        setupDefaults();
        PDETestEclipseValidator validator = new PDETestEclipseValidator(getLog(), baseDirectory, eclipseDirectory, prefixes, testFrameworkVersion);
        validator.execute();
        File targetDirectory = new File(baseDirectory, PDETestConstants.TARGET_DIRECTORY);
        File testDirectory = new File(targetDirectory, PDETestConstants.PDE_TEST_DIRECTORY);
        if (!testDirectory.exists()) {
            if (!testDirectory.mkdirs()) {
                throw new MojoExecutionException("Unable to create '" + PDETestConstants.PDE_TEST_DIRECTORY + "' directory '" + testDirectory + "'");
            }
        }
        PDETestEclipseCleaner cleaner = new PDETestEclipseCleaner(getLog(), baseDirectory, eclipseDirectory, prefixes);
        cleaner.execute();
        File buildFile = new File(testDirectory, "test.xml");
        PDETestFileBuilder builder = new PDETestFileBuilder(getLog(), baseDirectory, eclipseDirectory, prefixes, project, buildFile, testOutputDirectory, project.getArtifactId(), testFrameworkVersion);
        builder.execute();
        if (builder.getCount() > 0) {
            PDETestDeployer deployer = new PDETestDeployer(getLog(), baseDirectory, eclipseDirectory, prefixes, project, session);
            deployer.execute();
            PDETestRunner runner = new PDETestRunner(getLog(), baseDirectory, eclipseDirectory, prefixes, buildFile, testFailureIgnore);
            runner.execute();
        } else {
            getLog().info("No test cases found - skipping");
        }
    }

    /**
     * Returns a boolean flag indicating whether or not we need to run tests. If
     * the 'skip' flag is <code>true</code> or if there are no tests, this
     * method returns <code>false</code>.
     * @return <code>true</code> if tests need to be run; <code>false</code>
     * otherwise.
     */
    private boolean needsToRunTests() {
        if (skip) {
            getLog().info("Tests are skipped.");
            return false;
        }
        if (!testOutputDirectory.exists()) {
            getLog().info("No tests to run.");
            return false;
        }
        return true;
    }

    /**
     * Sets up appropriate defaults.
     */
    private void setupDefaults() {
        if (eclipseDirectory == null) {
            File userHomeDirecory = new File(System.getProperty("user.home"));
            eclipseDirectory = new File(userHomeDirecory, EclipseConstants.ECLIPSE_DIRECTORY);
            getLog().info("Defaulting Eclipse home directory to '" + eclipseDirectory + "'.");
        }
        if ((prefixes == null) || (prefixes.size() == 0)) {
            if (prefixes == null) {
                prefixes = new ArrayList();
            }
            String artifactId = project.getArtifactId();
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
}
