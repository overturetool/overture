package org.overture.ide.debug.utils;
import java.util.jar.*;
import java.util.*;
import java.io.*;

public class PackageUtils
{

	private static boolean debug = false;

	public static List<String> getClasseNamesInPackage(String jarName,
			String packageName)
	{
		ArrayList<String> classes = new ArrayList<String>();

		if(packageName!=null)
		packageName = packageName.replaceAll("\\.", "/");
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
				if ((packageName==null ||jarEntry.getName().startsWith(packageName))
						&& (jarEntry.getName().endsWith(".class")))
				{
					
					String className=jarEntry.getName().replaceAll("/", "\\.");
					className=className.substring(0, className.length()-".class".length());
					
					if(className.endsWith("$1"))
					{
						continue;
					}
					
					className = className.replace('$', '.');
					
					if (debug)
					{
						System.out.println("Found "+className);
					}
					
					classes.add(className);
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}finally
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
