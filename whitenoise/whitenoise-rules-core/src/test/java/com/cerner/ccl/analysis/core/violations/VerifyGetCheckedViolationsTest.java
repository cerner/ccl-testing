package com.cerner.ccl.analysis.core.violations;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.tools.SPInterface;
import org.apache.commons.discovery.tools.Service;
import org.apache.commons.lang.StringUtils;
import org.jdom2.Document;
import org.junit.Test;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.jdom.JdomAnalysisRule.Delegate;

/**
 * Unit test to ensure that getCheckedViolations is implemented for each rule
 *
 * @author Jeff Wiedemann
 */

public class VerifyGetCheckedViolationsTest {

    /**
     * This unit test will iterate through all of the registered rules and call the getCheckedViolations routine. It
     * will then scan through the classes in the .violations package looking for any violation classes and ensure that
     * each coded violation is returned by some rules getCheckedViolations. This guarantees that as people add rules and
     * violations to the code they are ensuring that they add that violation to getCheckedViolations routine. If this
     * unit tests fails it means that you have added a rule or new violation to an existing rule and have not returned
     * that violation as a 'checked' violation of that rule. It needs to be added to the getCheckedViolation subroutine
     * so that the report can display a list of all violations that were checked as part of the whitenoise run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testGetCheckedViolationsVerification() {
        final ClassLoaders classLoaders = new ClassLoaders();
        final Class<Document>[] classes = new Class[] { Document.class };
        final Document[] objects = new Document[] { new Document() };
        classLoaders.put(Document.class.getClassLoader());
        final Enumeration<Delegate> delegates = Service.providers(new SPInterface(Delegate.class, classes, objects),
                classLoaders);
        final Set<Class<?>> allCheckedViolations = new HashSet<Class<?>>();

        // Add all checked violations to the set
        while (delegates.hasMoreElements()) {
            for (Violation v : delegates.nextElement().getCheckedViolations()) {
                allCheckedViolations.add(v.getClass());
            }
        }

        // Loop through each class in the violation package and check to see if it's a violation
        // If it is ensure that it exists in the getCheckedViolations list
        File violationsDir = new File(getClassLocation(InfiniteLoopViolation.class).toString().replace("file:", ""));
        for (String fileName : violationsDir.list()) {
            try {
                Class<?> o = Class.forName("com.cerner.ccl.analysis.core.violations." + fileName.replace(".class", ""));

                // Loop through the classes methods looking for getViolationId which pretty much makes it a violation
                for (Method method : o.getMethods()) {
                    if (method.getName().equalsIgnoreCase("getViolationId")) {
                        // Now that we know this class is a violation, ensure that it exists somewhere in the list of
                        // all checked violations
                        assertThat(allCheckedViolations).contains(o);
                    }
                }
            } catch (ClassNotFoundException e) {
            } catch (SecurityException e) {
            }
        }
    }

    private static URL getClassLocation(final Class<?> c) {
        URL url = c.getResource(c.getSimpleName() + ".class");
        if (url == null) {
            return null;
        }
        String s = url.toExternalForm();
        // s most likely ends with a /, then the full class name with . replaced
        // with /, and .class. Cut that part off if present. If not also check
        // for backslashes instead. If that's also not present just return null

        String end = "/" + c.getSimpleName().replaceAll("\\.", "/") + ".class";
        if (s.endsWith(end)) {
            s = s.substring(0, s.length() - end.length());
        } else {
            end = end.replaceAll("/", "\\");
            if (s.endsWith(end)) {
                s = s.substring(0, s.length() - end.length());
            } else {
                return null;
            }
        }
        // s is now the URL of the location, but possibly with jar: in front and
        // a trailing !
        if (StringUtils.startsWith(s, "jar:") && s.endsWith("!")) {
            s = s.substring(4, s.length() - 1);
        }
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
