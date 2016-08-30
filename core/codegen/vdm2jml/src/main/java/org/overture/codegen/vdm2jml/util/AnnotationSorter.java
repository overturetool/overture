package org.overture.codegen.vdm2jml.util;

import java.util.Collections;
import java.util.Comparator;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.ir.PIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.ir.declarations.ADefaultClassDeclIR;

public class AnnotationSorter extends DepthFirstAnalysisAdaptor
{
	private Comparator<ClonableString> comparator;

	public AnnotationSorter()
	{
		super();
		this.comparator = new JmlAnnotationComparator();
	}

	@Override
	public void defaultInPIR(PIR node) throws AnalysisException
	{
		if (!node.getMetaData().isEmpty())
		{
			Collections.sort(node.getMetaData(), comparator);
		}

		if (node instanceof ADefaultClassDeclIR)
		{
			ADefaultClassDeclIR clazz = (ADefaultClassDeclIR) node;

			if (!clazz.getGlobalMetaData().isEmpty())
			{
				Collections.sort(clazz.getGlobalMetaData(), comparator);
			}

		}
	}
}
