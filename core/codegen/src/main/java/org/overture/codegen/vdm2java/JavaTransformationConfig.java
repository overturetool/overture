package org.overture.codegen.vdm2java;

import org.overture.codegen.constants.IJavaCodeGenConstants;
import org.overture.codegen.transform.ITransformationConfig;

public class JavaTransformationConfig implements ITransformationConfig
{
	@Override
	public String seqUtilFile()
	{
		return IJavaCodeGenConstants.SEQ_UTIL_FILE;
	}
	
	@Override
	public String setUtilFile()
	{
		return IJavaCodeGenConstants.SET_UTIL_FILE;
	}
	
	@Override
	public String mapUtilFile()
	{
		return IJavaCodeGenConstants.MAP_UTIL_FILE;
	}
		
	@Override
	public String seqUtilEmptySeqCall()
	{
		return IJavaCodeGenConstants.SEQ_UTIL_EMPTY_SEQ_CALL;
	}

	@Override
	public String setUtilEmptySetCall()
	{
		return IJavaCodeGenConstants.SET_UTIL_EMPTY_SET_CALL;
	}
	
	@Override
	public String mapUtilEmptyMapCall()
	{
		return IJavaCodeGenConstants.MAP_UTIL_EMPTY_MAP_CALL;
	}
	
	@Override
	public String addElementToSeq()
	{
		return IJavaCodeGenConstants.ADD_ELEMENT_TO_SEQ;
	}

	@Override
	public String addElementToSet()
	{
		return IJavaCodeGenConstants.ADD_ELEMENT_TO_SET;
	}
	
	@Override
	public String addElementToMap()
	{
		return IJavaCodeGenConstants.ADD_ELEMENT_TO_MAP;
	}
	
	@Override
	public String iteratorType()
	{
		return IJavaCodeGenConstants.ITERATOR_TYPE;
	}
	
	@Override
	public String hasNextElement()
	{
		return IJavaCodeGenConstants.HAS_NEXT_ELEMENT_ITERATOR;
	}

	@Override
	public String nextElement()
	{
		return IJavaCodeGenConstants.NEXT_ELEMENT_ITERATOR;
	}

	@Override
	public String runtimeExceptionTypeName()
	{
		return IJavaCodeGenConstants.RUNTIME_EXCEPTION_TYPE_NAME;
	}

	@Override
	public String iteratorMethod()
	{
		return IJavaCodeGenConstants.GET_ITERATOR;
	}
}
