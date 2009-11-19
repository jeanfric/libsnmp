package org.inuua.snmp;

import java.io.IOException;

public interface IOExceptionHandler {

    void handleIOException(IOException ex);
}
