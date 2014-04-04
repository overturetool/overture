package org.overture.interpreter.utilities.definition;

import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.ObjectContext;

/***************************************
 * 
 * This method locates a value for a definition node. 
 * 
 * @author gkanos
 *
 ****************************************/
public class ValuesDefinitionLocator extends QuestionAnswerAdaptor<ObjectContext, ValueList>
{
	protected IInterpreterAssistantFactory af;
	
	public ValuesDefinitionLocator(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

}
