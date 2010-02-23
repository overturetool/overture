/*******************************************************************************
 *
 *	Copyright (C) 2008 Fujitsu Services Ltd.
 *
 *	Author: Nick Battle
 *
 *	This file is part of VDMJ.
 *
 *	VDMJ is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	VDMJ is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with VDMJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 ******************************************************************************/

package org.overturetool.vdmj.ast;

import java.io.File;
import java.util.List;
import java.util.Vector;

import jp.co.csk.vdm.toolbox.VDM.CGException;

import org.overturetool.ast.itf.*;
import org.overturetool.vdmj.definitions.AccessSpecifier;
import org.overturetool.vdmj.definitions.AssignmentDefinition;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.ClassInvariantDefinition;
import org.overturetool.vdmj.definitions.ClassList;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.DefinitionList;
import org.overturetool.vdmj.definitions.EqualsDefinition;
import org.overturetool.vdmj.definitions.ExplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ExplicitOperationDefinition;
import org.overturetool.vdmj.definitions.ImplicitFunctionDefinition;
import org.overturetool.vdmj.definitions.ImplicitOperationDefinition;
import org.overturetool.vdmj.definitions.InstanceVariableDefinition;
import org.overturetool.vdmj.definitions.MutexSyncDefinition;
import org.overturetool.vdmj.definitions.NamedTraceDefinition;
import org.overturetool.vdmj.definitions.PerSyncDefinition;
import org.overturetool.vdmj.definitions.SystemDefinition;
import org.overturetool.vdmj.definitions.ThreadDefinition;
import org.overturetool.vdmj.definitions.TypeDefinition;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.expressions.*;
import org.overturetool.vdmj.lex.LexBooleanToken;
import org.overturetool.vdmj.lex.LexCharacterToken;
import org.overturetool.vdmj.lex.LexIdentifierToken;
import org.overturetool.vdmj.lex.LexIntegerToken;
import org.overturetool.vdmj.lex.LexKeywordToken;
import org.overturetool.vdmj.lex.LexLocation;
import org.overturetool.vdmj.lex.LexNameList;
import org.overturetool.vdmj.lex.LexNameToken;
import org.overturetool.vdmj.lex.LexQuoteToken;
import org.overturetool.vdmj.lex.LexRealToken;
import org.overturetool.vdmj.lex.LexStringToken;
import org.overturetool.vdmj.lex.LexToken;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.messages.InternalException;
import org.overturetool.vdmj.patterns.Bind;
import org.overturetool.vdmj.patterns.BooleanPattern;
import org.overturetool.vdmj.patterns.CharacterPattern;
import org.overturetool.vdmj.patterns.ConcatenationPattern;
import org.overturetool.vdmj.patterns.ExpressionPattern;
import org.overturetool.vdmj.patterns.IdentifierPattern;
import org.overturetool.vdmj.patterns.IgnorePattern;
import org.overturetool.vdmj.patterns.IntegerPattern;
import org.overturetool.vdmj.patterns.MultipleBind;
import org.overturetool.vdmj.patterns.MultipleSetBind;
import org.overturetool.vdmj.patterns.MultipleTypeBind;
import org.overturetool.vdmj.patterns.Pattern;
import org.overturetool.vdmj.patterns.PatternBind;
import org.overturetool.vdmj.patterns.PatternList;
import org.overturetool.vdmj.patterns.QuotePattern;
import org.overturetool.vdmj.patterns.RealPattern;
import org.overturetool.vdmj.patterns.RecordPattern;
import org.overturetool.vdmj.patterns.SeqPattern;
import org.overturetool.vdmj.patterns.SetBind;
import org.overturetool.vdmj.patterns.SetPattern;
import org.overturetool.vdmj.patterns.StringPattern;
import org.overturetool.vdmj.patterns.TuplePattern;
import org.overturetool.vdmj.patterns.TypeBind;
import org.overturetool.vdmj.patterns.UnionPattern;
import org.overturetool.vdmj.statements.AlwaysStatement;
import org.overturetool.vdmj.statements.AssignmentStatement;
import org.overturetool.vdmj.statements.AtomicStatement;
import org.overturetool.vdmj.statements.BlockStatement;
import org.overturetool.vdmj.statements.CallObjectStatement;
import org.overturetool.vdmj.statements.CallStatement;
import org.overturetool.vdmj.statements.CaseStmtAlternative;
import org.overturetool.vdmj.statements.CasesStatement;
import org.overturetool.vdmj.statements.CyclesStatement;
import org.overturetool.vdmj.statements.DefStatement;
import org.overturetool.vdmj.statements.DurationStatement;
import org.overturetool.vdmj.statements.ElseIfStatement;
import org.overturetool.vdmj.statements.ErrorCase;
import org.overturetool.vdmj.statements.ErrorStatement;
import org.overturetool.vdmj.statements.ExitStatement;
import org.overturetool.vdmj.statements.ExternalClause;
import org.overturetool.vdmj.statements.FieldDesignator;
import org.overturetool.vdmj.statements.ForAllStatement;
import org.overturetool.vdmj.statements.ForIndexStatement;
import org.overturetool.vdmj.statements.ForPatternBindStatement;
import org.overturetool.vdmj.statements.IdentifierDesignator;
import org.overturetool.vdmj.statements.IfStatement;
import org.overturetool.vdmj.statements.LetBeStStatement;
import org.overturetool.vdmj.statements.LetDefStatement;
import org.overturetool.vdmj.statements.MapSeqDesignator;
import org.overturetool.vdmj.statements.NonDeterministicStatement;
import org.overturetool.vdmj.statements.NotYetSpecifiedStatement;
import org.overturetool.vdmj.statements.ObjectApplyDesignator;
import org.overturetool.vdmj.statements.ObjectDesignator;
import org.overturetool.vdmj.statements.ObjectFieldDesignator;
import org.overturetool.vdmj.statements.ObjectIdentifierDesignator;
import org.overturetool.vdmj.statements.ObjectNewDesignator;
import org.overturetool.vdmj.statements.ObjectSelfDesignator;
import org.overturetool.vdmj.statements.ReturnStatement;
import org.overturetool.vdmj.statements.SimpleBlockStatement;
import org.overturetool.vdmj.statements.SkipStatement;
import org.overturetool.vdmj.statements.SpecificationStatement;
import org.overturetool.vdmj.statements.StartStatement;
import org.overturetool.vdmj.statements.StateDesignator;
import org.overturetool.vdmj.statements.Statement;
import org.overturetool.vdmj.statements.SubclassResponsibilityStatement;
import org.overturetool.vdmj.statements.TixeStatement;
import org.overturetool.vdmj.statements.TixeStmtAlternative;
import org.overturetool.vdmj.statements.TrapStatement;
import org.overturetool.vdmj.statements.WhileStatement;
import org.overturetool.vdmj.traces.TraceApplyExpression;
import org.overturetool.vdmj.traces.TraceBracketedExpression;
import org.overturetool.vdmj.traces.TraceCoreDefinition;
import org.overturetool.vdmj.traces.TraceDefinition;
import org.overturetool.vdmj.traces.TraceDefinitionTerm;
import org.overturetool.vdmj.traces.TraceLetBeStBinding;
import org.overturetool.vdmj.traces.TraceLetDefBinding;
import org.overturetool.vdmj.traces.TraceRepeatDefinition;
import org.overturetool.vdmj.typechecker.NameScope;
import org.overturetool.vdmj.types.BooleanType;
import org.overturetool.vdmj.types.BracketType;
import org.overturetool.vdmj.types.CharacterType;
import org.overturetool.vdmj.types.Field;
import org.overturetool.vdmj.types.FunctionType;
import org.overturetool.vdmj.types.InMapType;
import org.overturetool.vdmj.types.IntegerType;
import org.overturetool.vdmj.types.InvariantType;
import org.overturetool.vdmj.types.MapType;
import org.overturetool.vdmj.types.NamedType;
import org.overturetool.vdmj.types.NaturalOneType;
import org.overturetool.vdmj.types.NaturalType;
import org.overturetool.vdmj.types.OperationType;
import org.overturetool.vdmj.types.OptionalType;
import org.overturetool.vdmj.types.ParameterType;
import org.overturetool.vdmj.types.PatternListTypePair;
import org.overturetool.vdmj.types.PatternTypePair;
import org.overturetool.vdmj.types.ProductType;
import org.overturetool.vdmj.types.QuoteType;
import org.overturetool.vdmj.types.RationalType;
import org.overturetool.vdmj.types.RealType;
import org.overturetool.vdmj.types.RecordType;
import org.overturetool.vdmj.types.Seq1Type;
import org.overturetool.vdmj.types.SeqType;
import org.overturetool.vdmj.types.SetType;
import org.overturetool.vdmj.types.TokenType;
import org.overturetool.vdmj.types.Type;
import org.overturetool.vdmj.types.TypeList;
import org.overturetool.vdmj.types.UnionType;
import org.overturetool.vdmj.types.UnresolvedType;
import org.overturetool.vdmj.types.VoidType;

@SuppressWarnings("unchecked")
public class ASTConverter
{
	private static final long MAX_TIMES = 5;

	public final File filename;
	public final IOmlDocument document;
	private String classname = null;
	private LexNameToken classLexName = null;

	public ASTConverter(File filename, IOmlDocument document)
	{
		this.filename = filename;
		this.document = document;
	}

	public ClassList convertDocument()
	{
		try
		{
			if (document.hasSpecifications())
			{
				return convertSpecifications(document.getSpecifications());
			}
			else
			{
				throw new InternalException(12, "Document has no specifications?");
			}
		}
		catch (CGException e)
		{
			throw new InternalException(0, e.getMessage());
		}
	}

	public Expression convertDocumentExpression()
	{
		try
		{
			if (document.hasExpression())
			{
				return convertExpression(document.getExpression());
			}
			else
			{
				throw new InternalException(13, "Document has no expression?");
			}
		}
		catch (CGException e)
		{
			throw new InternalException(0, e.getMessage());
		}
	}

	private ClassList convertSpecifications(IOmlSpecifications specs)
		throws CGException
	{
		List<IOmlClass> astClasses = specs.getClassList();
		ClassList result = new ClassList();

		for (IOmlClass cls: astClasses)
		{
			setClassName(cls);
			result.add(convertClass(cls));
		}

		return result;
	}

	private ClassDefinition convertClass(IOmlClass cls) throws CGException
	{
		LexNameList supernames = convertInheritance(cls);
		DefinitionList definitions = convertDefinitionsBlocks(cls.getClassBody());

		if (cls.getSystemSpec())
		{
			return new SystemDefinition(classLexName, definitions);
		}
		else
		{
			return new ClassDefinition(classLexName, supernames, definitions);
		}
	}

	private LexNameList convertInheritance(IOmlClass cls)
		throws CGException
	{
		LexNameList names = new LexNameList();

		if (cls.hasInheritanceClause())
		{
    		IOmlInheritanceClause clause = cls.getInheritanceClause();
    		List<String> ids = clause.getIdentifierList();

    		for (String id: ids)
    		{
    			names.add(new LexNameToken("CLASS", id, getLocation(clause)));
    		}
		}

		return names;
	}

	private DefinitionList convertDefinitionsBlocks(List<IOmlDefinitionBlock> classBody)
		throws CGException
	{
		DefinitionList definitions = new DefinitionList();

		for (IOmlDefinitionBlock block: classBody)
		{
			definitions.addAll(convertDefinitionBlock(block));
		}

		return definitions;
	}

	private DefinitionList convertDefinitionBlock(IOmlDefinitionBlock block)
		throws CGException
	{
		DefinitionList defs = null;

		if (block instanceof IOmlTypeDefinitions)
		{
			IOmlTypeDefinitions tdefs = (IOmlTypeDefinitions)block;
			defs = convertTypeDefinitions(tdefs.getTypeList());
		}
		else if (block instanceof IOmlValueDefinitions)
		{
			IOmlValueDefinitions vdefs = (IOmlValueDefinitions)block;
			defs = convertValueDefinitions(vdefs.getValueList());
		}
		else if (block instanceof IOmlFunctionDefinitions)
		{
			IOmlFunctionDefinitions fdefs = (IOmlFunctionDefinitions)block;
			defs = convertFunctionDefinitions(fdefs.getFunctionList());
		}
		else if (block instanceof IOmlOperationDefinitions)
		{
			IOmlOperationDefinitions opdefs = (IOmlOperationDefinitions)block;
			defs = convertOperationDefinitions(opdefs.getOperationList());
		}
		else if (block instanceof IOmlInstanceVariableDefinitions)
		{
			IOmlInstanceVariableDefinitions ivdefs = (IOmlInstanceVariableDefinitions)block;
			defs = convertInstanceVariableDefinitions(ivdefs.getVariablesList());
		}
		else if (block instanceof IOmlSynchronizationDefinitions)
		{
			IOmlSynchronizationDefinitions sdefs = (IOmlSynchronizationDefinitions)block;
			defs = convertSynchronizationDefinitions(sdefs.getSyncList());
		}
		else if (block instanceof IOmlThreadDefinition)
		{
			defs = convertThreadDefinition((IOmlThreadDefinition)block);
		}
		else if (block instanceof IOmlTraceDefinitions)
		{
			IOmlTraceDefinitions tdefs = (IOmlTraceDefinitions)block;
			defs = convertNamedTraceDefinitions(tdefs.getTraces());
		}
		else
		{
			throw new InternalException(14, "Unexpected type in definition block");
		}

		return defs;
	}

	private DefinitionList convertTypeDefinitions(List<IOmlTypeDefinition> tdefs)
		throws CGException
	{
		DefinitionList defs = new DefinitionList();

		for (IOmlTypeDefinition tdef: tdefs)
		{
			defs.add(convertTypeDefinition(tdef));
		}

		return defs;
	}

	private Definition convertTypeDefinition(IOmlTypeDefinition tdef)
		throws CGException
	{
		IOmlTypeShape shape = tdef.getShape();
		IOmlAccessDefinition access = tdef.getAccess();
		Definition def = null;

		if (shape instanceof IOmlSimpleType)
		{
			IOmlSimpleType st = (IOmlSimpleType)shape;
			LexNameToken name = idToName(st.getIdentifier(), st);
			Type type = convertType(st.getType());
			InvariantType ntype = new NamedType(name, type);

			Pattern invp = null;
			Expression inve = null;

			if (st.hasInvariant())
			{
    			IOmlInvariant inv = st.getInvariant();
				invp = convertPattern(inv.getPattern());
				inve = convertExpression(inv.getExpression());
			}

			def = new TypeDefinition(name, ntype, invp, inve);
		}
		else if (shape instanceof IOmlComplexType)
		{
			IOmlComplexType st = (IOmlComplexType)shape;
			LexNameToken name = idToName(st.getIdentifier(), st);
			List<Field> flist = convertFieldList(st.getFieldList());
			InvariantType ntype = new RecordType(name, flist);

			Pattern invp = null;
			Expression inve = null;

			if (st.hasInvariant())
			{
    			IOmlInvariant inv = st.getInvariant();
				invp = convertPattern(inv.getPattern());
				inve = convertExpression(inv.getExpression());
			}

			def = new TypeDefinition(name, ntype, invp, inve);
		}
		else
		{
			throw new InternalException(15, "Unexpected type definition shape: " + shape);
		}

		def.setAccessSpecifier(convertAccess(access));
		return def;
	}

	private DefinitionList convertValueDefinitions(List<IOmlValueDefinition> vdefs)
		throws CGException
	{
		DefinitionList defs = new DefinitionList();

		for (IOmlValueDefinition vdef: vdefs)
		{
			defs.add(convertValueDefinition(vdef));
		}

		return defs;
	}

	private Definition convertValueDefinition(IOmlValueDefinition vdef)
		throws CGException
	{
		IOmlAccessDefinition access = vdef.getAccess();
		IOmlValueShape shape = vdef.getShape();
		IOmlPattern pattern = shape.getPattern();
		IOmlType type = shape.hasType() ? shape.getType() : null;
		IOmlExpression expression = shape.getExpression();

		ValueDefinition def = new ValueDefinition(
			convertPattern(pattern),
			NameScope.GLOBAL,
			type == null ? null : convertType(type),
			convertExpression(expression));

		def.setAccessSpecifier(convertAccess(access));
		return def;
	}

	private DefinitionList convertFunctionDefinitions(
		List<IOmlFunctionDefinition> fdefs) throws CGException
	{
		DefinitionList defs = new DefinitionList();

		for (IOmlFunctionDefinition fdef: fdefs)
		{
			defs.add(convertFunctionDefinition(fdef));
		}

		return defs;
	}

	private Definition convertFunctionDefinition(IOmlFunctionDefinition fdef)
		throws CGException
	{
		IOmlFunctionShape shape = fdef.getShape();
		IOmlAccessDefinition access = fdef.getAccess();
		Definition def = null;

		if (shape instanceof IOmlExplicitFunction)
		{
			IOmlExplicitFunction exf = (IOmlExplicitFunction)shape;
			List<IOmlTypeVariable> typeParams = exf.getTypeVariableList();
			IOmlType type = exf.getType();
			List<IOmlParameter> parameters = exf.getParameterList();
			IOmlFunctionBody body = exf.getBody();
			IOmlFunctionTrailer trailer = exf.getTrailer();

			def = new ExplicitFunctionDefinition(
				idToName(exf.getIdentifier(), exf),
				NameScope.GLOBAL,
				convertTypeVariableList(typeParams),
				(FunctionType)convertType(type),
				convertParameterList(parameters),
				convertFunctionBody(body),
				trailer.hasPreExpression() ?
					convertExpression(trailer.getPreExpression()) : null,
				trailer.hasPostExpression() ?
					convertExpression(trailer.getPostExpression()) : null,
				false,
				null);
		}
		else if (shape instanceof IOmlExtendedExplicitFunction)
		{
			IOmlExtendedExplicitFunction exf = (IOmlExtendedExplicitFunction)shape;
			List<IOmlTypeVariable> typeParams = exf.getTypeVariableList();
			List<IOmlPatternTypePair> parameters = exf.getPatternTypePairList();
			List<IOmlIdentifierTypePair> results = exf.getIdentifierTypePairList();
			IOmlFunctionBody body = exf.getBody();
			IOmlFunctionTrailer trailer = exf.getTrailer();

			def = new ImplicitFunctionDefinition(
				idToName(exf.getIdentifier(), exf),
				NameScope.GLOBAL,
				convertTypeVariableList(typeParams),
				convertPatternTypePairList(parameters),
				convertIdentifierTypePairList(results, getLocation(shape)),
				convertFunctionBody(body),
				trailer.hasPreExpression() ?
					convertExpression(trailer.getPreExpression()) : null,
				trailer.hasPostExpression() ?
					convertExpression(trailer.getPostExpression()) : null, null);
		}
		else if (shape instanceof IOmlImplicitFunction)
		{
			IOmlImplicitFunction exf = (IOmlImplicitFunction)shape;
			List<IOmlTypeVariable> typeParams = exf.getTypeVariableList();
			List<IOmlPatternTypePair> parameters = exf.getPatternTypePairList();
			List<IOmlIdentifierTypePair> results = exf.getIdentifierTypePairList();
			IOmlFunctionTrailer trailer = exf.getTrailer();

			def = new ImplicitFunctionDefinition(
				idToName(exf.getIdentifier(), exf),
				NameScope.GLOBAL,
				convertTypeVariableList(typeParams),
				convertPatternTypePairList(parameters),
				convertIdentifierTypePairList(results, getLocation(shape)),
				null,
				trailer.hasPreExpression() ?
					convertExpression(trailer.getPreExpression()) : null,
				trailer.hasPostExpression() ?
					convertExpression(trailer.getPostExpression()) : null, null);
		}
		else if (shape instanceof IOmlTypelessExplicitFunction)
		{
			throw new InternalException(16, "Typeless functions not supported");
		}
		else
		{
			throw new InternalException(17, "Unexpected function shape: " + shape);
		}

		def.setAccessSpecifier(convertAccess(access));
		return def;
	}

	private Expression convertFunctionBody(IOmlFunctionBody body)
		throws CGException
	{
		if (body.hasFunctionBody())
		{
			return convertExpression(body.getFunctionBody());
		}
		else if (body.getNotYetSpecified())
		{
			return new NotYetSpecifiedExpression(getLocation(body));
		}
		else if (body.getSubclassResponsibility())
		{
			return new SubclassResponsibilityExpression(getLocation(body));
		}
		else
		{
			throw new InternalException(18, "Unknown function body type");
		}
	}

	private DefinitionList convertOperationDefinitions(
		List<IOmlOperationDefinition> opdefs) throws CGException
	{
		DefinitionList defs = new DefinitionList();

		for (IOmlOperationDefinition opdef: opdefs)
		{
			defs.add(convertOperationDefinition(opdef));
		}

		return defs;
	}

	private Definition convertOperationDefinition(IOmlOperationDefinition odef)
		throws CGException
	{
		IOmlOperationShape shape = odef.getShape();
		IOmlAccessDefinition access = odef.getAccess();
		Definition def = null;

		if (shape instanceof IOmlExplicitOperation)
		{
			IOmlExplicitOperation exop = (IOmlExplicitOperation)shape;
			IOmlType type = exop.getType();
			List<IOmlPattern> parameters = exop.getParameterList();
			IOmlOperationBody body = exop.getBody();
			IOmlOperationTrailer trailer = exop.getTrailer();

			def = new ExplicitOperationDefinition(
				idToName(exop.getIdentifier(), exop),
				(OperationType)convertType(type),
				convertPatternList(parameters),
				trailer.hasPreExpression() ?
					convertExpression(trailer.getPreExpression()) : null,
				trailer.hasPostExpression() ?
					convertExpression(trailer.getPostExpression()) : null,
				convertOperationBody(body));
		}
		else if (shape instanceof IOmlExtendedExplicitOperation)
		{
			IOmlExtendedExplicitOperation exf = (IOmlExtendedExplicitOperation)shape;
			List<IOmlPatternTypePair> parameters = exf.getPatternTypePairList();
			List<IOmlIdentifierTypePair> results = exf.getIdentifierTypePairList();
			IOmlOperationBody body = exf.getBody();
			IOmlOperationTrailer trailer = exf.getTrailer();

			def = new ImplicitOperationDefinition(
				idToName(exf.getIdentifier(), exf),
				convertPatternTypePairList(parameters),
				convertIdentifierTypePairList(results, getLocation(shape)),
				convertOperationBody(body),
				convertOperationTrailer(trailer));
		}
		else if (shape instanceof IOmlImplicitOperation)
		{
			IOmlImplicitOperation exf = (IOmlImplicitOperation)shape;
			List<IOmlPatternTypePair> parameters = exf.getPatternTypePairList();
			List<IOmlIdentifierTypePair> results = exf.getIdentifierTypePairList();
			IOmlOperationTrailer trailer = exf.getTrailer();

			def = new ImplicitOperationDefinition(
				idToName(exf.getIdentifier(), exf),
				convertPatternTypePairList(parameters),
				convertIdentifierTypePairList(results, getLocation(shape)),
				null,
				convertOperationTrailer(trailer));
		}
		else
		{
			throw new InternalException(19, "Unexpected operation shape: " + shape);
		}

		def.setAccessSpecifier(convertAccess(access));
		return def;
	}

	private Statement convertOperationBody(IOmlOperationBody body)
		throws CGException
	{
		if (body.hasStatement())
		{
			return convertStatement(body.getStatement());
		}
		else if (body.getNotYetSpecified())
		{
			return new NotYetSpecifiedStatement(getLocation(body));
		}
		else if (body.getSubclassResponsibility())
		{
			return new SubclassResponsibilityStatement(getLocation(body));
		}
		else
		{
			throw new InternalException(20, "Unknown operation body type");
		}
	}

	private SpecificationStatement convertOperationTrailer(
		IOmlOperationTrailer trailer) throws CGException
	{
		return new SpecificationStatement(
			getLocation(trailer),
			trailer.hasExternals() ?
				convertExternals(trailer.getExternals()) : null,
			trailer.hasPreExpression() ?
				convertExpression(trailer.getPreExpression()) : null,
			trailer.hasPostExpression() ?
				convertExpression(trailer.getPostExpression()) : null,
			trailer.hasExceptions() ?
				convertErrors(trailer.getExceptions()) : null);
	}

	private List<ErrorCase> convertErrors(IOmlExceptions exceptions)
		throws CGException
	{
		List<ErrorCase> list = new Vector<ErrorCase>();
		List<IOmlError> errors = exceptions.getErrorList();

		for (IOmlError err: errors)
		{
			LexIdentifierToken name = getId(err.getIdentifier(), err);
			Expression left = convertExpression(err.getLhs());
			Expression right = convertExpression(err.getRhs());
			list.add(new ErrorCase(name, left, right));
		}

		return list;
	}

	private List<ExternalClause> convertExternals(IOmlExternals externals)
		throws CGException
	{
		List<ExternalClause> list = new Vector<ExternalClause>();
		List<IOmlVarInformation> exlist = externals.getExtList();
		LexLocation location = getLocation(externals);

		for (IOmlVarInformation var: exlist)
		{
			long mode = var.getMode().getValue();
			LexToken tmode = null;

			if (mode == IOmlModeQuotes.IQRD)
			{
				tmode = new LexKeywordToken(Token.READ, location);
			}
			else if (mode == IOmlModeQuotes.IQWR)
			{
				tmode = new LexKeywordToken(Token.WRITE, location);
			}

			List<IOmlName> nlist = var.getNameList();
			LexNameList names = convertNameList(nlist);
			Type type = null;

			if (var.hasType())
			{
				type = convertType(var.getType());
			}

			list.add(new ExternalClause(tmode, names, type));
		}

		return list;
	}

	private DefinitionList convertInstanceVariableDefinitions(
		List<IOmlInstanceVariableShape> ivdefs) throws CGException
	{
		DefinitionList defs = new DefinitionList();

		for (IOmlInstanceVariableShape ivdef: ivdefs)
		{
			defs.add(convertInstanceVariableDefinition(ivdef));
		}

		return defs;
	}

	private Definition convertInstanceVariableDefinition(
		IOmlInstanceVariableShape shape) throws CGException
	{
		Definition def = null;

		if (shape instanceof IOmlInstanceVariable)
		{
			IOmlInstanceVariable iv = (IOmlInstanceVariable)shape;
			IOmlAccessDefinition access = iv.getAccess();
			IOmlAssignmentDefinition assdef = iv.getAssignmentDefinition();
			LexNameToken name = idToName(assdef.getIdentifier(), assdef);
			Type type = convertType(assdef.getType());
			Expression value = null;

			if (assdef.hasExpression())
			{
				value = convertExpression(assdef.getExpression());
			}
			else
			{
				value = new UndefinedExpression(name.location);
			}

			def = new InstanceVariableDefinition(name, type, value);
			def.setAccessSpecifier(convertAccess(access));
		}
		else if (shape instanceof IOmlInstanceVariableInvariant)
		{
			IOmlInstanceVariableInvariant inv = (IOmlInstanceVariableInvariant)shape;

			def = new ClassInvariantDefinition(
				classLexName.getInvName(getLocation(inv)),
				convertExpression(inv.getInvariant()));
		}
		else
		{
			throw new InternalException(21, "Unknown instance variable type");
		}

		return def;
	}

	private DefinitionList convertSynchronizationDefinitions(
		List<IOmlSyncPredicate> sdefs) throws CGException
	{
		DefinitionList defs = new DefinitionList();

		for (IOmlSyncPredicate sdef: sdefs)
		{
			defs.add(convertSyncPredicate(sdef));
		}

		return defs;
	}

	private Definition convertSyncPredicate(IOmlSyncPredicate sdef)
		throws CGException
	{
		Definition def = null;

		if (sdef instanceof IOmlPermissionPredicate)
		{
			IOmlPermissionPredicate pp = (IOmlPermissionPredicate)sdef;

			def = new PerSyncDefinition(
				getLocation(sdef),
				convertName(pp.getName()),
				convertExpression(pp.getExpression()));
		}
		else if (sdef instanceof IOmlMutexPredicate)
		{
			IOmlMutexPredicate mp = (IOmlMutexPredicate)sdef;

			def = new MutexSyncDefinition(
				getLocation(sdef), convertNameList(mp.getNameList()));
		}
		else if (sdef instanceof IOmlMutexAllPredicate)
		{
			def = new MutexSyncDefinition(getLocation(sdef), new LexNameList());
		}
		else
		{
			throw new InternalException(22, "Unknown sync predicate type");
		}

		return def;
	}

	private DefinitionList convertThreadDefinition(IOmlThreadDefinition tdef)
		throws CGException
	{
		DefinitionList list = new DefinitionList();

		if (tdef.hasThreadSpecification())
		{
			IOmlThreadSpecification th = tdef.getThreadSpecification();

			if (th instanceof IOmlPeriodicThread)
			{
				IOmlPeriodicThread pt = (IOmlPeriodicThread)th;
				ExpressionList args = convertExpressionList(pt.getArgs());
				list.add(new ThreadDefinition(convertName(pt.getName()), args));
			}
			else if (th instanceof IOmlSporadicThread)
			{
				throw new InternalException(24, "Sporadic threads not implemented");
			}
			else if (th instanceof IOmlProcedureThread)
			{
				IOmlProcedureThread pt = (IOmlProcedureThread)th;
				list.add(new ThreadDefinition(convertStatement(pt.getStatement())));
			}
			else
			{
				throw new InternalException(25, "Unknown thread specification type");
			}
		}

		return list;
	}

	private DefinitionList convertNamedTraceDefinitions(List<IOmlNamedTrace> names)
		throws CGException
	{
		DefinitionList defs = new DefinitionList();

		for (IOmlNamedTrace name: names)
		{
			defs.add(convertNamedTraceDefinition(name));
		}

		return defs;
	}

	private Definition convertNamedTraceDefinition(IOmlNamedTrace name)
		throws CGException
	{
		return new NamedTraceDefinition(
			getLocation(name),
			name.getName(),
			convertTraceDefinition(name.getDefs()));
	}

	private List<TraceDefinitionTerm> convertTraceDefinition(
		IOmlTraceDefinition tdef) throws CGException
	{
		List<TraceDefinitionTerm> terms = new Vector<TraceDefinitionTerm>();
		TraceDefinitionTerm term = new TraceDefinitionTerm();

		if (tdef instanceof IOmlTraceDefinitionItem)
		{
			IOmlTraceDefinitionItem item = (IOmlTraceDefinitionItem)tdef;
			term.add(convertTraceDefinitionItem(item));
			terms.add(term);
		}
		else if (tdef instanceof IOmlTraceSequenceDefinition)
		{
			IOmlTraceSequenceDefinition seq = (IOmlTraceSequenceDefinition)tdef;
			List<IOmlTraceDefinition> items = seq.getDefs();

			for (IOmlTraceDefinition item: items)
			{
				terms.addAll(convertTraceDefinition(item));
			}
		}
		else if (tdef instanceof IOmlTraceChoiceDefinition)
		{
			IOmlTraceChoiceDefinition choice = (IOmlTraceChoiceDefinition)tdef;
			List<IOmlTraceDefinitionItem> items = choice.getDefs();

			for (IOmlTraceDefinitionItem item: items)
			{
				term.add(convertTraceDefinitionItem(item));
			}

			terms.add(term);
		}
		else
		{
			throw new InternalException(28, "Unknown trace specification type");
		}

		return terms;
	}

	private TraceDefinition convertTraceDefinitionItem(
		IOmlTraceDefinitionItem item) throws CGException
	{
		TraceCoreDefinition core = convertTraceCore(item.getTest());
		TraceRepeatDefinition repeat = convertTraceRepeatPattern(item, core);
		List<IOmlTraceBinding> bindings = item.getBind();

		if (bindings.isEmpty())
		{
			return repeat;
		}
		else
		{
			return convertBindings(bindings, repeat);
		}
	}

	private TraceRepeatDefinition convertTraceRepeatPattern(
		IOmlTraceDefinitionItem item, TraceCoreDefinition core)
		throws CGException
	{
		long from = 1;
		long to = 1;

		if (item.hasRegexpr())
		{
			IOmlTraceRepeatPattern regexpr = item.getRegexpr();

    		if (regexpr instanceof IOmlTraceZeroOrMore)
    		{
    			from = 0;
    			to = MAX_TIMES;
    		}
    		else if (regexpr instanceof IOmlTraceOneOrMore)
    		{
    			from = 1;
    			to = MAX_TIMES;
    		}
    		else if (regexpr instanceof IOmlTraceZeroOrOne)
    		{
    			from = 0;
    			to = 1;
    		}
    		else if (regexpr instanceof IOmlTraceRange)
    		{
    			IOmlTraceRange range = (IOmlTraceRange)regexpr;
    			from = range.getLower().getVal();
    			to = range.getUpper().getVal();
    		}
		}

		return new TraceRepeatDefinition(core.location, core, from, to);
	}

	private TraceCoreDefinition convertTraceCore(IOmlTraceCoreDefinition core)
		throws CGException
	{
		if (core instanceof IOmlTraceMethodApply)
		{
			IOmlTraceMethodApply apply = (IOmlTraceMethodApply)core;
			LexNameToken var = idToName(apply.getVariableName(), core);
			ObjectIdentifierDesignator obj = new ObjectIdentifierDesignator(var);
			ExpressionList args = convertExpressionList(apply.getArgs());

			return new TraceApplyExpression(
				new CallObjectStatement(obj, null, apply.getMethodName(), args),
				classname);
		}
		else
		{
			IOmlTraceBracketedDefinition block = (IOmlTraceBracketedDefinition)core;
			return new TraceBracketedExpression(getLocation(core),
				convertTraceDefinition(block.getDefinition()));
		}
	}

	private TraceDefinition convertBindings(
		List<IOmlTraceBinding> bindings, TraceRepeatDefinition repeat)
		throws CGException
	{
		if (bindings.isEmpty())
		{
			return repeat;
		}
		else
		{
    		IOmlTraceBinding bind = bindings.get(0);
    		bindings.remove(0);

    		if (bind instanceof IOmlTraceLetBinding)
    		{
    			IOmlTraceLetBinding let = (IOmlTraceLetBinding)bind;
    			DefinitionList defs = convertValueShapeList(let.getDefinitionList());
    			List<ValueDefinition> vdefs = new Vector<ValueDefinition>();

    			for (Definition d: defs)
    			{
    				if (d instanceof ValueDefinition)
    				{
    					vdefs.add((ValueDefinition)d);
    				}
    				else
    				{
    					throw new InternalException(26, "Let binding expects value definition");
    				}
    			}

    			return new TraceLetDefBinding(
    				getLocation(bind), vdefs, convertBindings(bindings, repeat));
    		}
    		else
    		{
    			IOmlTraceLetBeBinding let = (IOmlTraceLetBeBinding)bind;
    			MultipleBind b = convertMultiBind(let.getBind());
    			Expression e = let.hasBest() ? convertExpression(let.getBest()) : null;
    			return new TraceLetBeStBinding(
    				getLocation(let), b, e, convertBindings(bindings, repeat));
    		}
		}
	}

	private LexLocation getLocation(IOmlNode node) throws CGException
	{
		return new LexLocation(filename, classname,
			node.getLine().intValue(), node.getColumn().intValue(), 0, 0);
	}

	private LexNameToken idToName(String name, IOmlNode node)
		throws CGException
	{
		LexNameToken lex = new LexNameToken(classname, name, getLocation(node));
		return lex;
	}

	private void setClassName(IOmlClass cls) throws CGException
	{
		// return new LexNameToken(classname, classname, new LexLocation());
		classname = cls.getIdentifier();
		classLexName =  new LexNameToken("CLASS", cls.getIdentifier(), getLocation(cls));
	}

	private LexIdentifierToken getId(String name, IOmlNode node) throws CGException
	{
		return new LexIdentifierToken(name, false, getLocation(node));
	}

	private LexIdentifierToken getOldId(String name, IOmlNode node) throws CGException
	{
		return new LexIdentifierToken(name, true, getLocation(node));
	}

	private Statement convertStatement(IOmlStatement statement) throws CGException
	{
		LexLocation location = getLocation(statement);
		Statement stmt = null;

		if (statement instanceof IOmlReturnStatement)
		{
			IOmlReturnStatement rs = (IOmlReturnStatement)statement;

			if (rs.hasExpression())
			{
				stmt = new ReturnStatement(location,
					convertExpression(rs.getExpression()));
			}
			else
			{
				stmt = new ReturnStatement(location);
			}
		}
		else if (statement instanceof IOmlLetStatement)
		{
			IOmlLetStatement ls = (IOmlLetStatement)statement;
			DefinitionList defs = convertValueShapeList(ls.getDefinitionList());
			Statement body = convertStatement(ls.getStatement());
			stmt = new LetDefStatement(location, defs, body);
		}
		else if (statement instanceof IOmlLetBeStatement)
		{
			IOmlLetBeStatement lbs = (IOmlLetBeStatement)statement;
			MultipleBind bind = convertMultiBind(lbs.getBind());
			Expression best = null;

			if (lbs.hasBest())
			{
				best = convertExpression(lbs.getBest());
			}

			Statement body = convertStatement(lbs.getStatement());
			stmt = new LetBeStStatement(location, bind, best, body);
		}
		else if (statement instanceof IOmlDefStatement)
		{
			IOmlDefStatement ds = (IOmlDefStatement)statement;
			List<IOmlEqualsDefinition> eqdefs = ds.getDefinitionList();
			DefinitionList defs = convertEqualsDefinitionList(eqdefs);
			Statement body = convertStatement(ds.getStatement());
			stmt = new DefStatement(location, defs, body);
		}
		else if (statement instanceof IOmlBlockStatement)
		{
			IOmlBlockStatement bs = (IOmlBlockStatement)statement;
			List<IOmlDclStatement> dcls = bs.getDclStatementList();
			DefinitionList defs = convertDclStatementList(dcls);
			List<IOmlStatement> stmtlist = bs.getStatementList();

			BlockStatement block = new BlockStatement(location, defs);

			for (IOmlStatement s: stmtlist)
			{
				block.add(convertStatement(s));
			}

			stmt = block;
		}
		else if (statement instanceof IOmlAssignStatement)
		{
			IOmlAssignStatement ass = (IOmlAssignStatement)statement;
			StateDesignator des = convertDesignator(ass.getStateDesignator());
			Expression value = convertExpression(ass.getExpression());
			stmt = new AssignmentStatement(location, des, value);
		}
		else if (statement instanceof IOmlAtomicStatement)
		{
			IOmlAtomicStatement as = (IOmlAtomicStatement)statement;
			List<IOmlAssignStatement> assigns = as.getAssignmentList();
			List<AssignmentStatement> stmts = new Vector<AssignmentStatement>();

			for (IOmlAssignStatement ass: assigns)
			{
				StateDesignator des = convertDesignator(ass.getStateDesignator());
				Expression value = convertExpression(ass.getExpression());
				stmts.add(new AssignmentStatement(location, des, value));
			}

			stmt = new AtomicStatement(location, stmts);
		}
		else if (statement instanceof IOmlIfStatement)
		{
			IOmlIfStatement ifs = (IOmlIfStatement)statement;
			Expression ie = convertExpression(ifs.getExpression());
			Statement ts = convertStatement(ifs.getThenStatement());
			List<IOmlElseIfStatement> elseifs = ifs.getElseifStatement();
			List<ElseIfStatement> eis = convertElseifStmtList(elseifs);
			Statement es = null;

			if (ifs.hasElseStatement())
			{
				es = convertStatement(ifs.getElseStatement());
			}

			stmt = new IfStatement(location, ie, ts, eis, es);

		}
		else if (statement instanceof IOmlCasesStatement)
		{
			IOmlCasesStatement cexp = (IOmlCasesStatement)statement;
			Expression test = convertExpression(cexp.getMatchExpression());
			List<IOmlCasesStatementAlternative> alts = cexp.getAlternativeList();
			List<CaseStmtAlternative> list = convertAlternativeStmtList(alts);
			Statement others = null;

			if (cexp.hasOthersStatement())
			{
				others = convertStatement(cexp.getOthersStatement());
			}

			stmt = new CasesStatement(location, test, list, others);
		}
		else if (statement instanceof IOmlSequenceForLoop)
		{
			IOmlSequenceForLoop fl = (IOmlSequenceForLoop)statement;
			PatternBind pb = convertPatternBind(fl.getPatternBind());
			boolean rev = fl.getInReverse();
			Expression exp = convertExpression(fl.getExpression());
			Statement body = convertStatement(fl.getStatement());
			stmt = new ForPatternBindStatement(location, pb, rev, exp, body);
		}
		else if (statement instanceof IOmlSetForLoop)
		{
			IOmlSetForLoop fl = (IOmlSetForLoop)statement;
			Pattern pattern = convertPattern(fl.getPattern());
			Expression set = convertExpression(fl.getExpression());
			Statement body = convertStatement(fl.getStatement());
			stmt = new ForAllStatement(location, pattern, set, body);
		}
		else if (statement instanceof IOmlIndexForLoop)
		{
			IOmlIndexForLoop loop = (IOmlIndexForLoop)statement;
			LexNameToken var = idToName(loop.getIdentifier(), loop);
			Expression from = convertExpression(loop.getInitExpression());
			Expression to = convertExpression(loop.getLimitExpression());
			Expression by = null;

			if (loop.hasByExpression())
			{
				by = convertExpression(loop.getByExpression());
			}

			Statement body = convertStatement(loop.getStatement());
			stmt = new ForIndexStatement(location, var, from, to, by, body);
		}
		else if (statement instanceof IOmlWhileLoop)
		{
			IOmlWhileLoop loop = (IOmlWhileLoop)statement;
			Expression exp = convertExpression(loop.getExpression());
			Statement body = convertStatement(loop.getStatement());
			stmt = new WhileStatement(location, exp, body);
		}
		else if (statement instanceof IOmlNondeterministicStatement)
		{
			IOmlNondeterministicStatement nds = (IOmlNondeterministicStatement)statement;
			SimpleBlockStatement block = new NonDeterministicStatement(location);
			List<IOmlStatement> slist = nds.getStatementList();

			for (IOmlStatement s: slist)
			{
				block.add(convertStatement(s));
			}

			stmt = block;
		}
		else if (statement instanceof IOmlCallStatement)
		{
			IOmlCallStatement cs = (IOmlCallStatement)statement;

			if (cs.hasObjectDesignator())
			{
				ObjectDesignator designator = convertObjectDesignator(cs.getObjectDesignator());
				IOmlName name = cs.getName();
				String cname = name.hasClassIdentifier() ? name.getClassIdentifier() : null;
				String fname = cs.getName().getIdentifier();
				ExpressionList args = convertExpressionList(cs.getExpressionList());
				stmt = new CallObjectStatement(designator, cname, fname, args);
			}
			else
			{
				LexNameToken name = convertName(cs.getName());
				ExpressionList args = convertExpressionList(cs.getExpressionList());
				stmt = new CallStatement(name, args);
			}
		}
		else if (statement instanceof IOmlSpecificationStatement)
		{
			IOmlSpecificationStatement ss = (IOmlSpecificationStatement)statement;

			stmt = new SpecificationStatement(
				getLocation(ss),
				ss.hasExternals() ?	convertExternals(ss.getExternals()) : null,
				ss.hasPreExpression() ?	convertExpression(ss.getPreExpression()) : null,
				convertExpression(ss.getPostExpression()),
				ss.hasExceptions() ? convertErrors(ss.getExceptions()) : null);
		}
		else if (statement instanceof IOmlStartStatement)
		{
			IOmlStartStatement ss = (IOmlStartStatement)statement;
			Expression arg = convertExpression(ss.getExpression());
			stmt = new StartStatement(location, arg);
		}
		else if (statement instanceof IOmlAlwaysStatement)
		{
			IOmlAlwaysStatement as = (IOmlAlwaysStatement)statement;
			Statement always = convertStatement(as.getAlwaysPart());
			Statement body = convertStatement(as.getInPart());
			stmt = new AlwaysStatement(location, always, body);
		}
		else if (statement instanceof IOmlTrapStatement)
		{
			IOmlTrapStatement ts = (IOmlTrapStatement)statement;
			PatternBind pb = convertPatternBind(ts.getPatternBind());
			Statement with = convertStatement(ts.getWithPart());
			Statement body = convertStatement(ts.getInPart());
			stmt = new TrapStatement(location, pb, with, body);
		}
		else if (statement instanceof IOmlRecursiveTrapStatement)
		{
			IOmlRecursiveTrapStatement rts = (IOmlRecursiveTrapStatement)statement;
			List<IOmlTrapDefinition> traps = rts.getTrapList();
			List<TixeStmtAlternative> tlist = convertTrapDefinitions(traps);
			Statement body = convertStatement(rts.getInPart());
			stmt = new TixeStatement(location, tlist, body);
		}
		else if (statement instanceof IOmlExitStatement)
		{
			IOmlExitStatement es = (IOmlExitStatement)statement;

			if (es.hasExpression())
			{
				Expression e = convertExpression(es.getExpression());
				stmt = new ExitStatement(location, e);
			}
			else
			{
				stmt = new ExitStatement(location);
			}
		}
		else if (statement instanceof IOmlErrorStatement)
		{
			stmt = new ErrorStatement(location);
		}
		else if (statement instanceof IOmlSkipStatement)
		{
			stmt = new SkipStatement(location);
		}
		else if (statement instanceof IOmlDclStatement)
		{
			// These should only appear inside BlockStatements (above).
			throw new InternalException(27, "Bare Dcl statement encountered");
		}
		else if (statement instanceof IOmlDurationStatement)
		{
			IOmlDurationStatement ds = (IOmlDurationStatement)statement;
			List<IOmlExpression> durations = ds.getDurationExpression();
			stmt = new DurationStatement(location,
            	convertExpression(durations.get(0)),
            	convertStatement(ds.getStatement()));
		}
		else if (statement instanceof IOmlCyclesStatement)
		{
			IOmlCyclesStatement cs = (IOmlCyclesStatement)statement;
			List<IOmlExpression> cycles = cs.getCyclesExpression();
			stmt = new CyclesStatement(location,
            	convertExpression(cycles.get(0)),
            	convertStatement(cs.getStatement()));
		}
		else
		{
			throw new InternalException(30, "Statement type unsupported: " + statement);
		}

		return stmt;
	}

	private List<TixeStmtAlternative> convertTrapDefinitions(
		List<IOmlTrapDefinition> traps) throws CGException
	{
		List<TixeStmtAlternative> result = new Vector<TixeStmtAlternative>();

		for (IOmlTrapDefinition d: traps)
		{
			PatternBind pb = convertPatternBind(d.getPatternBind());
			Statement s = convertStatement(d.getStatement());
			result.add(new TixeStmtAlternative(pb, s));
		}

		return result;
	}

	private ObjectDesignator convertObjectDesignator(IOmlObjectDesignator des)
		throws CGException
	{
		LexLocation location = getLocation(des);

		if (des instanceof IOmlObjectDesignatorExpression)
		{
			IOmlObjectDesignatorExpression d = (IOmlObjectDesignatorExpression)des;
			Expression exp = convertExpression(d.getExpression());

			if (exp instanceof VariableExpression)
			{
				VariableExpression ve = (VariableExpression)exp;

				// If the designator is a bare identifier, we always say that the
				// name is explicit, because variable updates are not overridden
				// by subclass overrides.

				ve.setExplicit(true);
				return new ObjectIdentifierDesignator(ve.name);
			}
			else if (exp instanceof SelfExpression)
			{
				return new ObjectSelfDesignator(location);
			}
			else if (exp instanceof NewExpression)
			{
				NewExpression ne = (NewExpression)exp;
				return new ObjectNewDesignator(ne.classname, ne.args);
			}
			else
			{
				throw new InternalException(31, "Expected object state designator type");
			}
		}
		else if (des instanceof IOmlObjectFieldReference)
		{
			IOmlObjectFieldReference d = (IOmlObjectFieldReference)des;
			ObjectDesignator od = convertObjectDesignator(d.getObjectDesignator());
			IOmlName name = d.getName();
			String cname = name.hasClassIdentifier() ? name.getClassIdentifier() : null;
			String fname = d.getName().getIdentifier();
			return new ObjectFieldDesignator(od, cname, fname);
		}
		else if (des instanceof IOmlObjectApply)
		{
			IOmlObjectApply d = (IOmlObjectApply)des;
			ObjectDesignator od = convertObjectDesignator(d.getObjectDesignator());
			ExpressionList args = convertExpressionList(d.getExpressionList());
			return new ObjectApplyDesignator(od, args);
		}
		else
		{
			throw new InternalException(32, "Expected object state designator type");
		}
	}

	private StateDesignator convertDesignator(IOmlStateDesignator sd)
		throws CGException
	{
		if (sd instanceof IOmlStateDesignatorName)
		{
			IOmlStateDesignatorName des = (IOmlStateDesignatorName)sd;
			LexNameToken name = convertName(des.getName());
			return new IdentifierDesignator(name);
		}
		else if (sd instanceof IOmlFieldReference)
		{
			IOmlFieldReference des = (IOmlFieldReference)sd;
			StateDesignator record = convertDesignator(des.getStateDesignator());
			LexIdentifierToken field = getId(des.getIdentifier(), des);
			return new FieldDesignator(record, field);
		}
		else if (sd instanceof IOmlMapOrSequenceReference)
		{
			IOmlMapOrSequenceReference des = (IOmlMapOrSequenceReference)sd;
			StateDesignator record = convertDesignator(des.getStateDesignator());
			Expression index = convertExpression(des.getExpression());
			return new MapSeqDesignator(record, index);
		}
		else
		{
			throw new InternalException(33, "Expected state designator type");
		}
	}

	private Expression convertExpression(IOmlExpression expression) throws CGException
	{
		LexLocation location = getLocation(expression);
		Expression exp = null;

		if (expression instanceof IOmlName)
		{
			IOmlName name = (IOmlName)expression;
			LexNameToken n = convertName(name);
			exp = new VariableExpression(n, true);
		}
		else if (expression instanceof IOmlSymbolicLiteralExpression)
		{
			IOmlSymbolicLiteralExpression sym = (IOmlSymbolicLiteralExpression)expression;
			exp = convertLiteral(sym.getLiteral(), location);
		}
		else if (expression instanceof IOmlBinaryExpression)
		{
			IOmlBinaryExpression bexp = (IOmlBinaryExpression)expression;
			exp = convertBinaryExpression(bexp);
		}
		else if (expression instanceof IOmlUnaryExpression)
		{
			IOmlUnaryExpression uexp = (IOmlUnaryExpression)expression;
			exp = convertUnaryExpression(uexp);
		}
		else if (expression instanceof IOmlTupleConstructor)
		{
			IOmlTupleConstructor tuple = (IOmlTupleConstructor)expression;
			List<IOmlExpression> list = tuple.getExpressionList();
			exp = new TupleExpression(location, convertExpressionList(list));
		}
		else if (expression instanceof IOmlRecordConstructor)
		{
			IOmlRecordConstructor rc = (IOmlRecordConstructor)expression;
			LexNameToken name = convertName(rc.getName());
			List<IOmlExpression> list = rc.getExpressionList();
			exp = new MkTypeExpression(name, convertExpressionList(list));
		}
		else if (expression instanceof IOmlBracketedExpression)
		{
			IOmlBracketedExpression be = (IOmlBracketedExpression)expression;
			exp = convertExpression(be.getExpression());
		}
		else if (expression instanceof IOmlLetExpression)
		{
			IOmlLetExpression le = (IOmlLetExpression)expression;
			DefinitionList defs = convertValueShapeList(le.getDefinitionList());
			Expression body = convertExpression(le.getExpression());
			exp = new LetDefExpression(location, defs, body);
		}
		else if (expression instanceof IOmlDefExpression)
		{
			IOmlDefExpression de = (IOmlDefExpression)expression;
			List<IOmlPatternBindExpression> deflist = de.getPatternBindList();
			DefinitionList defs = convertDefExpressionList(deflist);
			Expression body = convertExpression(de.getExpression());
			exp = new DefExpression(location, defs, body);
		}
		else if (expression instanceof IOmlLetBeExpression)
		{
			IOmlLetBeExpression lbe = (IOmlLetBeExpression)expression;
			MultipleBind bind = convertMultiBind(lbe.getBind());
			Expression best = null;

			if (lbe.hasBest())
			{
				best = convertExpression(lbe.getBest());
			}

			Expression body = convertExpression(lbe.getExpression());
			exp = new LetBeStExpression(location, bind, best, body);
		}
		else if (expression instanceof IOmlSetEnumeration)
		{
			IOmlSetEnumeration set = (IOmlSetEnumeration)expression;
			ExpressionList members = convertExpressionList(set.getExpressionList());
			exp = new SetEnumExpression(location, members);
		}
		else if (expression instanceof IOmlSetComprehension)
		{
			IOmlSetComprehension comp = (IOmlSetComprehension)expression;
			Expression first = convertExpression(comp.getExpression());
			List<IOmlBind> binds = comp.getBindList();
			List<MultipleBind> mbinds = convertMultiBind(binds);
			Expression predicate = null;

			if (comp.hasGuard())
			{
				predicate = convertExpression(comp.getGuard());
			}

			exp = new SetCompExpression(location, first, mbinds, predicate);
		}
		else if (expression instanceof IOmlSetRangeExpression)
		{
			IOmlSetRangeExpression sre = (IOmlSetRangeExpression)expression;
			Expression from = convertExpression(sre.getLower());
			Expression to = convertExpression(sre.getUpper());
			exp = new SetRangeExpression(location, from, to);
		}
		else if (expression instanceof IOmlSequenceEnumeration)
		{
			IOmlSequenceEnumeration seq = (IOmlSequenceEnumeration)expression;
			ExpressionList members = convertExpressionList(seq.getExpressionList());
			exp = new SeqEnumExpression(location, members);
		}
		else if (expression instanceof IOmlSequenceComprehension)
		{
			IOmlSequenceComprehension comp = (IOmlSequenceComprehension)expression;
			Expression first = convertExpression(comp.getExpression());
			SetBind bind = convertSetBind(comp.getSetBind());
			Expression predicate = null;

			if (comp.hasGuard())
			{
				predicate = convertExpression(comp.getGuard());
			}

			exp = new SeqCompExpression(location, first, bind, predicate);
		}
		else if (expression instanceof IOmlSubsequenceExpression)
		{
			IOmlSubsequenceExpression sse = (IOmlSubsequenceExpression)expression;
			Expression seq = convertExpression(sse.getExpression());
			Expression from = convertExpression(sse.getLower());
			Expression to = convertExpression(sse.getUpper());
			exp = new SubseqExpression(seq, from, to);
		}
		else if (expression instanceof IOmlMapEnumeration)
		{
			IOmlMapEnumeration map = (IOmlMapEnumeration)expression;
			List<MapletExpression> members = convertMapletList(map.getMapletList());
			exp = new MapEnumExpression(location, members);
		}
		else if (expression instanceof IOmlMapComprehension)
		{
			IOmlMapComprehension comp = (IOmlMapComprehension)expression;
			MapletExpression first = convertMaplet(comp.getExpression());
			List<MultipleBind> bind = convertMultiBind(comp.getBindList());
			Expression predicate = null;

			if (comp.hasGuard())
			{
				predicate = convertExpression(comp.getGuard());
			}

			exp = new MapCompExpression(location, first, bind, predicate);

		}
		else if (expression instanceof IOmlIfExpression)
		{
			IOmlIfExpression ifex = (IOmlIfExpression)expression;
			Expression ie = convertExpression(ifex.getIfExpression());
			Expression te = convertExpression(ifex.getThenExpression());
			List<IOmlElseIfExpression> elseifs = ifex.getElseifExpressionList();
			List<ElseIfExpression> el = convertElseifExpList(elseifs);
			Expression ee = convertExpression(ifex.getElseExpression());
			exp = new IfExpression(location, ie, te, el, ee);
		}
		else if (expression instanceof IOmlCasesExpression)
		{
			IOmlCasesExpression cexp = (IOmlCasesExpression)expression;
			Expression test = convertExpression(cexp.getMatchExpression());
			List<IOmlCasesExpressionAlternative> alts = cexp.getAlternativeList();
			List<CaseAlternative> list = convertAlternativeExpList(test, alts);
			Expression others = null;

			if (cexp.hasOthersExpression())
			{
				others = convertExpression(cexp.getOthersExpression());
			}

			exp = new CasesExpression(location, test, list, others);
		}
		else if (expression instanceof IOmlForAllExpression)
		{
			IOmlForAllExpression fe = (IOmlForAllExpression)expression;
			List<MultipleBind> bindList = convertMultiBind(fe.getBindList());
			Expression predicate = convertExpression(fe.getExpression());
			exp = new ForAllExpression(location, bindList, predicate);
		}
		else if (expression instanceof IOmlExistsExpression)
		{
			IOmlExistsExpression ex = (IOmlExistsExpression)expression;
			List<MultipleBind> bindList = convertMultiBind(ex.getBindList());
			Expression predicate = convertExpression(ex.getExpression());
			exp = new ExistsExpression(location, bindList, predicate);
		}
		else if (expression instanceof IOmlExistsUniqueExpression)
		{
			IOmlExistsUniqueExpression ex = (IOmlExistsUniqueExpression)expression;
			Bind bind = convertBind(ex.getBind());
			Expression predicate = convertExpression(ex.getExpression());
			exp = new Exists1Expression(location, bind, predicate);
		}
		else if (expression instanceof IOmlIotaExpression)
		{
			IOmlIotaExpression ex = (IOmlIotaExpression)expression;
			Bind bind = convertBind(ex.getBind());
			Expression predicate = convertExpression(ex.getExpression());
			exp = new IotaExpression(location, bind, predicate);
		}
		else if (expression instanceof IOmlTokenExpression)
		{
			IOmlTokenExpression te = (IOmlTokenExpression)expression;
			Expression arg = convertExpression(te.getExpression());
			exp = new MkBasicExpression(new TokenType(location), arg);
		}
		else if (expression instanceof IOmlMuExpression)
		{
			IOmlMuExpression mu = (IOmlMuExpression)expression;
			Expression record = convertExpression(mu.getExpression());
			List<IOmlRecordModifier> mlist = mu.getModifierList();
			List<RecordModifier> modifiers = convertModifierList(mlist);
			exp = new MuExpression(location, record, modifiers);
		}
		else if (expression instanceof IOmlApplyExpression)
		{
			IOmlApplyExpression ae = (IOmlApplyExpression)expression;
			IOmlExpression re = ae.getExpression();
			Expression root = convertExpression(re);

			// All IOmlNames are set to be explicit, because member names
			// accessed as simple variables are always explicit (they never
			// refer to overrides). But with an apply expression, the root
			// could refer to a sub/superclass field if not explicit.

			if (root instanceof VariableExpression && re instanceof IOmlName)
			{
				IOmlName var = (IOmlName)re;

				if (!var.hasClassIdentifier())
				{
					VariableExpression ve = (VariableExpression)root;
					ve.setExplicit(false);
				}
			}

			ExpressionList args = convertExpressionList(ae.getExpressionList());

			if (args.isEmpty())
			{
				exp = new ApplyExpression(root);
			}
			else
			{
				exp = new ApplyExpression(root, args);
			}
		}
		else if (expression instanceof IOmlFieldSelect)
		{
			IOmlFieldSelect fs = (IOmlFieldSelect)expression;
			IOmlExpression re = fs.getExpression();
			Expression root = convertExpression(re);

			// All IOmlNames are set to be explicit, because member names
			// accessed as simple variables are always explicit (they never
			// refer to overrides). But with an field select expression, the root
			// could refer to a sub/superclass field if not explicit.

			if (root instanceof VariableExpression && re instanceof IOmlName)
			{
				IOmlName var = (IOmlName)re;

				if (!var.hasClassIdentifier())
				{
					VariableExpression ve = (VariableExpression)root;
					ve.setExplicit(false);
				}
			}

			IOmlName name = fs.getName();

			if (name.hasClassIdentifier())
			{
				exp = new FieldExpression(root, convertName(name));
			}
			else
			{
				exp = new FieldExpression(root, getId(name.getIdentifier(), name));
			}

		}
		else if (expression instanceof IOmlFunctionTypeSelect)
		{
			IOmlFunctionTypeSelect fts = (IOmlFunctionTypeSelect)expression;
			Expression root = convertExpression(fts.getExpression());
			IOmlFunctionTypeInstantiation inst = fts.getFunctionTypeInstantiation();

			IOmlName name = inst.getName();

			if (name.hasClassIdentifier())
			{
				exp = new FieldExpression(root, convertName(name));
			}
			else
			{
				exp = new FieldExpression(root, getId(name.getIdentifier(), name));
			}

			TypeList targs = convertTypeList(inst.getTypeList());
			exp = new FuncInstantiationExpression(exp, targs);
		}
		else if (expression instanceof IOmlFunctionTypeInstantiation)
		{
			IOmlFunctionTypeInstantiation inst = (IOmlFunctionTypeInstantiation)expression;
			VariableExpression name = new VariableExpression(convertName(inst.getName()));
			TypeList targs = convertTypeList(inst.getTypeList());
			exp = new FuncInstantiationExpression(name, targs);
		}
		else if (expression instanceof IOmlLambdaExpression)
		{
			IOmlLambdaExpression lam = (IOmlLambdaExpression)expression;
			List<IOmlTypeBind> bindList = lam.getTypeBindList();
			List<TypeBind> tbl = convertTypeBindList(bindList);
			Expression body = convertExpression(lam.getExpression());
			exp = new LambdaExpression(location, tbl, body);
		}
		else if (expression instanceof IOmlNewExpression)
		{
			IOmlNewExpression ne = (IOmlNewExpression)expression;
			LexIdentifierToken id = getId(ne.getName().getIdentifier(), ne);
			ExpressionList args = convertExpressionList(ne.getExpressionList());
			exp = new NewExpression(location, id, args);
		}
		else if (expression instanceof IOmlSelfExpression)
		{
			exp = new SelfExpression(location);
		}
		else if (expression instanceof IOmlThreadIdExpression)
		{
			exp = new ThreadIdExpression(location);
		}
		else if (expression instanceof IOmlTimeExpression)
		{
			exp = new TimeExpression(location);
		}
		else if (expression instanceof IOmlIsExpression)
		{
			IOmlIsExpression is = (IOmlIsExpression)expression;
			Type type = convertType(is.getType());
			Expression e = convertExpression(is.getExpression());

			if (type instanceof NamedType)
			{
				NamedType nt = (NamedType)type;
				exp = new IsExpression(nt.typename, e);
			}
			else
			{
				exp = new IsExpression(type, e);
			}
		}
		else if (expression instanceof IOmlUndefinedExpression)
		{
			exp = new UndefinedExpression(location);
		}
		else if (expression instanceof IOmlPreconditionExpression)
		{
			IOmlPreconditionExpression pre = (IOmlPreconditionExpression)expression;
			ExpressionList all = convertExpressionList(pre.getExpressionList());
			Expression func = all.get(0);
			ExpressionList args = new ExpressionList();
			args.addAll(1, all);
			exp = new PreExpression(location, func, args);
		}
		else if (expression instanceof IOmlIsofbaseclassExpression)
		{
			IOmlIsofbaseclassExpression iobc = (IOmlIsofbaseclassExpression)expression;
			LexNameToken name = convertName(iobc.getName());
			Expression e = convertExpression(iobc.getExpression());
			exp = new IsOfBaseClassExpression(location, name, e);
		}
		else if (expression instanceof IOmlIsofclassExpression)
		{
			IOmlIsofclassExpression ioc = (IOmlIsofclassExpression)expression;
			LexNameToken name = convertName(ioc.getName());
			Expression e = convertExpression(ioc.getExpression());
			exp = new IsOfClassExpression(location, name, e);
		}
		else if (expression instanceof IOmlSamebaseclassExpression)
		{
			IOmlSamebaseclassExpression iosbc = (IOmlSamebaseclassExpression)expression;
			Expression e1 = convertExpression(iosbc.getLhsExpression());
			Expression e2 = convertExpression(iosbc.getRhsExpression());
			ExpressionList args = new ExpressionList();
			args.add(e1);
			args.add(e2);
			exp = new SameBaseClassExpression(location, args);
		}
		else if (expression instanceof IOmlSameclassExpression)
		{
			IOmlSameclassExpression iosc = (IOmlSameclassExpression)expression;
			Expression e1 = convertExpression(iosc.getLhsExpression());
			Expression e2 = convertExpression(iosc.getRhsExpression());
			ExpressionList args = new ExpressionList();
			args.add(e1);
			args.add(e2);
			exp = new SameClassExpression(location, args);

		}
		else if (expression instanceof IOmlReqExpression)
		{
			exp = new HistoryExpression(location, Token.REQ, new LexNameList());
		}
		else if (expression instanceof IOmlActExpression)
		{
			IOmlActExpression act = (IOmlActExpression)expression;
			LexNameList names = convertNameList(act.getNameList());
			exp = new HistoryExpression(location, Token.ACT, names);
		}
		else if (expression instanceof IOmlFinExpression)
		{
			IOmlFinExpression fin = (IOmlFinExpression)expression;
			LexNameList names = convertNameList(fin.getNameList());
			exp = new HistoryExpression(location, Token.FIN, names);
		}
		else if (expression instanceof IOmlActiveExpression)
		{
			IOmlActiveExpression active = (IOmlActiveExpression)expression;
			LexNameList names = convertNameList(active.getNameList());
			exp = new HistoryExpression(location, Token.ACTIVE, names);
		}
		else if (expression instanceof IOmlWaitingExpression)
		{
			IOmlWaitingExpression waiting = (IOmlWaitingExpression)expression;
			LexNameList names = convertNameList(waiting.getNameList());
			exp = new HistoryExpression(location, Token.WAITING, names);
		}
		else if (expression instanceof IOmlOldName)
		{
			IOmlOldName old = (IOmlOldName)expression;
			LexNameToken oldname = new LexNameToken(
						classname, getOldId(old.getIdentifier(), old));
			exp = new VariableExpression(oldname);
		}
		else
		{
			throw new InternalException(35, "Expression type unsupported: " + expression);
		}

		return exp;
	}

	private List<RecordModifier> convertModifierList(List<IOmlRecordModifier> mlist)
		throws CGException
	{
		List<RecordModifier> result = new Vector<RecordModifier>();

		for (IOmlRecordModifier m: mlist)
		{
			LexIdentifierToken tag = getId(m.getIdentifier(), m);
			Expression value = convertExpression(m.getExpression());
			result.add(new RecordModifier(tag, value));
		}

		return result;
	}

	private List<CaseStmtAlternative> convertAlternativeStmtList(
		List<IOmlCasesStatementAlternative> alts) throws CGException
	{
		List<CaseStmtAlternative> list = new Vector<CaseStmtAlternative>();

		for (IOmlCasesStatementAlternative alt: alts)
		{
			PatternList plist = convertPatternList(alt.getPatternList());
			Statement result = convertStatement(alt.getStatement());

			for (Pattern p: plist)
			{
				list.add(new CaseStmtAlternative(p, result));
			}
		}

		return list;
	}

	private List<CaseAlternative> convertAlternativeExpList(Expression cexp,
		List<IOmlCasesExpressionAlternative> alts) throws CGException
	{
		List<CaseAlternative> list = new Vector<CaseAlternative>();

		for (IOmlCasesExpressionAlternative alt: alts)
		{
			PatternList plist = convertPatternList(alt.getPatternList());
			Expression result = convertExpression(alt.getExpression());

			for (Pattern p: plist)
			{
				list.add(new CaseAlternative(cexp, p, result));
			}
		}

		return list;
	}

	private List<ElseIfStatement> convertElseifStmtList(
		List<IOmlElseIfStatement> elseifs) throws CGException
	{
		List<ElseIfStatement> result = new Vector<ElseIfStatement>();

		for (IOmlElseIfStatement e: elseifs)
		{
			Expression ie = convertExpression(e.getExpression());
			Statement ts = convertStatement(e.getStatement());
			result.add(new ElseIfStatement(getLocation(e), ie, ts));
		}

		return result;
	}

	private List<ElseIfExpression> convertElseifExpList(List<IOmlElseIfExpression> list)
		throws CGException
	{
		List<ElseIfExpression> result = new Vector<ElseIfExpression>();

		for (IOmlElseIfExpression e: list)
		{
			Expression ie = convertExpression(e.getElseifExpression());
			Expression te = convertExpression(e.getThenExpression());
			result.add(new ElseIfExpression(getLocation(e), ie, te));
		}

		return result;
	}

	private List<MapletExpression> convertMapletList(List<IOmlMaplet> mapletList)
		throws CGException
	{
		List<MapletExpression> result = new Vector<MapletExpression>();

		for (IOmlMaplet m: mapletList)
		{
			result.add(convertMaplet(m));
		}

		return result;
	}

	private MapletExpression convertMaplet(IOmlMaplet m)
		throws CGException
	{
		LexToken op = new LexKeywordToken(Token.MAPLET, getLocation(m));
		Expression dom = convertExpression(m.getDomExpression());
		Expression rng = convertExpression(m.getRngExpression());
		return new MapletExpression(dom, op, rng);
	}

	private DefinitionList convertDclStatementList(List<IOmlDclStatement> dcls)
		throws CGException
	{
		DefinitionList defs = new DefinitionList();

		for (IOmlDclStatement dcl: dcls)
		{
			List<IOmlAssignmentDefinition> assdefs = dcl.getDefinitionList();

			for (IOmlAssignmentDefinition assdef: assdefs)
			{
				LexNameToken name = idToName(assdef.getIdentifier(), assdef);
				Type type = convertType(assdef.getType());
				Expression e = null;

				if (assdef.hasExpression())
				{
					e = convertExpression(assdef.getExpression());
				}
				else
				{
					e = new UndefinedExpression(name.location);
				}

				defs.add(new AssignmentDefinition(name, type, e));
			}
		}

		return defs;
	}

	private DefinitionList convertEqualsDefinitionList(
		List<IOmlEqualsDefinition> eqdefs) throws CGException
	{
		DefinitionList defs = new DefinitionList();

		for (IOmlEqualsDefinition pbe: eqdefs)
		{
			IOmlPatternBind pb = pbe.getPatternBind();
			Expression e = convertExpression(pbe.getExpression());

			if (pb instanceof IOmlPattern)
			{
				Pattern p = convertPattern((IOmlPattern)pb);
				defs.add(new EqualsDefinition(getLocation(pb), p, e));
			}
			else if (pb instanceof IOmlBind)
			{
				MultipleBind mb = convertMultiBind((IOmlBind)pb);
				defs.add(new EqualsDefinition(getLocation(pb), mb.plist.get(0), e));
			}
			else
			{
				throw new InternalException(36, "Unexpected pattern/bind type");
			}
		}

		return defs;
	}

	private DefinitionList convertDefExpressionList(
		List<IOmlPatternBindExpression> pbelist) throws CGException
	{
		DefinitionList defs = new DefinitionList();

		for (IOmlPatternBindExpression pbe: pbelist)
		{
			IOmlPatternBind pb = pbe.getPatternBind();
			Expression e = convertExpression(pbe.getExpression());

			if (pb instanceof IOmlPattern)
			{
				Pattern p = convertPattern((IOmlPattern)pb);
				defs.add(new EqualsDefinition(getLocation(pb), p, e));
			}
			else if (pb instanceof IOmlBind)
			{
				MultipleBind mb = convertMultiBind((IOmlBind)pb);
				defs.add(new EqualsDefinition(getLocation(pb), mb.plist.get(0), e));
			}
			else
			{
				throw new InternalException(37, "Unexpected pattern/bind type");
			}
		}

		return defs;
	}

	private PatternBind convertPatternBind(IOmlPatternBind pb) throws CGException
	{
		if (pb instanceof IOmlPattern)
		{
			Pattern p = convertPattern((IOmlPattern)pb);
			return new PatternBind(getLocation(pb), p);
		}
		else if (pb instanceof IOmlBind)
		{
			Bind b = convertBind((IOmlBind)pb);
			return new PatternBind(getLocation(pb), b);
		}
		else
		{
			throw new InternalException(38, "Unexpected pattern/bind type");
		}
	}

	private List<MultipleBind> convertMultiBind(List<IOmlBind> binds) throws CGException
	{
		List<MultipleBind> result = new Vector<MultipleBind>();

		for (IOmlBind b: binds)
		{
			result.add(convertMultiBind(b));
		}

		return result;
	}

	private MultipleBind convertMultiBind(IOmlBind stbind) throws CGException
	{
		MultipleBind multibind = null;

		if (stbind instanceof IOmlSetBind)
		{
			IOmlSetBind sb = (IOmlSetBind)stbind;
			PatternList p = convertPatternList(sb.getPattern());
			Expression e = convertExpression(sb.getExpression());
			multibind = new MultipleSetBind(p, e);
		}
		else if (stbind instanceof IOmlTypeBind)
		{
			IOmlTypeBind tb = (IOmlTypeBind)stbind;
			PatternList p = convertPatternList(tb.getPattern());
			Type t = convertType(tb.getType());
			multibind = new MultipleTypeBind(p, t);
		}
		else
		{
			throw new InternalException(39, "Unexpected bind type");
		}

		return multibind;
	}

	private Bind convertBind(IOmlBind stbind) throws CGException
	{
		Bind multibind = null;

		if (stbind instanceof IOmlSetBind)
		{
			IOmlSetBind sb = (IOmlSetBind)stbind;
			PatternList p = convertPatternList(sb.getPattern());
			Expression e = convertExpression(sb.getExpression());
			multibind = new SetBind(p.get(0), e);
		}
		else if (stbind instanceof IOmlTypeBind)
		{
			IOmlTypeBind tb = (IOmlTypeBind)stbind;
			PatternList p = convertPatternList(tb.getPattern());
			Type t = convertType(tb.getType());
			multibind = new TypeBind(p.get(0), t);
		}
		else
		{
			throw new InternalException(40, "Unexpected bind type");
		}

		return multibind;
	}

	private List<TypeBind> convertTypeBindList(List<IOmlTypeBind> bindList)
		throws CGException
	{
		List<TypeBind> result = new Vector<TypeBind>();

		for (IOmlTypeBind tb: bindList)
		{
			result.add(convertTypeBind(tb));
		}

		return result;
	}

	private SetBind convertSetBind(IOmlBind stbind) throws CGException
	{
		if (stbind instanceof IOmlSetBind)
		{
			IOmlSetBind sb = (IOmlSetBind)stbind;
			PatternList p = convertPatternList(sb.getPattern());
			Expression e = convertExpression(sb.getExpression());
			return new SetBind(p.get(0), e);
		}
		else
		{
			throw new InternalException(41, "Expected set bind type");
		}
	}

	private TypeBind convertTypeBind(IOmlBind tbind) throws CGException
	{
		if (tbind instanceof IOmlTypeBind)
		{
			IOmlTypeBind tb = (IOmlTypeBind)tbind;
			PatternList p = convertPatternList(tb.getPattern());
			Type t = convertType(tb.getType());
			return new TypeBind(p.get(0), t);
		}
		else
		{
			throw new InternalException(42, "Expected set bind type");
		}
	}

	private DefinitionList convertValueShapeList(List<IOmlValueShape> values)
		throws CGException
	{
		DefinitionList defs = new DefinitionList();

		for (IOmlValueShape shape: values)
		{
			defs.add(convertValueShape(shape));
		}

		return defs;
	}

	private Definition convertValueShape(IOmlValueShape shape)
		throws CGException
	{
		Pattern p = convertPattern(shape.getPattern());
		Expression e = convertExpression(shape.getExpression());
		Type t = null;

		if (shape.hasType())
		{
			t = convertType(shape.getType());
		}

		return new ValueDefinition(p, NameScope.GLOBAL, t, e);
	}

	private ExpressionList convertExpressionList(List<IOmlExpression> list)
		throws CGException
	{
		ExpressionList exlist = new ExpressionList();

		for (IOmlExpression e: list)
		{
			exlist.add(convertExpression(e));
		}

		return exlist;
	}

	private Expression convertUnaryExpression(IOmlUnaryExpression uexp)
		throws CGException
	{
		Expression exp = convertExpression(uexp.getExpression());
		Expression result = null;

		IOmlUnaryOperator op = uexp.getOperator();
		LexLocation location = getLocation(uexp);
		long value = op.getValue();

		if (value == IOmlUnaryOperatorQuotes.IQABS)
		{
			result = new AbsoluteExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQPLUS)
		{
			result = new UnaryPlusExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQMINUS)
		{
			result = new UnaryMinusExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQFLOOR)
		{
			result = new FloorExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQNOT)
		{
			result = new NotExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQCARD)
		{
			result = new CardinalityExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQPOWER)
		{
			result = new PowerSetExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQDUNION)
		{
			result = new DistUnionExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQDINTER)
		{
			result = new DistIntersectExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQHD)
		{
			result = new HeadExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQTL)
		{
			result = new TailExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQLEN)
		{
			result = new LenExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQELEMS)
		{
			result = new ElementsExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQINDS)
		{
			result = new IndicesExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQDCONC)
		{
			result = new DistConcatExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQDOM)
		{
			result = new MapDomainExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQRNG)
		{
			result = new MapRangeExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQDMERGE)
		{
			result = new DistMergeExpression(location, exp);
		}
		else if (value == IOmlUnaryOperatorQuotes.IQINVERSE)
		{
			result = new MapInverseExpression(location, exp);
		}
		else
		{
			throw new InternalException(43, "Operator type unsupported: " + op.getStringValue());
		}

		return result;
	}

	private Expression convertBinaryExpression(IOmlBinaryExpression bexp)
		throws CGException
	{
		Expression lhs = convertExpression(bexp.getLhsExpression());
		Expression rhs = convertExpression(bexp.getRhsExpression());
		Expression exp = null;

		IOmlBinaryOperator op = bexp.getOperator();
		LexLocation location = getLocation(bexp);
		long value = op.getValue();

		if (value == IOmlBinaryOperatorQuotes.IQPLUS)
		{
			LexKeywordToken token = new LexKeywordToken(Token.PLUS, location);
			exp = new PlusExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQMINUS)
		{
			LexKeywordToken token = new LexKeywordToken(Token.MINUS, location);
			exp = new SubtractExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQMULTIPLY)
		{
			LexKeywordToken token = new LexKeywordToken(Token.TIMES, location);
			exp = new TimesExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQDIVIDE)
		{
			LexKeywordToken token = new LexKeywordToken(Token.DIVIDE, location);
			exp = new DivideExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQMOD)
		{
			LexKeywordToken token = new LexKeywordToken(Token.MOD, location);
			exp = new ModExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQREM)
		{
			LexKeywordToken token = new LexKeywordToken(Token.REM, location);
			exp = new RemExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQDIV)
		{
			LexKeywordToken token = new LexKeywordToken(Token.DIV, location);
			exp = new DivExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQGT)
		{
			LexKeywordToken token = new LexKeywordToken(Token.GT, location);
			exp = new GreaterExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQLT)
		{
			LexKeywordToken token = new LexKeywordToken(Token.LT, location);
			exp = new LessExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQGE)
		{
			LexKeywordToken token = new LexKeywordToken(Token.GE, location);
			exp = new GreaterEqualExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQLE)
		{
			LexKeywordToken token = new LexKeywordToken(Token.GT, location);
			exp = new LessEqualExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQNE)
		{
			LexKeywordToken token = new LexKeywordToken(Token.NE, location);
			exp = new NotEqualExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQEQ)
		{
			LexKeywordToken token = new LexKeywordToken(Token.EQUALS, location);
			exp = new EqualsExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQAND)
		{
			LexKeywordToken token = new LexKeywordToken(Token.AND, location);
			exp = new AndExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQOR)
		{
			LexKeywordToken token = new LexKeywordToken(Token.OR, location);
			exp = new OrExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQEQUIV)
		{
			LexKeywordToken token = new LexKeywordToken(Token.EQUIVALENT, location);
			exp = new EquivalentExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQIMPLY)
		{
			LexKeywordToken token = new LexKeywordToken(Token.IMPLIES, location);
			exp = new ImpliesExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQPSUBSET)
		{
			LexKeywordToken token = new LexKeywordToken(Token.PSUBSET, location);
			exp = new ProperSubsetExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQSUBSET)
		{
			LexKeywordToken token = new LexKeywordToken(Token.SUBSET, location);
			exp = new SubsetExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQINSET)
		{
			LexKeywordToken token = new LexKeywordToken(Token.INSET, location);
			exp = new InSetExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQNOTINSET)
		{
			LexKeywordToken token = new LexKeywordToken(Token.NOTINSET, location);
			exp = new NotInSetExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQINTER)
		{
			LexKeywordToken token = new LexKeywordToken(Token.INTER, location);
			exp = new SetIntersectExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQUNION)
		{
			LexKeywordToken token = new LexKeywordToken(Token.UNION, location);
			exp = new SetUnionExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQDIFFERENCE)
		{
			LexKeywordToken token = new LexKeywordToken(Token.SETDIFF, location);
			exp = new SetDifferenceExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQMAPDOMRESBY)
		{
			LexKeywordToken token = new LexKeywordToken(Token.DOMRESBY, location);
			exp = new DomainResByExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQMAPDOMRESTO)
		{
			LexKeywordToken token = new LexKeywordToken(Token.DOMRESTO, location);
			exp = new DomainResToExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQMAPRNGRESBY)
		{
			LexKeywordToken token = new LexKeywordToken(Token.RANGERESBY, location);
			exp = new RangeResByExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQMAPRNGRESTO)
		{
			LexKeywordToken token = new LexKeywordToken(Token.RANGERESTO, location);
			exp = new RangeResToExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQMUNION)
		{
			LexKeywordToken token = new LexKeywordToken(Token.MUNION, location);
			exp = new MapUnionExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQCOMP)
		{
			LexKeywordToken token = new LexKeywordToken(Token.COMP, location);
			exp = new CompExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQITERATE)
		{
			LexKeywordToken token = new LexKeywordToken(Token.STARSTAR, location);
			exp = new StarStarExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQCONC)
		{
			LexKeywordToken token = new LexKeywordToken(Token.CONCATENATE, location);
			exp = new SeqConcatExpression(lhs, token, rhs);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQTUPSEL)
		{
			LexIntegerToken lit = null;

			if (rhs instanceof IntegerLiteralExpression)
			{
				IntegerLiteralExpression ile = (IntegerLiteralExpression)rhs;
				lit = ile.value;
			}
			else
			{
				throw new InternalException(44, "Tuple field select is not a number");
			}

			exp = new FieldNumberExpression(lhs, lit);
		}
		else if (value == IOmlBinaryOperatorQuotes.IQMODIFY)
		{
			LexKeywordToken token = new LexKeywordToken(Token.PLUSPLUS, location);
			exp = new PlusPlusExpression(lhs, token, rhs);
		}
		else
		{
			throw new InternalException(45, "Unexpected expression type: " + op.getStringValue());
		}

		return exp;
	}

	private Expression convertLiteral(IOmlLiteral literal, LexLocation location)
		throws CGException
	{
		Expression exp = null;

		if (literal instanceof IOmlNumericLiteral)
		{
			IOmlNumericLiteral nl = (IOmlNumericLiteral)literal;
			long value = nl.getVal().longValue();
			exp = new IntegerLiteralExpression(new LexIntegerToken(value, location));
		}
		else if (literal instanceof IOmlRealLiteral)
		{
			IOmlRealLiteral rl = (IOmlRealLiteral)literal;
			double value = rl.getVal().doubleValue();
			exp = new RealLiteralExpression(new LexRealToken(value, location));
		}
		else if (literal instanceof IOmlBooleanLiteral)
		{
			IOmlBooleanLiteral bl = (IOmlBooleanLiteral)literal;
			boolean value = bl.getVal().booleanValue();
			exp = new BooleanLiteralExpression(new LexBooleanToken(value, location));
		}
		else if (literal instanceof IOmlNilLiteral)
		{
			exp = new NilExpression(location);
		}
		else if (literal instanceof IOmlCharacterLiteral)
		{
			IOmlCharacterLiteral cl = (IOmlCharacterLiteral)literal;
			char value = cl.getVal().charValue();
			exp = new CharLiteralExpression(new LexCharacterToken(value, location));
		}
		else if (literal instanceof IOmlTextLiteral)
		{
			IOmlTextLiteral tl = (IOmlTextLiteral)literal;
			String value = tl.getVal();
			exp = new StringLiteralExpression(new LexStringToken(value, location));
		}
		else if (literal instanceof IOmlQuoteLiteral)
		{
			IOmlQuoteLiteral ql = (IOmlQuoteLiteral)literal;
			String value = ql.getVal();
			exp = new QuoteLiteralExpression(new LexQuoteToken(value, location));
		}
		else
		{
			throw new InternalException(46, "Unexpected literal expression");
		}

		return exp;
	}

	private TypeList convertTypeList(List<IOmlType> typelist) throws CGException
	{
		TypeList result = new TypeList();

		for (IOmlType t: typelist)
		{
			result.add(convertType(t));
		}

		return result;
	}

	private Type convertType(IOmlType type) throws CGException
	{
		LexLocation location = getLocation(type);
		Type tp = null;

		if (type instanceof IOmlNatType)
		{
			tp = new NaturalType(location);
		}
		else if (type instanceof IOmlNat1Type)
		{
			tp = new NaturalOneType(location);
		}
		else if (type instanceof IOmlIntType)
		{
			tp = new IntegerType(location);
		}
		else if (type instanceof IOmlRatType)
		{
			tp = new RationalType(location);
		}
		else if (type instanceof IOmlRealType)
		{
			tp = new RealType(location);
		}
		else if (type instanceof IOmlPartialFunctionType)
		{
			IOmlPartialFunctionType pft = (IOmlPartialFunctionType)type;
			TypeList params = productExpand(convertType(pft.getDomType()));
			Type result = convertType(pft.getRngType());
			tp = new FunctionType(location, true, params, result);
		}
		else if (type instanceof IOmlTotalFunctionType)
		{
			IOmlTotalFunctionType pft = (IOmlTotalFunctionType)type;
			TypeList params = productExpand(convertType(pft.getDomType()));
			Type result = convertType(pft.getRngType());
			tp = new FunctionType(location, false, params, result);
		}
		else if (type instanceof IOmlOperationType)
		{
			IOmlOperationType pft = (IOmlOperationType)type;
			TypeList params = productExpand(convertType(pft.getDomType()));
			Type result = convertType(pft.getRngType());
			tp = new OperationType(location, params, result);
		}
		else if (type instanceof IOmlProductType)
		{
			IOmlProductType ptype = (IOmlProductType)type;
			TypeList all = productExpand(convertType(ptype.getLhsType()));
			all.addAll(productExpand(convertType(ptype.getRhsType())));
			tp = new ProductType(location, all);
		}
		else if (type instanceof IOmlBracketedType)
		{
			IOmlBracketedType bt = (IOmlBracketedType)type;
			tp = new BracketType(location, convertType(bt.getType()));
		}
		else if (type instanceof IOmlBoolType)
		{
			tp = new BooleanType(location);
		}
		else if (type instanceof IOmlCharType)
		{
			tp = new CharacterType(location);
		}
		else if (type instanceof IOmlEmptyType)
		{
			tp = new VoidType(location);
		}
		else if (type instanceof IOmlTokenType)
		{
			tp = new TokenType(location);
		}
		else if (type instanceof IOmlQuoteType)
		{
			IOmlQuoteType qt = (IOmlQuoteType)type;
			IOmlQuoteLiteral ql = qt.getQuoteLiteral();
			tp = new QuoteType(new LexQuoteToken(ql.getVal(), location));
		}
		else if (type instanceof IOmlCompositeType)
		{
			IOmlCompositeType ct = (IOmlCompositeType)type;
			LexNameToken name = idToName(ct.getIdentifier(), ct);
			List<IOmlField> fields = ct.getFieldList();
			tp = new RecordType(name, convertFieldList(fields));
		}
		else if (type instanceof IOmlUnionType)
		{
			IOmlUnionType ut = (IOmlUnionType)type;
			Type lhs = convertType(ut.getLhsType());
			Type rhs = convertType(ut.getRhsType());
			tp = new UnionType(location, lhs, rhs);
		}
		else if (type instanceof IOmlOptionalType)
		{
			IOmlOptionalType ot = (IOmlOptionalType)type;
			tp = new OptionalType(location, convertType(ot.getType()));
		}
		else if (type instanceof IOmlSetType)
		{
			IOmlSetType st = (IOmlSetType)type;
			tp = new SetType(location, convertType(st.getType()));
		}
		else if (type instanceof IOmlSeq0Type)
		{
			IOmlSeq0Type st = (IOmlSeq0Type)type;
			tp = new SeqType(location, convertType(st.getType()));
		}
		else if (type instanceof IOmlSeq1Type)
		{
			IOmlSeq1Type st = (IOmlSeq1Type)type;
			tp = new Seq1Type(location, convertType(st.getType()));
		}
		else if (type instanceof IOmlGeneralMapType)
		{
			IOmlGeneralMapType mt = (IOmlGeneralMapType)type;
			Type from = convertType(mt.getDomType());
			Type to = convertType(mt.getRngType());
			tp = new MapType(location, from, to);
		}
		else if (type instanceof IOmlInjectiveMapType)
		{
			IOmlInjectiveMapType mt = (IOmlInjectiveMapType)type;
			Type from = convertType(mt.getDomType());
			Type to = convertType(mt.getRngType());
			tp = new InMapType(location, from, to);
		}
		else if (type instanceof IOmlTypeName)
		{
			IOmlTypeName nt = (IOmlTypeName)type;
			LexNameToken name = convertName(nt.getName());
			tp = new UnresolvedType(name);
		}
		else if (type instanceof IOmlTypeVariable)
		{
			IOmlTypeVariable tv = (IOmlTypeVariable)type;
			tp = new ParameterType(idToName(tv.getIdentifier(), tv));
		}
		else if (type instanceof IOmlClassTypeInstantiation)
		{
			throw new InternalException(47, "Class instantiation not supported");
		}
		else
		{
			throw new InternalException(48, "Unexpected type expression");
		}

		return tp;
	}

	private LexNameToken convertName(IOmlName name) throws CGException
	{
		LexNameToken lex = null;

		if (name.hasClassIdentifier())
		{
			lex = new LexNameToken(
				name.getClassIdentifier(),
				name.getIdentifier(),
				getLocation(name),
				false, true);
		}
		else
		{
			lex = idToName(name.getIdentifier(), name);
		}

		return lex;
	}

	private LexNameList convertNameList(List<IOmlName> nlist) throws CGException
	{
		LexNameList names = new LexNameList();

		for (IOmlName name: nlist)
		{
			names.add(convertName(name));
		}

		return names;
	}

	private List<Field> convertFieldList(List<IOmlField> fields) throws CGException
	{
		List<Field> list = new Vector<Field>();

		for (IOmlField f: fields)
		{
			LexNameToken tagname = null;
			String tag = null;

			if (f.hasIdentifier())
			{
				tag = f.getIdentifier();
				tagname = idToName(tag, f);
			}
			else
			{
    			tag = Integer.toString(list.size() + 1);
    			tagname = new LexNameToken(classname, tag, getLocation(f));
			}

			Type type = convertType(f.getType());
			list.add(new Field(tagname, tag, type, f.getIgnore()));
		}

		return list;
	}

	private TypeList productExpand(Type parameters)
	{
		TypeList types = new TypeList();

		if (parameters instanceof ProductType)
		{
			// Expand unbracketed product types
			ProductType pt = (ProductType)parameters;

			for (Type t: pt.types)
			{
				types.addAll(productExpand(t));
			}
		}
		else if (parameters instanceof VoidType)
		{
			// No type
		}
		else
		{
			// One parameter, including bracketed product types
			types.add(parameters);
		}

		return types;
	}

	private PatternTypePair convertIdentifierTypePairList(
		List<IOmlIdentifierTypePair> results, LexLocation location) throws CGException
	{
		PatternList resultNames = new PatternList();
		TypeList resultTypes = new TypeList();

		for (IOmlIdentifierTypePair itp: results)
		{
			LexNameToken n = idToName(itp.getIdentifier(), itp);
			resultNames.add(new IdentifierPattern(n));
			Type t = convertType(itp.getType());
			resultTypes.add(t);
		}

		if (resultNames.size() > 1)
		{
			return new PatternTypePair(
				new TuplePattern(location, resultNames),
				new ProductType(location, resultTypes));
		}
		else if (resultNames.size() == 1)
		{
			return new PatternTypePair(
				resultNames.get(0), resultTypes.get(0));
		}
		else
		{
   			return null;
		}
	}

	private List<PatternListTypePair> convertPatternTypePairList(
		List<IOmlPatternTypePair> parameters) throws CGException
	{
		List<PatternListTypePair> pltpl = new Vector<PatternListTypePair>();

		for (IOmlPatternTypePair ptp: parameters)
		{
			Type t = convertType(ptp.getType());
			PatternList l = convertPatternList(ptp.getPatternList());
			pltpl.add(new PatternListTypePair(l, t));
		}

		return pltpl;
	}

	private LexNameList convertTypeVariableList(List<IOmlTypeVariable> typeParams)
		throws CGException
	{
		LexNameList list = new LexNameList();

		for (IOmlTypeVariable p: typeParams)
		{
			list.add(idToName(p.getIdentifier(), p));
		}

		return list.isEmpty() ? null : list;
	}

	private List<PatternList> convertParameterList(List<IOmlParameter> parameters)
		throws CGException
	{
		List<PatternList> lpl = new Vector<PatternList>();

		for (IOmlParameter par: parameters)
		{
			List<IOmlPattern> pl = par.getPatternList();
			PatternList l = new PatternList();

			for (IOmlPattern p: pl)
			{
				l.add(convertPattern(p));
			}

			lpl.add(l);
		}

		return lpl;
	}

	private PatternList convertPatternList(List<IOmlPattern> patternList)
		throws CGException
	{
		PatternList pl = new PatternList();

		for (IOmlPattern p: patternList)
		{
			pl.add(convertPattern(p));
		}

		return pl;
	}

	private Pattern convertPattern(IOmlPattern pattern) throws CGException
	{
		LexLocation location = getLocation(pattern);
		Pattern pat = null;

		if (pattern instanceof IOmlDontCarePattern)
		{
			pat = new IgnorePattern(location);
		}
		else if (pattern instanceof IOmlPatternIdentifier)
		{
			IOmlPatternIdentifier id = (IOmlPatternIdentifier)pattern;
			LexNameToken name = idToName(id.getIdentifier(), id);
			pat = new IdentifierPattern(name);
		}
		else if (pattern instanceof IOmlTuplePattern)
		{
			IOmlTuplePattern tp = (IOmlTuplePattern)pattern;
			PatternList tpl = convertPatternList(tp.getPatternList());
			pat = new TuplePattern(location, tpl);
		}
		else if (pattern instanceof IOmlMatchValue)
		{
			IOmlMatchValue mv = (IOmlMatchValue)pattern;
			pat = new ExpressionPattern(convertExpression(mv.getExpression()));
		}
		else if (pattern instanceof IOmlSymbolicLiteralPattern)
		{
			IOmlSymbolicLiteralPattern slp = (IOmlSymbolicLiteralPattern)pattern;
			Expression exp = convertLiteral(slp.getLiteral(), location);

			if (exp instanceof BooleanLiteralExpression)
			{
				BooleanLiteralExpression be = (BooleanLiteralExpression)exp;
				pat = new BooleanPattern(be.value);
			}
			else if (exp instanceof CharLiteralExpression)
			{
				CharLiteralExpression ce = (CharLiteralExpression)exp;
				pat = new CharacterPattern(ce.value);
			}
			else if (exp instanceof RealLiteralExpression)
			{
				RealLiteralExpression re = (RealLiteralExpression)exp;
				pat = new RealPattern(re.value);
			}
			else if (exp instanceof IntegerLiteralExpression)
			{
				IntegerLiteralExpression ie = (IntegerLiteralExpression)exp;
				pat = new IntegerPattern(ie.value);
			}
			else if (exp instanceof QuoteLiteralExpression)
			{
				QuoteLiteralExpression qe = (QuoteLiteralExpression)exp;
				pat = new QuotePattern(qe.type);
			}
			else if (exp instanceof StringLiteralExpression)
			{
				StringLiteralExpression se = (StringLiteralExpression)exp;
				pat = new StringPattern(se.value);
			}
			else
			{
				throw new InternalException(49, "Unexpected literal pattern type");
			}
		}
		else if (pattern instanceof IOmlSetEnumPattern)
		{
			IOmlSetEnumPattern sep = (IOmlSetEnumPattern)pattern;
			pat = new SetPattern(location, convertPatternList(sep.getPatternList()));
		}
		else if (pattern instanceof IOmlSetUnionPattern)
		{
			IOmlSetUnionPattern sup = (IOmlSetUnionPattern)pattern;
			Pattern lhs = convertPattern(sup.getLhsPattern());
			Pattern rhs = convertPattern(sup.getRhsPattern());
			pat = new UnionPattern(lhs, location, rhs);
		}
		else if (pattern instanceof IOmlSeqEnumPattern)
		{
			IOmlSeqEnumPattern sep = (IOmlSeqEnumPattern)pattern;
			pat = new SeqPattern(location, convertPatternList(sep.getPatternList()));
		}
		else if (pattern instanceof IOmlSeqConcPattern)
		{
			IOmlSeqConcPattern scp = (IOmlSeqConcPattern)pattern;
			Pattern lhs = convertPattern(scp.getLhsPattern());
			Pattern rhs = convertPattern(scp.getRhsPattern());
			pat = new ConcatenationPattern(lhs, location, rhs);
		}
		else if (pattern instanceof IOmlRecordPattern)
		{
			IOmlRecordPattern rp = (IOmlRecordPattern)pattern;
			pat = new RecordPattern(
				convertName(rp.getName()),
				convertPatternList(rp.getPatternList()));
		}
		else
		{
			throw new InternalException(50, "Unexpected pattern type");
		}

		return pat;
	}

	private AccessSpecifier convertAccess(IOmlAccessDefinition access)
		throws CGException
	{
		IOmlScope scope = access.getScope();
		Token tok = null;
		long val = scope.getValue();

		if (val == IOmlScopeQuotes.IQPROTECTED)
		{
			tok = Token.PROTECTED;
		}
		else if (val == IOmlScopeQuotes.IQPRIVATE)
		{
			tok = Token.PRIVATE;
		}
		else if (val == IOmlScopeQuotes.IQPUBLIC)
		{
			tok = Token.PUBLIC;
		}
		else if (val == IOmlScopeQuotes.IQDEFAULT)
		{
			tok = Token.PRIVATE;	// the default
		}
		else
		{
			throw new InternalException(51, "Unexpected scope value");
		}

		return new AccessSpecifier(
			access.getStaticAccess(), access.getAsyncAccess(), tok);
	}
}
