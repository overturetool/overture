package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlLexem
{
	abstract Long getLine() throws CGException;
	abstract Long getColumn() throws CGException;
	abstract Long getLexval() throws CGException;
	abstract String getText() throws CGException;
	abstract Long getIndex() throws CGException;
	abstract Long getType() throws CGException;
	abstract Boolean isReservedWord() throws CGException;
	abstract Boolean isIdentifier() throws CGException;
	abstract Boolean isLineComment() throws CGException;
	abstract Boolean isBlockComment() throws CGException;
	abstract void accept(IOmlVisitor theVisitor) throws CGException;
}

