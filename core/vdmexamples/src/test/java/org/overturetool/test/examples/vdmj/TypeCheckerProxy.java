/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
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
package org.overturetool.test.examples.vdmj;

import java.util.HashSet;
import java.util.Set;

import org.overturetool.test.examples.vdmj.VdmjFactories.IMessageConverter;
import org.overturetool.test.framework.results.IMessage;
import org.overturetool.test.framework.results.Result;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.typechecker.TypeChecker;

public class TypeCheckerProxy
{
	public static <R>Result<R> typeCheck(TypeChecker checker, IMessageConverter factory)
	{
		checker.typeCheck();
		Set<IMessage> warnings = new HashSet<IMessage>();
		Set<IMessage> errors = new HashSet<IMessage>();
		for (VDMError m : TypeChecker.getErrors())
		{
			errors.add(factory.convertMessage(m));
		}

		for (VDMWarning m : TypeChecker.getWarnings())
		{
			warnings.add(factory.convertMessage(m));
		}

		return new Result<R>(null, warnings, errors);
	}
}
