package org.overture.interpreter.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public abstract class QuickProfiler
{

	private static String PROFILE_PATH = "profile.csv";

	public static void printDuration(long start, String name)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(", ");
		sb.append(System.currentTimeMillis() - start);
		sb.append("\n");
		try
		{
			FileUtils.writeStringToFile(new File(PROFILE_PATH), sb.toString(), true);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
