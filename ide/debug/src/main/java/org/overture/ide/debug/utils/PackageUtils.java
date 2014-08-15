/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class PackageUtils
{

	private static boolean debug = false;

	public static List<String> getClasseNamesInPackage(String jarName,
			String packageName)
	{
		ArrayList<String> classes = new ArrayList<String>();

		if (packageName != null)
		{
			packageName = packageName.replaceAll("\\.", "/");
		}
		if (debug)
		{
			System.out.println("Jar " + jarName + " looking for " + packageName);
		}

		JarInputStream jarFile = null;
		try
		{
			jarFile = new JarInputStream(new FileInputStream(jarName));
			JarEntry jarEntry;

			while (true)
			{
				jarEntry = jarFile.getNextJarEntry();
				if (jarEntry == null)
				{
					break;
				}
				if ((packageName == null || jarEntry.getName().startsWith(packageName))
						&& jarEntry.getName().endsWith(".class"))
				{

					String className = jarEntry.getName().replaceAll("/", "\\.");
					className = className.substring(0, className.length()
							- ".class".length());

					if (className.endsWith("$1"))
					{
						continue;
					}

					className = className.replace('$', '.');

					if (debug)
					{
						System.out.println("Found " + className);
					}

					classes.add(className);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				jarFile.close();
			} catch (IOException e)
			{
			}
		}
		return classes;
	}

	/**
*
*/
	public static void main(String[] args)
	{
		List<String> list = PackageUtils.getClasseNamesInPackage("C:\\overture\\overture_gitAST\\core\\parser\\target\\parser-2.0.1-SNAPSHOT.jar", null);
		System.out.println(list);
		/*
		 * output : Jar C:/j2sdk1.4.1_02/lib/mail.jar looking for com/sun/mail/handlers Found
		 * com.sun.mail.handlers.text_html.class Found com.sun.mail.handlers.text_plain.class Found
		 * com.sun.mail.handlers.text_xml.class Found com.sun.mail.handlers.image_gif.class Found
		 * com.sun.mail.handlers.image_jpeg.class Found com.sun.mail.handlers.multipart_mixed.class Found
		 * com.sun.mail.handlers.message_rfc822.class [com.sun.mail.handlers.text_html.class,
		 * com.sun.mail.handlers.text_xml.class, com .sun.mail.handlers.image_jpeg.class, ,
		 * com.sun.mail.handlers.message_rfc822.class]
		 */
	}
}
