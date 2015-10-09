package org.overture.vdm2jml.tests;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.vdm2jml.util.NameGen;

public class NameGenTests
{
	public static final String MSG = "Got unexpected name suggestion from name generator";
	
	private NameGen nameGen;
	
	@Before
	public void init()
	{
		ADefaultClassDeclCG testClass = new ADefaultClassDeclCG();
		
		AFieldDeclCG field = new AFieldDeclCG();
		field.setName("field");
		
		testClass.getFields().add(field);
		
		this.nameGen = new NameGen(testClass); 
	}
	
	@Test
	public void notTaken()
	{
		Assert.assertEquals(MSG, "x", nameGen.getName("x"));
	}
	
	@Test
	public void suggestSameNameTwice()
	{
		nameGen.getName("x");
		Assert.assertEquals(MSG, "x_1", nameGen.getName("x"));
	}
	
	@Test
	public void suggestSameNameThreeTimes()
	{
		nameGen.getName("x");
		nameGen.getName("x");
		Assert.assertEquals(MSG, "x_2", nameGen.getName("x"));
	}
	
	@Test
	public void suggestField()
	{
		Assert.assertEquals(MSG, "field_1",  nameGen.getName("field")); 
	}
	
	@Test
	public void addExtraName()
	{
		nameGen.addName("x");
		Assert.assertEquals(MSG, "x_1", nameGen.getName("x"));
	}
}
