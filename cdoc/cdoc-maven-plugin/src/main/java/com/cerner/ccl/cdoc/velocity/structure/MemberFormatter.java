package com.cerner.ccl.cdoc.velocity.structure;

import org.apache.maven.reporting.MavenReportException;

import com.cerner.ccl.parser.data.record.RecordStructureMember;

/**
 * Definition of an object used to format record structure members.
 * 
 * @author Joshua Hyde
 * 
 * @param <M>
 *            The type of record structure member to be formatted.
 */

public interface MemberFormatter<M extends RecordStructureMember> {
    /**
     * Format a record structure member.
     * 
     * @param member
     *            A {@link RecordStructureMember} object to be formatted.
     * @return A {@link String} containing the formatted version of the given structure member.
     * @throws MavenReportException
     *             If any errors occur during the formatting.
     */
    String format(M member) throws MavenReportException;
}
