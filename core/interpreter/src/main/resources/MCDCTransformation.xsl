<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

    <xsl:output indent="yes" method="html"/>

    <xsl:template match="/">
        <xsl:apply-templates select="file"/>
    </xsl:template>

    <xsl:template match="file">
        <html>
            <head>
                <title>MCDC Report</title>
            </head>
            <body>
                <center>
                    <h1><xsl:value-of select="file_name"/> MC/DC Report</h1>
                </center>
                <xsl:apply-templates select="/*" mode="specific"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="if_statement" mode="specific">
        <center>
            <h3>If Statement</h3>
            <h4>Line : <xsl:value-of select="@start_line"/></h4>
        </center>
        <center>
            <table width="70%" border="1">
                <thead>
                    <tr>
                        <td>
                            <b>Outcome : <xsl:value-of select="source_code"/></b>
                        </td>
                        <xsl:for-each select="condition">
                            <xsl:sort select="@start_column"/>
                            <td>
                                <xsl:value-of select="source_code"/>
                            </td>
                        </xsl:for-each>
                    </tr>
                </thead>
                <tbody>
                    <xsl:for-each select="condition[1]/evaluation">
                        <xsl:sort select="@n"/>
                        <xsl:apply-templates select="@n"/>
                    </xsl:for-each>
                </tbody>
            </table>
        </center>
    </xsl:template>

    <xsl:template match="elseif_statement" mode="specific">
        <center>
            <h3>ElseIf Statement</h3>
            <h4>Line : <xsl:value-of select="@start_line"/></h4>
        </center>
        <center>
            <table width="70%" border="1">
                <thead>
                    <tr>
                        <td>
                            <b>Outcome : <xsl:value-of select="source_code"/></b>
                        </td>
                        <xsl:for-each select="condition">
                            <xsl:sort select="@start_column"/>
                            <td>
                                <xsl:value-of select="source_code"/>
                            </td>
                        </xsl:for-each>
                    </tr>
                </thead>
                <tbody>
                    <xsl:for-each select="condition[1]/evaluation">
                        <xsl:sort select="@n"/>
                        <xsl:apply-templates select="@n"/>
                    </xsl:for-each>
                </tbody>
            </table>
        </center>
    </xsl:template>

    <xsl:template match="while_statement" mode="specific">
        <center>
            <h3>ElseIf Statement</h3>
            <h4>Line : <xsl:value-of select="@start_line"/></h4>
        </center>
        <center>
            <table width="70%" border="1">
                <thead>
                    <tr>
                        <td>
                            <b>Outcome : <xsl:value-of select="source_code"/></b>
                        </td>
                        <xsl:for-each select="condition">
                            <xsl:sort select="@start_column"/>
                            <td>
                                <xsl:value-of select="source_code"/>
                            </td>
                        </xsl:for-each>
                    </tr>
                </thead>
                <tbody>
                    <xsl:for-each select="condition[1]/evaluation">
                        <xsl:sort select="@n"/>
                        <xsl:apply-templates select="@n"/>
                    </xsl:for-each>
                </tbody>
            </table>
        </center>
    </xsl:template>

    <xsl:template match="if_expression" mode="specific">
        <center>
            <h3>If Expression</h3>
            <h4>Line : <xsl:value-of select="@start_line"/></h4>
        </center>
        <center>
            <table width="70%" border="1">
                <thead>
                    <tr>
                        <td>
                            <b>Outcome : <xsl:value-of select="source_code"/></b>
                        </td>
                        <xsl:for-each select="condition">
                            <xsl:sort select="@start_column"/>
                            <td>
                                <xsl:value-of select="source_code"/>
                            </td>
                        </xsl:for-each>
                    </tr>
                </thead>
                <tbody>
                    <xsl:for-each select="condition[1]/evaluation">
                        <xsl:sort select="@n"/>
                        <xsl:apply-templates select="@n"/>
                    </xsl:for-each>
                </tbody>
            </table>
        </center>
    </xsl:template>

    <xsl:template match="@n">
        <xsl:variable name="test_number" select="."/>
        <tr>
            <td>
                <xsl:choose>
                    <xsl:when test="../../../evaluation[@tested='false' and @n=$test_number]">
                        <font color="red">
                            <b>Test <xsl:value-of select="$test_number"/>: <xsl:value-of
                                select="../../../evaluation[@n=$test_number]"/></b>
                        </font>
                    </xsl:when>
                    <xsl:when test="../../../evaluation[@tested='true' and @n=$test_number]">
                        <font color="green">
                            <b>Test <xsl:value-of select="$test_number"/>: <xsl:value-of
                                select="../../../evaluation[@n=$test_number]"/></b>
                        </font>
                    </xsl:when>
                </xsl:choose>
                
            </td>

            <xsl:for-each select="../../..//condition/evaluation[@n=$test_number]">
                <xsl:sort select="../@start_column"/>
                <td>
                    <xsl:choose>
                        <xsl:when test="@tested='false'">
                            <font color="red">
                                <xsl:value-of select="."/>
                            </font>
                        </xsl:when>
                        <xsl:when test="@tested='true'">
                            <font color="green">
                                <xsl:value-of select="."/>
                            </font>
                        </xsl:when>
                    </xsl:choose>
                </td>
            </xsl:for-each>
        </tr>
    </xsl:template>
</xsl:stylesheet>
