package org.overture.codegen.utils;

import java.io.IOException;
import java.io.InputStream;

public class GeneralUtils
{
	public static StringBuffer readFromFile(String relativepath) throws IOException
	{
		InputStream input = GeneralUtils.class.getResourceAsStream(relativepath);

		if (input == null)
			return null;

		StringBuffer data = new StringBuffer();
		int c = 0;
		while ((c = input.read()) != -1)
		{
			data.append((char) c);
		}
		input.close();

		return data;
	}
}
