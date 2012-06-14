package org.overture.ast.lex;

import java.util.HashMap;
import java.util.Map;

import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import org.overture.ast.analysis.intf.IQuestionAnswer;

public class LexIdentifierToken extends LexToken {
	private static final long serialVersionUID = 1L;
	public final String name;
	public final boolean old;

	public LexIdentifierToken(String name, boolean old, LexLocation location) {
		super(location, VDMToken.IDENTIFIER);
		this.name = name;
		this.old = old;
	}

	public LexNameToken getClassName() {
		// We don't know the class name of the name of a class until we've
		// read the name. So create a new location with the right module.

		LexLocation loc = new LexLocation(location.file, name,
				location.startLine, location.startPos, location.endLine,
				location.endPos, location.startOffset, location.endOffset);

		return new LexNameToken("CLASS", name, loc);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof LexIdentifierToken) {
			LexIdentifierToken tother = (LexIdentifierToken) other;
			return this.name.equals(tother.getName())
					&& this.old == tother.isOld();
		}

		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode() + (old ? 1 : 0);
	}

	@Override
	public String toString() {
		return name + (old ? "~" : "");
	}

	public boolean isOld() {
		return old;
	}

	public String getName() {
		return name;
	}

	public LexLocation getLocation() {
		return location;
	}

	@Override
	public Object clone() {
		return new LexIdentifierToken(name, old, location);
	}
	
	@Override
	public void apply(IAnalysis analysis) throws Throwable {
		analysis.caseLexIdentifierToken(this); 
	}

	@Override
	public <A> A apply(IAnswer<A> caller) throws Throwable {
		return caller.caseLexIdentifierToken(this);
	}

	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question) throws Throwable {
		caller.caseLexIdentifierToken(this, question);
	}

	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question) throws Throwable {
		return caller.caseLexIdentifierToken(this, question);
	}
	
	/**
	 * Creates a map of all field names and their value
	 * @param includeInheritedFields if true all inherited fields are included
	 * @return a a map of names to values of all fields
	 */
	@Override
	public Map<String,Object> getChildren(Boolean includeInheritedFields)
	{
		Map<String,Object> fields = new HashMap<String,Object>();
		if(includeInheritedFields)
		{
			fields.putAll(super.getChildren(includeInheritedFields));
		}
		fields.put("name",this.name);
		fields.put("old", this.old);
		return fields;
	}
}
