package com.cerner.ccl.analysis.engine.j4ccl;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.cerner.ccl.analysis.exception.TranslationException;
import com.cerner.ccl.j4ccl.CclExecutor;
import com.cerner.ccl.j4ccl.record.DynamicRecordList;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.Structure;
import com.cerner.ccl.j4ccl.record.StructureBuilder;
import com.cerner.ccl.j4ccl.record.factory.RecordFactory;
import com.cerner.ftp.Downloader;
import com.cerner.ftp.data.factory.FileRequestFactory;
import com.cerner.ftp.sftp.SftpDownloader;

/**
 * A translator that translate scripts into XML.
 * 
 * @author Joshua Hyde
 * 
 */

public class ScriptTranslator {
    private final FtpProductProvider productProvider;

    /**
     * Create a translator.
     * 
     * @param productProvider
     *            An {@link FtpProductProvider} used to provide sufficient information to download files from the remote
     *            server.
     * @throws IllegalArgumentException
     *             If the given product provider is {@code null}.
     */
    public ScriptTranslator(final FtpProductProvider productProvider) {
        if (productProvider == null)
            throw new IllegalArgumentException("FTP product provider cannot be null.");

        this.productProvider = productProvider;
    }

    /**
     * Translate scripts into XML.
     * 
     * @param programNames
     *            A {@link Collection} of {@link String} objects that are the names of the CCL programs to be translated
     *            to XML.
     * @return A {@link Map}; the keys are the program names that were translated successfully, and the values are the
     *         XML forms of each file.
     * @throws IllegalArgumentException
     *             If any of the given collection is {@code null}.
     */
    public Map<String, String> getTranslations(final Collection<String> programNames) {
        if (programNames == null)
            throw new IllegalArgumentException("Program names cannot be null.");

        final Record request = RecordFactory.create("translateRequest", getRequestStructure());
        final Record reply = RecordFactory.create("translateReply", getReplyStructure());

        final CclExecutor executor = CclExecutor.getExecutor();
        for (final String programName : programNames)
            request.getDynamicList("programs").addItem().setVC("program_name", programName);
        executor.addScriptExecution("ccl_xml_translator").withReplace("reply", reply).withReplace("request", request)
                .commit();
        executor.execute();

        if (!reply.getRecord("status_data").getChar("status").trim().equalsIgnoreCase("S"))
            throw new TranslationException(
                    reply.getRecord("status_data").getList("subeventstatus").get(0).getVC("TargetObjectValue"));

        final DynamicRecordList programsList = reply.getDynamicList("programs");
        final Map<String, String> translations = new HashMap<String, String>(programsList.getSize());
        for (final Record program : programsList) {
            final File localDestination = createTempFile();
            final Downloader downloader = SftpDownloader.createDownloader(productProvider.getProduct());
            downloader.download(Collections.singleton(FileRequestFactory
                    .create(URI.create(program.getVC("translation_xml_file")), localDestination.toURI())));

            translations.put(program.getVC("program_name"), readFile(localDestination));
        }

        return translations;
    }

    /**
     * Get the structure of the request to the XML translation script.
     * 
     * @return A {@link Structure} representing the structure of the request to be submitted to the CCL program.
     */
    protected Structure getRequestStructure() {
        final Structure requestProgramsListStructure = StructureBuilder.getBuilder().addVC("program_name").build();
        return StructureBuilder.getBuilder().addDynamicList("programs", requestProgramsListStructure).build();
    }

    /**
     * Get the structure of the reply from the XML translation script.
     * 
     * @return A {@link Structure} representing the structure of the reply from the XML translation program.
     */
    protected Structure getReplyStructure() {
        final Structure replyProgramsListStructure = StructureBuilder.getBuilder().addVC("program_name")
                .addVC("translation_xml_file").build();
        return StructureBuilder.getBuilder().addDynamicList("programs", replyProgramsListStructure).addStatusData()
                .build();
    }

    /**
     * Create a temporary file.
     * 
     * @return A {@link File} reference to a temporary file.
     */
    protected File createTempFile() {
        try {
            return File.createTempFile("analysisEngine-analyze", null);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to create temporary file.", e);
        }
    }

    /**
     * Read a file to a string.
     * 
     * @param file
     *            A {@link File} reference to the file to be read.
     * @return A {@link String} object representing the textual contents of the given file.
     */
    protected String readFile(final File file) {
        // TODO: verify that this is the encoding used by CCL
        try {
            return FileUtils.readFileToString(file, "utf-8");
        } catch (final IOException e) {
            throw new RuntimeException("Failed to read file " + file, e);
        }
    }
}
