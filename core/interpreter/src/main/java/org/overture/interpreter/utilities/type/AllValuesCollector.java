package org.overture.interpreter.utilities.type;

import java.util.List;
import java.util.Vector;

import org.overture.ast.analysis.AnalysisException;
import org.overture.ast.analysis.QuestionAnswerAdaptor;
import org.overture.ast.assistant.pattern.PTypeList;
import org.overture.ast.factory.AstFactory;
import org.overture.ast.lex.LexNameToken;
import org.overture.ast.node.INode;
import org.overture.ast.patterns.PPattern;
import org.overture.ast.types.ABooleanBasicType;
import org.overture.ast.types.AFieldField;
import org.overture.ast.types.AInMapMapType;
import org.overture.ast.types.ANamedInvariantType;
import org.overture.ast.types.ANatOneNumericBasicType;
import org.overture.ast.types.AOptionalType;
import org.overture.ast.types.AParameterType;
import org.overture.ast.types.AProductType;
import org.overture.ast.types.AQuoteType;
import org.overture.ast.types.ARecordInvariantType;
import org.overture.ast.types.ASetType;
import org.overture.ast.types.AUnionType;
import org.overture.ast.types.PType;
import org.overture.ast.types.SBasicType;
import org.overture.ast.types.SInvariantType;
import org.overture.ast.types.SMapType;
import org.overture.ast.types.SNumericBasicType;
import org.overture.config.Settings;
import org.overture.interpreter.assistant.IInterpreterAssistantFactory;
import org.overture.interpreter.runtime.Context;
import org.overture.interpreter.runtime.ValueException;
import org.overture.interpreter.values.BooleanValue;
import org.overture.interpreter.values.InvariantValue;
import org.overture.interpreter.values.MapValue;
import org.overture.interpreter.values.NameValuePair;
import org.overture.interpreter.values.NameValuePairList;
import org.overture.interpreter.values.NilValue;
import org.overture.interpreter.values.NumericValue;
import org.overture.interpreter.values.ParameterValue;
import org.overture.interpreter.values.Quantifier;
import org.overture.interpreter.values.QuantifierList;
import org.overture.interpreter.values.QuoteValue;
import org.overture.interpreter.values.RecordValue;
import org.overture.interpreter.values.SetValue;
import org.overture.interpreter.values.TupleValue;
import org.overture.interpreter.values.Value;
import org.overture.interpreter.values.ValueList;
import org.overture.interpreter.values.ValueMap;
import org.overture.interpreter.values.ValueSet;
import org.overture.parser.config.Properties;

/**
 * This class collects all the values exist in a type.
 * 
 * @author gkanos
 */
public class AllValuesCollector extends
		QuestionAnswerAdaptor<Context, ValueList>
{
	protected IInterpreterAssistantFactory af;

	public AllValuesCollector(IInterpreterAssistantFactory af)
	{
		this.af = af;
	}

	@Override
	public ValueList caseABooleanBasicType(ABooleanBasicType type, Context ctxt)
			throws AnalysisException
	{
		ValueList v = new ValueList();
		v.add(new BooleanValue(true));
		v.add(new BooleanValue(false));
		return v;
	}

	@Override
	public ValueList defaultSNumericBasicType(SNumericBasicType node,
			Context question) throws AnalysisException
	{
		if (Properties.numeric_type_bind_generation)
		{
			return generateNumbers(Properties.minint, Properties.maxint, question);
		}
		return super.defaultSNumericBasicType(node, question);
	}

	@Override
	public ValueList caseANatOneNumericBasicType(ANatOneNumericBasicType node,
			Context question) throws AnalysisException
	{
		if (Properties.numeric_type_bind_generation)
		{
			int min = Properties.minint;
			if (min < 1)
			{
				min = 1;
			}
			return generateNumbers(min, Properties.maxint, question);
		}
		return super.caseANatOneNumericBasicType(node, question);
	}

	/**
	 * Generator method for numeric type bindings.
	 * 
	 * @param min
	 *            the minimum number
	 * @param max
	 *            the maximum number
	 * @param ctxt
	 *            current context
	 * @return the newly generated list of values between min and max, including.
	 * @throws ValueException
	 */
	protected ValueList generateNumbers(int min, int max, Context ctxt)
			throws ValueException
	{
		ValueList list = new ValueList();
		for (int i = min; i < max + 1; i++)
		{
			list.add(NumericValue.valueOf(i, ctxt));
		}

		return list;
	}

	@Override
	public ValueList defaultSBasicType(SBasicType type, Context ctxt)
			throws AnalysisException
	{
		throw new ValueException(4, "Cannot get bind values for type " + type, ctxt);
	}

	@Override
	public ValueList caseANamedInvariantType(ANamedInvariantType type,
			Context ctxt) throws AnalysisException
	{
		ValueList raw = type.getType().apply(THIS, ctxt);
		boolean checks = Settings.invchecks;
		Settings.invchecks = true;

		ValueList result = new ValueList();
		for (Value v : raw)
		{
			try
			{
				result.add(new InvariantValue(type, v, ctxt));
			} catch (ValueException e)
			{
				// Raw value not in type because of invariant
			}
		}

		Settings.invchecks = checks;
		return result;
	}

	@Override
	public ValueList caseARecordInvariantType(ARecordInvariantType type,
			Context ctxt) throws AnalysisException
	{
		List<PType> types = new Vector<PType>();

		for (AFieldField f : type.getFields())
		{
			types.add(f.getType());
		}

		ValueList results = new ValueList();

		for (Value v : getAllValues(types, ctxt))
		{
			try
			{
				TupleValue tuple = (TupleValue) v;
				results.add(new RecordValue(type, tuple.values, ctxt));
			} catch (ValueException e)
			{
				// Value does not match invariant, so ignore it
			}
		}

		return results;
	}

	@Override
	public ValueList defaultSInvariantType(SInvariantType type, Context ctxt)
			throws AnalysisException
	{
		throw new ValueException(4, "Cannot get bind values for type " + type, ctxt);
	}

	@Override
	public ValueList caseAInMapMapType(AInMapMapType type, Context ctxt)
			throws AnalysisException
	{
		// TODO:Here we have a strange behavior from transforming this call to type.apply(THIS,ctxt)
		ValueList maps = THIS.defaultSMapType(type, ctxt);// ctxt.assistantFactory.createSMapTypeAssistant().getAllValues(type,
															// ctxt);
		ValueList result = new ValueList();

		for (Value map : maps)
		{
			MapValue vm = (MapValue) map;

			if (vm.values.isInjective())
			{
				result.add(vm);
			}
		}

		return result;
	}

	@Override
	public ValueList caseAOptionalType(AOptionalType type, Context ctxt)
			throws AnalysisException
	{
		ValueList list = type.getType().apply(THIS, ctxt);
		list.add(new NilValue());
		return list;
	}

	@Override
	public ValueList caseAProductType(AProductType type, Context ctxt)
			throws AnalysisException
	{
		return getAllValues(type.getTypes(), ctxt);

	}

	@Override
	public ValueList defaultSMapType(SMapType type, Context ctxt)
			throws AnalysisException
	{
		PTypeList tuple = new PTypeList();
		tuple.add(type.getFrom());
		tuple.add(type.getTo());

		ValueList results = new ValueList();
		ValueList tuples = getAllValues(tuple, ctxt);
		ValueSet set = new ValueSet();
		set.addAll(tuples);
		List<ValueSet> psets = set.powerSet();

		for (ValueSet map : psets)
		{
			ValueMap result = new ValueMap();

			for (Value v : map)
			{
				TupleValue tv = (TupleValue) v;
				result.put(tv.values.get(0), tv.values.get(1));
			}

			results.add(new MapValue(result));
		}

		return results;
	}

	@Override
	public ValueList caseAQuoteType(AQuoteType type, Context ctxt)
			throws AnalysisException
	{
		ValueList v = new ValueList();
		v.add(new QuoteValue(type.getValue().getValue()));
		return v;
	}

	@Override
	public ValueList caseASetType(ASetType type, Context ctxt)
			throws AnalysisException
	{
		ValueList list = type.getSetof().apply(THIS, ctxt);
		ValueSet set = new ValueSet(list.size());
		set.addAll(list);
		List<ValueSet> psets = set.powerSet();
		list.clear();

		for (ValueSet v : psets)
		{
			list.add(new SetValue(v));
		}

		return list;
	}

	@Override
	public ValueList caseAUnionType(AUnionType type, Context ctxt)
			throws AnalysisException
	{
		ValueList v = new ValueList();

		for (PType utype : type.getTypes())
		{
			v.addAll(utype.apply(THIS, ctxt));
		}

		return v;
	}

	@Override
	public ValueList caseAParameterType(AParameterType type, Context ctxt)
			throws AnalysisException
	{
		Value t = ctxt.lookup(type.getName());

		if (t == null)
		{
			throw new ValueException(4008, "No such type parameter @"
					+ type.getName() + " in scope", ctxt);
		} else if (t instanceof ParameterValue)
		{
			ParameterValue tv = (ParameterValue) t;
			return tv.type.apply(THIS, ctxt);
		}

		throw new ValueException(4009, "Type parameter/local variable name clash, @"
				+ type.getName(), ctxt);
	}

	@Override
	public ValueList createNewReturnValue(INode node, Context question)
			throws AnalysisException
	{
		throw new ValueException(4, "Cannot get bind values for type " + node, question);
	}

	@Override
	public ValueList createNewReturnValue(Object node, Context question)
			throws AnalysisException
	{
		return null;
	}
	
	public ValueList getAllValues(List<PType> linkedList, Context ctxt)
			throws AnalysisException
	{
		QuantifierList quantifiers = new QuantifierList();
		int n = 0;

		for (PType t : linkedList)
		{
			LexNameToken name = new LexNameToken("#", String.valueOf(n), t.getLocation());
			PPattern p = AstFactory.newAIdentifierPattern(name);
			Quantifier q = new Quantifier(p, af.createPTypeAssistant().getAllValues(t, ctxt));
			quantifiers.add(q);
		}

		quantifiers.init(ctxt, true);
		ValueList results = new ValueList();

		while (quantifiers.hasNext())
		{
			NameValuePairList nvpl = quantifiers.next();
			ValueList list = new ValueList();

			for (NameValuePair nvp : nvpl)
			{
				list.add(nvp.value);
			}

			results.add(new TupleValue(list));
		}

		return results;
	}

}
