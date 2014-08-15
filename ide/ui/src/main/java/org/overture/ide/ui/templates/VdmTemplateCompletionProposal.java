/*
 * #%~
 * org.overture.ide.ui
 * %%
 * Copyright (C) 2008 - 2014 Overture
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #~%
 */
package org.overture.ide.ui.templates;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateProposal;
import org.eclipse.swt.graphics.Image;

public class VdmTemplateCompletionProposal extends TemplateProposal {

//	private final Template fTemplate;
//	private final TemplateContext fContext;
//	private final Image fImage;
//	private final IRegion fRegion;
//	private int fRelevance;
//	private String fDisplayString;
	
	public VdmTemplateCompletionProposal(Template template,
			TemplateContext context, IRegion region, Image image, int relevance) {
		super(template, context, region, image, relevance);
//		fTemplate=template;
//		 fContext = context;
//		 fImage=image;
//		 fRegion=region;
//		 fRelevance=relevance;
//		 fDisplayString=null;
	}

//	@Override
//
//	public String getAdditionalProposalInfo() {
//	 return StringUtils.convertToHTMLContent(fTemplate.getPattern());
//
//	 }

}
