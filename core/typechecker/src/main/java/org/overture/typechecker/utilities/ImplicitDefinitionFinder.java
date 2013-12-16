package org.overture.typechecker.utilities;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAdaptor;
import org.overture.ast.definitions.AClassInvariantDefinition;
import org.overture.ast.definitions.AEqualsDefinition;
import org.overture.ast.definitions.AExplicitFunctionDefinition;
import org.overture.ast.definitions.AExplicitOperationDefinition;
import org.overture.ast.definitions.AImplicitFunctionDefinition;
import org.overture.ast.definitions.AImplicitOperationDefinition;
import org.overture.ast.definitions.AStateDefinition;
import org.overture.ast.definitions.ASystemClassDefinition;
import org.overture.ast.definitions.AThreadDefinition;
import org.overture.ast.definitions.ATypeDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.node.INode;
import org.overture.typechecker.Environment;
import org.overture.typechecker.assistant.ITypeCheckerAssistantFactory;

/**
 * This class implements a way to find ImplicitDefinitions from nodes from the AST.
 * 
 * @author kel
 */
public class ImplicitDefinitionFinder extends QuestionAdaptor<Environment>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5570219609306178637L;
	
	protected ITypeCheckerAssistantFactory af;

	public ImplicitDefinitionFinder(ITypeCheckerAssistantFactory af)
	{
		this.af = af;
	}
	
	protected AStateDefinition findStateDefinition(Environment question, INode node)
	{
		return question.findStateDefinition();
	}
	
	
	@Override
	public void defaultSClassDefinition(SClassDefinition node,
			Environment question) throws AnalysisException
	{
		//TODO: should I expand this even more?
		if (node instanceof ASystemClassDefinition)
		{
			af.createASystemClassDefinitionAssistant().implicitDefinitions((ASystemClassDefinition)node, question);
		} else
		{
			af.createSClassDefinitionAssistant().implicitDefinitionsBase(node, question);
		}
	}
	
	@Override
	public void caseAClassInvariantDefinition(AClassInvariantDefinition node,
				Environment question) throws AnalysisException
	{
			
	}
	
	@Override
	public void caseAEqualsDefinition(AEqualsDefinition node,
			Environment question) throws AnalysisException
	{
		
	}
	
	@Override
	public void caseAExplicitFunctionDefinition(
			AExplicitFunctionDefinition node, Environment question)
			throws AnalysisException
	{
		if (node.getPrecondition() != null)
		{
			node.setPredef(af.createAExplicitFunctionDefinitionAssistant().getPreDefinition(node));
			//PDefinitionAssistantTC.markUsed(d.getPredef());//ORIGINAL CODE
			af.getUsedMarker().caseAExplicitFunctionDefinition(node.getPredef());
		} else
		{
			node.setPredef(null);
		}

		if (node.getPostcondition() != null)
		{
			node.setPostdef(af.createAExplicitFunctionDefinitionAssistant().getPostDefinition(node));
			//PDefinitionAssistantTC.markUsed(d.getPostdef());//ORIGINAL CODE
			af.getUsedMarker().caseAExplicitFunctionDefinition(node.getPostdef());
		} else
		{
			node.setPostdef(null);
		}
	}
	

	@Override
	public void caseAExplicitOperationDefinition(
			AExplicitOperationDefinition node, Environment question)
			throws AnalysisException
	{
		node.setState(findStateDefinition(question,node));

		if (node.getPrecondition() != null)
		{
			node.setPredef(af.createAExplicitOperationDefinitionAssistant().getPreDefinition(node, question));
			af.createPDefinitionAssistant().markUsed(node.getPredef()); //ORIGINAL CODE
			
		}

		if (node.getPostcondition() != null)
		{
			node.setPostdef(af.createAExplicitOperationDefinitionAssistant().getPostDefinition(node, question));
			af.createPDefinitionAssistant().markUsed(node.getPostdef());
		}
	}

	

	@Override
	public void caseAImplicitFunctionDefinition(
			AImplicitFunctionDefinition node, Environment question)
			throws AnalysisException
	{
		
		if (node.getPrecondition() != null)
		{
			node.setPredef(af.createAImplicitFunctionDefinitionAssistant().getPreDefinition(node));
			af.createPDefinitionAssistant().markUsed(node.getPredef());
			//af.createPDefinitionAssistant().markUsed(node.getPredef());
		} else
		{
			node.setPredef(null);
		}

		if (node.getPostcondition() != null)
		{
			node.setPostdef(af.createAImplicitFunctionDefinitionAssistant().getPostDefinition(node));
			af.createPDefinitionAssistant().markUsed(node.getPostdef());
			
			
		} else
		{
			node.setPostdef(null);
		}
	}
	@Override
	public void caseAImplicitOperationDefinition(
			AImplicitOperationDefinition node, Environment question)
			throws AnalysisException
	{
		node.setState(findStateDefinition(question,node));

		if (node.getPrecondition() != null)
		{
			node.setPredef(af.createAImplicitOperationDefinitionAssistant().getPreDefinition(node, question));
			af.createPDefinitionAssistant().markUsed(node.getPredef());
			
		}

		if (node.getPostcondition() != null)
		{
			node.setPostdef(af.createAImplicitOperationDefinitionAssistant().getPostDefinition(node, question));
			af.createPDefinitionAssistant().markUsed(node.getPostdef());
			
		}
	}
	@Override
	public void caseAStateDefinition(AStateDefinition node, Environment question)
			throws AnalysisException
	{
		if (node.getInvPattern() != null)
		{
			node.setInvdef(af.createAStateDefinitionAssistant().getInvDefinition(node));
		}

		if (node.getInitPattern() != null)
		{
			node.setInitdef(af.createAStateDefinitionAssistant().getInitDefinition(node));
		}
	}
	@Override
	public void caseAThreadDefinition(AThreadDefinition node,
			Environment question) throws AnalysisException
	{
		//ORIGINAL CODE FROM ASSISTANT
		//node.setOperationDef(AThreadDefinitionAssistantTC.getThreadDefinition(node)); 
		//Mine non static call of the code.
		node.setOperationDef(af.createAThreadDefinitionAssistant().getThreadDefinition(node));
	}	
	@Override
	public void caseATypeDefinition(ATypeDefinition node, Environment question)
			throws AnalysisException
	{		
		if (node.getInvPattern() != null)
		{
			//node.setInvdef(getInvDefinition(d)); //Original code from Assistant.
			node.setInvdef(af.createATypeDefinitionAssistant().getInvDefinition(node));
			node.getInvType().setInvDef(node.getInvdef());
		} else
		{
			node.setInvdef(null);
		}
	}
	@Override
	public void defaultPDefinition(PDefinition node, Environment question)
			throws AnalysisException
	{
		return ;
	}
	
	
}
