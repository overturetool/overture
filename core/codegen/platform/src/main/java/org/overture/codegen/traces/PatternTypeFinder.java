package org.overture.codegen.traces;

import java.util.IdentityHashMap;

import org.overture.codegen.cgast.SPatternCG;
import org.overture.codegen.cgast.STypeCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.QuestionAdaptor;
import org.overture.codegen.cgast.declarations.ARecordDeclCG;
import org.overture.codegen.cgast.patterns.ABoolPatternCG;
import org.overture.codegen.cgast.patterns.ACharPatternCG;
import org.overture.codegen.cgast.patterns.AIdentifierPatternCG;
import org.overture.codegen.cgast.patterns.AIgnorePatternCG;
import org.overture.codegen.cgast.patterns.AIntPatternCG;
import org.overture.codegen.cgast.patterns.ANullPatternCG;
import org.overture.codegen.cgast.patterns.AQuotePatternCG;
import org.overture.codegen.cgast.patterns.ARealPatternCG;
import org.overture.codegen.cgast.patterns.ARecordPatternCG;
import org.overture.codegen.cgast.patterns.AStringPatternCG;
import org.overture.codegen.cgast.patterns.ATuplePatternCG;
import org.overture.codegen.cgast.types.ABoolBasicTypeCG;
import org.overture.codegen.cgast.types.ACharBasicTypeCG;
import org.overture.codegen.cgast.types.AIntNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ANat1NumericBasicTypeCG;
import org.overture.codegen.cgast.types.ANatNumericBasicTypeCG;
import org.overture.codegen.cgast.types.AQuoteTypeCG;
import org.overture.codegen.cgast.types.ARealNumericBasicTypeCG;
import org.overture.codegen.cgast.types.ARecordTypeCG;
import org.overture.codegen.cgast.types.ATupleTypeCG;
import org.overture.codegen.cgast.types.AUnionTypeCG;
import org.overture.codegen.cgast.types.AUnknownTypeCG;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;

public class PatternTypeFinder extends QuestionAdaptor<STypeCG>
{
	private IRInfo info;
	private IdentityHashMap<SPatternCG, STypeCG> typeTable;

	public PatternTypeFinder(IRInfo info)
	{
		this.info = info;
		this.typeTable = new IdentityHashMap<>();
	}

	@Override
	public void defaultSPatternCG(SPatternCG node, STypeCG question) throws AnalysisException
	{
		storeType(node, question);
		Logger.getLog().printErrorln("Got unexpected pattern " + node + " in '" + this.getClass().getSimpleName()
				+ "'");
	}

	@Override
	public void caseAIdentifierPatternCG(AIdentifierPatternCG node, STypeCG question) throws AnalysisException
	{
		storeType(node, question);
	}

	@Override
	public void caseAIgnorePatternCG(AIgnorePatternCG node, STypeCG question) throws AnalysisException
	{
		storeType(node, question);
	}

	@Override
	public void caseABoolPatternCG(ABoolPatternCG node, STypeCG question) throws AnalysisException
	{
		storeType(node, new ABoolBasicTypeCG());
	}

	@Override
	public void caseACharPatternCG(ACharPatternCG node, STypeCG question) throws AnalysisException
	{
		storeType(node, new ACharBasicTypeCG());
	}

	@Override
	public void caseAIntPatternCG(AIntPatternCG node, STypeCG question) throws AnalysisException
	{
		long value = node.getValue();

		if (value > 0)
		{
			storeType(node, new ANat1NumericBasicTypeCG());
		} else if (value >= 0)
		{
			storeType(node, new ANatNumericBasicTypeCG());
		} else
		{
			storeType(node, new AIntNumericBasicTypeCG());
		}
	}

	@Override
	public void caseANullPatternCG(ANullPatternCG node, STypeCG question) throws AnalysisException
	{
		storeType(node, new AUnknownTypeCG());
	}

	@Override
	public void caseAQuotePatternCG(AQuotePatternCG node, STypeCG question) throws AnalysisException
	{
		AQuoteTypeCG quoteTypeCg = new AQuoteTypeCG();
		quoteTypeCg.setValue(node.getValue());

		storeType(node, quoteTypeCg);
	}

	@Override
	public void caseARealPatternCG(ARealPatternCG node, STypeCG question) throws AnalysisException
	{
		storeType(node, new ARealNumericBasicTypeCG());
	}

	@Override
	public void caseAStringPatternCG(AStringPatternCG node, STypeCG question) throws AnalysisException
	{
		storeType(node, question);
	}

	@Override
	public void caseATuplePatternCG(ATuplePatternCG node, STypeCG question) throws AnalysisException
	{
		ATupleTypeCG tupType = null;

		if (question instanceof ATupleTypeCG)
		{
			tupType = (ATupleTypeCG) question;

		} else if (question instanceof AUnionTypeCG)
		{
			tupType = info.getPatternAssistant().getTupleType((AUnionTypeCG) question, node);
		}

		storeType(node, question);

		if (tupType != null)
		{

			if (tupType.getTypes().size() == node.getPatterns().size())
			{
				for (int i = 0; i < node.getPatterns().size(); i++)
				{
					SPatternCG p = node.getPatterns().get(i);
					STypeCG t = tupType.getTypes().get(i);

					p.apply(this, t);
				}
			} else
			{
				Logger.getLog().printErrorln("Problem encountered when determining the "
						+ "type of a tuple pattern. Patterns and types do not match in terms of size in '"
						+ this.getClass().getSimpleName() + "'");
			}

		} else
		{
			Logger.getLog().printErrorln("Expected tuple type or union type in '" + this.getClass().getSimpleName()
					+ "'.  Got: " + question);
		}
	}

	@Override
	public void caseARecordPatternCG(ARecordPatternCG node, STypeCG question) throws AnalysisException
	{
		STypeCG type = node.getType();
		storeType(node, type);

		if (type instanceof ARecordTypeCG)
		{
			ARecordTypeCG recType = (ARecordTypeCG) type;

			ARecordDeclCG rec = info.getDeclAssistant().findRecord(info.getClasses(), recType);

			if (rec.getFields().size() == node.getPatterns().size())
			{
				for (int i = 0; i < node.getPatterns().size(); i++)
				{
					SPatternCG p = node.getPatterns().get(i);
					STypeCG t = rec.getFields().get(i).getType();

					p.apply(this, t);
				}
			} else
			{
				Logger.getLog().printErrorln("Record patterns and record fields do not match in terms of size in '"
						+ this.getClass().getSimpleName() + "'");
			}

		} else
		{
			Logger.getLog().printErrorln("Expected record pattern to have a record type in '"
					+ this.getClass().getSimpleName() + "'. Got: " + type);
		}
	}

	private void storeType(SPatternCG pattern, STypeCG type)
	{
		this.typeTable.put(pattern, type);
	}

	public STypeCG getPatternType(SPatternCG pattern)
	{
		return this.typeTable.get(pattern);
	}

	public static STypeCG getType(PatternTypeFinder typeFinder, AIdentifierPatternCG occ)
	{
		STypeCG occType = typeFinder.getPatternType(occ);

		if (occType == null)
		{
			Logger.getLog().printErrorln("Could not find type of identifier pattern " + occ + " in '"
					+ PatternTypeFinder.class.getSimpleName() + "'");
			occType = new AUnknownTypeCG();
		} else
		{
			occType = occType.clone();
		}

		return occType;
	}
}
