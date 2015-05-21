package org.overture.vdm2jml.tests;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.util.ClonableString;
import org.overture.codegen.analysis.violations.UnsupportedModelingException;
import org.overture.codegen.cgast.PCG;
import org.overture.codegen.cgast.declarations.AClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.vdm2jml.tests.util.NullableCollector;

public class NullableTest extends AnnotationTestsBase
{
	private static final String JML_NULLABLE = "//@ nullable;";

	private static Set<PCG> nullables;

	@BeforeClass
	public static void init() throws AnalysisException,
			UnsupportedModelingException,
			org.overture.codegen.cgast.analysis.AnalysisException
	{
		nullables = new HashSet<PCG>();

		List<AClassDeclCG> classes = getClasses("Nullable.vdmsl");

		NullableCollector collector = new NullableCollector();
		
		if (classes != null)
		{
			for (AClassDeclCG c : classes)
			{
				c.apply(collector);
			}
		}

		nullables.addAll(collector.getNullables());
	}
	
	@Test
	public void rightNumberOfNullables()
	{
		Assert.assertEquals("Got an unexpected number of @nullable nodes", 7, nullables.size());
	}

	@Test
	public void value()
	{
		assertNullableField("vOpt");
	}

	@Test
	public void stateField()
	{
		assertNullableField("sOpt");
	}

	@Test
	public void recField()
	{
		assertNullableField("rOpt");
	}
	
	@Test
	public void varCheckOpA()
	{
		assertNullableVar("aOpt");
	}
	
	@Test
	public void varCheckOpB()
	{
		assertNullableVar("bOpt");
	}
	
	// There should be no @nullables in opC
	
	@Test
	public void varCheckOpDField1()
	{
		assertNullableVar("d1Opt");
	}
	
	@Test
	public void varCheckOpDField2()
	{
		assertNullableVar("d2Opt");
	}
	
	public void assertNullableField(String name)
	{
		for (PCG n : nullables)
		{
			if (n instanceof AFieldDeclCG)
			{
				if (((AFieldDeclCG) n).getName().equals(name))
				{
					Assert.assertTrue("Expected field '" + name
							+ "' to be nullable", isNullable(n));
					return;
				}
			}
		}

		Assert.assertTrue("Could not find field '" + name + "'", false);
	}

	public void assertNullableVar(String name)
	{
		for (PCG n : nullables)
		{
			if (n instanceof AVarDeclCG)
			{
				AVarDeclCG var = (AVarDeclCG) n;

				if (var.getPattern() instanceof AIdentifierPatternCG)
				{
					AIdentifierPatternCG id = (AIdentifierPatternCG) var.getPattern();

					if (id.getName().equals(name))
					{
						Assert.assertTrue("Expected variable declaration '"
								+ name + "' to be nullable", isNullable(n));
						return;
					}
				} else
				{
					Assert.assertTrue("Expected pattern of local variable declaration to "
							+ "be an identifier pattern at this point. Got: "
							+ var.getPattern(), false);
				}
			}
		}

		Assert.assertTrue("Could not find variable declaration '" + name + "'", false);
	}
	
	private boolean isNullable(PCG n)
	{
		for (ClonableString m : n.getMetaData())
		{
			if (m.value.equals(JML_NULLABLE))
			{
				return true;
			}
		}

		return false;
	}
}
