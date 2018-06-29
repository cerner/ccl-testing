<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes" doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"/>

  <xsl:variable name="listing" select="document('listing.xml')/LISTING"/>

  <xsl:key name="assertionResultsByLine" match="/TESTCASE/TESTS/TEST/ASSERTS/ASSERT" use="LINENUMBER"/>

  <xsl:template match="/">
    <xsl:element name="HTML">
      <xsl:element name="HEAD">
        <xsl:call-template name="importStyles"/>
      </xsl:element>

      <xsl:element name="BODY">
        <xsl:call-template name="writeSummaryBlock"/>
        <xsl:element name="br"/>

        <!-- Write out the program Code -->
        <xsl:call-template name="writeProgramCode">
          <xsl:with-param name="lines" select="$listing/LINES/LINE"/>
        </xsl:call-template>
      </xsl:element>
    </xsl:element>
  </xsl:template>

  <xsl:template name="importStyles">
    <LINK REL="StyleSheet" HREF="css/testResultsProgram.css"/>
  </xsl:template>

  <xsl:template name="writeSummaryBlock">
    <table class="summary">
      <xsl:variable name="unitTestsPassCnt" select="count(/TESTCASE/TESTS/TEST[RESULT = 'PASSED'])"/>
      <xsl:variable name="unitTestsFailCnt" select="count(/TESTCASE/TESTS/TEST[RESULT = 'FAILED'])"/>
      <xsl:variable name="unitTestsTotalCnt" select="count(/TESTCASE/TESTS/TEST)"/>

      <xsl:if test="$unitTestsTotalCnt &gt; 0">
        <tr>
          <td>Passed Tests</td>
          <td>
            <xsl:value-of select="format-number($unitTestsPassCnt div $unitTestsTotalCnt , '000.00%')"/>
            <xsl:value-of select="concat(' - ', $unitTestsPassCnt, ' / ', $unitTestsTotalCnt)"/>
          </td>
        </tr>
        <tr>
          <td>Failed Tests</td>
          <td>
            <xsl:value-of select="format-number($unitTestsFailCnt div $unitTestsTotalCnt , '000.00%')"/>
            <xsl:value-of select="concat(' - ', $unitTestsFailCnt, ' / ', $unitTestsTotalCnt)"/>
          </td>
        </tr>
      </xsl:if>
    </table>
  </xsl:template>

  <xsl:template name="writeProgramCode">
    <xsl:param name="lines"/>

    <table class="programCode">
      <xsl:for-each select="$lines">
        <xsl:variable name="lineAssertResults" select="key('assertionResultsByLine', NBR)"/>

        <xsl:variable name="lineAssertResult">
          <xsl:if test="count($lineAssertResults) &gt; 0">
            <xsl:choose>
              <xsl:when test="count($lineAssertResults) = 0">
                <xsl:value-of select="'NOT_AN_ASSERT'"/>
              </xsl:when>
              <xsl:when test="count($lineAssertResults) = count($lineAssertResults[RESULT = 'PASSED'])">
                <xsl:value-of select="'PASSED_ASSERT'"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="'FAILED_ASSERT'"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:if>
        </xsl:variable>

        <xsl:element name="tr">
          <xsl:attribute name="class">line</xsl:attribute>

          <xsl:element name="td">
            <xsl:choose>
              <xsl:when test="$lineAssertResult = 'PASSED_ASSERT'">
                <img src="../images/icon_success_sml.gif" height="15px" width="15px"/>
              </xsl:when>
              <xsl:when test="$lineAssertResult = 'FAILED_ASSERT'">
                <img src="../images/icon_error_sml.gif" height="15px" width="15px"/>
              </xsl:when>
            </xsl:choose>
          </xsl:element>

          <xsl:element name="td">
            <xsl:element name="span">
              <xsl:attribute name="class">
                <xsl:choose>
                  <xsl:when test="$lineAssertResult = 'PASSED_ASSERT'">code passed-assert</xsl:when>
                  <xsl:when test="$lineAssertResult = 'FAILED_ASSERT'">code failed-assert</xsl:when>
                  <xsl:otherwise>code</xsl:otherwise>
                </xsl:choose>
              </xsl:attribute>

              <xsl:value-of select="TEXT"/>
            </xsl:element>
          </xsl:element>
        </xsl:element>

        <xsl:if test="$lineAssertResult = 'FAILED_ASSERT'">
          <xsl:element name="tr">
            <td/>
            <td>
              <div class="assertDetail">
                <div class="assertDetailHeader">Failure Details:</div>
                <xsl:for-each select="$lineAssertResults">
                  <div>Test: <xsl:value-of select="./TEST"/></div>
                </xsl:for-each>
              </div>
            </td>
          </xsl:element>
        </xsl:if>
      </xsl:for-each>
    </table>
  </xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c) 2004-2009. Progress Software Corporation. All rights reserved.

<metaInformation>
  <scenarios/>
  <MapperMetaTag>
    <MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/>
    <MapperBlockPosition></MapperBlockPosition>
    <TemplateContext></TemplateContext>
    <MapperFilter side="source"></MapperFilter>
  </MapperMetaTag>
</metaInformation>
-->