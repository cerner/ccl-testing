package com.cerner.ccltesting.maven.ccl.reports.common;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.maven.reporting.MavenReportException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.cerner.ccltesting.xsl.XslAPI;
import com.cerner.ccltesting.xsl.XslAPIException;

/**
 * POJO for the ccl environment data.
 *
 * @author Fred Eckertson
 *
 */
@SuppressWarnings("javadoc")
public class CclEnvironment {

    private String nodeName = "";
    private String domainName = "";
    private String databaseType = "";
    private String databaseName = "";
    private String databaseVersion = "";
    private String databaseUser = "";
    private String databaseOperatingSystem = "";
    private String cclVersion = "";
    private String operatingSystem = "";
    private String locale = "";
    private String cclUser = "";
    private String osUser = "";
    private String utcIndicator = "";
    private String utcOffset = "";
    private String timeZone = "";
    private String timeZoneIndex = "";
    private String appTimeZoneIndex = "";
    private String cclGroup = "";
    private String frameworkVersion = "";
    private String minimumRequiredCclVersion = "";
    private boolean dataAvailable = false;

    /**
     * Constructor based on an environment xml file.
     *
     * @param environmentXmlFile
     *            The environment xml file.
     * @throws MavenReportException
     *             The exception thrown if something bad happens.
     */
    public CclEnvironment(File environmentXmlFile) throws MavenReportException {
        String environmentXml = "";
        Document environmentDOM = null;
        try {
            if (environmentXmlFile != null && environmentXmlFile.exists()) {
                environmentXml = FileUtils.readFileToString(environmentXmlFile, "utf-8");
            }
        } catch (IOException e) {
            throw new MavenReportException("Failed to read environment.xml file.", e);
        }
        try {
            if (!environmentXml.isEmpty()) {
                environmentDOM = XslAPI.getDocumentFromString(environmentXml);
                Node environmentNode = XslAPI.getXPathNodeList(environmentDOM, "./ENVIRONMENT").item(0);
                nodeName = XslAPI.getNodeXPathValue(environmentNode, "./CURNODE");
                domainName = XslAPI.getNodeXPathValue(environmentNode, "./CURDOMAIN");
                databaseType = XslAPI.getNodeXPathValue(environmentNode, "./CURRDB");
                databaseName = XslAPI.getNodeXPathValue(environmentNode, "./CURRDBNAME");
                databaseVersion = XslAPI.getNodeXPathValue(environmentNode, "./DBVERSION");
                databaseUser = XslAPI.getNodeXPathValue(environmentNode, "./CURRDBUSER");
                databaseOperatingSystem = XslAPI.getNodeXPathValue(environmentNode, "./CURRDBSYS2");
                cclVersion = XslAPI.getNodeXPathValue(environmentNode, "./CCLVER");
                operatingSystem = XslAPI.getNodeXPathValue(environmentNode, "./CURSYS");
                locale = XslAPI.getNodeXPathValue(environmentNode, "./CURLOCALE");
                cclUser = XslAPI.getNodeXPathValue(environmentNode, "./CCLUSER");
                osUser = XslAPI.getNodeXPathValue(environmentNode, "./CURUSER");
                utcIndicator = XslAPI.getNodeXPathValue(environmentNode, "./CURUTC");
                utcOffset = XslAPI.getNodeXPathValue(environmentNode, "./CURUTCDIFF");
                timeZone = XslAPI.getNodeXPathValue(environmentNode, "./CURTIMEZONE");
                timeZoneIndex = XslAPI.getNodeXPathValue(environmentNode, "./CURTIMEZONESYS");
                appTimeZoneIndex = XslAPI.getNodeXPathValue(environmentNode, "./CURTIMEZONEAPP");
                cclGroup = XslAPI.getNodeXPathValue(environmentNode, "./CURGROUP");
                frameworkVersion = XslAPI.getNodeXPathValue(environmentNode, "./FRAMEWORK_VERSION");
                minimumRequiredCclVersion = XslAPI.getNodeXPathValue(environmentNode, "./REQUIRED_CCL");
                dataAvailable = true;
            }
        } catch (XslAPIException e) {
            throw new MavenReportException("Failed to parse environment.xml file.", e);
        }
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getDatabaseType() {
        return databaseType;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getDatabaseVersion() {
        return databaseVersion;
    }

    public String getDatabaseUser() {
        return databaseUser;
    }

    public String getDatabaseOperatingSystem() {
        return databaseOperatingSystem;
    }

    public String getCclVersion() {
        return cclVersion;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getLocale() {
        return locale;
    }

    public String getCclUser() {
        return cclUser;
    }

    public String getOsUser() {
        return osUser;
    }

    public String getUtcIndicator() {
        return utcIndicator;
    }

    public String getUtcOffset() {
        return utcOffset;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getTimeZoneIndex() {
        return timeZoneIndex;
    }

    public String getAppTimeZoneIndex() {
        return appTimeZoneIndex;
    }

    public String getCclGroup() {
        return cclGroup;
    }

    public String getFrameworkVersion() {
        return frameworkVersion;
    }

    public String getMinimumRequiredCclVersion() {
        return minimumRequiredCclVersion;
    }

    public boolean isDataAvailable() {
        return dataAvailable;
    }
}
