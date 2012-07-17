import junit.framework.TestCase;

import org.overture.ast.expressions.AE1Exp;
import org.overture.ast.expressions.AE1ExpInterpreter;
import org.overture.ast.expressions.AE2ExpInterpreter;
import org.overture.ast.expressions.AE3Exp;
import org.overture.ast.expressions.AE3ExpInterpreter;
import org.overture.ast.expressions.AE4ExpInterpreter;
import org.overture.ast.expressions.EExp;
import org.overture.ast.expressions.EExpInterpreter;
import org.overture.ast.node.NodeEnum;
import org.overture.ast.node.NodeEnumInterpreter;

public class TestCase1 extends TestCase
{
	AE1Exp base = null;
	AE2ExpInterpreter extended = null;

	@Override
	protected void setUp() throws Exception
	{
		base = new AE1Exp();
		extended = new AE2ExpInterpreter();
	}

	public void testenum()
	{
		base.kindNode();
		extended.kindNode();

		base.kindPExp();
		try
		{
			extended.kindPExp();
			fail("kindPExp successeded but should have failed");
		} catch (RuntimeException e)
		{

		}

		extended.kindPExpInterpreter();
	}

	public void testBaseVisitor()
	{
		System.out.println("testBaseVisitor");
		base.apply(new BaseVisitor());

		try
		{
			extended.apply(new BaseVisitor());
			fail("apply with BaseVisitor successeded but should have failed since this exp is not in the base ast");
		} catch (RuntimeException e)
		{

		}

	}

	@SuppressWarnings("deprecation")
	public void testAddedFieldToNode()
	{

		System.out.println("Base visit E3");
		AE1Exp field1Base = new AE1Exp();
		AE3Exp rootBase = new AE3Exp(field1Base);
		DBaseVisitor vBase = new DBaseVisitor();
		rootBase.apply(vBase);
		assertTrue("Root must be visited first", vBase.visitedNodes.get(0).equals(rootBase));
		assertTrue("Field 1 of root must be visited second", vBase.visitedNodes.get(1).equals(field1Base));
		assertEquals(vBase.visitedNodes.size(), 2);

		// System.out.println("Mixed visit E3");
		// AE1ExpInterpreter field1_ = new AE1ExpInterpreter();
		// AE3Exp mixtedRoot = new AE3Exp(field1_);
		// try
		// {
		// mixtedRoot.apply(vBase);
		// fail("E1 interpreter can not be visited with a base visitor. To allow this the extended visitor must be used and have an explicit delegation to the base implementation");
		// } catch (RuntimeException e)
		// {
		//
		// }
		//
		// System.out.println("Mixed Extended visit E3");
		// DExtendedDelegateVisitor extendedDelegateVisitor = new DExtendedDelegateVisitor();
		// mixtedRoot.apply(extendedDelegateVisitor);
		// System.out.println(extendedDelegateVisitor.visitedNodes);

		System.out.println("Extended visit E3");
		AE1ExpInterpreter field1_ = new AE1ExpInterpreter();
		AE2ExpInterpreter field2_ = extended;
		AE3ExpInterpreter root = new AE3ExpInterpreter(field1_, field2_);

		DExtendedVisitor vExtended = new DExtendedVisitor();
		root.apply(vExtended);

		assertTrue("Root must be visited first", vExtended.visitedNodes.get(0).equals(root));
		assertTrue("Field 1 of root must be visited second", vExtended.visitedNodes.get(1).equals(field1_));
		assertTrue("Field 2 of root must be visited second", vExtended.visitedNodes.get(2).equals(field2_));
		System.out.println(vExtended.visitedNodes);
	}

	public void testExtendVisitor()
	{
		System.out.println("testExtendVisitor");
		base.apply(new ExtendedVisitor());
		extended.apply(new ExtendedVisitor());

	}

	public void testPExtendedEnums()
	{
		for (NodeEnum e1 : NodeEnum.values())
		{
			try
			{
				Enum.valueOf(NodeEnumInterpreter.class, e1.toString());
			} catch (IllegalArgumentException e)
			{
				fail(e1 + " should exist in both ASTs");
			}
		}

		for (EExp e1 : EExp.values())
		{
			try
			{
				Enum.valueOf(EExpInterpreter.class, e1.toString());
			} catch (IllegalArgumentException e)
			{
				fail(e1 + " should exist in both ASTs");
			}
		}

		try
		{
			if (Enum.valueOf(NodeEnum.class, NodeEnumInterpreter.STM.toString()) != null)
			{
				fail("STM should exist in both ASTs");
			}
			fail("STM should exist in both ASTs");
		} catch (IllegalArgumentException e)
		{

		}

		try
		{
			if (Enum.valueOf(EExp.class, EExpInterpreter.E2.toString()) != null)
			{
				fail("E2 should exist in both ASTs");
			}
		} catch (IllegalArgumentException e)
		{

		}
	}
	
	@SuppressWarnings("deprecation")
	public void testclone()
	{
		AE2ExpInterpreter a=	extended.clone();
		System.out.println(a);
		
		AE1ExpInterpreter field1_ = new AE1ExpInterpreter();
		AE2ExpInterpreter field2_ = extended;
		AE3ExpInterpreter root = new AE3ExpInterpreter(field1_, field2_);
		
		AE3ExpInterpreter  r2 = root.clone();
		if(r2==root)
		{
			fail("Clone did not work");
		}
		
		if(r2.getField1()==field1_)
		{
			fail("Clone did not work");
		}
		
		if(r2.getField2()==field2_)
		{
			fail("Clone did not work");
		}
	}
	
	@SuppressWarnings("deprecation")
	public void testParent()
	{
		AE1ExpInterpreter field1_ = new AE1ExpInterpreter();
		AE2ExpInterpreter field2_ = extended;
		AE3ExpInterpreter root = new AE3ExpInterpreter(field1_, field2_);
		if(field1_.parent()!=root)
		{
			fail("Parent not set correctly");
		}
		if(field2_.parent()!=root)
		{
			fail("Parent not set correctly");
		}
		if(root.parent()!=null)
		{
			fail("Parent not set correctly");
		}
		
		AE3ExpInterpreter root2 = new AE3ExpInterpreter(field1_, field2_);
		
		if(field1_.parent()!=root2)
		{
			fail("Parent not set correctly");
		}
		if(field2_.parent()!=root2)
		{
			fail("Parent not set correctly");
		}
		if(root2.parent()!=null)
		{
			fail("Parent not set correctly");
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public void testGraphField()
	{
		AE1ExpInterpreter field1_ = new AE1ExpInterpreter();
		if(field1_.parent()!=null)
		{
			fail("Parent not set correctly");
		}
		AE4ExpInterpreter e4 = new AE4ExpInterpreter(field1_);
		if(field1_.parent()!=e4)
		{
			fail("Parent not set correctly");
		}
		
		AE4ExpInterpreter e4_1 = new AE4ExpInterpreter(field1_);
		
		if(field1_.parent()!=e4)
		{
			fail("Parent not set correctly");
		}
		
		AE3ExpInterpreter e3 = new AE3ExpInterpreter(field1_, null);
		
		if(field1_.parent()!=e3)
		{
			fail("Parent not set correctly");
		}
		
		if(e4.getField2()==null || e4_1.getField2()==null)
		{
			fail("Graph nodes should not be removed when used in other nodes");
		}
		
	}
}
