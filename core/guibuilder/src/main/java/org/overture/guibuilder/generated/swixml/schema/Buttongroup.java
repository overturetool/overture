//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.3-hudson-jaxb-ri-2.2-70- 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.04.08 at 06:53:28 PM WEST 
//

package org.overture.guibuilder.generated.swixml.schema;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for anonymous complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded" minOccurs="0">
 *         &lt;any/>
 *       &lt;/sequence>
 *       &lt;attribute name="constraints" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="plaf" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="bundle" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="locale" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="refid" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="use" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="include" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="initclass" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="action" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="macos_preferences" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="macos_about" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="macos_quit" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="macos_openapp" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="macos_openfile" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="macos_print" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="macos_reopen" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "content" })
@XmlRootElement(name = "buttongroup")
public class Buttongroup
{

	@XmlMixed
	@XmlAnyElement(lax = true)
	protected List<Object> content;
	@XmlAttribute(name = "constraints")
	protected String constraints;
	@XmlAttribute(name = "plaf")
	protected String plaf;
	@XmlAttribute(name = "bundle")
	protected String bundle;
	@XmlAttribute(name = "locale")
	protected String locale;
	@XmlAttribute(name = "id")
	protected String id;
	@XmlAttribute(name = "refid")
	protected String refid;
	@XmlAttribute(name = "use")
	protected String use;
	@XmlAttribute(name = "include")
	protected String include;
	@XmlAttribute(name = "initclass")
	protected String initclass;
	@XmlAttribute(name = "action")
	protected String action;
	@XmlAttribute(name = "macos_preferences")
	protected String macosPreferences;
	@XmlAttribute(name = "macos_about")
	protected String macosAbout;
	@XmlAttribute(name = "macos_quit")
	protected String macosQuit;
	@XmlAttribute(name = "macos_openapp")
	protected String macosOpenapp;
	@XmlAttribute(name = "macos_openfile")
	protected String macosOpenfile;
	@XmlAttribute(name = "macos_print")
	protected String macosPrint;
	@XmlAttribute(name = "macos_reopen")
	protected String macosReopen;

	/**
	 * Gets the value of the content property.
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
	 * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
	 * the content property.
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getContent().add(newItem);
	 * </pre>
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String } {@link Object }
	 * @return 
	 */
	public List<Object> getContent()
	{
		if (content == null)
		{
			content = new ArrayList<Object>();
		}
		return this.content;
	}

	/**
	 * Gets the value of the constraints property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getConstraints()
	{
		return constraints;
	}

	/**
	 * Sets the value of the constraints property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setConstraints(String value)
	{
		this.constraints = value;
	}

	/**
	 * Gets the value of the plaf property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getPlaf()
	{
		return plaf;
	}

	/**
	 * Sets the value of the plaf property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setPlaf(String value)
	{
		this.plaf = value;
	}

	/**
	 * Gets the value of the bundle property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getBundle()
	{
		return bundle;
	}

	/**
	 * Sets the value of the bundle property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setBundle(String value)
	{
		this.bundle = value;
	}

	/**
	 * Gets the value of the locale property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getLocale()
	{
		return locale;
	}

	/**
	 * Sets the value of the locale property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setLocale(String value)
	{
		this.locale = value;
	}

	/**
	 * Gets the value of the id property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * Sets the value of the id property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setId(String value)
	{
		this.id = value;
	}

	/**
	 * Gets the value of the refid property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getRefid()
	{
		return refid;
	}

	/**
	 * Sets the value of the refid property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setRefid(String value)
	{
		this.refid = value;
	}

	/**
	 * Gets the value of the use property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getUse()
	{
		return use;
	}

	/**
	 * Sets the value of the use property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setUse(String value)
	{
		this.use = value;
	}

	/**
	 * Gets the value of the include property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getInclude()
	{
		return include;
	}

	/**
	 * Sets the value of the include property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setInclude(String value)
	{
		this.include = value;
	}

	/**
	 * Gets the value of the initclass property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getInitclass()
	{
		return initclass;
	}

	/**
	 * Sets the value of the initclass property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setInitclass(String value)
	{
		this.initclass = value;
	}

	/**
	 * Gets the value of the action property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 * Sets the value of the action property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setAction(String value)
	{
		this.action = value;
	}

	/**
	 * Gets the value of the macosPreferences property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getMacosPreferences()
	{
		return macosPreferences;
	}

	/**
	 * Sets the value of the macosPreferences property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setMacosPreferences(String value)
	{
		this.macosPreferences = value;
	}

	/**
	 * Gets the value of the macosAbout property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getMacosAbout()
	{
		return macosAbout;
	}

	/**
	 * Sets the value of the macosAbout property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setMacosAbout(String value)
	{
		this.macosAbout = value;
	}

	/**
	 * Gets the value of the macosQuit property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getMacosQuit()
	{
		return macosQuit;
	}

	/**
	 * Sets the value of the macosQuit property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setMacosQuit(String value)
	{
		this.macosQuit = value;
	}

	/**
	 * Gets the value of the macosOpenapp property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getMacosOpenapp()
	{
		return macosOpenapp;
	}

	/**
	 * Sets the value of the macosOpenapp property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setMacosOpenapp(String value)
	{
		this.macosOpenapp = value;
	}

	/**
	 * Gets the value of the macosOpenfile property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getMacosOpenfile()
	{
		return macosOpenfile;
	}

	/**
	 * Sets the value of the macosOpenfile property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setMacosOpenfile(String value)
	{
		this.macosOpenfile = value;
	}

	/**
	 * Gets the value of the macosPrint property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getMacosPrint()
	{
		return macosPrint;
	}

	/**
	 * Sets the value of the macosPrint property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setMacosPrint(String value)
	{
		this.macosPrint = value;
	}

	/**
	 * Gets the value of the macosReopen property.
	 * 
	 * @return possible object is {@link String }
	 */
	public String getMacosReopen()
	{
		return macosReopen;
	}

	/**
	 * Sets the value of the macosReopen property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 */
	public void setMacosReopen(String value)
	{
		this.macosReopen = value;
	}

}