package org.overture.vdm2jml.tests;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.runtime.V2J;
import org.overture.codegen.vdm2jml.predgen.info.SeqInfo;
import org.overture.codegen.vdm2jml.predgen.info.TupleInfo;

public class V2JCallTests
{
	@Test
	public void checkIsTup()
	{
		assertMethod(TupleInfo.IS_TUP_METHOD_NAME, Object.class);
	}
	
	@Test
	public void checkTupField()
	{
		assertMethod(TupleInfo.GET_FIELD_METHOD_NAME, Object.class, int.class);
	}
	
	@Test
	public void checkGetSeqElement()
	{
		assertMethod(SeqInfo.GET_METHOD, Object.class, int.class);
	}
	
	@Test
	public void checkSeqSize()
	{
		assertMethod(SeqInfo.SIZE__METHOD, Object.class);
	}
	
	@Test
	public void checkIsSeq()
	{
		assertMethod(SeqInfo.IS_SEQ_METHOD, Object.class);
	}
	
	public void assertMethod(String methodName, Class<?>... paramTypes)
	{
		Method method = null;
		
		try
		{
			method = V2J.class.getMethod(methodName, paramTypes);
		} catch (NoSuchMethodException | SecurityException e)
		{
			// Do nothing
		}

		Assert.assertNotNull("Could not find method '" + methodName + "' in V2J with arguments "
				+ Arrays.toString(paramTypes), method);
	}
}
