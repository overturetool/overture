package org.overturetool.interpreter.util;

import java.io.File;
import java.net.URI;

import org.overture.interpreter.ast.node.ExternalNodeInterpreter;

public class ClonableFile extends File implements ExternalNodeInterpreter
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ClonableFile(String pathname)
	{
		super(pathname);
	}

	public ClonableFile(String parent, String child)
	{
		super(parent, child);
	}

	public ClonableFile(URI uri)
	{

		super(uri);
	}
	
	public ClonableFile(File file)
	{
		super(file.getAbsolutePath());
	}

	public Object clone()
	{
		return new ClonableFile(this.getAbsolutePath());
	}

}
