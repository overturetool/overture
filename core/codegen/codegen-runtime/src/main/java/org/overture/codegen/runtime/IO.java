/*
 * #%~
 * VDM Code Generator Runtime
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
package org.overture.codegen.runtime;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;


public class IO
{

	private static File BaseDIr = new File(".").getParentFile();

	private static final String NOT_SUPPORTED_MSG = "Operation is currently not supported";

	public static <p> boolean writeval(p val)
	{

		String text = formatArg(val);

		System.out.print(text);
		System.out.flush();

		return true;
	}

	public static <p> boolean fwriteval(String filename, p val, Object fdir)
	{
		File file = getFile(filename);
		String text = Utils.toString(val);
		String fdirStr = Utils.toString(fdir); // <start>|<append>

		try
		{
			FileOutputStream fos = new FileOutputStream(file, fdirStr.equals("<append>"));

			fos.write(text.getBytes(Charset.defaultCharset().name()));
			fos.close();
		} catch (IOException e)
		{
			return false;
		}

		return true;
	}

	public static <p> boolean fwriteval(VDMSeq filename, p val, Object fdir)
	{
		return fwriteval(SeqUtil.toStr(filename), val, fdir);
	}

	public static <p> Tuple freadval(String filename)
	{

		throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
	}

	public static <p> Tuple freadval(VDMSeq filename)
	{

		throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
	}

	private static File getFile(String fileStr)
	{
		String path = fileStr.replace('/', File.separatorChar);
		File file = new File(path);

		if (!file.isAbsolute())
		{
			file = new File(BaseDIr, path);
		}

		/**
		 * The VDM IO library requires the parent structure of a file to exist before the file is accessed. However,
		 * when a model is code generated to some location the parent folder structure for a file will not exists. To
		 * overcome this (and for convenience) the Java code generator runtime library tries to create the folder
		 * structure
		 */

		file.getAbsoluteFile().getParentFile().mkdirs();

		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			} catch (IOException e)
			{
			}
		}

		return file;
	}

	protected static File getFile(VDMSeq fval)
	{
		throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
	}

	/*
	 * Unlike the VDMPP/VDM-RT versions, echo, fecho (including the overloaded
	 * versions) and ferror are declared *static* in the code-generation runtime.
	 * This ensures that they also work for code-generated VDM-SL models (where
	 * everything is static)
	 */
	public static boolean echo(String text)
	{
		return fecho("[]", text, null);
	}

	public static boolean echo(VDMSeq text)
	{
		return fecho("[]", SeqUtil.toStr(text), null);
	}

	public static boolean fecho(String filename, String text, Object fdir)
	{

		if (filename.equals("[]"))
		{
			System.out.print(text);
			System.out.flush();
		} else
		{
			try
			{
				File file = getFile(filename);
				FileOutputStream fos = new FileOutputStream(file, fdir != null
						&& fdir.getClass().getName().endsWith("quotes.appendQuote"));

				fos.write(text.getBytes(Charset.defaultCharset().name()));
				fos.close();
			} catch (IOException e)
			{
				return false;
			}
		}

		return true;
	}

	public static boolean fecho(VDMSeq filename, VDMSeq text, Object fdir)
	{

		return fecho(filename.toString(), text.toString(), fdir);
	}

	public static String ferror()
	{
		throw new UnsupportedOperationException(NOT_SUPPORTED_MSG);
	}

	public static void print(Object arg)
	{

		System.out.printf("%s", formatArg(arg));
		System.out.flush();
	}

	public static void println(Object arg)
	{

		System.out.printf("%s", formatArg(arg));
		System.out.printf("%s", "\n");
		System.out.flush();

	}

	public static void printf(String format, VDMSeq args)
	{

		System.out.printf(format, formatList(args));
		System.out.flush();
	}

	public static void printf(VDMSeq seq, VDMSeq args)
	{

		System.out.printf(seq.toString(), formatList(args));
		System.out.flush();
	}

	public static String sprintf(String format, List<Object> args)
	{

		return String.format(format, args.toArray());
	}

	public static VDMSeq sprintf(VDMSeq seq, VDMSeq args)
	{

		throw new UnsupportedOperationException("sprintf is only supported for formats of type String");
	}

	@SuppressWarnings("unchecked")
	private static Object[] formatList(VDMSeq args)
	{
		for (int i = 0; i < args.size(); i++)
		{
			Object arg = args.get(i);
			args.set(i, formatArg(arg));
		}

		return args.toArray();
	}

	private static String formatArg(Object arg)
	{
		return Utils.toString(arg);
	}
}
