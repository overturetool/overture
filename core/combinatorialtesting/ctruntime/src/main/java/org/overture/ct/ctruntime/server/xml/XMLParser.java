/*******************************************************************************
 *
 *	Copyright (c) 2009 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overture.ct.ctruntime.server.xml;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class XMLParser
{
	private StringBuilder buffer;
	private int end;
	private int pos;
	private char ch;
	private Token token;
	private String value; // Tag and quoted tokens have values

	private enum Token
	{
		OPEN("<"), CLOSE(">"), QOPEN("<?"), QCLOSE("?>"), COMPLETE("/>"), STOP(
				"</"), TAG("tag"), EQUALS("="), QUOTED("quoted value"), CDATA(
				"<![CDATA["), CDEND("]]>"), EOF("EOF");

		private String value;

		Token(String s)
		{
			value = s;
		}

		@Override
		public String toString()
		{
			return value;
		}
	}

	public XMLParser(byte[] xml) throws IOException
	{
		if (xml[0] == '<' && xml[1] == '?')
		{
			String ascii = new String(xml, "ASCII"); // header is ASCII
			buffer = new StringBuilder(ascii);
			end = ascii.length();
			pos = 0;
			rdCh();
			rdToken();

			checkFor(Token.QOPEN);

			// Check header, like <?xml version="1.0" encoding="UTF-8"?>

			if (token != Token.TAG || !value.equals("xml"))
			{
				throw new IOException("Expecting '<?xml' at " + pos);
			}

			rdToken();
			Properties p = readAttributes();
			checkFor(Token.QCLOSE);

			String version = p.getProperty("version");
			String encoding = p.getProperty("encoding");

			if (version == null || !version.equals("1.0"))
			{
				throw new IOException("Expecting XML version 1.0 at " + pos);
			}

			// Re-encode the remainder using the header encoding

			String body = new String(ascii.substring(pos - 2).getBytes("ASCII"), encoding);
			buffer = new StringBuilder(body);
			end = body.length();
			pos = 0;
			rdCh();
			rdToken();
		} else
		{
			String s = new String(xml); // default platform encoding
			buffer = new StringBuilder(s);
			end = s.length();
			pos = 0;
			rdCh();
			rdToken();
		}
	}

	private char rdCh()
	{
		if (pos == end)
		{
			ch = 0; // EOF
		} else
		{
			ch = buffer.charAt(pos);
			pos++;
		}

		return ch;
	}

	private void checkFor(char c) throws IOException
	{
		if (ch == c)
		{
			rdCh();
			return;
		}

		throw new IOException("Expecting '" + c + "' at pos " + pos);
	}

	private void checkFor(Token tok) throws IOException
	{
		if (token == tok)
		{
			rdToken();
			return;
		}

		throw new IOException("Expecting '" + tok + "' at pos " + pos);
	}

	private boolean isStart(char c)
	{
		return Character.isLetter(c) || c == '_';
	}

	private String rdTag()
	{
		StringBuilder tag = new StringBuilder();

		while (isStart(ch) || Character.isDigit(ch))
		{
			tag.append(ch);
			rdCh();
		}

		return tag.toString();
	}

	private String rdQuoted() throws IOException
	{
		StringBuilder sb = new StringBuilder();
		rdCh();

		while (ch != '"')
		{
			sb.append(ch);
			rdCh();
		}

		checkFor('"');

		return sb.toString();
	}

	private Token rdToken() throws IOException
	{
		boolean rdch = true;
		value = null;

		while (Character.isWhitespace(ch))
		{
			rdCh();
		}

		switch (ch)
		{
			case '<':
				if (rdCh() == '!')
				{
					rdCh();
					checkFor('[');
					checkFor('C');
					checkFor('D');
					checkFor('A');
					checkFor('T');
					checkFor('A');
					checkFor('[');
					token = Token.CDATA;
					rdch = false;
				} else if (ch == '?')
				{
					token = Token.QOPEN;
				} else if (ch == '/')
				{
					token = Token.STOP;
				} else
				{
					token = Token.OPEN;
					rdch = false;
				}
				break;

			case '/':
				rdCh();
				checkFor('>');
				token = Token.COMPLETE;
				rdch = false;
				break;

			case '?':
				rdCh();
				checkFor('>');
				token = Token.QCLOSE;
				rdch = false;
				break;

			case '>':
				token = Token.CLOSE;
				break;

			case ']':
				rdCh();
				checkFor(']');
				checkFor('>');
				token = Token.CDEND;
				rdch = false;
				break;

			case '=':
				token = Token.EQUALS;
				break;

			case '"':
				value = rdQuoted();
				token = Token.QUOTED;
				rdch = false;
				break;

			case 0:
				token = Token.EOF;
				break;

			default:
				if (isStart(ch))
				{
					value = rdTag();
					token = Token.TAG;
					rdch = false;
				} else
				{
					throw new IOException("Unexpected char '" + ch
							+ "' at pos " + pos);
				}
				break;
		}

		if (rdch)
		{
			rdCh();
		}
		return token;
	}

	public XMLNode readNode() throws IOException
	{
		switch (token)
		{
			case OPEN:
				return readTagNode();

			case CDATA:
				return readDataNode();

			default:
				throw new IOException("Expecting <tag or <![CDATA[ at " + pos);
		}
	}

	private XMLNode readTagNode() throws IOException
	{
		checkFor(Token.OPEN);
		String tag = value;
		checkFor(Token.TAG);

		Properties attr;
		List<XMLNode> children = new Vector<XMLNode>();

		if (token == Token.TAG)
		{
			attr = readAttributes();
		} else
		{
			attr = new Properties();
		}

		switch (token)
		{
			case CLOSE:
				if (ch != '<')
				{
					children.add(new XMLTextNode(rdQuotedData()));
					rdToken();
				} else
				{
					rdToken();

					while (token == Token.OPEN || token == Token.CDATA)
					{
						XMLNode node = readNode();
						children.add(node);
					}
				}

				String finish = readStop();

				if (!finish.equals(tag))
				{
					throw new IOException("Expecting </" + tag + "> at " + pos);
				}

				return new XMLOpenTagNode(tag, attr, children);

			case COMPLETE:
				rdToken();
				return new XMLTagNode(tag, attr);

			default:
				throw new IOException("Expecting </" + tag + "> at " + pos);
		}
	}

	private String rdQuotedData()
	{
		StringBuilder sb = new StringBuilder();

		while (ch != '<' && ch != 0)
		{
			sb.append(ch);
			rdCh();
		}

		// De-quote the text...

		return sb.toString();
	}

	private XMLDataNode readDataNode() throws IOException
	{
		StringBuilder sb = new StringBuilder();

		while (true)
		{
			if (ch == ']')
			{
				if (buffer.substring(pos - 1, pos + 2).equals("]]>"))
				{
					rdCh();
					rdCh();
					rdCh();
					break;
				}
			}

			sb.append(ch);
			rdCh();
		}

		rdToken();
		return new XMLDataNode(sb.toString());
	}

	private String readStop() throws IOException
	{
		checkFor(Token.STOP);
		String tag = value;
		checkFor(Token.TAG);
		checkFor(Token.CLOSE);
		return tag;
	}

	private Properties readAttributes() throws IOException
	{
		Properties p = new Properties();

		while (token == Token.TAG)
		{
			String attr = value;
			rdToken();
			checkFor(Token.EQUALS);
			String aval = value;
			checkFor(Token.QUOTED);

			p.put(attr, aval);
		}

		return p;
	}
}
