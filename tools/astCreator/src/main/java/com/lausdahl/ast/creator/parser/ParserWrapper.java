package com.lausdahl.ast.creator.parser;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.FailedPredicateException;
import org.antlr.runtime.Lexer;
import org.antlr.runtime.MismatchedNotSetException;
import org.antlr.runtime.MismatchedRangeException;
import org.antlr.runtime.MismatchedSetException;
import org.antlr.runtime.MismatchedTokenException;
import org.antlr.runtime.MismatchedTreeNodeException;
import org.antlr.runtime.MissingTokenException;
import org.antlr.runtime.NoViableAltException;
import org.antlr.runtime.Parser;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.Token;
import org.antlr.runtime.UnwantedTokenException;

public abstract class ParserWrapper<T>
{
	public class ParseError implements IError
	{

		private final File file;
		private final int line;
		private final int charPositionInLine;
		private final String message;

		public ParseError(File file, int line, int charPositionInLine,
				String message)
		{
			this.file = file;
			this.line = line;
			this.charPositionInLine = charPositionInLine;
			this.message = message;
		}

		public int getCharPositionInLine()
		{
			return charPositionInLine;
		}

		public File getFile()
		{
			return file;
		}

		public int getLine()
		{
			return line;
		}

		public String getMessage()
		{
			return message;
		}
		
		@Override
		public String toString()
		{
		return file.getName()+" line "+line+":"+charPositionInLine+" "+message;
		}

	}

	protected Parser parser;
	protected Lexer lexer;
	
	List<IError> errors = new Vector<IError>();

	protected abstract T internalParse(File source, CharStream input) throws IOException;
	
	public T parse(File source) throws IOException
	{
		ANTLRFileStream input = new ANTLRFileStream(source.getAbsolutePath());
		return internalParse(source,input);
	}

	public T parse(File source, String data) throws IOException
	{
		ANTLRStringStream input = new ANTLRStringStream(data);
		return internalParse(source,input);
	}
		
	protected synchronized void addErrorsParser(File source,List<RecognitionException> exps)
	{
		for (RecognitionException errEx : exps)
		{
			addError(new ParseError(source, errEx.line, errEx.charPositionInLine, getErrorMessage(errEx, parser.getTokenNames())));
		}
	}
	
	protected synchronized void addErrorsLexer(File source,List<RecognitionException> exps)
	{
		for (RecognitionException errEx : exps)
		{
			addError(new ParseError(source, errEx.line, errEx.charPositionInLine, getErrorMessageLexer(errEx, parser.getTokenNames())));
		}
	}
	
	protected synchronized void addError(IError err)
	{
		errors.add(err);
	}

	public boolean hasErrors()
	{
		return errors.size() != 0;
	}
	
	public List<IError> getErrors()
	{
		return errors;
	}
	
	public String getErrorMessageLexer(RecognitionException e, String[] tokenNames) {
		String msg = null;
		if ( e instanceof MismatchedTokenException ) {
			MismatchedTokenException mte = (MismatchedTokenException)e;
			msg = "mismatched character "+lexer.getCharErrorDisplay(e.c)+" expecting "+lexer.getCharErrorDisplay(mte.expecting);
		}
		else if ( e instanceof NoViableAltException ) {
			NoViableAltException nvae = (NoViableAltException)e;
			// for development, can add "decision=<<"+nvae.grammarDecisionDescription+">>"
			// and "(decision="+nvae.decisionNumber+") and
			// "state "+nvae.stateNumber
			msg = "no viable alternative at character "+lexer.getCharErrorDisplay(e.c);
		}
		else if ( e instanceof EarlyExitException ) {
			EarlyExitException eee = (EarlyExitException)e;
			// for development, can add "(decision="+eee.decisionNumber+")"
			msg = "required (...)+ loop did not match anything at character "+lexer.getCharErrorDisplay(e.c);
		}
		else if ( e instanceof MismatchedNotSetException ) {
			MismatchedNotSetException mse = (MismatchedNotSetException)e;
			msg = "mismatched character "+lexer.getCharErrorDisplay(e.c)+" expecting set "+mse.expecting;
		}
		else if ( e instanceof MismatchedSetException ) {
			MismatchedSetException mse = (MismatchedSetException)e;
			msg = "mismatched character "+lexer.getCharErrorDisplay(e.c)+" expecting set "+mse.expecting;
		}
		else if ( e instanceof MismatchedRangeException ) {
			MismatchedRangeException mre = (MismatchedRangeException)e;
			msg = "mismatched character "+lexer.getCharErrorDisplay(e.c)+" expecting set "+
			lexer.getCharErrorDisplay(mre.a)+".."+lexer.getCharErrorDisplay(mre.b);
		}
		else {
			msg = getErrorMessage(e, tokenNames);
		}
		return msg;
	}
	
	protected String getErrorMessage(RecognitionException e, String[] tokenNames) {
		String msg = e.getMessage();
		if ( e instanceof UnwantedTokenException ) {
			UnwantedTokenException ute = (UnwantedTokenException)e;
			String tokenName="<unknown>";
			if ( ute.expecting== Token.EOF ) {
				tokenName = "EOF";
			}
			else {
				tokenName = tokenNames[ute.expecting];
			}
			msg = "extraneous input "+parser.getTokenErrorDisplay(ute.getUnexpectedToken())+
				" expecting "+tokenName;
		}
		else if ( e instanceof MissingTokenException ) {
			MissingTokenException mte = (MissingTokenException)e;
			String tokenName="<unknown>";
			if ( mte.expecting== Token.EOF ) {
				tokenName = "EOF";
			}
			else {
				tokenName = tokenNames[mte.expecting];
			}
			msg = "missing "+tokenName+" at "+parser.getTokenErrorDisplay(e.token);
		}
		else if ( e instanceof MismatchedTokenException ) {
			MismatchedTokenException mte = (MismatchedTokenException)e;
			String tokenName="<unknown>";
			if ( mte.expecting== Token.EOF ) {
				tokenName = "EOF";
			}
			else {
				tokenName = tokenNames[mte.expecting];
			}
			msg = "mismatched input "+parser.getTokenErrorDisplay(e.token)+
				" expecting "+tokenName;
		}
		else if ( e instanceof MismatchedTreeNodeException ) {
			MismatchedTreeNodeException mtne = (MismatchedTreeNodeException)e;
			String tokenName="<unknown>";
			if ( mtne.expecting==Token.EOF ) {
				tokenName = "EOF";
			}
			else {
				tokenName = tokenNames[mtne.expecting];
			}
			msg = "mismatched tree node: "+mtne.node+
				" expecting "+tokenName;
		}
		else if ( e instanceof NoViableAltException ) {
			//NoViableAltException nvae = (NoViableAltException)e;
			// for development, can add "decision=<<"+nvae.grammarDecisionDescription+">>"
			// and "(decision="+nvae.decisionNumber+") and
			// "state "+nvae.stateNumber
			msg = "no viable alternative at input "+parser.getTokenErrorDisplay(e.token);
		}
		else if ( e instanceof EarlyExitException ) {
			//EarlyExitException eee = (EarlyExitException)e;
			// for development, can add "(decision="+eee.decisionNumber+")"
			msg = "required (...)+ loop did not match anything at input "+
			parser.getTokenErrorDisplay(e.token);
		}
		else if ( e instanceof MismatchedSetException ) {
			MismatchedSetException mse = (MismatchedSetException)e;
			msg = "mismatched input "+parser.getTokenErrorDisplay(e.token)+
				" expecting set "+mse.expecting;
		}
		else if ( e instanceof MismatchedNotSetException ) {
			MismatchedNotSetException mse = (MismatchedNotSetException)e;
			msg = "mismatched input "+parser.getTokenErrorDisplay(e.token)+
				" expecting set "+mse.expecting;
		}
		else if ( e instanceof FailedPredicateException ) {
			FailedPredicateException fpe = (FailedPredicateException)e;
			msg = "rule "+fpe.ruleName+" failed predicate: {"+
				fpe.predicateText+"}?";
		}
		return msg;
	}
}
