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

package org.overture.parser.syntax;

import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.overture.ast.annotations.Annotation;
import org.overture.ast.annotations.PAnnotation;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.intf.lex.ILexCommentList;
import org.overture.ast.intf.lex.ILexLocation;
import org.overture.ast.intf.lex.ILexToken;
import org.overture.ast.lex.Dialect;
import org.overture.ast.lex.LexIdentifierToken;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.lex.LexToken;
import org.overture.ast.lex.VDMToken;
import org.overture.ast.messages.InternalException;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.statements.PStm;
import org.overture.parser.annotations.ASTAnnotation;
import org.overture.parser.lex.LexException;
import org.overture.parser.lex.LexTokenReader;
import org.overture.parser.messages.LocatedException;
import org.overture.parser.messages.VDMError;
import org.overture.parser.messages.VDMWarning;

/**
 * The parent class of all syntax readers.
 */

public abstract class SyntaxReader
{
	/** The lexical analyser. */
	protected final LexTokenReader reader;
	/** The dialect of VDM that we're parsing. */
	protected final Dialect dialect;

	/** A DefinitionReader, if created. */
	protected DefinitionReader definitionReader = null;
	/** An ExpressionReader, if created. */
	protected ExpressionReader expressionReader = null;
	/** A PatternReader, if created. */
	protected PatternReader patternReader = null;
	/** A TypeReader, if created. */
	protected TypeReader typeReader = null;
	/** A BindReader, if created. */
	protected BindReader bindReader = null;
	/** A StatementReader, if created. */
	protected StatementReader statementReader = null;
	/** A ClassReader, if created. */
	protected ClassReader classReader = null;

	/** The errors raised. */
	private List<VDMError> errors = new Vector<VDMError>();

	/** The warnings raised. */
	private List<VDMWarning> warnings = new Vector<VDMWarning>();

	/** The sub-readers defined, if any. */
	private List<SyntaxReader> readers = new Vector<SyntaxReader>();

	/** The maximum number of syntax errors allowed in one Reader. */
	private static final int MAX = 100;

	/**
	 * Create a reader with the given lexical analyser and VDM++ flag.
	 */

	protected SyntaxReader(LexTokenReader reader)
	{
		this.reader = reader;
		this.dialect = reader.dialect;
	}

	protected SyntaxReader()
	{
		this.reader = null;
		this.dialect = null;
	}

	/**
	 * Read the next token from the lexical analyser, and advance by one token.
	 * 
	 * @return The next token.
	 */

	protected LexToken nextToken() throws LexException
	{
		return reader.nextToken();
	}

	/**
	 * Return the last token read by the lexical analyser without advancing. Repeated calls to this method will return
	 * the same result.
	 * 
	 * @return The last token again.
	 */

	protected LexToken lastToken() throws LexException
	{
		return reader.getLast();
	}

	/**
	 * Return the last token read, and also advance by one token. This is equivalent to calling {@link #lastToken}
	 * followed by {@link #nextToken}, but returning the result of lastToken.
	 * 
	 * @return The last token.
	 * @throws LexException
	 */

	protected LexToken readToken() throws LexException
	{
		LexToken tok = reader.getLast();
		reader.nextToken();
		return tok;
	}

	/**
	 * Set the name of the current module or class. Unqualified symbol names use this as their module/class name. See
	 * {@link #idToName}.
	 * 
	 * @param module
	 */

	public void setCurrentModule(String module)
	{
		reader.currentModule = module;
	}

	/**
	 * @return The current module/class name.
	 */

	public String getCurrentModule()
	{
		return reader.currentModule;
	}

	/**
	 * Convert an identifier into a name. A name is an identifier that has a module name qualifier, so this method uses
	 * the current module to convert the identifier passed in.
	 * 
	 * @param id
	 *            The identifier to convert
	 * @return The corresponding name.
	 */

	protected LexNameToken idToName(LexIdentifierToken id)
	{
		LexNameToken name = new LexNameToken(reader.currentModule, id);
		return name;
	}

	/**
	 * Return the last token, converted to a {@link LexIdentifierToken}. If the last token is not an identifier token,
	 * an exception is thrown with the message passed in.
	 * 
	 * @return The last token as a LexIdentifierToken.
	 * @throws LexException
	 */

	protected LexIdentifierToken lastIdToken() throws ParserException,
			LexException
	{
		LexToken tok = reader.getLast();

		if (tok.type == VDMToken.IDENTIFIER)
		{
			LexIdentifierToken id = (LexIdentifierToken) tok;

			if (id.isOld())
			{
				throwMessage(2295, "Can't use old name here", tok);
			}

			return id;
		}

		throwMessage(2058, "Expecting Identifier");
		return null;
	}

	/**
	 * Return the last token, converted to a {@link LexNameToken}. If the last token is not a name token, or an
	 * identifier token that can be converted to a name, an exception is thrown with the message passed in.
	 * 
	 * @return The last token as a LexIdentifierToken.
	 * @throws LexException
	 * @throws ParserException
	 */

	protected LexNameToken lastNameToken() throws LexException, ParserException
	{
		LexToken tok = reader.getLast();

		if (tok instanceof LexNameToken)
		{
			LexNameToken name = (LexNameToken) tok;

			if (name.old)
			{
				throwMessage(2295, "Can't use old name here", tok);
			}

			return name;
		} else if (tok instanceof LexIdentifierToken)
		{
			LexIdentifierToken id = (LexIdentifierToken) tok;

			if (id.isOld())
			{
				throwMessage(2295, "Can't use old name here", tok);
			}

			return new LexNameToken(reader.currentModule, id);
		}

		throwMessage(2059, "Expecting a name");
		return null;
	}

	/**
	 * Return the last token as an identifier, and advance by one token. This is similar to calling {@link #lastIdToken}
	 * followed by nextToken, and returning the result of the lastIdToken.
	 * 
	 * @param message
	 *            The message to throw if the last token is not an id.
	 * @return The last token as a LexIdentifierToken.
	 * @throws LexException
	 * @throws ParserException
	 */

	protected LexIdentifierToken readIdToken(String message)
			throws LexException, ParserException
	{
		LexToken tok = reader.getLast();

		if (tok.type == VDMToken.IDENTIFIER)
		{
			nextToken();
			LexIdentifierToken id = (LexIdentifierToken) tok;

			if (id.isOld())
			{
				throwMessage(2295, "Can't use old name here", tok);
			}

			return id;
		}

		if (tok.type == VDMToken.NAME)
		{
			message = "Found qualified name " + tok + ". " + message;
		}

		throwMessage(2060, message);
		return null;
	}

	/**
	 * Return the last token as a name, and advance by one token. This is similar to calling {@link #lastNameToken}
	 * followed by nextToken, and returning the result of the lastNameToken.
	 * 
	 * @param message
	 *            The message to throw if the last token is not a name.
	 * @return The last token as a LexNameToken.
	 * @throws LexException
	 * @throws ParserException
	 */

	protected LexNameToken readNameToken(String message) throws LexException,
			ParserException
	{
		LexToken tok = reader.getLast();
		nextToken();

		if (tok instanceof LexNameToken)
		{
			LexNameToken name = (LexNameToken) tok;

			if (name.old)
			{
				throwMessage(2295, "Can't use old name here", tok);
			}

			return name;
		} else if (tok instanceof LexIdentifierToken)
		{
			LexIdentifierToken id = (LexIdentifierToken) tok;

			if (id.isOld())
			{
				throwMessage(2295, "Can't use old name here", tok);
			}

			return new LexNameToken(reader.currentModule, id);
		}

		throwMessage(2061, message);
		return null;
	}

	/**
	 * Read any annotations from the collected comments, and clear them.
	 */
	private static int readingAnnotations = 0;
	
	protected List<PAnnotation> readAnnotations(ILexCommentList comments) throws LexException, ParserException
	{
		List<PAnnotation> annotations = new Vector<PAnnotation>();
		if (readingAnnotations > 0) return annotations; else readingAnnotations++;
		
		for (int i=0; i<comments.size(); i++)
		{
			if (comments.getComment(i).trim().startsWith("@"))
			{
				try
				{
					annotations.add(readAnnotation(new LexTokenReader(
							comments.getComment(i), comments.getLocation(i), reader)));
				}
				catch (Exception e)
				{
					// ignore - comment is not parsable
				}
			}
		}
		
		readingAnnotations--;
		return annotations;
	}
	
	private PAnnotation readAnnotation(LexTokenReader ltr) throws LexException, ParserException
	{
		ltr.nextToken();
		
		if (ltr.nextToken().is(VDMToken.IDENTIFIER))
		{
			LexIdentifierToken name = (LexIdentifierToken)ltr.getLast();
			ASTAnnotation impl = loadAnnotationImpl(name);
			List<PExp> args = impl.parse(ltr);
			PAnnotation ast = AstFactory.newAAnnotationAnnotation(name, args);
			ast.setImpl((Annotation) impl);
			impl.setAST(ast);
			return ast;
		}
		
		throwMessage(0, "Comment has no @Annotation");
		return null;
	}

	/**
	 * @return A new DefinitionReader.
	 */

	protected DefinitionReader getDefinitionReader()
	{
		if (definitionReader == null)
		{
			definitionReader = new DefinitionReader(reader);
			readers.add(definitionReader);
		}

		return definitionReader;
	}

	/**
	 * @return A new DefinitionReader.
	 */

	protected ExpressionReader getExpressionReader()
	{
		if (expressionReader == null)
		{
			expressionReader = new ExpressionReader(reader);
			readers.add(expressionReader);
		}

		return expressionReader;
	}

	/**
	 * @return A new PatternReader.
	 */

	protected PatternReader getPatternReader()
	{
		if (patternReader == null)
		{
			patternReader = new PatternReader(reader);
			readers.add(patternReader);
		}

		return patternReader;
	}

	/**
	 * @return A new TypeReader.
	 */

	protected TypeReader getTypeReader()
	{
		if (typeReader == null)
		{
			typeReader = new TypeReader(reader);
			readers.add(typeReader);
		}

		return typeReader;
	}

	/**
	 * @return A new BindReader.
	 */

	protected BindReader getBindReader()
	{
		if (bindReader == null)
		{
			bindReader = new BindReader(reader);
			readers.add(bindReader);
		}

		return bindReader;
	}

	/**
	 * @return A new StatementReader.
	 */

	protected StatementReader getStatementReader()
	{
		if (statementReader == null)
		{
			statementReader = new StatementReader(reader);
			readers.add(statementReader);
		}

		return statementReader;
	}

	/**
	 * @return A new ClassReader.
	 */

	protected ClassReader getClassReader()
	{
		if (classReader == null)
		{
			classReader = new ClassReader(reader);
			readers.add(classReader);
		}

		return classReader;
	}

	public void close()
	{
		reader.close();
	}

	/**
	 * If the last token is as expected, advance, else raise an error.
	 * 
	 * @param tok
	 *            The token type to check for.
	 * @param number
	 *            The error number.
	 * @param message
	 *            The error message to raise if the token is not as expected.
	 * @throws LexException
	 * @throws ParserException
	 */

	protected void checkFor(VDMToken tok, int number, String message)
			throws LexException, ParserException
	{
		if (lastToken().is(tok))
		{
			nextToken();
		} else
		{
			throwMessage(number, message);
		}
	}

	/**
	 * If the last token is the one passed, advance by one, else do nothing.
	 * 
	 * @param tok
	 *            The token type to check for.
	 * @return True if the token was skipped.
	 * @throws LexException
	 */

	protected boolean ignore(VDMToken tok) throws LexException
	{
		if (lastToken().is(tok))
		{
			nextToken();
			return true;
		} else
		{
			return false;
		}
	}

	/**
	 * Raise a {@link ParserException} at the current location.
	 * 
	 * @param number
	 *            The error number.
	 * @param message
	 *            The error message.
	 * @throws ParserException
	 * @throws LexException
	 */

	protected void throwMessage(int number, String message)
			throws ParserException, LexException
	{
		throw new ParserException(number, message, lastToken().location, reader.getTokensRead());
	}

	/**
	 * Raise a {@link ParserException} at the location of the token passed in.
	 * 
	 * @param number
	 *            The error number.
	 * @param message
	 *            The error message.
	 * @param token
	 *            The location of the error.
	 * @throws ParserException
	 */

	protected void throwMessage(int number, String message, ILexToken token)
			throws ParserException
	{
		throw new ParserException(number, message, token.getLocation(), reader.getTokensRead());
	}
	
	/**
	 * Raise a {@link ParserException} with a given token depth.
	 * @param number The error number.
	 * @param message The error message.
	 * @param depth The depth of the exception (tokens read).
	 *
	 * @throws ParserException
	 */ 

	protected void throwMessage(int number, String message, int depth)
			throws ParserException, LexException
	{
		throw new ParserException(number, message, lastToken().location, depth);
	}

	/**
	 * Raise a syntax error and attempt to recover. The error is added to the errors list, and if this exceeds 100
	 * errors the parser is aborted. The "after" and "upto" lists of token types are then used to control the advance of
	 * the parser to skip beyond the error. Tokens are read until one occurs in either list or EOF is reached. If the
	 * token is in the "after" list, one more token is read before returning; if it is in the "upto" list, the last
	 * token is left pointing to the token before returning. If EOF is reached, the method returns.
	 * 
	 * @param error
	 *            The exception that caused the error.
	 * @param after
	 *            A list of tokens to recover to, and step one beyond.
	 * @param upto
	 *            A list of tokens to recover to.
	 */

	protected void report(LocatedException error, VDMToken[] after,
			VDMToken[] upto)
	{
		VDMError vdmerror = new VDMError(error);
		errors.add(vdmerror);

		if (errors.size() >= MAX - 1)
		{
			errors.add(new VDMError(9, "Too many syntax errors", error.location));
			throw new InternalException(9, "Too many syntax errors");
		}

		// Either leave one token beyond something in the after list, or
		// at something in the next upto list.

		List<VDMToken> afterList = Arrays.asList(after);
		List<VDMToken> uptoList = Arrays.asList(upto);

		try
		{
			VDMToken tok = lastToken().type;

			while (!uptoList.contains(tok) && tok != VDMToken.EOF)
			{
				if (afterList.contains(tok))
				{
					nextToken();
					break;
				}

				tok = nextToken().type;
			}
		} catch (LexException le)
		{
			errors.add(new VDMError(le));
		}
	}

	/**
	 * Report a warning. Unlike errors, this does no token recovery.
	 */

	protected void warning(int no, String msg, ILexLocation location)
	{
		VDMWarning vdmwarning = new VDMWarning(no, msg, location);
		warnings.add(vdmwarning);

		if (warnings.size() >= MAX - 1)
		{
			errors.add(new VDMError(9, "Too many warnings", location));
			throw new InternalException(9, "Too many warnings");
		}
	}

	/**
	 * @return The error count from all readers that can raise errors.
	 */

	public int getErrorCount()
	{
		int size = 0;

		for (SyntaxReader rdr : readers)
		{
			size += rdr.getErrorCount();
		}

		return size + errors.size();
	}

	/**
	 * @return The errors from all readers that can raise errors.
	 */

	public List<VDMError> getErrors()
	{
		List<VDMError> list = new Vector<VDMError>();

		for (SyntaxReader rdr : readers)
		{
			list.addAll(rdr.getErrors());
		}

		list.addAll(errors);
		return list;
	}

	/**
	 * @return The warning count from all readers that can raise warnings.
	 */

	public int getWarningCount()
	{
		int size = 0;

		for (SyntaxReader rdr : readers)
		{
			size += rdr.getWarningCount();
		}

		return size + warnings.size();
	}

	/**
	 * @return The warnings from all readers that can raise warnings.
	 */

	public List<VDMWarning> getWarnings()
	{
		List<VDMWarning> list = new Vector<VDMWarning>();

		for (SyntaxReader rdr : readers)
		{
			list.addAll(rdr.getWarnings());
		}

		list.addAll(warnings);
		return list;
	}

	/**
	 * Print errors and warnings to the PrintWriter passed.
	 * 
	 * @param out
	 */

	public void printErrors(PrintWriter out)
	{
		for (VDMError e : getErrors())
		{
			out.println(e.toString());
		}
	}

	public void printWarnings(PrintWriter out)
	{
		for (VDMWarning w : getWarnings())
		{
			out.println(w.toString());
		}
	}

	@Override
	public String toString()
	{
		return reader.toString();
	}
	
	protected ASTAnnotation loadAnnotationImpl(LexIdentifierToken name)
			throws ParserException, LexException
	{
		String classpath = System.getProperty("overture.annotations", "org.overture.annotations;annotations;org.overture.annotations.examples;org.overture.annotations.provided");
		String[] packages = classpath.split(";|:");

		for (String pack: packages)
		{
			try
			{
				Class<?> clazz = Class.forName(pack + "." + name + "Annotation");
				Constructor<?> ctor = clazz.getConstructor();
				return (ASTAnnotation) ctor.newInstance();
			}
			catch (ClassNotFoundException e)
			{
				// Try the next package
			}
			catch (Exception e)
			{
				throwMessage(2334, "Failed to instantiate AST" + name + "Annotation");
			}
		}

		throwMessage(2334, "Cannot find AST" + name + "Annotation on " + classpath);
		return null;
	}
		
	protected ILexCommentList getComments()
	{
		return reader.getComments();
	}
	
	/**
	 * Annotation processing...
	 */
	protected void beforeAnnotations(SyntaxReader reader, List<PAnnotation> annotations)
	{
		for (PAnnotation annotation: annotations)
		{
			if (annotation.getImpl() instanceof ASTAnnotation)
			{
				ASTAnnotation impl = (ASTAnnotation)annotation.getImpl();
				
				// This is not as ugly as multiple overloaded beforeAnotation and beforeAnnotations!
				if (reader instanceof DefinitionReader)
				{
					impl.astBefore((DefinitionReader)reader);
				}
				else if (reader instanceof ExpressionReader)
				{
					impl.astBefore((ExpressionReader)reader);
				}
				else if (reader instanceof StatementReader)
				{
					impl.astBefore((StatementReader)reader);
				}
				else if (reader instanceof ModuleReader)
				{
					impl.astBefore((ModuleReader)reader);
				}
				else if (reader instanceof ClassReader)
				{
					impl.astBefore((ClassReader)reader);
				}
				else
				{
					System.err.println("Cannot apply annoation to " + reader.getClass().getSimpleName());
				}
			}
		}
	}

	protected void afterAnnotations(DefinitionReader reader, List<PAnnotation> annotations, PDefinition def)
	{
		for (PAnnotation annotation: annotations)
		{
			if (annotation.getImpl() instanceof ASTAnnotation)
			{
				ASTAnnotation impl = (ASTAnnotation)annotation.getImpl();
				impl.astAfter(reader, def);
			}
		}
	}
	
	protected void afterAnnotations(ExpressionReader reader, List<PAnnotation> annotations, PExp exp)
	{
		for (PAnnotation annotation: annotations)
		{
			if (annotation.getImpl() instanceof ASTAnnotation)
			{
				ASTAnnotation impl = (ASTAnnotation)annotation.getImpl();
				impl.astAfter(reader, exp);
			}
		}
	}
	
	protected void afterAnnotations(StatementReader reader, List<PAnnotation> annotations, PStm stmt)
	{
		for (PAnnotation annotation: annotations)
		{
			if (annotation.getImpl() instanceof ASTAnnotation)
			{
				ASTAnnotation impl = (ASTAnnotation)annotation.getImpl();
				impl.astAfter(reader, stmt);
			}
		}
	}
	
	protected void afterAnnotations(ModuleReader reader, List<PAnnotation> annotations, AModuleModules module)
	{
		for (PAnnotation annotation: annotations)
		{
			if (annotation.getImpl() instanceof ASTAnnotation)
			{
				ASTAnnotation impl = (ASTAnnotation)annotation.getImpl();
				impl.astAfter(reader, module);
			}
		}
	}
	
	protected void afterAnnotations(ClassReader reader, List<PAnnotation> annotations, SClassDefinition clazz)
	{
		for (PAnnotation annotation: annotations)
		{
			if (annotation.getImpl() instanceof ASTAnnotation)
			{
				ASTAnnotation impl = (ASTAnnotation)annotation.getImpl();
				impl.astAfter(reader, clazz);
			}
		}
	}
}
