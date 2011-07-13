/**
 * OapsAPIPort.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OapsAPI_pkg;

public interface OapsAPIPort extends java.rmi.Remote {
    public java.lang.String startJob(OapsAPI_pkg.Login login, byte[] data, java.lang.String lang) throws java.rmi.RemoteException;
    public OapsAPI_pkg.ReportResult getReport(OapsAPI_pkg.Login login, java.lang.String jobid) throws java.rmi.RemoteException;
    public OapsAPI_pkg.ReportResult getNextReport(OapsAPI_pkg.Login login) throws java.rmi.RemoteException;
    public boolean deleteJob(OapsAPI_pkg.Login login, java.lang.String jobid) throws java.rmi.RemoteException;
}
