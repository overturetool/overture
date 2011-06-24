package org.overturetool.vdmj.runtime.validation;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class TimingInvariantsParser
{
	public List<ConjectureDefinition> parse(File file) throws IOException
	{
		StringBuffer contents = new StringBuffer();
		BufferedReader reader = null;
		boolean enabled = false;

		try
		{
			reader = new BufferedReader(new FileReader(file));
			String text = null;

			// repeat until all lines is read
			while ((text = reader.readLine()) != null)
			{
				if (text.replaceAll("\\s", " ").startsWith("/* timing invariants"))
				{
					enabled = true;
					continue;
				}

				if (text.replaceAll("\\s", "").startsWith("*/"))
				{
					enabled = false;
				}

				if (enabled && text.trim().length() > 0)
				{
					contents.append("\n" + text);
				}
			}
			return parse(contents.toString());

		} finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

	private List<ConjectureDefinition> parse(String contents)
	{
		List<ConjectureDefinition> defs = new Vector<ConjectureDefinition>();

		try
		{
			int indexOfLParn = contents.indexOf('(');

			while ((indexOfLParn = contents.indexOf('(')) != -1)
			{
				String propName = contents.substring(0, indexOfLParn).trim();
				contents = contents.substring(indexOfLParn + 1);
				int end = contents.indexOf(';') - 1;
				String propBody = contents.substring(0, end);
				contents = contents.substring(end + 2);

				String[] elements = propBody.split(",");
				List<String> elems = Arrays.asList(elements);
				
				//TODO create definitions
			}
		} catch (IndexOutOfBoundsException e)
		{

		}

		return defs;
	}
}
