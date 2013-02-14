// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 30-07-2009 16:59:35
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   Record.java

package jp.co.csk.vdm.toolbox.VDM;

import java.io.Serializable;

public interface Record
    extends Cloneable, Serializable
{

    public abstract boolean equals(Object obj);

    public abstract String toString();

    public abstract Object clone();
}