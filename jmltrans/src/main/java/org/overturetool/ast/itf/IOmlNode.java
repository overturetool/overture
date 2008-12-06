package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlNode
{
	abstract String identity() throws CGException;
	abstract void accept(IOmlVisitor theVisitor) throws CGException;
	abstract Long getLine () throws CGException;
	abstract Long getColumn () throws CGException;
	abstract void setPosition(Long iLine, Long iColumn) throws CGException;
	abstract void setPosLexem(IOmlLexem iLexem) throws CGException;
	abstract void setPosNode(IOmlNode iNode) throws CGException;
}

