package org.overture.ide.plugins.idedebughelper.actions;

import java.text.DecimalFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.overture.ide.ast.AstManager;
import org.overture.ide.ast.RootNode;

public class AstProfilerAction implements IObjectActionDelegate {

	private Shell shell;

	/**
	 * Constructor for Action1.
	 */
	public AstProfilerAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {

		org.eclipse.swt.widgets.Shell s = new org.eclipse.swt.widgets.Shell();
		try {
			IProject selectedProject = null;
			selectedProject = ProjectHelper.getSelectedProject(action,
					selectedProject);
			if (selectedProject == null) {
				ConsoleWriter.ConsolePrint(shell,
						"Could not find selected project");
				return;
			}

			profileAst();

		} catch (Exception ex) {
			System.err.println(ex.getMessage() + ex.getStackTrace());
			ConsoleWriter.ConsolePrint(s, ex);
		}

	}
	
	public static String paddString(String string, int size)
	{
		if(string.length()>size)
			return string.substring(0,size);
		else
		{
			for (int i = string.length(); i < size; i++) {
				string+=" ";
			}
			return string;
		}
	}

	private void profileAst() {
		final Job expandJob = new Job("Profiling") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				monitor.worked(IProgressMonitor.UNKNOWN);
				ConsoleWriter console = new ConsoleWriter(shell);
				String dividerLine="----------------------------------------------------------------------------------";
				String headingLine="==================================================================================";
				console.println(dividerLine);
				DecimalFormat threeDec = new DecimalFormat("0.000");
				Double sum= new Double(0);
				for (IProject  project : AstManager.instance().getProjects()) {
					for(String nature: AstManager.instance().getNatures(project))
					{
						RootNode root = AstManager.instance().getRootNode(project, nature);
						
						//threeDec.setGroupingUsed(false);
						Double size = new Double(0);
						if(root!=null && root.getRootElementList()!=null)
							size = new Double(Profiler.sizeOfInKb(root.getRootElementList()));
						
						nature=nature.replaceAll("org.overture.ide.", "");
						nature=nature.replaceAll(".core.nature", "");
						if(size>0)
							sum+=size;
						console.println("Project: "+ paddString(project.getName(),30)+ " Nature: "+ paddString(nature,10)+ " Size: "+ paddString(threeDec.format(size),5)+ " kB");
					}
				}
				console.println(dividerLine);
				console.println("Total sum: "+ paddString(threeDec.format(sum),5)+ " kB");
				console.println(headingLine);
				monitor.done();
				// expandCompleted = true;

				return new Status(IStatus.OK, "org.overture.ide.umltrans",
						IStatus.OK, "Translation completed", null);

			}

		};
		expandJob.setPriority(Job.INTERACTIVE);
		expandJob.schedule(0);

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
