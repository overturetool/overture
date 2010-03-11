package org.overture.ide.core.utility;

import java.util.List;
import java.util.Vector;

import org.overturetool.vdmj.lex.Dialect;

public class Language implements ILanguage
{
	private String name;
	private String nature;
	private final List<String> contentTypes = new Vector<String>();
	private Dialect dialect = null;

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setNature(String nature)
	{
		this.nature = nature;
	}

	public String getNature()
	{
		return nature;
	}

	public void addContentType(String contentType)
	{
		this.contentTypes.add( contentType);
	}

	public List<String> getContentTypes()
	{
		return contentTypes;
	}

	public void setDialect(Dialect dialect)
	{
		this.dialect = dialect;
	}

	public Dialect getDialect()
	{
		return dialect;
	}

}
