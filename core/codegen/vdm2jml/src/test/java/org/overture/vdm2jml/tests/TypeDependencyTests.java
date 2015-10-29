package org.overture.vdm2jml.tests;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.overture.ast.modules.AModuleModules;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.ANatNumericBasicTypeCG;
import org.overture.codegen.utils.GeneralCodeGenUtils;
import org.overture.codegen.vdm2jml.JmlGenerator;
import org.overture.codegen.vdm2jml.predgen.info.LeafTypeInfo;
import org.overture.codegen.vdm2jml.predgen.info.NamedTypeInfo;
import org.overture.codegen.vdm2jml.predgen.info.NamedTypeInvDepCalculator;
import org.overture.typechecker.util.TypeCheckerUtil;
import org.overture.typechecker.util.TypeCheckerUtil.TypeCheckResult;

public class TypeDependencyTests extends AnnotationTestsBase
{
	public static final String TEST_RES_TYPE_DEP_ROOT = AnnotationTestsBase.TEST_RESOURCES_ROOT
			+ "type_dep" + File.separatorChar;

	public static final String MODULE = "Entry";

	private List<NamedTypeInfo> typeInfoList;

	public void load(String filename)
	{
		try
		{
			List<File> files = new LinkedList<File>();

			files.add(new File(TEST_RES_TYPE_DEP_ROOT + filename));

			TypeCheckResult<List<AModuleModules>> tcResult = TypeCheckerUtil.typeCheckSl(files);
			
			if(GeneralCodeGenUtils.hasErrors(tcResult))
			{
				Assert.fail("Could not parse/type check VDM model:\n" + GeneralCodeGenUtils.errorStr(tcResult));
			}

			List<AModuleModules> modules = tcResult.result;
			
			Assert.assertTrue("Expected a single module but got "
					+ modules.size(), modules.size() == 1);

			AModuleModules module = modules.get(0);

			JmlGenerator jmlGen = new JmlGenerator();
			initJmlGen(jmlGen);
			
			NamedTypeInvDepCalculator depCalc = new NamedTypeInvDepCalculator(jmlGen.getJavaGen().getInfo());

			module.apply(depCalc);

			this.typeInfoList = depCalc.getTypeDataList();
			Assert.assertTrue("Could not load type info", this.typeInfoList != null);

			for (NamedTypeInfo info : typeInfoList)
			{
				Assert.assertEquals("Expected the enclosing module to be '"
						+ MODULE + "'", MODULE, info.getDefModule());
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			Assert.assertFalse("Problems loading test data: " + e.getMessage(), true);
		}
	}

	private void assertTypeName(String typeName, NamedTypeInfo info)
	{
		Assert.assertEquals("Expected type name to be '" + typeName + "'", typeName, info.getTypeName());
	}

	private String infoStr(NamedTypeInfo info)
	{
		String message = info.getDefModule() + "." + info.getTypeName();
		return message;
	}

	private void assertNoOfLeafs(NamedTypeInfo info, int no)
	{
		Assert.assertEquals("Number of actual leaf types differs from those expected", no, info.getLeafTypesRecursively().size());
	}

	private void assertLeafType(NamedTypeInfo info, Class<?> leafType,
			boolean nullAllowed)
	{
		for (LeafTypeInfo leaf : info.getLeafTypesRecursively())
		{
			if (leafType == leaf.getType().getClass())
			{
				Assert.assertEquals("Found leaf type but 'allowsNull' does not equal", nullAllowed, leaf.isOptional());
				return;
			}
		}

		Assert.assertFalse("Could not find leaf type '" + leafType
				+ " with ' nullAllowed: '" + nullAllowed
				+ "' for named invariant type '" + infoStr(info) + "'", true);
	}

	private void assertNotOptional(NamedTypeInfo info)
	{
		Assert.assertTrue("Expected named type invariant '" + infoStr(info)
				+ "' NOT to be optional", !info.isOptional());
	}

	private void assertTotalNoOfNamedInvTypes(int no)
	{
		Assert.assertTrue("Expected " + no + " named invariant types but got "
				+ typeInfoList.size(), typeInfoList.size() == no);
	}

	private void assertNoInv(NamedTypeInfo info)
	{
		Assert.assertTrue("Expected named invariant type '" + infoStr(info)
				+ "' to NOT have an invariant", !info.hasInv());
	}

	private void assertInv(NamedTypeInfo info)
	{
		Assert.assertTrue("Expected named invariant type '" + infoStr(info)
				+ "' to have an invariant", info.hasInv());
	}

	private NamedTypeInfo getInfo(String typeName)
	{
		NamedTypeInfo info = NamedTypeInvDepCalculator.findTypeInfo(typeInfoList, MODULE, typeName);
		assertTypeName(typeName, info);
		return info;
	}

	@Test
	public void namedInvTypeNat()
	{
		load("Nat.vdmsl");

		// N = nat;
		String typeName = "N";
		NamedTypeInfo info = getInfo(typeName);

		assertTotalNoOfNamedInvTypes(1);
		assertNoOfLeafs(info, 1);
		assertLeafType(info, ANatNumericBasicTypeCG.class, false);
		assertNotOptional(info);
		assertNoInv(info);
	}

	@Test
	public void namedInvTypeNatOpt()
	{
		// N = [nat];
		load("NatOpt.vdmsl");

		String typeName = "N";
		NamedTypeInfo info = getInfo(typeName);

		assertTotalNoOfNamedInvTypes(1);
		assertNoOfLeafs(info, 1);
		assertLeafType(info, ANatNumericBasicTypeCG.class, true);
		assertNotOptional(info);
		assertNoInv(info);
	}

	@Test
	public void unionOfBasicAndOptBasic()
	{
		// CN = nat|[char];
		load("UnionOfBasicAndOptBasic.vdmsl");

		String typeName = "CN";
		NamedTypeInfo info = getInfo(typeName);

		assertTotalNoOfNamedInvTypes(1);
		assertNoOfLeafs(info, 2);
		assertLeafType(info, ANatNumericBasicTypeCG.class, false);
		assertLeafType(info, ACharBasicTypeCG.class, true);
		assertNotOptional(info);
		assertNoInv(info);
	}

	@Test
	public void unionOfNamedInvTypes()
	{
		// CN = C|N;
		// N = nat;
		// C = [char];

		load("UnionOfNamedInvTypes.vdmsl");

		String typeName = "CN";
		NamedTypeInfo info = getInfo(typeName);

		assertTotalNoOfNamedInvTypes(3);
		assertNoOfLeafs(info, 2);
		assertNotOptional(info);
		assertNoInv(info);

		typeName = "N";
		info = getInfo(typeName);

		assertNoOfLeafs(info, 1);
		assertLeafType(info, ANatNumericBasicTypeCG.class, false);
		assertNotOptional(info);
		assertNoInv(info);

		typeName = "C";
		info = getInfo(typeName);

		assertNoOfLeafs(info, 1);
		assertLeafType(info, ACharBasicTypeCG.class, true);
		assertNotOptional(info);
		assertNoInv(info);
	}

	@Test
	public void invariants()
	{
		// CN = C|N
		// inv cn == is_char(cn) => cn = 'a';
		// N = nat
		// inv n == n = 1;
		// C = [char];
		load("Invariants.vdmsl");

		assertInv(getInfo("CN"));
		assertInv(getInfo("N"));
		assertNoInv(getInfo("C"));
	}

	@Test
	public void optionalUnion()
	{
		load("OptionalUnion.vdmsl");
		// CN = [C|N];
		// N = nat;
		// C = char;

		assertNotOptional(getInfo("CN"));
		assertNotOptional(getInfo("N"));
		assertNotOptional(getInfo("C"));
	}
	
	@Test
	public void optionalNamed()
	{
		load("OptionalNamedType.vdmsl");
		//CN = [C]|N;
		//N = nat;
		//C = char;
		
		assertNotOptional(getInfo("CN"));
		assertNotOptional(getInfo("C"));
		assertNotOptional(getInfo("N"));
	}

	@Test
	public void record()
	{
		load("Rec.vdmsl");
		// R :: x : int
		// inv r == r.x = 1;
		//
		// Rn = R | nat;

		String typeName = "Rn";
		NamedTypeInfo info = getInfo(typeName);

		// We do not expect the record to be included
		assertTotalNoOfNamedInvTypes(1);
		// We expect to have the record type 'R' registered as a leaf type
		assertNoOfLeafs(info, 2);
	}

	@Test
	public void recursive()
	{
		load("Recursive.vdmsl");
		// T = nat | T;

		String typeName = "T";

		NamedTypeInfo info = getInfo(typeName);

		assertTotalNoOfNamedInvTypes(1);
		assertNoOfLeafs(info, 1);
		assertLeafType(info, ANatNumericBasicTypeCG.class, false);
	}
	
	@Test
	public void unionWithoutNull()
	{
		load("UnionWithoutNull.vdmsl");
		//CN = C|N;
		//N = nat;
		//C = char;
		
		assertNotOptional(getInfo("CN"));
	}
}
