/*
 * #%~
 * UML2 Translator Tests
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
package org.overture.ide.plugins.uml2;

import java.util.List;




import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Relationship;
import org.junit.Assert;
import org.junit.Test;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;
import org.overture.ide.plugins.uml2.vdm2uml.Vdm2Uml;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class ClassTestCase 
{

	private static final boolean preferAssociations = false;

	private Model convert(String model)
	{
		Settings.dialect = Dialect.VDM_PP;
		TypeCheckResult<List<SClassDefinition>> result = TypeCheckerUtil.typeCheckPp(model);
		List<SClassDefinition> input = result.result;

		Assert.assertTrue("Parse errors: " + result.parserResult.errors.toString(), result.parserResult.errors.isEmpty());
		Assert.assertTrue("Type Check errors" + result.errors.toString(), result.errors.isEmpty());

		Vdm2Uml vdm2uml = new Vdm2Uml(preferAssociations, false);
		vdm2uml.convert("Test Model", input);

		Model umlmodel = vdm2uml.getModel();
		Assert.assertNotNull("No model", umlmodel);
		return umlmodel;
	}

	public void assertIsSubClassOf(NamedElement cl, String superClass)
	{
		Assert.assertFalse("No generalizations", cl.getRelationships().isEmpty());

		boolean extendsSuper = false;
		for (Relationship r : cl.getRelationships())
		{
			if (r instanceof Generalization)
			{
				Generalization g = (Generalization) r;
				extendsSuper = g.getGeneral().getName().equals(superClass);
			}
		}
		Assert.assertTrue(cl.getName() + " does not extend " + superClass, extendsSuper);
	}

	@Test
	public void testClass()
	{
		Model umlmodel = convert("class A \nend A");
		NamedElement classA = umlmodel.getOwnedMember("A");
		Assert.assertNotNull("No class", classA);
	}

	@Test
	public void testInheritanceClass()
	{
		Model umlmodel = convert("class A \nend A\n class B is subclass of A\nend B");
		NamedElement classA = umlmodel.getOwnedMember("A");
		Assert.assertNotNull("No class", classA);

		NamedElement classB = umlmodel.getOwnedMember("B");
		Assert.assertNotNull("No class", classB);
		assertIsSubClassOf(classB,"A");
	}
}
