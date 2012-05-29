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

import java.util.List;

import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.statements.PStm;
import org.overture.ast.types.AOperationType;
import org.overturetool.vdmj.lex.LexNameToken;

public class ThreadSupport extends AExplicitOperationDefinition {

	public ThreadSupport(AExplicitOperationDefinition eod) {
		
		super(eod.getLocation(),eod.getName(),eod.getNameScope(),eod.getUsed(),eod.getClassDefinition(),eod.getAccess().clone(),null, eod.getParameterPatterns(),eod.getBody().clone(),eod.getPrecondition(),eod.getPostcondition(),eod.getType(),null,null,null,null,eod.getActualResult(),false);
		System.out.println(eod.getLocation());
	}
	
	public ThreadSupport(LexNameToken name, AOperationType type,
			List<PPattern> parameters, PExp precondition,
			PExp postcondition, PStm body) {
//		super(name, type, parameters, precondition, postcondition, body);
		super(null,name,null,false,null,null,null, parameters,body,precondition,postcondition,type,null,null,null,null,null,false);

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
