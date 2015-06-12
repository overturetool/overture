package org.overture.ide.plugins.isatrans;

import java.io.File;
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
		preFlightCheck();
		File isaDir = makeThyDirs();
		try
		{
			AModuleModules ast = proj.getModel().getModuleList().get(0);
			IsaPog ip = new IsaPog(ast);

			if (ip.getModelThyString().equals("")
					|| ip.getPosThyString().equals(""))
			{
				openErrorDialog("Internal error.");
				return;
			}

			File modelFile = new File(isaDir.getPath() + File.separatorChar
					+ ip.getModelThyName());
			FileUtils.writeStringToFile(modelFile, ip.getModelThyString());

			File posFile = new File(isaDir.getPath() + File.separatorChar
					+ ip.getPosThyName());
			FileUtils.writeStringToFile(posFile, ip.getPosThyString());

			refreshProject();
			
		} catch (Exception e)
		{
			openErrorDialog("Internal error.");
			e.printStackTrace();
		}
	}

	public void generateTheoryFilesModel()
	{

		preFlightCheck();
		File isaDir = makeThyDirs();

		IsaGen ig = new IsaGen();

		try
		{
			List<AModuleModules> ast = proj.getModel().getModuleList();
			List<GeneratedModule> modellTheoryList = ig.generateIsabelleSyntax(ast);

			GeneratedModule modelTheory = modellTheoryList.get(0);

			if (modelTheory.getContent().isEmpty())
			{
				openErrorDialog("Internal error.");
				return;
			}

			String thyName = modelTheory.getName() + ".thy";

			File thyFile = new File(isaDir.getPath() + File.separatorChar
					+ thyName);
			FileUtils.writeStringToFile(thyFile, modelTheory.getContent());

			refreshProject();

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
