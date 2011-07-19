//package org.overture.typechecker.tests;
//
//import org.overture.ast.analysis.AnalysisAdaptor;
//import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
//import org.overture.ast.expressions.PExp;
//
//public class ValidateTypeExistance extends DepthFirstAnalysisAdaptor
//{
//	
//	case
//	@Override
//	public void defaultPExp(PExp node)
//	{
//		if (node.getType() == null)
//		{
//			throw new AssertionError("Type is null for node:" + node);
//		}
//	}
//}
