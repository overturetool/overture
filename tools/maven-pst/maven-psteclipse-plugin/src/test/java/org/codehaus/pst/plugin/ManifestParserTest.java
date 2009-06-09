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

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Manifest;

import junit.framework.TestCase;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.codehaus.pst.plugin.ManifestConstants;
import org.codehaus.pst.plugin.ManifestParser;

/**
 * <ul>
 * <li>Title: ManifestParserTest</li>
 * <li>Description: </li>
 * <li>Created: Aug 2, 2006 by: prisgupt01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.4 $
 */
public class ManifestParserTest extends TestCase {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/test/java/com/princetonsoftech/maven/psteclipse/ManifestParserTest.java,v 1.4 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * Tests the artifact id parsing.
     * @throws Exception
     */
    public void testArtifactId() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/axis-manifest.mf");
        Manifest manifest = new Manifest(inputStream);
        ManifestParser parser = new ManifestParser(manifest);
        parser.parse();
        String artifactId = parser.getArtifactId();
        assertNotNull("Manifest parser failed to parse artifact id", artifactId);
        assertEquals("Manifest parser failed to parse artifact id correctly", "org.apache.axis", artifactId);
    }

    /**
     * Tests the version parsing.
     * @throws Exception
     */
    public void testVersion() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream("/axis-manifest.mf");
        Manifest manifest = new Manifest(inputStream);
        ManifestParser parser = new ManifestParser(manifest);
        parser.parse();
        String version = parser.getVersion();
        assertNotNull("Manifest parser failed to parse version", version);
        assertEquals("Manifest parser failed to strip extra version information", "1.3.0", version);
    }

    /**
     * Test a complex plugin manifest
     * @throws Exception
     */
    public void testComplexRequiredBundles() throws Exception {
        DependencyManagement management = getDependenciesUsingParser("/axis-manifest.mf");
        List dependencies = management.getDependencies();
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
     * Test a simple manifest file
     * @throws Exception
     */
    public void testSimpleRequiredBundles() throws Exception {
        DependencyManagement management = getDependenciesUsingParser("/hibernate-manifest.mf");
        List dependencies = management.getDependencies();
        assertEquals(3, dependencies.size());
        Iterator iterator = dependencies.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            Dependency dependency = (Dependency) iterator.next();
            if (dependency.getArtifactId().equals("com.princetonsoftech.common.hibernate")) {
                assertNull(dependency.getVersion());
                count++;
            }
            assertEquals(1, count);
        }
    }

    /**
     * Tests the bundle class path.
     * @throws Exception
     */
    public void testBundleClassPath() throws Exception {
        DependencyManagement management = getDependenciesUsingParser("/axis-manifest.mf");
        List dependencies = management.getDependencies();
        assertEquals(11, dependencies.size());
        int count = 0;
        Iterator iterator = dependencies.iterator();
        while (iterator.hasNext()) {
            Dependency dependency = (Dependency) iterator.next();
            String scope = dependency.getScope();
            if (ManifestConstants.SCOPE_SYSTEM.equals(scope)) {
                count++;
            }
        }
        assertEquals(7, count);
    }
    
    /**
     * Uses the ManifestParser to parse out the dependencies
     * @param manifestPath the manifest path.
     * @return a DependencyManagement object.
     * @throws IOException
     */
    private DependencyManagement getDependenciesUsingParser(String manifestPath) throws IOException {
        InputStream inputStream = getClass().getResourceAsStream(manifestPath);
        Manifest manifest = new Manifest(inputStream);
        ManifestParser parser = new ManifestParser(manifest);
        parser.parse();
        DependencyManagement management = parser.getDependencyManagement();
        return management;
    }
}
