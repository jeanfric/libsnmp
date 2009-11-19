package org.inuua.snmp;

import org.inuua.snmp.types.SnmpMessage;

public interface SnmpConnection {

    public void registerIncomingSnmpMessageHandler(IncomingSnmpMessageHandler msgHandler);

    public void registerIncomingVariableBindingsHandler(IncomingVariableBindingsHandler mibMapHandler);

    public void registerIOExceptionHandler(IOExceptionHandler exceptionHandler);

    public void retrieveAllObjectsStartingFrom(String objectIdentifier);

    public void retrieveOneObject(String objectIdentifier);

    public void sendSnmpMessage(SnmpMessage msg);

    public void unRegisterIncomingSnmpMessageHandler(IncomingSnmpMessageHandler msgHandler);

    public void unRegisterIncomingVariableBindingsHandler(IncomingVariableBindingsHandler mibMapHandler);

    public void unRegisterIOExceptionHandler(IOExceptionHandler exceptionHandler);
}
