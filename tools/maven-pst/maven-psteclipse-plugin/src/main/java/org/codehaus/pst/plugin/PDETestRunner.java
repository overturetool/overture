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
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * <ul>
 * <li>Title: PDETestRunner</li>
 * <li>Description: The class <code>PDETestRunner</code> is a Mojo helper
 * that executes a headless Eclipse, using the ant-runner application, with a
 * build file for executing PDE tests using the Eclipse PDE test framework.</li>
 * <li>Created: Aug 29, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.15 $
 */
public class PDETestRunner extends AbstractEclipseMojoHelper {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/PDETestRunner.java,v 1.15 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * The build file.
     */
    private File buildFile;

    /**
     * The test failure ignore flag.
     */
    private boolean testFailureIgnore;

    /**
     * The XML document builder.
     */
    private DocumentBuilder builder;

    /**
     * The failures.
     */
    private int failures = 0;

    /**
     * The errors.
     */
    private int errors = 0;

    /**
     * Constructs a new <code>PDETestRunner</code> instance.
     * @param log the Mojo's log.
     * @param baseDirectory the base directory.
     * @param eclipseDirectory the Eclipse directory.
     * @param prefixes the plugin artifact id prefixes.
     * @param buildFile the build file to execute.
     * @param testFailureIgnore <code>true</code> if test failures should be
     * ignored; <code>false</code> otherwise.
     */
    public PDETestRunner(Log log, File baseDirectory, File eclipseDirectory, List prefixes, File buildFile, boolean testFailureIgnore) {
        super(log, baseDirectory, eclipseDirectory, prefixes);
        this.buildFile = buildFile;
        this.testFailureIgnore = testFailureIgnore;
    }

    /* 
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractMojoHelper#doExecute()
     */
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        String command = createCommand();
        ProgramRunner runner = new ProgramRunner(command);
        try {
            runner.execute();
        } catch (IOException e) {
            throw new MojoExecutionException("The Eclipse launch failed", e);
        } catch (InterruptedException e) {
            throw new MojoExecutionException("The Eclipse execution was interrupted", e);
        }
        extractErrorsAndFailures();
    }

    /**
     * Creates the required command to launch the Eclipse ant-runner
     * application.
     * @return the command.
     */
    private String createCommand() {
        File eclipseDirectory = getEclipseDirectory();
        getLog().info("Executing target Eclipse environment in '" + eclipseDirectory + "'...");
        String quote = ((eclipseDirectory.getAbsolutePath().indexOf(' ') != -1) ? "\"" : "");
        StringBuffer buffer = new StringBuffer(256);
        buffer.append("java -jar ");
        buffer.append(quote);
        buffer.append(getStartupJarFile().getAbsolutePath());
        buffer.append(quote);
        buffer.append(" -application org.eclipse.ant.core.antRunner -buildfile ");
        buffer.append(buildFile.getAbsolutePath());
        buffer.append(" -Declipse-home=");
        buffer.append(quote);
        buffer.append(eclipseDirectory.getAbsolutePath());
        buffer.append(quote);
        buffer.append(" -Dos=");
        buffer.append(getOperatingSystem());
        buffer.append(" -Dws=");
        buffer.append(getWindowSystem());
        buffer.append(" -Darch=");
        buffer.append(getArchitecture());
        buffer.append(" -clean -data ");
        buffer.append(quote);
        buffer.append(getWorkspaceDirectory().getAbsolutePath());
        buffer.append(quote);
        String command = buffer.toString();
        getLog().debug("Command-line is '" + command + "'.");
        return command;
    }

    /**
     * Determines and returns the operating system name, as needed by Eclipse.
     * @return the operating system name.
     */
    private String getOperatingSystem() {
        String name = System.getProperty("os.name");
        if (name.startsWith("Linux")) {
            return "linux";
        } else if (name.startsWith("Mac OS X")) {
            return "macosx";
        } else if (name.startsWith("Windows")) {
            return "win32";
        }
        return "unknown";
    }

    /**
     * Determines and returns the window system name, as needed by Eclipse.
     * @return the window system name.
     */
    private String getWindowSystem() {
        String name = System.getProperty("os.name");
        if (name.startsWith("Linux")) {
            return "gtk";
        } else if (name.startsWith("Mac OS X")) {
            return "carbon";
        } else if (name.startsWith("Windows")) {
            return "win32";
        }
        return "unknown";
    }

    /**
     * Determines and returns the CPU architecture name, as needed by Eclipse.
     * @return the arhictecture name.
     */
    private String getArchitecture() {
        String arch = System.getProperty("os.arch");
        if (arch.equals("x86")) {
            return "x86";
        } else if (arch.equals("i386")) {
            return "x86";
        }
        return "unknown";
    }

    /**
     * Extracts errors and failures from the surefire reports.
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    private void extractErrorsAndFailures() throws MojoExecutionException, MojoFailureException {
        getLog().info("Extracting errors and failures from surefire reports");
        System.out.println("-------------------------------------------------------");
        System.out.println(" T E S T S ");
        System.out.println("-------------------------------------------------------");
        File targetDirectory = new File(getBaseDirectory(), PDETestConstants.TARGET_DIRECTORY);
        File surefireDirectory = new File(targetDirectory, PDETestConstants.SUREFIRE_DIRECTORY);
        if (!surefireDirectory.isDirectory()) {
            getLog().warn("The surefire reports directory '" + surefireDirectory + "' was not created.");
            return;
        }
        File[] files = surefireDirectory.listFiles();
        if ((files == null) || (files.length == 0)) {
            getLog().warn("No surefire reports were moved.");
            return;
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to create XML document builder", e);
        }
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            if (name.endsWith(".xml")) {
                extractErrorsAndFailures(files[i]);
            }
        }
        if ((failures > 0) || (errors > 0)) {
            if (testFailureIgnore) {
                getLog().info("Ignoring test failures");
                return;
            }
            StringBuffer buffer = new StringBuffer();
            buffer.append("There are test ");
            if (failures > 0) {
                buffer.append("failures");
                if (errors > 0) {
                    buffer.append(" and ");
                }
            }
            if (errors > 0) {
                buffer.append("errors");
            }
            buffer.append(".");
            throw new MojoFailureException(buffer.toString());
        }
    }

    /**
     * Extracts errors and failures from the specified surefire report.
     * @param file the surefire report file.
     * @throws MojoExecutionException if the file cannot be parsed or is
     * invalid.
     */
    private void extractErrorsAndFailures(File file) throws MojoExecutionException {
        getLog().debug("Extracting errors and failures from '" + file + "'...");
        Document document;
        try {
            document = builder.parse(file);
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to parse surefire report '" + file + "'", e);
        }
        Element element = document.getDocumentElement();
        if (!PDETestConstants.ELEMENT_TESTSUITE.equals(element.getTagName())) {
            throw new MojoExecutionException("The surefire report '" + file + "' is not a valid surefire report");
        }
        String name = element.getAttribute(PDETestConstants.ATTRIBUTE_NAME);
        String time = element.getAttribute(PDETestConstants.ATTRIBUTE_TIME);
        int tests = extractCount(file, element, PDETestConstants.ATTRIBUTE_TESTS);
        int failures = extractCount(file, element, PDETestConstants.ATTRIBUTE_FAILURES);
        int errors = extractCount(file, element, PDETestConstants.ATTRIBUTE_ERRORS);
        int skipped = 0; // Can we figure this out for real?
        StringBuffer buffer = new StringBuffer();
        buffer.append("Ran ");
        buffer.append(name);
        buffer.append("\n");
        buffer.append("Tests run: ");
        buffer.append(tests);
        buffer.append(", Failures: ");
        buffer.append(failures);
        buffer.append(", Errors: ");
        buffer.append(errors);
        buffer.append(", Skipped: ");
        buffer.append(skipped);
        buffer.append(", Time elapsed: ");
        buffer.append(time);
        buffer.append(" sec");
        if ((failures > 0) || (errors > 0)) {
            buffer.append(" <<<");
            if (failures > 0) {
                buffer.append(" FAILURE!");
            }
            if (errors > 0) {
                buffer.append(" ERROR!");
            }
        }
        System.out.println(buffer.toString());
        this.failures += failures;
        this.errors += errors;
    }

    /**
     * Extracts the count from the specified attribute on the specified element,
     * parsed from the specified file.
     * @param file the surefire report file.
     * @param element the element.
     * @param name the name of the attribute to extract the count from.
     * @return the count.
     * @throws MojoExecutionException if the attribute does not exist or is not
     * an integer.
     */
    private int extractCount(File file, Element element, String name) throws MojoExecutionException {
        String value = element.getAttribute(name);
        if ((value == null) || (value.length() == 0)) {
            throw new MojoExecutionException("The surefire report '" + file + "' is missing the '" + name + "' attribute");
        }
        int count;
        try {
            count = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new MojoExecutionException("The surefire report '" + file + "' has invalid '" + name + "' attribute", e);
        }
        getLog().debug("The surefire report '" + file + "' has " + count + " " + name + ".");
        return count;
    }
}
