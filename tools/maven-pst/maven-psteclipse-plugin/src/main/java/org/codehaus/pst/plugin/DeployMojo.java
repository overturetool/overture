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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * <ul>
 * <li>Title: DeployMojo</li>
 * <li>Description: The class <code>DeployMojo</code> is a Maven Mojo that
 * "scrapes" an Eclipse home directory and creates a package and POM for each
 * plugin for deployment to a Maven repository.</li>
 * <li>Created: Aug 1, 2006 by: prippete01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.10 $
 * @goal deploy
 * @phase deploy
 * @requiresProject false
 */
public class DeployMojo extends AbstractEclipseMojo implements DeployConstants, ManifestConstants {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/DeployMojo.java,v 1.10 2007/02/08 22:02:30 prippete01 Exp $";

    /**
     * The poms.
     */
    private List poms = new ArrayList();

    /**
     * The artifacts.
     */
    private HashMap artifacts = new HashMap();

    /**
     * The prefixes.
     */
    private List prefixes = new ArrayList();

    /**
     * The eclipse home.
     * @parameter expression="${eclipseHome}"
     * @required
     */
    private File eclipseHome;

    /**
     * The repository home.
     * @parameter expression="${repositoryHome}"
     */
    private File repositoryHome;

    /**
     * The group id.
     * @parameter expression="${groupId}"
     * @required
     */
    private String groupId;

    /**
     * The prefix.
     * @parameter expression="${prefix}"
     */
    private String prefix;

    /**
     * The repository id.
     * @parameter expression="${repositoryId}"
     * @required
     */
    private String repositoryId;

    /**
     * The URL.
     * @parameter expression="${url}"
     * @required
     */
    private String url;

    /**
     * Constructs a new <code>DeployMojo</code> instance.
     */
    public DeployMojo() {
        super();
    }

    /* 
     * (non-Javadoc)
     * @see org.codehaus.pst.plugin.AbstractEclipseMojo#doExecute()
     */
    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        if (!eclipseHome.isDirectory()) {
            throw new MojoExecutionException("The 'eclipseHome' location '" + eclipseHome + "' is not a valid directory");
        }
        if (repositoryHome == null) {
            repositoryHome = new File(System.getProperty(KEY_USER_DIR));
            getLog().info("Defaulting 'repositoryHome' to '" + repositoryHome + "'");
        }
        if (!repositoryHome.isDirectory()) {
            throw new MojoExecutionException("The 'repositoryHome' location '" + repositoryHome + "' is not a valid directory");
        }
        if (prefix == null) {
            getLog().info("Defaulting 'prefix' to '<none>'");
        }
        if (prefix != null) {
            String[] temp = prefix.split(",");
            for (int i = 0; i < temp.length; i++) {
                String prefix = temp[i];
                if (!prefix.endsWith(".")) {
                    prefix = prefix + ".";
                }
                prefixes.add(prefix);
            }
        }
        try {
            populateRepository();
        } catch (IOException e) {
            throw new MojoExecutionException("Repository population failed", e);
        }
        try {
            writePOMs();
        } catch (IOException e) {
            throw new MojoExecutionException("POM writing failed", e);
        }
        try {
            createPOMDeploymentScript();
        } catch (IOException e) {
            throw new MojoExecutionException("POM deployment script creation failed", e);
        }
    }

    /**
     * Populates the repository.
     * @throws IOException if an I/O error occurs.
     */
    private void populateRepository() throws IOException {
        File pluginHome = new File(eclipseHome, DIR_PLUGINS);
        if (!pluginHome.isDirectory()) {
            throw new FileNotFoundException("The eclipse 'plugins' directory was not found");
        }
        getLog().info("Processing plugins in '" + pluginHome + "'");
        File[] plugins = pluginHome.listFiles();
        for (int i = 0; i < plugins.length; i++) {
            String name = plugins[i].getName();
            boolean include;
            if (prefixes.size() > 0) {
                include = false;
                for (int j = 0; j < prefixes.size(); j++) {
                    String prefix = (String) prefixes.get(j);
                    if (name.startsWith(prefix)) {
                        include = true;
                        break;
                    }
                }
            } else {
                include = true;
            }
            if (!include) {
                getLog().debug("Skipping plug-in '" + name + "'");
                continue;
            }
            getLog().info("Processing plug-in '" + name + "'");
            if (plugins[i].isDirectory()) {
                handlePluginDirectory(plugins[i]);
            } else {
                handlePluginFile(plugins[i]);
            }
        }
    }

    /**
     * Copies the plugin's jars and creates a POM for the plugin itself.
     * @param pluginDirectory the plugin's directory.
     * @throws IOException if an I/O error occurs.
     */
    private void handlePluginDirectory(File pluginDirectory) throws IOException {
        File manifestFile = new File(pluginDirectory, "META-INF/MANIFEST.MF");
        if (!manifestFile.isFile()) {
            getLog().error("The directory plugin '" + pluginDirectory + "' does not have a manifest");
            return;
        }
        Manifest manifest = new Manifest(new FileInputStream(manifestFile));
        ManifestParser parser = new ManifestParser(manifest);
        parser.setGroupId(groupId);
        parser.parse();
        if (!parser.hasArtifactId()) {
            getLog().error("The plugin '" + pluginDirectory + "' does not have a bundle symbolic name");
            return;
        }
        if (!parser.hasVersion()) {
            getLog().error("The plugin '" + pluginDirectory + "' does not have a version");
            return;
        }
        Pom pom = buildPOM(parser, PACKAGING_POM);
        Iterator iterator = parser.getDependencyManagement().getDependencies().iterator();
        while (iterator.hasNext()) {
            Dependency dependency = (Dependency) iterator.next();
            String scope = dependency.getScope();
            if (SCOPE_SYSTEM.equals(scope)) {
                File sourceFile = new File(pluginDirectory, dependency.getSystemPath());
                if (!sourceFile.isFile()) {
                    getLog().warn("The plugin '" + pluginDirectory + "' declares a classpath entry '" + dependency.getSystemPath() + "' which does not exist");
                    continue;
                }
                if (dependency.getVersion() == null) {
                    dependency.setVersion(parser.getVersion());
                }
                File destinationFile = new File(repositoryHome, dependency.getArtifactId() + "-" + dependency.getVersion() + EXTENSION_JAR);
                copyFile(sourceFile, destinationFile);
            }
            pom.getDependencyManagement().addDependency(dependency);
        }
    }

    /**
     * Copies the specified jar-based plugin to the repository directory.
     * @param pluginFile the plugin's file.
     * @throws IOException if an I/O error occurs.
     */
    private void handlePluginFile(File pluginFile) throws IOException {
        JarFile jarFile = new JarFile(pluginFile);
        Manifest manifest = jarFile.getManifest();
        ManifestParser parser = new ManifestParser(manifest);
        parser.setGroupId(groupId);
        parser.parse();
        if (!parser.hasArtifactId()) {
            getLog().error("The plugin '" + pluginFile + "' does not have a bundle symbolic name");
            return;
        }
        if (!parser.hasVersion()) {
            getLog().error("The plugin '" + pluginFile + "' does not have a version");
            return;
        }
        Pom pom = buildPOM(parser, PACKAGING_JAR);
        Iterator iterator = parser.getDependencyManagement().getDependencies().iterator();
        while (iterator.hasNext()) {
            Dependency dependency = (Dependency) iterator.next();
            String scope = dependency.getScope();
            if (SCOPE_SYSTEM.equals(scope)) {
                getLog().warn("The plugin '" + pluginFile + "' declares a classpath entry '" + dependency.getSystemPath() + "' which cannot be resolved (it's a jar plugin)");
                continue;

            }
            pom.getDependencyManagement().addDependency(dependency);
        }
        String artifactId = parser.getArtifactId();
        File destinationFile = new File(repositoryHome, artifactId + "-" + parser.getVersion() + EXTENSION_JAR);
        copyFile(pluginFile, destinationFile);
        pom.setFile(destinationFile);
    }

    /**
     * Builds and returns a new POM instance for the specified manifest parser
     * and packaging.
     * @param parser the manifest parser.
     * @param packaging the packaging.
     * @return the POM.
     */
    private Pom buildPOM(ManifestParser parser, String packaging) {
        Pom pom = new Pom();
        pom.setParser(parser);
        String artifactId = parser.getArtifactId();
        String version = parser.getVersion();
        File file = new File(repositoryHome, artifactId + "-" + version + EXTENSION_POM);
        pom.setPomFile(file);
        String name = file.getName();
        int index = name.lastIndexOf('.');
        name = name.substring(0, index) + EXTENSION_POM;
        pom.setPackaging(packaging);
        pom.setGroupId(groupId);
        pom.setArtifactId(artifactId);
        pom.setVersion(version);
        poms.add(pom);
        artifacts.put(pom.getArtifactId(), pom);
        return pom;
    }

    /**
     * Writes the POMs for the plug-ins.
     * @throws IOException if an I/O error occurs.
     */
    private void writePOMs() throws IOException {
        Iterator iterator = poms.iterator();
        while (iterator.hasNext()) {
            Pom pom = (Pom) iterator.next();
            writePOM(pom);
        }
    }

    /**
     * Writes an XML POM from the specified POM instance.
     * @param pom the POM.
     * @throws IOException if an I/O error occurs.
     */
    private void writePOM(Pom pom) throws IOException {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        buffer.append("<project>\n");
        buffer.append("   <modelVersion>4.0.0</modelVersion>\n");
        buffer.append("   <groupId>");
        buffer.append(pom.getGroupId());
        buffer.append("</groupId>\n");
        buffer.append("   <artifactId>");
        buffer.append(pom.getArtifactId());
        buffer.append("</artifactId>\n");
        buffer.append("   <version>");
        buffer.append(pom.getVersion());
        buffer.append("</version>\n");
        buffer.append("   <packaging>");
        buffer.append(pom.getPackaging());
        buffer.append("</packaging>\n");
        buffer.append("   <distributionManagement>\n");
        buffer.append("      <repository>\n");
        buffer.append("         <id>");
        buffer.append(repositoryId);
        buffer.append("</id>\n");
        buffer.append("         <name>PST Apollo Development Repository</name>\n");
        buffer.append("         <url>");
        buffer.append(url);
        buffer.append("</url>\n");
        buffer.append("      </repository>\n");
        buffer.append("   </distributionManagement>\n");
        List dependencies = pom.getDependencyManagement().getDependencies();
        if (!dependencies.isEmpty()) {
            buffer.append("   <dependencies>\n");
            Iterator iterator = dependencies.iterator();
            while (iterator.hasNext()) {
                Dependency dependency = (Dependency) iterator.next();
                String groupId = dependency.getGroupId();
                String artifactId = dependency.getArtifactId();
                String version = dependency.getVersion();
                String scope = dependency.getScope();
                String type = dependency.getType();
                if (!SCOPE_SYSTEM.equals(scope)) {
                    Pom dependentPOM = (Pom) artifacts.get(artifactId);
                    if (dependentPOM == null) {
                        getLog().error("The plug-in '" + pom.getArtifactId() + "' requires '" + artifactId + "' which was not found");
                        continue;
                    }
                    if (version == null) {
                        version = dependentPOM.getVersion();
                    }
                    if (!version.equals(dependentPOM.getVersion())) {
                        version = dependentPOM.getVersion();
                    }
                    type = dependentPOM.getPackaging();
                }
                buffer.append("      <dependency>\n");
                buffer.append("         <groupId>");
                buffer.append(groupId);
                buffer.append("</groupId>\n");
                buffer.append("         <artifactId>");
                buffer.append(artifactId);
                buffer.append("</artifactId>\n");
                buffer.append("         <version>");
                buffer.append(version);
                buffer.append("</version>\n");
                if (type != null) {
                    buffer.append("         <type>");
                    buffer.append(type);
                    buffer.append("</type>\n");
                }
                buffer.append("      </dependency>\n");
            }
            buffer.append("   </dependencies>\n");
        }
        buffer.append("</project>\n");
        File file = pom.getPomFile();
        FileWriter writer = new FileWriter(file);
        writer.write(buffer.toString());
        writer.close();
    }

    /**
     * Deploys the POMs for the plug-ins.
     * @throws IOException if an I/O error occurs.
     */
    private void createPOMDeploymentScript() throws IOException {
        File deployScriptFile = new File(repositoryHome, "deploy.txt");
        File installScriptFile = new File(repositoryHome, "install.txt");
        BufferedWriter deployScriptWriter = new BufferedWriter(new FileWriter(deployScriptFile));
        BufferedWriter installScriptWriter = new BufferedWriter(new FileWriter(installScriptFile));
        Iterator iterator = poms.iterator();
        while (iterator.hasNext()) {
            Pom pom = (Pom) iterator.next();
            writePOMCommands(pom, deployScriptWriter, installScriptWriter);
        }
        deployScriptWriter.close();
        installScriptWriter.close();
    }

    /**
     * @param pom
     * @param deployScriptWriter
     * @param installScriptWriter
     * @throws IOException
     */
    private void writePOMCommands(Pom pom, Writer deployScriptWriter, Writer installScriptWriter) throws IOException {
        StringBuffer buffer = new StringBuffer();
        Iterator iterator = pom.getDependencyManagement().getDependencies().iterator();
        while (iterator.hasNext()) {
            Dependency dependency = (Dependency) iterator.next();
            String scope = dependency.getScope();
            if (SCOPE_SYSTEM.equals(scope)) {
                String groupId = dependency.getGroupId();
                String artifactId = dependency.getArtifactId();
                String version = dependency.getVersion();
                File file = new File(repositoryHome, artifactId + "-" + version + EXTENSION_JAR);
                // deploy-file command line for dependent jar:
                buffer.setLength(0);
                buffer.append("mvn deploy:deploy-file -Dfile=");
                buffer.append(file.getAbsolutePath());
                buffer.append(" -DgroupId=");
                buffer.append(groupId);
                buffer.append(" -DartifactId=");
                buffer.append(artifactId);
                buffer.append(" -Dversion=");
                buffer.append(version);
                buffer.append(" -Dpackaging=jar");
                buffer.append(" -DrepositoryId=");
                buffer.append(repositoryId);
                buffer.append(" -Durl=");
                buffer.append(url);
                buffer.append("\n");
                deployScriptWriter.write(buffer.toString());
                // install-file command line for dependent jar:
                buffer.setLength(0);
                buffer.append("mvn install:install-file -Dfile=");
                buffer.append(file.getAbsolutePath());
                buffer.append(" -DgroupId=");
                buffer.append(groupId);
                buffer.append(" -DartifactId=");
                buffer.append(artifactId);
                buffer.append(" -Dversion=");
                buffer.append(version);
                buffer.append(" -Dpackaging=jar");
                buffer.append("\n");
                installScriptWriter.write(buffer.toString());
            }
        }
        if (PACKAGING_JAR.equals(pom.getPackaging())) {
            // deploy-file command line for plug-in jar:
            buffer.setLength(0);
            buffer.append("mvn deploy:deploy-file -Dfile=");
            buffer.append(pom.getFile().getAbsolutePath());
            buffer.append(" -DgroupId=");
            buffer.append(pom.getGroupId());
            buffer.append(" -DartifactId=");
            buffer.append(pom.getArtifactId());
            buffer.append(" -Dversion=");
            buffer.append(pom.getVersion());
            buffer.append(" -Dpackaging=jar -DpomFile=");
            buffer.append(pom.getPomFile().getAbsolutePath());
            buffer.append(" -DrepositoryId=");
            buffer.append(repositoryId);
            buffer.append(" -Durl=");
            buffer.append(url);
            buffer.append("\n");
            deployScriptWriter.write(buffer.toString());
            // install-file command line for plug-in jar:
            buffer.setLength(0);
            buffer.append("mvn install:install-file -Dfile=");
            buffer.append(pom.getFile().getAbsolutePath());
            buffer.append(" -DgroupId=");
            buffer.append(pom.getGroupId());
            buffer.append(" -DartifactId=");
            buffer.append(pom.getArtifactId());
            buffer.append(" -Dversion=");
            buffer.append(pom.getVersion());
            buffer.append(" -Dpackaging=jar -DpomFile=");
            buffer.append(pom.getPomFile().getAbsolutePath());
            buffer.append("\n");
            installScriptWriter.write(buffer.toString());
        } else {
            // deploy command-line for POM
            buffer.setLength(0);
            buffer.append("mvn -f ");
            buffer.append(pom.getPomFile().getAbsolutePath());
            buffer.append(" deploy:deploy");
            buffer.append("\n");
            deployScriptWriter.write(buffer.toString());
            // install command-line for POM
            buffer.setLength(0);
            buffer.append("mvn -f ");
            buffer.append(pom.getPomFile().getAbsolutePath());
            buffer.append(" install:install");
            buffer.append("\n");
            installScriptWriter.write(buffer.toString());
        }
    }
}
