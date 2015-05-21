package org.overture.vdm2jml.tests.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.overture.ast.util.ClonableString;
import org.overture.codegen.cgast.PCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.DepthFirstAnalysisAdaptor;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.logging.Logger;
import org.overture.codegen.vdm2jml.JmlGenerator;

public class NullableCollector extends DepthFirstAnalysisAdaptor
{
	private Map<String, PCG> nullables;

	public NullableCollector()
	{
		this.nullables = new HashMap<String, PCG>();
	}

	public Set<PCG> getNullables()
	{
		return new HashSet<PCG>(nullables.values());
	}

	@Override
	public void caseAVarDeclCG(AVarDeclCG node) throws AnalysisException
	{
		if (node.getPattern() instanceof AIdentifierPatternCG)
		{
			AIdentifierPatternCG id = (AIdentifierPatternCG) node.getPattern();
			collect(id.getName(), node);
		} else
		{
			Logger.getLog().printErrorln("Expected pattern of local variable declaration to be an identifier pattern at this point. Got: "
					+ node.getPattern()
					+ " in '"
					+ this.getClass().getSimpleName() + "'");
		}
	}

	@Override
	public void caseAFieldDeclCG(AFieldDeclCG node) throws AnalysisException
	{
		collect(node.getName(), node);
	}

	public void collect(String name, PCG node) throws AnalysisException
	{
		if (!nullables.keySet().contains(name) && node.getMetaData() != null
				&& !node.getMetaData().isEmpty())
		{
			for (ClonableString m : node.getMetaData())
			{
				if (m.value.equals(JmlGenerator.JML_NULLABLE))
				{
					nullables.put(name, node);
				}
			}
		}
	}
}
