/**
 * ReportResult.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package OapsAPI_pkg;

public class ReportResult  implements java.io.Serializable {
    private java.lang.String jobid;

    private java.lang.String status;

    private int rating;

    private byte[] file;

    public ReportResult() {
    }

    public ReportResult(
           java.lang.String jobid,
           java.lang.String status,
           int rating,
           byte[] file) {
           this.jobid = jobid;
           this.status = status;
           this.rating = rating;
           this.file = file;
    }


    /**
     * Gets the jobid value for this ReportResult.
     * 
     * @return jobid
     */
    public java.lang.String getJobid() {
        return jobid;
    }


    /**
     * Sets the jobid value for this ReportResult.
     * 
     * @param jobid
     */
    public void setJobid(java.lang.String jobid) {
        this.jobid = jobid;
    }


    /**
     * Gets the status value for this ReportResult.
     * 
     * @return status
     */
    public java.lang.String getStatus() {
        return status;
    }


    /**
     * Sets the status value for this ReportResult.
     * 
     * @param status
     */
    public void setStatus(java.lang.String status) {
        this.status = status;
    }


    /**
     * Gets the rating value for this ReportResult.
     * 
     * @return rating
     */
    public int getRating() {
        return rating;
    }


    /**
     * Sets the rating value for this ReportResult.
     * 
     * @param rating
     */
    public void setRating(int rating) {
        this.rating = rating;
    }


    /**
     * Gets the file value for this ReportResult.
     * 
     * @return file
     */
    public byte[] getFile() {
        return file;
    }


    /**
     * Sets the file value for this ReportResult.
     * 
     * @param file
     */
    public void setFile(byte[] file) {
        this.file = file;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof ReportResult)) return false;
        ReportResult other = (ReportResult) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.jobid==null && other.getJobid()==null) || 
             (this.jobid!=null &&
              this.jobid.equals(other.getJobid()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            this.rating == other.getRating() &&
            ((this.file==null && other.getFile()==null) || 
             (this.file!=null &&
              java.util.Arrays.equals(this.file, other.getFile())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getJobid() != null) {
            _hashCode += getJobid().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        _hashCode += getRating();
        if (getFile() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getFile());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getFile(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

}
