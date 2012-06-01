package org.overture.typechecker.tests.framework;

import java.io.File;

import org.overturetool.test.framework.ResultTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class TypeCheckTestCase extends ResultTestCase<Boolean> {

	public TypeCheckTestCase()
	{
		super();

	}

	public TypeCheckTestCase(File file)
	{
		super(file);
	}
	
	
	public void encondeResult(Boolean result, Document doc, Element resultElement) {
		
	}

	public Boolean decodeResult(Node node) {
		return null;
	}

	@Override
	protected boolean assertEqualResults(Boolean expected, Boolean actual) {
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
