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
