/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.overturetool.eclipse.plugins.editor.internal.ui.wizards;

import java.util.Observable;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.util.BusyIndicatorRunnableContext;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.dltk.ui.wizards.BuildpathsBlock;
import org.eclipse.dltk.ui.wizards.NewElementWizard;
import org.eclipse.dltk.ui.wizards.ProjectWizardSecondPage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.INewWizard;
import org.overturetool.eclipse.plugins.editor.core.OvertureConstants;
import org.overturetool.eclipse.plugins.editor.core.OvertureNature;
import org.overturetool.eclipse.plugins.editor.internal.ui.UIPlugin;
import org.overturetool.eclipse.plugins.editor.internal.ui.preferences.OvertureBuildPathsBlock;
import org.overturetool.eclipse.plugins.editor.ui.EditorCoreUIConstants;
import org.overturetool.eclipse.plugins.editor.ui.OvertureImages;
import org.overturetool.eclipse.plugins.editor.ui.OverturePreferenceConstants;

public class OvertureProjectCreationWizard extends NewElementWizard implements
		INewWizard, IExecutableExtension {
	private OvertureProjectWizardFirstPage fFirstPage;
	private ProjectWizardSecondPage fSecondPage;
	private IConfigurationElement fConfigElement;

	public OvertureProjectCreationWizard() {
		setDefaultPageImageDescriptor(OvertureImages.DESC_WIZBAN_PROJECT_CREATION);
		setDialogSettings(DLTKUIPlugin.getDefault().getDialogSettings());
		setWindowTitle(OvertureWizardMessages.ProjectCreationWizard_title);
		DLTKUIPlugin.getImageDescriptorRegistry();
	}

	public void addPages() {
		super.addPages();
		fFirstPage = new OvertureProjectWizardFirstPage() {

			OvertureInterpreterGroup fInterpreterGroup;

			final class OvertureInterpreterGroup extends
					AbstractInterpreterGroup {

				public OvertureInterpreterGroup(Composite composite) {
					super(composite);
				}

				protected String getCurrentLanguageNature() {
					return OvertureNature.NATURE_ID;
				}
				
				protected String getIntereprtersPreferencePageId() {
					return EditorCoreUIConstants.INTERPRETER_PREFERENCE_PAGE; 
				} 
			};

			protected IInterpreterGroup createInterpreterGroup(Composite parent) {
				fInterpreterGroup = new OvertureInterpreterGroup(parent);
				return fInterpreterGroup;
			}
			

			protected Observable getInterpreterGroupObservable() {
				return fInterpreterGroup;
			}

	

			protected IInterpreterInstall getInterpreter() {
				return fInterpreterGroup.getSelectedInterpreter();
			}


			
		};
		fFirstPage
				.setTitle(OvertureWizardMessages.ProjectCreationWizardFirstPage_title);
		fFirstPage
				.setDescription(OvertureWizardMessages.ProjectCreationWizardFirstPage_description);
		addPage(fFirstPage);
		
		fSecondPage = new ProjectWizardSecondPage(fFirstPage) {
			protected BuildpathsBlock createBuildpathBlock(
					IStatusChangeListener listener) {
				return new OvertureBuildPathsBlock(
						new BusyIndicatorRunnableContext(), listener, 0,
						useNewSourcePage(), null);
			}

			protected String getScriptNature() {
				return OvertureNature.NATURE_ID;
			}

			protected IPreferenceStore getPreferenceStore() {
				return UIPlugin.getDefault().getPreferenceStore();
			}
		};
		
	}

	
	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		fSecondPage.performFinish(monitor); // use the full progress monitor
		
	}
	
	

	public boolean performFinish() {	
		
		boolean res = super.performFinish();
		
		try {
			IProject proj = fFirstPage.getProjectHandle();
			QualifiedName qn = new QualifiedName(UIPlugin.PLUGIN_ID,OverturePreferenceConstants.OVERTURE_DIALECT_KEY);
			proj.setPersistentProperty(qn, fFirstPage.getDialectSetting());
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return res;
	}
	
	
	@Override
	public void createPageControls(Composite pageContainer) {
		super.createPageControls(pageContainer);	
		
	}

	/*
	 * Stores the configuration element for the wizard. The config element will
	 * be used in <code>performFinish</code> to set the result perspective.
	 */
	public void setInitializationData(IConfigurationElement cfig,
			String propertyName, Object data) {
		fConfigElement = cfig;
	}

	public boolean performCancel() {
		fSecondPage.performCancel();
		return super.performCancel();
	}

	public IModelElement getCreatedElement() {
		return DLTKCore.create(fFirstPage.getProjectHandle());
	}
}
