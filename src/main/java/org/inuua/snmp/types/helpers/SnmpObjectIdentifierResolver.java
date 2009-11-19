package org.inuua.snmp.types.helpers;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import org.inuua.util.ListHelpers;

public class SnmpObjectIdentifierResolver {

    private final String[] dottedStrings;
    private final String[] humanizedStrings;

    private SnmpObjectIdentifierResolver(String[] dottedStrings, String[] humanizedStrings) {
        this.dottedStrings = dottedStrings;
        this.humanizedStrings = humanizedStrings;
    }

    public static SnmpObjectIdentifierResolver newFromDottedStringAndHumanNamesArrays(String[] dottedStrings, String[] humanizedStrings) {
        return new SnmpObjectIdentifierResolver(dottedStrings, humanizedStrings);
    }

    public String fromDottedRepresentation(String dottedRepresentation) {

        List<String> partList = Arrays.asList(dottedRepresentation.split(Pattern.quote(".")));

        for (int len = partList.size(); len > 0; len--) {
            int retIdx = Arrays.binarySearch(dottedStrings, ListHelpers.implode(partList.subList(0, len), "."));
            if (retIdx >= 0) {
                String ret = humanizedStrings[retIdx];
                if (len == partList.size()) {
                    return ret;
                } else {
                    return ret + "." + ListHelpers.implode(partList.subList(len, partList.size()), ".");
                }
            }
        }
        return dottedRepresentation;
    }
}
