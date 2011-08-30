package org.overture.typechecker.tests.framework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;

import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.Environment;
import org.overturetool.vdmj.typechecker.FlatCheckedEnvironment;
import org.overturetool.vdmj.typechecker.ModuleTypeChecker;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.typechecker.TypeComparator;
import org.overturetool.vdmj.types.Type;

public class ClassTestCase extends BasicTypeCheckTestCase {

	public static final String tcHeader = "-- TCErrors:";

	File file;
	String name;
	String content;
	String expectedType;
	ParserType parserType;
	private boolean showWarnings = true;
	private boolean generateResultOutput = true;

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
		TypeChecker.clearErrors();
	}

	public void test() throws ParserException, LexException, IOException {
		if (content != null) {
			moduleTc(content);
		}
	}

	private void moduleTc(String expressionString) throws ParserException,
			LexException, IOException {
		System.out.flush();
		System.err.flush();
		insertTCHeader();

		printFile(file);
		ClassList classes = parse(ParserType.Class, file);

		ClassTypeChecker moduleTC = new ClassTypeChecker(classes);
		moduleTC.typeCheck();

		String errorMessages = null;
		if (TypeChecker.getErrorCount() > 0) {
			// perrs += reader.getErrorCount();
			StringWriter s = new StringWriter();
			TypeChecker.printErrors(new PrintWriter(s));// new
														// PrintWriter(System.out));
			errorMessages = "\n" + s.toString() + "\n";
			System.out.println(s.toString());
		}

		//assertEquals(errorMessages, 0, TypeChecker.getErrorCount());

		if (showWarnings && TypeChecker.getWarningCount() > 0) {
			// perrs += reader.getErrorCount();
			StringWriter s = new StringWriter();
			TypeChecker.printWarnings(new PrintWriter(s));// new
															// PrintWriter(System.out));
			// String warningMessages = "\n" + s.toString() + "\n";
			System.out.println(s.toString());
		}

		printTCHeader();

	}

	private void printFile(File file) throws IOException {
		FileReader in = new FileReader(file);
		BufferedReader br = new BufferedReader(in);
		String line = null;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
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
