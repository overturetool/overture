//COPYRIGHT
package %generated.node%;

import java.util.Map;

import %org.overture.ast.analysis.IAnalysis%;
import %org.overture.ast.analysis.IAnswer%;
import %org.overture.ast.analysis.IQuestion%;
import %org.overture.ast.analysis.IQuestionAnswer%;

public interface %INode%
{

	public abstract Object clone();

	public abstract %INode% clone(Map<%INode%, %INode%> oldToNewMap);

	/**
	 * Returns the {@link NodeEnum} corresponding to the
	 * type of this {@link Node} node.
	 * @return the {@link NodeEnum} for this node
	 */
	public abstract %NodeEnum% kindNode();

	/**
	 * Returns the parent node of this node.
	 * @return the parent node of this node
	 */
	public abstract %INode% parent();

	/**
	 * Sets the parent node of this node.
	 * @param parent the new parent node of this node
	 */
	public abstract void parent(%INode% parent);

	/**
	 * Removes the {@link Node} {@code child} as a child of this node.
	 * @param child the child node to be removed from this node
	 * @throws RuntimeException if {@code child} is not a child of this node
	 */
	public abstract void removeChild(%INode% child);

	/**
	 * Returns the nearest ancestor of this node (including itself)
	 * which is a subclass of {@code classType}.
	 * @param classType the superclass used
	 * @return the nearest ancestor of this node
	 */
	public abstract <T extends %INode%> T getAncestor(Class<T> classType);

	/**
	 * Applies this node to the {@link Analysis} visitor {@code analysis}.
	 * @param analysis the {@link Analysis} to which this node is applied
	 */
	public abstract void apply(%IAnalysis% analysis);

	/**
	 * Returns the answer for {@code caller} by applying this node to the
	 * {@link Answer} visitor.
	 * @param caller the {@link Answer} to which this node is applied
	 * @return the answer as returned from {@code caller}
	 */
	public abstract <A> A apply(%IAnswer<A>% caller);

	/**
	 * Applies this node to the {@link Question} visitor {@code caller}.
	 * @param caller the {@link Question} to which this node is applied
	 * @param question the question provided to {@code caller}
	 */
	public abstract <Q> void apply(%IQuestion<Q>% caller, Q question);

	/**
	 * Returns the answer for {@code answer} by applying this node with the
	 * {@code question} to the {@link QuestionAnswer} visitor.
	 * @param caller the {@link QuestionAnswer} to which this node is applied
	 * @param question the question provided to {@code answer}
	 * @return the answer as returned from {@code answer}
	 */
	public abstract <Q, A> A apply(%IQuestionAnswer<Q,A>% caller, Q question);

}