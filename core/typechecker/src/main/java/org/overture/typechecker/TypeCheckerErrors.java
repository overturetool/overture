/*
 * #%~
 * The VDM Type Checker
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
package org.overture.typechecker;

import org.overture.ast.intf.lex.ILexLocation;

public class TypeCheckerErrors
{

	public static void report(int number, String msg, ILexLocation location,
			Object node)
	{
		TypeChecker.report(number, msg, location);
	}

	public static void concern(boolean serious, int number, String msg,
			ILexLocation location, Object node)
	{

		if (serious)
		{
			TypeChecker.report(number, msg, location);
		} else
		{
			TypeChecker.warning(number, msg, location);
		}
	}

	public static void detail(String tag, Object obj)
	{
		TypeChecker.detail(tag, obj);
	}

	public static void detail(boolean serious, String tag, Object obj)
	{
		if (serious)
		{
			TypeChecker.detail(tag, obj);
		}
	}

	public static void detail2(boolean serious, String tag1, Object obj1,
			String tag2, Object obj2)
	{
		if (serious)
		{
			TypeChecker.detail2(tag1, obj1, tag2, obj2);
		}
	}

	public static void detail2(String tag1, Object obj1, String tag2,
			Object obj2)
	{
		TypeChecker.detail2(tag1, obj1, tag2, obj2);
	}

	public static void warning(int number, String msg, ILexLocation loc,
			Object node)
	{
		TypeChecker.warning(number, msg, loc);
	}

}
