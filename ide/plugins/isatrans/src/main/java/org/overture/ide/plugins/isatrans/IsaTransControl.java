package org.overture.ide.plugins.isatrans;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.lex.Dialect;
import org.overture.codegen.utils.GeneratedModule;
import org.overture.ide.core.ast.NotAllowedException;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;
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

	public void generateTheoryFiles()
	{

		preFlightCheck();

		// Translate specification to Isabelle
		DateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

		File isaDir = new File(new File(proj.getModelBuildPath().getOutput().getLocation().toFile(), "Isabelle"), df.format(new Date()));
		isaDir.mkdirs();

		IsaGen ig = new IsaGen();

		try
		{
			List<SClassDefinition> ast = proj.getModel().getClassList();
			List<GeneratedModule> modellTheoryList = ig.generateIsabelleSyntax(ast);

			GeneratedModule modelTheory = modellTheoryList.get(0);

			if (modelTheory.getContent().isEmpty())
			{
				openErrorDialog("Internal error.");
				return;
			}

			String thyName = "model.thy";// FIXME compute thy name from model

			File thyFile = new File(isaDir.getPath() + File.separatorChar
					+ thyName);
			FileUtils.writeStringToFile(thyFile, modelTheory.getContent());

			IProject p = (IProject) proj.getAdapter(IProject.class);
			p.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());

		} catch (Exception e)
		{
			openErrorDialog("Internal error.");
			e.printStackTrace();
		}

	}

	private void preFlightCheck()
	{
		if (!VdmTypeCheckerUi.typeCheck(shell, proj))
		{
			openErrorDialog("Model has errors.");
			return;
		}
		;

		if (!proj.getDialect().equals(Dialect.VDM_PP))
		{
			openErrorDialog("Only single class VDM++ models are allowed.");
			return;
		}
		try
		{
			if (proj.getModel().getClassList().size() > 1)
			{
				openErrorDialog("Only single class VDM++ models are allowed.");
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
