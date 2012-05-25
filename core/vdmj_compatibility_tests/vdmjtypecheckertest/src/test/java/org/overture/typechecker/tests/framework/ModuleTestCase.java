package org.overture.typechecker.tests.framework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.overture.vdmjUtils.VdmjCompatibilityUtils;
import org.overturetool.test.util.XmlResultReaderWritter;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.ModuleTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;

public class ModuleTestCase extends BasicTypeCheckTestCase {

	public static final String tcHeader = "-- TCErrors:";

	File file;
	String name;
	String content;
	String expectedType;
	ParserType parserType;
	private boolean showWarnings = true;

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


	private void insertTCHeader() throws IOException {
		if (!file.exists())
			return;
		FileReader in = new FileReader(file);
		BufferedReader br = new BufferedReader(in);

		Vector<String> lines = new Vector<String>();
		String line = null;

		while ((line = br.readLine()) != null) {
			lines.add(line);
		}

		in.close();

		if (lines.size() > 0) {
			String firstLine = lines.get(0);
			if (!firstLine.startsWith(tcHeader)) {
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				out.write(tcHeader);
				out.newLine();
				for (String string : lines) {
					out.write(string);
					out.write("\n");
				}
				out.flush();
				out.close();
			}
		}

	}

	private void printTCHeader() throws IOException {
		if (!file.exists())
			return;
		FileReader in = new FileReader(file);
		BufferedReader br = new BufferedReader(in);

		Vector<String> lines = new Vector<String>();
		String line = null;

		while ((line = br.readLine()) != null) {
			lines.add(line);
		}

		in.close();

		if (lines.size() > 1) {						
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				out.write(getTCHeader());
				out.newLine();
				int i = 1;
				for(; i < lines.size() - 1; i++)
				{
					out.write(lines.get(i));
					out.write("\n");
				}
				out.write(lines.get(i));
				out.flush();
				out.close();
			
		}

	}

	private String getTCHeader() {
		StringBuffer sb = new StringBuffer();
		sb.append(tcHeader);
		
		for (VDMError error : TypeChecker.getErrors()) {
			sb.append(" ERROR:");
			sb.append(error.number);
			sb.append(":");
			sb.append(error.location.startLine);
			sb.append(",");
			sb.append(error.location.startPos);

		}

		for (VDMWarning error : TypeChecker.getWarnings()) {
			sb.append(" WARNING:");
			sb.append(error.number);
			sb.append(":");
			sb.append(error.location.startLine);
			sb.append(",");
			sb.append(error.location.startPos);

		}
		
		return sb.toString();

	}
	
	@Override
	public String getName() {
		
		return file.getName();
	}

}
