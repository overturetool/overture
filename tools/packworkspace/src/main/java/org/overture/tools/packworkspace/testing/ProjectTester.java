/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.tools.packworkspace.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Vector;

import org.overture.tools.packworkspace.ProjectPacker;
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

public class ProjectTester
{
	public static boolean FORCE_RERUN = false;
	public static boolean skipInterpreter = false;
	VDMJ controller;
	File reportLocation;
	final String DEVIDER_LINE = "\n\n======================================================================\n\n";
	ExitStatus statusParse = null;
	ExitStatus statusTypeCheck = null;
	ExitStatus statusPo = null;
	ExitStatus statusInterpreter = null;
	Integer poCount = 0;

	File pdf = null;

	boolean isFaild = false;
	private File modelDir = null;

	enum Phase
	{
		SyntaxCheck, TypeCheck, PO, Interpretation, Latex
	}

	public ProjectTester(File reportLocation)
	{
		this.reportLocation = reportLocation;
		if (!reportLocation.exists())
			reportLocation.mkdirs();
	}

	public String test(ProjectPacker project) throws IOException
	{
		LatexBuilder latex = null;
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
		Settings.dynamictypechecks = project.getSettings().getDynamicTypeChecks();
		Settings.invchecks = project.getSettings().getInvChecks();
		Settings.postchecks = project.getSettings().getPostChecks();
		Settings.prechecks = project.getSettings().getPreChecks();
		Settings.release = project.getSettings().getLanguageVersion();
		if (project.getSettings().getEncoding() != null
				&& project.getSettings().getEncoding().length() > 0)
		{
			if (Charset.isSupported(project.getSettings().getEncoding()))
				VDMJ.filecharset = project.getSettings().getEncoding();
			else
				System.err.println("Charset not supported: "
						+ project.getSettings().getEncoding());
		}
		StringBuilder sb = new StringBuilder();

		File dir = new File(reportLocation, project.getSettings().getName());
		if (!dir.exists())
			dir.mkdirs();

		modelDir = new File(dir, "model");
		if (!modelDir.exists())
			modelDir.mkdirs();

		project.getSettings().createReadme(new File(dir, "Settings.txt"));
		project.packTo(dir, modelDir);
		// FileUtils.writeFile(FileUtils.readFile("/web/default.asp"), new File(dir, "model/default.asp"));

		setConsole(project.getSettings().getName(), Phase.SyntaxCheck);

		CrcTable tmpCrcTable = new CrcTable(dir, false);
		for (File file : project.getSpecFiles())
		{
			tmpCrcTable.add(file.getAbsolutePath());
		}
		CrcTable readCrcTable = new CrcTable(dir);
		boolean runCheck = true;
		if (tmpCrcTable.equals(readCrcTable))
			runCheck = false;

		if (runCheck || FORCE_RERUN)
		{
			tmpCrcTable.saveCheckSums();
			System.out.print("Syntax..");
			statusParse = controller.parse(project.getSpecFiles());
			if (statusParse == ExitStatus.EXIT_OK)
			{
				System.out.print("Type..");
				setConsole(project.getSettings().getName(), Phase.TypeCheck);
				statusTypeCheck = controller.typeCheck();
				try
				{
					System.out.print("PO..");
					setConsole(project.getSettings().getName(), Phase.PO);
					ProofObligationList pos = controller.getInterpreter().getProofObligations();
					pos.renumber();
					poCount = pos.size();
					if (poCount == 0)
						statusPo = ExitStatus.EXIT_OK;
					else
					{
						for (ProofObligation proofObligation : pos)
						{
							Console.out.println("Number "
									+ proofObligation.number + ": \n\n"
									+ proofObligation.toString() + DEVIDER_LINE);
							statusPo = ExitStatus.EXIT_OK;
						}
					}
				} catch (Exception e)
				{
					e.printStackTrace(Console.err);
					statusPo = ExitStatus.EXIT_ERRORS;
				}

				try
				{
					latex = new LatexBuilder(project);
					if (project.getSettings().getTexDocument() != null
							&& project.getSettings().getTexDocument().length() > 0)
						latex.setAlternativeDocumentFileName(project.getSettings().getTexDocument());
					System.out.print("Doc..");
					latex.build(reportLocation, controller.getInterpreter(), project.getSettings().getTexAuthor());
				} catch (Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				int intryPointCount = 0;
				for (String entryPoint : project.getSettings().getEntryPoints())
				{
					if (entryPoint != null && entryPoint.length() > 0
							&& statusTypeCheck == ExitStatus.EXIT_OK
							&& !skipInterpreter)
					{
						try
						{

							if (intryPointCount == 0)
								System.out.println("Runtime:");
							intryPointCount++;

							System.out.println("\tTesting Entrypoint: "
									+ entryPoint);

							setConsole(project.getSettings().getName(), Phase.Interpretation);
							Interpreter i = controller.getInterpreter();
							i.init(null);
							if (project.getDialect() == Dialect.VDM_SL)
								i.setDefaultName(entryPoint.substring(0, entryPoint.indexOf('`')));
							// Value value = i.execute(entryPoint, null);
							statusInterpreter = runInterpreter(project, entryPoint);
							// Console.out.println(value);

						} catch (Exception e)
						{
							Console.err.write(e.toString());
							Console.err.flush();
							Console.out.flush();
							statusInterpreter = ExitStatus.EXIT_ERRORS;

						} catch (Error e)
						{
							Console.err.write(e.toString());
							Console.err.flush();
							Console.out.flush();
							statusInterpreter = ExitStatus.EXIT_ERRORS;
						} finally
						{
							Console.out.flush();
						}
						Console.out.write(DEVIDER_LINE);
						Console.err.write(DEVIDER_LINE);
					}
				}
				storeStatus(dir);
			}
		} else
		{
			System.out.print("Skipping..");
			loadStatus(dir);
			latex = new LatexBuilder(project);
			latex.setDocumentFileName(project.getSettings().getName() + ".tex");
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

		String settingsLink = HtmlPage.makeLink("(?)", project.getSettings().getName()
				+ "/Settings.txt");

		String modelLink = HtmlPage.makeLink(project.getSettings().getName(), project.getSettings().getName()
				+ "/model");

		if (!isFaild)
			sb.append(HtmlTable.makeCell(modelLink + " " + settingsLink));
		else
			sb.append(makeCell(ExitStatus.EXIT_ERRORS, modelLink + " "
					+ settingsLink));

		if (statusParse != null)
			sb.append(makeCell(statusParse, HtmlPage.getName(statusParse)
					+ " "
					+ getLinks(project.getSettings().getName(), Phase.SyntaxCheck)));
		else
			sb.append(HtmlTable.makeCell(""));

		if (statusTypeCheck != null)
			sb.append(makeCell(statusTypeCheck, HtmlPage.getName(statusTypeCheck)
					+ " "
					+ getLinks(project.getSettings().getName(), Phase.TypeCheck)));
		else
			sb.append(HtmlTable.makeCell(""));

		if (statusPo != null)
			sb.append(makeCell(statusPo, HtmlPage.getName(statusPo) + " "
					+ getLinks(project.getSettings().getName(), Phase.PO)));
		else
			sb.append(HtmlTable.makeCell(""));

		if (statusInterpreter != null)
			sb.append(makeCell(statusInterpreter, HtmlPage.getName(statusInterpreter)
					+ " "
					+ getLinks(project.getSettings().getName(), Phase.Interpretation)));
		else
			sb.append(HtmlTable.makeCell(""));

		if (latex != null)
		{
			pdf = latex.getPdfFile();
			String pdfPath = project.getSettings().getName() + "/latex/"
					+ project.getSettings().getName() + ".pdf";

			if (!latex.isFinished())
			{
				try
				{
					Thread.sleep(1000);
				} catch (InterruptedException e)
				{
				}
			}

			boolean pdfFileExists = new File(dir, "latex/"
					+ project.getSettings().getName() + ".pdf").exists();

			ExitStatus pdfExists = pdfFileExists ? ExitStatus.EXIT_OK
					: ExitStatus.EXIT_ERRORS;

			sb.append(makeCell(pdfExists, HtmlPage.getName(pdfExists)
					+ " "
					+ getLinks(project.getSettings().getName() + "/latex", Phase.Latex)
					+ (pdfExists == ExitStatus.EXIT_OK ? " "
							+ HtmlPage.makeLink("Pdf", pdfPath) : "")));
		} else
			sb.append(HtmlTable.makeCell(""));

		return HtmlTable.makeRow(sb.toString());
	}

	public boolean isBuild(String name)
	{
		if (name.length() > 5)
		{
			return new File(name.substring(0, name.length() - 4) + ".pdf").exists();
		}
		return false;
	}

	private void loadStatus(File dir)
	{
		TestStatus info = new TestStatus(dir, true);
		statusParse = info.statusParse;
		statusTypeCheck = info.statusTypeCheck;
		statusPo = info.statusPo;
		statusInterpreter = info.statusInterpreter;
		poCount = info.poCount;
		isFaild = info.isFaild;

	}

	private void storeStatus(File dir)
	{
		TestStatus info = new TestStatus(dir, false);
		info.statusParse = statusParse;
		info.statusTypeCheck = statusTypeCheck;
		info.statusPo = statusPo;
		info.statusInterpreter = statusInterpreter;
		info.poCount = poCount;
		info.isFaild = isFaild;
		info.save();

	}

	@SuppressWarnings("deprecation")
	private ExitStatus runInterpreter(ProjectPacker project, String entryPoint)
			throws IOException, InterruptedException
	{

		List<String> command = new ArrayList<String>();
		command.add("java");
		for (String argument : project.getSettings().getVmArguments())
		{
			command.add("-" + argument);
		}
		command.add("-cp");
		// File thisJar = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
		// String cp = thisJar.getAbsolutePath();
		// if (System.getProperty("user.name", "").equals("kela"))
		// cp += getCpSeperator()
		// + new File("C:/overture/overturesvn/core/vdmj/target/classes".replace('/',
		// File.separatorChar)).getAbsolutePath();
		String cp = System.getProperty("java.class.path");// +";vdmj_time-2.0.2.jar";
		String splitOn = cp.contains(";") ? ";" : ":";
		String[] cps = cp.split(splitOn);
		cp = "";
		for (String path : cps)
		{
			File f = new File(path);
			if (f.isFile() && !f.isAbsolute())
			{
				cp += new File(f.getName()).getAbsolutePath() + splitOn;
			} else
			{
				cp += path + splitOn;
			}
		}

		if (cp.length() > 0)
		{
			cp = cp.substring(0, cp.length() - 1);
		}

		File lib = new File(project.getSettings().getWorkingDirectory(), "lib");
		if (lib.exists() && lib.isDirectory())
		{
			for (File f : lib.listFiles())
			{
				if (f.getName().toLowerCase().endsWith(".jar"))
					cp += getCpSeperator() + f.getAbsolutePath();

			}
		}
		command.add(cp);
		command.add("org.overturetool.vdmj.VDMJ");

		command.add(project.getDialect().getArgstring());
		// -r <release>: VDM language release
		command.add("-r");
		command.add(project.getSettings().getLanguageVersion().toString());
		// -w: suppress warning messages
		// command.add("-w");

		// -q: suppress information messages
		// command.add("-q");
		// -i: run the interpreter if successfully type checked
		// command.add("-i");
		// command.add(entryPoint);
		// -p: generate proof obligations and stop

		// -e <exp>: evaluate <exp> and stop
		command.add("-e");
		command.add("\"" + entryPoint.replace("\"", "\\\"") + "\"");

		if (project.getSettings().getEncoding().length() > 0)
		{
			// -c <charset>: select a file charset
			command.add("-c");
			command.add(project.getSettings().getEncoding());
			// -t <charset>: select a console charset
			command.add("-t");
			command.add(project.getSettings().getEncoding());
		}

		// -o <filename>: saved type checked specification
		if (!project.getSettings().getPreChecks())
			command.add("-pre");
		// -pre: disable precondition checks
		if (!project.getSettings().getPostChecks())
			command.add("-post");
		// -post: disable postcondition checks
		if (!project.getSettings().getInvChecks())
			command.add("-inv");
		// -inv: disable type/state invariant checks
		if (!project.getSettings().getInvChecks())
			command.add("-inv");
		// -dtc: disable all dynamic type checking
		if (!project.getSettings().getDynamicTypeChecks())
			command.add("-dtc");
		// -log: enable real-time event logging

		// -remote <class>: enable remote control

		// -default: sets the default module

		if (entryPoint.contains("`") && !entryPoint.startsWith("new "))
		{
			command.add("-default");
			command.add(entryPoint.substring(0, entryPoint.indexOf('`')));
		} else if (entryPoint.startsWith("new "))
		{
			command.add("-default");
			command.add(entryPoint.substring(4, entryPoint.indexOf('(')));
		}

		for (File f : project.getSpecFiles())
		{
			command.add(f.getAbsolutePath());
		}
		// System.err.println("VDMJ: " + msg + "\n");
		// System.err.println("Usage: VDMJ <-vdmsl | -vdmpp | -vdmrt> [<options>] [<files>]");
		// System.err.println("-vdmsl: parse files as VDM-SL");
		// System.err.println("-vdmpp: parse files as VDM++");
		// System.err.println("-vdmrt: parse files as VICE");
		// System.err.println("-r <release>: VDM language release");
		// System.err.println("-w: suppress warning messages");
		// System.err.println("-q: suppress information messages");
		// System.err.println("-i: run the interpreter if successfully type checked");
		// System.err.println("-p: generate proof obligations and stop");
		// System.err.println("-e <exp>: evaluate <exp> and stop");
		// System.err.println("-c <charset>: select a file charset");
		// System.err.println("-t <charset>: select a console charset");
		// System.err.println("-o <filename>: saved type checked specification");
		// System.err.println("-pre: disable precondition checks");
		// System.err.println("-post: disable postcondition checks");
		// System.err.println("-inv: disable type/state invariant checks");
		// System.err.println("-dtc: disable all dynamic type checking");
		// System.err.println("-log: enable real-time event logging");
		// System.err.println("-remote <class>: enable remote control");

		ProcessBuilder pb = new ProcessBuilder(command);
		// pb.directory(project.getSettings().getWorkingDirectory());
		pb.directory(modelDir);
		Process p = pb.start();

		File projectDir = new File(reportLocation, project.getSettings().getName());
		projectDir.mkdirs();

		StringBuilder sb = new StringBuilder();
		for (String cmd : command)
		{
			sb.append(" " + cmd);
		}
		sb.append("\nWorking directory: " + pb.directory().getAbsolutePath());

		ProcessConsolePrinter pcpOut = new ProcessConsolePrinter(new File(projectDir, Phase.Interpretation
				+ "Out.txt"), p.getInputStream(), sb.toString().trim());
		pcpOut.start();

		ProcessConsolePrinter pcpErr = new ProcessConsolePrinter(new File(projectDir, Phase.Interpretation
				+ "Err.txt"), p.getErrorStream());
		pcpErr.start();

		boolean failed = false;
		try
		{
			Thread killer = new ProcessTimeOutKiller(p, 5 * 60);
			p.waitFor();
			killer.stop();
		} catch (Exception e)
		{
			failed = true;

		}
		try
		{
			pcpErr.interrupt();
		} catch (Exception e)
		{

		}
		try
		{
			pcpOut.interrupt();
		} catch (Exception e)
		{

		}
		if (p.exitValue() == 0 && !failed)
			return ExitStatus.EXIT_OK;
		else
			return ExitStatus.EXIT_ERRORS;

	}

	private static class ProcessTimeOutKiller extends Thread
	{
		Process p;
		final long startTime;

		final long endTime;

		public ProcessTimeOutKiller(Process p, int timeOutSeconds)
		{
			this.p = p;
			startTime = System.currentTimeMillis();

			endTime = startTime + (timeOutSeconds * 1000);
			// System.out.println("Run in ms: "+ new Long(endTime-startTime));
			setDaemon(true);
			start();
		}

		@Override
		public void run()
		{
			while (true)
			{
				try
				{
					Thread.sleep(100);
					if (endTime <= System.currentTimeMillis())
					{
						p.destroy();
						System.out.print(" -KILLED");
						return;
					}
				} catch (InterruptedException e)
				{

				}
			}
		}
	}

	private String getCpSeperator()
	{
		if (System.getProperty("os.name").toLowerCase().contains("win"))
			return ";";
		else
			return ":";
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
			if (!file.exists())
				return false;
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
		Console.out = new StdoutRedirector(new FileWriter(new File(projectDir, phase
				+ "Out.txt"), false));
		Console.err = new StderrRedirector(new FileWriter(new File(projectDir, phase
				+ "Err.txt"), false));
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

	public File getPdf()
	{
		return pdf;
	}
}
