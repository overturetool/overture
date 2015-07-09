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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.overture.codegen.vdm2java.JavaToolsUtils;

public class JavaExecution
{
	public static String run(File cp, String mainClassName)
	{
		String javaHome = System.getenv(JavaToolsUtils.JAVA_HOME);
		File java = new File(new File(javaHome, JavaToolsUtils.BIN_FOLDER), JavaToolsUtils.JAVA);
		return run(java, mainClassName, cp);
	}

	public static String run(File java, String mainClassName, File cp)
	{
		String cpArgs = consCpArg(cp);

		Process p = null;
		ProcessBuilder pb = null;

		try
		{
				String javaArg = JavaToolsUtils.isWindows() ? java.getAbsolutePath() : "java";
				pb = new ProcessBuilder(javaArg, "-cp", cpArgs, mainClassName.trim());
				pb.directory(cp);
				pb.redirectErrorStream(true);
				
				try
				{
					p = pb.start();
					return getProcessOutput(p);
					
				} catch (InterruptedException e)
				{
					e.printStackTrace();
					return null;
				}
				finally
				{
					if(p != null)
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
		Thread t = new Thread(new Runnable() {
		    public void run() {
		        try {
		            BufferedReader reader =
		                new BufferedReader(new InputStreamReader(is));
		            String line;
		            while ((line = reader.readLine()) != null) {
		            	sb.append(line + "\n");
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		        } finally {
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

	private static String consCpArg(File file)
	{
		if (file == null || file.isFile())
		{
			return JavaToolsUtils.CURRENT_FOLDER;
		}

		File[] allFiles = file.listFiles();

		if (allFiles == null || allFiles.length == 0)
		{
			return JavaToolsUtils.CURRENT_FOLDER;
		}

		StringBuilder sb = new StringBuilder();

		char fileSep = JavaToolsUtils.isWindows() ? ';' : ':';

		if (file.isDirectory())
		{
			sb.append(file.getAbsolutePath() + fileSep);
		}

		for (int i = 0; i < allFiles.length; i++)
		{
			File currentFile = allFiles[i];

			if (currentFile.isDirectory())
			{
				sb.append(currentFile.getAbsolutePath() + fileSep);
			}
		}

		if (sb.length() == 0)
		{
			return JavaToolsUtils.CURRENT_FOLDER;
		}

		return sb.substring(0, sb.length() - 1);
	}
}
