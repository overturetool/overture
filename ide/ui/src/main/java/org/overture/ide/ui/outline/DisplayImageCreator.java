/*******************************************************************************
 * Copyright (c) 2009, 2011 Overture Team and others.
 *
 * Overture is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Overture is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Overture.  If not, see <http://www.gnu.org/licenses/>.
 * 	
 * The Overture Tool web-site: http://overturetool.org/
 *******************************************************************************/
package org.overture.ide.ui.outline;


import org.eclipse.swt.graphics.Image;
import org.overture.ast.definitions.APrivateAccess;
import org.overture.ast.definitions.AProtectedAccess;
import org.overture.ast.definitions.APublicAccess;
import org.overture.ast.definitions.AValueDefinition;
import org.overture.ast.definitions.PDefinition;
import org.overture.ast.definitions.SClassDefinition;
import org.overture.ast.modules.AModuleModules;
import org.overture.ide.ui.VdmPluginImages;

public class DisplayImageCreator
{

	public static Image getImage(Object element)
	{
		if (element instanceof SClassDefinition || element instanceof AModuleModules)
			return VdmPluginImages.get(VdmPluginImages.IMG_OBJS_CLASS);
		else if (element instanceof AValueDefinition)
		{
			PDefinition def = (PDefinition) element;
			switch (def.getAccess().getAccess().kindPAccess())
			{
			case APrivateAccess.kindPAccess:
				return VdmPluginImages.get(VdmPluginImages.IMG_FIELD_PRIVATE);

			case AProtectedAccess.kindPAccess:
				return VdmPluginImages.get(VdmPluginImages.IMG_FIELD_PROTECTED);

			case APublicAccess.kindPAccess:
			default:
				return VdmPluginImages.get(VdmPluginImages.IMG_FIELD_PUBLIC);
			}
		} else if (element instanceof PDefinition)
		{
			PDefinition def = (PDefinition) element;
			if(def.getClassDefinition()==null)
				return VdmPluginImages.get(VdmPluginImages.IMG_METHOD_PUBLIC);//VDM-SL
			switch (def.getAccess().getAccess().kindPAccess())
			{
			case APrivateAccess.kindPAccess:
				return VdmPluginImages.get(VdmPluginImages.IMG_METHOD_PRIVATE);

			case AProtectedAccess.kindPAccess:
				return VdmPluginImages.get(VdmPluginImages.IMG_METHOD_PROTECTED);

			case APublicAccess.kindPAccess:
			default:
				return VdmPluginImages.get(VdmPluginImages.IMG_METHOD_PUBLIC);
			}
		}
		return null;
	}

}
