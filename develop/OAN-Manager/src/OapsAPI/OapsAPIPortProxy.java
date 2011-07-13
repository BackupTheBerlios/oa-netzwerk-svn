package OapsAPI;

import java.rmi.RemoteException;

import OapsAPI_pkg.Login;
import OapsAPI_pkg.ReportResult;

public class OapsAPIPortProxy implements OapsAPI_pkg.OapsAPIPort {
  private String _endpoint = null;
  private OapsAPI_pkg.OapsAPIPort oapsAPIPort = null;
  
  public OapsAPIPortProxy() {
    _initOapsAPIPortProxy();
  }
  
  public OapsAPIPortProxy(String endpoint) {
    _endpoint = endpoint;
    _initOapsAPIPortProxy();
  }
  
  private void _initOapsAPIPortProxy() {
    try {
      oapsAPIPort = (new OapsAPI_pkg.OapsAPILocator()).getOapsAPIPort();
      if (oapsAPIPort != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)oapsAPIPort)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)oapsAPIPort)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (oapsAPIPort != null)
      ((javax.xml.rpc.Stub)oapsAPIPort)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public OapsAPI_pkg.OapsAPIPort getOapsAPIPort() {
    if (oapsAPIPort == null)
      _initOapsAPIPortProxy();
    return oapsAPIPort;
  }

@Override
public String startJob(Login login, byte[] data, String lang) throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public ReportResult getReport(Login login, String jobid) throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public ReportResult getNextReport(Login login) throws RemoteException {
	// TODO Auto-generated method stub
	return null;
}

@Override
public boolean deleteJob(Login login, String jobid) throws RemoteException {
	// TODO Auto-generated method stub
	return false;
}
  
  
}