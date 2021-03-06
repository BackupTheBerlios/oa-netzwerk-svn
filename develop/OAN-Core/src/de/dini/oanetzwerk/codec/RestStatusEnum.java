package de.dini.oanetzwerk.codec;

public enum RestStatusEnum {
	OK,
	REST_XML_ENCODING_ERROR,
	REST_XML_DECODING_ERROR,
	SQL_ERROR,
	NO_OBJECT_FOUND_ERROR,
	CLASS_NOT_FOUND_ERROR,
	CLASS_COULD_NOT_BE_INSTANTIATED_ERROR,
	ILLEGAL_ACCESS_ERROR,
	IO_ERROR,
	NOT_ENOUGH_PARAMETERS_ERROR,
	NOT_IMPLEMENTED_ERROR,
	INCOMPLETE_ENTRYSET_ERROR,
	UNKNOWN_ERROR, 
	WRONG_STATEMENT, 
	WRONG_PARAMETER, 
	NO_RAWDATA_FOUND,
	SQL_WARNING,
	UNSUPPORTED_ENCODING,
	AGGREGATION_WARNING,
	NO_METADATA_AVAILABLE
}
