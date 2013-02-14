package com.lausdahl.ast.creator.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.RewriteEmptyStreamException;

public class AstParserWrapper extends ParserWrapper<AstcParser.root_return>
{
	protected AstcParser.root_return internalParse(File source, CharStream data)
			throws IOException
	{
		super.lexer = new AstcLexer(data);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		
		AstcParser thisParser = new AstcParser(tokens);
		parser = thisParser;

		((AstcLexer)lexer).enableErrorMessageCollection(true);
		thisParser.enableErrorMessageCollection(true);
		try
		{
			AstcParser.root_return result =	thisParser.root();

			if (((AstcLexer)lexer).hasExceptions())
			{
				List<RecognitionException> exps = ((AstcLexer)lexer).getExceptions();
				addErrorsLexer(source, exps);
				return null;
			}

			if (thisParser.hasExceptions())
			{

				List<RecognitionException> exps = thisParser.getExceptions();
				addErrorsParser(source, exps);
			} else
			{
				return result;
			}
		} catch (RecognitionException errEx)
		{
			errEx.printStackTrace();
			addError(new ParseError(source, errEx.line, errEx.charPositionInLine, getErrorMessage(errEx, parser.getTokenNames())));
		}catch(RewriteEmptyStreamException errEx)
		{
			if (((AstcLexer)lexer).hasExceptions())
			{
				List<RecognitionException> exps = ((AstcLexer)lexer).getExceptions();
				addErrorsLexer(source, exps);
				
			}
			if (thisParser.hasExceptions())
			{

				List<RecognitionException> exps = thisParser.getExceptions();
				addErrorsParser(source, exps);
			}
			addError(new ParseError(source,lexer.getLine(), lexer.getCharPositionInLine(), "Rewrite error for empty stream at: " +errEx.elementDescription));
		}catch(Exception e)
		{
			if (((AstcLexer)lexer).hasExceptions())
			{
				List<RecognitionException> exps = ((AstcLexer)lexer).getExceptions();
				addErrorsLexer(source, exps);
				
			}
			if (thisParser.hasExceptions())
			{

				List<RecognitionException> exps = thisParser.getExceptions();
				addErrorsParser(source, exps);
			}
			e.printStackTrace();
		}
		return null;
	}
}
