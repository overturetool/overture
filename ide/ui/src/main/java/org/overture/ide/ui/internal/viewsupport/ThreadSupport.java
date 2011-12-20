/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.ui.internal.viewsupport;

import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.expressions.Expression;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.types.OperationType;

public class ThreadSupport extends ExplicitOperationDefinition {

	public ThreadSupport(ExplicitOperationDefinition eod) {
		
		super(eod.name,eod.type,eod.parameterPatterns,eod.precondition,eod.postcondition,eod.body);
		System.out.println(eod.location);
	}
	
	public ThreadSupport(LexNameToken name, OperationType type,
			PatternList parameters, Expression precondition,
			Expression postcondition, Statement body) {
		super(name, type, parameters, precondition, postcondition, body);

	}

	@Override
	public int hashCode() {
		
		return "thread".hashCode();
	}
	
	@Override
	public boolean equals(Object other) {
		return this.hashCode() == other.hashCode();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3726548835737405782L;

}
