package org.overture.typechecker.tests.framework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.overture.vdmjUtils.VdmjCompatibilityUtils;
import org.overturetool.test.util.XmlResultReaderWritter;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

public class ClassTestCase extends BasicTypeCheckTestCase {

	public static final String tcHeader = "-- TCErrors:";

	File file;
	String name;
	String content;
	String expectedType;
	ParserType parserType;

	public ClassTestCase() {
		super("test");

	}

	public ClassTestCase(File file) {
		super("test");
		this.parserType = ParserType.Module;
		this.file = file;
		this.content = file.getName();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Settings.dialect = Dialect.VDM_PP;
		Settings.release = Release.VDM_10;
		TypeChecker.clearErrors();
	}

	public void test() throws ParserException, LexException, IOException {
		if (content != null) {
			moduleTc(content);
		}
	}

	private void moduleTc(String expressionString) throws ParserException,
			LexException, IOException {


		
		ClassList classes = parse(ParserType.Class, file);

		ClassTypeChecker moduleTC = new ClassTypeChecker(classes);
		moduleTC.typeCheck();

		File resultFile = new File(file.getAbsolutePath() + ".result");
		XmlResultReaderWritter xmlResult = new XmlResultReaderWritter(resultFile);
		
		xmlResult.setResult("type_checker", VdmjCompatibilityUtils.convertToResult(file,"vdmj type checker"));
		try {
			xmlResult.saveInXml();			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}

	}
	


	
	@Override
	public String getName() {
		
		return file.getName();
	}

}
