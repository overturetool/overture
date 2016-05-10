package org.overture.vdm2jml.tests.util;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.overture.codegen.utils.GeneralUtils;
import org.overture.codegen.vdm2java.IJavaConstants;
import org.overture.codegen.vdm2java.JavaCodeGen;
import org.overture.vdm2jml.tests.OpenJmlValidationBase;
import org.overture.vdm2jml.tests.exec.JmlExecTestBase;

public class TestUtil
{
	public static List<File> collectStoredJavaJmlFiles(File folder)
	{
		List<File> files = GeneralUtils.getFiles(folder);
		
		LinkedList<File> javaFiles = new LinkedList<>();
		
		for(File f : files)
		{
			if(f.getName().endsWith(IJavaConstants.JAVA_FILE_EXTENSION))
			{
				javaFiles.add(f);
			}
		}
		
		return javaFiles;
	}
	
	public static List<File> collectGenJavaJmlFiles(File folder)
	{
		List<File> files = GeneralUtils.getFilesRecursively(folder);
		
		String projDir = File.separatorChar + JmlExecTestBase.DEFAULT_JAVA_ROOT_PACKAGE
				+ File.separatorChar;
		String quotesDir = File.separatorChar + JavaCodeGen.JAVA_QUOTES_PACKAGE
				+ File.separatorChar;
		
		List<File> filesToStore = new LinkedList<>();
	
		for (File file : files)
		{
			String absPath = file.getAbsolutePath();
	
			if (absPath.endsWith(IJavaConstants.JAVA_FILE_EXTENSION)
					&& absPath.contains(projDir)
					&& !absPath.contains(quotesDir))
			{
				filesToStore.add(file);
			}
		}
		
		return filesToStore;
	}
	
	public static Collection<Object[]> collectVdmslFiles(List<File> files)
	{
		List<Object[]> testInputFiles = new LinkedList<>();

		for (File f : files)
		{
			if (f.getName().endsWith(OpenJmlValidationBase.VDMSL_FILE_EXT))
			{
				testInputFiles.add(new Object[] { f });
			}
		}

		return testInputFiles;
	}
}
