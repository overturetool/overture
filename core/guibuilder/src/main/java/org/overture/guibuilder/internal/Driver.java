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
package org.overture.guibuilder.internal;

/**
 * "Main" class.
 * 
 * @author carlos
 */
public class Driver
{

	public static void printUsage()
	{
		System.out.println("\nUsage:");
		System.out.println("java -jar <jar file> -p <vdm++ project folder>");
		System.out.println("The following options are available:\n");
		System.out.println("-i <folder containing xml ui descriptor files> - Use existing ui xml descriptors");
		System.out.println("-a  - Generate in annotation mode (no effect if using -i)");
		System.out.println("-s  - Save the generated xml files (no effect if using -i)");
	}

	/**
	 * @param args
	 *            Just a class to house the main function, and parse the arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{

		
	}



}
