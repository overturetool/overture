package org.overturetool.ast.itf;

import jp.co.csk.vdm.toolbox.VDM.*;

public abstract interface IOmlNode
{
	abstract String identity() throws CGException;
	abstract void accept(IOmlVisitor theVisitor) throws CGException;
	abstract Boolean hasPositionInfo() throws CGException;
	abstract Long getLeftMostLexemIndex() throws CGException;
	abstract Long getRightMostLexemIndex() throws CGException;
}

