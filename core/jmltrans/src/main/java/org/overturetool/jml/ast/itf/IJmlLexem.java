package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlLexem
{
	Long ILEXEMUNKNOWN      = new Long(0);
	Long ILEXEMKEYWORD      = new Long(1);
	Long ILEXEMIDENTIFIER   = new Long(2);
	Long ILEXEMLINECOMMENT  = new Long(3);
	Long ILEXEMBLOCKCOMMENT = new Long(4);
	abstract void accept(IJmlVisitor theVisitor) throws CGException;
	abstract Long getLine() throws CGException;
	abstract Long getColumn() throws CGException;
	abstract Long getLexval() throws CGException;
	abstract String getText() throws CGException;
	abstract Long getType() throws CGException;
	abstract Boolean isKeyword() throws CGException;
	abstract Boolean isIdentifier() throws CGException;
	abstract Boolean isComment() throws CGException;
	abstract Boolean isLineComment() throws CGException;
	abstract Boolean isBlockComment() throws CGException;
}

