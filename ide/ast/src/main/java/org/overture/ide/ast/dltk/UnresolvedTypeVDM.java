package org.overture.ide.ast.dltk;

import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.types.UnresolvedType;

public class UnresolvedTypeVDM extends UnresolvedType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnresolvedTypeVDM(LexNameToken typename) {
		super(typename);
	}

	@Override
	public String toDisplay() {
		return typename.toString();
	}
}
