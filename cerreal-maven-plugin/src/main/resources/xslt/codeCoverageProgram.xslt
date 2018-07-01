<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>
 
<xsl:template match="/">
  <xsl:element name="head">
    <xsl:call-template name="writeJavaScript"/>
    <xsl:call-template name="importStyles"/>
  </xsl:element>
 
  <xsl:element name="body">
    <xsl:attribute name="onload">loadForm()</xsl:attribute>

    <xsl:call-template name="writeSummarySection"/>

    <br/>
    <form>
    	<input type="checkbox" class="C" id="chk1" onClick="toggleHighlight(1, this.checked)">Highlight Covered Code</input><br/>
    	<input type="checkbox" class="U" id="chk2" onClick="toggleHighlight(2, this.checked)">Highlight Not Covered Code</input><br/>
    	<input type="checkbox" class="N" id="chk3" onClick="toggleHighlight(3, this.checked)">Highlight Not Executable Code</input><br/>
    </form>
    <br/>
    
    <table class="programCode">
      <xsl:apply-templates select="/program/lines/line"/>
    </table>
  </xsl:element>
</xsl:template>

<xsl:template name="importStyles">
  <style type="text/css" id="styleUncovered">
    .NOT_COVERED
    {
      background-color:#FFCCCC
    }
  </style>
  <style type="text/css" id="styleCovered">
    .COVERED
    {
      background-color:#CCFFCC
    }
  </style>
  <style type="text/css" id="styleNotExe">
    .NOT_EXECUTABLE, .UNDEFINED
    {
      background-color:#CCCCCC
    }
  </style>
  <LINK REL="StyleSheet" HREF="css/codeCoverageProgram.css"/>
</xsl:template>

<xsl:template name="writeSummarySection">
  <table class="summary">
    <xsl:variable name="coveredCnt"       select="count(/program/lines/line[coveredStatus = 'COVERED'])"/>
    <xsl:variable name="uncoveredCnt"     select="count(/program/lines/line[coveredStatus = 'NOT_COVERED'])"/>
    <xsl:variable name="notExecutableCnt" select="count(/program/lines/line[coveredStatus != 'COVERED' and coveredStatus != 'NOT_COVERED'])"/>
    <xsl:variable name="totalLineCnt"     select="$coveredCnt + $uncoveredCnt + $notExecutableCnt"/>
 
    <tr>
      <td>Program Name</td>
      <td><xsl:value-of select="/program/name"/></td>
    </tr>
    <tr>
      <td>Covered</td>
      <td>
        <xsl:value-of select="format-number($coveredCnt div ($coveredCnt + $uncoveredCnt) , '00.00%')"/>
        <xsl:value-of select="concat(' - ', $coveredCnt, ' / ', $coveredCnt + $uncoveredCnt)"/>
      </td>
    </tr>
    <tr>
      <td>Not Covered</td>
      <td>
        <xsl:value-of select="format-number($uncoveredCnt div ($coveredCnt + $uncoveredCnt) , '00.00%')"/>
        <xsl:value-of select="concat(' - ', $uncoveredCnt, ' / ', $coveredCnt + $uncoveredCnt)"/>
      </td>
    </tr>
    <tr>
      <td>Not Executable</td>
      <td>
        <xsl:value-of select="format-number($notExecutableCnt div $totalLineCnt , '00.00%')"/>
        <xsl:value-of select="concat(' - ', $notExecutableCnt, ' / ', $totalLineCnt)"/>
      </td>
    </tr>
  </table>
</xsl:template>


<xsl:template name="writeJavaScript">
  <script type="text/javascript">
    function loadForm()
    {
    	document.getElementById("chk1").checked = true;
    	document.getElementById("chk2").checked = true;
    	document.getElementById("chk3").checked = false;
    	document.getElementById("styleNotExe").disabled = true;
    }

    function toggleHighlight(idx, val)
    {
    	if (idx == 1)
    	{
    		document.getElementById("styleCovered").disabled = !val;
    	}
    	else if (idx == 2)
    	{
    		document.getElementById("styleUncovered").disabled = !val;
    	}
    	else if (idx == 3)
    	{
    		document.getElementById("styleNotExe").disabled = !val;
    	}
    }
  </script>
</xsl:template>
 
<xsl:template match="/program/lines/line">
  <tr class="line">
    <td class="lineNum"><xsl:value-of select="concat(number, ')')"/></td>
  
    <xsl:element name="td">
      <xsl:element name="span">
        <xsl:attribute name="class"><xsl:value-of select="concat('code ', coveredStatus)"/></xsl:attribute>

        <xsl:value-of select="text"/>
      </xsl:element>
    </xsl:element>
  </tr>
</xsl:template>
 
</xsl:stylesheet>