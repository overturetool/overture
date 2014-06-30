package org.overture.core.npp;

/**
 * The Class IndentTracker keeps track of indentation levels when printing ASTs. The indentation level is initially zero
 * (no indentation). It can be increased by one level at a time with {@link incIndent} and reset with
 * {@link resetIndent}. <br>
 * Indentation is done exclusively with spaces. The default length is 2 spaces (half a tab) but this can be changed with
 * {@link setIndentLength}.
 */
public class IndentTracker
{

	/** The indentation counter. */
	int count;

	// indent characters.
	private String indent = "\t";

	/**
	 * Gets the indentation string.
	 * 
	 * @return the indentation
	 */
	String getIndentation()
	{
		return indent;
	}

	/**
	 * Indent a string. Inserts an indentation at the beginning of the string. For strings with line breaks, a new
	 * indentation will also be inserted after each line break <b>(including the last)</b>.
	 * 
	 * @param s
	 *            the string to indent
	 * @return the indented string: "  s"
	 */
	String indent(String s)
	{
		if (count > 0)
		{ // don't bother indenting if the level is zero
			StringBuilder sb = new StringBuilder();
			sb.append("\n");
			sb.append(getIndentation());
			StringBuilder r = new StringBuilder();
			r.append(getIndentation());
			r.append(s.replaceAll("\n", sb.toString()));
			return r.toString();
		} else
		{
			return s;
		}
	}


	/**
	 * Increase the level of indentation by one.
	 */
	void incrIndent()
	{
		count++;
	}

	/**
	 * Decrease the level of indentation by one. Does nothing if indentation if zero.
	 */
	void decrIndent()
	{
		if (count > 0)
		{
			count--;
		}
	}

	/**
	 * Instantiates a new indent tracker.
	 */
	public IndentTracker()
	{
		count = 0;
	}

	/**
	 * Reset the indentation level to 0.
	 */
	public void resetIndent()
	{
		count = 0;
	}

}
