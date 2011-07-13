/**
 * OapsAPI.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OapsAPI_pkg;

public interface OapsAPI extends javax.xml.rpc.Service {
    public java.lang.String getOapsAPIPortAddress();

    public OapsAPI_pkg.OapsAPIPort getOapsAPIPort() throws javax.xml.rpc.ServiceException;

    public OapsAPI_pkg.OapsAPIPort getOapsAPIPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
