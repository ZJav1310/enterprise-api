package com.enterpriseproject.enterpriseapi;

import Models.Film;
import Utils.JSONReader;
import Utils.Reader;
import Utils.TextReader;
import Utils.XMLReader;

import com.enterpriseproject.enterpriseapi.APIControllers.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * - TODO:  Whats the return code if connection to the database isn't working?
 * - TODO:  Formatter should be changed to a helper class.
 * - TODO:  Change the methods at core to throw the correct exceptions and to not merge them
 *          because the client interfaces (Like this API) should deal with them -> helps with what to output.
 * - TODO: IF IT FAILS WHAT THE FUCK HAPPENS!!! NEED VALIDATION -> CHECK IF THE INPUTS WORK.
 *
 * Headers:
 *      - Use Accept for receiving payload,
 *      - Use Content-Type when sending payload,
 *
 *      - on request:
 *          -If there is Accept in the header, use the appropriate parser.
 *      - on response:
 *          - If content-type has been defined, use appropriate method,
 *          - If not, output JSON.
 *
 * Expected format:
 *  Returning Films:
 * {
 *   "films": [
 *     {
 *       "title": "qqqqqqqq",
 *       "year": "qqq",
 *       "director": "qqq",
 *       "stars": "qqqqq",
 *       "review": "qq",
 *       "id": 2
 *     },
 *     {
 *       "title": "ssss",
 *       "year": "d",
 *       "director": "d",
 *       "stars": "d",
 *       "review": "d",
 *       "id": 6
 *     }
 *   ]
 * }
 * Returning Film:
 * <Film>
 *     <id>10005</id>
 *     <title>This has been changed</title>
 *     <year>344</year>
 *     <director>sadadasdsad</director>
 *     <stars>asdadadad</stars>
 *     <review>adsdaasdadas</review>
 * </Film>
 *
 *
 * Formatter:
 *      - Film Object -> Serialise -> Requested format as string
 *      - Payload -> Deserialise -> Film Object
 *
 * API:
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status">...</a>
 * https://gist.github.com/igorjs/407ffc3126f6ef2a6fe8f918a0673b59
 *
 *  - If the input is malformed return 400 Bad request?
 *  - 412 Precondition failed - "The client has indicated preconditions in its headers which the server does not meet."
 *  - 415 Unsupported Media Type - "The media format of the requested data is not supported by the server, so the server is rejecting the request."
 *  - 500 Internal Server Error -> Could use this if the database connection failed?
 *  - This way of writing it exists -> HttpServletResponse.SC_NOT_FOUND
 *      - GET
 *          - Get all films
 *          - Get film with id
 *          - 200 OK -> resource has been fetched
 *          - 409 Conflict -> use when input is incorrect
 *      - PUT
 *          - Update film with new payload
 *          - Return 200 if successful
 *      - POST
 *          - Create new film
 *          - 201 Created -> use when insert worked.
 *      - DELETE
 *          - Delete film with id
 *          - Returns Header 200 if successful
 *          - 404 Not Found -> if id is not found
 *          - <a href="https://stackoverflow.com/questions/17884469/what-is-the-http-response-code-for-failed-http-delete-operation#:~:text=Yes%2C%20it%20would%20be%20404">...</a>.
 *
 */

@WebServlet(name = "FilmAPIServlet", value = "/FilmAPIServlet")
public class FilmAPIServlet extends HttpServlet {
    // DataRequest is the bridge from core to this api, it has all the database query methods I need for this API.
    private final DataRequest dataRequestCommand = new DataRequest();
    private List<String> errorMessage = new ArrayList<>();

    // Fake loading plugin that an easily be replaced with reflection stuff to build proper plugin loading
    private final List<Reader> supportedReaders = new ArrayList<>(Arrays.asList(JSONReader.getInstance(), XMLReader.getInstance(), TextReader.getInstance()));
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {

        Reader reader = chooseReader(request);

        /*
            Checks for params and does query that matches it:
                id -> Get film with that id
                title -> Searches for films that contain the title param
                /empty -> Gets ALL fills
         */

        List<Film> films = new ArrayList<>();

        if (areParamsValid(request, "id")) {
            int id = Integer.parseInt(request.getParameter("id"));
            films.add(dataRequestCommand.getById(id));

        } else if (areParamsValid(request, "title")) {

            films.addAll(dataRequestCommand.searchByTitle(request.getParameter("title")));

        } else {
            films.addAll(dataRequestCommand.getAll());
        }

        response.setContentType(reader.getAcceptedMimeType());

        //new Film[0] is to work around Type erasure.
        var output = reader.serialiseObject(films.toArray(new Film[0]));

        try{
            response.getWriter().println(output);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            errorMessage.add("Error returning output.");
            System.err.println(Arrays.toString(errorMessage.toArray()));
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }

    // Add new film
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        var input = "";
        int result = -1;

        try {
            input = request.getReader().lines().collect(Collectors.joining());
        } catch (IOException e) {
            this.errorMessage.add("Error reading request body");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        Reader reader = chooseReader(request, input);

        if(reader != null){
            response.setContentType(reader.getAcceptedMimeType());
            Film f = reader.deserialiseObject(input, Film.class);
            if(f != null){
                result = dataRequestCommand.insertFilm(f);
            }
        } else {
            this.errorMessage.add("Content is malformed or not accepted");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }

        if (result > 0){
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        try {
            response.getWriter().println(result);
        } catch (IOException e) {
            errorMessage.add("Error returning output.");
            System.err.println(Arrays.toString(errorMessage.toArray()));
        }
    }

    // https://www.ibm.com/docs/en/eamfoc/7.6.0?topic=methods-delete-method
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {

        int result = -1;

        Reader reader = chooseReader(request);

        response.setContentType(reader.getAcceptedMimeType());

        if (areParamsValid(request, "id")) {
            result = dataRequestCommand.deleteFilm(Integer.parseInt(request.getParameter("id")));
        } else {
            errorMessage.add("No id given.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        if (result > 0){
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        try {
            response.getWriter().println(result);
        } catch (IOException e) {
            errorMessage.add("Error returning output.");
            System.err.println(Arrays.toString(errorMessage.toArray()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        var input = "";
        int result = -1;

        try {
            input = request.getReader().lines().collect(Collectors.joining());
        } catch (IOException e) {
            this.errorMessage.add("Error reading request body");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        Reader reader = chooseReader(request, input);

        if(reader != null){
            response.setContentType(reader.getAcceptedMimeType());
            Film f = reader.deserialiseObject(input, Film.class);
            if(f != null){
                result = dataRequestCommand.updateFilm(f);
            }
        } else {
            this.errorMessage.add("Content is malformed or not accepted");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }

        if (result > 0){
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

        try {
            response.getWriter().println(result);
        } catch (IOException e) {
            errorMessage.add("Error returning output.");
            System.err.println(Arrays.toString(errorMessage.toArray()));
        }
    }

    private Reader chooseReader(HttpServletRequest request){
//        var readers = this.supportedReaders.stream().filter(o -> o.getAcceptedMimeType().equals(request.getHeader("Accept"))).collect(Collectors.toList());
//        if(readers.size() == 0){
//            this.errorMessage.add("Format not defined in Accept, set to JSON");
//            return this.supportedReaders.get(0);
//        } else {
//            // Gets the reader that matches the Accept Header (MIME-TYPE)
//            return readers.get(0);
//        }
        return chooseReader(request, null);
    }

    private Reader chooseReader(HttpServletRequest request, String input){
        var readers = this.supportedReaders.stream().filter(o -> o.getAcceptedMimeType().equals(request.getHeader("Accept"))).collect(Collectors.toList());
        if(readers.size() == 0){
            this.errorMessage.add("Format not defined in Accept Header, Finding reader.");
            if(input != null){

                for(Reader r : this.supportedReaders){
                    if(r.isValidInput(input, Film.class)){
                        return r;
                    }
                }
                this.errorMessage.add("Reader could not be found.");
            } else {
                return this.supportedReaders.get(0);
            }
        } else {
            // Gets the reader that matches the Accept Header (MIME-TYPE)
            return readers.get(0);
        }
        return null;
    }

    // Utility type methods to make retrieving commonly occurring instructions easier
    private static boolean areParamsValid(HttpServletRequest request, String key) {
        return request.getParameterMap().containsKey(key);
    }

}

