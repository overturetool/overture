package org.overture.ide.plugins.traces.views.treeView;

public class GroupSizeCalculator
{
	private Long size;
	private Double numberOfGroups;
	private Long testCountInGroup;

	public GroupSizeCalculator(Long size)
	{
		this.size = size;

		if (hasGroups())
		{
			numberOfGroups = Math.ceil(size.doubleValue()
					/ TraceTestGroup.GROUP_SIZE);

			if (numberOfGroups > TraceTestGroup.GROUP_SIZE)
				numberOfGroups = TraceTestGroup.GROUP_SIZE.doubleValue();

			testCountInGroup = (size) / numberOfGroups.longValue();

			if (testCountInGroup < TraceTestGroup.GROUP_SIZE
					&& size >= TraceTestGroup.GROUP_SIZE)
				testCountInGroup = TraceTestGroup.GROUP_SIZE; // top up all
																// groups
		}
	}

	public boolean hasGroups()
	{
		return size > TraceTestGroup.GROUP_SIZE;
	}

	public Double getNumberOfGroups()
	{
		return numberOfGroups;
	}

	public Long getGroupSize()
	{
		return testCountInGroup;

	}

	public Long getTotalSize()
	{
		return size;
	}

}
