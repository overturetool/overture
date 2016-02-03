package org.overture.vdm2jml.tests;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.ir.STypeCG;
import org.overture.codegen.ir.types.ABoolBasicTypeCG;
import org.overture.codegen.ir.types.ACharBasicTypeCG;
import org.overture.codegen.ir.types.AIntNumericBasicTypeCG;
import org.overture.codegen.ir.types.ANat1NumericBasicTypeCG;
import org.overture.codegen.ir.types.ANatNumericBasicTypeCG;
import org.overture.codegen.ir.types.AQuoteTypeCG;
import org.overture.codegen.ir.types.ARatNumericBasicTypeCG;
import org.overture.codegen.ir.types.ARealNumericBasicTypeCG;
import org.overture.codegen.ir.types.ARecordTypeCG;
import org.overture.codegen.ir.types.AStringTypeCG;
import org.overture.codegen.ir.types.ATokenBasicTypeCG;
import org.overture.codegen.runtime.Utils;
import org.overture.codegen.vdm2jml.predgen.info.LeafTypeInfo;

public class LeafTypeCallTests
{
	@Test
	public void boolCheck()
	{
		assertMethod(ABoolBasicTypeCG.class, Object.class);
	}

	@Test
	public void natCheck()
	{
		assertMethod(ANatNumericBasicTypeCG.class, Object.class);
	}

	@Test
	public void nat1Check()
	{
		assertMethod(ANat1NumericBasicTypeCG.class, Object.class);
	}

	@Test
	public void intCheck()
	{
		assertMethod(AIntNumericBasicTypeCG.class, Object.class);
	}

	@Test
	public void ratCheck()
	{
		assertMethod(ARatNumericBasicTypeCG.class, Object.class);
	}

	@Test
	public void realCheck()
	{
		assertMethod(ARealNumericBasicTypeCG.class, Object.class);
	}

	@Test
	public void charCheck()
	{
		assertMethod(ACharBasicTypeCG.class, Object.class);
	}

	@Test
	public void tokenCheck()
	{
		assertMethod(ATokenBasicTypeCG.class, Object.class);
	}

	@Test
	public void quoteCheck()
	{
		assertMethod(AQuoteTypeCG.class, Object.class, Class.class);
	}

	@Test
	public void recTest()
	{
		assertMethod(ARecordTypeCG.class, Object.class, Class.class);
	}
	
	@Test
	public void stringTest()
	{
		assertMethod(AStringTypeCG.class, Object.class, Class.class);
	}

	private void assertMethod(Class<? extends STypeCG> type, Class<?>... paramTypes)
	{
		String methodName = LeafTypeInfo.getUtilsCallMap().get(type);
		Assert.assertNotNull("Could not find method name corresponding to type '" + type + "'", methodName);

		Method method = null;
		try
		{
			method = Utils.class.getMethod(methodName, paramTypes);
		} catch (NoSuchMethodException | SecurityException e)
		{
			// Do nothing
		}

		Assert.assertNotNull("Could not find method corresponding to type ;" + type + "' with arguments "
				+ Arrays.toString(paramTypes), method);
	}
}
