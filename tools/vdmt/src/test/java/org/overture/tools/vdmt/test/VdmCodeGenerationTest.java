package org.overture.tools.vdmt.test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.apache.maven.plugin.logging.Log;
import org.overture.tools.vdmt.LogTesting;
import org.overture.tools.vdmt.VdmCodeGeneration;

public class VdmCodeGenerationTest extends VdmCodeGeneration
{
	public void testSimpleIO() throws IOException
	{
		Log log = (Log) new LogTesting();
		List<File> dependedArtifactsSourceLocation = new Vector<File>();

		File baseDir = createTestProject("test1");
		assertNotNull("Cannot create test project", baseDir);

		System.out.println("Test project location: "
				+ baseDir.getAbsolutePath());

		List<String> excludePackages = new Vector<String>();
		List<String> excludeClasses = new Vector<String>();
		List<String> importPackages = new Vector<String>();
		importPackages.add("jp.co.csk.vdm.toolbox.VDM.quotes");

		runGeneration(
				log,
				vppde,
				baseDir,
				dependedArtifactsSourceLocation,
				excludePackages,
				excludeClasses,
				importPackages);
	}

	public void testStdLib() throws IOException
	{
		Log log = (Log) new LogTesting();
		List<File> dependedArtifactsSourceLocation = new Vector<File>();

		File baseDir = createTestProject("stdlib");
		assertNotNull("Cannot create test project", baseDir);

		System.out.println("Test project location: "
				+ baseDir.getAbsolutePath());

		List<String> excludePackages = new Vector<String>();
		List<String> excludeClasses = new Vector<String>();
		List<String> importPackages = new Vector<String>();
		importPackages.add("jp.co.csk.vdm.toolbox.VDM.quotes");

		runGeneration(
				log,
				vppde,
				baseDir,
				dependedArtifactsSourceLocation,
				excludePackages,
				excludeClasses,
				importPackages);
	}

	public void testUtil() throws IOException
	{
		Log log = (Log) new LogTesting();
		List<File> dependedArtifactsSourceLocation = new Vector<File>();

		File baseDir = createTestProject("util");
		assertNotNull("Cannot create test project", baseDir);

		System.out.println("Test project location: "
				+ baseDir.getAbsolutePath());

		List<String> excludePackages = new Vector<String>();
		List<String> excludeClasses = new Vector<String>();
		List<String> importPackages = new Vector<String>();
		importPackages.add("jp.co.csk.vdm.toolbox.VDM.quotes");

		runGeneration(
				log,
				vppde,
				baseDir,
				dependedArtifactsSourceLocation,
				excludePackages,
				excludeClasses,
				importPackages);
	}
	
	public void testWithbak() throws IOException
	{
		Log log = (Log) new LogTesting();
		List<File> dependedArtifactsSourceLocation = new Vector<File>();

		File baseDir = createTestProject("testwithbak");
		assertNotNull("Cannot create test project", baseDir);

		System.out.println("Test project location: "
				+ baseDir.getAbsolutePath());

		List<String> excludePackages = new Vector<String>();
		List<String> excludeClasses = new Vector<String>();
		List<String> importPackages = new Vector<String>();
		importPackages.add("jp.co.csk.vdm.toolbox.VDM.quotes");

		runGeneration(
				log,
				vppde,
				baseDir,
				dependedArtifactsSourceLocation,
				excludePackages,
				excludeClasses,
				importPackages);
	}
	
	public void testInheritance() throws IOException
	{
		Log log = (Log) new LogTesting();
		List<File> dependedArtifactsSourceLocation = new Vector<File>();

		File baseDir = createTestProject("inheritance");
		assertNotNull("Cannot create test project", baseDir);

		System.out.println("Test project location: "
				+ baseDir.getAbsolutePath());

		List<String> excludePackages = new Vector<String>();
		List<String> excludeClasses = new Vector<String>();
		List<String> importPackages = new Vector<String>();
		importPackages.add("jp.co.csk.vdm.toolbox.VDM.quotes");

		runGeneration(
				log,
				vppde,
				baseDir,
				dependedArtifactsSourceLocation,
				excludePackages,
				excludeClasses,
				importPackages);
	}
}
