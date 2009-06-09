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
import java.io.File;
import java.io.FileReader;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.pst.plugin.AbstractEclipseMojo;

/**
 * <ul>
 * <li>Title: DeployMojoTest</li>
 * <li>Description: </li>
 * <li>Created: Aug 4, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.6 $
 */
public class DeployMojoTest extends AbstractMojoTestCase {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/test/java/com/princetonsoftech/maven/psteclipse/DeployMojoTest.java,v 1.6 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * Constructs a new <code>DeployMojoTest</code> instance.
     */
    public DeployMojoTest() {
        super();
    }

    /**
     * Test to make sure that the Mojo can be found
     * @throws Exception
     */
    public void testMojoLookup() throws Exception {
        File baseDirectory = new File(getBasedir());
        File testResourcesDirectory = new File(baseDirectory, "src/test/resources");
        File pluginXml = new File(testResourcesDirectory, "deploy-plugin-config.xml");
        AbstractEclipseMojo mojo = (AbstractEclipseMojo) lookupMojo("deploy", pluginXml);
        assertNotNull(mojo);
    }
    
    /**
     * Tests the execution of the mojo.
     * @throws Exception
     */
    public void testMojoExecution() throws Exception {
        File baseDirectory = new File(getBasedir());
        File testResourcesDirectory = new File(baseDirectory, "src/test/resources");
        File pluginXml = new File(testResourcesDirectory, "deploy-plugin-config.xml");
        AbstractEclipseMojo mojo = (AbstractEclipseMojo) lookupMojo("deploy", pluginXml);
        mojo.execute();
        File deployScript = new File("target/deploy.txt");
        assertTrue("Mojo failed to create 'deploy.txt'", deployScript.length() > 0);
        BufferedReader reader = new BufferedReader(new FileReader(deployScript));
        String line;
        int count = 0;
        do {
            line = reader.readLine();
            if (line != null) {
                count++;
                assertTrue("Mojo emitted garbage", line.startsWith("mvn "));
            }
        } while (line != null);
        assertEquals("Mojo failed to create correct number of commands in 'deploy.txt'", 4, count);
    }
}
