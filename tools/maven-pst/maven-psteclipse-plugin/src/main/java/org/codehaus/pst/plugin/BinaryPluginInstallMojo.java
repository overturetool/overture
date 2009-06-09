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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.installer.ArtifactInstallationException;
import org.apache.maven.artifact.installer.ArtifactInstaller;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * <ul>
 * <li>Title: BinaryPluginInstallMojo</li>
 * <li>Description: The class <code>BinaryPluginInstallMojo</code> is a Mojo
 * for installing binary plugins.</li>
 * <li>Created: Aug 30, 2006 by: prisgupt01</li>
 * </ul>
 * @author $Author: prippete01 $
 * @version $Revision: 1.4 $
 * @goal install
 * @requiresProject true
 */
public class BinaryPluginInstallMojo extends AbstractMojo implements DeployConstants {
    /**
     * The packaging.
     * @parameter expression="${project.packaging}"
     * @required
     * @readonly
     */
    protected String packaging;

    /**
     * The POM file.
     * @parameter expression="${project.file}"
     * @required
     * @readonly
     */
    private File pomFile;

    /**
     * Whether to update the metadata to make the artifact as release.
     * @parameter expression="${updateReleaseInfo}" default-value="false"
     */
    private boolean updateReleaseInfo;

    /**
     * The artifact.
     * @parameter expression="${project.artifact}"
     * @required
     * @readonly
     */
    private Artifact artifact;

    /**
     * The attached artifacts.
     * @parameter expression="${project.attachedArtifacts}
     * @required
     * @readonly
     */
    private List attachedArtifacts;

    /**
     * The artifact installer.
     * @parameter expression="${component.org.apache.maven.artifact.installer.ArtifactInstaller}"
     * @required
     * @readonly
     */
    protected ArtifactInstaller installer;

    /**
     * The local repository.
     * @parameter expression="${localRepository}"
     * @required
     * @readonly
     */
    protected ArtifactRepository localRepository;

    /**
     * Constructs a new <code>BinaryPluginInstallMojo</code> instance.
     */
    public BinaryPluginInstallMojo() {
        super();
    }

    /**
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
        if (updateReleaseInfo) {
            artifact.setRelease(true);
        }
        try {
            if (packaging.equals(PACKAGING_BINARY_PLUGIN)) {
                installer.install(pomFile, artifact, localRepository);
            }
            for (Iterator i = attachedArtifacts.iterator(); i.hasNext();) {
                Artifact attached = (Artifact) i.next();
                installer.install(attached.getFile(), attached, localRepository);
                // TODO : sugupta - get checksum property and install checksum
                // as well

                // if( createChecksum )
                // {
                // installCheckSum( attached.getFile(), attached, false );
                // }
            }
        } catch (ArtifactInstallationException e) {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}