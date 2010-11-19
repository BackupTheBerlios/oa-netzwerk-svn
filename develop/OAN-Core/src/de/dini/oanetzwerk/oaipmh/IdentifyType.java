package de.dini.oanetzwerk.oaipmh;

import java.util.ArrayList;
import java.util.List;

/** 
 * Schema fragment(s) for this class:
 * <pre>
 * &lt;xs:complexType xmlns:ns="http://www.openarchives.org/OAI/2.0/" xmlns:xs="http://www.w3.org/2001/XMLSchema" name="IdentifyType">
 *   &lt;xs:sequence>
 *     &lt;xs:element type="xs:string" name="repositoryName"/>
 *     &lt;xs:element type="xs:string" name="baseURL"/>
 *     &lt;xs:element type="ns:protocolVersionType" name="protocolVersion"/>
 *     &lt;xs:element type="xs:string" name="adminEmail" maxOccurs="unbounded"/>
 *     &lt;xs:element type="xs:string" name="earliestDatestamp"/>
 *     &lt;xs:element type="ns:deletedRecordType" name="deletedRecord"/>
 *     &lt;xs:element type="ns:granularityType" name="granularity"/>
 *     &lt;xs:element type="xs:string" name="compression" minOccurs="0" maxOccurs="unbounded"/>
 *     &lt;xs:element type="ns:descriptionType" name="description" minOccurs="0" maxOccurs="unbounded"/>
 *   &lt;/xs:sequence>
 * &lt;/xs:complexType>
 * </pre>
 */
public class IdentifyType
{
    private String repositoryName;
    private String baseURL;
    private ProtocolVersionType protocolVersion;
    private List<String> adminEmailList = new ArrayList<String>();
    private String earliestDatestamp;
    private DeletedRecordType deletedRecord;
    private GranularityType granularity;
    private List<String> compressionList = new ArrayList<String>();
    private List<DescriptionType> descriptionList = new ArrayList<DescriptionType>();

    /** 
     * Get the 'repositoryName' element value.
     * 
     * @return value
     */
    public String getRepositoryName() {
        return repositoryName;
    }

    /** 
     * Set the 'repositoryName' element value.
     * 
     * @param repositoryName
     */
    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    /** 
     * Get the 'baseURL' element value.
     * 
     * @return value
     */
    public String getBaseURL() {
        return baseURL;
    }

    /** 
     * Set the 'baseURL' element value.
     * 
     * @param baseURL
     */
    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }

    /** 
     * Get the 'protocolVersion' element value.
     * 
     * @return value
     */
    public ProtocolVersionType getProtocolVersion() {
        return protocolVersion;
    }

    /** 
     * Set the 'protocolVersion' element value.
     * 
     * @param protocolVersion
     */
    public void setProtocolVersion(ProtocolVersionType protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    /** 
     * Get the list of 'adminEmail' element items.
     * 
     * @return list
     */
    public List<String> getAdminEmails() {
        return adminEmailList;
    }

    /** 
     * Set the list of 'adminEmail' element items.
     * 
     * @param list
     */
    public void setAdminEmails(List<String> list) {
        adminEmailList = list;
    }

    /** 
     * Get the 'earliestDatestamp' element value.
     * 
     * @return value
     */
    public String getEarliestDatestamp() {
        return earliestDatestamp;
    }

    /** 
     * Set the 'earliestDatestamp' element value.
     * 
     * @param earliestDatestamp
     */
    public void setEarliestDatestamp(String earliestDatestamp) {
        this.earliestDatestamp = earliestDatestamp;
    }

    /** 
     * Get the 'deletedRecord' element value.
     * 
     * @return value
     */
    public DeletedRecordType getDeletedRecord() {
        return deletedRecord;
    }

    /** 
     * Set the 'deletedRecord' element value.
     * 
     * @param deletedRecord
     */
    public void setDeletedRecord(DeletedRecordType deletedRecord) {
        this.deletedRecord = deletedRecord;
    }

    /** 
     * Get the 'granularity' element value.
     * 
     * @return value
     */
    public GranularityType getGranularity() {
        return granularity;
    }

    /** 
     * Set the 'granularity' element value.
     * 
     * @param granularity
     */
    public void setGranularity(GranularityType granularity) {
        this.granularity = granularity;
    }

    /** 
     * Get the list of 'compression' element items.
     * 
     * @return list
     */
    public List<String> getCompressions() {
        return compressionList;
    }

    /** 
     * Set the list of 'compression' element items.
     * 
     * @param list
     */
    public void setCompressions(List<String> list) {
        compressionList = list;
    }

    /** 
     * Get the list of 'description' element items.
     * 
     * @return list
     */
    public List<DescriptionType> getDescriptions() {
        return descriptionList;
    }

    /** 
     * Set the list of 'description' element items.
     * 
     * @param list
     */
    public void setDescriptions(List<DescriptionType> list) {
        descriptionList = list;
    }
}
