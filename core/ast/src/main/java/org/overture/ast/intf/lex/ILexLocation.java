package org.overture.ast.intf.lex;

import java.io.File;

import org.overture.ast.lex.LexLocation;
import org.overture.ast.node.ExternalNode;

public interface ILexLocation extends ExternalNode
{

	String file = null;

	String toString();
	String toShortString();
	
	public LexLocation clone();
	
	boolean within(ILexLocation span);
	
	public void executable(boolean exe);
	public void hit();
	
	public boolean getExecutable();
	
	public long getHits();
	public void setHits(long hits);
	
	
	/** The filename of the token. */
	public File getFile();
	/** The module/class name of the token. */
	public  String getModule();
	/** The line number of the start of the token. */
	public  int getStartLine();
	/** The character position of the start of the token. */
	public  int getStartPos();
	/** The last line of the token. */
	public  int getEndLine();
	/** The character position of the end of the token. */
	public  int getEndPos();
	
	
	public  int getStartOffset();
	
	public  int getEndOffset();
	
	
		
}
