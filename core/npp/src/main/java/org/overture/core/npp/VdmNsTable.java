package org.overture.core.npp;

import java.util.HashMap;

/**
 * The VdmNsTable Class provides a default VDM-based implementation of {@link InsTable}. It provides attributes for all
 * VDM language constructs needed to print out the Overture AST. <br>
 * Pretty Printers for language extensions may subclass VdmNsTable and override methods as necessary
 * 
 * @author ldc
 */

public class VdmNsTable implements InsTable
{

	private static String tail = "tl";
	private static String plus = "+";

	
	public String getTail()
	{
		return tail;
	}

	// missing attrib message
	private final String missing = "NOATTRIB";

	// singleton instance
	private static VdmNsTable instance = null;

	// attribute mapping
	private HashMap<String, String> vdmNs;

	protected VdmNsTable()
	{
		// don't instantiate me!
		init();
	}

	private void init()
	{
		vdmNs = new HashMap<String, String>();
		vdmNs.put("TAIL", "tl");
		vdmNs.put("AND", "and");
		vdmNs.put("OR", "or");
		vdmNs.put("PLUS", "+");
		vdmNs.put("MINUS", "-");
		vdmNs.put("DIVIDE", "/");
		vdmNs.put("TIMES", "*");
		vdmNs.put("LT", "<");
		vdmNs.put("LE", "<=");
		vdmNs.put("GT", ">");
		vdmNs.put("GE", ">=");
		vdmNs.put("NE", "<>");
		vdmNs.put("EQUALS", "=");
		vdmNs.put("EQUIV", "<=>");
		vdmNs.put("IMPLIES", "=>");
		vdmNs.put("SETDIFF", "\\");
		vdmNs.put("PLUSPLUS", "++");
		vdmNs.put("STARSTAR", "**");
		vdmNs.put("CONCATENATE", "^");
		vdmNs.put("MAPLET", "|->");
		vdmNs.put("RANGE", "...");
		vdmNs.put("DOMRESTO", "<:");
		vdmNs.put("DOMRESBY", "<-:");
		vdmNs.put("RANGERESTO", ":>");
		vdmNs.put("RANGERESBY", ":->");
		vdmNs.put("LAMBDA", "lambda");
		vdmNs.put("IOTA", "iota");
		vdmNs.put("EXISTS1", "exists1");
		vdmNs.put("EXISTS", "exists");
		vdmNs.put("POINT", ".");
		vdmNs.put("HEAD", "hd");
		vdmNs.put("FORALL", "forall");
		vdmNs.put("COMPOSITION", "comp");
		vdmNs.put("INDS", "inds");
		vdmNs.put("DISTCONC", "conc");
		vdmNs.put("DUNION", "dunion");
		vdmNs.put("FLOOR", "floor");
		vdmNs.put("MERGE", "merge");
		vdmNs.put("DINTER", "dinter");
		vdmNs.put("ABSOLUTE", "abs");
		vdmNs.put("ELEMS", "elems");
		vdmNs.put("RNG", "rng");
		vdmNs.put("POWER", "power");
		vdmNs.put("LEN", "len");
		vdmNs.put("DOM", "dom");
		vdmNs.put("CARD", "card");
		vdmNs.put("INVERSE", "inverse");
		vdmNs.put("INTER", "inter");
		vdmNs.put("UNION", "union");
		vdmNs.put("MUNION", "munion");
		vdmNs.put("REM", "rem");
		vdmNs.put("MOD", "mod");
		vdmNs.put("DIV", "div");
		vdmNs.put("SUBSET", "subset");
		vdmNs.put("PSUBSET", "psubset");
		vdmNs.put("INSET", "in set");
		vdmNs.put("NOTINSET", "not in set");

		// separators and stuff
		vdmNs.put("PRED", "&");
		vdmNs.put("SEP", ";");
		vdmNs.put("DEF", "==");
		vdmNs.put("OPENQUOTE", "<");
		vdmNs.put("CLOSEQUOTE", ">");
		vdmNs.put("CHARDELIM", "'");
		vdmNs.put("STRINGDELIM", "\"");
	}

	// create initial mapping of vdm elements and attributes
	public static VdmNsTable getInstance()
	{
		if (instance == null)
		{
			instance = new VdmNsTable();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.core.npp.InsTable#getAttribute(java.lang.String)
	 */
	@Override
	public String getAttribute(String key)
	{
		String r = vdmNs.get(key);
		if (r == null)
		{
			return this.getError();
		}
		return r;
	}

	/**
	 * Expose the size of the table. Used for testing content coverage. Other usage discouraged.
	 * 
	 * @return the table size.
	 */
	int getAttribCount()
	{
		return vdmNs.size();
	}

	/**
	 * Reset the table to the default values. Used for testing singleton. Other usage discouraged.
	 * 
	 * @return the table size.
	 */
	void reset()
	{
		init();
	}

	/*
	 * (non-Javadoc)
	 * @see org.overture.core.npp.InsTable#insertAttribute(java.lang.String, java.lang.String)
	 */
	@Override
	public void insertAttribute(String key, String attribute)
	{
		vdmNs.put(key, attribute);
	}

	@Override
	public String getError()
	{
		return missing;
	}

	@Override
	public String getPlus()
	{
		return plus;

	}

}
