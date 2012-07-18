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

package org.overture.ast.node;
import java.util.Map;
/**
 * {@code Token} is the superclass of all tokens is the AST.
 */
@SuppressWarnings("nls")
public interface IToken extends INode {
	/**
	 * Returns the {@link TokenEnum} corresponding to the
	 * type of this {@link Token} node.
	 * @return the {@link TokenEnum} for this node
	 */
//	public abstract TokenEnum kindToken();

	/**
	 * Returns the {@link NodeEnum} corresponding to the
	 * type of this {@link Node} node.
	 * @return the {@link NodeEnum} for this node
	 */
	public NodeEnum kindNode();

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
	 * Implements the {@link Node#removeChild(INode)} method. Since tokens have no
	 * children, it always throws a {@link RuntimeException}.
	 * @param child the child node to be removed from this {@link Token} node
	 * @throws RuntimeException if {@code child} is not a child of this {@link Token} node
	 */
	public void removeChild(INode child);

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
