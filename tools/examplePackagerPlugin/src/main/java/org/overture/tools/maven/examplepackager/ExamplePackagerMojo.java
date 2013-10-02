package org.overture.tools.maven.examplepackager;

import java.io.File;
import java.util.List;
import java.util.Vector;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.overture.tools.examplepackager.Controller;
import org.overture.tools.examplepackager.Dialect;

/**
 * Example Packager
 * 
 */
@Mojo(name="package-examples",
      defaultPhase=LifecyclePhase.PROCESS_RESOURCES)
public class ExamplePackagerMojo extends AbstractMojo {
	
	  /**
     * A boolean indicating whether example zips should be generated
     *
     */
    @Parameter(alias="output-zip")
    protected boolean outputZipFiles = true;
    

	  /**
   * A boolean indicating whether web pages should be generated
   *
   */
  @Parameter(alias="output-web")
  protected boolean outputWebFiles = false;
    
	
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
    @Parameter(defaultValue="${project.build.directory}")
    protected File outputDirectory;

    /**
     * Location of the staging directory for the example packager.
     */
    @Parameter(defaultValue="${project.build.directory}/generated-resources/example-packager", readonly=true)
    protected File tmpdir;


	private boolean overtureCSSWeb= true;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File zipFile;
        Controller controller;
        
        List<Controller> controllers = new Vector<Controller>();
        List<File> zipFiles = new Vector<File>();
		
    	for (File exampleDir : exampleSLBaseDirectories) {
            zipFile = new File(outputDirectory, outputPrefix + exampleDir.getName() + ".zip");
            zipFiles.add(zipFile);
            controller = new Controller(Dialect.VDM_SL, exampleDir, outputDirectory, false);
            controllers.add(controller);
            controller.packExamples(new File(tmpdir, exampleDir.getName()), zipFile, !outputZipFiles);
            
            if(outputWebFiles)
            {
            	controller.createWebSite(overtureCSSWeb);
            }
    	}

    	for (File exampleDir : examplePPBaseDirectories) {
            zipFile = new File(outputDirectory, outputPrefix + exampleDir.getName() + ".zip");
            zipFiles.add(zipFile);
            controller = new Controller(Dialect.VDM_PP, exampleDir, outputDirectory, false);
            controllers.add(controller);
            controller.packExamples(new File(tmpdir, exampleDir.getName()), zipFile, !outputZipFiles);
            
            if(outputWebFiles)
            {
            	controller.createWebSite(overtureCSSWeb);
            }
    	}

    	for (File exampleDir : exampleRTBaseDirectories) {
            zipFile = new File(outputDirectory, outputPrefix + exampleDir.getName() + ".zip");
            zipFiles.add(zipFile);
            controller = new Controller(Dialect.VDM_RT, exampleDir, outputDirectory, false);
            controllers.add(controller);
            controller.packExamples(new File(tmpdir, exampleDir.getName()), zipFile, !outputZipFiles);
            
            if(outputWebFiles)
            {
            	controller.createWebSite(overtureCSSWeb);
            }
    	}
    	
    	
    	if(outputWebFiles && !controllers.isEmpty())
    	{
    		controllers.iterator().next().createWebOverviewPage(controllers, zipFiles,overtureCSSWeb);
    	}
    }
}
