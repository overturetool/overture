/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.tools.packworkspace.testing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.overture.vdmj.ExitStatus;

public class TestStatus
{
	File file;

	public ExitStatus statusParse = null;
	public ExitStatus statusTypeCheck = null;
	public ExitStatus statusPo = null;
	public ExitStatus statusInterpreter = null;
	public Integer poCount = 0;
	public boolean isFaild = false;

	public TestStatus(File base, boolean load) {
		file = new File(base.getAbsolutePath(), ".status");
		if (load)
			load();

	}

	private void load()
	{
		FileReader inputFileReader;
		try
		{
			if (!file.exists())
				return;
			inputFileReader = new FileReader(file);

			// Create Buffered/PrintWriter Objects
			BufferedReader inputStream = new BufferedReader(inputFileReader);

			statusParse = getStatus(getLine(inputStream));
			statusTypeCheck = getStatus(getLine(inputStream));
			statusPo = getStatus(getLine(inputStream));
			statusInterpreter = getStatus(getLine(inputStream));
			poCount = Integer.parseInt(getLine(inputStream));
			isFaild = Boolean.parseBoolean(getLine(inputStream));
			inputStream.close();
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private ExitStatus getStatus(String line)
	{
		if(line.equals("null"))
			return null;
		
		return ExitStatus.valueOf(line);
	}

	private String getLine(BufferedReader inputStream) throws IOException
	{
		String inLine = null;
		inLine = inputStream.readLine();
		return inLine;
	}

	public void save()
	{
		FileWriter outputFileReader;
		try
		{
			if (file.exists())
				file.delete();
			outputFileReader = new FileWriter(file);
			StringBuilder sb = new StringBuilder();

			sb.append(statusParse + "\n");
			sb.append(statusTypeCheck + "\n");
			sb.append(statusPo + "\n");
			sb.append(statusInterpreter + "\n");
			sb.append(poCount + "\n");
			sb.append(isFaild + "\n");

			BufferedWriter outputStream = new BufferedWriter(outputFileReader);
			outputStream.write(sb.toString());
			outputStream.close();
		} catch (IOException e)
		{

		}
	}
}
