package de.dini.oanetzwerk.oaipmh;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;


public class OAIPMHError {

	private OAIPMHErrorcodeType error;
	private String message;

	
	public OAIPMHError (OAIPMHErrorcodeType error) {
		
		this.error = error;
	}
	
	public OAIPMHError (OAIPMHErrorcodeType error, String message) {
		
		this.error = error;
		this.message = message;
	}
    
    
    @Override
	public String toString ( ) {
		
    	Writer writer = new StringWriter();
    	
		try {
			IBindingFactory oaipmhFactory = BindingDirectory.getFactory(OAIPMHtype.class);
            IMarshallingContext mctx = oaipmhFactory.createMarshallingContext();
            mctx.setIndent(2);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            mctx.setOutput(bos, "UTF-8");
            mctx.marshalDocument(createErrorMsg(), "UTF-8", false, writer);
            
			return writer.toString();
		} catch (JiBXException e) {
			
			System.err.println(e.getLocalizedMessage ( ) +  e);
		}
		return null;
	}

	/**
	 * @return 
	 * 
	 */
	
	private OAIPMHtype createErrorMsg ( ) {
		
		OAIPMHErrorType oaiError = new OAIPMHErrorType();
		
		switch (this.error) {
			case BAD_ARGUMENT:
				oaiError.setCode (OAIPMHErrorcodeType.BAD_ARGUMENT);
				oaiError.setValue ("One argument in the request is not valid.");
				break;
			
			case BAD_VERB:
				oaiError.setCode (OAIPMHErrorcodeType.BAD_VERB);
				oaiError.setValue ("The requested verb is illegal.");
				break;
			
			case BAD_RESUMPTION_TOKEN:
				oaiError.setCode (OAIPMHErrorcodeType.BAD_RESUMPTION_TOKEN);
				oaiError.setValue ("The requested resumptionToken does not exist or has already expired.");
				break;
			
			case CANNOT_DISSEMINATE_FORMAT:
				oaiError.setCode (OAIPMHErrorcodeType.CANNOT_DISSEMINATE_FORMAT);
				oaiError.setValue ("The requested metadata format is not supported.");
				break;
			
			case ID_DOES_NOT_EXIST:
				oaiError.setCode (OAIPMHErrorcodeType.ID_DOES_NOT_EXIST);
				oaiError.setValue ("The requested identifier does not exist.");
				break;
			
			case NO_METADATA_FORMATS:
				oaiError.setCode (OAIPMHErrorcodeType.NO_METADATA_FORMATS);
				oaiError.setValue ("There is no metadata format supplied within your request.");
				break;
			
			case NO_RECORDS_MATCH:
				oaiError.setCode (OAIPMHErrorcodeType.NO_RECORDS_MATCH);
				oaiError.setValue ("There are no records matching the request.");
				break;
			
			case NO_SET_HIERARCHY:
				oaiError.setCode (OAIPMHErrorcodeType.NO_SET_HIERARCHY);
				break;
			
			default:
				oaiError.setValue ("Sorry! I don't know what happened and I don't know what to do. The only thing I know is that YOU should do something completely different!");
				break;
		}
		
		// overwrite default error message if there has been one explicitly defined 
		if (message != null)
		{
			oaiError.setValue(message);
		}
		
		RequestType reqType = new RequestType();
		OAIPMHtype oaipmhMsg = new OAIPMHtype(reqType);
		oaipmhMsg.getErrors().add (oaiError);
		
		return oaipmhMsg;
	}
}
