package org.overture.codegen.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.overture.codegen.utils.GeneralCodeGenUtils;

public class UtilsTest
{
	@Test
	public void testValidPackage1()
	{
		assertTrue(GeneralCodeGenUtils.isValidJavaPackage("org.overture.codegen.tests"));
	}
	
	@Test
	public void testStartWithCapLetter()
	{
		assertTrue(GeneralCodeGenUtils.isValidJavaPackage("Hello.hello"));
	}
	
	@Test
	public void testEmptyPackage()
	{
		assertFalse(GeneralCodeGenUtils.isValidJavaPackage(""));
	}
	
	@Test
	public void testTwoDotsInPackage()
	{
		assertFalse(GeneralCodeGenUtils.isValidJavaPackage("org..overture"));
	}
	
	@Test
	public void testNoDotsPackage()
	{
		assertTrue(GeneralCodeGenUtils.isValidJavaPackage("myPackage"));
	}
	
	@Test
	public void spaceAroundPackage()
	{
		assertTrue(GeneralCodeGenUtils.isValidJavaPackage("   org.overture    "));
	}
	
	@Test
	public void spaceInPackage()
	{
		assertFalse(GeneralCodeGenUtils.isValidJavaPackage("org. overture"));
	}
	
	@Test
	public void testNumberNamePackage()
	{
		assertFalse(GeneralCodeGenUtils.isValidJavaPackage("2be.or.not.to.be"));
	}
}
