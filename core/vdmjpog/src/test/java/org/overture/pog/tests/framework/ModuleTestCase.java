package org.overture.pog.tests.framework;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Vector;

import org.overture.typechecker.tests.framework.BasicTypeCheckTestCase;
import org.overturetool.vdmj.Release;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.lex.LexException;
import org.overturetool.vdmj.messages.VDMError;
import org.overturetool.vdmj.messages.VDMWarning;
import org.overturetool.vdmj.modules.Module;
import org.overturetool.vdmj.modules.ModuleList;
import org.overturetool.vdmj.pog.POContextStack;
import org.overturetool.vdmj.pog.ProofObligation;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.syntax.ParserException;
import org.overturetool.vdmj.typechecker.ClassTypeChecker;
import org.overturetool.vdmj.typechecker.ModuleTypeChecker;
import org.overturetool.vdmj.typechecker.TypeChecker;
import org.overturetool.vdmj.util.Base64;

public class ModuleTestCase extends BasicTypeCheckTestCase
{

	public static final String tcHeader = "-- TCErrors:";

	File file;
	String name;
	String content;
	String expectedType;
	ParserType parserType;
	private boolean showWarnings = true;
	private boolean generateResultOutput = true;
	private ProofObligationList proofObligations = new ProofObligationList();

	public ModuleTestCase()
	{
		super("test");

	}

	public ModuleTestCase(File file)
	{
		super("test");
		this.parserType = ParserType.Module;
		this.file = file;
		this.content = file.getName();
	}

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();
		Settings.dialect = Dialect.VDM_SL;
		Settings.release = Release.VDM_10;
		TypeChecker.clearErrors();
	}

	public void test() throws ParserException, LexException, IOException
	{
		if (content != null)
		{
			moduleTc(content);
		}
	}

	private void moduleTc(String expressionString) throws ParserException,
			LexException, IOException
	{

		if (file.getName().endsWith("vpp"))
			Settings.dialect = Dialect.VDM_PP;

		System.out.flush();
		System.err.flush();
		insertTCHeader();

		// printFile(file);
		boolean parseIsOk = false;
		TypeChecker tc = null;
		Runnable doPoG;
		switch (Settings.dialect)
		{
			case VDM_SL:
			{
				final ModuleList modules = parse(ParserType.Module, file);
				tc = new ModuleTypeChecker(modules);
				doPoG = new Runnable()
				{

					public void run()
					{
						for (Module m : modules)
							proofObligations.addAll(m.getProofObligations());
					}
				};
			}
				break;
			case VDM_PP:
			{
				final ClassList classes = parse(ParserType.Class, file);
				tc = new ClassTypeChecker(classes);
				doPoG = new Runnable()
				{
					public void run()
					{
						POContextStack ctxt = new POContextStack();
						for (ClassDefinition cd : classes)
							proofObligations.addAll(cd.getProofObligations(ctxt));
					}
				};
			}
				break;
			default:
				throw new RuntimeException("Unspecified dialect: "
						+ Settings.dialect + " among: " + Dialect.VDM_SL
						+ " and " + Dialect.VDM_PP);
		}

		tc.typeCheck();
		parseIsOk = TypeChecker.getErrorCount() == 0;

		String errorMessages = null;
		if (TypeChecker.getErrorCount() > 0)
		{
			// perrs += reader.getErrorCount();
			StringWriter s = new StringWriter();
			TypeChecker.printErrors(new PrintWriter(s));// new
														// PrintWriter(System.out));
			errorMessages = "\n" + s.toString() + "\n";
			System.out.println(s.toString());
		}

		// assertEquals(errorMessages, 0, TypeChecker.getErrorCount());

		if (showWarnings && TypeChecker.getWarningCount() > 0)
		{
			// perrs += reader.getErrorCount();
			StringWriter s = new StringWriter();
			TypeChecker.printWarnings(new PrintWriter(s));// new
															// PrintWriter(System.out));
			// String warningMessages = "\n" + s.toString() + "\n";
			System.out.println(s.toString());
		}

		if (parseIsOk)
		{
			doPoG.run();
		}

		printTCHeader();

	}

	private void printFile(File file) throws IOException
	{
		FileReader in = new FileReader(file);
		BufferedReader br = new BufferedReader(in);
		String line = null;
		while ((line = br.readLine()) != null)
		{
			System.out.println(line);
		}

	}

	private void insertTCHeader() throws IOException
	{
		if (!file.exists())
			return;
		FileReader in = new FileReader(file);
		BufferedReader br = new BufferedReader(in);

		Vector<String> lines = new Vector<String>();
		String line = null;

		while ((line = br.readLine()) != null)
		{
			lines.add(line);
		}

		in.close();

		if (lines.size() > 0)
		{
			String firstLine = lines.get(0);
			if (!firstLine.startsWith(tcHeader))
			{
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				out.write(tcHeader);
				out.newLine();
				for (String string : lines)
				{
					out.write(string);
					out.write("\n");
				}
				out.flush();
				out.close();
			}
		}

	}

	private void printTCHeader() throws IOException
	{
		if (!file.exists())
			return;
		FileReader in = new FileReader(file);
		BufferedReader br = new BufferedReader(in);

		Vector<String> lines = new Vector<String>();
		String line = null;

		while ((line = br.readLine()) != null)
		{
			lines.add(line);
		}

		in.close();

		if (lines.size() > 1)
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(getTCHeader());
			out.newLine();
			int i = 1;
			for (; i < lines.size() - 1; i++)
			{
				out.write(lines.get(i));
				out.write("\n");
			}
			out.write(lines.get(i));
			out.flush();
			out.close();

		}

	}

	private String getTCHeader()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(tcHeader);

		for (VDMError error : TypeChecker.getErrors())
		{
			sb.append(" ERROR:");
			sb.append(error.number);
			sb.append(":");
			sb.append(error.location.startLine);
			sb.append(",");
			sb.append(error.location.startPos);

		}

		for (VDMWarning error : TypeChecker.getWarnings())
		{
			sb.append(" WARNING:");
			sb.append(error.number);
			sb.append(":");
			sb.append(error.location.startLine);
			sb.append(",");
			sb.append(error.location.startPos);

		}

		for (ProofObligation po : proofObligations)
		{
			String poString = "|" + po.location.startLine + ":"
					+ po.location.startPos + " " + po.name + "," + po.value
					+ "," + po.kind + "," + po.proof + "," + po.status + "|";
			StringBuffer poAsB64 = Base64.encode(poString.getBytes(Charset.forName("UTF-8")));
			sb.append(" PROOFOBLIGATION: ");
			sb.append(poAsB64);
		}

		return sb.toString();

	}

	public String getName()
	{

		return file.getName();
	}

}
