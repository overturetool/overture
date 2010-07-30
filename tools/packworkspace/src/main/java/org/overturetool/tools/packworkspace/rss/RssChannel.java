package org.overturetool.tools.packworkspace.rss;

import java.util.List;
import java.util.Vector;

public class RssChannel
{
	public final static String CHANNEL_TAG = "channel";

	public final static String TITLE_TAG = "title";
	public final static String LINK_TAG = "link";
	public final static String DESCRIPTION_TAG = "description";
	public final static String CATEGORY_TAG = "category";
	public final static String GENERATOR_TAG = "generator";
	//public final static String ITEMS_TAG = "items";

	public String title;
	public String link;
	public String description;
	public String category = "VDM";
	public String generator = "Overture Example Workspace Packer";

	public final List<RssItem> items = new Vector<RssItem>();

	public StringBuilder getXml(int tabs)
	{
		StringBuilder sb = new StringBuilder();
		int internalTabs = tabs+1;
		sb.append(getTabs(tabs) + "<" + CHANNEL_TAG + ">");

		sb.append(getTabs(internalTabs) + tag(TITLE_TAG, title));
		sb.append(getTabs(internalTabs) + tag(LINK_TAG, link));
		sb.append(getTabs(internalTabs) + tag(DESCRIPTION_TAG, description));
		sb.append(getTabs(internalTabs) + tag(CATEGORY_TAG, category));
		//sb.append(getTabs(internalTabs) + tag(GENERATOR_TAG, generator));

		//StringBuilder sbItems = new StringBuilder();
		for (RssItem item : items)
		{
			sb.append("\n" + item.getXml(internalTabs));
		}
		sb.append("\n");
		//sb.append(getTabs(internalTabs) +"<"+ITEMS_TAG+">"+ sbItems.toString()+getTabs(internalTabs)+"\n</"+ITEMS_TAG+">");

		sb.append(getTabs(tabs) + "</" + CHANNEL_TAG + ">");
		return sb;
	}

	public String tag(String name, String content)
	{
		return "<" + name + ">" + EscapeChars.forXML(content) + "</" + name
				+ ">";
	}

	public static String getTabs(int tabs)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\n");
		for (int i = 0; i < tabs; i++)
		{
			sb.append('\t');
		}
		return sb.toString();
	}

}
