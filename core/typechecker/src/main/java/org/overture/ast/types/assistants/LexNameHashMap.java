package org.overture.ast.types.assistants;

import java.util.HashMap;

import org.overturetool.vdmj.util.HelpLexNameToken;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.messages.InternalException;

public class LexNameHashMap<V> extends HashMap<LexNameToken, V> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -413438348706313409L;

	@Override
	public V get(Object key) {
		try
		{
			return super.get(key);
		}catch (InternalException e) {
			for (java.util.Map.Entry<LexNameToken, V> element : entrySet()) {
				if(HelpLexNameToken.isEqual(element.getKey(),key))
					return element.getValue();
			}
			return null;
		}
	}

}
