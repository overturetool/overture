package org.overture.tools.maven.examplepackager;

import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;


/**
 * Example Packager
 * 
 */
@Mojo(name="package-examples",
      defaultPhase=LifecyclePhase.PROCESS_RESOURCES)
public class ExamplePackagerMojo extends AbstractMojo {
    /**
     * A list of directories containing subdirectories with example
     * VDM projects.  Note that the name of the output bundle will be
     * derived from the name of the base directory.
     *
     * 
     */
    @Parameter(required=true)
    protected List<File> exampleBaseDirectories;
 
    /**
     * Name of the directory into which the packaged examples will be placed.
     */
    @Parameter(defaultValue="${project.build.directory}")
    protected File outputDirectory;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("narf");
        getLog().info(exampleBaseDirectories!=null?exampleBaseDirectories.toString():"No base dirs");
        getLog().info(outputDirectory.toString());
        getLog().info("zot");
    }
	

}
