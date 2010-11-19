package de.dini.oanetzwerk.oaipmh;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class OAIPMHtype
{
	private String schemaLocation = "http://www.openarchives.org/OAI/2.0/ http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd";
	
    private Date responseDate;
    private RequestType request;
    private int choiceSelect = -1;
    private static final int ERROR_CHOICE = 0;
    private static final int IDENTIFY_CHOICE = 1;
    private static final int LIST_METADATA_FORMATS_CHOICE = 2;
    private static final int LIST_SETS_CHOICE = 3;
    private static final int GET_RECORD_CHOICE = 4;
    private static final int LIST_IDENTIFIERS_CHOICE = 5;
    private static final int LIST_RECORDS_CHOICE = 6;
    private List<OAIPMHErrorType> errorList = new ArrayList<OAIPMHErrorType>();
    private IdentifyType identify;
    private ListMetadataFormatsType listMetadataFormats;
    private ListSetsType listSets;
    private GetRecordType getRecord;
    private ListIdentifiersType listIdentifiers;
    private ListRecordsType listRecords;

    
    
    
    protected OAIPMHtype() {
		super();
	}

	public OAIPMHtype(RequestType request) {
		super();
		this.responseDate = new Date();
		this.request = request;
	}

	/** 
     * Get the 'responseDate' element value.
     * 
     * @return value
     */
    public Date getResponseDate() {
        return responseDate;
    }

    /** 
     * Set the 'responseDate' element value.
     * 
     * @param responseDate
     */
    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    /** 
     * Get the 'request' element value.
     * 
     * @return value
     */
    public RequestType getRequest() {
        return request;
    }

    /** 
     * Set the 'request' element value.
     * 
     * @param request
     */
    public void setRequest(RequestType request) {
        this.request = request;
    }

    private void setChoiceSelect(int choice) {
        if (choiceSelect == -1) {
            choiceSelect = choice;
        } else if (choiceSelect != choice) {
            throw new IllegalStateException(
                    "Need to call clearChoiceSelect() before changing existing choice");
        }
    }

    /** 
     * Clear the choice selection.
     */
    public void clearChoiceSelect() {
        choiceSelect = -1;
    }

    /** 
     * Check if Errors is current selection for choice.
     * 
     * @return <code>true</code> if selection, <code>false</code> if not
     */
    public boolean ifError() {
        return choiceSelect == ERROR_CHOICE;
    }

    /** 
     * Get the list of 'error' element items.
     * 
     * @return list
     */
    public List<OAIPMHErrorType> getErrors() {
        return errorList;
    }

    /** 
     * Set the list of 'error' element items.
     * 
     * @param list
     */
    public void setErrors(List<OAIPMHErrorType> list) {
        setChoiceSelect(ERROR_CHOICE);
        errorList = list;
    }

    /** 
     * Check if Identify is current selection for choice.
     * 
     * @return <code>true</code> if selection, <code>false</code> if not
     */
    public boolean ifIdentify() {
        return choiceSelect == IDENTIFY_CHOICE;
    }

    /** 
     * Get the 'Identify' element value.
     * 
     * @return value
     */
    public IdentifyType getIdentify() {
        return identify;
    }

    /** 
     * Set the 'Identify' element value.
     * 
     * @param identify
     */
    public void setIdentify(IdentifyType identify) {
        setChoiceSelect(IDENTIFY_CHOICE);
        this.identify = identify;
    }

    /** 
     * Check if ListMetadataFormats is current selection for choice.
     * 
     * @return <code>true</code> if selection, <code>false</code> if not
     */
    public boolean ifListMetadataFormats() {
        return choiceSelect == LIST_METADATA_FORMATS_CHOICE;
    }

    /** 
     * Get the 'ListMetadataFormats' element value.
     * 
     * @return value
     */
    public ListMetadataFormatsType getListMetadataFormats() {
        return listMetadataFormats;
    }

    /** 
     * Set the 'ListMetadataFormats' element value.
     * 
     * @param listMetadataFormats
     */
    public void setListMetadataFormats(
            ListMetadataFormatsType listMetadataFormats) {
        setChoiceSelect(LIST_METADATA_FORMATS_CHOICE);
        this.listMetadataFormats = listMetadataFormats;
    }

    /** 
     * Check if ListSets is current selection for choice.
     * 
     * @return <code>true</code> if selection, <code>false</code> if not
     */
    public boolean ifListSets() {
        return choiceSelect == LIST_SETS_CHOICE;
    }

    /** 
     * Get the 'ListSets' element value.
     * 
     * @return value
     */
    public ListSetsType getListSets() {
        return listSets;
    }

    /** 
     * Set the 'ListSets' element value.
     * 
     * @param listSets
     */
    public void setListSets(ListSetsType listSets) {
        setChoiceSelect(LIST_SETS_CHOICE);
        this.listSets = listSets;
    }

    /** 
     * Check if GetRecord is current selection for choice.
     * 
     * @return <code>true</code> if selection, <code>false</code> if not
     */
    public boolean ifGetRecord() {
        return choiceSelect == GET_RECORD_CHOICE;
    }

    /** 
     * Get the 'GetRecord' element value.
     * 
     * @return value
     */
    public GetRecordType getGetRecord() {
        return getRecord;
    }

    /** 
     * Set the 'GetRecord' element value.
     * 
     * @param getRecord
     */
    public void setGetRecord(GetRecordType getRecord) {
        setChoiceSelect(GET_RECORD_CHOICE);
        this.getRecord = getRecord;
    }

    /** 
     * Check if ListIdentifiers is current selection for choice.
     * 
     * @return <code>true</code> if selection, <code>false</code> if not
     */
    public boolean ifListIdentifiers() {
        return choiceSelect == LIST_IDENTIFIERS_CHOICE;
    }

    /** 
     * Get the 'ListIdentifiers' element value.
     * 
     * @return value
     */
    public ListIdentifiersType getListIdentifiers() {
        return listIdentifiers;
    }

    /** 
     * Set the 'ListIdentifiers' element value.
     * 
     * @param listIdentifiers
     */
    public void setListIdentifiers(ListIdentifiersType listIdentifiers) {
        setChoiceSelect(LIST_IDENTIFIERS_CHOICE);
        this.listIdentifiers = listIdentifiers;
    }

    /** 
     * Check if ListRecords is current selection for choice.
     * 
     * @return <code>true</code> if selection, <code>false</code> if not
     */
    public boolean ifListRecords() {
        return choiceSelect == LIST_RECORDS_CHOICE;
    }

    /** 
     * Get the 'ListRecords' element value.
     * 
     * @return value
     */
    public ListRecordsType getListRecords() {
        return listRecords;
    }

    /** 
     * Set the 'ListRecords' element value.
     * 
     * @param listRecords
     */
    public void setListRecords(ListRecordsType listRecords) {
        setChoiceSelect(LIST_RECORDS_CHOICE);
        this.listRecords = listRecords;
    }
}
