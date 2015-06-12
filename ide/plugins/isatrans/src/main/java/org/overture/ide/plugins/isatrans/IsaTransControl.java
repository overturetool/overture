package org.overture.ide.plugins.isatrans;

import java.io.File;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.overture.ast.lex.Dialect;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
import org.overture.isapog.IsaPog;
import org.overturetool.cgisa.IsaGen;

public class IsaTransControl
{
	private IWorkbenchWindow window;
	private IVdmProject proj;
	private Shell shell;

	public IsaTransControl(IWorkbenchWindow window, IVdmProject proj,
			Shell shell)
	{
		this.window = window;
		this.proj = proj;
		this.shell = shell;
	}

	public void generateTheoryFilesModelPos()
	{
		final MessageConsole console = new MessageConsole("Isabelle Translation", null);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });

		PrintStream printStream = new PrintStream(console.newMessageStream());

		printStream.println("Starting Isabelle to VDM translation of Model and Proof Obligations...");

		preFlightCheck();
		File isaDir = makeThyDirs();
		try
		{
			AModuleModules ast = proj.getModel().getModuleList().get(0);
			printStream.println();
			printStream.println("Generating Proof Obligations.");
			printStream.println("Translating Model.");
			printStream.println("Translating POs.");
			IsaPog ip = new IsaPog(ast);

			if (ip.getModelThyString().equals("")
					|| ip.getPosThyString().equals(""))
			{
				openErrorDialog("Internal error.");
				// FIXME report errors
				return;
			}

			printStream.println("Writing files.");
			File modelFile = new File(isaDir.getPath() + File.separatorChar
					+ ip.getModelThyName());
			FileUtils.writeStringToFile(modelFile, ip.getModelThyString());

			File posFile = new File(isaDir.getPath() + File.separatorChar
					+ ip.getPosThyName());
			FileUtils.writeStringToFile(posFile, ip.getPosThyString());

			refreshProject();

			printStream.println();
			printStream.println("Model and Proof Obligations Translation complete.");
			printStream.close();

		} catch (Exception e)
		{
			openErrorDialog("Internal error.");
			e.printStackTrace();
		}
	}

	public void generateTheoryFilesModel()
	{
		final MessageConsole console = new MessageConsole("Isabelle Translation", null);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { console });

		PrintStream printStream = new PrintStream(console.newMessageStream());

		printStream.println("Starting Isabelle to VDM translation of Model...");
		preFlightCheck();
		File isaDir = makeThyDirs();

		IsaGen ig = new IsaGen();

		try
		{
			List<AModuleModules> ast = proj.getModel().getModuleList();
			printStream.println();
			printStream.println("Translating Model.");
			List<GeneratedModule> modellTheoryList = ig.generateIsabelleSyntax(ast);

			GeneratedModule modelTheory = modellTheoryList.get(0);

			if (modelTheory.getContent().isEmpty())
			{
				openErrorDialog("Internal error.");
				printStream.println(modelTheory.getMergeErrors());
				printStream.println(modelTheory.getUnsupportedInIr());
				printStream.println(modelTheory.getUnsupportedInTargLang());
				return;
			}

			String thyName = modelTheory.getName() + ".thy";

			File thyFile = new File(isaDir.getPath() + File.separatorChar
					+ thyName);
			printStream.println("Writing files.");
			FileUtils.writeStringToFile(thyFile, modelTheory.getContent());

			refreshProject();

			printStream.println();
			printStream.println("Model Translation complete.");
			printStream.close();
		} catch (Exception e)
		{
			openErrorDialog("Internal error.");
			e.printStackTrace();
		}

	}

	private void refreshProject() throws CoreException
	{
		IProject p = (IProject) proj.getAdapter(IProject.class);
		p.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	}

	private File makeThyDirs()
	{
		// Translate specification to Isabelle
		DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

		File isaDir = new File(new File(proj.getModelBuildPath().getOutput().getLocation().toFile(), "Isabelle"), df.format(new Date()));
		isaDir.mkdirs();
		return isaDir;
	}

	private void preFlightCheck()
	{
		if (!VdmTypeCheckerUi.typeCheck(shell, proj))
		{
			openErrorDialog("Model has errors.");
			return;
		}
		;

		if (!proj.getDialect().equals(Dialect.VDM_SL))
		{
			openErrorDialog("Only module VDM-SL models are allowed.");
			return;
		}
		try
		{
			if (proj.getModel().getModuleList().size() > 1)
			{
				openErrorDialog("Only single module VDM-SL models are allowed.");
				return;
			}
		} catch (NotAllowedException e)
		{
			e.printStackTrace();
		}

	}

	private void openErrorDialog(String message)
	{
		MessageDialog.openError(window.getShell(), "VDM 2 Isabelle", "Cannot generate theory files.\n\n"
				+ message);
	}

}
