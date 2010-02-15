package org.overturetool.tools.packworkspace.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.overturetool.tools.packworkspace.ProjectPacker;
import org.overturetool.vdmj.ExitStatus;
import org.overturetool.vdmj.Settings;
import org.overturetool.vdmj.VDMJ;
import org.overturetool.vdmj.VDMPP;
import org.overturetool.vdmj.VDMRT;
import org.overturetool.vdmj.VDMSL;
import org.overturetool.vdmj.lex.Dialect;
import org.overturetool.vdmj.messages.Console;
import org.overturetool.vdmj.messages.StderrRedirector;
import org.overturetool.vdmj.messages.StdoutRedirector;
import org.overturetool.vdmj.pog.ProofObligation;
import org.overturetool.vdmj.pog.ProofObligationList;
import org.overturetool.vdmj.runtime.Interpreter;
import org.overturetool.vdmj.values.Value;

public class ProjectTester
{
	public static boolean skipInterpreter= true;
	VDMJ controller;
	File reportLocation;
	final String DEVIDER_LINE = "\n\n======================================================================\n\n";
	ExitStatus statusParse = null;
	ExitStatus statusTypeCheck = null;
	ExitStatus statusPo = null;
	ExitStatus statusInterpreter = null;
	Integer poCount = 0;

	boolean isFaild = false;

	enum Phase {
		SyntaxCheck, TypeCheck, PO, Interpretation
	}

	public ProjectTester(File reportLocation) {
		this.reportLocation = reportLocation;
		if (!reportLocation.exists())
			reportLocation.mkdirs();
	}

	public String test(ProjectPacker project) throws IOException
	{
		System.out.print(addFixedSize("\nTesting: "
				+ project.getSettings().getName(), 28)
				+ " => ");
		switch (project.getDialect())
		{
		case VDM_PP:
			controller = new VDMPP();
			break;
		case VDM_RT:
			controller = new VDMRT();
			break;
		case VDM_SL:
			controller = new VDMSL();
			break;
		}

		Settings.dialect = project.getDialect();
		Settings.dynamictypechecks = project.getSettings()
				.getDynamicTypeChecks();
		Settings.invchecks = project.getSettings().getInvChecks();
		Settings.postchecks = project.getSettings().getPostChecks();
		Settings.prechecks = project.getSettings().getPreChecks();
		Settings.release = project.getSettings().getLanguageVersion();

		StringBuilder sb = new StringBuilder();

		File dir = new File(reportLocation, project.getSettings().getName());
		if (!dir.exists())
			dir.mkdirs();

		project.getSettings().createReadme(new File(dir, "Settings.txt"));
		setConsole(project.getSettings().getName(), Phase.SyntaxCheck);

		System.out.print("Syntax check...");
		statusParse = controller.parse(project.getSpecFiles());
		if (statusParse == ExitStatus.EXIT_OK)
		{
			System.out.print("Type check...");
			setConsole(project.getSettings().getName(), Phase.TypeCheck);
			statusTypeCheck = controller.typeCheck();
			try
			{
				System.out.print("PO...");
				setConsole(project.getSettings().getName(), Phase.PO);
				ProofObligationList pos = controller.getInterpreter()
						.getProofObligations();
				pos.renumber();
				poCount = pos.size();
				for (ProofObligation proofObligation : pos)
				{
					Console.out.println("Number "+proofObligation.number+": \n\n"+proofObligation.toString()+DEVIDER_LINE);
					statusPo = ExitStatus.EXIT_OK;
				}
			} catch (Exception e)
			{
				e.printStackTrace(Console.err);
				statusPo = ExitStatus.EXIT_ERRORS;
			}

			for (String entryPoint : project.getSettings().getEntryPoints())
			{
				if (entryPoint != null && entryPoint.length() > 0
						&& statusTypeCheck == ExitStatus.EXIT_OK &&!skipInterpreter)
				{
					try
					{
						System.out.print("Interpreter test...");
						setConsole(project.getSettings().getName(),
								Phase.Interpretation);
						Interpreter i = controller.getInterpreter();
						i.init(null);
						if (project.getDialect() == Dialect.VDM_SL)
							i.setDefaultName(entryPoint.substring(0,
									entryPoint.indexOf('`')));
						Value value = i.execute(entryPoint, null);
						Console.out.println(value);
						Console.out.flush();
						statusInterpreter = ExitStatus.EXIT_OK;
					} catch (Exception e)
					{
						Console.err.write(e.toString());
						Console.err.flush();
						statusInterpreter = ExitStatus.EXIT_ERRORS;

					}
					Console.out.write(DEVIDER_LINE);
					Console.err.write(DEVIDER_LINE);
				}
			}
		}

		switch (project.getSettings().getExpectedResult())
		{
		case NO_CHECK:
			isFaild = false;
			break;
		case NO_ERROR_SYNTAX:
			isFaild = statusParse != ExitStatus.EXIT_OK;
			break;
		case NO_ERROR_TYPE_CHECK:
			isFaild = statusTypeCheck != ExitStatus.EXIT_OK
					|| statusParse != ExitStatus.EXIT_OK;
			break;
		case NO_ERROR_PO:
			isFaild = statusTypeCheck != ExitStatus.EXIT_OK
					|| statusParse != ExitStatus.EXIT_OK
					|| statusPo != ExitStatus.EXIT_OK;
			break;
		case NO_ERROR_INTERPRETER:
			isFaild = statusInterpreter != ExitStatus.EXIT_OK
					|| statusTypeCheck != ExitStatus.EXIT_OK
					|| statusParse != ExitStatus.EXIT_OK
					|| statusPo != ExitStatus.EXIT_OK;
			break;
		}

		if (!isFaild)
			sb.append(HtmlTable.makeCell(HtmlPage.makeLink(project.getSettings()
					.getName(),
					project.getSettings().getName() + "/Settings.txt")));
		else
			sb.append(makeCell(ExitStatus.EXIT_ERRORS,
					HtmlPage.makeLink(project.getSettings().getName(),
							project.getSettings().getName() + "/Settings.txt")));

		if (statusParse != null)
			sb.append(makeCell(statusParse, statusParse.name()
					+ " "
					+ getLinks(project.getSettings().getName(),
							Phase.SyntaxCheck)));
		else
			sb.append(HtmlTable.makeCell(""));

		if (statusTypeCheck != null)
			sb.append(makeCell(statusTypeCheck,
					statusTypeCheck.name()
							+ " "
							+ getLinks(project.getSettings().getName(),
									Phase.TypeCheck)));
		else
			sb.append(HtmlTable.makeCell(""));
		
		if (statusPo!= null)
			sb.append(makeCell(statusPo,
					statusPo.name()
							+ " "
							+ getLinks(project.getSettings().getName(),
									Phase.PO)));
		else
			sb.append(HtmlTable.makeCell(""));

		if (statusInterpreter != null)
			sb.append(makeCell(statusInterpreter, statusInterpreter.name()
					+ " "
					+ getLinks(project.getSettings().getName(),
							Phase.Interpretation)));
		else
			sb.append(HtmlTable.makeCell(""));

		return HtmlTable.makeRow(sb.toString());
	}

	private static String makeCell(ExitStatus status, String text)
	{
		switch (status)
		{
		case EXIT_ERRORS:
			return HtmlTable.makeCell(text, HtmlTable.STYLE_CLASS_FAILD);
		case EXIT_OK:
			return HtmlTable.makeCell(text, HtmlTable.STYLE_CLASS_OK);
		case RELOAD:
			return HtmlTable.makeCell(text);

		}
		return text;
	}

	private String getLinks(String projectName, Phase phase)
	{
		File out = new File(reportLocation, projectName + "/" + phase
				+ "Out.txt");
		File err = new File(reportLocation, projectName + "/" + phase
				+ "Err.txt");
		String value = "";
		if (logFileExists(out))
			value += HtmlPage.makeLink("Out", projectName + "/" + phase
					+ "Out.txt");
		if (logFileExists(err))
			value += "/"
					+ HtmlPage.makeLink("Err", projectName + "/" + phase
							+ "Err.txt");

		if (value.startsWith("/"))
			value = value.substring(1);

		return value;
	}

	public boolean logFileExists(File file)
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			BufferedReader input = new BufferedReader(new FileReader(file));
			try
			{

				String line = null;
				while ((line = input.readLine()) != null)
				{
					sb.append(line);
				}
			} finally
			{
				input.close();
			}
			if (sb.toString().trim().length() == 0)
			{
				file.delete();
				return false;
			}
		} catch (IOException ex)
		{
			ex.printStackTrace();
		}
		return true;
	}

	private void setConsole(String projectName, Phase phase) throws IOException
	{
		File projectDir = new File(reportLocation, projectName);
		projectDir.mkdirs();
		Console.out = new StdoutRedirector(new FileWriter(new File(projectDir,
				phase + "Out.txt"), false));
		Console.err = new StderrRedirector(new FileWriter(new File(projectDir,
				phase + "Err.txt"), false));
	}

	public boolean isSyntaxCorrect()
	{
		return statusParse == null || statusParse == ExitStatus.EXIT_OK;
	}

	public boolean isTypeCorrect()
	{
		return statusTypeCheck == null || statusTypeCheck == ExitStatus.EXIT_OK;
	}

	public boolean isInterpretationSuccessfull()
	{
		return statusInterpreter == null
				|| statusInterpreter == ExitStatus.EXIT_OK;
	}

	public boolean isFaild()
	{
		return isFaild;
	}

	public Integer getPoCount()
	{
		return poCount;
	}
	private String addFixedSize(String text, int size)
	{
		while (text.length() < size)
		{
			text += " ";
		}
		return text;

	}
}
