/*
 * #%~
 * Overture GUI Builder
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
/*******************************************************************************
 * Copyright (c) 2009, 2013 Overture Team and others.
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
package org.overture.guibuilder.internal.ir;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * Extracts annotation from vdm files. No sanity check is made on anything besides the annotations. These checks are
 * assumed to have already been made. This is very, very dirty, and only to be considered a temporary solution
 * 
 * @author carlos
 */
public class AnnotationReader
{

	private AnnotationTable annotationTable = null;

	private enum SECTION
	{
		NONE, OPERATIONS, FUNCTIONS
	};

	/**
	 * Constructor
	 * 
	 * @param annotationTable
	 *            The table that the reader will fill
	 */
	public AnnotationReader(AnnotationTable annotationTable)
	{
		this.annotationTable = annotationTable;
	}

	/**
	 * Reads the vdm++ specification files.
	 * 
	 * @param files
	 *            List of files (path).
	 */
	public void readFiles(Vector<File> files)
	{
		for (File file : files)
		{
			readFile(file);
		}
	}

	//  DIRTY, VERY DIRTY. A proper parser should be used for this.
	// This method only does simple line by line reading.
	/**
	 * Reads a single plain text file containing a vdm++ specification
	 * 
	 * @param file
	 */
	private void readFile(File file)
	{
		try
		{
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String strLine;
			String className = null, opName = null, annotationName = null, value = null;
			boolean foundAnnotation = false;
			Pattern annotationPatern = Pattern.compile("^--@[\\w]+[=[\\w]+]?[\\s]*");
			// TODO: public Inicio() == (
			Pattern methodPatern = Pattern.compile("[\\s]*[\\w]*[\\s]*[\\w]+[\\s]*[:|(]");
			SECTION currentSection = SECTION.NONE;

			while ((strLine = reader.readLine()) != null)
			{

				if (strLine.contains("class "))
				{
					className = strLine.split(" ")[1];
				}

				// found a annotation
				//  We don't take into account multiple annotations for a class or method
				if (annotationPatern.matcher(strLine).lookingAt())
				{
					String[] result = strLine.split("=");
					annotationName = result[0].substring(3).trim();
					if (result.length > 1)
					{
						value = result[1];
					}
					foundAnnotation = true;
				}

				if (strLine.trim().equals("operations"))
				{
					currentSection = SECTION.OPERATIONS;
				} else if (strLine.trim().equals("function"))
				{
					currentSection = SECTION.FUNCTIONS;
				}

				// if we previously found a annotation, identify it with the proper element
				if (foundAnnotation)
				{
					if (strLine.contains("class "))
					{
						className = strLine.split(" ")[1];
						annotationTable.addClassAnnotation(className, annotationName, value);
						foundAnnotation = false;
					} else
					{
						if (!(currentSection == SECTION.NONE))
						{
							// in this line there's a method header
							if (methodPatern.matcher(strLine).lookingAt())
							{
								String split[] = strLine.split(" ");
								for (int i = 0; i < split.length; ++i)
								{
									if (split[i].equals("public")
											|| split[i].equals("private"))
									{
										opName = split[++i];
										// removing extra chars
										while (opName.indexOf(":") != -1
												|| opName.indexOf("(") != -1)
										{
											opName = opName.substring(0, opName.length() - 1);
										}

										annotationTable.addOpAnnotation(className, opName, annotationName, value);
										foundAnnotation = false;
										break;
									}
								} // for
							} // lookinAt()
							else if (strLine.contains("public")
									|| strLine.contains("private"))
							{ // Redundant
								String split[] = strLine.split(" ");
								if (split.length >= 2)
								{
									opName = split[2];
									if (opName.contains(":"))
									{
										opName = opName.substring(0, opName.indexOf(":") - 1);
									}
									annotationTable.addOpAnnotation(className, opName, annotationName, value);
									foundAnnotation = false;
								}
							}
						} // currentSection
					}
				} // foundAnnotation

			}
			reader.close();
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

}
