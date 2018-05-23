package com.cerner.ccl.cdoc.mojo.data;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import com.cerner.ccl.cdoc.AbstractBeanUnitTest;

/**
 * Unit tests for {@link Documentation}.
 *
 * @author Joshua Hyde
 *
 */
@SuppressWarnings("unused")
public class DocumentationTest extends AbstractBeanUnitTest<Documentation> {

    /**
     * Pretest initialization.
     */
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test the construction of documentation for a {@code .INC} file.
     */
    @Test
    public void testConstructIncludeFile() {
        final File includeFile = new File("target/include.inc");
        final Documentation doc = new Documentation(includeFile);
        assertThat(doc.getObjectType()).isEqualTo(ObjectType.INC);
        assertThat(doc.getObjectName()).isEqualTo(includeFile.getName());
        assertThat(doc.getObjectFilename()).isEqualTo(includeFile.getName());
        assertThat(doc.getDestinationFilename()).isEqualTo("include-inc.html");
        assertThat(doc.getSourceFile()).isEqualTo(includeFile);
    }

    /**
     * Construction with a {@code null} source file should fail.
     */
    @Test
    public void testConstructNullSourceFile() {
        expect(IllegalArgumentException.class);
        expect("Source file cannot be null.");
        new Documentation(null);
    }

    /**
     * Test the construction of documentation for a {@code .PRG} file.
     */
    @Test
    public void testConstructScriptFile() {
        final File scriptFile = new File("target/script.prg");
        final Documentation doc = new Documentation(scriptFile);
        assertThat(doc.getObjectType()).isEqualTo(ObjectType.PRG);
        assertThat(doc.getObjectName()).isEqualTo("script");
        assertThat(doc.getObjectFilename()).isEqualTo(scriptFile.getName());
        assertThat(doc.getDestinationFilename()).isEqualTo("script-prg.html");
        assertThat(doc.getSourceFile()).isEqualTo(scriptFile);
    }

    /**
     * Test the construction of documentation for a {@code .SUB} file.
     */
    @Test
    public void testConstructSubFile() {
        final File subFile = new File("target/include.sub");
        final Documentation doc = new Documentation(subFile);
        assertThat(doc.getObjectType()).isEqualTo(ObjectType.SUB);
        assertThat(doc.getObjectName()).isEqualTo(subFile.getName());
        assertThat(doc.getObjectFilename()).isEqualTo(subFile.getName());
        assertThat(doc.getDestinationFilename()).isEqualTo("include-sub.html");
        assertThat(doc.getSourceFile()).isEqualTo(subFile);
    }

    /**
     * Construction of a documentation object with an unrecognized file extension should fail.
     */
    @Test
    public void testConstructUnrecognizedFiletype() {
        final File textFile = new File("target/unrecognized.text");
        expect(IllegalArgumentException.class);
        expect("Unrecognized file type: " + textFile.getAbsolutePath());
        new Documentation(textFile);
    }

    /**
     * Two documentation objects with different source files should be inequal.
     */
    @Test
    public void testEqualsDifferentSourceFile() {
        final File firstFile = new File("target/file.prg");
        final File secondFile = new File("target/other/file.prg");

        final Documentation firstDoc = new Documentation(firstFile);
        final Documentation secondDoc = new Documentation(secondFile);

        assertThat(firstDoc).isNotEqualTo(secondDoc);
        assertThat(secondDoc).isNotEqualTo(firstDoc);
    }

    /**
     * Test the retrieval of the source file.
     */
    @Test
    public void testGetSourceFile() {
        final File scriptFile = new File("target/script.prg");
        assertThat(new Documentation(scriptFile).getSourceFile()).isEqualTo(scriptFile);
    }

    @Override
    protected Documentation getBean() {
        final String sourceFilename = "file.prg";
        final String sourceFileAbsolutePath = "/home/" + sourceFilename;

        final File sourceFile = mock(File.class);
        when(sourceFile.getName()).thenReturn(sourceFilename);
        when(sourceFile.getAbsolutePath()).thenReturn(sourceFileAbsolutePath);

        return new Documentation(sourceFile);
    }

    @Override
    protected Documentation newBeanFrom(final Documentation otherBean) {
        return new Documentation(otherBean.getSourceFile());
    }

}
