/*
 * #%~
 * VDM Code Generator
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.codegen.tests.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.LinkedList;
import java.util.List;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;
import org.overture.config.Settings;

public abstract class ExecutableTestHandler extends TestHandler
{
	public static final String MAIN_CLASS = "Exp";

	public static final String SERIALIZE_METHOD =
			"  public static void serialize(){\n" 
			+ "     try{\n"
			+ "       File file = new File(\"myData.bin\");\n"
			+ "	      FileOutputStream fout = new FileOutputStream( file );\n"
			+ "	      ObjectOutputStream oos = new ObjectOutputStream(fout);\n"
			+ "       Object exp = null;\n"
			+ "       try{\n"
			+ "         exp = exp();\n"
			+ "       } catch(Exception e){\n"
			+ "         exp = e.getMessage();\n"
			+ "       }\n"
			+ "		  java.lang.System.out.println(exp);\n"
			+ "	      oos.writeObject( exp );\n" 
			+ "	      oos.close();\n"
			+ "     }catch(Exception ex){\n" 
			+ "	      ex.printStackTrace();\n "
			+ "     }\n"
			+ "  }\n";

	public String getMainClass()
	{
		StringBuilder methodsMerged = new StringBuilder();
		
		for(String method : getMainClassMethods())
		{
			methodsMerged.append(method).append("\n\n");
		}
		
		return
				"  import java.io.File;\n"
				+ "import java.io.FileOutputStream;\n"
				+ "import java.io.ObjectOutputStream;\n"
				+ "import org.overture.codegen.runtime.*;\n"
//				+ "import org.overture.codegen.runtime.traces.*;\n"
				+ "import java.util.*;\n\n"
				+ "public class Exp {\n"
				+ "  public static Object exp()\n"
				+ "  {\n"
				+ "    return %s;\n"
				+ "  }\n\n"
				+ "  public static void main(String[] args)"
				+ "  {\n"
				+ "      serialize();\n" 
				+ "  }\n\n" 
				+    SERIALIZE_METHOD
				+    methodsMerged
				+ "}\n";
	}
	
	private static final String CG_VALUE_BINARY_FILE = "target"
			+ File.separatorChar + "cgtest" + File.separatorChar + "myData.bin";
	
	public ExecutableTestHandler(Release release, Dialect dialect)
	{
		Settings.release = release;
		Settings.dialect = dialect;
	}
	
	public abstract ExecutionResult interpretVdm(File intputFile) throws Exception;
	
	public List<String> getMainClassMethods()
	{
		return new LinkedList<String>();
	}
	
	public void injectArgIntoMainClassFile(File parent, String body)
			throws IOException
	{
		File mainClassFile = getMainClassFile(parent);
		writeToFile(String.format(getMainClass(), body), mainClassFile);
	}

	private File getMainClassFile(File parent) throws IOException
	{
		return getFile(parent, MAIN_CLASS);
	}
	
	public ExecutionResult runJava(File folder)
	{
		FileInputStream fin = null;
		ObjectInputStream ois = null;
		
		try
		{
			String processOutput =  JavaExecution.run(folder, ExecutableTestHandler.MAIN_CLASS);
			
			File dataFile = new File(CG_VALUE_BINARY_FILE);
			fin = new FileInputStream(dataFile);
			ois = new ObjectInputStream(fin);
			Object cgValue = (Object) ois.readObject();
			
			return new ExecutionResult(processOutput, cgValue);
			
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			if (fin != null)
			{
				try
				{
					fin.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
			
			if (ois != null)
			{
				try
				{
					ois.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
}
