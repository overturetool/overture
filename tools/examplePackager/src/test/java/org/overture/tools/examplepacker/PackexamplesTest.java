package org.overture.tools.examplepacker;

import java.io.File;

import junit.framework.TestCase;

import org.overture.tools.examplepackager.Main;

public class PackexamplesTest extends TestCase
{
	File input = new File(FilePathUtil.getPlatformPath("src/test/resources/examples"));
	File outputFolder = new File(FilePathUtil.getPlatformPath("target/testData/"));
	
	public void testPackexamples() throws Exception
	{
		Main.main(new String[] { "-i", input.getAbsolutePath(), "-o",
				new File(outputFolder,"packed-examples").getAbsolutePath() ,"-z"});
	}
	
	public void testCreateWebpages() throws Exception
	{
		Main.main(new String[] { "-i", input.getAbsolutePath(), "-o",
				new File(outputFolder,"plain-web").getAbsolutePath() ,"-w"});
	}
	
	public void testCreateWebpagesOverture() throws Exception
	{
		Main.main(new String[] { "-i", input.getAbsolutePath(), "-o",
				new File(outputFolder,"overture-web").getAbsolutePath() ,"-w","--overture-css"});
	}
}
