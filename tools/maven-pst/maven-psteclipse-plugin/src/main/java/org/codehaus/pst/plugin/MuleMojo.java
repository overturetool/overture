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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * <ul>
 * <li>Title: MuleMojo</li>
 * <li>Description: The class <code>MuleMojo</code> is a "test mule" for
 * figuring out things about Maven that requires the execution of a Mojo within
 * the context of a POM.</li>
 * <li>Created: Aug 30, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.7 $
 * @goal mule
 * @requiresProject true
 * @requiresDependencyResolution compile
 */
public class MuleMojo extends AbstractMojo {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/MuleMojo.java,v 1.7 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * The project.
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * Set to true to force the mule to simply stop.
     * @parameter expression="${stop}"
     */
    private boolean stop;
    
    /**
     * Constructs a new <code>MuleMojo</code> instance.
     */
    public MuleMojo() {
        super();
    }

    /* 
     * (non-Javadoc)
     * @see org.apache.maven.plugin.Mojo#execute()
     */
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("The Mule is running...");
        dumpProperties();
        if (stop) {
            throw new MojoExecutionException("Mule stop!");
        }
        getLog().info("The Mule has ended.");
    }
    
    /**
     * Dumps the properties.
     */
    protected void dumpProperties() {
        getLog().info("Properties:");
        getLog().info("-----------");
        Properties properties = project.getProperties();
        if (properties != null) {
            properties.list(System.out);
        } else {
            getLog().info("null");
        }
    }
    
    /**
     * Dumps the modules.
     */
    protected void dumpModules() {
        getLog().info("Modules:");
        getLog().info("--------");
        Iterator iterator = project.getModules().iterator();
        while (iterator.hasNext()) {
            getLog().info("Module: " + iterator.next());
        }
    }
    
    /**
     * Dumps the dependency trails.
     */
    protected void dumpDependencyTrails() {
        getLog().info("Artifact dependency trails:");
        getLog().info("---------------------------");
        Iterator artifacts = project.getCompileArtifacts().iterator();
        while (artifacts.hasNext()) {
            Artifact artifact = (Artifact) artifacts.next();
            List trail = artifact.getDependencyTrail();
            String string = (String) trail.get(1);
            if (string.startsWith("com.princetonsoftech.apollo:com.princetonsoftech.common.misc")) {
                getLog().info("Misc artifact: " + artifact);
            }
        }
    }
    
    /**
     * Dumps an artifact handler.
     * @param artifactHandler the artifact handler.
     */
    protected void dumpArtifactHandler(ArtifactHandler artifactHandler) {
        getLog().info("Artifact handler:");
        getLog().info("-----------------");
        getLog().info("Packaging...: " + artifactHandler.getPackaging());
        getLog().info("Extension...: " + artifactHandler.getExtension());
        getLog().info("Language....: " + artifactHandler.getLanguage());
    }
    
    /**
     * Dumps the artifacts.
     */
    protected void dumpArtifacts() {
        getLog().info("Artifacts:");
        getLog().info("----------");
        dumpArtifacts(project.getArtifacts());
    }
    
    /**
     * Dumps the test artifacts.
     */
    protected void dumpTestArtifacts() {
        getLog().info("Test artifacts:");
        getLog().info("---------------");
        dumpArtifacts(project.getTestArtifacts());
    }

    /**
     * Dumps the plugin artifacts.
     */
    protected void dumpPluginArtifacts() {
        getLog().info("Plugin artifacts:");
        getLog().info("-----------------");
        dumpArtifacts(project.getPluginArtifacts());
    }
    
    /**
     * Dumps the plugin context.
     */
    protected void dumpPluginContext() {
        getLog().info("Plugin context:");
        getLog().info("---------------");
        Map map = getPluginContext();
        Iterator keys = map.keySet().iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            Object value = map.get(key);
            getLog().info("Key '" + key + "' = '" + value + "',");
        }
    }
    
    /**
     * Dumps the specified artifacts.
     * @param artifacts the artifacts.
     */
    protected void dumpArtifacts(Collection artifacts) {
        Iterator iterator = artifacts.iterator();
        while (iterator.hasNext()) {
            Artifact artifact = (Artifact) iterator.next();
            getLog().info("Artifact: " + artifact);
        }
    }
}
