package org.overture.pog.tests.framework;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.overture.vdmjUtils.VdmjCompatibilityUtils;
import org.overturetool.test.util.XmlResultReaderWritter;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.ModuleTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

public class ModuleTestCase extends BasicPogTestCase {

	public static final String tcHeader = "-- TCErrors:";

	File file;
	String name;
	String content;
	String expectedType;
	ParserType parserType;

	public ModuleTestCase() {
		super("test");
		//this.name = "blabla";
	}

	public ModuleTestCase(File file) {
		super("test");
		this.parserType = ParserType.Module;
		this.file = file;
		this.content = file.getName();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
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
	
		ModuleList modules = parse(ParserType.Module, file);
		
		
		ModuleTypeChecker moduleTC = new ModuleTypeChecker(modules);
		moduleTC.typeCheck();

		ProofObligationList pos = new ProofObligationList();
		for (Module module : modules) {
			pos.addAll(module.getProofObligations());
		}
		
		System.out.println(pos);
		File resultFile = new File(file.getAbsolutePath() + ".result");
		XmlResultReaderWritter xmlResult = new XmlResultReaderWritter(resultFile);
		
		xmlResult.setResult("proof_obligation", VdmjCompatibilityUtils.convertPOsToResult(pos,file,"vdmj type checker"));
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
