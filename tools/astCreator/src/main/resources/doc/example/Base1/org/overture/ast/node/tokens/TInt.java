
package org.overture.ast.node.tokens;


import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.node.tokens.TInt;
import java.util.Map;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.node.INode;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import java.util.HashMap;
import org.overture.ast.node.Token;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public final class TInt extends Token
{
	private static final long serialVersionUID = 1L;

	private String _text;


	/**
	* Creates a new {@code TInt} node with the given nodes as children.
	* @deprecated This method should not be used, use AstFactory instead.
	* The basic child nodes are removed from their previous parents.
	* @param text_ the {@link String} node for the {@code text} child of this {@link TInt} node
	*/
	public TInt(String text_)
	{
		super();
		this.setText(text_);

	}



	public TInt()
	{
		_text = "int";
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
		fields.put("_text",this._text);
		return fields;
	}


	/**
	 * Creates a deep clone of this {@link TInt} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link TInt} node
	 */
	public TInt clone(Map<INode,INode> oldToNewMap)
	{
		TInt token = new TInt( getText());
		oldToNewMap.put(this, token);
		return token;
	}



	public String toString()
	{
		return (_text!=null?_text.toString():this.getClass().getSimpleName());
	}


	/**
	* Essentially this.toString().equals(o.toString()).
	**/
	@Override
	public boolean equals(Object o)
	{
		if (o != null && o instanceof TInt)		{
			 return toString().equals(o.toString());
		}
		return false;
	}


	/**
	 * Returns a deep clone of this {@link TInt} node.
	 * @return a deep clone of this {@link TInt} node
	 */
	public TInt clone()
	{
		return new TInt( getText());
	}


	/**
	 * Sets the {@code _text} child of this {@link TInt} node.
	 * @param value the new {@code _text} child of this {@link TInt} node
	*/
	public void setText(String value)
	{
		this._text = value;
	}


	/**
	 * @return the {@link String} node which is the {@code _text} child of this {@link TInt} node
	*/
	public String getText()
	{
		return this._text;
	}


	/**
	* Calls the {@link IAnalysis#caseTInt(TInt)} of the {@link IAnalysis} {@code analysis}.
	* @param analysis the {@link IAnalysis} to which this {@link TInt} node is applied
	*/
	@Override
	public void apply(IAnalysis analysis)
	{
		analysis.caseTInt(this);
	}


	/**
	* Calls the {@link IAnswer#caseTInt(TInt)} of the {@link IAnswer} {@code caller}.
	* @param caller the {@link IAnswer} to which this {@link TInt} node is applied
	*/
	@Override
	public <A> A apply(IAnswer<A> caller)
	{
		return caller.caseTInt(this);
	}


	/**
	* Calls the {@link IQuestion#caseTInt(TInt, Object)} of the {@link IQuestion} {@code caller}.
	* @param caller the {@link IQuestion} to which this {@link TInt} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question)
	{
		caller.caseTInt(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswer#caseTInt(TInt, Object)} of the {@link IQuestionAnswer} {@code caller}.
	* @param caller the {@link IQuestionAnswer} to which this {@link TInt} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question)
	{
		return caller.caseTInt(this, question);
	}



}
