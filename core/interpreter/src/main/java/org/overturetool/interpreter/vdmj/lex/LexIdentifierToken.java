package org.overturetool.interpreter.vdmj.lex;


import org.overturetool.vdmj.lex.VDMToken;

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
				location.endPos);

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
}
