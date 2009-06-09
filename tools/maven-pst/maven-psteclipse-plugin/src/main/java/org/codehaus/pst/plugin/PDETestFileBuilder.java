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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * <ul>
 * <li>Title: PDETestFileBuilder</li>
 * <li>Description: The class <code>PDETestFileBuilder</code> is a Mojo
 * helper that builds a PDE test framework build file for executing PDE tests.</li>
 * <li>Created: Aug 29, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.10 $
 */
public class PDETestFileBuilder extends AbstractEclipseMojoHelper {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/PDETestFileBuilder.java,v 1.10 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * The buffer.
     */
    private StringBuffer buffer = new StringBuffer(4096);

    /**
     * The count.
     */
    private int count = 0;
    
    /**
     * The project.
     */
    private MavenProject project;

    /**
     * The file.
     */
    private File file;

    /**
     * The directory.
     */
    private File directory;

    /**
     * The artifact id.
     */
    private String artifactId;

    /**
     * The Eclipse PDE test framework version.
     */
    private String testFrameworkVersion;

    /**
     * The Cobertura jar file.
     */
    private File coberturaJar;

    /**
     * Constructs a new <code>BuildFileBuilder</code> instance.
     * @param log the Mojo's log.
     * @param baseDirectory the base directory.
     * @param eclipseDirectory the Eclipse directory.
     * @param prefixes the plugin artifact id prefixes.
     * @param project the Maven project.
     * @param file the build file.
     * @param directory the directory to look for tests.
     * @param artifactId the artifact id.
     * @param testFrameworkVersion the test framework version.
     */
    public PDETestFileBuilder(Log log, File baseDirectory, File eclipseDirectory, List prefixes, MavenProject project, File file, File directory, String artifactId, String testFrameworkVersion) {
        super(log, baseDirectory, eclipseDirectory, prefixes);
        this.project = project;
        this.file = file;
        this.directory = directory;
        this.artifactId = artifactId;
        this.testFrameworkVersion = testFrameworkVersion;
    }

    /**
     * Returns the count.
     * @return the count.
     */
    public int getCount() {
        return count;
    }
    
    /* 
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractMojoHelper#doExecute()
     */
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Building 'test.xml' for PDE testing in '" + file + "'...");
        findCoberturaJar();
        buffer.append("<?xml version=\"1.0\"?>\n\n");
        buffer.append("<!-- Auto-generated file - DO NOT MODIFY! -->\n\n");
        buffer.append("<project name=\"");
        getLog().debug("The artifact id is '" + artifactId + "'.");
        buffer.append(artifactId);
        buffer.append("\" default=\"run\" basedir=\"");
        buffer.append(getBaseDirectory().getAbsolutePath());
        buffer.append("\">\n");
        appendProperties();
        appendInitTarget();
        List testClasses = new ArrayList();
        findTestClasses(directory, testClasses);
        appendSuiteTarget(testClasses);
        appendCleanupTarget();
        appendRunTarget();
        buffer.append("</project>\n");
        try {
            FileWriter writer = new FileWriter(file);
            writer.write(buffer.toString());
            writer.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Unable to create 'test.xml'", e);
        }
    }

    /**
     * Looks for the Cobertura test jar artifact's file. If found, it means
     * we're running as a part of a cobertura run; if not, it's normal testing.
     */
    private void findCoberturaJar() {
        getLog().debug("Looking for the Cobertura jar...");
        Iterator iterator = project.getTestArtifacts().iterator();
        while (iterator.hasNext()) {
            Artifact artifact = (Artifact) iterator.next();
            if (!"cobertura".equals(artifact.getGroupId())) {
                continue;
            }
            if (!"cobertura".equals(artifact.getArtifactId())) {
                continue;
            }
            if (!DeployConstants.PACKAGING_JAR.equals(artifact.getType())) {
                continue;
            }
            coberturaJar = artifact.getFile();
            getLog().debug("Found '" + coberturaJar + "'.");
            return;
        }
        getLog().debug("Unable to locate Cobertura jar.");
    }

    /**
     * Finds all test classes in the specified directory and adds them to the
     * specified list.
     * @param directory the directory to look for classes in.
     * @param testClasses the list of test classes.
     */
    private void findTestClasses(File directory, List testClasses) {
        getLog().debug("Looking for tests in '" + directory + "'...");
        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                findTestClasses(files[i], testClasses);
            } else {
                String name = files[i].getName();
                if (name.endsWith("Test.class")) {
                    getLog().debug("The file '" + files[i] + "' appears to be a test.");
                    if (name.startsWith("Abstract")) {
                        getLog().debug("Skipping file '" + files[i] + "' since it's abstract.");
                        continue;
                    }
                    getLog().debug("Adding file '" + files[i] + "'.");
                    testClasses.add(files[i]);
                    count++;
                }
            }
        }
    }

    /**
     * Appends all the required properties to the build file buffer.
     */
    private void appendProperties() {
        appendProperty("eclipse-home", getEclipseDirectory().getAbsolutePath());
        appendProperty("plugin-name", artifactId);
        appendProperty("test-framework-version", testFrameworkVersion);
        appendProperty("library-file", "${eclipse-home}/plugins/org.eclipse.test_${test-framework-version}/library.xml");
        buffer.append("\n");
    }

    /**
     * Appends a property to the build file buffer.
     * @param name the property name.
     * @param value the property value.
     */
    private void appendProperty(String name, String value) {
        buffer.append("\t<property name=\"");
        buffer.append(name);
        buffer.append("\" value=\"");
        buffer.append(value);
        buffer.append("\" />\n");
    }

    /**
     * Appends the 'init' target to the build file buffer.
     */
    private void appendInitTarget() {
        buffer.append("\t<target name=\"init\">\n");
        buffer.append("\t\t<delete>\n");
        buffer.append("\t\t\t<fileset dir=\"${eclipse-home}\">\n");
        buffer.append("\t\t\t\t<include name=\"*.xml\" />\n");
        buffer.append("\t\t\t\t<include name=\"cobertura.ser\" />\n");
        buffer.append("\t\t\t</fileset>\n");
        buffer.append("\t\t</delete>\n");
        if (coberturaJar != null) {
            buffer.append("\t\t<move file=\"cobertura.ser\" todir=\"${eclipse-home}\" />\n");
        }
        buffer.append("\t</target>\n");
        buffer.append("\n");
    }

    /**
     * Appends the 'suite' target to the build file buffer.
     * @param testClasses the list of test classes to append.
     */
    private void appendSuiteTarget(List testClasses) {
        buffer.append("\t<target name=\"suite\">\n");
        buffer.append("\t\t<property name=\"session-folder\" value=\"${eclipse-home}/core_session_sniff_folder\" />\n");
        buffer.append("\t\t<delete dir=\"${session-folder}\" quiet=\"true\" />\n");
        int beginIndex = directory.getAbsolutePath().length() + 1;
        Iterator iterator = testClasses.iterator();
        while (iterator.hasNext()) {
            File testClass = (File) iterator.next();
            String relativePath = testClass.getAbsolutePath().substring(beginIndex);
            String className = relativePath.substring(0, relativePath.length() - 6);
            className = className.replace('\\', '/');
            className = className.replace('/', '.');
            appendCoreTestAntCall(className);
        }
        buffer.append("\t</target>\n");
        buffer.append("\n");
    }

    /**
     * Appends an ant call to the 'core-test' target to the specified build file
     * buffer for the specified class name.
     * @param className the class name.
     */
    private void appendCoreTestAntCall(String className) {
        buffer.append("\t\t<ant target=\"core-test\" antfile=\"${library-file}\" dir=\"${eclipse-home}\">\n");
        if (coberturaJar != null) {
            buffer.append("\t\t\t<property name=\"vmargs\" value=\"");
            buffer.append("-Xbootclasspath/a:&quot;");
            buffer.append(coberturaJar.getAbsolutePath());
            buffer.append("&quot;\" />\n");
        }
        buffer.append("\t\t\t<property name=\"data-dir\" value=\"&quot;${session-folder}&quot;\" />\n");
        buffer.append("\t\t\t<property name=\"plugin-name\" value=\"${plugin-name}\" />\n");
        buffer.append("\t\t\t<property name=\"classname\" value=\"");
        buffer.append(className);
        buffer.append("\" />\n");
        buffer.append("\t\t</ant>\n");
    }

    /**
     * Appends the 'cleanup' target to the build file buffer.
     */
    private void appendCleanupTarget() {
        buffer.append("\t<target name=\"cleanup\">\n");
        buffer.append("\t</target>\n");
        buffer.append("\n");
    }

    /**
     * Appends the 'run' target to the build file buffer.
     */
    private void appendRunTarget() {
        buffer.append("\t<target name=\"run\" depends=\"init,suite,cleanup\">\n");
        buffer.append("\t\t<echo message=\"Moving surefire reports\"/>\n");
        buffer.append("\t\t<mkdir dir=\"target/surefire-reports\" />\n");
        buffer.append("\t\t<move todir=\"target/surefire-reports\">\n");
        buffer.append("\t\t\t<fileset dir=\"${eclipse-home}\">\n");
        buffer.append("\t\t\t\t<include name=\"*.xml\" />\n");
        buffer.append("\t\t\t</fileset>\n");
        buffer.append("\t\t</move>\n");
        if (coberturaJar != null) {
            buffer.append("\t\t<move file=\"${eclipse-home}/cobertura.ser\" todir=\"${basedir}\" />\n");
        }
        buffer.append("\t</target>\n");
        buffer.append("\n");
    }
}
