package org.overture.vdm2jml.tests;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.runtime.V2J;
import org.overture.codegen.vdm2jml.predgen.info.AbstractSetSeqInfo;
import org.overture.codegen.vdm2jml.predgen.info.MapInfo;
import org.overture.codegen.vdm2jml.predgen.info.SeqInfo;
import org.overture.codegen.vdm2jml.predgen.info.SetInfo;
import org.overture.codegen.vdm2jml.predgen.info.TupleInfo;

public class V2JCallTests
{
	@Test
	public void checkIsInjMap()
	{
		assertMethod(MapInfo.IS_INJECTIVE_MAP_METHOD, Object.class);
	}
	
	@Test
	public void checkIsMap()
	{
		assertMethod(MapInfo.IS_MAP_METHOD, Object.class);
	}
	
	@Test
	public void checkGetDom()
	{
		assertMethod(MapInfo.GET_DOM_METHOD, Object.class, int.class);
	}
	
	@Test
	public void checkGetRng()
	{
		assertMethod(MapInfo.GET_RNG_METHOD, Object.class, int.class);
	}
	
	@Test
	public void checkIsSet()
	{
		assertMethod(SetInfo.IS_SET_METHOD, Object.class);
	}
	
	@Test
	public void checkIsTup()
	{
		assertMethod(TupleInfo.IS_TUP_METHOD_NAME, Object.class, int.class);
	}
	
	@Test
	public void checkTupField()
	{
		assertMethod(TupleInfo.GET_FIELD_METHOD_NAME, Object.class, int.class);
	}
	
	@Test
	public void checkGetSeqElement()
	{
		assertMethod(AbstractSetSeqInfo.GET_METHOD, Object.class, int.class);
	}
	
	@Test
	public void checkSeqSize()
	{
		assertMethod(AbstractSetSeqInfo.SIZE__METHOD, Object.class);
	}
	
	@Test
	public void checkIsSeq()
	{
		assertMethod(SeqInfo.IS_SEQ_METHOD, Object.class);
	}
	
	@Test
	public void checkIsSeq1()
	{
		assertMethod(SeqInfo.IS_SEQ1_METHOD, Object.class);
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
