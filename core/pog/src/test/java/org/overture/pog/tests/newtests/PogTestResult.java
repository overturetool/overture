package org.overture.pog.tests.newtests;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.apache.commons.collections4.CollectionUtils;
import org.overture.pog.pub.IProofObligation;
import org.overture.pog.pub.IProofObligationList;

public class PogTestResult extends Vector<String> implements Serializable,
		List<String>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static PogTestResult convert(IProofObligationList ipol)
	{
		PogTestResult r = new PogTestResult();
		for (IProofObligation ipo : ipol)
		{
			r.add(ipo.getKindString() + " obligation "
					+ipo.getLocation().toString()+": "
					+ ipo.getFullPredString());
		}
		return r;
	}

	public static ResultComparison compare(PogTestResult actual,
			PogTestResult expected)
	{
		Collection<String> stored_notfound = CollectionUtils.removeAll(expected, actual);
		Collection<String> found_notstored = CollectionUtils.removeAll(actual, expected);

		if (stored_notfound.isEmpty() && found_notstored.isEmpty())
		{
			// Results match, tests pass;do nothing
		} else
		{
			StringBuilder sb = new StringBuilder();
			if (!stored_notfound.isEmpty())
			{
				sb.append("Expected (but not found) POS: " + "\n");
				for (String pr : stored_notfound)
				{
					sb.append(pr + "\n");
				}
			}
			if (!found_notstored.isEmpty())
			{
				sb.append("Found (but not expected) POS: " + "\n");
				for (String pr : found_notstored)
				{
					sb.append(pr + "\n");
				}
			}
			return new ResultComparison(false, sb.toString());
		}

		return new ResultComparison(true);
	}

	public static class ResultComparison
	{
		final boolean match;
		final String message;

		public ResultComparison(boolean match, String message)
		{
			super();
			this.match = match;
			this.message = message;
		}

		public ResultComparison(boolean match)
		{
			this.match = match;
			this.message = "";
		}

		public boolean isMatch()
		{
			return match;
		}

		public String getMessage()
		{
			return message;
		}

	}
}
