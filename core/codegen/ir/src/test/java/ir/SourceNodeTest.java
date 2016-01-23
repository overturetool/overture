package ir;

import org.junit.Assert;
import org.junit.Test;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.ir.SourceNode;

public class SourceNodeTest
{
	@Test
	public void nodeCopy()
	{
		ABooleanBasicType vdmNode = new ABooleanBasicType();

		ABoolBasicTypeCG irFirst = new ABoolBasicTypeCG();
		irFirst.setSourceNode(new SourceNode(vdmNode));

		ABoolBasicTypeCG irSecond = new ABoolBasicTypeCG();

		Assert.assertTrue("Expected no source node at this point", irSecond.getSourceNode() == null);

		SourceNode.copy(irFirst, irSecond);

		Assert.assertTrue("Got unexpected VDM source node", irSecond.getSourceNode().getVdmNode() == vdmNode);
	}
}
