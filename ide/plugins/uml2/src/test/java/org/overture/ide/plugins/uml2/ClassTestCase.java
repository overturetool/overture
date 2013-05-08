package org.overture.ide.plugins.uml2;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Relationship;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.config.Settings;
import org.overture.ide.plugins.uml2.vdm2uml.Vdm2Uml;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class ClassTestCase extends TestCase
{

	private static final boolean preferAssociations = false;

	private Model convert(String model)
	{
		Settings.dialect = Dialect.VDM_PP;
		TypeCheckResult<List<SClassDefinition>> result = TypeCheckerUtil.typeCheckPp(model);
		List<SClassDefinition> input = result.result;

		assertTrue("Parse errors: " + result.parserResult.errors.toString(), result.parserResult.errors.isEmpty());
		assertTrue("Type Check errors" + result.errors.toString(), result.errors.isEmpty());

		Vdm2Uml vdm2uml = new Vdm2Uml(preferAssociations, false);
		vdm2uml.convert("Test Model", input);

		Model umlmodel = vdm2uml.getModel();
		assertNotNull("No model", umlmodel);
		return umlmodel;
	}

	public void assertIsSubClassOf(NamedElement cl, String superClass)
	{
		assertFalse("No generalizations", cl.getRelationships().isEmpty());

		boolean extendsSuper = false;
		for (Relationship r : cl.getRelationships())
		{
			if (r instanceof Generalization)
			{
				Generalization g = (Generalization) r;
				extendsSuper = g.getGeneral().getName().equals(superClass);
			}
		}
		assertTrue(cl.getName() + " does not extend " + superClass, extendsSuper);
	}

	public void testClass()
	{
		Model umlmodel = convert("class A \nend A");
		NamedElement classA = umlmodel.getOwnedMember("A");
		assertNotNull("No class", classA);
	}

	public void testInheritanceClass()
	{
		Model umlmodel = convert("class A \nend A\n class B is subclass of A\nend B");
		NamedElement classA = umlmodel.getOwnedMember("A");
		assertNotNull("No class", classA);

		NamedElement classB = umlmodel.getOwnedMember("B");
		assertNotNull("No class", classB);
		assertIsSubClassOf(classB,"A");
	}
}
