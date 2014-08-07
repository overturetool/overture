package org.overture.core.tests.examples;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.overture.ast.lex.Dialect;
import org.overture.config.Release;

/**
 * Raw test data of an Overture example. This class holds the name and sources (the VDM model encoded as a single
 * {@link String}). It also holds the {@link Release} and {@link Dialect} the example is in.
 * 
 * @author ldc
 */
public class ExampleSourceData implements Serializable
{

	private static final long serialVersionUID = 1L;

	String name;
	Dialect dialect;
	Release release;
	List<File> source;

	public ExampleSourceData(String name, Dialect dialect, Release release,
			List<File> source)
	{
		this.name = name;
		this.dialect = dialect;
		this.source = source;
		this.release = release;
	}

	public ExampleSourceData(String name, Dialect dialect, Release release,
			File source)
	{
		this(name, dialect, release, Arrays.asList(new File[] { source }));
	}

	public Release getRelease()
	{
		return release;
	}

	public String getName()
	{
		return name;
	}

	public Dialect getDialect()
	{
		return dialect;
	}

	public List<File> getSource()
	{
		return source;
	}
	
	@Override
	public String toString()
	{
	return this.name +" "+ this.dialect + " "+this.dialect +" "+getFilesString();
	}

	private String getFilesString()
	{
		StringBuffer sb = new StringBuffer();
		for (Iterator<File> itr = source.iterator(); itr.hasNext();)
		{
			sb.append(itr.next().getName());
			if(itr.hasNext())
			{
				sb.append(", ");
			}
			
		}
		return sb.toString();
	}

}
