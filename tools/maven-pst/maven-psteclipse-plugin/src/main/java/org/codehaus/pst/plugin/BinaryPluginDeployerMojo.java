package org.codehaus.pst.plugin;

import java.io.File;
import java.util.ArrayList;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.profiles.DefaultProfileManager;
import org.apache.maven.profiles.ProfileManager;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.util.xml.Xpp3Dom;

/**
 * <ul>
 * <li>Title: </li>
 * <li>Description: </li>
 * <li>Created: Feb 12, 2007 by: prisgupt01</li>
 * </ul>
 * @author $Author: prisgupt01 $
 * @version $Revision: 1.2 $
 * @goal deploy-binary-plugin
 */
public class BinaryPluginDeployerMojo extends AbstractEclipseMojo {
    /**
     * Legal copyright notice.
     */
    public static final String COPYRIGHT = "Copyright (c) 2006, Princeton Softech Inc. All rights reserved.";

    /**
     * SCCS header.
     */
    public static final String HEADER = "$Header: /users1/cvsroot/maven-pst/maven-psteclipse-plugin/src/main/java/com/princetonsoftech/maven/psteclipse/BinaryPluginDeployerMojo.java,v 1.2 2007/02/14 05:41:41 prisgupt01 Exp $";

    /**
     * The target directory
     */
    public static final String TARGET_DIR = "target";

    /**
     * The work directory for assemby
     */
    public static final String WORK_DIR = "eclipse";

    /**
     * The plugins directory
     */
    public static final String PLUGINS_DIR = "plugins";

    /**
     * @parameter expression="${basedir}"
     * @required
     */
    private File baseDirectory;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject mavenProject;

    /**
     * @parameter expression="${session}"
     * @required
     * @readonly
     */
    private MavenSession session;

    /**
     * @parameter expression="${pluginOutputDir}"
     * @optional
     */
    private File outputDirectory;

    /**
     * @parameter expression="${dependencies}"
     */
    private Dependency[] dependencies;

    /**
     * @component
     */
    private ArtifactResolver artifactResolver;

    /**
     * @component
     */
    private ArtifactFactory artifactFactory;

    /**
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    private ArtifactRepository artifactRepository;

    /**
     * Constructs a new <code>BinaryPluginDeployerMojo</code> instance.
     */
    public BinaryPluginDeployerMojo() {
        super();
    }

    protected void doExecute() throws MojoExecutionException, MojoFailureException {
        if (outputDirectory == null) {
            outputDirectory = new File(baseDirectory, TARGET_DIR + File.separator + WORK_DIR + File.separator + PLUGINS_DIR);
        }
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        for (int i = 0; i < dependencies.length; i++) {
            Dependency dependency = dependencies[i];
            Artifact artifact = artifactFactory.createBuildArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), "pom");
            try {
                artifactResolver.resolve(artifact, mavenProject.getRemoteArtifactRepositories(), artifactRepository);
                deployPom(artifact);
            } catch (Exception e) {
                throw new MojoExecutionException("Error resolving or expanding artifact : " + artifact.getArtifactId(), e);
            }
        }

    }

    /**
     * Deploys the specified pom-based artifact to the Eclipse plugins
     * directory.
     * @param artifact the artifact.
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    private void deployPom(Artifact artifact) throws MojoExecutionException, MojoFailureException {
        MavenProject project;
        try {
            ProfileManager profileManager = new DefaultProfileManager(session.getContainer());
            MavenProjectBuilder builder = (MavenProjectBuilder) session.lookup(MavenProjectBuilder.class.getName());
            project = builder.buildWithDependencies(artifact.getFile(), session.getLocalRepository(), profileManager);
        } catch (ComponentLookupException e) {
            throw new MojoExecutionException("Unable to lookup project builder", e);
        } catch (ProjectBuildingException e) {
            throw new MojoExecutionException("Unable to build dependent project", e);
        } catch (Exception e) {
            throw new MojoExecutionException("Unable to resolve", e);
        }
        ArrayList buddies = new ArrayList();
        Plugin plugin = (Plugin) project.getBuild().getPluginsAsMap().get("org.overturetool.tools.maven-pst:maven-psteclipse-plugin");
        if (plugin != null) {
            Xpp3Dom configuration = (Xpp3Dom) plugin.getConfiguration();
            if (configuration != null) {
                Xpp3Dom buddiesDom = configuration.getChild("buddies");
                if (buddiesDom != null) {
                    Xpp3Dom[] buddyDoms = buddiesDom.getChildren("buddy");
                    if (buddyDoms != null) {
                        for (int i = 0; i < buddyDoms.length; i++) {
                            buddies.add(buddyDoms[i].getValue());
                        }
                    }
                }
            }
        }
        String artifactName = artifact.getArtifactId() + "-" + artifact.getVersion();
        getLog().info("Expanding binary plugin '" + artifactName + "'...");
        File pluginDirectory = new File(outputDirectory, artifactName);
        if (!pluginDirectory.exists()) {
            pluginDirectory.mkdir();
        }
        ManifestGenerator generator = new ManifestGenerator(getLog(), baseDirectory, project, buddies, pluginDirectory,doNotExportPackagePrefixes,importInsteadOfExportPackagePrefixes);
        generator.execute();
    }

}
