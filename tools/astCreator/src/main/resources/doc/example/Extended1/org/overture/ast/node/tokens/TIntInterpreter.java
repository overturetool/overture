
package org.overture.ast.node.tokens;


import org.overture.ast.analysis.intf.IAnalysis;
import org.overture.ast.analysis.intf.IAnswer;
import org.overture.ast.analysis.intf.IQuestion;
import org.overture.ast.analysis.intf.IQuestionAnswer;
import org.overture.ast.analysis.intf.IQuestionAnswerInterpreter;
import org.overture.ast.analysis.intf.IQuestionInterpreter;
import java.util.Map;
import org.overture.ast.node.tokens.TIntInterpreter;
import org.overture.ast.analysis.intf.IAnalysisInterpreter;
import org.overture.ast.node.INode;
import org.overture.ast.node.NodeEnum;
import org.overture.ast.node.TokenInterpreter;
import org.overture.ast.analysis.intf.IAnswerInterpreter;
import java.util.HashMap;


/**
* Generated file by AST Creator
* @author Kenneth Lausdahl
*
*/
public final class TIntInterpreter extends TokenInterpreter
{
	private static final long serialVersionUID = 1L;

	private String _text;


	/**
	* Creates a new {@code TIntInterpreter} node with the given nodes as children.
	* @deprecated This method should not be used, use AstFactory instead.
	* The basic child nodes are removed from their previous parents.
	* @param text_ the {@link String} node for the {@code text} child of this {@link TIntInterpreter} node
	*/
	public TIntInterpreter(String text_)
	{
		super();
		this.setText(text_);

	}



	public TIntInterpreter()
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
	 * Creates a deep clone of this {@link TIntInterpreter} node while putting all
	 * old node-new node relations in the map {@code oldToNewMap}.
	 * @param oldToNewMap the map filled with the old node-new node relation
	 * @return a deep clone of this {@link TIntInterpreter} node
	 */
	public TIntInterpreter clone(Map<INode,INode> oldToNewMap)
	{
		TIntInterpreter token = new TIntInterpreter( getText());
		oldToNewMap.put(this, token);
		return token;
	}


	/**
	 * Returns a deep clone of this {@link TIntInterpreter} node.
	 * @return a deep clone of this {@link TIntInterpreter} node
	 */
	public TIntInterpreter clone()
	{
		return new TIntInterpreter( getText());
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
		if (o != null && o instanceof TIntInterpreter)		{
			 return toString().equals(o.toString());
		}
		return false;
	}


	/**
	 * Sets the {@code _text} child of this {@link TIntInterpreter} node.
	 * @param value the new {@code _text} child of this {@link TIntInterpreter} node
	*/
	public void setText(String value)
	{
		this._text = value;
	}


	/**
	 * @return the {@link String} node which is the {@code _text} child of this {@link TIntInterpreter} node
	*/
	public String getText()
	{
		return this._text;
	}


	/**
	* Calls the {@link IAnalysisInterpreter#caseTIntInterpreter(TIntInterpreter)} of the {@link IAnalysisInterpreter} {@code analysis}.
	* @param analysis the {@link IAnalysisInterpreter} to which this {@link TIntInterpreter} node is applied
	*/
	@Override
	public void apply(IAnalysisInterpreter analysis)
	{
		analysis.caseTIntInterpreter(this);
	}


	/**
	* Calls the {@link IAnswerInterpreter#caseTIntInterpreter(TIntInterpreter)} of the {@link IAnswerInterpreter} {@code caller}.
	* @param caller the {@link IAnswerInterpreter} to which this {@link TIntInterpreter} node is applied
	*/
	@Override
	public <A> A apply(IAnswerInterpreter<A> caller)
	{
		return caller.caseTIntInterpreter(this);
	}


	/**
	* Calls the {@link IQuestionInterpreter#caseTIntInterpreter(TIntInterpreter, Object)} of the {@link IQuestionInterpreter} {@code caller}.
	* @param caller the {@link IQuestionInterpreter} to which this {@link TIntInterpreter} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q> void apply(IQuestionInterpreter<Q> caller, Q question)
	{
		caller.caseTIntInterpreter(this, question);
	}


	/**
	* Calls the {@link IQuestionAnswerInterpreter#caseTIntInterpreter(TIntInterpreter, Object)} of the {@link IQuestionAnswerInterpreter} {@code caller}.
	* @param caller the {@link IQuestionAnswerInterpreter} to which this {@link TIntInterpreter} node is applied
	* @param question the question provided to {@code caller}
	*/
	@Override
	public <Q, A> A apply(IQuestionAnswerInterpreter<Q, A> caller, Q question)
	{
		return caller.caseTIntInterpreter(this, question);
	}



	@Override
	public NodeEnum kindNode()
	{
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public void parent(INode parent)
	{
		// TODO Auto-generated method stub
		
	}



	@Override
	public void removeChild(INode child)
	{
		// TODO Auto-generated method stub
		
	}



	@Override
	public void apply(IAnalysis analysis)
	{
		// TODO Auto-generated method stub
		
	}



	@Override
	public <A> A apply(IAnswer<A> caller)
	{
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public <Q> void apply(IQuestion<Q> caller, Q question)
	{
		// TODO Auto-generated method stub
		
	}



	@Override
	public <Q, A> A apply(IQuestionAnswer<Q, A> caller, Q question)
	{
		return null;
		// TODO Auto-generated method stub
		
	}



}
