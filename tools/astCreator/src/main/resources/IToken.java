//COPYRIGHT
package %generated.node%;
import java.util.Map;
/**
 * {@code Token} is the superclass of all tokens is the AST.
 */
@SuppressWarnings("nls")
public interface %IToken% extends %INode% {
//	/**
//	 * Returns the {@link TokenEnum} corresponding to the
//	 * type of this {@link Token} node.
//	 * @return the {@link TokenEnum} for this node
//	 */
//	public abstract TokenEnum kindToken();

	/**
	 * Returns the {@link String} corresponding to the
	 * type of this {@link Node} node.
	 * @return the {@link String} for this node
	 */
	public String kindNode();

	/**
	 * Returns the text from the input file from which this token was made.
	 * @return the text from the input file from which this token was made
	 */
	public String getText();

	/**
	 * Sets the text of this token.
	 * @param text the new text of this token
	 */
	public void setText(String text);

	/**
	 * Returns the line number information of this token.
	 * @return the line number information of this token
	 */
	public int getLine();

	/**
	 * Sets the line number information of this token.
	 * @param line the new line number information of this token
	 */
	public void setLine(int line);

	/**
	 * Returns the position information of this token.
	 * @return the position information of this token
	 */
	public int getPos();

	/**
	 * Sets the position information of this token.
	 * @param pos the new position information of this token
	 */
	public void setPos(int pos);

//	@Override void toString(StringBuilder sb, int levels, boolean multiline, String prefix, String indent) {
//		sb.append("Token(\"" + this.text + "\")");
//	}

	/**
	 * Implements the {@link Node#removeChild(%INode%)} method. Since tokens have no
	 * children, it always throws a {@link RuntimeException}.
	 * @param child the child node to be removed from this {@link %IToken%} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link %IToken%} node
	 */
	public void removeChild(%INode% child);

//	/**
//	 * Implements the {@link Node#replaceChild(Node,Node)} method. Since tokens have no
//	 * children, it always throws a {@link RuntimeException}.
//	 * @param oldChild the child node to be replaced
//	 * @param newChild the new child node of this {@link Token} node
//	 * @throws RuntimeException if {@code oldChild} is not a child of this {@link Token} node
//	 */
//	@Override void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild) {
//		throw new RuntimeException("Not a child.");
//	}
	
	public abstract Map<String,Object> getChildren(Boolean includeInheritedFields);
}
