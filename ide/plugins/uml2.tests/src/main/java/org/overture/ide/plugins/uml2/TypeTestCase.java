package org.overture.ide.plugins.uml2;

import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Namespace;
import org.junit.Test;
import org.overture.ide.plugins.uml2.vdm2uml.UmlTypeCreator;


public class TypeTestCase extends BaseUmlTestCase
{
	String baseSpec = "class A\n types\n t = %s\n end A";
	
	private void primitiveTest(String typeName)
	{
		Model model = convert(String.format(baseSpec, typeName));
		Classifier classA = getClass(model, "A");
		model.getPackagedElement(UmlTypeCreator.BASIC_VDM_TYPES_PACKAGE);
		getClass((Namespace)model.getPackagedElement(UmlTypeCreator.BASIC_VDM_TYPES_PACKAGE), typeName);
		Classifier classt = getClass(classA, "t");
		assertIsSubClassOf(classt, typeName);
	}
	
	@Test
	public void testBoolType()
	{
		primitiveTest("bool");
	}
	
	@Test
	public void testCharType()
	{
		primitiveTest("char");
	}
	
	@Test
	public void testTokenType()
	{
		primitiveTest("token");
	}
	
	@Test
	public void testIntType()
	{
		primitiveTest("int");
	}
	
	@Test
	public void testNatType()
	{
		primitiveTest("nat");
	}
	
	@Test
	public void testNat1Type()
	{
		primitiveTest("nat1");
	}
	
	@Test
	public void testRatType()
	{
		primitiveTest("rat");
	}
	
	@Test
	public void testRealType()
	{
		primitiveTest("real");
	}
}
