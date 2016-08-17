import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.overture.codegen.runtime.traces.Permutor;

public class PermutorTest
{
	public List<int[]> consPermutations(int[] limits)
	{
		Permutor p = new Permutor(limits);

		List<int[]> intArrays = new LinkedList<int[]>();

		while (p.hasNext())
		{
			intArrays.add(p.next());
		}

		return intArrays;
	}

	@Test
	public void testPermutor1()
	{
		List<int[]> intArrays = consPermutations(new int[] { 3 });

		assertNoOfPermutations(3, intArrays);

		assertArrayContained(new int[] { 0 }, intArrays);
		assertArrayContained(new int[] { 1 }, intArrays);
		assertArrayContained(new int[] { 2 }, intArrays);
	}

	@Test
	public void testPermutor2()
	{
		List<int[]> intArrays = consPermutations(new int[] { 2, 2 });

		assertNoOfPermutations(4, intArrays);

		assertArrayContained(new int[] { 0, 0 }, intArrays);
		assertArrayContained(new int[] { 1, 0 }, intArrays);
		assertArrayContained(new int[] { 0, 1 }, intArrays);
		assertArrayContained(new int[] { 1, 1 }, intArrays);
	}

	@Test
	public void testPermutor3()
	{
		List<int[]> intArrays = consPermutations(new int[] { 2, 2, 2 });

		assertNoOfPermutations(8, intArrays);

		assertArrayContained(new int[] { 0, 0, 0 }, intArrays);
		assertArrayContained(new int[] { 1, 1, 1 }, intArrays);

		assertArrayContained(new int[] { 1, 0, 0 }, intArrays);
		assertArrayContained(new int[] { 1, 1, 0 }, intArrays);
		assertArrayContained(new int[] { 1, 0, 1 }, intArrays);

		assertArrayContained(new int[] { 0, 1, 0 }, intArrays);
		assertArrayContained(new int[] { 0, 1, 1 }, intArrays);

		assertArrayContained(new int[] { 0, 0, 1 }, intArrays);
	}

	public static void assertNoOfPermutations(int expected,
			List<int[]> intArrays)
	{
		Assert.assertEquals("Expected number of permutations to be "
				+ expected, expected, intArrays.size());
	}

	public static void assertArrayContained(int[] subject,
			List<int[]> intArrays)
	{
		for (int[] arr : intArrays)
		{
			if (Arrays.equals(subject, arr))
			{
				return;
			}
		}

		Assert.fail("Expected " + Arrays.toString(subject)
				+ " to a permutation");
	}
}
