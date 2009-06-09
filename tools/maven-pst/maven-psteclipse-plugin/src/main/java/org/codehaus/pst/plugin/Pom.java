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

import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;

/**
 * <ul>
 * <li>Title: POM</li>
 * <li>Description: The class <code>POM</code> encapsulates the information
 * needed to create a 3rd party artifact deployment POM for an Eclipse plug-in
 * required bundle.</li>
 * <li>Created: Aug 1, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.2 $
 */
public class Pom {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved."; //$NON-NLS-1$

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/Pom.java,v 1.2 2007/02/08 22:02:30 prippete01 Exp $"; //$NON-NLS-1$

    /**
     * The manifest parser.
     */
    private ManifestParser parser;
    
    /**
     * The POM file.
     */
    private File pomFile;

    /**
     * The file
     */
    private File file;
    
    /**
     * The packaging.
     */
    private String packaging;

    /**
     * The group id.
     */
    private String groupId;
    
    /**
     * The artifact id.
     */
    private String artifactId;

    /**
     * The version.
     */
    private String version;

    /**
     * The dependency management.
     */
    private DependencyManagement management = new DependencyManagement();

    /**
     * Constructs a new <code>POMInformation</code> instance.
     */
    public Pom() {
        super();
    }

    /**
     * Returns the parser.
     * @return the parser.
     */
    public ManifestParser getParser() {
        return parser;
    }

    /**
     * Sets the parser.
     * @param parser the parser.
     */
    public void setParser(ManifestParser parser) {
        this.parser = parser;
    }

    /**
     * Returns the POM file.
     * @return the POM file.
     */
    public File getPomFile() {
        return pomFile;
    }

    /**
     * Sets the POM file.
     * @param pomFile the file.
     */
    public void setPomFile(File pomFile) {
        this.pomFile = pomFile;
    }

    /**
     * Returns the file.
     * @return the file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Sets the file.
     * @param file the file.
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * Returns the packaging.
     * @return the packaging.
     */
    public String getPackaging() {
        return packaging;
    }

    /**
     * Sets the packaging.
     * @param packaging the packaging.
     */
    public void setPackaging(String packaging) {
        this.packaging = packaging;
    }

    /**
     * Returns the group id.
     * @return the group id.
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets the group id.
     * @param groupId the group id.
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Returns the artifact id.
     * @return the artifact id.
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Sets the artifact id.
     * @param artifactId the artifact id.
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * Returns the version.
     * @return the version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     * @param version the version.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Adds a dependency to this POM.
     * @param dependency the dependency.
     */
    public void addDependency(Dependency dependency) {
        management.addDependency(dependency);
    }

    /**
     * Returns the dependency management.
     * @return the dependency management.
     */
    public DependencyManagement getDependencyManagement() {
        return management;
    }
}
