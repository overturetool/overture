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
package org.overture.ide.ui.property;

import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.overture.ide.core.resources.IVdmProject;
import org.overture.ide.core.resources.ModelBuildPath;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.utility.VdmTypeCheckerUi;

@SuppressWarnings("restriction")
public class VdmBuildPathPropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage
{
	private static class ModelPathContentProvider implements
			ITreeContentProvider
	{

		public Object[] getChildren(Object parentElement)
		{
			return null;
		}

		public Object getParent(Object element)
		{
			return null;
		}

		public boolean hasChildren(Object element)
		{
			return false;
		}

		@SuppressWarnings("rawtypes")
		public Object[] getElements(Object inputElement)
		{
			if(inputElement instanceof ModelBuildPath)
			{
				return ((ModelBuildPath)inputElement).getModelSrcPaths().toArray();
			}
			if (inputElement instanceof List)
			{
				return ((List) inputElement).toArray();
			}
			return new Object[] {};
		}

		public void dispose()
		{

		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
		{

		}

	}

	ModelBuildPath modelpath = null;
	TreeViewer tree = null;
//	List<IContainer> data = null;

	@Override
	protected Control createContents(Composite parent)
	{
		Composite myComposite = new Composite(parent, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		myComposite.setLayoutData(gd);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = false;
		layout.numColumns = 2;
		myComposite.setLayout(layout);

		// FillLayout layout = new FillLayout();
		//
		// layout.type = SWT.VERTICAL;
		// myComposite.setLayout(layout);

		Composite myCompositeTree = new Composite(myComposite, SWT.NONE);
		FillLayout layout1 = new FillLayout();

		layout1.type = SWT.VERTICAL;
		myCompositeTree.setLayout(layout1);
		tree = new TreeViewer(myCompositeTree, SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
		myCompositeTree.setLayoutData(gd);

		IProject project = getSelectedProject();
//		modelpath = new ModelBuildPath((IVdmProject) project.getAdapter(IVdmProject.class));
//		data = modelpath.getModelSrcPaths();
		modelpath =((IVdmProject) project.getAdapter(IVdmProject.class)).getModelBuildPath();
		tree.setLabelProvider(new WorkbenchLabelProvider());
		tree.setContentProvider(new ModelPathContentProvider());
		tree.setInput(modelpath);

		Button addFolderButtom = createPushButton(myComposite, "Add...", null);
		gd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		addFolderButtom.setLayoutData(gd);
		addFolderButtom.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				class SrcContentProvider extends BaseWorkbenchContentProvider
				{
					@Override
					public boolean hasChildren(Object element)
					{
						if (element instanceof IProject
								|| element instanceof IFolder)
						{
							return super.hasChildren(element);
						}
						return false;
					}

					@Override
					public Object[] getElements(Object element)
					{
						List<IFolder> elements = new ArrayList<IFolder>();
						Object[] arr = super.getElements(element);
						if (arr != null)
						{
							for (Object object : arr)
							{
								if (// object instanceof IFile ||
								object instanceof IFolder)
								{
									elements.add((IFolder) object);
								}
							}
							return elements.toArray();
						}
						return null;
					}

					@Override
					public Object[] getChildren(Object element)
					{
						List<IFolder> elements = new ArrayList<IFolder>();
						Object[] arr = super.getChildren(element);
						if (arr != null)
						{
							for (Object object : arr)
							{
								if (// object instanceof IFile ||
								object instanceof IFolder)
								{
									elements.add((IFolder) object);
								}
							}
							return elements.toArray();
						}
						return null;
					}

				}
				;
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), new WorkbenchLabelProvider(), new SrcContentProvider());
				dialog.setTitle("Select source folder");
				dialog.setMessage("Select a source folder:");
				dialog.setComparator(new ViewerComparator());
				dialog.setInput(getSelectedProject());

				if (dialog.open() == Window.OK)
				{
					if (dialog.getFirstResult() != null
					// && dialog.getFirstResult() instanceof IProject
					// && ((IProject) dialog.getFirstResult()).getAdapter(IVdmProject.class) != null)
					)
					{
						Object o = dialog.getFirstResult();
						if (o instanceof IContainer)
						{
							IContainer source = (IContainer) o;
//							if (!data.contains(source))
//							{
							modelpath.add(source);
//								data.add(source);
								tree.refresh();
//							}
						}
						// fScenarioText.setText(((IFile) dialog.getFirstResult()).getProjectRelativePath().toString());
					}

				}

			}

		});
		addFolderButtom.setEnabled(false);

		return myComposite;
	}

	@SuppressWarnings( { "deprecation" })
	public static IProject getSelectedProject()
	{
		ISelection selectedItem = WorkbenchPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection();
		IProject selectedProject = null;

		if (selectedItem instanceof ITreeSelection)
		{
			ITreeSelection selection = (ITreeSelection) selectedItem;
			if (selection.getPaths().length > 0)
			{
				Object project = selection.getPaths()[0].getFirstSegment();
				if (project instanceof IProject)
					selectedProject = (IProject) project;

			}
		} else if (selectedItem instanceof IStructuredSelection)
		{
			IStructuredSelection selection = (IStructuredSelection) selectedItem;
			if (selection.getFirstElement() instanceof IProject)
				selectedProject = (IProject) selection.getFirstElement();
		}

		return selectedProject;
	}

	/**
	 * Creates and returns a new push button with the given label and/or image.
	 * 
	 * @param parent
	 *            parent control
	 * @param label
	 *            button label or <code>null</code>
	 * @param image
	 *            image of <code>null</code>
	 * @return a new push button
	 */
	public static Button createPushButton(Composite parent, String label,
			Image image)
	{
		Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		if (image != null)
		{
			button.setImage(image);
		}
		if (label != null)
		{
			button.setText(label);
		}
		GridData gd = new GridData();
		button.setLayoutData(gd);

		return button;
	}

	@Override
	public boolean performOk()
	{
		if (modelpath != null)
		{
			try
			{
				modelpath.save();
//				modelpath.save(data, modelpath.getOutput());

				IVdmProject vdmProject = (IVdmProject) getSelectedProject().getAdapter(IVdmProject.class);
				VdmTypeCheckerUi.typeCheck(getShell(), vdmProject);
			} catch (CoreException e)
			{
				VdmUIPlugin.log("Faild to save model path changed", e);
			}
			return true;
		}
		return false;
	}

}
