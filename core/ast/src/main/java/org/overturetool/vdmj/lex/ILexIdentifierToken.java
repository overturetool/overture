package org.overturetool.vdmj.lex;

import org.overture.ast.node.Node;

public abstract class ILexIdentifierToken extends Node{

	public abstract ILexNameToken getClassName();

	public abstract boolean isOld();

	public abstract String getName();

}