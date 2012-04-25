package org.overturetool.jml.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IJmlNode
{
	abstract String identity() throws CGException;
	abstract void accept(IJmlVisitor theVisitor) throws CGException;
	abstract Long getLine () throws CGException;
	abstract Long getColumn () throws CGException;
	abstract void setPosition(Long iLine, Long iColumn) throws CGException;
	abstract void setPosLexem(IJmlLexem iLexem) throws CGException;
	abstract void setPosNode(IJmlNode iNode) throws CGException;
}

