package com.enterpriseproject.enterpriseapi;

import Models.Film;
import com.enterpriseproject.enterpriseapi.APIControllers.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@WebServlet(name = "FilmAPIServlet", value = "/FilmAPIServlet")
public class FilmAPIServlet extends HttpServlet {
    private final DataRequest dataRequestCommand = new DataRequest();
    private FormatType incomingContentType;
    private FormatType outgoingMediaType;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        /**
         * Checks if "format" parameter is given,
         * Checks if this parameter value is in FormatType enum list
         * If it exists, set the outgoingMediaType with getMimeType()
         */
        if(areParamsValid(request, "format")) {
            if(isFormatValid(getParamValue(request, "format"))){
                this.outgoingMediaType = null; // getContentType returns the Media Type
            }
        }

//        FormatterFactory.getInstance().setReaderType(outgoingMediaType);
//        response.setContentType(FormatType.getReader().getMediaType());
//        response.getWriter().println(getMethodContent(request));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int result = 0;
        StringBuilder output = new StringBuilder();
        Film f = null;
        List<String> parameters = List.of("title", "year", "stars", "director", "review");

        /**
         * Checks if the request URL has the parameters to fulfill the POST request,
         * If it has all the parameters, then create the Film object.
         * <p>
         * If not, check the request body is not null,
         * If true, call method to serialise from the request body.
         */
//        if (areParamsValid(request, parameters)) {
//            f = toFilm(request, parameters);
//
//        } else if (request.getReader() != null) {
//            f = serialiseToFilm(request);
//        }
//
//        setOutgoingContentType(request);
//
//        if (f != null)
//            result = dataRequestCommand.insertFilm(f);
//
//        if (result > 0)
//            output.append(formatter.serialise(f));
//
//        response.setContentType(formatter.getContentType());
//        response.getWriter().println(output);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        defineResponseFormat(request);
//        response.setContentType(formatter.getContentType());
//        response.getWriter().println(deleteMethod(request));
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

//        int result = 0;
//        Film f = null;
//
//        List<String> parameters = List.of("title", "year", "stars", "director", "review");
//
//        StringBuilder output = new StringBuilder();
//
//        if (areParamsValid(request, parameters)) {
//            f = toFilm(request, parameters);
//
//        } else if (request.getReader() != null) {
//            f = serialiseToFilm(request);
//        }
//
//        defineResponseFormat(request);
//
//        if (f != null)
//            result = dataRequestCommand.updateFilm(f);
//
//        if (result > 0)
//            output.append(formatter.serialise(f));
//
//        response.setContentType(formatter.getContentType());
//        response.getWriter().println(output);

    }

    private static boolean areParamsValid(HttpServletRequest request, String key) {
        return request.getParameterMap().containsKey(key);
    }
    private static String getParamValue(HttpServletRequest request, String key){
        return request.getParameter(key);
    }

    private static boolean areParamsValid(HttpServletRequest request, List<String> arrayList) {
        for (String name : arrayList) {
            if (!request.getParameterMap().containsKey(name)) {
                return false;
            }
        }
        return true;
    }

    private FormatType responseContentType (HttpServletRequest request) {
        return FormatType.valueOf(request.getParameter("format").trim().toUpperCase());
    }

    private StringBuilder getMethodContent(HttpServletRequest request) {
        StringBuilder output = new StringBuilder();
//        if (areParamsValid(request, "id")) {
//            int id = Integer.parseInt(request.getParameter("id"));
//            output.append(formatter.serialise(dataRequestCommand.getById(id)));
//
//        } else if (areParamsValid(request, "title")) {
//            for (Film f : dataRequestCommand.searchByTitle(request.getParameter("title"))) {
//                output.append(formatter.serialise(f));
//            }
//        } else {
//            for (Film f : dataRequestCommand.getAll()) {
//                output.append(formatter.serialise(f));
//            }
//        }
        return output;
    }

    private StringBuilder deleteMethod(HttpServletRequest request) {
        StringBuilder output = new StringBuilder();
        if (areParamsValid(request, "id")) {
            int result = dataRequestCommand.deleteFilm(Integer.parseInt(request.getParameter("id")));

            if (result > 0) {
                output.append("Deleted.");
            }
        }
        return output;
    }

    private Film serialiseToFilm(HttpServletRequest request) throws IOException {
//        String requestBody = request.getReader().lines().collect(Collectors.joining());
//        formatter.setReader(request.getContentType());
//        return (Film) formatter.deserialise(requestBody, Film.class);
        return null;
    }

    private Film toFilm(HttpServletRequest request, List<String> list) {
        String title = request.getParameter("title");
        int year = Integer.parseInt(request.getParameter("year"));
        String director = request.getParameter("director");
        String stars = request.getParameter("stars");
        String review = request.getParameter("review");

        if (list.size() == 6) {
            int id = Integer.parseInt(request.getParameter("id"));
            return new Film(id, title, year, director, stars, review);
        } else {
            return new Film(title, year, director, stars, review);
        }
    }

    private boolean isFormatParamRequested(HttpServletRequest request){
        return areParamsValid(request, "format");
    }

//    private boolean isFormatMimeTypeValid(String stringFormat){
//        return Arrays.stream(FormatType.values()).anyMatch( x -> Objects.equals(x.getMimeType(), stringFormat.trim()));
//    }

    private boolean isFormatValid(String stringFormat){
        return Arrays.stream(FormatType.values()).anyMatch( x -> Objects.equals(x.name(), stringFormat.trim().toUpperCase()));
    }


//    private FormatType getFormatMimeType(String stringFormat){
//        for (FormatType f : FormatType.values()) {
//            if(Objects.equals(f.getMimeType(), stringFormat)){
//                return f;
//            }
//        }
//        return null;
//    }
}

