package org.overture.interpreter.traces;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Vector;

import org.overture.interpreter.traces.util.RandomList;

public class ReducedTestSequence extends TestSequence
{

	public interface ShapeIterator extends Iterator<CallSequence> 
	{
		int iterationCount();
	}
	
	/**
	 * A random reduction iterator that only returns the elements within the random restrictions
	 * 
	 * @author kel
	 */
	private static class RandomReductionIterator implements ShapeIterator
	{
		private TestSequence data;
		private int size;

		private int nextIndex;
		private RandomList randomList;
		private int numberOfTests;

		public RandomReductionIterator(TestSequence data, int size,
				long numberOfTests, Random prng)
		{
			this.data = data;
			this.size = size;
			this.numberOfTests = (int) numberOfTests;

			final int N = size;
			final int R = this.numberOfTests;

			this.randomList = new RandomList(N, R, prng);

			computeNextIndex();
		}

		private void computeNextIndex()
		{
			this.nextIndex = randomList.next() - 1;
		}

		@Override
		public boolean hasNext()
		{
			return this.nextIndex >= 0 && this.nextIndex < size;
		}

		@Override
		public CallSequence next()
		{
			CallSequence next = data.get(nextIndex);

			computeNextIndex();

			return next;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public int iterationCount()
		{
			return this.numberOfTests;
		}
	}

	/**
	 * A shape reduction iterator that only returns the elements within the shape restrictions
	 * 
	 * @author kel
	 */
	private static class ShapeReductionIterator implements ShapeIterator
	{

		private TestSequence data;
		private long delta;
		private Random prng;
		private int size;
		private TraceReductionType type;

		private List<Integer> chosenTestIndices = new Vector<Integer>();
		private int choosenIndexPtr = 0;
		private Iterator<CallSequence> choosenTestItr;
		private int choosenTestIndexPtr = 0;

		public ShapeReductionIterator(TestSequence data, int size, long delta,
				Random prng, TraceReductionType type)
		{
			this.data = data;
			this.delta = delta;
			this.prng = prng;
			this.size = size;
			this.type = type;

			initialize();
		}

		private void initialize()
		{
			Map<String, List<Integer>> map = new HashMap<String, List<Integer>>();

			int index = 0;
			for (Iterator<CallSequence> itr = data.iterator(); itr.hasNext();)
			{
				String shape = itr.next().toShape(type);
				List<Integer> subset = map.get(shape);

				if (subset == null)
				{
					subset = new Vector<Integer>();
					map.put(shape, subset);
				}

				subset.add(index);

				index++;
			}

			String[] shapes = map.keySet().toArray(new String[0]);

			if (size - delta < shapes.length)
			{
				// We must keep one test for each shape
				delta = size - shapes.length;
			}

			for (long i = 0; i < delta; i++)
			{
				int x = prng.nextInt(shapes.length);
				List<Integer> tests = map.get(shapes[x]);
				int s = tests.size();

				if (s < 2)
				{
					i--; // Find another group
				} else
				{
					tests.remove(prng.nextInt(s));
				}
			}

			for (Entry<String, List<Integer>> entry : map.entrySet())
			{
				chosenTestIndices.addAll(map.get(entry.getKey()));
			}

			Collections.sort(chosenTestIndices);
		}

		@Override
		public boolean hasNext()
		{
			return !chosenTestIndices.isEmpty()
					&& choosenIndexPtr < chosenTestIndices.size();
		}

		@Override
		public CallSequence next()
		{
			if (choosenTestItr == null)
			{
				choosenTestItr = data.iterator();
				choosenTestIndexPtr = -1;
			}

			int index = chosenTestIndices.get(choosenIndexPtr++);

			CallSequence test;
			do
			{
				test = choosenTestItr.next();
				choosenTestIndexPtr++;
			} while (choosenTestIndexPtr < index);
			
			return test;

		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public int iterationCount()
		{
			return chosenTestIndices.size();
		}

	}

	/**
	 * serial
	 */
	private static final long serialVersionUID = 1L;

	private final TestSequence data;

	private final boolean enabled;

	private final Random prng;

	private int size;

	private final float subset;

	private final TraceReductionType type;
	
	private Iterator<CallSequence> iterator;

	public ReducedTestSequence(TestSequence data, float subset,
			TraceReductionType type, long seed)
	{
		this.data = data;
		this.subset = subset;
		this.type = type;
		this.prng = new Random(seed);
		
		this.size = this.data.size();
		long n = Math.round(Math.ceil(size * subset));
		this.enabled = n < size;
	}

	@Override
	public synchronized Iterator<CallSequence> iterator()
	{
		if(iterator != null)
		{
			return iterator;
		}
		
		if (!enabled || type == TraceReductionType.NONE)
		{
			iterator = this.data.iterator();
			return iterator;
		}

		long n = Math.round(Math.ceil(size * subset));
		long delta = size - n;
		switch (type)
		{
			case RANDOM:
				iterator = new RandomReductionIterator(this.data, size, n, prng);
				return iterator;
			case SHAPES_NOVARS:
			case SHAPES_VARNAMES:
			case SHAPES_VARVALUES:
				iterator = new ShapeReductionIterator(this.data, size, delta, prng, type);
				return iterator;
			case NONE:
			default:
				iterator = this.data.iterator();
				return iterator;
		}
	}
	
	@Override
	public synchronized int size()
	{
		if(iterator == null)
		{
			iterator();
		}
		
		if(iterator instanceof ShapeIterator)
		{
			return ((ShapeIterator) iterator).iterationCount();
		}
		
		return this.data.size();
	}

	// private void randomReduction(long delta, Random prng)
	// {
	// int s = size();
	//
	// for (long i = 0; i < delta; i++)
	// {
	// int x = prng.nextInt(s);
	// this.remove(x);
	// s--;
	// }
	// }

	//
	// private void reduce(float subset, TraceReductionType type, long seed)
	// {
	// Random prng = new Random(seed);
	// int s = size();
	// long n = Math.round(Math.ceil(s * subset));
	//
	// if (n < s)
	// {
	// long delta = s - n;
	//
	// switch (type)
	// {
	// case NONE:
	// break;
	//
	// case RANDOM:
	// randomReduction(delta, prng);
	// break;
	//
	// case SHAPES_NOVARS:
	// case SHAPES_VARNAMES:
	// case SHAPES_VARVALUES:
	// shapesReduction(delta, type, prng);
	// break;
	//
	// default:
	// throw new InternalException(53, "Unknown trace reduction");
	// }
	// }
	// }

//	private void shapesReduction(long delta, TraceReductionType type,
//			Random prng)
//	{
//		Map<String, TestSequence> map = new HashMap<String, TestSequence>();
//
//		for (CallSequence cs : this)
//		{
//			String shape = cs.toShape(type);
//			TestSequence subset = map.get(shape);
//
//			if (subset == null)
//			{
//				subset = new TestSequence();
//				map.put(shape, subset);
//			}
//
//			subset.add(cs);
//		}
//
//		String[] shapes = map.keySet().toArray(new String[0]);
//
//		if (size() - delta < shapes.length)
//		{
//			// We must keep one test for each shape
//			delta = size() - shapes.length;
//		}
//
//		for (long i = 0; i < delta; i++)
//		{
//			int x = prng.nextInt(shapes.length);
//			TestSequence tests = map.get(shapes[x]);
//			int s = tests.size();
//
//			if (s < 2)
//			{
//				i--; // Find another group
//			} else
//			{
//				tests.remove(prng.nextInt(s));
//			}
//		}
//
//		clear();
//
//		for (Entry<String, TestSequence> entry : map.entrySet())
//		{
//			addAll(map.get(entry.getKey()));
//		}
//	}
}
