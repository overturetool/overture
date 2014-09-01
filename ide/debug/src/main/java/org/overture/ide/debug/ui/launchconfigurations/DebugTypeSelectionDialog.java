/*
 * #%~
 * org.overture.ide.debug
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
package org.overture.ide.debug.ui.launchconfigurations;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.node.INode;
import org.overture.ide.debug.core.VdmDebugPlugin;
import org.overture.ide.ui.utility.ast.AstNameUtil;

public class DebugTypeSelectionDialog extends FilteredItemsSelectionDialog
{

	/**
	 * Main list label provider
	 */
	public class DebugTypeLabelProvider implements ILabelProvider
	{
		Map<ImageDescriptor, Image> fImageMap = new HashMap<ImageDescriptor, Image>();

		public Image getImage(Object element)
		{
			if (element instanceof IAdaptable)
			{
				IWorkbenchAdapter adapter = (IWorkbenchAdapter) ((IAdaptable) element).getAdapter(IWorkbenchAdapter.class);
				if (adapter != null)
				{
					ImageDescriptor descriptor = adapter.getImageDescriptor(element);
					Image image = (Image) fImageMap.get(descriptor);
					if (image == null)
					{
						image = descriptor.createImage();
						fImageMap.put(descriptor, image);
					}
					return image;
				}
			}
			return null;
		}

		public String getText(Object element)
		{
			if (element instanceof INode)
			{
				INode type = (INode) element;
				String label = AstNameUtil.getName(type);
				String container = getDeclaringContainerName(type);
				if (container != null && !"".equals(container)) { //$NON-NLS-1$
					label += " - " + container; //$NON-NLS-1$
				}
				return label;
			}
			return null;
		}

		/**
		 * Returns the name of the declaring container name
		 * 
		 * @param type
		 *            the type to find the container name for
		 * @return the container name for the specified type
		 */
		protected String getDeclaringContainerName(INode type)
		{

			if (type instanceof AExplicitFunctionDefinition)
			{
				return ((AExplicitFunctionDefinition) type).getLocation().getModule();
			}
			if (type instanceof AExplicitOperationDefinition)
			{
				return ((AExplicitOperationDefinition) type).getLocation().getModule();
			}

			return "";
			// INode outer = type.getDeclaringType();
			// if(outer != null) {
			// return outer.getFullyQualifiedName('.');
			// }
			// else {
			// String name = type.getPackageFragment().getElementName();
			//				if("".equals(name)) { //$NON-NLS-1$
			// name = LauncherMessages.MainMethodLabelProvider_0;
			// }
			// return name;
			// }
		}

		/**
		 * Returns the narrowest enclosing <code>IJavaElement</code> which is either an <code>IType</code> (enclosing)
		 * or an <code>IPackageFragment</code> (contained in)
		 * 
		 * @param type
		 *            the type to find the enclosing <code>IJavaElement</code> for.
		 * @return the enclosing element or <code>null</code> if none
		 */
		protected INode getDeclaringContainer(INode type)
		{
			// IJavaElement outer = type.getDeclaringType();
			// if(outer == null) {
			// outer = type.getPackageFragment();
			// }
			// return outer;

			if (type instanceof AExplicitFunctionDefinition)
			{
				return ((AExplicitFunctionDefinition) type).getClassDefinition();
			}
			if (type instanceof AExplicitOperationDefinition)
			{
				return ((AExplicitOperationDefinition) type).getClassDefinition();
			}

			return null;
		}

		public void dispose()
		{
			fImageMap.clear();
			fImageMap = null;
		}

		public void addListener(ILabelProviderListener listener)
		{
		}

		public boolean isLabelProperty(Object element, String property)
		{
			return false;
		}

		public void removeListener(ILabelProviderListener listener)
		{
		}
	}

	/**
	 * Provides a label and image for the details area of the dialog
	 */
	class DebugTypeDetailsLabelProvider extends DebugTypeLabelProvider
	{
		public String getText(Object element)
		{
			if (element instanceof INode)
			{
				INode type = (INode) element;
				String name = getDeclaringContainerName(type);
				if (name != null)
				{
					if (name.equals(LauncherMessages.MainMethodLabelProvider_0))
					{
						// IProject project =vdmProject;// type.getJavaProject();
						if (project != null)
						{
							// try {
							return project.getLocation().toOSString().substring(1)
									+ " - " + name; //$NON-NLS-1$
							// }
							// catch (JavaModelException e) {JDIDebugUIPlugin.log(e);}
						}
					} else
					{
						return name;
					}
				}
			}
			return null;
		}

		public Image getImage(Object element)
		{
			if (element instanceof INode)
			{
				return super.getImage(getDeclaringContainer((INode) element));
			}
			return super.getImage(element);
		}
	}

	/**
	 * Simple items filter
	 */
	class DebugTypeItemsFilter extends ItemsFilter
	{
		public boolean isConsistentItem(Object item)
		{
			return item instanceof INode;
		}

		public boolean matchItem(Object item)
		{
			if (!(item instanceof INode)
					|| !Arrays.asList(fTypes).contains(item))
			{
				return false;
			}
			return matches(AstNameUtil.getName((INode) item));
		}
	}

	/**
	 * The selection history for the dialog
	 */
	class DebugTypeSelectionHistory extends SelectionHistory
	{
		protected Object restoreItemFromMemento(IMemento memento)
		{
			// IJavaElement element = JavaCore.create(memento.getTextData());
			Object element = null;
			return element instanceof INode ? element : null;
		}

		protected void storeItemToMemento(Object item, IMemento memento)
		{
			if (item instanceof INode)
			{
				// memento.putTextData(((INode) item).getHandleIdentifier());
			}
		}
	}

	private static final String SETTINGS_ID = "";// JDIDebugUIPlugin.getUniqueIdentifier() + ".MAIN_METHOD_SELECTION_DIALOG"; //$NON-NLS-1$
	private INode[] fTypes = null;
	private IProject project;

	/**
	 * Constructor
	 * 
	 * @param elements
	 *            the types to display in the dialog
	 */
	public DebugTypeSelectionDialog(Shell shell, INode[] elements,
			String title, IProject project)
	{
		super(shell, false);
		setTitle(title);
		fTypes = elements;
		this.project = project;
		setMessage(LauncherMessages.VdmMainTab_Choose_a_main__type_to_launch__12);
		setInitialPattern("**"); //$NON-NLS-1$
		setListLabelProvider(new DebugTypeLabelProvider());
		setDetailsLabelProvider(new DebugTypeDetailsLabelProvider());
		setSelectionHistory(new DebugTypeSelectionHistory());
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createDialogArea(Composite parent)
	{
		Control ctrl = super.createDialogArea(parent);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(ctrl, IJavaDebugHelpContextIds.SELECT_MAIN_METHOD_DIALOG);
		return ctrl;
	}

	/**
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#getDialogSettings()
	 */
	protected IDialogSettings getDialogSettings()
	{
		IDialogSettings settings = VdmDebugPlugin.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection(SETTINGS_ID);
		if (section == null)
		{
			section = settings.addNewSection(SETTINGS_ID);
		}
		return section;
		// return null;
	}

	/**
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#getItemsComparator()
	 */
	protected Comparator<Object> getItemsComparator()
	{
		Comparator<Object> comp = new Comparator<Object>()
		{
			public int compare(Object o1, Object o2)
			{
				if (o1 instanceof INode && o2 instanceof INode)
				{
					return AstNameUtil.getName((INode) o1).compareTo(AstNameUtil.getName((INode) o2));
				}
				return -1;
			}
		};
		return comp;
	}

	/**
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#validateItem(java.lang.Object)
	 */
	protected IStatus validateItem(Object item)
	{
		return Status.OK_STATUS;
	}

	/**
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#createExtendedContentArea(org.eclipse.swt.widgets.Composite)
	 */
	protected Control createExtendedContentArea(Composite parent)
	{
		return null;
	}

	/**
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#createFilter()
	 */
	protected ItemsFilter createFilter()
	{
		return new DebugTypeItemsFilter();
	}

	/**
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#fillContentProvider(org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.AbstractContentProvider,
	 *      org.eclipse.ui.dialogs.FilteredItemsSelectionDialog.ItemsFilter, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void fillContentProvider(AbstractContentProvider contentProvider,
			ItemsFilter itemsFilter, IProgressMonitor progressMonitor)
			throws CoreException
	{
		if (fTypes != null && fTypes.length > 0)
		{
			for (int i = 0; i < fTypes.length; i++)
			{
				if (itemsFilter.isConsistentItem(fTypes[i]))
				{
					contentProvider.add(fTypes[i], itemsFilter);
				}
			}
		}
	}

	/**
	 * @see org.eclipse.ui.dialogs.FilteredItemsSelectionDialog#getElementName(java.lang.Object)
	 */
	public String getElementName(Object item)
	{
		if (item instanceof INode)
		{
			return AstNameUtil.getName((INode) item);
		}
		return null;
	}
}
