package org.overture.codegen.vdm2jml.util;

import java.util.Comparator;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.vdm2jml.JmlGenerator;

public class JmlAnnotationComparator implements Comparator<ClonableString>
{
	@Override
	public int compare(ClonableString left, ClonableString right)
	{
		int leftOrder = getOrder(left.value);
		int rightOrder = getOrder(right.value);

		return leftOrder - rightOrder;
	}

	public int getOrder(String annotation)
	{
		if (annotation.equals(JmlGenerator.JML_NULLABLE))
		{
			return 10;
		} else if (annotation.equals(JmlGenerator.JML_SPEC_PUBLIC))
		{
			return 9;
		} else if (annotation.equals(JmlGenerator.JML_HELPER))
		{
			return 8;
		} else if (annotation.equals(JmlGenerator.JML_PURE))
		{
			return 7;
		} else
		{
			return 0;
		}
	}
}
