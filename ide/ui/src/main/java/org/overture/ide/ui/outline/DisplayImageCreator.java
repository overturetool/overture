package org.overture.ide.ui.outline;

import org.eclipse.swt.graphics.Image;
import org.overture.ide.ui.VdmPluginImages;
import org.overturetool.vdmj.definitions.ClassDefinition;
import org.overturetool.vdmj.definitions.Definition;
import org.overturetool.vdmj.definitions.ValueDefinition;
import org.overturetool.vdmj.modules.Module;

public class DisplayImageCreator
{

	public static Image getImage(Object element)
	{
		if (element instanceof ClassDefinition || element instanceof Module)
			return VdmPluginImages.get(VdmPluginImages.IMG_OBJS_CLASS);
		else if (element instanceof ValueDefinition)
		{
			Definition def = (Definition) element;
			switch (def.accessSpecifier.access)
			{
			case PRIVATE:
				return VdmPluginImages.get(VdmPluginImages.IMG_FIELD_PRIVATE);

			case PROTECTED:
				return VdmPluginImages.get(VdmPluginImages.IMG_FIELD_PROTECTED);

			case PUBLIC:
			default:
				return VdmPluginImages.get(VdmPluginImages.IMG_FIELD_PUBLIC);
			}
		} else if (element instanceof Definition)
		{
			Definition def = (Definition) element;
			if(def.classDefinition==null)
				return VdmPluginImages.get(VdmPluginImages.IMG_METHOD_PUBLIC);//VDM-SL
			switch (def.accessSpecifier.access)
			{
			case PRIVATE:
				return VdmPluginImages.get(VdmPluginImages.IMG_METHOD_PRIVATE);

			case PROTECTED:
				return VdmPluginImages.get(VdmPluginImages.IMG_METHOD_PROTECTED);

			case PUBLIC:
			default:
				return VdmPluginImages.get(VdmPluginImages.IMG_METHOD_PUBLIC);
			}
		}
		return null;
	}

}
