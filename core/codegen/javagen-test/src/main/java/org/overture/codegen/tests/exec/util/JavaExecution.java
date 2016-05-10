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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.overture.codegen.tests.util.JavaToolsUtils;

public class JavaExecution
{
	public final static String cpPathSeparator = System.getProperty("path.separator");

	public static String run(String mainClassName, String[] preArgs, String[] postArgs,
			File directory, File... cp)
	{
		String javaHome = System.getenv(JavaToolsUtils.JAVA_HOME);
		File java = new File(new File(javaHome, JavaToolsUtils.BIN_FOLDER), JavaToolsUtils.JAVA);
		return run(java, directory, mainClassName, preArgs, postArgs, cp);
	}

	public static String run(File java, File directory, String mainClassName, String[] preArgs, String[] postArgs,
			File... cp)
	{
		String cpArgs = consCpArg(cp);

		Process p = null;
		ProcessBuilder pb;

		try
		{
			String javaArg = JavaToolsUtils.isWindows() ? java.getAbsolutePath()
					: "java";

			List<String> commands = new Vector<String>();
			commands.add(javaArg);
			commands.addAll(Arrays.asList(preArgs));
			commands.add("-cp");
			commands.add(cpArgs);
			commands.add(mainClassName.trim());
			commands.addAll(Arrays.asList(postArgs));

			pb = new ProcessBuilder(commands);

			pb.directory(directory);
			pb.redirectErrorStream(true);

			try
			{
				p = pb.start();
				return getProcessOutput(p);

			} catch (InterruptedException e)
			{
				e.printStackTrace();
				return null;
			} finally
			{
				if (p != null)
				{
					p.destroy();
				}
			}
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}

	}

	private static String getProcessOutput(Process p)
			throws InterruptedException
	{
		final StringBuffer sb = new StringBuffer();
		final InputStream is = p.getInputStream();
		is.mark(0);

		// the background thread watches the output from the process
		Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String line;
					while ((line = reader.readLine()) != null)
					{
						sb.append(line + "\n");

					}
				} catch (IOException e)
				{
					e.printStackTrace();
				} finally
				{
					try
					{
						is.close();
					} catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});

		t.start();
		p.waitFor();
		t.join();

		return sb.toString();
	}

	private static String consCpArg(File... file)
	{
		String cp = "";
		for (File file2 : file)
		{
			String cpPart = consCpArg(file2);

			if (cp.length() == 0)
			{
				cp = cpPart;
			} else
			{
				cp += cpPathSeparator + cpPart;
			}
		}

		return cp;
	}

	private static String consCpArg(File file)
	{
		if (file == null)
		{
			return JavaToolsUtils.CURRENT_FOLDER;
		}

		if (file.isFile())
		{
			if (file.getName().endsWith(".class"))
			{
				return JavaToolsUtils.CURRENT_FOLDER;
			} else
			{
				return file.getAbsolutePath();
			}

		}
		StringBuilder sb = new StringBuilder();

		File[] allFiles = file.listFiles();

		if (allFiles == null || allFiles.length == 0)
		{
			return JavaToolsUtils.CURRENT_FOLDER;
		}

		if (file.isDirectory())
		{
			sb.append(file.getAbsolutePath() + cpPathSeparator);
		}

		for (int i = 0; i < allFiles.length; i++)
		{
			File currentFile = allFiles[i];

			if (currentFile.isDirectory())
			{
				sb.append(currentFile.getAbsolutePath() + cpPathSeparator);
			}
		}

		if (sb.length() == 0)
		{
			return JavaToolsUtils.CURRENT_FOLDER;
		}

		return sb.substring(0, sb.length() - 1);
	}
}
