package com.enterpriseproject.enterpriseapi.APIControllers;

import Utils.JSONReader;
import Utils.Reader;
import Utils.TextReader;
import Utils.XMLReader;

// TODO: Probably rename this to factory?
public class FormatterFactory {
    private static volatile FormatterFactory instance = null;
    private FormatterFactory(){}
    public static FormatterFactory getInstance(){
        if(instance == null){
            synchronized (FormatterFactory.class){
                if(instance == null){
                    instance = new FormatterFactory();
                }
            }
        }
        return instance;
    }
    private Reader reader;
    private String mediaType;

//    public FormatterFactory(FormatType formatType){
//        setReader(formatType);
//    }
//    public FormatterFactory() {
//        setReader(FormatType.JSON); // Set it to JSON by default if nothing is given.
//    }

    public Reader getReader() {
        return reader;
    }

    public String serialise(Object o){
        return reader.serialiseObject(o);
    }

    public Object deserialise(String i, Class c){
        return reader.deserialiseObject(i, c);
    }

//    public String getMediaType(){
//        return this.mediaType;
//    }

//    public void setReaderType(FormatType formatType) {
//        switch (formatType) {
//            case XML:
//                reader = XMLReader.getInstance();
//                break;
//            case TEXT:
//                reader = TextReader.getInstance();
//                break;
//            case JSON:
//                reader = JSONReader.getInstance();
//                break;
//        }
//    }
//
//    public void setReaderType(String mimeType) {
//        switch (mimeType) {
//            case "application/xml":
//                reader = XMLReader.getInstance();
//                break;
//            case "text/html":
//                reader = TextReader.getInstance();
//                break;
//            case "application/json":
//                reader = JSONReader.getInstance();
//                break;
//        }
//    }
}
