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
import java.util.Iterator;
import java.util.List;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.codehaus.pst.plugin.ManifestConstants;
import org.codehaus.pst.plugin.PomModifierMojo;

/**
 * <ul>
 * <li>Title: PomModifierMojoTest</li>
 * <li>Description: </li>
 * <li>Created: Aug 3, 2006 by: prisgupt01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.6 $
 */
public class PomModifierMojoTest extends AbstractMojoTestCase {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/test/java/com/princetonsoftech/maven/psteclipse/PomModifierMojoTest.java,v 1.6 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * Test to make sure that the Mojo can be found
     * @throws Exception
     */
    public void testMojoLookup() throws Exception {
        File pluginXml = new File(getBasedir(), "src/test/resources/pom-modifier-plugin-config.xml");
        PomModifierMojo mojo = (PomModifierMojo) lookupMojo("update", pluginXml);
        assertNotNull(mojo);
    }

    /**
     * Test that the Mojo does indeed insert dependencies from a Manifest found
     * under META-INF into the MavenProject
     * @throws Exception
     */
    public void testMojoExecution() throws Exception {
        File baseDirectory = new File(getBasedir());
        File testResourcesDirectory = new File(baseDirectory, "src/test/resources");
        File pluginXml = new File(testResourcesDirectory, "pom-modifier-plugin-config.xml");
        PomModifierMojo mojo = (PomModifierMojo) lookupMojo("update", pluginXml);
        mojo.setBaseDirectory(testResourcesDirectory);
        Model model = new Model();
        model.setGroupId(ManifestConstants.GROUP_ID_PST_ECLIPSE);
        model.setArtifactId(ManifestConstants.GROUP_ID_PST_ECLIPSE);
        MavenProject mavenProject = new MavenProject(model);
        mojo.setMavenProject(mavenProject);
        assertEquals(mavenProject, mojo.getMavenProject());
        mojo.execute();
        List dependencies = mavenProject.getDependencies();
        assertEquals(11, dependencies.size());
        Iterator iterator = dependencies.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            Dependency dependency = (Dependency) iterator.next();
            if (dependency.getArtifactId().equals("org.apache.ant")) {
                assertEquals(dependency.getVersion(), "1.6.5");
                count++;
            }
        }
        assertEquals(1, count);
    }

    /**
     * Test logging capabilities and configuration for the Mojo
     * 
     * @throws Exception
     */
    public void testMojoLogging() throws Exception {
        File baseDirectory = new File(getBasedir());
        File testResourcesDirectory = new File(baseDirectory, "src/test/resources");
        File pluginXml = new File(testResourcesDirectory, "pom-modifier-plugin-config.xml");
        PomModifierMojo mojo = (PomModifierMojo) lookupMojo("update", pluginXml);
        File manifest = new File(testResourcesDirectory, "axis-manifest.mf");
        mojo.setManifest(manifest);
        mojo.setBaseDirectory(baseDirectory);
        mojo.setLogModifications(true);
        //For coverage purposes, test the getters
        assertEquals(manifest, mojo.getManifest());
        assertEquals(baseDirectory, mojo.getBaseDirectory());
        assertTrue(mojo.isLogModifications());
        Model model = new Model();
        model.setGroupId(ManifestConstants.GROUP_ID_PST_ECLIPSE);
        model.setArtifactId(ManifestConstants.GROUP_ID_PST_ECLIPSE);
        MavenProject mavenProject = new MavenProject(model);
        mojo.setMavenProject(mavenProject);
        mojo.execute();
        File modLogFile = new File(baseDirectory, "target/pst-logs/" + PomModifierMojo.MODIFIED_POM_LOG_FILE);
        File origLogFile = new File(baseDirectory, "target/pst-logs/" + PomModifierMojo.ORIGINAL_POM_LOG_FILE);        
        assertTrue(modLogFile.exists());
        assertTrue(origLogFile.exists());
    }
}
