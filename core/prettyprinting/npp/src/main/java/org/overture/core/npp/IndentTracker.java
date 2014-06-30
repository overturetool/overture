package org.overture.core.npp;

/**
 * The Class IndentTracker keeps track of indentation levels when printing ASTs. The indentation level is initially zero
 * (no indentation). It can be increased by one level at a time with {@link incrIndent} and reset with
 * {@link resetIndent}. <br>
 * Indentation is done exclusively with tabs. This allows for easy rewriting of indents in the final string.
 */
public class IndentTracker
{

	/** The indentation counter. */
	private int count;

	// indent character.
	private String indent = "\t";

	/**
	 * Instantiates a new indent tracker.
	 */
	public IndentTracker()
	{
		count = 0;
	}

	/**
	 * Decrease the level of indentation by one. Does nothing if level is zero.
	 */
	void decrIndent()
	{
		if (count > 0)
		{
			count--;
		}
	}

	/**
	 * Get the current indentation, computed as <code>indentLevel x indendtString</code>.
	 * 
	 * @return the indentation
	 */
	String getIndentation()
	{
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < count; i++)
		{
			sb.append(indent);
		}

		return sb.toString();
	}

	/**
	 * Increase the level of indentation by one.
	 */
	void incrIndent()
	{
		count++;
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
	 * Reset the indentation level to 0.
	 */
	void resetIndent()
	{
		count = 0;
	}

}
