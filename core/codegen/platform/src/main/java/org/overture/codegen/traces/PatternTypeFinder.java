package org.overture.codegen.traces;

import java.util.IdentityHashMap;

import org.overture.codegen.ir.SPatternIR;
import org.overture.codegen.ir.STypeIR;
import org.overture.codegen.ir.analysis.AnalysisException;
import org.overture.codegen.ir.analysis.QuestionAdaptor;
import org.overture.codegen.ir.declarations.ARecordDeclIR;
import org.overture.codegen.ir.patterns.ABoolPatternIR;
import org.overture.codegen.ir.patterns.ACharPatternIR;
import org.overture.codegen.ir.patterns.AIdentifierPatternIR;
import org.overture.codegen.ir.patterns.AIgnorePatternIR;
import org.overture.codegen.ir.patterns.AIntPatternIR;
import org.overture.codegen.ir.patterns.ANullPatternIR;
import org.overture.codegen.ir.patterns.AQuotePatternIR;
import org.overture.codegen.ir.patterns.ARealPatternIR;
import org.overture.codegen.ir.patterns.ARecordPatternIR;
import org.overture.codegen.ir.patterns.AStringPatternIR;
import org.overture.codegen.ir.patterns.ATuplePatternIR;
import org.overture.codegen.ir.types.ABoolBasicTypeIR;
import org.overture.codegen.ir.types.ACharBasicTypeIR;
import org.overture.codegen.ir.types.AIntNumericBasicTypeIR;
import org.overture.codegen.ir.types.ANat1NumericBasicTypeIR;
import org.overture.codegen.ir.types.ANatNumericBasicTypeIR;
import org.overture.codegen.ir.types.AQuoteTypeIR;
import org.overture.codegen.ir.types.ARealNumericBasicTypeIR;
import org.overture.codegen.ir.types.ARecordTypeIR;
import org.overture.codegen.ir.types.ATupleTypeIR;
import org.overture.codegen.ir.types.AUnionTypeIR;
import org.overture.codegen.ir.types.AUnknownTypeIR;
import org.overture.codegen.ir.IRInfo;
import org.overture.codegen.logging.Logger;

public class PatternTypeFinder extends QuestionAdaptor<STypeIR>
{
	private IRInfo info;
	private IdentityHashMap<SPatternIR, STypeIR> typeTable;

	public PatternTypeFinder(IRInfo info)
	{
		this.info = info;
		this.typeTable = new IdentityHashMap<>();
	}

	@Override
	public void defaultSPatternIR(SPatternIR node, STypeIR question) throws AnalysisException
	{
		storeType(node, question);
		Logger.getLog().printErrorln("Got unexpected pattern " + node + " in '" + this.getClass().getSimpleName()
				+ "'");
	}

	@Override
	public void caseAIdentifierPatternIR(AIdentifierPatternIR node, STypeIR question) throws AnalysisException
	{
		storeType(node, question);
	}

	@Override
	public void caseAIgnorePatternIR(AIgnorePatternIR node, STypeIR question) throws AnalysisException
	{
		storeType(node, question);
	}

	@Override
	public void caseABoolPatternIR(ABoolPatternIR node, STypeIR question) throws AnalysisException
	{
		storeType(node, new ABoolBasicTypeIR());
	}

	@Override
	public void caseACharPatternIR(ACharPatternIR node, STypeIR question) throws AnalysisException
	{
		storeType(node, new ACharBasicTypeIR());
	}

	@Override
	public void caseAIntPatternIR(AIntPatternIR node, STypeIR question) throws AnalysisException
	{
		long value = node.getValue();

		if (value > 0)
		{
			storeType(node, new ANat1NumericBasicTypeIR());
		} else if (value >= 0)
		{
			storeType(node, new ANatNumericBasicTypeIR());
		} else
		{
			storeType(node, new AIntNumericBasicTypeIR());
		}
	}

	@Override
	public void caseANullPatternIR(ANullPatternIR node, STypeIR question) throws AnalysisException
	{
		storeType(node, new AUnknownTypeIR());
	}

	@Override
	public void caseAQuotePatternIR(AQuotePatternIR node, STypeIR question) throws AnalysisException
	{
		AQuoteTypeIR quoteTypeCg = new AQuoteTypeIR();
		quoteTypeCg.setValue(node.getValue());

		storeType(node, quoteTypeCg);
	}

	@Override
	public void caseARealPatternIR(ARealPatternIR node, STypeIR question) throws AnalysisException
	{
		storeType(node, new ARealNumericBasicTypeIR());
	}

	@Override
	public void caseAStringPatternIR(AStringPatternIR node, STypeIR question) throws AnalysisException
	{
		storeType(node, question);
	}

	@Override
	public void caseATuplePatternIR(ATuplePatternIR node, STypeIR question) throws AnalysisException
	{
		ATupleTypeIR tupType = null;

		if (question instanceof ATupleTypeIR)
		{
			tupType = (ATupleTypeIR) question;

		} else if (question instanceof AUnionTypeIR)
		{
			tupType = info.getPatternAssistant().getTupleType((AUnionTypeIR) question, node);
		}

		storeType(node, question);

		if (tupType != null)
		{

			if (tupType.getTypes().size() == node.getPatterns().size())
			{
				for (int i = 0; i < node.getPatterns().size(); i++)
				{
					SPatternIR p = node.getPatterns().get(i);
					STypeIR t = tupType.getTypes().get(i);

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
	public void caseARecordPatternIR(ARecordPatternIR node, STypeIR question) throws AnalysisException
	{
		STypeIR type = node.getType();
		storeType(node, type);

		if (type instanceof ARecordTypeIR)
		{
			ARecordTypeIR recType = (ARecordTypeIR) type;

			ARecordDeclIR rec = info.getDeclAssistant().findRecord(info.getClasses(), recType);

			if (rec.getFields().size() == node.getPatterns().size())
			{
				for (int i = 0; i < node.getPatterns().size(); i++)
				{
					SPatternIR p = node.getPatterns().get(i);
					STypeIR t = rec.getFields().get(i).getType();

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

	private void storeType(SPatternIR pattern, STypeIR type)
	{
		this.typeTable.put(pattern, type);
	}

	public STypeIR getPatternType(SPatternIR pattern)
	{
		return this.typeTable.get(pattern);
	}

	public static STypeIR getType(PatternTypeFinder typeFinder, AIdentifierPatternIR occ)
	{
		STypeIR occType = typeFinder.getPatternType(occ);

		if (occType == null)
		{
			Logger.getLog().printErrorln("Could not find type of identifier pattern " + occ + " in '"
					+ PatternTypeFinder.class.getSimpleName() + "'");
			occType = new AUnknownTypeIR();
		} else
		{
			occType = occType.clone();
		}

		return occType;
	}
}
