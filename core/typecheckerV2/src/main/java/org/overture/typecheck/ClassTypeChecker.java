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

package org.overture.typecheck;

import java.util.List;

import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.definitions.assistants.PDefinitionAssistantTC;
import org.overture.ast.definitions.assistants.SClassDefinitionAssistant;
import org.overture.typecheck.visitors.TypeCheckVisitor;

/**
 * A class to coordinate all class type checking processing.
 */

public class ClassTypeChecker extends TypeChecker
{
	/** The list of classes to check. */
	private final List<SClassDefinition>  classes;

	/**
	 * Create a type checker with the list of classes passed.
	 *
	 * @param classes
	 */

	public ClassTypeChecker(List<SClassDefinition> classes)
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
		boolean nothing = true;
		boolean hasSystem = false;

		for (SClassDefinition c1: classes)
		{
			for (SClassDefinition c2: classes)
			{
				if (c1 != c2 && c1.getName().equals(c2.getName()))
				{
					TypeChecker.report(3426, "Class " + c1.getName() + " duplicates " + c2.getName(), c1.getName().location);
				}
			}

			if (!c1.getIsTypeChecked()) nothing = false;

			if (c1 instanceof ASystemClassDefinition)
			{
				if (hasSystem)
				{
					TypeChecker.report(3294, "Only one system class permitted", c1.getLocation());
				}
				else
				{
					hasSystem = true;
				}
			}
		}

		if (nothing)
		{
			return;
		}

		Environment allClasses = new PublicClassEnvironment(classes);

		for (SClassDefinition c: classes)
		{
			if (!c.getIsTypeChecked())
			{
				SClassDefinitionAssistant.implicitDefinitions(c, allClasses);
			}
		}

    	for (SClassDefinition c: classes)
		{
			if (!c.getIsTypeChecked())
			{
    			try
    			{
    				Environment self = new PrivateClassEnvironment(c, allClasses);
    				SClassDefinitionAssistant.typeResolve(c, null, new TypeCheckInfo(self));
    			}
    			catch (TypeCheckException te)
    			{
    				report(3427, te.getMessage(), te.location);
    			}
			}
		}

		for (SClassDefinition c: classes)
		{
			if (!c.getIsTypeChecked())
			{
				SClassDefinitionAssistant.checkOver(c);
			}
		}
		TypeCheckVisitor tc = new TypeCheckVisitor();
	    for (Pass pass: Pass.values())
		{
        	for (SClassDefinition c: classes)
    		{
    			if (!c.getIsTypeChecked())
    			{
    				try
    				{
    					Environment self = new PrivateClassEnvironment(c, allClasses);
    	         		SClassDefinitionAssistant.typeCheckPass(c,pass, self,tc);
    				}
    				catch (TypeCheckException te)
    				{
    					report(3428, te.getMessage(), te.location);
    				}
    			}
    		}
		}

    	for (SClassDefinition c: classes)
		{
			if (!c.getIsTypeChecked())
			{
				SClassDefinitionAssistant.initializedCheck(c);
				PDefinitionAssistantTC.unusedCheck(c);
			}
		}
	}
}
