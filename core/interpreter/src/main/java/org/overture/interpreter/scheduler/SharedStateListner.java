/*******************************************************************************
 * Copyright (c) 2010, 2011 DESTECS Team and others.
 *
 * DESTECS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DESTECS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DESTECS.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The DESTECS web-site: http://destecs.org/
 *******************************************************************************/
package org.overture.interpreter.scheduler;

import java.util.HashSet;
import java.util.Set;

import org.overture.ast.expressions.AVariableExp;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.ast.statements.AAssignmentStm;
import org.overture.ast.statements.PStateDesignator;
import org.overture.interpreter.values.UpdatableValue;
import org.overture.interpreter.values.Value;

/**
 * Special DESTECS contribution to allow experimental optimizations to be tried out.
 * 
 * @author kela
 */
public class SharedStateListner
{
	public static interface IdentityChecker
	{
		/**
		 * This method checks if a name needs a continuous time step before it is evaluated. Example is: a := b where b
		 * must be updated before it is evaluated.
		 * 
		 * @param name
		 * @return
		 */
		boolean requiresCheck(ILexNameToken name);

		/**
		 * This method checks if a change made in an assignment statement must be synchronized with the continuous time
		 * simulator. Example is a := b, where a must be updated in the CT side as soon as the change is visible
		 * internally to VDM.
		 * 
		 * @param target
		 * @return
		 */
		boolean reuiresCheck(PStateDesignator target);
	}

	private static final Set<ILexLocation> values = new HashSet<>();
	private static Boolean autoIncrementTime = true;
	private static IdentityChecker checker = null;

	public static void beforeVariableReadDuration(AVariableExp var)
	{
		if (checker != null && checker.requiresCheck(var.getName()))
		{
			synchronized (values)
			{
				autoIncrementTime = false;
			}
		}
	}

	public static void beforeAssignmentSet(AAssignmentStm assignStmt,
			Value oldval, Value newval)
	{
		if (checker != null && checker.reuiresCheck(assignStmt.getTarget()))
		{
			synchronized (values)
			{
				values.add(assignStmt.getTarget().getLocation());
			}
		}
	}

	public static void variableChanged(UpdatableValue updatableValue,
			ILexLocation location)
	{
		synchronized (values)
		{
			if (values.contains(location))
			{
				autoIncrementTime = false;
				values.remove(location);
			}
		}
	}

	public static boolean isAutoIncrementTime()
	{
		synchronized (values)
		{
			return autoIncrementTime;
		}
	}

	public static void resetAutoIncrementTime()
	{
		synchronized (values)
		{
			autoIncrementTime = true;
		}
	}

	public static void setIdentityChecker(IdentityChecker checker)
	{
		SharedStateListner.checker = checker;
	}

}
