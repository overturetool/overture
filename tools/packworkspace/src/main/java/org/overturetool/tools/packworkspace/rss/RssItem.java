package org.overturetool.tools.packworkspace.rss;

import java.io.UnsupportedEncodingException;

public class RssItem
{
	public final static String ITEM_TAG = "item";

	public final static String TITLE_TAG = "title";
	public final static String LINK_TAG = "link";
	public final static String DESCRIPTION_TAG = "description";
	public final static String CATEGORY_TAG = "category";
	public final static String AUTHOR_TAG = "author";
	public final static String COMMANTS_TAG = "comments";
	public final static String GUID_TAG = "guid";
	public final static String PUB_DATE_TAG = "pubDate";

	public String title;
	public String link;
	public String description;

	public String author;
	public String category;
	public String comments;
	public String guid;
	public String pubDate = "Fri, 02 Jul 2010 00:00:01 +0000";

	public StringBuilder getXml(int tabs)
	{
		StringBuilder sb = new StringBuilder();
		int internalTabs = tabs + 1;
		sb.append(getTabs(tabs) + "<" + ITEM_TAG + ">");

		sb.append(getTabs(internalTabs) + tag(TITLE_TAG, title));
		sb.append(getTabs(internalTabs) + tag(LINK_TAG, link));
		sb.append(getTabs(internalTabs) + tag(DESCRIPTION_TAG, description));
		sb.append(getTabs(internalTabs) + tag(AUTHOR_TAG, author));
		sb.append(getTabs(internalTabs) + tag(CATEGORY_TAG, category));
		//sb.append(getTabs(internalTabs) + tag(COMMANTS_TAG, comments));
		sb.append(getTabs(internalTabs) + tag(GUID_TAG, guid));
		sb.append(getTabs(internalTabs) + tag(PUB_DATE_TAG, pubDate));

		sb.append(getTabs(tabs) + "</" + ITEM_TAG + ">");
		return sb;
	}

	public String tag(String name, String content)
	{
//		try
//		{
//			return "<" + name + ">" + /*escapeHtmlFull(EscapeChars.forXML(*/escapeHtmlFull(EscapeChars.forHTML(new String(content.getBytes("UTF-8"))))/*))*/
//					+ "</" + name + ">";
			return "<" + name + ">" + stringToHTMLString(content)/*))*/
			+ "</" + name + ">";
//		} catch (UnsupportedEncodingException e)
//		{
//			return "";
//		}
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

	public static String stringToHTMLString(String string)
	{
		StringBuffer sb = new StringBuffer(string.length());
		boolean lastWasBlankChar = false;
		int len = string.length();
		char c;
		for (int i = 0; i < len; i++)
		{
			c = string.charAt(i);
			if (c == ' ')
			{
				if (lastWasBlankChar)
				{
					lastWasBlankChar = false;
					//sb.append("&nbsp;");
					sb.append(" ");
				} else
				{
					lastWasBlankChar = true;
					sb.append(' ');
				}
			} else
			{
				lastWasBlankChar = false;
				//
				// HTML Special Chars
				if (c == '"')
				{
					sb.append("&quot;");
				} else if (c == '&')
				{
					sb.append("&amp;");
				} else if (c == '<')
				{
					sb.append("&lt;");
				} else if (c == '>')
				{
					sb.append("&gt;");
				} else if (c == '\n')
				{
					sb.append("&lt;br/&gt;");
				} else
				{
					int ci = 0xffff & c;
					if (ci < 160)
					{
						// 7 Bit encoding
						sb.append(c);
					} else
					{
						// unicode encoding
						sb.append("&#");
						sb.append(new Integer(ci).toString());
						sb.append(';');
					}
				}
			}
		}
		return sb.toString();
	}

	public static StringBuilder escapeHtmlFull(String s)
	{
		StringBuilder b = new StringBuilder(s.length());
		for (int i = 0; i < s.length(); i++)
		{
			char ch = s.charAt(i);
			if (ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z' || ch >= '0'
					&& ch <= '9')
			{
				// safe
				b.append(ch);
			} else if (Character.isWhitespace(ch))
			{
				// paranoid version: whitespaces are unsafe - escape
				// conversion of (int)ch is naive
				b.append("&#").append((int) ch).append(";");
			} else if (Character.isISOControl(ch))
			{
				// paranoid version:isISOControl which are not isWhitespace removed !
				// do nothing do not include in output !
			} else if (Character.isHighSurrogate(ch))
			{
				int codePoint;
				if (i + 1 < s.length()
						&& Character.isSurrogatePair(ch, s.charAt(i + 1))
						&& Character.isDefined(codePoint = (Character.toCodePoint(ch, s.charAt(i + 1)))))
				{
					b.append("&#").append(codePoint).append(";");
				} else
				{
					// log("bug:isHighSurrogate");
				}
				i++; // in both ways move forward
			} else if (Character.isLowSurrogate(ch))
			{
				// wrong char[] sequence, //TODO: LOG !!!
				// log("bug:isLowSurrogate");
				i++; // move forward,do nothing do not include in output !
			} else
			{
				if (Character.isDefined(ch))
				{
					// paranoid version
					// the rest is unsafe, including <127 control chars
					b.append("&#").append((int) ch).append(";");
				}
				// do nothing do not include undefined in output!
			}
		}
		return b;
	}
}
