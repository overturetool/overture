package org.overture.tools.treegen;

// import the HashMap prototype
import java.util.HashMap;

public class TreeGenOptions {
	// constant definitions
	static private final String DOPT = new String ("directory");
	static private final String POPT = new String ("package");
	static private final String TOPT = new String ("toplevel");
	
	// placeholder for the file name
	private String fname;
	
	// placeholder for the file options
	private HashMap<String,String> options = new HashMap<String,String>();
	
	// default constructor (file name only)
	public TreeGenOptions (String pfname)
	{
		// consistency checks
		assert (pfname != null);
		assert (pfname.length() > 0);
		
		// remember the file name
		fname = pfname;
		
		// initialize the options (default set to empty string)
		options.put(DOPT, new String());
		options.put(POPT, new String());
		options.put(TOPT, new String());
	}
	
	// auxiliary constructor (file name and all options)
	public TreeGenOptions (
			String pfname,
			String pdir,
			String ppack,
			String ptplvl
	) {
		// consistency checks
		assert (pfname != null);
		assert (pfname.length() > 0);
		assert (pdir.length() > 0);
		assert (ppack.length() > 0);
		assert (ptplvl.length() > 0);
		
		// remember the file name
		fname = pfname;
		
		// initialize the options (default set to empty string)
		options.put(DOPT, new String(pdir));
		options.put(POPT, new String(ppack));
		options.put(TOPT, new String(ptplvl));
	}
	
	// get file name
	public String getFilename()
	{
		// return the file name
		return fname;
	}
		
	// set the directory option
	public void setDirectory(String pdir)
	{
		// consistency check
		if (pdir == null) return;
		if (pdir.length() == 0) return;
		
		// update the option (store a copy)
		options.put(DOPT, new String(pdir));
	}
	
	// get the directory option
	public String getDirectory()
	{
		return options.get(DOPT);
	}
	
	// set the directory option
	public void setPackage(String ppack)
	{
		// consistency check
		if (ppack == null) return;
		if (ppack.length() == 0) return;
		
		// update the option (store a copy)
		options.put(POPT, new String(ppack));
	}
	
	// get the directory option
	public String getPackage()
	{
		return options.get(POPT);
	}

	// set the directory option
	public void setToplevel(String ptop)
	{
		// consistency check
		if (ptop == null) return;
		if (ptop.length() == 0) return;
		
		// update the option (store a copy)
		options.put(TOPT, new String(ptop));
	}
	
	// get the directory option
	public String getToplevel()
	{
		return options.get(TOPT);
	}
}
