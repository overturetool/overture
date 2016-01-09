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
package org.overture.codegen.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class GeneralUtils
{
	public static boolean isEscapeSequence(char c)
	{
		return c == '\t' || c == '\b' || c == '\n' || c == '\r' || c == '\f'
				|| c == '\'' || c == '\"' || c == '\\';
	}

	/**
	 * Not for use with extensions. Use {@link #readFromFile(String, Class)} instead.
	 * @param relativepath
	 * @return
	 * @throws IOException
	 */
	public static StringBuffer readFromFile(String relativepath)
			throws IOException
	{
		InputStream input = GeneralUtils.class.getResourceAsStream('/' + relativepath.replace("\\", "/"));

		return readFromInputStream(input);
	}
	
	public static StringBuffer readFromFile(String relativepath, Class<?> classRef) throws IOException{
		InputStream input = classRef.getResourceAsStream('/' + relativepath.replace("\\", "/"));

		return readFromInputStream(input);
	}

	public static StringBuffer readFromInputStream(InputStream input)
			throws IOException
	{
		if (input == null)
		{
			return null;
		}

		StringBuffer data = new StringBuffer();
		int c = 0;
		while ((c = input.read()) != -1)
		{
			data.append((char) c);
		}
		input.close();

		return data;
	}

	public static String readFromFile(File file) throws IOException
	{
		return readLines(file, System.getProperty("line.separator")).toString().trim();
	}

	public static StringBuilder readLines(File file, String outputLineEnding)
			throws UnsupportedEncodingException, FileNotFoundException,
			IOException
	{
		StringBuilder data = new StringBuilder();
		BufferedReader in = null;

		try
		{
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));

			String str = "";

			while ((str = in.readLine()) != null)
			{
				data.append(str + outputLineEnding);
			}

		} finally
		{
			if (in != null)
			{
				in.close();
			}
		}

		return data;
	}

	public static List<File> getFiles(File folder)
	{
		File[] listOfFiles = folder.listFiles();

		List<File> fileList = new LinkedList<File>();

		if (listOfFiles == null || listOfFiles.length == 0)
		{
			return fileList;
		}

		for (File file : listOfFiles)
		{
			if (file.isFile())
			{
				fileList.add(file);
			}
		}

		return fileList;
	}

	public static void deleteFolderContents(File folder, boolean removeFolders)
	{
		deleteFolderContents(folder, new ArrayList<String>(), removeFolders);
	}

	public static void deleteFolderContents(File folder,
			List<String> folderNamesToAvoid, boolean removeFolders)
	{
		if (folder == null)
		{
			return;
		}

		File[] files = folder.listFiles();

		if (files == null)
		{
			return;
		}

		for (File f : files)
		{
			if (f.isDirectory())
			{
				if (!folderNamesToAvoid.contains(f.getName()))
				{
					deleteFolderContents(f, folderNamesToAvoid, removeFolders);
				}
			} else
			{
				f.delete();
			}
			
			if(removeFolders)
			{
				f.delete();
			}
		}
	}
	
	public static <T> T[] concat(T[] first, T[] second) {
		  T[] result = Arrays.copyOf(first, first.length + second.length);
		  System.arraycopy(second, 0, result, first.length, second.length);
		  return result;
		}
	
	public static String cleanupWhiteSpaces(String str)
	{
		return str.replaceAll("\\s+", " ").trim();
	}
	
	public static List<File> getFilesRecursively(File folder)
	{
		File[] listOfFiles = folder.listFiles();
		
		List<File> fileList = new LinkedList<File>();

		if(listOfFiles == null || listOfFiles.length == 0)
			return fileList;
		
		for (File file : listOfFiles)
		{
			if (file.isFile())
			{
				fileList.add(file);
			}
			if(file.isDirectory())
			{
				fileList.addAll(getFilesRecursively(file));
			}
		}
		return fileList;
	}
}
