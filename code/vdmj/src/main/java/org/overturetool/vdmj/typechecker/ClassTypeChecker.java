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

import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.definitions.Definition;

/**
 * A class to coordinate all class type checking processing.
 */

public class ClassTypeChecker extends TypeChecker
{
	/** The list of classes to check. */
	private final ClassList classes;

	/**
	 * Create a type checker with the list of classes passed.
	 *
	 * @param classes
	 */

	public ClassTypeChecker(ClassList classes)
	{
		super();
		this.classes = classes;
	}

	/**
	 * Perform type checking across all classes in the list.
	 */

	@Override
	public void typeCheck()
	{
		for (Definition c1: classes)
		{
			for (Definition c2: classes)
			{
				if (c1 != c2 && c1.name.equals(c2.name))
				{
					TypeChecker.report(3426, "Class " + c1.name + " duplicates " + c2.name, c1.name.location);
				}
			}
		}

		Environment allClasses = new PublicClassEnvironment(classes);

		for (ClassDefinition c: classes)
		{
    		c.implicitDefinitions(allClasses);
		}

    	for (ClassDefinition c: classes)
		{
			try
			{
				Environment self = new PrivateClassEnvironment(c, allClasses);
				c.typeResolve(self);
			}
			catch (TypeCheckException te)
			{
				report(3427, te.getMessage(), te.location);
			}
		}

		for (ClassDefinition c: classes)
		{
    		c.checkOver();
		}

	    for (Pass pass: Pass.values())
		{
        	for (ClassDefinition c: classes)
    		{
				try
				{
					Environment self = new PrivateClassEnvironment(c, allClasses);
	         		c.typeCheckPass(pass, self);
				}
				catch (TypeCheckException te)
				{
					report(3428, te.getMessage(), te.location);
				}
    		}
		}

    	for (ClassDefinition c: classes)
		{
    		c.unusedCheck();
		}
	}
}
