package org.overture.typechecker.tests.framework;

import java.io.File;

import org.overturetool.test.framework.ResultTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class TypeCheckTestCase extends ResultTestCase<Object> {

	public TypeCheckTestCase()
	{
		super();

	}

	public TypeCheckTestCase(File file)
	{
		super(file);
	}
	
	
	public void encondeResult(Object result, Document doc, Element resultElement) {
		
	}

	public Object decodeResult(Node node) {
		return null;
	}

	@Override
	protected boolean compareResult(Object expected, Object actual) {
		return true;
	}

	@Override
	protected File createResultFile(String filename) {
		return new File(filename + ".result");
	}

	@Override
	protected File getResultFile(String filename) {
		return new File(filename + ".result");
	}

	

}
