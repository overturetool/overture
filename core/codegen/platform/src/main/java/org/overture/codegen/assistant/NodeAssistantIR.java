package org.overture.codegen.assistant;

import java.util.LinkedList;
import java.util.List;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.PIR;

public class NodeAssistantIR extends AssistantBase
{
	public NodeAssistantIR(AssistantManager assistantManager)
	{
		super(assistantManager);
	}

	public void addMetaData(PIR node, List<ClonableString> extraMetaData,
			boolean prepend)
	{
		List<ClonableString> allMetaData = buildData(node.getMetaData(), extraMetaData, prepend);

		node.setMetaData(allMetaData);
	}

	public List<ClonableString> buildData(
			List<? extends ClonableString> currentMetaData,
			List<ClonableString> extraMetaData, boolean prepend)
	{
		if (extraMetaData == null || extraMetaData.isEmpty())
		{
			return new LinkedList<>();
		}

		List<ClonableString> allMetaData = new LinkedList<ClonableString>();

		if (prepend)
		{
			allMetaData.addAll(extraMetaData);
			allMetaData.addAll(currentMetaData);
		} else
		{
			allMetaData.addAll(currentMetaData);
			allMetaData.addAll(extraMetaData);
		}

		return allMetaData;
	}
}
