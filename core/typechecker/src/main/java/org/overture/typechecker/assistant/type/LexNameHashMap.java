package org.overture.typechecker.assistant.type;

import java.util.HashMap;

import org.overture.ast.lex.LexNameToken;
import org.overture.ast.messages.InternalException;
import org.overture.typechecker.util.HelpLexNameToken;

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
