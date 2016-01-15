package org.overture.codegen.vdm2jml.util;

import java.util.Collections;
import java.util.Comparator;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.PCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;

public class AnnotationSorter extends DepthFirstAnalysisAdaptor
{
	private Comparator<ClonableString> comparator;
	
	public AnnotationSorter()
	{
		super();
		this.comparator = new JmlAnnotationComparator();
	}
	
	@Override
	public void defaultInPCG(PCG node) throws AnalysisException
	{
		if(!node.getMetaData().isEmpty())
		{
			Collections.sort(node.getMetaData(), comparator);
		}
		
		if(node instanceof ADefaultClassDeclCG)
		{
			ADefaultClassDeclCG clazz = (ADefaultClassDeclCG) node;
			
			if(!clazz.getGlobalMetaData().isEmpty())
			{
				Collections.sort(clazz.getGlobalMetaData(), comparator);
			}
			
		}
	}
}
