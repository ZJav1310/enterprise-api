package com.enterpriseproject.enterpriseapi.APIControllers;

import Utils.JSONReader;
import Utils.TextReader;
import Utils.XMLReader;
import Utils.Reader;
@Deprecated
public enum FormatType {
    JSON(JSONReader.getInstance()),
    TEXT(TextReader.getInstance()),
    XML(XMLReader.getInstance());

    private final Reader reader;
    FormatType(Reader reader) {
        this.reader = reader;
    }

    public Reader getReader(){
        return this.reader;
    }
    public String getMimeType(){
        return this.reader.getAcceptedMimeType();
    }
}