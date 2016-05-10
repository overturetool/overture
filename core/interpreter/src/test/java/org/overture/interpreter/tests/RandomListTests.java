package org.overture.interpreter.tests;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;
import org.overture.interpreter.traces.util.RandomList;

public class RandomListTests
{
	@Test
	public void test01()
	{
		long seed = 123;
		Random gen = new Random(seed);
		
		final int N = 1111;
		final int R = 5;
		
		RandomList randList = new RandomList(N, R, gen);
		
		
		List<Integer> actualResults = new LinkedList<>();
		
		for(int i = 0; i < R + 1; i++)
		{
			actualResults.add(randList.next());
		}
		
		List<Integer> expectedNumbers = new LinkedList<>();
		expectedNumbers.add(121);
		expectedNumbers.add(488);
		expectedNumbers.add(597);
		expectedNumbers.add(1015);
		expectedNumbers.add(1033);
		//-1 signals that all R numbers have been generated
		expectedNumbers.add(-1);
		
		Assert.assertTrue(actualResults.equals(expectedNumbers));
	}
}
