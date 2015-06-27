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
package org.overture.codegen.tests.exec;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.overture.codegen.utils.GeneratedModule;
import org.overture.codegen.vdm2java.JavaCodeGen;

public class TestUtils
{
	public static List<File> getFiles(File file, String extension)
	{
		List<File> files = new Vector<File>();
		for (File f : file.listFiles())
		{
			if (f.isDirectory())
			{
				files.addAll(getFiles(f, extension));
			} else if (f.getName().toLowerCase().endsWith(extension))
			{
				files.add(f);
			}
		}

		Collections.sort(files, new FileComparator());

		return files;
	}

	public static List<File> getTestInputFiles(File file)
	{
		List<File> files = new Vector<File>();
		for (File f : file.listFiles())
		{
			Collections.sort(files, new FileComparator());
			if (f.isDirectory())
			{
				files.addAll(getTestInputFiles(f));
			} else
			{
				String name = f.getName();

				if (name.endsWith(".vdmsl") || name.endsWith(".vdmpp")
						|| name.endsWith(".vdmrt") || name.endsWith("vpp")
						|| name.endsWith("vsl"))
				{
					files.add(f);
				}
			}
		}

		Collections.sort(files, new FileComparator());

		return files;
	}

	public static String getJavaModuleName(StringBuffer moduleContent)
	{
		int moduleIdx = moduleContent.indexOf("class");

		if (moduleIdx == -1)
		{
			moduleIdx = moduleContent.indexOf("interface");
		}

		int startClassIdx = moduleContent.indexOf(" ", moduleIdx);

		int endTemplateClassIdx = moduleContent.indexOf("<", 1 + startClassIdx);
		int endClassIdx = moduleContent.indexOf(" ", 1 + startClassIdx);

		if (endTemplateClassIdx > 0 && endTemplateClassIdx < endClassIdx)
		{
			endClassIdx = endTemplateClassIdx;
		}

		String className = moduleContent.substring(1 + startClassIdx, endClassIdx);

		return className;
	}

	public static List<StringBuffer> readJavaModulesFromResultFile(File file, String rootPackage)
			throws IOException
	{
		final char DELIMITER_CHAR = '#';

		FileInputStream input = new FileInputStream(file);

		List<StringBuffer> classes = new LinkedList<StringBuffer>();

		StringBuffer data = new StringBuffer();
		int c = 0;
		while ((c = input.read()) != -1)
		{
			if (c == DELIMITER_CHAR)
			{
				while (input.read() == DELIMITER_CHAR)
				{
				}
				
				String dataStr = data.toString();
				
				String QUOTES_INDICATOR = "*Quotes*";
				if(dataStr.trim().startsWith(QUOTES_INDICATOR))
				{
					String[] quotes = dataStr.replace(QUOTES_INDICATOR, "").trim().split(",");
					
					JavaCodeGen javaCodeGen = new JavaCodeGen();
					javaCodeGen.getJavaSettings().setJavaRootPackage(rootPackage);
					
					for(String q : quotes)
					{
						javaCodeGen.getInfo().registerQuoteValue(q);
					}
					
					List<GeneratedModule> genQuotes = javaCodeGen.generateJavaFromVdmQuotes();
					
					for(GeneratedModule q : genQuotes)
					{
						classes.add(new StringBuffer(q.getContent()));
					}
					
				} else
				{
					String NAME_VIOLATIONS_INDICATOR = "*Name Violations*";
					if (!dataStr.trim().startsWith(NAME_VIOLATIONS_INDICATOR))
					{
						classes.add(data);
					}
				}

				data = new StringBuffer();
			} else
			{
				data.append((char) c);
			}
		}
		input.close();

		return classes;
	}
}
