package org.overture.tools.maven.examplepackager;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import org.overture.ast.lex.Dialect;
import org.overture.tools.examplepackager.Controller;

/**
 * Example Packager
 * 
 */
@Mojo(name="package-examples",
      defaultPhase=LifecyclePhase.PROCESS_RESOURCES)
public class ExamplePackagerMojo extends AbstractMojo {
    /**
     * A list of directories containing subdirectories with example
     * VDM-SL projects.  Note that the name of the output bundle will
     * be derived from the name of the base directory.
     *
     */
    @Parameter(alias="slExamples")
    protected List<File> exampleSLBaseDirectories;
 
    /**
     * A list of directories containing subdirectories with example
     * VDM-PP projects.  Note that the name of the output bundle will
     * be derived from the name of the base directory.
     *
     */
    @Parameter(alias="ppExamples")
    protected List<File> examplePPBaseDirectories;
 
    /**
     * A list of directories containing subdirectories with example
     * VDM-RT projects.  Note that the name of the output bundle will
     * be derived from the name of the base directory.
     *
     */
    @Parameter(alias="rtExamples")
    protected List<File> exampleRTBaseDirectories;
 
    /**
     * A prefix to the output zip filename.
     *
     */
    @Parameter(defaultValue="Examples-")
    protected String outputPrefix;
    
    /**
     * Name of the directory into which the packaged examples will be
     * placed.  Readonly at the moment as the only place they should
     * be dropped is in the project's usual target directory.
     */
    @Parameter(defaultValue="${project.build.directory}", readonly=true)
    protected File outputDirectory;

    /**
     * String giving the relative path of the staging directory for
     * the packager.
     */
    private static final String tmpdirBaseString = "generated/example-packager";

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File tmpdir = new File(outputDirectory, tmpdirBaseString);
        File zipFile;
        Controller controller;
		
    	for (File exampleDir : exampleSLBaseDirectories) {
            zipFile = new File(outputDirectory, outputPrefix + exampleDir.getName() + ".zip");
            controller = new Controller(Dialect.VDM_SL, exampleDir, outputDirectory, false);
            controller.packExamples(new File(tmpdir, exampleDir.getName()), zipFile, false);
    	}

    	for (File exampleDir : examplePPBaseDirectories) {
            zipFile = new File(outputDirectory, outputPrefix + exampleDir.getName() + ".zip");
            controller = new Controller(Dialect.VDM_PP, exampleDir, outputDirectory, false);
            controller.packExamples(new File(tmpdir, exampleDir.getName()), zipFile, false);
    	}

    	for (File exampleDir : exampleRTBaseDirectories) {
            zipFile = new File(outputDirectory, outputPrefix + exampleDir.getName() + ".zip");
            controller = new Controller(Dialect.VDM_RT, exampleDir, outputDirectory, false);
            controller.packExamples(new File(tmpdir, exampleDir.getName()), zipFile, false);
    	}
    }
}
