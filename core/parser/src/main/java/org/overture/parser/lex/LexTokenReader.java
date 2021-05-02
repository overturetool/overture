/*******************************************************************************
 *
 *	Copyright (c) 2008 Fujitsu Services Ltd.
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

package org.overture.parser.lex;

import java.io.File;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import org.overture.ast.intf.lex.ILexCommentList;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexBooleanToken;
import org.overture.ast.lex.LexCharacterToken;
import org.overture.ast.lex.LexCommentList;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.lex.LexIntegerToken;
import org.overture.ast.lex.LexKeywordToken;
import org.overture.ast.lex.LexLocation;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexQuoteToken;
import org.overture.ast.lex.LexRealToken;
import org.overture.ast.lex.LexStringToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.parser.config.Properties;
import org.overture.parser.syntax.SyntaxReader;

/**
 * The main lexical analyser class.
 */

public class LexTokenReader extends BacktrackInputReader
{
	/** The filename used for console expressions. */
	public static String consoleFileName = "console";
	/** The current module name, if parsing a module. */
	public String currentModule = "";
	/** The current file name. */
	public final File file;
	/** The VDM language dialect we're parsing. */
	public final Dialect dialect;

	/** The current line, starting at line 1. */
	private int linecount;
	/** The current character position on the line, starting at 1. */
	private int charpos;
	/** The number of chars read since the last push. */
	private int charsread;
	/** The number of tokens read since the last push. */
	private int tokensread;
	/** The current offset */
	private int offset;

	/** The next character to process. */
	private char ch;
	/** The last token returned. */
	private LexToken last = null;
	/** True if ch is a quoted double quote, ie. \" */
	private boolean quotedQuote = false;

	/** Added to fix location on the traces **/
	private ILexLocation location = null;

	/** Comments read since last getComments call */
	private LexCommentList comments = new LexCommentList();

	/** The primary SyntaxReader (ie. class or module) using this reader */
	private SyntaxReader syntaxReader = null;


	/**
	 * An inner class to hold all the position details that need to be saved and restored on push/pop.
	 */

	private class Position
	{
		public int lc;
		public int cc;
		public int cr;
		public int tr;

		public char c;
		public LexToken l;
    	public LexCommentList co = new LexCommentList();

		/**
		 * Create a Position from the outer class' current position details.
		 */

		@SuppressWarnings("synthetic-access")
		public Position()
		{
			lc = linecount;
			cc = charpos;
			cr = charsread;
			tr = tokensread;
			co.addAll(comments);

			c = ch;
			l = last;
		}

		/**
		 * Set the outer class position details to those contained in this.
		 */

		@SuppressWarnings("synthetic-access")
		public void set()
		{
			linecount = lc;
			charpos = cc;
			charsread = cr;
			tokensread = tr;

			ch = c;
			last = l;
			comments.clear();
			comments.addAll(co);
		}
	}

	/** A stack of Positions for backtracking. */
	private Stack<Position> stack = new Stack<Position>();

	/** An end of file symbol. */
	private static final char EOF = (char) -1;

	/** The assumed tab stop, for calculating positions. */
	public static/* final */int TABSTOP = 4;

	/**
	 * Create a LexTokenReader for the filename passed.
	 * 
	 * @param file
	 *            The filename to parse.
	 * @param dialect
	 *            if VDM-SL or VDM++ tokens should be processed.
	 */

	public LexTokenReader(File file, Dialect dialect)
	{
		super(file);
		this.file = file;
		this.dialect = dialect;
		init();
	}

	/**
	 * Create a LexTokenReader for the filename and charset passed.
	 * 
	 * @param file
	 *            The filename to parse.
	 * @param dialect
	 *            if VDM-SL or VDM++ tokens should be processed.
	 * @param charset
	 *            The charset for the file.
	 */

	public LexTokenReader(File file, Dialect dialect, String charset)
	{
		super(file, charset);
		this.file = file;
		this.dialect = dialect;
		init();
	}

	/**
	 * Create a LexTokenReader for the string passed.
	 * 
	 * @param expression
	 *            The string (expression) to parse.
	 * @param dialect
	 *            Parse VDM++ or VDM-SL tokens.
	 */
	public LexTokenReader(String expression, Dialect dialect)
	{
		super(expression);
		this.file = new File(consoleFileName);
		this.dialect = dialect;
		init();
	}

	/**
	 * Create a LexTokenReader for the string and charset passed.
	 * 
	 * @param expression
	 *            The string (expression) to parse.
	 * @param dialect
	 *            Parse VDM++ or VDM-SL tokens.
	 * @param charset
	 *            The charset to use.
	 */
	public LexTokenReader(String expression, Dialect dialect, String charset)
	{
		super(expression, charset);
		this.file = new File(consoleFileName);
		this.dialect = dialect;
		init();
	}

	/**
	 * Create a LexTokenReader to read content which originates from a file which is not yet saved and enable the source
	 * of the file to be set. This is used in the IDE to provide while typing outline and parse error info.
	 * 
	 * @param content
	 * @param dialect
	 * @param file
	 */

	public LexTokenReader(String content, Dialect dialect, File file)
	{
		super(content);
		this.file = file;
		this.dialect = dialect;
		init();
	}

	/**
	 * Create a LexTokenReader to read content which originates from a file which is not yet saved and enable the source
	 * of the file to be set. This is used in the IDE to provide while typing outline and parse error info.
	 * 
	 * @param content
	 * @param dialect
	 * @param file
	 * @param charset
	 * @param streamReaderType
	 */

	public LexTokenReader(String content, Dialect dialect, File file,
			String charset, ReaderType streamReaderType)
	{
		super(content, charset, file, streamReaderType);
		this.file = file;
		this.dialect = dialect;
		init();
	}

	/**
	 * Added to fix the location on traces
	 * 
	 * @param content
	 * @param dialect
	 * @param location
	 */
	public LexTokenReader(String content, Dialect dialect, ILexLocation location)
	{
		super(content);
		this.file = location.getFile();
		this.dialect = dialect;
		this.location = location;
		init();
	}

	/**
	 * Create a string based LexTokenReader, with the position details of another reader.
	 */
	public LexTokenReader(String content, ILexLocation location, LexTokenReader reader)
	{
		super(content);
		this.currentModule = reader.currentModule;
		this.file = location.getFile();
		this.dialect = reader.dialect;
		this.syntaxReader = reader.syntaxReader;
		rdCh();
		this.linecount = location.getStartLine();
		this.charpos = location.getStartPos();
		this.charsread = 0;
		this.tokensread = 0;
		this.last = null;
		this.comments.clear();
	}

	/**
	 * A string representation of the current location.
	 */
	@Override
	public String toString()
	{
		return "Last token [" + last + "], last char [" +
				(ch == EOF ? "EOF" : ch) +
				"] in " + file + " at " + linecount + ":" + charpos;
	}

	/**
	 * Initialize the position counters for a new file/string.
	 */

	private void init()
	{
		linecount = 1;
		charpos = 0;
		rdCh();
		charsread = 0;
		tokensread = 0;
		comments.clear();
	}

	/**
	 * Throw a {@link LexException} with the given message and details of the current file and position appended.
	 * 
	 * @param number
	 *            The error number.
	 * @param msg
	 *            The basic error message.
	 * @throws LexException
	 */

	private void throwMessage(int number, String msg) throws LexException
	{
		throwMessage(number, linecount, charpos, msg);
	}

	private void throwMessage(int number, int line, int pos, String msg)
			throws LexException
	{
		throw new LexException(number, msg, new LexLocation(file, currentModule, line, pos, line, pos, -1, -1));//
	}

	/**
	 * Check the next character is as expected. If the character is not as expected, throw the message as a
	 * {@link LexException}.
	 * 
	 * @param c
	 *            The expected next character.
	 * @param number
	 *            The number of the error message.
	 * @param message
	 *            The error message.
	 * @throws LexException
	 */

	private void checkFor(char c, int number, String message)
			throws LexException
	{
		if (ch == c)
		{
			rdCh();
		} else
		{
			throwMessage(number, message);
		}
	}

	/**
	 * @see org.overture.parser.lex.BacktrackInputReader#push()
	 */

	@Override
	public void push()
	{
		super.push();
		stack.push(new Position());
		charsread = 0;
		tokensread = 0; // Restored on pop
	}

	/**
	 * @see org.overture.parser.lex.BacktrackInputReader#pop()
	 */

	@Override
	public void pop()
	{
		super.pop();
		stack.pop().set();
		LexLocation.clearAfter(file, linecount, charpos);
	}

	/**
	 * @see org.overture.parser.lex.BacktrackInputReader#unpush()
	 */

	@Override
	public void unpush()
	{
		super.unpush();
		Position p = stack.pop(); // Note, don't set the position
		charsread = charsread + p.cr;
		tokensread = tokensread + p.tr;
	}

	/**
	 * Go back to the mark, and re-mark it. This just calls pop() followed by push().
	 */

	public void retry()
	{
		pop();
		push();
	}

	/**
	 * @return the number of characters read since the last push().
	 */

	public int getCharsRead()
	{
		return charsread;
	}
	
	/**
	 * @return the comments read since last getComments(), and clear.
	 */
	public ILexCommentList getComments()
	{
		LexCommentList list = new LexCommentList(comments);
		comments.clear();
		return list;
	}

	/**
	 * @return the number of tokens read since the last push().
	 */
	public int getTokensRead()
	{
		return tokensread;
	}
	
	/**
	 * @return the current location.
	 */
	public ILexLocation getLocation()
	{
		return new LexLocation(file, currentModule, linecount, charpos, 0, 0, 0, 0);
	}

	/**
	 * Read the next character from the stream. The position details are updated, accounting for newlines and tab stops.
	 * The next character is set in the "ch" field, as well as being returned for convenience.
	 * 
	 * @return the next character.
	 */

	private char rdCh()
	{
		char c = super.readCh();

		if (c == '\n')
		{
			linecount++;
			charpos = 0;
		} else if (c == '\t')
		{
			charpos += Properties.parser_tabstop - charpos
					% Properties.parser_tabstop;
		} else if (c != (char) -1)
		{
			charpos++;
		}

		ch = c;
		charsread++;
		offset = getCurrentRawReadOffset();

		// if(ch == '\r')
		// {
		// ch = rdCh();
		// }else
		// {
		// ch = c;
		// }
		return ch;
	}

	/**
	 * Read a backslash quoted character from the stream. This method is used when parsing strings and individual quoted
	 * characters, which may include things like "\n".
	 * 
	 * @return The actual character value (eg. "\n" returns 10).
	 * @throws LexException
	 */

	private char rdQuotedCh() throws LexException
	{
		quotedQuote = false;
		char c = rdCh();

		if (c == '\\')
		{
			rdCh();

			switch (ch)
			{
				case 'r':
					ch = '\r';
					break;
				case 'n':
					ch = '\n';
					break;
				case 't':
					ch = '\t';
					break;
				case 'f':
					ch = '\f';
					break;
				case 'e':
					ch = '\033';
					break;
				case 'a':
					ch = '\007';
					break;
				case '\'':
					ch = '\'';
					break;
				case '\"':
					ch = '\"';
					quotedQuote = true;
					break;
				case '\\':
					ch = '\\';
					break;

				case 'x':
					ch = (char) (value(rdCh()) * 16 + value(rdCh()));
					break;

				case 'c':
					ch = (char) (rdCh() - 'A' + 1); // eg. CTRL-A = 1
					break;

				case 'u':
					ch = (char) (value(rdCh()) * 4096 + value(rdCh()) * 256
							+ value(rdCh()) * 16 + value(rdCh()));
					break;

				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
					ch = (char) (value(ch) * 64 + value(rdCh()) * 8 + value(rdCh()));
					break;

				default:
					throwMessage(1000, "Malformed quoted character");
			}
		}

		return ch;
	}

	/**
	 * Return the value of a character for parsing numbers. The ASCII characters 0-9 are turned into decimal 0-9, while
	 * a-f and A-F are turned into the hex values 10-15. Characters outside these ranges return -1.
	 * 
	 * @param c
	 *            The ASCII value to convert.
	 * @return The converted value.
	 * @see #rdNumber
	 */

	private int value(char c)
	{
		switch (c)
		{
			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				return c - '0';

			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
				return c - 'a' + 10;

			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
				return c - 'A' + 10;

			default:
				return -1;
		}
	}

	/**
	 * Read a number of the given base. Parsing terminates when a character not within the number base is read.
	 * 
	 * @param base
	 *            The base of the number (eg. 10 for reading decimals)
	 * @return The integer value of the number read.
	 * @throws LexException
	 */

	private String rdNumber(int base) throws LexException
	{
		StringBuilder v = new StringBuilder();
		int n = value(ch);
		// long v = n;
		v.append(ch);

		if (n < 0 || n >= base)
		{
			throwMessage(1001, "Invalid char [" + ch + "] in base " + base
					+ " number");
		}

		while (true)
		{
			rdCh();
			n = value(ch);

			if (n < 0 || n >= base)
			{
				return v.toString();
			}

			// v = (v * base) + n;
			v.append(ch);
		}
	}

	/**
	 * Read the next complete token from the input stream. Whitespace is skipped, and the start line and position of the
	 * token are noted from the current stream position. Then the next character to process is used to drive a large
	 * switch statement to produce the right {@link VDMToken}. The lastToken field is updated and the result returned.
	 * 
	 * @return The next token, or a LexToken of type EOF.
	 * @throws LexException
	 */

	public LexToken nextToken() throws LexException
	{
		while (Character.isWhitespace(ch))
		{
			rdCh();
		}

		int tokline = linecount;
		int tokpos = charpos;
		int tokOffset = offset;
		VDMToken type = null;
		last = null;
		boolean rdch = true;

		switch (ch)
		{
			case EOF:
				type = VDMToken.EOF;
				break;

			case '-':
				if (rdCh() == '-')
				{
					StringBuilder sb = new StringBuilder();
					ILexLocation here = location(linecount, charpos + 1, tokpos, tokOffset);
					
					while (ch != '\n' && ch != EOF)
					{
						sb.append(ch);
						rdCh();
					}

					comments.add(here, sb.toString().substring(1), false);
					return nextToken();
				}
				else if (ch == '>')
				{
					type = VDMToken.ARROW;
				}
				else
				{
					rdch = false;
					type = VDMToken.MINUS;
				}
				break;

			case '+':
				if (rdCh() == '>')
				{
					type = VDMToken.TOTAL_FUNCTION;
				} else if (ch == '+')
				{
					type = VDMToken.PLUSPLUS;
				} else
				{
					rdch = false;
					type = VDMToken.PLUS;
				}
				break;

			case ':':
				if (rdCh() == '-')
				{
					if (rdCh() == '>')
					{
						type = VDMToken.RANGERESBY;
					} else
					{
						rdch = false;
						type = VDMToken.EQABST;
					}
				} else if (ch == '>')
				{
					type = VDMToken.RANGERESTO;
				} else if (ch == '=')
				{
					type = VDMToken.ASSIGN;
				} else if (ch == ':')
				{
					type = VDMToken.COLONCOLON;
				} else
				{
					rdch = false;
					type = VDMToken.COLON;
				}
				break;

			case '|':
				if (rdCh() == '-')
				{
					if (rdCh() == '>')
					{
						type = VDMToken.MAPLET;
					} else
					{
						throwMessage(1002, "Expecting '|->'");
					}
				} else if (ch == '|')
				{
					type = VDMToken.PIPEPIPE;
				} else
				{
					rdch = false;
					type = VDMToken.PIPE;
				}
				break;

			case '.':
				if (rdCh() == '.')
				{
					if (rdCh() == '.')
					{
						type = VDMToken.RANGE;
						break;
					}

					throwMessage(1003, "Expecting '...'");
				} else
				{
					rdch = false;
					type = VDMToken.POINT;
				}
				break;

			case '=':
				if (rdCh() == '=')
				{
					if (rdCh() == '>')
					{
						type = VDMToken.OPDEF;
					} else
					{
						rdch = false;
						type = VDMToken.EQUALSEQUALS;
					}
				} else if (ch == '>')
				{
					type = VDMToken.IMPLIES;
				} else
				{
					rdch = false;
					type = VDMToken.EQUALS;
				}
				break;

			case '*':
				if (rdCh() == '*')
				{
					type = VDMToken.STARSTAR;
				} else
				{
					rdch = false;
					type = VDMToken.TIMES;
				}
				break;

			case '<':
				push();
				if (rdCh() == '=')
				{
					unpush();
					if (rdCh() == '>')
					{
						type = VDMToken.EQUIVALENT;
					} else
					{
						rdch = false;
						type = VDMToken.LE;
					}
				} else if (ch == ':')
				{
					unpush();
					type = VDMToken.DOMRESTO;
				} else if (ch == '-')
				{
					unpush();
					if (rdCh() == ':')
					{
						type = VDMToken.DOMRESBY;
					} else
					{
						throwMessage(1004, "Expecting '<-:'");
					}
				} else if (ch == '>')
				{
					unpush();
					type = VDMToken.NE;
				} else if (startOfName(ch))
				{
					// <QuoteLiteral> or <x
					String name = rdIdentifier();

					if (ch == '>')
					{
						unpush();
						last = new LexQuoteToken(name, new LexLocation(file, currentModule, tokline, tokpos, linecount, charpos + 1, tokOffset, offset + 1));
						// location(tokline, tokpos, tokOffset, offset));
						type = VDMToken.QUOTE;
					} else
					{
						pop();
						type = VDMToken.LT;
					}
				} else
				{
					unpush();
					rdch = false;
					type = VDMToken.LT;
				}
				break;

			case '>':
				if (rdCh() == '=')
				{
					type = VDMToken.GE;
				} else
				{
					rdch = false;
					type = VDMToken.GT;
				}
				break;

			case '"':
				rdQuotedCh();
				StringBuffer msg = new StringBuffer();

				while ((ch != '"' || quotedQuote == true) && ch != EOF)
				{
					msg.append(ch);
					rdQuotedCh();
				}

				checkFor('\"', 1005, "Expecting close double quote");
				last = new LexStringToken(msg.toString(), location(tokline, tokpos, tokOffset, offset));
				rdch = false;
				break;

			case '\'':
				last = new LexCharacterToken(rdQuotedCh(), location(tokline, tokpos, tokOffset, offset));
				rdCh();
				checkFor('\'', 1006, "Expecting close quote after character");
				rdch = false;
				break;

			case '#':
				if (Character.isLetter(rdCh()))
				{
					StringBuilder tag = new StringBuilder();
					tag.append('#');

					do
					{
						tag.append(ch);
						rdCh();
					} while (Character.isLetter(ch));

					type = VDMToken.lookup(tag.toString(), dialect);

					if (type == null)
					{
						throwMessage(1007, "Unexpected tag after '#'");
					}

					rdch = false;
				} else
				{
					type = VDMToken.HASH;
					rdch = false;
				}
				break;

			case '/':
				if (rdCh() == '*') // Block comments
				{
					StringBuilder sb = new StringBuilder();
					rdCh();
					ILexLocation here = location(linecount, charpos, tokline, tokpos);
					int nestedCount = 0;
					int nestingSupport = Properties.parser_comment_nesting;

					while (ch != EOF)
					{
					    while (ch != '*' && ch != '/' && ch != EOF)
					    {
					    	sb.append(ch);
					    	rdCh();
					    }

					    if (ch == EOF)
					    {
					    	throwMessage(1011, tokline, tokpos, "Unterminated block comment");
					    }
					    else if (ch == '/')
					    {
					    	sb.append('/');

					    	if (rdCh() == '*')
						    {
					    		switch (nestingSupport)
					    		{
					    			case 3:	// ignored - ie. original behaviour, no nest count
					    				break;

					    			case 2:	// error
					    				nestedCount++;
					    				report(1013, "Illegal nested block comment", location(linecount, charpos-1, tokline, tokpos));
					    				break;

					    			case 1:	// warning
					    				warning(1012, "Deprecated nested block comment", location(linecount, charpos-1, tokline, tokpos));
					    				nestedCount++;
					    				break;

					    			case 0:	// supported
					    			default:
								    	nestedCount++;
					    				break;
					    		}

						    	sb.append('*');
						    	rdCh();
						    }
					    }
					    else
					    {
						    if (rdCh() == '/')
						    {
						    	if (nestedCount == 0)
						    	{
						    		break;
						    	}
						    	else
						    	{
						    		nestedCount--;
						    		sb.append('*');
						    		sb.append('/');
						    		rdCh();
						    	}
						    }
						    else
						    {
						    	sb.append('*');
						    }
					    }
					}

					comments.add(here, sb.toString(), true);
					rdCh();
					return nextToken();
				}
				else
				{
					type = VDMToken.DIVIDE;
					rdch = false;
				}
				break;

			case ',':
				type = VDMToken.COMMA;
				break;
			case ';':
				type = VDMToken.SEMICOLON;
				break;
			case '?':
				type = VDMToken.QMARK;
				break;
			case '@':
				type = VDMToken.AT;
				break;
			case '&':
				type = VDMToken.AMPERSAND;
				break;
			case '^':
				type = VDMToken.CONCATENATE;
				break;
			case '(':
				type = VDMToken.BRA;
				break;
			case ')':
				type = VDMToken.KET;
				break;
			case '{':
				type = VDMToken.SET_OPEN;
				break;
			case '}':
				type = VDMToken.SET_CLOSE;
				break;
			case '[':
				type = VDMToken.SEQ_OPEN;
				break;
			case ']':
				type = VDMToken.SEQ_CLOSE;
				break;
			case '\\':
				type = VDMToken.SETDIFF;
				break;

			case '0':
				push();
				rdCh();

				if (ch == 'x' || ch == 'X')
				{
					unpush();
					rdCh();
					String decimal = String.valueOf(Long.parseLong(rdNumber(16), 16));
					last = new LexIntegerToken(decimal, location(tokline, tokpos, tokOffset, offset));
				}
				else
				{
					pop();
					last = rdReal(tokline, tokpos, tokOffset);
				}

				rdch = false;
				break;

			default:
				if (ch >= '0' && ch <= '9')
				{
					last = rdReal(tokline, tokpos, tokOffset);
					rdch = false;
				} else if (startOfName(ch))
				{
					List<String> name = rdName(); // module`name parts
					rdch = false;

					switch (name.size())
					{
						case 1:
							type = VDMToken.lookup(name.get(0), dialect);

							if (type == null)
							{
								last = new LexIdentifierToken(name.get(0), ch == '~', location(tokline, tokpos, tokOffset, offset));
								rdch = ch == '~';
							} else
							{
								switch (type)
								{
									case TRUE:
									case FALSE:
										last = new LexBooleanToken(type, location(tokline, tokpos, tokOffset, offset));
										break;

									default:
										last = new LexKeywordToken(type, location(tokline, tokpos, tokOffset, offset));
										break;
								}
							}
							break;

						case 2:
							last = new LexNameToken(name.get(0), name.get(1), location(tokline, tokpos, tokOffset, offset), false, true);
							break;

						default:
							throwMessage(1008, "Malformed module`name");
					}
				} else
				{
					char badch = ch;
					rdCh();
					throwMessage(1009, "Unexpected character '" + badch
							+ "' (code 0x" + Integer.toHexString(badch) + ")");
				}
				break;
		}

		if (rdch)
		{
			rdCh();
		}

		if (last == null)
		{
			last = new LexKeywordToken(type, location(tokline, tokpos, tokOffset, offset));
		}

		tokensread++;
		return last;
	}

	/**
	 * Create a {@link LexLocation} object from the current stream position.
	 * 
	 * @param tokline
	 *            The token start line.
	 * @param tokpos
	 *            The token start position.
	 * @param endOffset
	 * @return A new LexLocation.
	 */

	private ILexLocation location(int tokline, int tokpos, int startOffset,
			int endOffset)
	{
		// Fix for location on traces
		if (this.location != null)
		{
			return this.location;
		} else
		{
			return new LexLocation(file, currentModule, tokline, tokpos, linecount, charpos, startOffset, endOffset);
		}
	}

	/**
	 * Read a decimal floating point number.
	 * 
	 * @param tokline
	 *            The start line of the number.
	 * @param tokpos
	 *            The start position of the number.
	 * @param tokOffset
	 * @return Either a LexRealToken or a LexIntegerToken.
	 * @throws LexException
	 */

	private LexToken rdReal(int tokline, int tokpos, int tokOffset)
			throws LexException
	{
		String floatSyntax = "Expecting <digits>[.<digits>][e<+-><digits>]";
		String value = rdNumber(10);
		String fraction = null;
		String exponent = null;
		boolean negative = false;

		push();

		if (ch == '.')
		{
			rdCh();

			if (ch >= '0' && ch <= '9')
			{
				fraction = rdNumber(10);
				exponent = "0";
			} else
			{
				// Somthing like rec.#1.field, so just return the integer
				pop();
				return new LexIntegerToken(value, location(tokline, tokpos, tokOffset, offset));
			}
		}

		unpush();

		if (ch == 'e' || ch == 'E')
		{
			if (fraction == null)
			{
				fraction = "0";
			}

			switch (rdCh())
			{
				case '+':
				{
					rdCh();
					exponent = rdNumber(10);
					break;
				}

				case '-':
				{
					rdCh();
					exponent = rdNumber(10);
					negative = true;
					break;
				}

				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				{
					exponent = rdNumber(10);
					break;
				}

				default:
					throwMessage(1010, floatSyntax);
			}
		}

		if (fraction != null)
		{
			String real = "+" + value + "." + fraction + "e"
					+ (negative ? "-" : "+") + exponent;

			return new LexRealToken(real, location(tokline, tokpos, tokOffset, offset));
		}

		return new LexIntegerToken(value, location(tokline, tokpos, tokOffset, offset));
	}

	/**
	 * Get the last token returned from the reader.
	 * 
	 * @return The last token, or the first token if none read yet.
	 * @throws LexException
	 */

	public LexToken getLast() throws LexException
	{
		if (last == null)
		{
			nextToken();
		}

		return last;
	}

	/**
	 * @return True if the character passed can be the start of a variable name.
	 */

	private boolean startOfName(char c)
	{
		if (c < 0x0100)
		{
			return Character.isLetter(c) || c == '$';
		} else
		{
			switch (Character.getType(c))
			{
				case Character.CONTROL:
				case Character.LINE_SEPARATOR:
				case Character.PARAGRAPH_SEPARATOR:
				case Character.SPACE_SEPARATOR:
				case Character.SURROGATE:
				case Character.UNASSIGNED:
				case Character.DECIMAL_DIGIT_NUMBER:
				case Character.CONNECTOR_PUNCTUATION:
					return false;

				default:
					return true;
			}
		}
	}

	/**
	 * @return True if the character passed can be part of a variable name.
	 */

	private boolean restOfName(char c)
	{
		if (c < 0x0100)
		{
			return Character.isLetterOrDigit(c) || c == '$' || c == '_'
					|| c == '\'';
		} else
		{
			switch (Character.getType(c))
			{
				case Character.CONTROL:
				case Character.LINE_SEPARATOR:
				case Character.PARAGRAPH_SEPARATOR:
				case Character.SPACE_SEPARATOR:
				case Character.SURROGATE:
				case Character.UNASSIGNED:
					return false;

				default:
					return true;
			}
		}
	}

	/**
	 * Read a fully qualified module`name.
	 * 
	 * @return A list of one or two name parts.
	 */

	private List<String> rdName()
	{
		List<String> names = new Vector<String>();
		names.add(rdIdentifier());

		if (ch == '`')
		{
			if (startOfName(rdCh()))
			{
				names.add(rdIdentifier());
			}
		}

		if (names.size() == 2)
		{
			// We have the strange mk_Mod`name case...

			String first = names.get(0);

			if (first.startsWith("mk_") || first.startsWith("is_"))
			{
				List<String> one = new Vector<String>();
				one.add(first + "`" + names.get(1));
				names = one;
			}
		}

		return names;
	}

	/**
	 * Read a simple identifier without a module name prefix.
	 * 
	 * @return a simple name.
	 */

	private String rdIdentifier()
	{
		StringBuilder id = new StringBuilder();
		id.append(ch);

		while (restOfName(rdCh()))
		{
			id.append(ch);
		}

		return id.toString();
	}

	/**
	 * These methods allow the LexTokenReader to report an error or warning to
	 * its enclosing SyntaxReader, if any.
	 */
	public void setSyntaxReader(SyntaxReader syntaxReader)
	{
		if (this.syntaxReader == null)
		{
			this.syntaxReader = syntaxReader;
		}
	}
	
	private void warning(int no, String msg, ILexLocation location)
	{
		if (syntaxReader != null)
		{
			syntaxReader.warning(no, msg, location);
		}
	}

	private void report(int no, String msg, ILexLocation location)
	{
		if (syntaxReader != null)
		{
			syntaxReader.report(no, msg, location);
		}
	}
}
