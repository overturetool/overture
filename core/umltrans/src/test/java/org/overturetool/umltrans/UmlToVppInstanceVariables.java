package org.overturetool.umltrans;

import junit.framework.TestCase;

import org.overturetool.umltrans.basic.UmlToVppTestRunner;

public class UmlToVppInstanceVariables extends TestCase
{
	public UmlToVppInstanceVariables()
	{
		super("UML to VPP - Instance Variable Test");
	}

	public void test_simpleClass() throws Exception
	{
		UmlToVppTestRunner tc = new UmlToVppTestRunner("simpleClass.xml");
		tc.test(this);
	}

	public void test_basicInstanceVariablesClass() throws Exception
	{
		UmlToVppTestRunner tc = new UmlToVppTestRunner("instanceVariables",
				"basicInstanceVariablesClass.xml");
		tc.test(this);
	}

	public void test_basicDefaultInstanceVariablesClass() throws Exception
	{
		UmlToVppTestRunner tc = new UmlToVppTestRunner("instanceVariables",
				"basicDefaultInstanceVariablesClass.xml");
		tc.test(this);
	}

	public void test_objectDefaultRefInstanceVariablesClass() throws Exception
	{
		UmlToVppTestRunner tc = new UmlToVppTestRunner("instanceVariables",
				"objectDefaultRefInstanceVariablesClass.xml");
		tc.test(this);
	}

	public void test_objectRefInstanceVariablesClass() throws Exception
	{
		UmlToVppTestRunner tc = new UmlToVppTestRunner("instanceVariables",
				"objectRefInstanceVariablesClass.xml");
		tc.test(this);
	}
}
