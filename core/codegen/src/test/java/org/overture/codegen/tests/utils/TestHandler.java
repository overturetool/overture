package org.overture.codegen.tests.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.overture.ast.lex.Dialect;
import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.constants.IOoAstConstants;
import org.overture.codegen.utils.GeneralUtils;
import org.overture.config.Release;
import org.overture.config.Settings;

public abstract class TestHandler
{
	public static final String QUOTES_PACKAGE_NAME = "quotes";
	
	public static final String MAIN_CLASS = "Exp";
	
	public static final String SERIALIZE_METHOD = 	
			
			"  public static void serialize(){" 
			+ "     try{"
			+ "       File file = new File(\"myData.bin\");"
			+ "	      FileOutputStream fout = new FileOutputStream( file );" 
			+ "	      ObjectOutputStream oos = new ObjectOutputStream(fout);"
			+ "       Object exp = exp();"
			+ "		  java.lang.System.out.println(exp);" 
			+ "	      oos.writeObject( exp );" 
			+ "	      oos.close();"
			+ "     }catch(Exception ex){ "
			+ "	      ex.printStackTrace(); " 
			+ "     }" 
			+ "  }"; 
		
	public static final String EXP_WRAPPER_CODE = 
		  "import java.io.File;"
		+ "import java.io.FileOutputStream;"
		+ "import java.io.ObjectOutputStream;"
		+ "import org.overture.codegen.runtime.*;"
		+ "import java.util.*; " 
		+ "public class Exp { "
		
		+ "  public static Object exp(){ return %s ; } "
		
		+ "  public static void main(String[] args){ "
		+ "      serialize(); "
		+ "  } "
		+ 	SERIALIZE_METHOD
		+ "}";
	
	public TestHandler()
	{
		initVdmEnv();
	}
	
	public void initVdmEnv()
	{
		Settings.release = Release.VDM_10;
		Settings.dialect = Dialect.VDM_PP;
	}

	public abstract void writeGeneratedCode(File parent, File resultFile) throws IOException;
	
	public void injectArgIntoMainClassFile(File parent, String argument) throws IOException
	{
		File mainClassFile = getMainClassFile(parent);
		writeToFile(String.format(EXP_WRAPPER_CODE, argument), mainClassFile);
	}
	
	public void writeToFile(String toWrite, File file) throws IOException
	{
		FileWriter fileWriter = new FileWriter(file);

		fileWriter.write(toWrite);

		fileWriter.flush();
		fileWriter.close();

	}
	
	public File getFile(File parent, String className) throws IOException
	{
		File file = new File(parent, className
				+ IJavaCodeGenConstants.JAVA_FILE_EXTENSION);
		
		if (!file.exists())
			file.createNewFile();
		
		return file;
	}
	
	private File getMainClassFile(File parent) throws IOException
	{
		return getFile(parent, MAIN_CLASS);
	}
	
	public String readFromFile(File resultFile) throws IOException
	{
		return GeneralUtils.readFromFile(resultFile).replace('#', ' ');
	}
	
	protected File consTempFile(String className, File parent, StringBuffer classCgStr)
			throws IOException
	{
		File outputDir = parent;

		if (className.equals(IOoAstConstants.QUOTES_INTERFACE_NAME))
		{
			outputDir = new File(parent, QUOTES_PACKAGE_NAME);
			outputDir.mkdirs();
		}

		File tempFile = new File(outputDir, className
				+ IJavaCodeGenConstants.JAVA_FILE_EXTENSION);

		if (!tempFile.exists())
		{
			tempFile.createNewFile();
		}
		return tempFile;
	}
}


