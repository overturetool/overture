package org.overture.interpreter.traces;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.overture.interpreter.traces.util.RandomList;

public class ReducedTestSequence extends TestSequence
{

	/**
	 * A random reduction iterator that only returns the elements within the random restrictions
	 * 
	 * @author kel
	 */
	private static class RandomReductionIterator implements
			Iterator<CallSequence>
	{
		private TestSequence data;
		private int size;

		private int nextIndex;
		private RandomList randomList;
		
		public RandomReductionIterator(TestSequence data, int size, long numberOfTests, Random prng)
		{
			this.data = data;
			this.size = size;
			
			final int N =  size;
			final int R = (int) numberOfTests;
			
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
	}

	/**
	 * A shape reduction iterator that only returns the elements within the shape restrictions
	 * 
	 * @author kel
	 */
	private static class ShapreReductionIterator implements
			Iterator<CallSequence>
	{

		private TestSequence data;
		private long delta;
		private Random prng;
		private int size;
		private TraceReductionType type;

		public ShapreReductionIterator(TestSequence data, int size, long delta,
				Random prng, TraceReductionType type)
		{
			this.data = data;
			this.delta = delta;
			this.prng = prng;
			this.size = size;
			this.type = type;
		}

		@Override
		public boolean hasNext()
		{
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public CallSequence next()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException();
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
		if (!enabled || type == TraceReductionType.NONE)
		{
			return this.data.iterator();
		}

		long n = Math.round(Math.ceil(size * subset));
		long delta = size - n;
		switch (type)
		{
			case RANDOM:
				return new RandomReductionIterator(this.data, size, n, prng);
			case SHAPES_NOVARS:
			case SHAPES_VARNAMES:
			case SHAPES_VARVALUES:
				return new ShapreReductionIterator(this.data, size, delta, prng, type);
			case NONE:
			default:
				return this.data.iterator();
		}

	}

//	private void randomReduction(long delta, Random prng)
//	{
//		int s = size();
//
//		for (long i = 0; i < delta; i++)
//		{
//			int x = prng.nextInt(s);
//			this.remove(x);
//			s--;
//		}
//	}

	//
//	private void reduce(float subset, TraceReductionType type, long seed)
//	{
//		Random prng = new Random(seed);
//		int s = size();
//		long n = Math.round(Math.ceil(s * subset));
//
//		if (n < s)
//		{
//			long delta = s - n;
//
//			switch (type)
//			{
//				case NONE:
//					break;
//
//				case RANDOM:
//					randomReduction(delta, prng);
//					break;
//
//				case SHAPES_NOVARS:
//				case SHAPES_VARNAMES:
//				case SHAPES_VARVALUES:
//					shapesReduction(delta, type, prng);
//					break;
//
//				default:
//					throw new InternalException(53, "Unknown trace reduction");
//			}
//		}
//	}

	private void shapesReduction(long delta, TraceReductionType type,
			Random prng)
	{
		Map<String, TestSequence> map = new HashMap<String, TestSequence>();

		for (CallSequence cs : this)
		{
			String shape = cs.toShape(type);
			TestSequence subset = map.get(shape);

			if (subset == null)
			{
				subset = new TestSequence();
				map.put(shape, subset);
			}

			subset.add(cs);
		}

		String[] shapes = map.keySet().toArray(new String[0]);

		if (size() - delta < shapes.length)
		{
			// We must keep one test for each shape
			delta = size() - shapes.length;
		}

		for (long i = 0; i < delta; i++)
		{
			int x = prng.nextInt(shapes.length);
			TestSequence tests = map.get(shapes[x]);
			int s = tests.size();

			if (s < 2)
			{
				i--; // Find another group
			} else
			{
				tests.remove(prng.nextInt(s));
			}
		}

		clear();

		for (Entry<String, TestSequence> entry : map.entrySet())
		{
			addAll(map.get(entry.getKey()));
		}
	}
}
