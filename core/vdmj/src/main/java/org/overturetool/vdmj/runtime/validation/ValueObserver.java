package org.overturetool.vdmj.runtime.validation;

import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.runtime.Context;
import org.overturetool.vdmj.runtime.ValueException;
import org.overturetool.vdmj.values.Value;
import org.overturetool.vdmj.values.ValueListener;

public class ValueObserver implements ValueListener {

	
	public String[] name;
	public Value v;
	private ValueValidationExpression expr;
	
	public ValueObserver(String[] name, Value v, ValueValidationExpression expr) {
		this.name = name;
		this.v = v;
		this.expr = expr;
	}
	
	public void changedValue(LexLocation location, Value value, Context ctxt) {
		//System.out.println("Value " + printValueName() + " has changed to " + value.toString());
		this.expr.valueChanged(this);
	}

	private String printValueName()
	{
		if(name.length == 2)
		{
			return name[0] + "`" + name[1];
		}
		else
		{
			return name[0] + "`" + name[1] + "." + name[2];
		}
	}

	public double getValue() {
		try {
			return v.realValue(null);
		} catch (ValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	
	
}
