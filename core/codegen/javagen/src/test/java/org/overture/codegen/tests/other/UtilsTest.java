package org.overture.codegen.tests.other;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.overture.codegen.vdm2java.JavaCodeGenUtil;

public class UtilsTest
{
	@Test
	public void testValidPackage1()
	{
		assertTrue(JavaCodeGenUtil.isValidJavaPackage("org.overture.codegen.tests"));
	}

	@Test
	public void testStartWithCapLetter()
	{
		assertTrue(JavaCodeGenUtil.isValidJavaPackage("Hello.hello"));
	}

	@Test
	public void testEmptyPackage()
	{
		assertFalse(JavaCodeGenUtil.isValidJavaPackage(""));
	}

	@Test
	public void testTwoDotsInPackage()
	{
		assertFalse(JavaCodeGenUtil.isValidJavaPackage("org..overture"));
	}

	@Test
	public void testNoDotsPackage()
	{
		assertTrue(JavaCodeGenUtil.isValidJavaPackage("myPackage"));
	}

	@Test
	public void spaceAroundPackage()
	{
		assertTrue(JavaCodeGenUtil.isValidJavaPackage("   org.overture    "));
	}

	@Test
	public void spaceInPackage()
	{
		assertFalse(JavaCodeGenUtil.isValidJavaPackage("org. overture"));
	}

	@Test
	public void testNumberNamePackage()
	{
		assertFalse(JavaCodeGenUtil.isValidJavaPackage("2be.or.not.to.be"));
	}
}
