/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj.typechecker;

import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;


/**
 * The abstract root of all type checker classes.
 */

abstract public class TypeChecker
{
	private static List<VDMError> errors = new Vector<VDMError>();
	private static List<VDMWarning> warnings = new Vector<VDMWarning>();
	private static VDMError lastError = null;
	private static final int MAX = 100;

	public TypeChecker()
	{
		clearErrors();
	}

	abstract public void typeCheck();

	public static void report(int number, String problem, LexLocation location)
	{
		lastError = new VDMError(number, problem, location);
		errors.add(lastError);

		if (errors.size() >= MAX-1)
		{
			errors.add(new VDMError(10, "Too many type checking errors", location));
			throw new InternalException(10, "Too many type checking errors");
		}
	}

	public static void warning(int number, String problem, LexLocation location)
	{
		VDMWarning warning = new VDMWarning(number, problem, location);
		warnings.add(warning);
	}

	public static void detail(String tag, Object obj)
	{
		if (lastError != null)
		{
			lastError.add(tag + ": " + obj);
		}
	}

	public static void detail2(String tag1, Object obj1, String tag2, Object obj2)
	{
		detail(tag1, obj1);
		detail(tag2, obj2);
	}

	public static void clearErrors()
	{
		errors.clear();
		warnings.clear();
	}

	public static int getErrorCount()
	{
		return errors.size();
	}

	public static int getWarningCount()
	{
		return warnings.size();
	}

	public static List<VDMError> getErrors()
	{
		return errors;
	}

	public static List<VDMWarning> getWarnings()
	{
		return warnings;
	}

	public static void printErrors(PrintWriter out)
	{
		for (VDMError e: errors)
		{
			out.println(e.toString());
		}
	}

	public static void printWarnings(PrintWriter out)
	{
		for (VDMWarning w: warnings)
		{
			out.println(w.toString());
		}
	}
}
