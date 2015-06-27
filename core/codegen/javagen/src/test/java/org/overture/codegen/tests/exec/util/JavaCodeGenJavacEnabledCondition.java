/*
 * #%~
 * Integration of the ProB Solver for VDM
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
package org.overture.codegen.tests.exec.util;

import org.overture.test.framework.ConditionalIgnoreMethodRule.IgnoreCondition;

public class JavaCodeGenJavacEnabledCondition implements IgnoreCondition
{
	@Override
	public boolean isIgnored()
	{
		String javagenJavac = System.getProperty("javagen.javac");

		if (javagenJavac != null && javagenJavac.trim().toLowerCase().equals("true"))
		{
			return false;
		}
		return true;
	}
}
