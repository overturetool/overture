package org.overture.vdm2jml.tests;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.ACharBasicTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.ANat1NumericBasicTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.ir.types.AQuoteTypeIR;
import org.overture.codegen.ir.types.ARatNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARealNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.AStringTypeIR;
import org.overture.codegen.ir.types.ATokenBasicTypeIR;
import org.overture.codegen.runtime.Utils;
import org.overture.codegen.vdm2jml.predgen.info.LeafTypeInfo;

public class LeafTypeCallTests
{
	@Test
	public void boolCheck()
	{
		assertMethod(ABoolBasicTypeIR.class, Object.class);
	}

	@Test
	public void natCheck()
	{
		assertMethod(ANatNumericBasicTypeIR.class, Object.class);
	}

	@Test
	public void nat1Check()
	{
		assertMethod(ANat1NumericBasicTypeIR.class, Object.class);
	}

	@Test
	public void intCheck()
	{
		assertMethod(AIntNumericBasicTypeIR.class, Object.class);
	}

	@Test
	public void ratCheck()
	{
		assertMethod(ARatNumericBasicTypeIR.class, Object.class);
	}

	@Test
	public void realCheck()
	{
		assertMethod(ARealNumericBasicTypeIR.class, Object.class);
	}

	@Test
	public void charCheck()
	{
		assertMethod(ACharBasicTypeIR.class, Object.class);
	}

	@Test
	public void tokenCheck()
	{
		assertMethod(ATokenBasicTypeIR.class, Object.class);
	}

	@Test
	public void quoteCheck()
	{
		assertMethod(AQuoteTypeIR.class, Object.class, Object.class);
	}

	@Test
	public void recTest()
	{
		assertMethod(ARecordTypeIR.class, Object.class, Object.class);
	}

	@Test
	public void stringTest()
	{
		assertMethod(AStringTypeIR.class, Object.class, Object.class);
	}

	private void assertMethod(Class<? extends STypeIR> type,
			Class<?>... paramTypes)
	{
		String methodName = LeafTypeInfo.getUtilsCallMap().get(type);
		Assert.assertNotNull("Could not find method name corresponding to type '"
				+ type + "'", methodName);

		Method method = null;
		try
		{
			method = Utils.class.getMethod(methodName, paramTypes);
		} catch (NoSuchMethodException | SecurityException e)
		{
			// Do nothing
		}

		Assert.assertNotNull("Could not find method corresponding to type ;"
				+ type + "' with arguments "
				+ Arrays.toString(paramTypes), method);
	}
}
