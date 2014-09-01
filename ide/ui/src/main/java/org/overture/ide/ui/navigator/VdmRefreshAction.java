/*
 * #%~
 * org.overture.ide.ui
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.ui.navigator;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.overture.ide.ui.IVdmUiConstants;


public class VdmRefreshAction extends CommonActionProvider {

	 private RefreshAction refreshAction;

	    private Shell         shell;

	    /*
	     * (non-Javadoc)
	     * @see
	     * org.eclipse.ui.navigator.CommonActionProvider#init(org.eclipse.ui.navigator.ICommonActionExtensionSite)
	     */
	    @Override
	    public void init(ICommonActionExtensionSite aSite) {
	        super.init(aSite);
	        shell = aSite.getViewSite().getShell();
	        makeActions();
	    }

	    @Override
	    public void fillActionBars(IActionBars actionBars) {
	        actionBars.setGlobalActionHandler(ActionFactory.REFRESH.getId(), refreshAction);
	        updateActionBars();
	    }

	    /**
	     * Adds the refresh resource actions to the context menu.
	     *
	     * @param menu
	     * context menu to add actions to
	     */
	    
	    @SuppressWarnings("unchecked")
		@Override
	    public void fillContextMenu(IMenuManager menu) {
	        IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
	        boolean hasClosedProjects = false;
	        Iterator<Object> resources = selection.iterator();

	        while (resources.hasNext() && (!hasClosedProjects)) {
	            Object next = resources.next();
	            IProject project = null;

	            if (next instanceof IProject) {
	                project = (IProject) next;
	            } else if (next instanceof IAdaptable) {
	                project = (IProject) ((IAdaptable) next).getAdapter(IProject.class);
	            }

	            if (project == null) {
	                continue;
	            }

	            if (!project.isOpen()) {
	                hasClosedProjects = true;
	            }
	        }

	        if (!hasClosedProjects) {
	            refreshAction.selectionChanged(selection);
	            menu.appendToGroup(ICommonMenuConstants.GROUP_BUILD, refreshAction);
	        }
	    }

	    protected void makeActions() {
	        IShellProvider sp = new IShellProvider() {
	            @SuppressWarnings("synthetic-access")
	            public Shell getShell() {
	                return shell;
	            }
	        };

	        refreshAction = new RefreshAction(sp) {
	            @Override
	            public void run() {
	                final IStatus[] errorStatus = new IStatus[1];
	                errorStatus[0] = Status.OK_STATUS;
	                final WorkspaceModifyOperation op = (WorkspaceModifyOperation) createOperation(errorStatus);
	                WorkspaceJob job = new WorkspaceJob("refresh") { //$NON-NLS-1$

	                    @SuppressWarnings("synthetic-access")
	                    @Override
	                    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
	                        try {
	                            op.run(monitor);
	                            if (shell != null && !shell.isDisposed()) {
	                                shell.getDisplay().asyncExec(new Runnable() {
	                                    public void run() {
	                                        StructuredViewer viewer = getActionSite().getStructuredViewer();
	                                        if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed()) {
	                                            viewer.refresh();
	                                        }
	                                    }
	                                });
	                            }
	                        } catch (InvocationTargetException e) {
	                            String msg = NLS.bind("Exception in {0}. run: {1}", getClass().getName(), e.getTargetException());
	                            throw new CoreException(new Status(IStatus.ERROR, IVdmUiConstants.PLUGIN_ID, IStatus.ERROR, msg, e
	                                    .getTargetException()));
	                        } catch (InterruptedException e) {
	                            return Status.CANCEL_STATUS;
	                        }
	                        return errorStatus[0];
	                    }

	                };
	                ISchedulingRule rule = op.getRule();
	                if (rule != null) {
	                    job.setRule(rule);
	                }
	                job.setUser(true);
	                job.schedule();
	            }
	        };
	        //refreshAction.setDisabledImageDescriptor(getImageDescriptor("icons/refresh_nav_disabled.gif"));//$NON-NLS-1$
	        //refreshAction.setImageDescriptor(getImageDescriptor("icons/refresh_nav_enabled.gif"));//$NON-NLS-1$
	        //refreshAction.setImageDescriptor(VdmPluginImages.DESC_MENU_REFRESH);
	        refreshAction.setActionDefinitionId(IWorkbenchCommandConstants.FILE_REFRESH);
	    }

	    /**
	     * Returns the image descriptor with the given relative path.
	     */
	    protected ImageDescriptor getImageDescriptor(String relativePath) {
	        return AbstractUIPlugin.imageDescriptorFromPlugin(IVdmUiConstants.PLUGIN_ID, relativePath);
	        

	    }

	    @Override
	    public void updateActionBars() {
	        IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
	        refreshAction.selectionChanged(selection);
	    }


}
