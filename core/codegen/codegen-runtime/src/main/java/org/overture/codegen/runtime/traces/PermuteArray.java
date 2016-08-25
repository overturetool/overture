/*******************************************************************************
 *
 *	Copyright (C) 2008, 2009 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

// Based on source from http://www.merriampark.com/perm.htm

package org.overture.codegen.runtime.traces;

public class PermuteArray
{
	private int[] a;
	private long numLeft;
	private long total;

	// -----------------------------------------------------------
	// Constructor. WARNING: Don't make n too large.
	// Recall that the number of permutations is n!
	// which can be very large, even when n is as small as 20 --
	// 20! = 2,432,902,008,176,640,000 and
	// 21! is too big to fit into a Java long, which is
	// why we use BigInteger instead.
	// ----------------------------------------------------------

	public PermuteArray(int n)
	{
		if (n < 1)
		{
			throw new IllegalArgumentException("Min 1");
		}

		a = new int[n];
		total = getFactorial(n);
		reset();
	}

	public void reset()
	{
		for (int i = 0; i < a.length; i++)
		{
			a[i] = i;
		}

		numLeft = total;
	}

	// ------------------------------------------------
	// Return number of permutations not yet generated
	// ------------------------------------------------

	public long getNumLeft()
	{
		return numLeft;
	}

	// ------------------------------------
	// Return total number of permutations
	// ------------------------------------

	public long getTotal()
	{
		return total;
	}

	// -----------------------------
	// Are there more permutations?
	// -----------------------------

	public boolean hasNext()
	{
		return numLeft > 0;
	}

	// ------------------
	// Compute factorial
	// ------------------

	private static long getFactorial(int n)
	{
		return n == 1 ? 1 : n * getFactorial(n - 1);
	}

	// --------------------------------------------------------
	// Generate next permutation (algorithm from Rosen p. 284)
	// --------------------------------------------------------

	public int[] next()
	{
		if (numLeft == total)
		{
			numLeft = numLeft - 1;
			return a;
		}

		int temp;

		// Find largest index j with a[j] < a[j+1]

		int j = a.length - 2;

		while (a[j] > a[j + 1])
		{
			j--;
		}

		// Find index k such that a[k] is smallest integer
		// greater than a[j] to the right of a[j]

		int k = a.length - 1;

		while (a[j] > a[k])
		{
			k--;
		}

		// Interchange a[j] and a[k]

		temp = a[k];
		a[k] = a[j];
		a[j] = temp;

		// Put tail end of permutation after jth position in increasing order

		int r = a.length - 1;
		int s = j + 1;

		while (r > s)
		{
			temp = a[s];
			a[s] = a[r];
			a[r] = temp;
			r--;
			s++;
		}

		numLeft = numLeft - 1;
		return a;
	}
}
