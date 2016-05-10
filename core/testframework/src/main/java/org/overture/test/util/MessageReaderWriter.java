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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.ArrayList;

import org.overture.test.framework.results.IMessage;
import org.overture.test.framework.results.Message;
import org.overture.test.framework.results.Result;

public class MessageReaderWriter
{

	enum MsgType
	{
		Warning, Error, Result
	};

	public static String WARNING_LABEL = "WARNING";
	public static String ERROR_LABEL = "ERROR";
	public static String RESULT_LABEL = "RESULT";

	final List<IMessage> errors = new ArrayList<IMessage>();
	final List<IMessage> warnings = new ArrayList<IMessage>();
	String result = "";
	final File file;

	public MessageReaderWriter(File file)
	{
		this.file = file;
	}

	public MessageReaderWriter(String path)
	{
		this(new File(path));
	}

	public void setWarningsAndErrors(List<IMessage> errors,
			List<IMessage> warnings)
	{
		this.errors.clear();
		this.warnings.clear();
		this.errors.addAll(errors);
		this.warnings.addAll(warnings);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void set(Result result)
	{
		setWarningsAndErrors(result.errors, result.warnings);
		this.result = result.getStringResult();
	}

	public List<IMessage> getErrors()
	{
		return errors;
	}

	public List<IMessage> getWarnings()
	{
		return warnings;
	}

	public String getResult()
	{
		return result;
	}

	public boolean exists()
	{
		return file.exists();
	}

	public boolean load()
	{
		errors.clear();
		warnings.clear();
		result = "";
		BufferedReader in = null;
		try
		{
			in = new BufferedReader(new FileReader(file));
			String line;
			while ((line = in.readLine()) != null)
			{
				MsgType type = null;
				if (line.startsWith(ERROR_LABEL))
				{
					type = MsgType.Error;
					line = line.substring(ERROR_LABEL.length() + 1);
				} else if (line.startsWith(WARNING_LABEL))
				{
					type = MsgType.Warning;
					line = line.substring(WARNING_LABEL.length() + 1);
				} else if (line.startsWith(RESULT_LABEL))
				{
					type = MsgType.Result;
					line = line.substring(RESULT_LABEL.length() + 1);
				} else
				{
					return false;
				}

				String[] splitLine = line.split(":");

				if (splitLine.length != 4)
				{
					return false;
				}

				int number = Integer.parseInt(splitLine[1]);

				String[] position = splitLine[2].split(",");

				if (position.length != 2)
				{
					return false;
				}

				int startLine = Integer.parseInt(position[0]);
				int startCol = Integer.parseInt(position[1]);

				String message = splitLine[3];
				String resource = splitLine[0];

				IMessage msg = new Message(resource, number, startLine, startCol, message);

				switch (type)
				{
					case Error:
						errors.add(msg);
						break;
					case Warning:
						warnings.add(msg);
						break;
					case Result:
						result = message;
						break;
				}

			}

		} catch (FileNotFoundException e)
		{
			return false;
		} catch (IOException e)
		{
			return false;
		} finally
		{
			try
			{
				in.close();
			} catch (IOException e)
			{
			}
		}

		return true;
	}

	public boolean save()
	{
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(file));

			writeMessageSet(out, WARNING_LABEL, warnings);
			writeMessageSet(out, ERROR_LABEL, errors);
			writeResult(out, result);

			out.flush();
			out.close();
		} catch (IOException e)
		{
			return false;
		}

		return true;
	}

	private void writeResult(BufferedWriter out, String result2)
			throws IOException
	{
		StringBuffer sb = new StringBuffer();
		sb.append(RESULT_LABEL);
		sb.append(":");
		sb.append("result");
		sb.append(":");
		sb.append(-1);
		sb.append(":");
		sb.append(-1);
		sb.append(",");
		sb.append(-1);
		sb.append(":");
		sb.append(result2.replace(':', '\''));
		out.write(sb.toString());
		out.newLine();
	}

	public void writeMessageSet(BufferedWriter out, String label,
			List<IMessage> list) throws IOException
	{
		for (IMessage m : list)
		{
			StringBuffer sb = new StringBuffer();
			sb.append(label);
			sb.append(":");
			sb.append(m.getResource());
			sb.append(":");
			sb.append(m.getNumber());
			sb.append(":");
			sb.append(m.getLine());
			sb.append(",");
			sb.append(m.getCol());
			sb.append(":");
			sb.append(m.getMessage().replace(':', '\''));
			out.write(sb.toString());
			out.newLine();
		}
	}

}
