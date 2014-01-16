package org.overture.typechecker.tests.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.ast.node.INode;

public class TypeSetAnalysis extends DepthFirstAnalysisAdaptor
{

	@Override
	public void defaultInINode(INode node) throws AnalysisException
	{
		try
		{
			Method m = node.getClass().getMethod("getType");

			if (m.invoke(node) == null)
			{
				throw new AnalysisException("Type not set for type: "
						+ node.getClass().getSimpleName());
			}
		} catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e)
		{
		} catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
