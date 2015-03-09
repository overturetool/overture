package org.overture.interpreter.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public abstract class QuickProfiler
{

	private static String PROFILE_PATH = "profile.csv";
	private static StringBuilder sb = new StringBuilder();

	public static void printDuration(long start, String name)
	{
		sb.append("\"");
		sb.append(name);
		sb.append("\"");
		sb.append(", ");
		sb.append(System.currentTimeMillis() - start);
		sb.append("\n");
	}

	public static void print()
	{
		try
		{
			FileUtils.writeStringToFile(new File(PROFILE_PATH), sb.toString(), false);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
