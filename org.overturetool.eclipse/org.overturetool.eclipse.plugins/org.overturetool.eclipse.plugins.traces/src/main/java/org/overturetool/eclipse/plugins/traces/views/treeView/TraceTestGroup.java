package org.overturetool.eclipse.plugins.traces.views.treeView;

import java.util.ArrayList;
import java.util.List;

import org.overturetool.traces.utility.TraceTestResult;
import org.overturetool.traces.utility.ITracesHelper.TestResultType;

public class TraceTestGroup extends TraceTestTreeNode
{
	public static final Long GROUP_SIZE = new Long(200);
	Long startNumber;
	Long stopNumber;
	TestResultType lastKnownStatus = TestResultType.Unknown;

	public TraceTestGroup(Long startNumber, Long stopNumber)
	{
		super(null);
		this.startNumber = startNumber;
		this.stopNumber = stopNumber-1;
		children = new ArrayList<TraceTestTreeNode>();
		addChild(new NotYetReadyTreeNode());
	}

	private List<TraceTestTreeNode> children;
	TraceTreeNode parent;

	public TraceTreeNode getParent()
	{
		return parent;
	}

	@Override
	public String toString()
	{
		return "[" + startNumber + "..." + (stopNumber) + "]";
	}

	public String getName()
	{
		return toString();
	}

	public void setParent(TraceTreeNode parent)
	{
		this.parent = parent;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter)
	{
		return null;
	}

	public void addChild(TraceTestTreeNode child)
	{
		if (!children.contains(child))
		{
			boolean contains = false;
			for (TraceTestTreeNode node : getChildren())
			{
				if (node.getName().equals(child.getName()))
					contains = true;
			}
			if (!contains)
			{
				children.add(child);
				child.setParent(parent);
			}
		}
	}

	public void removeChild(TraceTestTreeNode child)
	{
		children.remove(child);
		child.setParent(null);
	}

	public List<TraceTestTreeNode> getChildren()
	{
		return children;
	}

	public boolean hasChildren()
	{
		// return children.size() > 0;
		return stopNumber - startNumber > 0;
	}

	public boolean hasChild(String name)
	{
		for (TraceTestTreeNode node : children)
		{
			if (node.getName().equals(name))
				return true;

		}
		return false;
	}
	
	public TestResultType GetStatus() {
		if(lastKnownStatus!=TestResultType.Unknown)
			return lastKnownStatus;
		
		TestResultType status =TestResultType.Unknown;
		
		for (TraceTestTreeNode node : children)
		{
			if(node.GetStatus()== TestResultType.Fail)
				status= node.GetStatus();
			else if(node.GetStatus()== TestResultType.Inconclusive && status!= TestResultType.Fail)
				status= node.GetStatus();
			else if(node.GetStatus()== TestResultType.Ok && status== TestResultType.Unknown)
					status= node.GetStatus();
		}
		lastKnownStatus = status;
		return status;
	}
	
	private void LoadTestNodes() throws Exception
	{
		

		List<TraceTestResult> traceStatus = parent.getTraceHelper().GetTraceTests(
				parent.getParent().getName(),
				parent.getName(),startNumber.intValue(),stopNumber.intValue());
		
		for (TraceTestResult traceTestStatus : traceStatus)
		{
			this.addChild(new TraceTestTreeNode(traceTestStatus));
		}
	}

	public void LoadTests() throws Exception
	{
		children.clear();

//		for (int i = startNumber; i < stopNumber && i < traceStatus.size(); i++)
//		{
//			this.addChild(new TraceTestTreeNode(traceStatus.get(i)));
//		}
		
		Long size = stopNumber-startNumber;
		
		if (size <= TraceTestGroup.GROUP_SIZE)
		{
			LoadTestNodes();
		} else
		{
			Double numberOfGroups = new Double(size) / TraceTestGroup.GROUP_SIZE;
//			Double t = TraceTestGroup.NumberOfLevels(new Long(size), TraceTestGroup.GROUP_SIZE);
			
			if(numberOfGroups>=TraceTestGroup.GROUP_SIZE)
				numberOfGroups=TraceTestGroup.GROUP_SIZE.doubleValue();
			
			Double testCountInGroup = (size.doubleValue()) / TraceTestGroup.GROUP_SIZE;
			
			if(testCountInGroup.longValue()==1)
			{
				testCountInGroup = TraceTestGroup.GROUP_SIZE.doubleValue();
			}
			
			Long currentCount = startNumber-1;
			for (int i = 0; i < numberOfGroups -1; i++)
			{
				TraceTestGroup group = new TraceTestGroup(currentCount+1, currentCount+testCountInGroup.longValue());
				currentCount+=testCountInGroup;
				this.addChild(group);
			}

			TraceTestGroup group = new TraceTestGroup(currentCount+1, stopNumber+1);
			this.addChild(group);

		}

	}

	public void UnloadTests()
	{
		children.clear();
		addChild(new NotYetReadyTreeNode());
	}
	
	public static Double NumberOfLevels(Long count, Long groupSize)
	{
//		levels(x,y) 
//		if x / y > y then 1+ levels(x/y,y)
//		else 1;
		if((count / groupSize) > groupSize)
			return new Double(1) + NumberOfLevels(count/groupSize, groupSize);
		else
			return new Double(1);
		
	}

}
