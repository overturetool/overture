import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.overture.codegen.runtime.traces.PermuteArray;

public class PermuteArrayTest
{
	public List<int[]> consPermutations(int n)
	{
		PermuteArray p = new PermuteArray(n);

		List<int[]> intArrays = new LinkedList<int[]>();

		while (p.hasNext())
		{
			int[] next = p.next();
			intArrays.add(next.clone());
		}

		return intArrays;
	}

	@Test
	public void permutateArray1()
	{
		List<int[]> intArrays = consPermutations(1);

		PermutorTest.assertNoOfPermutations(1, intArrays);

		PermutorTest.assertArrayContained(new int[] { 0 }, intArrays);
	}

	@Test
	public void permutateArray2()
	{
		List<int[]> intArrays = consPermutations(2);

		PermutorTest.assertNoOfPermutations(2, intArrays);

		PermutorTest.assertArrayContained(new int[] { 0, 1 }, intArrays);
		PermutorTest.assertArrayContained(new int[] { 1, 0 }, intArrays);
	}

	@Test
	public void permutateArray3()
	{
		List<int[]> intArrays = consPermutations(3);

		PermutorTest.assertNoOfPermutations(6, intArrays);

		PermutorTest.assertArrayContained(new int[] { 0, 1, 2 }, intArrays);
		PermutorTest.assertArrayContained(new int[] { 0, 2, 1 }, intArrays);
		PermutorTest.assertArrayContained(new int[] { 1, 0, 2 }, intArrays);
		PermutorTest.assertArrayContained(new int[] { 1, 2, 0 }, intArrays);
		PermutorTest.assertArrayContained(new int[] { 2, 0, 1 }, intArrays);
		PermutorTest.assertArrayContained(new int[] { 2, 1, 0 }, intArrays);
	}
}
