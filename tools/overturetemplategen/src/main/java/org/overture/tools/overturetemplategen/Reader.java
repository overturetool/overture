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
package org.overture.tools.overturetemplategen;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Reader
{
	private String current;
	BufferedReader reader = null;

	public TemplateList read(File file) throws Exception
	{
		TemplateList templates = new TemplateList();

		try
		{
			reader = new BufferedReader(new FileReader(file));

			readNextLine();
			while (current != null && current.startsWith(Template.NAME_TOKEN))
			{
				templates.add(readTemplate());
				readNextLine();
			}

		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
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
return templates;
	}

	private String readNextLine() throws IOException
	{
		current = reader.readLine();
		if (current != null && current.startsWith("--"))
		{
			readNextLine();
			return current;
		}
		return current;
	}

	private Template readTemplate() throws Exception
	{
		String name = current.substring(Template.NAME_TOKEN.length()).replace('\t', ' ').trim();
		readNextLine();
		if(!current.startsWith(Template.DESCRIPTION_TOKEN))
			throw new Exception("Wrong line: "+current);
		
		String description = current.substring(Template.DESCRIPTION_TOKEN.length()).replace('\t', ' ').trim();
		
		readNextLine();
		if(!current.startsWith(Template.DIALECT_TOKEN))
			throw new Exception("Wrong line: "+current);
		
		String dialect = current.substring(Template.DIALECT_TOKEN.length()).trim();
		
		String template = readTemplateSyntax();
		
		return new Template(name, description, dialect, template);
		
	}

	private String readTemplateSyntax() throws Exception
	{
		readNextLine();
		if(!current.startsWith(Template.TEMPLATE_BEGIN_TOKEN))
			throw new Exception("Expected "+Template.TEMPLATE_BEGIN_TOKEN+" got "+current);
		
		StringBuilder sb = new StringBuilder();
		
		while(!current.startsWith(Template.TEMPLATE_END_TOKEN))
		{
			readNextLine();
			if(current.startsWith(Template.TEMPLATE_END_TOKEN))
				break;
			sb.append(current+"\n");
		}
		
		if(sb.length()>0)
		{
			sb.deleteCharAt(sb.length()-1);
		}
		return sb.toString();
	}
}
