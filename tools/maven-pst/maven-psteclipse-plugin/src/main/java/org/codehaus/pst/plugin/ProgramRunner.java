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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * <ul>
 * <li>Title: ProgramRunner</li>
 * <li>Description: The class <code>ProgramRunner</code> is a simple program
 * runner for running an Eclipse test instance.</li>
 * <li>Created: Aug 31, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.3 $
 */
public class ProgramRunner {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/ProgramRunner.java,v 1.3 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * The command.
     */
    private String command;

    /**
     * Constructs a new <code>ProgramRunner</code> instance.
     * @param command the command to execute.
     */
    public ProgramRunner(String command) {
        super();
        this.command = command;
    }

    /**
     * Executes the command.
     * @return the command's exit value.
     * @throws IOException
     * @throws InterruptedException
     */
    public int execute() throws IOException, InterruptedException {
        Process process = Runtime.getRuntime().exec(command);
        ProcessStreamReader stdoutStreamReader = new ProcessStreamReader(process.getInputStream(), System.out);
        Thread stdoutThread = new Thread(stdoutStreamReader);
        stdoutThread.setDaemon(true);
        stdoutThread.start();
        ProcessStreamReader stderrStreamReader = new ProcessStreamReader(process.getErrorStream(), System.err);
        Thread stderrThread = new Thread(stderrStreamReader);
        stderrThread.setDaemon(true);
        stderrThread.start();
        int exitValue = process.waitFor();
        stdoutThread.join();
        stderrThread.join();
        return exitValue;
    }

    /**
     * The class <code>ProcessStreamReader</code> is a simple
     * <code>Runnable</code> for reading process output.
     */
    private class ProcessStreamReader implements Runnable {
        /**
         * The process' input stream.
         */
        private InputStream inputStream;

        /**
         * The print stream to print to.
         */
        private PrintStream printStream;

        /**
         * Constructs a new <code>ProcessStreamReader</code> instance.
         * @param inputStream
         * @param printStream
         */
        public ProcessStreamReader(InputStream inputStream, PrintStream printStream) {
            super();
            this.inputStream = inputStream;
            this.printStream = printStream;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                do {
                    line = reader.readLine();
                    if (line != null) {
                        printStream.println(line);
                    }
                } while (line != null);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
