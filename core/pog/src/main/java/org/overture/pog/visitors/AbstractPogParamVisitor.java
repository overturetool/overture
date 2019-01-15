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

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.annotations.PAnnotation;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.node.INode;
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
			list.addAll(impl.poBefore(node, question));
		}
		
		return list;
	}
	
	protected IProofObligationList afterAnnotation(PAnnotation annotation, INode node, IProofObligationList list, IPOContextStack question)
		throws AnalysisException
	{
		if (annotation.getImpl() instanceof POAnnotation)
		{
			POAnnotation impl = (POAnnotation)annotation.getImpl();
			impl.poAfter(node, list, question);
		}
		
		return list;
	}
	
	protected IProofObligationList beforeAnnotations(PDefinition node, IPOContextStack question) throws AnalysisException
	{
		IProofObligationList list = new ProofObligationList();

		for (PAnnotation annotation: node.getAnnotations())
		{
			list.addAll(beforeAnnotation(annotation, node, question));
		}
		
		return list;
	}
	
	protected IProofObligationList afterAnnotations(PDefinition node, IProofObligationList list, IPOContextStack question) throws AnalysisException
	{
		for (PAnnotation annotation: node.getAnnotations())
		{
			list.addAll(afterAnnotation(annotation, node, list, question));
		}
		
		return list;
	}
}
