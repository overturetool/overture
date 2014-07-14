package org.overture.codegen.tests.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
			if (JavaToolsUtils.isWindows())
			{
				pb = new ProcessBuilder(java.getAbsolutePath(), "-cp", cpArgs, mainClassName.trim());
				pb.directory(cp);
				pb.redirectErrorStream(true);
				try
				{
					p = pb.start();
					p.waitFor();
				} catch (InterruptedException e)
				{
					e.printStackTrace();
					return null;
				}
			} else
			{
				String arg = "java" + " -cp " + cpArgs + " "
						+ mainClassName;

				p = Runtime.getRuntime().exec(arg.replace('\"', ' '), null, cp);
				InputStream stderr = p.getErrorStream();

	            InputStreamReader isr = new InputStreamReader(stderr);

	            BufferedReader br = new BufferedReader(isr);
	            String debugLine = null;
	            String line = "";
				while ( (debugLine = br.readLine()) != null)
	                line  += debugLine + "\n";
	            int exitVal = -1;
				try
				{
					exitVal = p.waitFor();
					
					if(exitVal != 0)
					{
						System.out.println(line);
					}
					
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}

			StringBuilder out = new StringBuilder();
			String line;
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			
			while ((line = input.readLine()) != null)
			{
				out.append(line + "\n");
			}

			input.close();

			return out.toString();

		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	private static String consCpArg(File file)
	{
		if (file == null || file.isFile())
			return JavaToolsUtils.CURRENT_FOLDER;
		
		File[] allFiles = file.listFiles();
		
		if(allFiles == null || allFiles.length == 0)
			return JavaToolsUtils.CURRENT_FOLDER;
		
		StringBuilder sb = new StringBuilder();
		
		char fileSep = JavaToolsUtils.isWindows() ? ';' : ':';
		
		if(file.isDirectory())
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
		
		if(sb.length() == 0)
			return JavaToolsUtils.CURRENT_FOLDER;
		
		return sb.substring(0, sb.length()-1);
	}
}
