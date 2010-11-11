package org.overture.tools.overturetemplategen;
import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

public class TemplateList extends Vector<Template>
{
	private String dialectName = "all";

	public TemplateList()
	{

	}

	public TemplateList(String dialectName)
	{
		this.dialectName = dialectName;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected TemplateList getFilteredList(String dialect)
	{
		TemplateList templates = new TemplateList(dialect);

		for (Template t : this)
		{
			if (t.dialect.toLowerCase().contains("all")
					|| (t.dialect.toLowerCase().contains(dialect.toLowerCase())||(dialect.toLowerCase().equals("vdmrt")&& t.dialect.toLowerCase().contains("vdmpp"))))
			{
				templates.add(t);
			}
		}
		return templates;
	}

	public TemplateList getSlList()
	{
		return getFilteredList("vdmsl");
	}

	public TemplateList getPpList()
	{
		return getFilteredList("vdmpp");
	}

	public TemplateList getRtList()
	{
		return getFilteredList("vdmrt");
	}

	public String getTemplateFileContent() throws Exception
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
		sb.append("<templates>\n");
		sb.append(getEntries());
		sb.append("</templates>\n");

		return sb.toString();
	}

	private String getEntries() throws Exception
	{
		StringBuilder sb = new StringBuilder();

		for (Template t : this)
		{
			sb.append("\n" + getXml(t));
		}
		sb.append("\n");
		return sb.toString();
	}

	private String getXml(Template t) throws Exception
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\t<template autoinsert=\"true\" context=\"");
		sb.append(getContextType());
		sb.append("\" deleted=\"false\" description=\"");
		sb.append(EscapeChars.forXML(t.description));
		sb.append("\" enabled=\"true\" name=\"");
		sb.append(t.name);
		sb.append("\" id=\"");
		sb.append(t.name + dialectName.toUpperCase());
		sb.append("\">");

		sb.append(EscapeChars.forXML(t.template));

		sb.append("</template>");

		return sb.toString();
	}

	public String getContextType() throws Exception
	{
		if (dialectName.toLowerCase().endsWith("vdmsl"))
		{
			return "org.overture.ide.vdmsl.ui.contextType";
		}

		if (dialectName.toLowerCase().endsWith("vdmpp"))
		{
			return "org.overture.ide.vdmpp.ui.contextType";
		}
		if (dialectName.toLowerCase().endsWith("vdmrt"))
		{
			return "org.overture.ide.vdmrt.ui.contextType";
		}
		throw new Exception("Context type");
	}

	@Override
	public synchronized String toString()
	{
		Collections.sort(this);
		StringBuilder sb = new StringBuilder();

		sb.append("All dialects:\n");

		for (Template t : this)
		{
			if (t.dialect.toLowerCase().contains("all"))
			{
				sb.append("\t" + t.name + "\n");
			}
		}

		sb.append("\nVDM-SL:\n");

		for (Template t : this)
		{
			if (t.dialect.toLowerCase().contains("vdmsl"))
			{
				sb.append("\t" + t.name + "\n");
			}
		}

		sb.append("\nVDM-PP:\n");

		for (Template t : this)
		{
			if (t.dialect.toLowerCase().contains("vdmpp"))
			{
				sb.append("\t" + t.name + "\n");
			}
		}
		sb.append("\nVDM-RT:\n");

		for (Template t : this)
		{
			if (t.dialect.toLowerCase().contains("vdmrt")||t.dialect.toLowerCase().contains("vdmpp"))
			{
				sb.append("\t" + t.name + "\n");
			}
		}

		return sb.toString();
	}
	
	public String toLatexTable()
	{
		StringBuilder sb = new StringBuilder();
		final int KEY_SIZE=25;
		sb.append("\\begin{tabular}{ l p{9cm} }\n");
		sb.append("Key & Description\\\\\\hline");
		
		sb.append("\n% ALL \n");
		for (Template t : this)
		{
			if (t.dialect.toLowerCase().contains("all"))
			{
				sb.append(latex(pad(t.name,KEY_SIZE))+" & "+latex(t.description)+"\\\\\n");
			}
		}
		
		sb.append("\n% VDM-SL \n");
		for (Template t : this)
		{
			if (t.dialect.toLowerCase().contains("vdmsl"))
			{
				sb.append(latex(pad(t.name,KEY_SIZE))+" & "+latex(t.description)+"\\\\\n");
			}
		}
		
		sb.append("\n% VDM-PP \n");
		for (Template t : this)
		{
			if (t.dialect.toLowerCase().contains("vdmpp"))
			{
				sb.append(latex(pad(t.name,KEY_SIZE))+" & "+latex(t.description)+"\\\\\n");
			}
		}
		
		sb.append("\n% VDM-RT \n");
		for (Template t : this)
		{
			if (t.dialect.toLowerCase().contains("vdmrt")||t.dialect.toLowerCase().contains("vdmpp"))
			{
				sb.append(latex(pad(t.name,KEY_SIZE))+" & "+latex(t.description)+"\\\\\n");
			}
		}
		
		
		sb.append("\\end{tabular}\n");
		
		return sb.toString();
	}
	
	public String pad(String data,int size)
	{
		while(data.length()<size)
		{
			data+=" ";
		}
		return data;
	}
	
	public String latex(String data)
	{
		return data.replaceAll("\\#", "\\\\#").replaceAll("\\&", "\\\\&").replaceAll("\\\\n", "");
	}
}
