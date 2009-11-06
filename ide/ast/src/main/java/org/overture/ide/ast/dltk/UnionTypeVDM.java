package org.overture.ide.ast.dltk;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeSet;
import org.overturetool.vdmj.types.UnionType;

public class UnionTypeVDM extends UnionType {

	public UnionTypeVDM(LexLocation location, TypeSet types) {
		super(location, types);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toDisplay() {
		String typeStr = ""; 
		for (Type type : types) {
			type.toString();
		}
		return typeStr;
	}
}
