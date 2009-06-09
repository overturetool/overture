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
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.util.IOUtil;

/**
 * <ul>
 * <li>Title: AbstractMojoHelper</li>
 * <li>Description: The class <code>AbstractMojoHelper</code> is an abstract
 * class for implementing Mojo helpers.</li>
 * <li>Created: Aug 29, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.6 $
 */
public abstract class AbstractMojoHelper {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/AbstractMojoHelper.java,v 1.6 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * The log.
     */
    private Log log;

    /**
     * The base directory.
     */
    private File baseDirectory;

    /**
     * Constructs a new <code>AbstractMojoHelper</code> instance.
     * @param log the Mojo's log.
     * @param baseDirectory the base directory.
     */
    public AbstractMojoHelper(Log log, File baseDirectory) {
        super();
        this.log = log;
        this.baseDirectory = baseDirectory;
    }

    /**
     * Executes this helper.
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public final void execute() throws MojoExecutionException, MojoFailureException {
        boolean success = false;
        try {
            preExecute();
            doExecute();
        } catch (Throwable t) {
            success = false;
            if (t instanceof MojoExecutionException) {
                throw (MojoExecutionException) t;
            } else if (t instanceof MojoFailureException) {
                throw (MojoFailureException) t;
            } else {
                throw new MojoExecutionException("Mojo helper execution failed: " + t.getMessage(), t);
            }
        } finally {
            postExecute(success);
        }
    }

    /**
     * Invoked just prior to <code>doExecute</code>. Override to perform
     * pre-execution logic.
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    protected void preExecute() throws MojoExecutionException, MojoFailureException {
        getLog().debug("Mojo '" + getClass().getName() + "' executing...");
    }

    /**
     * Does the actual execution. Override to implement execution logic.
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    protected abstract void doExecute() throws MojoExecutionException, MojoFailureException;

    /**
     * Invoked just after <code>doExecute</code>. Override to perform
     * post-execution logic.
     * @param success <code>true</code> if the <code>preExecute</code> and
     * <code>doExecute</code> were successful; <code>false</code> otherwise.
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    protected void postExecute(boolean success) throws MojoExecutionException, MojoFailureException {
        getLog().debug("Mojo '" + getClass().getName() + "' executed " + (success ? "successfully" : "unsuccessfully"));
    }

    /**
     * Returns the Mojo's log.
     * @return the log.
     */
    protected Log getLog() {
        return log;
    }

    /**
     * Returns the base directory.
     * @return the base directory.
     */
    protected File getBaseDirectory() {
        return baseDirectory;
    }

    /**
     * Copies the contents of the specified source file to the specified
     * destination file.
     * @param sourceFile the source file.
     * @param destinationFile the destionation file.
     * @throws IOException if an I/O error occurs during copying.
     */
    protected void copyFile(File sourceFile, File destinationFile) throws IOException {
        FileInputStream sourceStream = new FileInputStream(sourceFile);
        FileOutputStream destinationStream = new FileOutputStream(destinationFile);
        IOUtil.copy(sourceStream, destinationStream);
        IOUtil.close(sourceStream);
        IOUtil.close(destinationStream);
    }
}
