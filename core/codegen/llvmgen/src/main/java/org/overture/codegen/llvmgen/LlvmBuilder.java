package org.overture.codegen.llvmgen;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.overture.ast.definitions.AClassClassDefinition;
import org.overture.codegen.cgast.INode;
import org.overture.codegen.cgast.SStmCG;
import org.overture.codegen.cgast.analysis.AnalysisException;
import org.overture.codegen.cgast.analysis.QuestionAnswerAdaptor;
import org.overture.codegen.cgast.declarations.ADefaultClassDeclCG;
import org.overture.codegen.cgast.declarations.AFieldDeclCG;
import org.overture.codegen.cgast.declarations.AMethodDeclCG;
import org.overture.codegen.cgast.declarations.ASystemClassDeclCG;
import org.overture.codegen.cgast.declarations.AVarDeclCG;
import org.overture.codegen.cgast.expressions.AFieldExpCG;
import org.overture.codegen.cgast.expressions.AIdentifierVarExpCG;
import org.overture.codegen.cgast.expressions.ARealLiteralExpCG;
import org.overture.codegen.cgast.statements.ABlockStmCG;
import org.overture.codegen.cgast.statements.AReturnStmCG;
import org.overture.codegen.cgast.types.ARealBasicTypeWrappersTypeCG;
import org.overture.codegen.cgast.types.AVoidTypeCG;
import org.overture.codegen.cgast.types.SNumericBasicTypeCG;
import org.robovm.llvm.binding.Attribute;
import org.robovm.llvm.binding.BasicBlockRef;
import org.robovm.llvm.binding.BuilderRef;
import org.robovm.llvm.binding.ContextRef;
import org.robovm.llvm.binding.LLVM;
import org.robovm.llvm.binding.ModuleRef;
import org.robovm.llvm.binding.TypeRef;
import org.robovm.llvm.binding.TypeRefArray;
import org.robovm.llvm.binding.ValueRef;
import org.robovm.llvm.binding.ValueRefArray;

public class LlvmBuilder extends
		QuestionAnswerAdaptor<LlvmBuilder.Context, LlvmNode>
{
	public static class Context
	{

		public TypeRef classType;
		public ModuleRef module;
		public String callPrefix;
		public Map<INode, ValueRef> functions = new HashMap<INode, ValueRef>();
		public BuilderRef builder;
		public Map<INode, TypeRef> types = new HashMap<INode, TypeRef>();
		public ValueRef thisPtr;
	}

	public static class TypeCreator extends
			QuestionAnswerAdaptor<Context, TypeRef>
	{

		@Override
		public TypeRef defaultSNumericBasicTypeCG(SNumericBasicTypeCG node,
				Context question) throws AnalysisException
		{
			return LLVM.DoubleType();
		}
		
		
		@Override
		public TypeRef caseAVoidTypeCG(AVoidTypeCG node, Context question)
				throws AnalysisException
		{
			return LLVM.VoidType();
		}

		@Override
		public TypeRef defaultINode(INode node, Context question)
				throws AnalysisException
		{
			throw new AnalysisException("null");
		}

		@Override
		public TypeRef createNewReturnValue(INode arg0, Context arg1)
				throws AnalysisException
		{
			return null;
		}

		@Override
		public TypeRef createNewReturnValue(Object arg0, Context arg1)
				throws AnalysisException
		{
			return null;
		}

	}

	@Override
	public LlvmNode caseADefaultClassDeclCG(ADefaultClassDeclCG node,
			Context arg1) throws AnalysisException
	{

		ModuleRef mod = LLVM.ModuleCreateWithName("my_" + node.getName());

		//
		// Define state
		//
		Context ctxt = new Context();
		// state count
		TypeRefArray param_types = new TypeRefArray(1);

		// state type, field 0

		for (int i = 0; i < node.getFields().size(); i++)
		{
			AFieldDeclCG field = node.getFields().get(i);
			TypeRef type = field.getType().apply(new TypeCreator(), ctxt);
			ctxt.types.put(field, type);
			param_types.set(i, type);
		}

		ContextRef C = LLVM.GetModuleContext(mod);

		String className = "class." + node.getName();
		String classPrefix = node.getName() + "_";

		// Name class
		TypeRef classAType = LLVM.StructCreateNamed(C, className);

		// add instance variables to state body
		LLVM.StructSetBody(classAType, param_types, 1, false);

		ctxt.classType = classAType;
		ctxt.module = mod;
		ctxt.callPrefix = classPrefix;

		for (AMethodDeclCG method : node.getMethods())
		{
			method.apply(this, ctxt);
		}

		return new LlvmNode(node.getName(), mod);
	}

	@Override
	public LlvmNode caseAMethodDeclCG(AMethodDeclCG node, Context question)
			throws AnalysisException
	{

		int paramSize = node.getMethodType().getParams().size() + 1;

		TypeRefArray param_types = new TypeRefArray(paramSize);
		param_types.set(0, LLVM.PointerType(question.classType, 0));

		for (int i = 0; i < paramSize - 1; i++)
		{
			param_types.set(i + 1, node.getMethodType().getParams().get(i).apply(new TypeCreator(), null));
		}

		// param_types.set(0, LLVM.PointerType(question.classType, 0));// The default address space is 0, used for
		// optimization
		// http://llvm.org/docs/LangRef.html
		TypeRef ret_type = LLVM.FunctionType(node.getIsConstructor() ? LLVM.VoidType()
				: node.getMethodType().getResult().apply(new TypeCreator(), null), param_types, paramSize, false);
		ValueRef ctor = LLVM.AddFunction(question.module, question.callPrefix
				+ node.getName(), ret_type);
		LLVM.AddFunctionAttr(ctor, Attribute.NoUnwindAttribute.swigValue());

		question.functions.put(node, ctor);

		BasicBlockRef entry = LLVM.AppendBasicBlock(ctor, "");// We dont need to label the block
		BuilderRef builder = LLVM.CreateBuilder();
		LLVM.PositionBuilderAtEnd(builder, entry);

		question.builder = builder;

		question.thisPtr = LLVM.GetParam(ctor, 0);

		node.getBody().apply(this, question);

		if (node.getIsConstructor())
		{
			// TODO check what happens if other returns is all ready present

			LinkedList<AFieldDeclCG> fields = node.getAncestor(ADefaultClassDeclCG.class).getFields();

			for (AFieldDeclCG field : fields)
			{
				if (field.getInitial() != null)
				{
					ValueRef fRef = getField(question, question.thisPtr, field, field.getName());

					ValueRefLLvmNode fieldValue = (ValueRefLLvmNode) field.getInitial().apply(this, question);
					LLVM.BuildStore(builder, fieldValue.valueRef, fRef);

				}
			}

			LLVM.BuildRetVoid(builder);
		}

		// TODO Auto-generated method stub
		return super.caseAMethodDeclCG(node, question);
	}

	public ValueRef getField(Context c, ValueRef thisPtr, INode fieldDcl,
			String name)
	{

		int fieldIndex = -1;

		LinkedList<AFieldDeclCG> fields = fieldDcl.getAncestor(ADefaultClassDeclCG.class).getFields();
		for (int i = 0; i < fields.size(); i++)
		{
			if (fields.get(i).getName().equals(name))
			{
				fieldIndex = i;
				break;
			}

		}

		if (fieldIndex < 0)
			return null;

		ValueRefArray valrefArr = new ValueRefArray(2);
		valrefArr.set(0, LLVM.ConstInt(LLVM.Int32Type(), new BigInteger("0"), false));
		valrefArr.set(1, LLVM.ConstInt(LLVM.Int32Type(), new BigInteger(""
				+ fieldIndex), false));

		ValueRef field0 = LLVM.BuildGEP(c.builder, thisPtr, valrefArr, 2, "");

		return field0;
	}

	public ValueRef getFieldValue(Context c, ValueRef thisPtr, INode fieldDcl,
			String name)
	{

		int fieldIndex = -1;

		LinkedList<AFieldDeclCG> fields = fieldDcl.getAncestor(ADefaultClassDeclCG.class).getFields();
		for (int i = 0; i < fields.size(); i++)
		{
			if (fields.get(i).getName().equals(name))
			{
				fieldIndex = i;
				break;
			}

		}

		if (fieldIndex < 0)
			return null;

		ValueRefArray valrefArr = new ValueRefArray(2);
		valrefArr.set(0, LLVM.ConstInt(LLVM.Int32Type(), new BigInteger("0"), false));
		valrefArr.set(1, LLVM.ConstInt(LLVM.Int32Type(), new BigInteger(""
				+ fieldIndex), false));

		ValueRef field0 = LLVM.BuildGEP(c.builder, thisPtr, valrefArr, 2, "");

		ValueRef length = LLVM.BuildLoad(c.builder, field0, "");

		return length;
	}
	
	@Override
	public LlvmNode caseARealLiteralExpCG(ARealLiteralExpCG node,
			Context question) throws AnalysisException
	{
		return new ValueRefLLvmNode(LLVM.ConstReal(LLVM.DoubleType(), node.getValue()));
	}

	@Override
	public LlvmNode caseAReturnStmCG(AReturnStmCG node, Context question)
			throws AnalysisException
	{

		if (node.getExp() == null)
			LLVM.BuildRetVoid(question.builder);
		else
		{
			LlvmNode value = node.getExp().apply(this, question);

			if (value instanceof ValueRefLLvmNode)
				LLVM.BuildRet(question.builder, ((ValueRefLLvmNode) value).valueRef);
		}

		return null;

	}

	@Override
	public LlvmNode caseAIdentifierVarExpCG(AIdentifierVarExpCG node,
			Context question) throws AnalysisException
	{

		ValueRef valref = getFieldValue(question, question.thisPtr, node, node.getName());

		if (valref != null)
			return new ValueRefLLvmNode(valref);

		return null;
		// TODO fix it for local vars

	}

	@Override
	public LlvmNode caseABlockStmCG(ABlockStmCG node, Context question)
			throws AnalysisException
	{

		for (SStmCG stm : node.getStatements())
		{
			stm.apply(this, question);
		}

		// TODO Auto-generated method stub
		return super.caseABlockStmCG(node, question);
	}

	@Override
	public LlvmNode caseASystemClassDeclCG(ASystemClassDeclCG node, Context arg1)
			throws AnalysisException
	{
		return new LlvmNode(node.getName());
	}

	@Override
	public LlvmNode createNewReturnValue(INode node, Context arg1)
			throws AnalysisException
	{
		return null;
	}

	@Override
	public LlvmNode createNewReturnValue(Object node, Context arg1)
			throws AnalysisException
	{
		return null;
	}

}