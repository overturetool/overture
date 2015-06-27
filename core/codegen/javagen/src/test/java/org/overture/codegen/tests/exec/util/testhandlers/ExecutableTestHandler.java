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
package org.overture.codegen.tests.exec.util.testhandlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.input.ClassLoaderObjectInputStream;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.tests.exec.util.ExecutionResult;
import org.overture.codegen.tests.exec.util.JavaExecution;
import org.overture.config.Release;

public abstract class ExecutableTestHandler extends TestHandler
{
	private final static Random rand = new Random(100);
	private static final String MAIN_CLASS = "Exp";

	public static final String SERIALIZE_METHOD = "  public static void serialize(File file){\n"
			+ "     try{\n"
			+ "       //File file = new File(\"myData.bin\");\n"
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
			+ "	      ex.printStackTrace();\n " + "     }\n" + "  }\n";

	public String getMainClass()
	{
		StringBuilder methodsMerged = new StringBuilder();

		for (String method : getMainClassMethods())
		{
			methodsMerged.append(method).append("\n\n");
		}

		return "  import java.io.File;\n"
				+ "import java.io.FileOutputStream;\n"
				+ "import java.io.ObjectOutputStream;\n"
				+ "import org.overture.codegen.runtime.*;\n"
				+ "import org.overture.codegen.runtime.traces.*;\n"
				+ "import java.util.*;\n\n"
				+ "public class Exp {\n"
				+ "  public static Object exp()\n"
				+ "  {\n"
				+ "    return %s;\n"
				+ "  }\n\n"
				+ "  public static void main(String[] args)"
				+ "  {\n"
				+ "  if(args.length < 1)\n"
				+ "  {\n"
				+ " \t java.lang.System.err.println(\"Error: Missing serilization file path\"); java.lang.System.exit( 1);"
				+ "  }\n" + "      serialize(new File(args[0]));\n" + "  }\n\n"
				+ SERIALIZE_METHOD + methodsMerged + "}\n";
	}


	public ExecutableTestHandler(Release release, Dialect dialect)
	{
		super(release, dialect);
	}

	public abstract ExecutionResult interpretVdm(File intputFile)
			throws Exception;

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
			File cgRuntime = new File(org.overture.codegen.runtime.EvaluatePP.class.getProtectionDomain().getCodeSource().getLocation().getFile());

			String resultFilename = String.format("serilizedExecutionResult-%d.bin", rand.nextLong());

			String processOutput = JavaExecution.run(ExecutableTestHandler.MAIN_CLASS, new String[] { resultFilename }, folder, folder, cgRuntime);

			File dataFile = new File(folder, resultFilename);
			dataFile.deleteOnExit();
			fin = new FileInputStream(dataFile);

			// Create a new class loader to load classes specific to the run folder
			URL[] urls = null;
			urls = new URL[] { folder.toURI().toURL() };
			ClassLoader cl = new URLClassLoader(urls);

			// Use a ObjectInputStream that loads from a custom class loader
			ois = new ClassLoaderObjectInputStream(cl, fin);
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
