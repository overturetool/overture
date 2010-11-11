package org.overture.tools.overturetemplategen;
public class Template implements Comparable 
{
	public static final String NAME_TOKEN ="#name";
	public static final String DESCRIPTION_TOKEN ="#description";
	public static final String DIALECT_TOKEN ="#dialect";
	public static final String TEMPLATE_BEGIN_TOKEN ="#begin";
	public static final String TEMPLATE_END_TOKEN ="#end";
	
	
	public final String name;
	public final String description;
	public final String dialect;
	public final String template;
	

	public Template(String name, String description, String dialect,
			String template)
	{
		this.name = name;
		this.description = description;
		this.dialect = dialect;
		this.template = template;
	}
	
	
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(pad(NAME_TOKEN+ ":",14) + name+"\n");
		sb.append(pad(DESCRIPTION_TOKEN+ ":",14)+ description+"\n");
		sb.append(pad(DIALECT_TOKEN+ ":",14) +dialect+"\n");
		sb.append(TEMPLATE_BEGIN_TOKEN + "\n"+template+"\n"+TEMPLATE_END_TOKEN);
		return sb.toString();
	}
	
	
	public static String pad(String text, int length)
	{
		while(text.length()<length)
		{
			text+=" ";
		}
		return text;
	}



	@Override
	public int compareTo(Object o)
	{
		if(o instanceof Template)
		{
			return this.name.compareTo(((Template)o).name);
		}
		return 0;
	}
}
