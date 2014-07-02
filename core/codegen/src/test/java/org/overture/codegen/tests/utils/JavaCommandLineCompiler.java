package org.overture.codegen.tests.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.Vector;

import junit.framework.Assert;

public class JavaCommandLineCompiler
{
	public static boolean compile(File dir, File cpJar)
	{
		String javaHome = System.getenv(JavaToolsUtils.JAVA_HOME);
		File javac = new File(new File(javaHome, JavaToolsUtils.BIN_FOLDER), JavaToolsUtils.JAVAC);
		return compile(javac, dir, cpJar);
	}

	public static boolean compile(File javac, File dir, File cpJar)
	{
		if (cpJar != null)
		{
			Assert.assertNotNull("Classpath jar not found: " + cpJar, cpJar);
		}
		boolean compileOk = true;

		List<File> files = getJavaSourceFiles(dir);

		String arguments = buildFileArgs(files);

		// printCompileMessage(files, cpJar);

		StringBuilder out = new StringBuilder();
		Process p = null;
		try
		{
			String line = "";
			ProcessBuilder pb = null;
			String arg = "";

			if (JavaToolsUtils.isWindows())

				pb = new ProcessBuilder(javac.getAbsolutePath(), (cpJar == null ? ""
						: " -cp " + cpJar.getAbsolutePath()), arguments.trim());
			else
				arg = "javac"
						+ (cpJar == null ? "" : " -cp "
								+ cpJar.getAbsolutePath()) + " "
						+ arguments.replace('\"', ' ').trim();

			if (pb != null)
			{
				pb.directory(dir);
				pb.redirectErrorStream(true);
				p = pb.start();
			} else
			{
				p = Runtime.getRuntime().exec(arg, null, dir);
				InputStream stderr = p.getErrorStream();
	            InputStreamReader isr = new InputStreamReader(stderr);

	            BufferedReader br = new BufferedReader(isr);

	            String debugLine = null;
	            while ( (debugLine = br.readLine()) != null)
	            {
	                line += debugLine + "\n";
	            }
	            
	            int exitVal = p.waitFor();
	            
	            if(exitVal != 0)
	            {
	            	System.out.println(line);
	            }
			}
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
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
				p.destroy();
		}

		if (!compileOk)
			System.err.println(out.toString());

		return compileOk;

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
		List<File> files = new Vector<File>();

		if (file.isFile())
			return files;

		for (File f : file.listFiles())
		{
			if (f.isDirectory())
				files.addAll(getJavaSourceFiles(f));
			else if (f.getName().toLowerCase().endsWith(".java"))
				files.add(f);
		}
		return files;
	}
}
