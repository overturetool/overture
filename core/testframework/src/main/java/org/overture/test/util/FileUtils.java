/*
 * #%~
 * Test Framework for Overture
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
package org.overture.test.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils
{

	public static List<String> readTextFromJar(String s) throws Exception
	{
		InputStream is = null;
		BufferedReader br = null;
		String line;
		List<String> list = new ArrayList<>();

		try
		{
			is = FileUtils.class.getResourceAsStream(s);
			br = new BufferedReader(new InputStreamReader(is));
			while (null != (line = br.readLine()))
			{
				list.add(line);
			}
		} catch (Exception e)
		{
			// System.err.println("Faild to read file from jar: \"" + s + "\"");
			// e.printStackTrace();
			throw e;
		} finally
		{
			try
			{
				if (br != null)
				{
					br.close();
				}
				if (is != null)
				{
					is.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return list;
	}

	public static String readFile(String relativePath) throws IOException
	{
		StringBuilder sb = new StringBuilder();

		try
		{
			for (String s : FileUtils.readTextFromJar(relativePath))
			{
				sb.append("\n" + s);
			}
		} catch (Exception e)
		{
			// ok maybe we are running in debug mode
			for (String s : FileUtils.readTextFromSource(relativePath))
			{
				sb.append("\n" + s);
			}
		}
		return sb.toString();
	}

	private static List<String> readTextFromSource(String relativePath)
	{
		List<String> list = new ArrayList<>();
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(new File(new File("."), ("src/test/resources/" + relativePath).replace('/', File.separatorChar))));
			String str;
			while ((str = in.readLine()) != null)
			{
				list.add(str);
			}
			in.close();
		} catch (IOException e)
		{
		}

		return list;
	}

	// public static void writeFile(File outputFolder, String fileName, String content)
	// throws IOException
	// {
	// FileWriter outputFileReader = new FileWriter(new File(outputFolder,
	// fileName), false);
	// BufferedWriter outputStream = new BufferedWriter(outputFileReader);
	// outputStream.write(content);
	// outputStream.close();
	//
	// }
	//
	// public static void writeFile(File file, String content) throws IOException
	// {
	// FileWriter outputFileWriter = new FileWriter(file);
	// BufferedWriter outputStream = new BufferedWriter(outputFileWriter);
	// outputStream.write(content);
	// outputStream.close();
	// outputFileWriter.close();
	// }

	public static void writeFile(String data, File file)
	{
		writeFile(data, file, false);
	}

	public static void writeFile(String data, File file, boolean append)
	{
		BufferedWriter outputStream = null;
		try
		{
			FileWriter outputFileReader = new FileWriter(file, append);
			outputStream = new BufferedWriter(outputFileReader);

			outputStream.write(data);

			outputStream.flush();
			outputStream.close();

		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			if (outputStream != null)
			{
				try
				{
					outputStream.close();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}
