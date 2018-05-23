<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="html" indent="yes"/>
  
<xsl:template match="/">
  <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;
</xsl:text>
  <xsl:element name="head">
    <xsl:call-template name="importStyles"/>
    <script>document.createElement('progress');</script>
  </xsl:element>
  <xsl:element name="body">
    <xsl:call-template name="writeReportTitle"/>
    <br/>
    
    <div class="sectionTitle">Test Suite Source Code Coverage</div>
    <div class="sectionDescription">This section displays the aggregate code coverage of all *.inc test programs for each source programs listed. 
                                    A line of code is considered covered if the line was covered by at least one test program in the test suite</div>
    <br/><br/>
    <xsl:call-template name="writeTestSuiteCoverageSection"/>
    <hr/>

    <div class="sectionTitle">Test Case Source Code Coverage</div>
    <div class="sectionDescription">This section displays the code coverage of each *.inc test programs for all source programs listed</div>
    <br/><br/>
    <xsl:for-each select="/coverageSummary/sourcePrograms/sourceProgram">
      <xsl:call-template name="writeSourceProgramCoverageSection">
        <xsl:with-param name="program" select="."/>
      </xsl:call-template>
    </xsl:for-each>
    <hr/>

    <div class="sectionTitle">Test Case Code Coverage</div>
    <div class="sectionDescription">This section displays the code coverage for each *.inc test program gathered during test execution. In most cases 
                                    code coverage of a test program should be at or near 100%. Uncovered code may be unreachable</div>
    <br/><br/>
    <xsl:call-template name="writeTestCaseCoverageSection"/>
    <script src="ccl-coverage-reports/js/progress-polyfill.js"></script>
  </xsl:element>
</xsl:template>

<xsl:template name="importStyles">
  <link rel="StyleSheet" href="ccl-coverage-reports/css/codeCoverageDashboard.css"/>
  <link rel="StyleSheet" href="ccl-coverage-reports/css/progress-polyfill.css"/>
</xsl:template>

<xsl:template name="writeReportTitle">
  <div class="title">CCL Code Coverage Report - Dashboard View</div>
</xsl:template>

<xsl:template name="writeTestSuiteCoverageSection">
  <div class="sectionSubTitle">With %i Include Files</div>
  <table>
    <xsl:call-template name="writeCoverageTableHeader"/>

    <xsl:for-each select="/coverageSummary/sourcePrograms/sourceProgram/withIncludes/coverage/aggregate">
      <xsl:call-template name="writeCoverageTableRow">
        <xsl:with-param name="name" select="../../../name"/>
        <xsl:with-param name="url" select="linkURL"/>
        <xsl:with-param name="lines" select="../../totalLines"/>
        <xsl:with-param name="coveredLines" select="covered"/>
        <xsl:with-param name="notCoveredLines" select="notCovered"/>
      </xsl:call-template>
    </xsl:for-each>
  </table>

  <br/>
  
  <div class="sectionSubTitle">Without %i Include Files</div>
  <table>
    <xsl:call-template name="writeCoverageTableHeader"/>

    <xsl:for-each select="/coverageSummary/sourcePrograms/sourceProgram/withoutIncludes/coverage/aggregate">
      <xsl:call-template name="writeCoverageTableRow">
        <xsl:with-param name="name" select="../../../name"/>
        <xsl:with-param name="url" select="linkURL"/>
        <xsl:with-param name="lines" select="../../totalLines"/>
        <xsl:with-param name="coveredLines" select="covered"/>
        <xsl:with-param name="notCoveredLines" select="notCovered"/>
      </xsl:call-template>
    </xsl:for-each>
  </table>
  
  <h5><i>*Code coverage percentage is calculated by dividing the covered lines by the sum of the covered and not covered lines. Not executable lines are not
          factored into code coverage calculations...</i></h5>
</xsl:template>

<xsl:template name="writeSourceProgramCoverageSection">
  <xsl:param name="program"/>
  
  <div class="sectionSubTitle"><xsl:value-of select="$program/name"/> - With %i Include Files</div>
  <table>
    <xsl:call-template name="writeCoverageTableHeader">
      <xsl:with-param name="h1Text" select="'Test Case Name'"/>
    </xsl:call-template>

    <xsl:for-each select="$program/withIncludes/coverage/tests/test">
      <xsl:call-template name="writeCoverageTableRow">
        <xsl:with-param name="name" select="name"/>
        <xsl:with-param name="url" select="linkURL"/>
        <xsl:with-param name="lines" select="../../../totalLines"/>
        <xsl:with-param name="coveredLines" select="covered"/>
        <xsl:with-param name="notCoveredLines" select="notCovered"/>
      </xsl:call-template>
    </xsl:for-each>
  </table>

  <br/>
  
  <div class="sectionSubTitle"><xsl:value-of select="$program/name"/> - Without %i Include Files</div>
  <table>
    <xsl:call-template name="writeCoverageTableHeader">
      <xsl:with-param name="h1Text" select="'Test Program'"/>
    </xsl:call-template>

    <xsl:for-each select="$program/withoutIncludes/coverage/tests/test">
      <xsl:call-template name="writeCoverageTableRow">
        <xsl:with-param name="name" select="name"/>
        <xsl:with-param name="url" select="linkURL"/>
        <xsl:with-param name="lines" select="../../../totalLines"/>
        <xsl:with-param name="coveredLines" select="covered"/>
        <xsl:with-param name="notCoveredLines" select="notCovered"/>
      </xsl:call-template>
    </xsl:for-each>
  </table>
  
  <h5><i>*Code coverage percentage is calculated by dividing the covered lines by the sum of the covered and not covered lines. Not executable lines are not
          factored into code coverage calculations...</i></h5>
</xsl:template>

<xsl:template name="writeTestCaseCoverageSection">
    <table>
      <xsl:call-template name="writeCoverageTableHeader">
        <xsl:with-param name="h1Text" select="'Test Program'"/>
      </xsl:call-template>

      <xsl:for-each select="/coverageSummary/testPrograms/testProgram/coverage">
        <xsl:call-template name="writeCoverageTableRow">
          <xsl:with-param name="name" select="../name"/>
          <xsl:with-param name="url" select="linkURL"/>
          <xsl:with-param name="lines" select="../totalLines"/>
          <xsl:with-param name="coveredLines" select="covered"/>
          <xsl:with-param name="notCoveredLines" select="notCovered"/>
        </xsl:call-template>
      </xsl:for-each>
    </table>
    <h5><i>*Code coverage percentage is calculated by dividing the covered lines by the sum of the covered and not covered lines. Not executable lines are not
          factored into code coverage calculations...</i></h5>
</xsl:template>

<xsl:template name="writeCoverageTableHeader">
  <xsl:param name="h1Text" select="'Source Program'"/>

  <th><xsl:value-of select="$h1Text"/></th>
  <th>Lines of Code</th>
  <th>Covered Lines</th>
  <th>Not Covered Lines</th>
  <th>Not Executable Lines</th>
  <th>Total Code Coverage*</th>
</xsl:template>

<xsl:template name="writeCoverageTableRow">
  <xsl:param name="name"/>
  <xsl:param name="url"/>
  <xsl:param name="lines"/>
  <xsl:param name="coveredLines"/>
  <xsl:param name="notCoveredLines"/>
  
  <xsl:variable name ="denominator" select="$coveredLines + $notCoveredLines"></xsl:variable>

  <tr>
    <td>
      <xsl:element name="a">
        <xsl:attribute name="href"><xsl:value-of select="$url"/></xsl:attribute>

        <xsl:value-of select="$name"/>
      </xsl:element>
    </td>
    <td><xsl:value-of select="$lines"/></td>
    <td><xsl:value-of select="$coveredLines"/></td>
    <td><xsl:value-of select="$notCoveredLines"/></td>
    <td><xsl:value-of select="$lines - $coveredLines - $notCoveredLines"/></td>
    <td>
      <xsl:choose>
        <xsl:when test="$denominator!=0">
          <xsl:call-template name="createPercentageBar">
            <xsl:with-param name="barWidthInPixels" select="100"/>
            <xsl:with-param name="percent" select="$coveredLines div ($denominator)"/>
          </xsl:call-template>
        </xsl:when>
        <xsl:otherwise>
          <xsl:call-template name="createPercentageBar">
            <xsl:with-param name="barWidthInPixels" select="100"/>
            <xsl:with-param name="percent" select="0"/>
          </xsl:call-template>
        </xsl:otherwise>
      </xsl:choose>
    </td>
  </tr>
</xsl:template>

<xsl:template name="createPercentageBar">
  <xsl:param name="percent"/>
  <xsl:param name="barWidthInPixels"/>

  <xsl:element name="label">
  <xsl:attribute name="class">progress-label</xsl:attribute>
  <xsl:value-of select="format-number($percent , '00.00%')"/>
  </xsl:element>
  <xsl:element name="progress">
  <xsl:attribute name="max">1</xsl:attribute>
  <xsl:attribute name="value"><xsl:value-of select="$percent"/></xsl:attribute>
  </xsl:element>
</xsl:template>

</xsl:stylesheet>