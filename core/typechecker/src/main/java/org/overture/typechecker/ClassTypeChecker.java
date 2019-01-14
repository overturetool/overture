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

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.annotations.Annotation;
import org.overture.ast.annotations.PAnnotation;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.typechecker.Pass;
import org.overture.ast.types.PType;
import org.overture.typechecker.annotations.TCAnnotation;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;
import org.overture.typechecker.visitor.TypeCheckVisitor;

/**
 * A class to coordinate all class type checking processing.
 */

public class ClassTypeChecker extends TypeChecker
{
	/** The list of classes to check. */
	protected final List<SClassDefinition> classes;

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
	 * Create a type checker with the list of classes passed.
	 * 
	 * @param classes
	 * @param factory
	 */

	public ClassTypeChecker(List<SClassDefinition> classes,
			ITypeCheckerAssistantFactory factory)
	{
		super(factory);
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

		for (SClassDefinition c1 : classes)
		{
			c1.setType(assistantFactory.createSClassDefinitionAssistant().getType(c1));
			for (SClassDefinition c2 : classes)
			{
				if (c1 != c2 && c1.getName().equals(c2.getName()))
				{
					TypeChecker.report(3426, "Class " + c1.getName()
							+ " duplicates " + c2.getName(), c1.getName().getLocation());
				}
			}

			if (!c1.getTypeChecked())
			{
				nothing = false;
			}

			if (c1 instanceof ASystemClassDefinition)
			{
				if (hasSystem)
				{
					TypeChecker.report(3294, "Only one system class permitted", c1.getLocation());
				} else
				{
					hasSystem = true;
				}
			}
		}

		if (nothing)
		{
			return;
		}

		Environment allClasses = getAllClassesEnvronment();

		for (SClassDefinition c : classes)
		{
			if (!c.getTypeChecked())
			{
				assistantFactory.createSClassDefinitionAssistant().implicitDefinitions(c, allClasses);
			}
		}

		for (SClassDefinition c : classes)
		{
			if (!c.getTypeChecked())
			{
				try
				{
					Environment self = new PrivateClassEnvironment(assistantFactory, c, allClasses);
					assistantFactory.createSClassDefinitionAssistant().typeResolve(c, null, new TypeCheckInfo(assistantFactory, self));
				} catch (TypeCheckException te)
				{
					report(3427, te.getMessage(), te.location);
					
    				if (te.extras != null)
    				{
    					for (TypeCheckException e: te.extras)
    					{
    						report(3427, e.getMessage(), e.location);
    					}
    				}
				} catch (AnalysisException te)
				{
					report(3431, te.getMessage(), null);// FIXME: internal error
				}
			}
		}

		for (SClassDefinition c : classes)
		{
			if (!c.getTypeChecked())
			{
				assistantFactory.createSClassDefinitionAssistant().checkOver(c);
			}
		}

		// Initialise any annotations
		Annotation.init();

		for (SClassDefinition c: classes)
		{
			for (PAnnotation annotation: c.getAnnotations())
			{
				if (annotation.getImpl() instanceof TCAnnotation)
				{
					TCAnnotation impl = (TCAnnotation)annotation.getImpl();
					impl.tcBefore(c, null);
				}
			}
		}

		QuestionAnswerAdaptor<TypeCheckInfo, PType> tc = getTypeCheckVisitor();
		for (Pass pass : Pass.values())
		{
			for (SClassDefinition c : classes)
			{
				if (!c.getTypeChecked())
				{
					try
					{
						Environment self = new PrivateClassEnvironment(assistantFactory, c, allClasses);
						assistantFactory.createSClassDefinitionAssistant().typeCheckPass(c, pass, self, tc);
					} catch (TypeCheckException te)
					{
						report(3428, te.getMessage(), te.location);
						
	    				if (te.extras != null)
	    				{
	    					for (TypeCheckException e: te.extras)
	    					{
	    						report(3428, e.getMessage(), e.location);
	    					}
	    				}
					} catch (AnalysisException te)
					{
						report(3431, te.getMessage(), null);// FIXME: internal error
					}
				}
			}
		}
		
		for (SClassDefinition c: classes)
		{
			for (PAnnotation annotation: c.getAnnotations())
			{
				if (annotation.getImpl() instanceof TCAnnotation)
				{
					TCAnnotation impl = (TCAnnotation)annotation.getImpl();
					impl.tcAfter(c, null);
				}
			}
		}

		List<PDefinition> allDefs = new Vector<PDefinition>();

		for (SClassDefinition c : classes)
		{
			if (!c.getTypeChecked())
			{
				assistantFactory.createSClassDefinitionAssistant().initializedCheck(c);
				assistantFactory.createPDefinitionAssistant().unusedCheck(c);
				allDefs.addAll(c.getDefinitions());
			}
		}
    	
    	cyclicDependencyCheck(allDefs);
	}

	protected Environment getAllClassesEnvronment()
	{
		return new PublicClassEnvironment(assistantFactory, classes, null);
	}

	protected QuestionAnswerAdaptor<TypeCheckInfo, PType> getTypeCheckVisitor()
	{
		return new TypeCheckVisitor();
	}
}
