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

import org.overture.ast.analysis.intf.IAnalysisInterpreter;
import org.overture.ast.analysis.intf.IAnswerInterpreter;
import org.overture.ast.analysis.intf.IQuestionInterpreter;
import org.overture.ast.analysis.intf.IQuestionAnswerInterpreter;

public interface INodeInterpreter  extends INode
{

	public abstract Object clone();

//	public abstract INodeInterpreter clone(Map<INodeInterpreter, INodeInterpreter> oldToNewMap);

	/**
	 * Returns the {@link NodeEnum} corresponding to the
	 * type of this {@link Node} node.
	 * @return the {@link NodeEnum} for this node
	 */
	public abstract NodeEnumInterpreter kindNodeInterpreter();

	/**
	 * Returns the parent node of this node.
	 * @return the parent node of this node
	 */
	public abstract INodeInterpreter parent();

	/**
	 * Sets the parent node of this node.
	 * @param parent the new parent node of this node
	 */
	public abstract void parent(INodeInterpreter parent);

	/**
	 * Removes the {@link INodeInterpreter} {@code child} as a child of this node.
	 * @param child the child node to be removed from this node
	 * @throws RuntimeException if {@code child} is not a child of this node
	 */
	public abstract void removeChild(INodeInterpreter child);

	/**
	 * Returns the nearest ancestor of this node (including itself)
	 * which is a subclass of {@code classType}.
	 * @param classType the superclass used
	 * @return the nearest ancestor of this node
	 */
//	public abstract <T extends INodeInterpreter> T getAncestor(Class<T> classType);

	/**
	 * Applies this node to the {@link IAnalysis} visitor {@code analysis}.
	 * @param analysis the {@link IAnalysis} to which this node is applied
	 */
	public abstract void apply(IAnalysisInterpreter analysis);

	/**
	 * Returns the answer for {@code caller} by applying this node to the
	 * {@link IAnswer} visitor.
	 * @param caller the {@link IAnswer} to which this node is applied
	 * @return the answer as returned from {@code caller}
	 */
	public abstract <A> A apply(IAnswerInterpreter<A> caller);

	/**
	 * Applies this node to the {@link IQuestion} visitor {@code caller}.
	 * @param caller the {@link IQuestion} to which this node is applied
	 * @param question the question provided to {@code caller}
	 */
	public abstract <Q> void apply(IQuestionInterpreter<Q> caller, Q question);

	/**
	 * Returns the answer for {@code answer} by applying this node with the
	 * {@code question} to the {@link IQuestionAnswer} visitor.
	 * @param caller the {@link IQuestionAnswer} to which this node is applied
	 * @param question the question provided to {@code answer}
	 * @return the answer as returned from {@code answer}
	 */
	public abstract <Q, A> A apply(IQuestionAnswerInterpreter<Q, A> caller, Q question);

	
	public abstract Map<String,Object> getChildren(Boolean includeInheritedFields);
}
