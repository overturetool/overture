package org.overture.codegen.vdm2cpp.vdmtools

import java.util.List
import org.overture.codegen.cgast.declarations.AClassDeclCG
import java.util.LinkedList

class CGGenHelper {
	
	
	
	List<AClassDeclCG> classes;
	
	new()
	{
		classes = new LinkedList<AClassDeclCG>();
	}
	
	
	def addClass(AClassDeclCG cls)
	{
		classes.add(cls);
	}
	
	def GenerateHelper()
	{return new String(
	'''
	#ifndef _CGBase_h
	#define _CGBase_h
	#include "cg.h"
	
	// forward declare all classes
	«FOR cls : classes»
	class «cls.name»;
	«ENDFOR»
	
	class CGBase : public vdmBase
	{
	private:
		virtual bool has_cg_base () const   
		{
			return true;
		}
	public: 
		«FOR cls : classes»
		virtual «cls.name» * Get_«cls.name» ()   
		{
			return 0;
		}
		«ENDFOR»
		
	};
	«FOR cls: classes»
	«cls.name» * ObjGet_«cls.name» (const ObjectRef &obj) 
	{
		if (!obj.IsInitialized()) 
			CGUTIL::RunTime(L"Identifier is undefined/not initialized");
		vdmBase * p = obj.GetRef();
	
		return (p ? (static_cast<CGBase *>(p))->Get_«cls.name»() : 0);
	}
	«ENDFOR»
	
	enum
	{
		«FOR cls : classes SEPARATOR ','»
		VDM_«cls.name»
		«ENDFOR»
	};
	#endif
	''');
	}
}

