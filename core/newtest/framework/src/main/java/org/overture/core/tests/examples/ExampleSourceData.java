package org.overture.core.tests.examples;

import java.io.Serializable;

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
	String source;

	public ExampleSourceData(String name, Dialect dialect, Release release,
			String source)
	{
		this.name = name;
		this.dialect = dialect;
		this.source = source;
		this.release = release;
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

	public String getSource()
	{
		return source;
	}

}
