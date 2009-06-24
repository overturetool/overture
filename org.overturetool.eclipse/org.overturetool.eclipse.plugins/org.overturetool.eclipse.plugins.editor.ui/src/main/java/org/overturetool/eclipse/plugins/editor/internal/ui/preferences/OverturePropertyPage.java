package org.overturetool.eclipse.plugins.editor.internal.ui.preferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.internal.events.BuildManager;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.internal.ui.util.CoreUtility;
import org.eclipse.dltk.internal.ui.wizards.NewWizardMessages;
import org.eclipse.dltk.internal.ui.wizards.buildpath.BPListElement;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.ui.preferences.AbstractOptionsBlock;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.editor.internal.ui.UIPlugin;
import org.overturetool.eclipse.plugins.editor.ui.OverturePreferenceConstants;
import org.overturetool.eclipse.plugins.launching.internal.launching.IOvertureInstallType;

public class OverturePropertyPage extends PropertyPage {

	private Group fGroup;
	private Combo comboInterpreter;
	private String[] fComplianceLabels;
	private Combo comboDialect;
	private InterpreterWidgetListener interpreterWidgetListener = new InterpreterWidgetListener();

	class InterpreterWidgetListener implements SelectionListener {

		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
		}

		public void widgetSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			if (e.widget instanceof Combo) {
				setDialects(((Combo) e.widget).getItem(((Combo) e.widget)
						.getSelectionIndex()));
			}
		}

	}

	protected Control createContents(Composite parent) {

	
		IResource res = (IResource) getElement().getAdapter(IResource.class);
		IProject project = res.getProject();		

		fGroup = SWTFactory.createGroup(parent, "Interpreter and Dialect", 2,
				2, GridData.FILL_HORIZONTAL);
		Label label = new Label(fGroup, SWT.NONE);
		label.setText("Select Interpreter");
		comboInterpreter = new Combo(fGroup, SWT.READ_ONLY);
		comboInterpreter.addSelectionListener(interpreterWidgetListener);
		fillInstalledInterpreters(comboInterpreter);
		Label label2 = new Label(fGroup, SWT.NONE);
		label2.setText("Select dialect");
		comboDialect = new Combo(fGroup, SWT.READ_ONLY);

		String dialect = "";
		String interpreterName = "";

		try {
			QualifiedName qn = new QualifiedName(UIPlugin.PLUGIN_ID,
					OverturePreferenceConstants.OVERTURE_DIALECT_KEY);
			dialect = project.getPersistentProperty(qn);
			qn = new QualifiedName(UIPlugin.PLUGIN_ID,
					OverturePreferenceConstants.OVERTURE_INTERPETER_KEY);
			interpreterName = project.getPersistentProperty(qn);
			setDialects(interpreterName);
			selectDialect(dialect);
			selectInterpreter(interpreterName);

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fGroup;

	}

	private void selectInterpreter(String interpreterName) {
		for (int i = 0; i < comboInterpreter.getItemCount(); i++) {
			if (comboInterpreter.getItem(i).equals(interpreterName)) {
				comboInterpreter.select(i);
			}
		}
	}

	private void selectDialect(String dialect) {
		for (int i = 0; i < comboDialect.getItemCount(); i++) {
			if (comboDialect.getItem(i).equals(dialect)) {
				comboDialect.select(i);
			}
		}
	}

	private void setDialects(String selectedInterpreterName) {
		IInterpreterInstall interpreter = getSelectedInterpreterInstall(selectedInterpreterName);

		if (interpreter != null) {
			comboDialect.setItems(((IOvertureInstallType) interpreter
					.getInterpreterInstallType()).getSupportedDialectStrings());
		} else {
			comboDialect.setItems(new String[] { "No supported dialects" });
		}
		comboDialect.select(0);
	}

	private void fillInstalledInterpreters(Combo comboField) {
		String selectedItem = null;
		int selectionIndex = -1;
		if (true) {// fUseProjectInterpreter.isSelected()) {

			selectionIndex = comboField.getSelectionIndex();
			if (selectionIndex != -1) {// paranoia
				selectedItem = comboField.getItems()[selectionIndex];
			}
		}

		IInterpreterInstall[] installedInterpreters = getWorkspaceInterpeters();

		selectionIndex = -1;// find new index
		fComplianceLabels = new String[installedInterpreters.length];
		for (int i = 0; i < installedInterpreters.length; i++) {
			fComplianceLabels[i] = installedInterpreters[i].getName();
			if (selectedItem != null
					&& fComplianceLabels[i].equals(selectedItem)) {
				selectionIndex = i;
			}
		}
		comboField.setItems(fComplianceLabels);
	}

	private IInterpreterInstall[] getWorkspaceInterpeters() {
		List<IInterpreterInstall> standins = new ArrayList<IInterpreterInstall>();
		IInterpreterInstallType[] types = ScriptRuntime
				.getInterpreterInstallTypes(OvertureNature.NATURE_ID);

		for (IInterpreterInstallType interpreterInstallType : types) {
			IInterpreterInstall[] installs = interpreterInstallType
					.getInterpreterInstalls();

			for (IInterpreterInstall interpreterInstall : installs) {
				standins.add(interpreterInstall);
			}

		}
		return ((IInterpreterInstall[]) standins
				.toArray(new IInterpreterInstall[standins.size()]));
	}

	@Override
	public boolean performOk() {
		try {

			IResource res = (IResource) getElement()
					.getAdapter(IResource.class);
			IProject project = res.getProject();

			IScriptProject scriptProj = ModelManager.getModelManager()
					.getModel().getScriptProject(project.getName());
			IInterpreterInstall interpreterInstall;
			IBuildpathEntry[] buildPaths;
			// buildPaths[1].getPath().
			// IBuildpathEntry bp =
			interpreterInstall = ScriptRuntime.getInterpreterInstall(scriptProj);
			buildPaths = scriptProj.getRawBuildpath();
			QualifiedName qn = new QualifiedName(UIPlugin.PLUGIN_ID,OverturePreferenceConstants.OVERTURE_DIALECT_KEY);
			project.setPersistentProperty(qn, comboDialect.getItem(comboDialect.getSelectionIndex()));

			qn = new QualifiedName(UIPlugin.PLUGIN_ID,
					OverturePreferenceConstants.OVERTURE_INTERPETER_KEY);
			String interpreterName = comboInterpreter.getItem(comboInterpreter
					.getSelectionIndex());
			project.setPersistentProperty(qn, interpreterName);
			IInterpreterInstall projectInterpreter = getSelectedInterpreterInstall(interpreterName);
			int erik = 60;
			List<BPListElement> bpList = new ArrayList<BPListElement>();
			for(int i = 0; i < buildPaths.length; i++){
				if(buildPaths[i].getEntryKind() == IBuildpathEntry.BPE_CONTAINER 
						&& buildPaths[i].getPath().toString().startsWith("org.eclipse.dltk.launching.INTERPRETER_CONTAINER")){
					String path = "org.eclipse.dltk.launching.INTERPRETER_CONTAINER/" + projectInterpreter.getInterpreterInstallType().getId() + "/" + projectInterpreter.getName();
					bpList.add(new BPListElement(scriptProj, IBuildpathEntry.BPE_CONTAINER, new Path(path), res, buildPaths[i].isExternal()));
				}else{
					bpList.add(new BPListElement(scriptProj, buildPaths[i].getEntryKind(), buildPaths[i].getPath(), res, buildPaths[i].isExternal()));
				}
					
			}
			
			
		
			flush(bpList, scriptProj, null);

		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//		

		return super.performOk();
	}

	public static void flush(List buildpathEntries, IScriptProject javaProject,
			IProgressMonitor monitor) throws CoreException,
			OperationCanceledException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		monitor
				.setTaskName(NewWizardMessages.BuildPathsBlock_operationdesc_Script);
		monitor.beginTask("", buildpathEntries.size() * 4 + 4); //$NON-NLS-1$
		try {
			IProject project = javaProject.getProject();
			IPath projPath = project.getFullPath();
			monitor.worked(1);
			// IWorkspaceRoot fWorkspaceRoot =
			// DLTKUIPlugin.getWorkspace().getRoot();
			// create and set the output path first
			monitor.worked(1);
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			int nEntries = buildpathEntries.size();
			IBuildpathEntry[] buildpath = new IBuildpathEntry[nEntries];
			int i = 0;
			for (Iterator iter = buildpathEntries.iterator(); iter.hasNext();) {
				BPListElement entry = (BPListElement) iter.next();
				buildpath[i] = entry.getBuildpathEntry();
				i++;
				IResource res = entry.getResource();
				// 1 tick
				if (res instanceof IFolder && entry.getLinkTarget() == null
						&& !res.exists()) {
					CoreUtility.createFolder((IFolder) res, true, true,
							new SubProgressMonitor(monitor, 1));
				} else {
					monitor.worked(1);
				}
				// 3 ticks
				if (entry.getEntryKind() == IBuildpathEntry.BPE_SOURCE) {
					monitor.worked(1);
					IPath path = entry.getPath();
					if (projPath.equals(path)) {
						monitor.worked(2);
						continue;
					}
					if (projPath.isPrefixOf(path)) {
						path = path
								.removeFirstSegments(projPath.segmentCount());
					}
					IFolder folder = project.getFolder(path);
					IPath orginalPath = entry.getOrginalPath();
					if (orginalPath == null) {
						if (!folder.exists()) {
							// New source folder needs to be created
							if (entry.getLinkTarget() == null) {
								CoreUtility.createFolder(folder, true, true,
										new SubProgressMonitor(monitor, 2));
							} else {
								folder.createLink(entry.getLinkTarget(),
										IResource.ALLOW_MISSING_LOCAL,
										new SubProgressMonitor(monitor, 2));
							}
						}
					} else {
						if (projPath.isPrefixOf(orginalPath)) {
							orginalPath = orginalPath
									.removeFirstSegments(projPath
											.segmentCount());
						}
						IFolder orginalFolder = project.getFolder(orginalPath);
						if (entry.getLinkTarget() == null) {
							if (!folder.exists()) {
								// Source folder was edited, move to new
								// location
								IPath parentPath = entry.getPath()
										.removeLastSegments(1);
								if (projPath.isPrefixOf(parentPath)) {
									parentPath = parentPath
											.removeFirstSegments(projPath
													.segmentCount());
								}
								if (parentPath.segmentCount() > 0) {
									IFolder parentFolder = project
											.getFolder(parentPath);
									if (!parentFolder.exists()) {
										CoreUtility.createFolder(parentFolder,
												true, true,
												new SubProgressMonitor(monitor,
														1));
									} else {
										monitor.worked(1);
									}
								} else {
									monitor.worked(1);
								}
								orginalFolder.move(entry.getPath(), true, true,
										new SubProgressMonitor(monitor, 1));
							}
						} else {
							if (!folder.exists()
									|| !entry.getLinkTarget().equals(
											entry.getOrginalLinkTarget())) {
								orginalFolder.delete(true,
										new SubProgressMonitor(monitor, 1));
								folder.createLink(entry.getLinkTarget(),
										IResource.ALLOW_MISSING_LOCAL,
										new SubProgressMonitor(monitor, 1));
							}
						}
					}
				} else {
					monitor.worked(3);
				}
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
			}
			javaProject.setRawBuildpath(buildpath, new SubProgressMonitor(monitor, 2));
		} finally {
			monitor.done();
		}
	}

	private IInterpreterInstall getSelectedInterpreterInstall(
			String selectedInterpreterName) {
		IInterpreterInstall[] interpreters = getWorkspaceInterpeters();

		for (IInterpreterInstall interpreterInstall : interpreters) {
			if (interpreterInstall.getName().equals(selectedInterpreterName)) {
				return interpreterInstall;

			}
		}
		return null;
	}

}
