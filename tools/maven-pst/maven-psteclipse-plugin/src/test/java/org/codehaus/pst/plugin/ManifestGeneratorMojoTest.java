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
import java.util.ArrayList;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.codehaus.pst.plugin.ManifestConstants;
import org.codehaus.pst.plugin.ManifestGeneratorMojo;

/**
 * <ul>
 * <li>Title: ManifestGeneratorMojoTest</li>
 * <li>Description: </li>
 * <li>Created: Aug 9, 2006 by: prisgupt01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.6 $
 */
public class ManifestGeneratorMojoTest extends AbstractMojoTestCase {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/test/java/com/princetonsoftech/maven/psteclipse/ManifestGeneratorMojoTest.java,v 1.6 2007/02/08 22:02:30 prippete01 Exp $";
    
    /**
     * Test to make sure that the Mojo can be found
     * @throws Exception
     */
    public void testMojoLookup() throws Exception {
        File baseDirectory = new File(getBasedir());
        File testResourcesDirectory = new File(baseDirectory, "src/test/resources");
        File pluginXml = new File(testResourcesDirectory, "manifest-generator-plugin-config.xml");
        ManifestGeneratorMojo mojo = (ManifestGeneratorMojo) lookupMojo("eclipse-plugin", pluginXml);
        assertNotNull(mojo);
    }
    
    /**
     * Test that buddy config works
     * @throws Exception
     */
    public void testBuddyConfig() throws Exception {
        File baseDirectory = new File(getBasedir());
        File testResourcesDirectory = new File(baseDirectory, "src/test/resources");
        File pluginXml = new File(testResourcesDirectory, "manifest-generator-plugin-config.xml");
        ManifestGeneratorMojo mojo = (ManifestGeneratorMojo) lookupMojo("eclipse-plugin", pluginXml);        
        ArrayList list = mojo.getBuddies();
        assertNotNull(list);
        assertEquals(2, list.size());
        assertTrue(list.contains("something"));
        assertTrue(list.contains("another"));
    }

    /**
     * Test that the Mojo does indeed generate a Manifest
     * under META-INF 
     * @throws Exception
     */
    public void testMojoExecution() throws Exception {
        File baseDirectory = new File(getBasedir());
        File testResourcesDirectory = new File(baseDirectory, "src/test/resources");
        File pluginXml = new File(testResourcesDirectory, "manifest-generator-plugin-config.xml");
        ManifestGeneratorMojo mojo = (ManifestGeneratorMojo) lookupMojo("eclipse-plugin", pluginXml);
        Model model = new Model();
        MavenProject mavenProject = new MavenProject(model);
        String artifactId = "test.something.id";
        mavenProject.setArtifactId(artifactId);
        mavenProject.setVersion("2.0");
        mavenProject.setName("Test POM");
        mavenProject.setPackaging("binary-plugin");
        Dependency dependency = new Dependency();
        dependency.setArtifactId("hibernate");
        dependency.setGroupId("org.hibernate");
        dependency.setVersion("3.1.3");
        dependency.setScope("compile");
        mavenProject.getDependencies().add(dependency);
        mojo.setMavenProject(mavenProject);
        assertEquals(mavenProject, mojo.getMavenProject());        
        File mojoBaseDirectory = new File(baseDirectory, "target");
        mojo.setBaseDirectory(mojoBaseDirectory);
        assertEquals(mojoBaseDirectory, mojo.getBaseDirectory());
        mojo.execute();
        File lib = new File(baseDirectory, "target/lib");
        assertTrue(lib.exists());
        File manifestFile = new File(baseDirectory, "target/META-INF/MANIFEST.MF");
        assertTrue(manifestFile.exists());
        Manifest manifest = new Manifest(new FileInputStream(manifestFile));
        Attributes mainAttributes = manifest.getMainAttributes();
        assertEquals(artifactId, mainAttributes.getValue(ManifestConstants.BUNDLE_SYMBOLIC_NAME));
        assertTrue(mainAttributes.getValue(ManifestConstants.ECLIPSE_REGISTER_BUDDY).contains("something"));
        assertEquals("2", mainAttributes.getValue(ManifestConstants.BUNDLE_MANIFEST_VERSION));
        assertEquals("1.0", mainAttributes.getValue("Manifest-Version"));
    }
    
}
