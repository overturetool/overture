package org.overturetool.tools.packworkspace.rss;

public class RssFeed
{
	public final static String RSS_TAG = "rss";
	
	public RssChannel channel;
	
	
	public StringBuilder getXml()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<"+RSS_TAG+" version=\"2.0\" encoding=\"UTF-8\">");
		sb.append("\n");
		sb.append(channel.getXml(1));
		sb.append("</"+RSS_TAG+">");
		return sb;
	}
}
