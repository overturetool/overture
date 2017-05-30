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

package org.overture.typechecker;

import java.io.PrintWriter;
import java.util.List;
import java.util.Vector;

import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.messages.InternalException;
import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMMessage;
import org.overture.parser.messages.VDMWarning;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.assistant.TypeCheckerAssistantFactory;

/**
 * The abstract root of all type checker classes.
 */

abstract public class TypeChecker
{
	private static boolean suppress=false;

	public interface IStatusListener
	{
		void report(VDMError error);

		void warning(VDMWarning warning);
	}

	private static List<VDMError> errors = new Vector<VDMError>();
	private static List<VDMWarning> warnings = new Vector<VDMWarning>();
	private static VDMMessage lastMessage = null;
	private static final int MAX = 200;

	final protected ITypeCheckerAssistantFactory assistantFactory;

	static List<IStatusListener> listners = new Vector<IStatusListener>();

	/**
	 * VDM-only constructor. <b>NOT</b> for use by extensions.
	 */
	public TypeChecker()
	{
		clearErrors();
		this.assistantFactory = new TypeCheckerAssistantFactory();
	}

	public TypeChecker(ITypeCheckerAssistantFactory factory)
	{
		clearErrors();
		this.assistantFactory = factory;
	}

	abstract public void typeCheck();

	public static void suppressErrors(boolean sup)
	{
		suppress =sup;
	}

	public static void report(int number, String problem, ILexLocation location)
	{
		if (suppress) return;
		VDMError error = new VDMError(number, problem, location);
		// System.out.println(error.toString());
		errors.add(error);
		lastMessage = error;

		for (IStatusListener listner : listners)
		{
			listner.report(error);
		}

		if (errors.size() >= MAX - 1)
		{
			errors.add(new VDMError(10, "Too many type checking errors", location));
			throw new InternalException(10, "Too many type checking errors");
		}
	}

	public static void warning(int number, String problem, ILexLocation location)
	{
		if (suppress) return;
		VDMWarning warning = new VDMWarning(number, problem, location);
		warnings.add(warning);
		lastMessage = warning;

		for (IStatusListener listner : listners)
		{
			listner.warning(warning);
		}
	}

	public static void detail(String tag, Object obj)
	{
		if (lastMessage != null)
		{
			lastMessage.add(tag + ": " + obj);
		}
	}

	public static void detail2(String tag1, Object obj1, String tag2,
			Object obj2)
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
		for (VDMError e : errors)
		{
			out.println(e.toString());
		}
	}

	public static void printWarnings(PrintWriter out)
	{
		for (VDMWarning w : warnings)
		{
			out.println(w.toString());
		}
	}

	public static void addStatusListner(IStatusListener listner)
	{
		listners.add(listner);
	}

	public static void removeStatusListner(IStatusListener listner)
	{
		listners.remove(listner);
	}
}
