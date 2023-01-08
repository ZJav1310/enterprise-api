package com.enterpriseproject.enterpriseapi.APIControllers;

import Utils.JSONReader;
import Utils.Reader;
import Utils.TextReader;
import Utils.XMLReader;

// TODO: Probably rename this to factory?
public class FormatUtility {

    private Reader reader;
    private String contentType;

    public FormatUtility(FormatType formatType){
        setReader(formatType);
    }
    public FormatUtility() {
        setReader(FormatType.JSON); // Set it to JSON by default if nothing is given.
    }

    public Reader getReader() {
        return reader;
    }

    public String serialise(Object o){
        return reader.serialiseObject(o);
    }

    public Object deserialise(String i, Class c){
        return reader.deserialiseObject(i, c);
    }

    public String getContentType(){
        return this.contentType;
    }

//    public void setReader(Reader reader) {
//        this.reader = reader;
//    }

    public void setReader(FormatType formatType) {
        switch (formatType) {
            case XML:
                reader = new XMLReader();
                contentType = "application/xml";
                break;
            case TEXT:
                reader = new TextReader();
                contentType = "text/html;charset=UTF-8";
                break;
            case JSON:
                reader = new JSONReader();
                contentType = "application/json";
                break;
        }
    }

    public void setReader(String formatType) {
        switch (formatType) {
            case "application/xml":
                reader = new XMLReader();
                contentType = "application/xml";
                break;
            case "text/html":
                reader = new TextReader();
                contentType = "text/html;charset=UTF-8";
                break;
            case "application/json":
                reader = new JSONReader();
                contentType = "application/json";
                break;
        }
    }
}
