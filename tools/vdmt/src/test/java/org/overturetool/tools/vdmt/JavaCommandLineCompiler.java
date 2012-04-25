package org.overturetool.tools.vdmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.Vector;

import junit.framework.Assert;

import org.overture.tools.vdmt.VDMToolsProxy.VdmProject;

public class JavaCommandLineCompiler
{
	public static boolean compile(File javac, File dir, File cpJar)
	{
		Assert.assertNotNull("Classpath jar not found: " + cpJar, cpJar);
		boolean compileOk = true;

		List<File> files = getFiles(dir);

		String arguments = buildFileArgs(files);

		printCompileMessage(files, cpJar);

		StringBuilder out = new StringBuilder();
		Process p = null;
		try
		{
			String line;
			ProcessBuilder pb = null;
			String arg = "";

			if (VdmProject.isWindows())

				pb = new ProcessBuilder("\"" + javac.getAbsolutePath()
						+ "\" -cp " + cpJar.getAbsolutePath() + " "
						+ arguments.trim());
			else
				arg = javac.getAbsolutePath() + " -cp "
						+ cpJar.getAbsolutePath() + " " + arguments.trim();

			if (pb != null)
			{
				pb.directory(dir);
				pb.redirectErrorStream(true);
				p = pb.start();
			} else
				p = Runtime.getRuntime().exec(arg, null, dir);

			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String secondLastLine = "";
			while ((line = input.readLine()) != null)
			{
				out.append("\n" + line);

				secondLastLine = line;
			}
			compileOk = !secondLastLine.toLowerCase().startsWith("error");
			input.close();
		} catch (Exception err)
		{
			System.out.println(out.toString());
			err.printStackTrace();

		} finally
		{
			if (p != null)
				p.destroy();
		}

		if (compileOk)
			System.out.println("Compile complited OK");
		else
			System.out.println(out.toString());

		return false;

	}

	private static void printCompileMessage(List<File> files, File cpJar)
	{
		PrintStream out = System.out;

		out.println("Java Compile:");
		out.println("Class path:  " + cpJar.getName());
		out.println("Java files:  ");

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

	private static List<File> getFiles(File file)
	{
		List<File> files = new Vector<File>();
		for (File f : file.listFiles())
		{
			if (f.isDirectory())
				files.addAll(getFiles(f));
			else if (f.getName().toLowerCase().endsWith(".java"))
				files.add(f);
		}
		return files;
	}
}
