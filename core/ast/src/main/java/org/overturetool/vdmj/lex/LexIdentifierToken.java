package org.overturetool.vdmj.lex;

import org.overture.ast.node.Node;

public abstract class LexIdentifierToken extends Node{

	public abstract LexNameToken getClassName();

	public abstract boolean isOld();

	public abstract String getName();

}