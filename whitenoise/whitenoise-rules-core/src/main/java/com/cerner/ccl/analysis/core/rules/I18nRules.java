package com.cerner.ccl.analysis.core.rules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.cerner.ccl.analysis.core.violations.DuplicateI18nKeyViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * A {@link AnalysisRule} that identifies the following issues with internationalization: 1. Instances where
 * uar_i18nGetMessage accidentally has a duplicate i18n key value
 *
 * @author Jeff Wiedemann
 * @author Albert Ponraj
 */

public class I18nRules extends TimedDelegate {

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public I18nRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Set<Violation> violations = new HashSet<Violation>();
        final Map<String, Element> i18nKeys = new HashMap<String, Element>();
        final List<Element> uarGetMessages = selectNodesByName("CALL.", "[NAME/@text = 'UAR_I18NGETMESSAGE']");

        for (final Element uarGetMessage : uarGetMessages) {
            String i18nKey = uarGetMessage.getChildren().get(2).getAttributeValue("text");
            String i18nText = uarGetMessage.getChildren().get(3).getAttributeValue("text");

            if (i18nKey != null && i18nText != null) {
                // The i18n key has not been added to the collection yet, so there is no violation to add. So add the
                // element to
                // the map and finish
                if (i18nKeys.get(i18nKey) == null) {
                    i18nKeys.put(i18nKey, uarGetMessage.getChildren().get(3));
                    continue;
                }

                // The i18n key has already been added, so check to see if the i18n Text Value matches
                // If not, then the script author has violated uniqueness of the i18n keys
                String existingI18nText = i18nKeys.get(i18nKey).getAttributeValue("text");
                if (!existingI18nText.equalsIgnoreCase(i18nText)) {
                    violations.add(new DuplicateI18nKeyViolation(getLineNumber(uarGetMessage), i18nKey));
                }
            }
        }

        return violations;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
        Set<Violation> violations = new HashSet<Violation>();
        violations.add(new DuplicateI18nKeyViolation(1, "someKey"));
        return violations;
    }
}