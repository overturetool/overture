/*******************************************************************************
 *
 *	Copyright (c) 2019 Nick Battle.
 *
 *	Author: Nick Battle
 *
 *	This file is part of Overture
 *
 ******************************************************************************/

package org.overture.pog.visitors;

import java.util.List;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.annotations.PAnnotation;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.expressions.PExp;
import org.overture.ast.modules.AModuleModules;
import org.overture.ast.node.INode;
import org.overture.ast.statements.PStm;
import org.overture.pog.annotations.POAnnotation;
import org.overture.pog.obligation.ProofObligationList;
import org.overture.pog.pub.IPOContextStack;
import org.overture.pog.pub.IProofObligationList;

/**
 * Abstract root of the PogParamVisitors, used to inherit common code.
 */
public abstract class AbstractPogParamVisitor extends QuestionAnswerAdaptor<IPOContextStack, IProofObligationList>
{
	/**
	 * Process annotations.
	 */
	protected IProofObligationList beforeAnnotation(PAnnotation annotation, INode node, IPOContextStack question)
		throws AnalysisException
	{
		IProofObligationList list = new ProofObligationList();

		if (annotation.getImpl() instanceof POAnnotation)
		{
			POAnnotation impl = (POAnnotation)annotation.getImpl();
			
			// This is not as ugly as multiple overloaded beforeAnotation and beforeAnnotations!
			if (node instanceof PDefinition)
			{
				impl.poBefore((PDefinition)node, question);
			}
			else if (node instanceof PExp)
			{
				impl.poBefore((PExp)node, question);
			}
			else if (node instanceof PStm)
			{
				impl.poBefore((PStm)node, question);
			}
			else if (node instanceof AModuleModules)
			{
				impl.poBefore((AModuleModules)node, question);
			}
			else if (node instanceof SClassDefinition)
			{
				impl.poBefore((SClassDefinition)node, question);
			}
			else
			{
				System.err.println("Cannot apply annoation to " + node.getClass().getSimpleName());
			}
		}
		
		return list;
	}
	
	protected IProofObligationList afterAnnotation(PAnnotation annotation, INode node, IProofObligationList list, IPOContextStack question)
			throws AnalysisException
	{
		if (annotation.getImpl() instanceof POAnnotation)
		{
			POAnnotation impl = (POAnnotation)annotation.getImpl();
			
			// This is not as ugly as multiple overloaded beforeAnotation and beforeAnnotations!
			if (node instanceof PDefinition)
			{
				impl.poAfter((PDefinition)node, list, question);
			}
			else if (node instanceof PExp)
			{
				impl.poAfter((PExp)node, list, question);
			}
			else if (node instanceof PStm)
			{
				impl.poAfter((PStm)node, list, question);
			}
			else if (node instanceof AModuleModules)
			{
				impl.poAfter((AModuleModules)node, list, question);
			}
			else if (node instanceof SClassDefinition)
			{
				impl.poAfter((SClassDefinition)node, list, question);
			}
			else
			{
				System.err.println("Cannot apply annoation to " + node.getClass().getSimpleName());
			}
		}

		return list;
	}

	/**
	 * Methods for applying before/after a list of annotations
	 */

	protected IProofObligationList beforeAnnotations(List<PAnnotation> annotations, INode node, IPOContextStack question) throws AnalysisException
	{
		IProofObligationList list = new ProofObligationList();

		for (PAnnotation annotation: annotations)
		{
			list.addAll(beforeAnnotation(annotation, node, question));
		}
		
		return list;
	}
	
	protected IProofObligationList afterAnnotations(List<PAnnotation> annotations, INode node, IProofObligationList list, IPOContextStack question) throws AnalysisException
	{
		for (PAnnotation annotation: annotations)
		{
			afterAnnotation(annotation, node, list, question);
		}
		
		return list;
	}
}
