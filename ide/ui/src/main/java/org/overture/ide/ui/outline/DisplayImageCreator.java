package org.overture.ide.ui.outline;

import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.swt.graphics.Image;
import org.omg.CORBA.OBJ_ADAPTER;
import org.overturetool.vdmj.definitions.*;
import org.overturetool.vdmj.lex.Token;
import org.overturetool.vdmj.modules.Module;

public class DisplayImageCreator
{

	public static Image getImage(Object element)
	{
		if (element instanceof ClassDefinition || element instanceof Module)
			return DLTKPluginImages.get(DLTKPluginImages.IMG_OBJS_CLASS);
		else if (element instanceof ValueDefinition)
		{
			Definition def = (Definition) element;
			switch (def.accessSpecifier.access)
			{
			case PRIVATE:
				return DLTKPluginImages.get(DLTKPluginImages.IMG_FIELD_PRIVATE);

			case PROTECTED:
				return DLTKPluginImages.get(DLTKPluginImages.IMG_FIELD_PROTECTED);

			case PUBLIC:
			default:
				return DLTKPluginImages.get(DLTKPluginImages.IMG_FIELD_PUBLIC);
			}
		} else if (element instanceof Definition)
		{
			Definition def = (Definition) element;
			if(def.classDefinition==null)
				return DLTKPluginImages.get(DLTKPluginImages.IMG_METHOD_PUBLIC);//VDM-SL
			switch (def.accessSpecifier.access)
			{
			case PRIVATE:
				return DLTKPluginImages.get(DLTKPluginImages.IMG_METHOD_PRIVATE);

			case PROTECTED:
				return DLTKPluginImages.get(DLTKPluginImages.IMG_METHOD_PROTECTED);

			case PUBLIC:
			default:
				return DLTKPluginImages.get(DLTKPluginImages.IMG_METHOD_PUBLIC);
			}
		}
		return null;
	}

}
