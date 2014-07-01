package org.overture.typechecker.util;

import java.io.Serializable;

import org.overture.ast.intf.lex.ILexNameToken;
import org.overture.typechecker.LexNameTokenAssistant;

class LexNameTokenWrapper implements Serializable
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -5420007432629328108L;
		public ILexNameToken token;
		private final LexNameTokenAssistant lnt;

		public LexNameTokenWrapper(ILexNameToken token, LexNameTokenAssistant lnt)
		{
			this.token = token;
			this.lnt = lnt;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (obj instanceof LexNameTokenWrapper)
			{
				return lnt.isEqual(this.token, ((LexNameTokenWrapper) obj).token);
			}

			return super.equals(obj);
		}

		@Override
		public int hashCode()
		{
			return this.token.hashCode();
		}

		@Override
		public String toString()
		{
			return token.toString();
		}
	}