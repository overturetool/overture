/*
 * #%~
 * Combinatorial Testing
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
package org.overture.ide.plugins.combinatorialtesting.views.treeView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.widgets.Display;
import org.overture.ast.definitions.ANamedTraceDefinition;
import org.overture.ct.utils.TraceHelperNotInitializedException;
import org.overture.ct.utils.TraceTestResult;
import org.overture.ide.plugins.combinatorialtesting.TracesXmlStoreReader.TraceInfo;
import org.overture.ide.plugins.combinatorialtesting.store.StorageManager;
import org.overture.ide.plugins.combinatorialtesting.views.TraceAstUtility;
import org.xml.sax.SAXException;

public class TraceTreeNode implements IAdaptable, ITreeNode
{

	private ANamedTraceDefinition traceDefinition;
	private ITreeNode parent;
	private List<ITreeNode> children;
	private int testSkippedCount = 0;
	private int testTotal = 0;
	private StorageManager traceStore;
	private TraceInfo info;

	public TraceTreeNode(ANamedTraceDefinition traceDef) throws SAXException,
			IOException, ClassNotFoundException,
			TraceHelperNotInitializedException
	{
		this.traceDefinition = traceDef;
		traceStore = new StorageManager(TraceAstUtility.getProject(traceDef), traceDef);
		this.children = new ArrayList<ITreeNode>();

		setInfo(traceStore.getTraceInfo());

		Integer totalTests = traceStore.getTraceTestCount();
		this.setTestTotal(totalTests);

		this.setSkippedCount(traceStore.getSkippedCount(traceDef.getName().getName()));

		if (totalTests > 0)
		{
			this.addChild(new NotYetReadyTreeNode());
		}
	}

	public ITreeNode getParent()
	{
		return parent;
	}

	public ANamedTraceDefinition getTraceDefinition()
	{
		return traceDefinition;
	}

	public void setSkippedCount(int skippedCount)
	{
		testSkippedCount = skippedCount;
	}

	@Override
	public String toString()
	{
		if (testSkippedCount != 0)
		{
			return getName() + " (" + getTestTotal() + " skipped "
					+ testSkippedCount + ")";
		} else
		{
			return getName() + " (" + getTestTotal() + ")";
		}
	}

	public String getName()
	{

		return traceDefinition.getName().getName();

	}

	public void setParent(ITreeNode parent)
	{
		this.parent = parent;
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter)
	{
		return null;
	}

	public void addChild(ITreeNode child)
	{
		if (!children.contains(child))
		{
			boolean contains = false;
			for (ITreeNode node : getChildren())
			{
				if (node.getName().equals(child.getName()))
				{
					contains = true;
				}
			}
			if (!contains)
			{
				children.add(child);
				child.setParent(this);
			}
		}
	}

	public void removeChild(TraceTestTreeNode child)
	{
		children.remove(child);
		child.setParent(null);
	}

	public List<ITreeNode> getChildren()
	{
		return children;
	}

	public boolean hasChildren()
	{
		return children.size() > 0;
	}

	public boolean hasChild(String name)
	{
		for (ITreeNode node : children)
		{
			if (node.getName().equals(name))
			{
				return true;
			}

		}
		return false;
	}

	/**
	 * @param testTotal
	 *            the testTotal to set
	 */
	public void setTestTotal(int testTotal)
	{
		this.testTotal = testTotal;
	}

	/**
	 * @return the testTotal
	 */
	public int getTestTotal()
	{
		return testTotal;
	}


	public void loadTests() throws Exception
	{
		children.clear();

		Long size = new Long(traceStore.getTraceTestCount());

		GroupSizeCalculator gs = new GroupSizeCalculator(size);

		if (!gs.hasGroups())
		{
			List<TraceTestResult> traceStatus = traceStore.getTraceTests();
			for (TraceTestResult traceTestStatus : traceStatus)
			{
				this.addChild(new TraceTestTreeNode(traceTestStatus));
			}
		} else
		{

			Long currentCount = new Long(0);
			for (int i = 0; i < gs.getNumberOfGroups() - 1
					&& currentCount < size; i++)
			{
				final TraceTestGroup group = new TraceTestGroup(currentCount + 1, currentCount
						+ gs.getGroupSize() + 1);
				currentCount += gs.getGroupSize();
				this.addChild(group);
				Display.getCurrent().syncExec(new Runnable()
				{

					public void run()
					{

						try
						{
							group.loadGroupStatus();
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});

			}
			if (!currentCount.equals(size))
			{
				final TraceTestGroup group = new TraceTestGroup(currentCount + 1, size + 1);
				this.addChild(group);
				Display.getCurrent().syncExec(new Runnable()
				{

					public void run()
					{

						try
						{
							group.loadGroupStatus();
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
			}
		}
	}

	public void unloadTests()
	{
		children.clear();
		children.add(new NotYetReadyTreeNode());
	}

	private void setInfo(TraceInfo info)
	{
		this.info = info;
	}

	public TraceInfo getInfo()
	{
		return info;
	}

	public StorageManager getTraceStore()
	{
		return traceStore;
	}

}
