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
package org.overture.codegen.tests.exec.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.Vector;

import org.overture.codegen.tests.util.JavaToolsUtils;

public class JavaCommandLineCompiler
{
	public static ProcessResult compile(File dir, File[] cpJars)
	{
		String javaHome = System.getenv(JavaToolsUtils.JAVA_HOME);
		File javac = new File(new File(javaHome, JavaToolsUtils.BIN_FOLDER), JavaToolsUtils.JAVAC);
		return compile(javac, dir, cpJars);
	}

	public static ProcessResult compile(File javac, File dir, File[] cpJars)
	{
		boolean compileOk = true;

		List<File> files = getJavaSourceFiles(dir);

		String arguments = buildFileArgs(files);

		StringBuilder out = new StringBuilder();
		Process p = null;
		try
		{
			String line = "";
			ProcessBuilder pb = null;
			String arg = "";

			String cpArg = "";
			if(cpJars != null && cpJars.length > 0)
			{
				cpArg = consCpArg(cpJars);
			}
			
			if (JavaToolsUtils.isWindows())
			{
				pb = new ProcessBuilder(javac.getAbsolutePath(), cpArg, arguments.trim());
			} else
			{
				arg = "javac"// -nowarn -J-client -J-Xms100m -J-Xmx100m"
						+ cpArg + " "
						+ arguments.replace('\"', ' ').trim();
			}

			if (pb != null)
			{
				pb.directory(dir);
				pb.redirectErrorStream(true);
				p = pb.start();
			} else
			{
				p = Runtime.getRuntime().exec(arg, null, dir);
			}
			
			p.waitFor();
			
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String secondLastLine = "";
			while ((line = input.readLine()) != null)
			{
				out.append("\n" + line);
				secondLastLine = line;
			}

			compileOk = !secondLastLine.toLowerCase().contains("error");
			input.close();
		} catch (Exception err)
		{
			System.err.println(out.toString());
			err.printStackTrace();

		} finally
		{
			if (p != null)
			{
				p.destroy();
			}
		}

		return new ProcessResult(compileOk ? 0 : 1, out);
	}

	private static String consCpArg(File[] cpJars)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" -cp ");
		
		String sep = "";
		
		for(File jar : cpJars)
		{
			sb.append(sep);
			sb.append(jar.getAbsolutePath());
			sep = File.pathSeparator;
		}
		
		return sb.toString();
	}

	public static void printCompileMessage(List<File> files, File cpJar)
	{
		PrintStream out = System.out;

		out.println("Compiling (JAVA):");
		if (cpJar != null)
		{
			out.println("Class path:  " + cpJar.getName());
		}

		for (File file : files)
		{
			out.println("             " + file.getName());
		}

	}

	private static String buildFileArgs(List<File> files)
	{
		StringBuilder sb = new StringBuilder();

		for (File file : files)
		{
			sb.append("\"" + file.getAbsolutePath() + "\" ");
		}

		return sb.toString();
	}

	private static List<File> getJavaSourceFiles(File file)
	{
		List<File> files = new ArrayList<File>();

		if (file.isFile())
		{
			return files;
		}

		for (File f : file.listFiles())
		{
			if (f.isDirectory())
			{
				files.addAll(getJavaSourceFiles(f));
			} else if (f.getName().toLowerCase().endsWith(".java"))
			{
				files.add(f);
			}
		}
		return files;
	}
}
