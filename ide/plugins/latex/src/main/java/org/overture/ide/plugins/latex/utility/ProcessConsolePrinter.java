/*
 * #%~
 * org.overture.ide.plugins.latex
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
package org.overture.ide.plugins.latex.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.overture.ide.ui.internal.util.ConsoleWriter;

public class ProcessConsolePrinter extends Thread
{
	ConsoleWriter cw = null;
	InputStream stream = null;
	List<String> fails = new ArrayList<String>();
	PdfGenerator generator = null;

	public ProcessConsolePrinter(ConsoleWriter cw, InputStream inputStream,
			PdfGenerator pdfLatex)
	{
		this.cw = cw;
		this.stream = inputStream;
		this.generator = pdfLatex;
	}

	@Override
	public void run()
	{

		String line = null;
		BufferedReader input = new BufferedReader(new InputStreamReader(stream));
		try
		{
			while ((line = input.readLine()) != null)
			{
				detectFail(line);
				if (cw != null)
				{
					cw.println(line);
				} else
				{
					System.out.println(line);
				}
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String> getFails()
	{
		return this.fails;
	}

	private void detectFail(String line)
	{
		if (line.contains("Emergency stop") || line.contains("LaTeX Error"))
		{
			fails.add(line);
			generator.setFail(true);
		}

	}
}
