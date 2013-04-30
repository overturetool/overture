package org.overture.ide.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.overture.ast.node.INode;
import org.overture.ide.core.resources.IVdmSourceUnit;
import org.overture.ide.ui.internal.viewsupport.DecorationgVdmLabelProvider;
import org.overture.ide.ui.internal.viewsupport.VdmUILabelProvider;
import org.overture.ide.ui.outline.VdmOutlineTreeContentProvider;

public class AdapterFactory2 implements IAdapterFactory
{
	public static class VdmSourcenitWorkbenchAdapter implements
			IWorkbenchAdapter
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

	}

	public static class NodeWorkbenchAdapter implements IWorkbenchAdapter
	{
		INode node;
		ILabelProvider labelProvider;
		VdmOutlineTreeContentProvider contentProvider = new VdmOutlineTreeContentProvider();

		public NodeWorkbenchAdapter(INode node)
		{
			this.node = node;
			this.labelProvider = new DecorationgVdmLabelProvider(new VdmUILabelProvider());
		}

		@Override
		public Object[] getChildren(Object o)
		{
			Object[] children = this.contentProvider.getChildren(o);
			if (children == null)
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
			return null;
		}

		@Override
		public String getLabel(Object o)
		{
			return labelProvider.getText(o);
		}

		@Override
		public Object getParent(Object o)
		{
			// TODO Auto-generated method stub
			return null;
		}

	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType)
	{
		if (adapterType == IWorkbenchAdapter.class)
		{
			if (adaptableObject instanceof IVdmSourceUnit)
			{
				return new VdmSourcenitWorkbenchAdapter((IVdmSourceUnit) adaptableObject);
			} else if (adaptableObject instanceof INode)
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

}
