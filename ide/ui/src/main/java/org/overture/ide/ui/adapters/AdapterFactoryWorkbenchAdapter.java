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
package org.overture.ide.ui.adapters;

import java.util.List;
import java.util.Vector;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.IWorkbenchAdapter3;
import org.overture.ast.node.INode;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.ui.IVdmUiConstants;
import org.overture.ide.ui.VdmUIPlugin;
import org.overture.ide.ui.internal.viewsupport.DecorationgVdmLabelProvider;
import org.overture.ide.ui.internal.viewsupport.VdmColoringLabelProvider;
import org.overture.ide.ui.internal.viewsupport.VdmUILabelProvider;
import org.overture.ide.ui.outline.VdmOutlineTreeContentProvider;

public class AdapterFactoryWorkbenchAdapter implements IAdapterFactory
{
	public static class VdmSourcenitWorkbenchAdapter implements
			IWorkbenchAdapter, IWorkbenchAdapter3
	{

		private IVdmSourceUnit sourceUnit;
		private IWorkbenchAdapter adapter;

		public VdmSourcenitWorkbenchAdapter(IVdmSourceUnit sourceUnit)
		{
			this.sourceUnit = sourceUnit;
			this.adapter = (IWorkbenchAdapter) Platform.getAdapterManager().getAdapter(sourceUnit.getFile(), IWorkbenchAdapter.class);
		}

		@Override
		public Object[] getChildren(Object o)
		{
			return sourceUnit.getParseList().toArray();
		}

		@Override
		public ImageDescriptor getImageDescriptor(Object object)
		{
			return this.adapter.getImageDescriptor(object);
		}

		@Override
		public String getLabel(Object o)
		{
			if (o instanceof INode)
			{
				System.out.println(o);
			}
			return this.adapter.getLabel(o);
		}

		@Override
		public Object getParent(Object o)
		{
			return this.adapter.getParent(o);
		}

		@Override
		public StyledString getStyledText(Object element)
		{
			return new StyledString(getLabel(element));
		}

	}

	public static class NodeWorkbenchAdapter implements IWorkbenchAdapter,
			IWorkbenchAdapter3
	{
		INode node;
		VdmColoringLabelProvider labelProvider;
		VdmOutlineTreeContentProvider contentProvider = new VdmOutlineTreeContentProvider();
		List<IOvertureWorkbenchAdapter> extensions = null;

		public NodeWorkbenchAdapter(INode node)
		{
			this.node = node;
			this.labelProvider = new DecorationgVdmLabelProvider(new VdmUILabelProvider());
			this.extensions = getOvertureWorkbenchAdapterExtensions();
		}

		@Override
		public Object[] getChildren(Object o)
		{
			Object[] children = this.contentProvider.getChildren(o);
			if (children == null)
			{
				for (IOvertureWorkbenchAdapter ext : extensions)
				{
					children = ext.getChildren(o);
					if (children != null)
					{
						break;
					}
				}
				
			}
			if(children == null)
			{
				children = new Object[0];
			}
			return children;
		}

		@Override
		public ImageDescriptor getImageDescriptor(Object object)
		{
			Image img = labelProvider.getImage(object);

			if (img != null)
			{
				return ImageDescriptor.createFromImage(img);
			}

			ImageDescriptor descriptor = null;
			for (IOvertureWorkbenchAdapter ext : extensions)
			{
				descriptor = ext.getImageDescriptor(object);
				if (descriptor != null)
				{
					break;
				}
			}

			return descriptor;
		}

		@Override
		public String getLabel(Object o)
		{
			String label = labelProvider.getText(o);

			if (label == null)
			{
				for (IOvertureWorkbenchAdapter ext : extensions)
				{
					label = ext.getLabel(o);
					if (label != null)
					{
						break;
					}
				}
			}

			if (label == null)
			{
				label = "Unsupported type reached: " + o;
			}

			return label;
		}

		@Override
		public Object getParent(Object o)
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public StyledString getStyledText(Object element)
		{
			StyledString text = labelProvider.getStyledStringProvider().getStyledText(element);

			if (text == null)
			{
				for (IOvertureWorkbenchAdapter ext : extensions)
				{
					text = ext.getStyledText(element);
					if (text != null)
					{
						break;
					}
				}
			}

			if (text == null)
			{
				text = new StyledString();
				text.append("Unsupported type reached: " + element);
			}

			return text;
		}

	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		if (adapterType == IWorkbenchAdapter.class
				|| adapterType == IWorkbenchAdapter3.class)
		{
			if (adaptableObject instanceof IVdmSourceUnit)
			{
				return new VdmSourcenitWorkbenchAdapter((IVdmSourceUnit) adaptableObject);
			}
		}

		if (adapterType == IWorkbenchAdapter.class
				|| adapterType == IWorkbenchAdapter3.class)
		{
			if (adaptableObject instanceof INode)
			{
				return new NodeWorkbenchAdapter((INode) adaptableObject);
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[] getAdapterList()
	{
		return new Class[] { IVdmSourceUnit.class, INode.class };
	}

	private static List<IOvertureWorkbenchAdapter> getOvertureWorkbenchAdapterExtensions()
	{
		List<IOvertureWorkbenchAdapter> extensions = new ArrayList<IOvertureWorkbenchAdapter>();
		try
		{
			IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(IVdmUiConstants.EXTENSION_WORKBENCH_DISPLAY);
			for (IConfigurationElement e : config)
			{
				final Object o = e.createExecutableExtension("class");
				if (o instanceof IOvertureWorkbenchAdapter)
				{
					IOvertureWorkbenchAdapter adaptor = (IOvertureWorkbenchAdapter) o;
					extensions.add(adaptor);
				}
			}
		} catch (Exception ex)
		{
			VdmUIPlugin.log(ex);
		}

		return extensions;
	}

}
