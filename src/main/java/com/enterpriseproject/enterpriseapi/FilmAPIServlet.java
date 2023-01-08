package com.enterpriseproject.enterpriseapi;

import Models.Film;
import com.enterpriseproject.enterpriseapi.APIControllers.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Change the reader class into static since?
@WebServlet(name = "FilmAPIServlet", value = "/FilmAPIServlet")
public class FilmAPIServlet extends HttpServlet {
    RequestUtility requestUtility = new RequestUtility();
    FormatUtility formatUtility = new FormatUtility();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (request.getParameterMap().containsKey("format")) {
            formatUtility.setReader(FormatType.valueOf(request.getParameter("format").trim().toUpperCase()));
        }

        StringBuilder output = new StringBuilder();

        if (request.getParameterMap().containsKey("id")) {
            int id = Integer.parseInt(request.getParameter("id"));
            output.append(formatUtility.serialise(requestUtility.getById(id)));

        } else if (request.getParameterMap().containsKey("title")) {
            for (Film f : requestUtility.searchByTitle(request.getParameter("title"))) {
                output.append(formatUtility.serialise(f));
            }
        } else {
            for (Film f : requestUtility.getAll()) {
                output.append(formatUtility.serialise(f));
            }
        }

        // TODO: Change all to stream the output instead of storing.
        response.setContentType(formatUtility.getContentType());
        response.getWriter().println(output);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        StringBuilder output = new StringBuilder();
        Film f;

        if (ValidationUtility.validateParameters(request, List.of("title", "year", "stars", "director", "review"))) {

            String title = request.getParameter("title");
            String director = request.getParameter("director");
            String stars = request.getParameter("stars");
            String review = request.getParameter("review");
            int year = Integer.parseInt(request.getParameter("year"));

            f = new Film(title, year, director, stars, review);
        }
        else {

            String body = request.getReader().lines().collect(Collectors.joining());
            formatUtility.setReader(request.getContentType());
            f = (Film) formatUtility.deserialise(body, Film.class);

        }

        if (request.getParameterMap().containsKey("format")) {
            formatUtility.setReader(FormatType.valueOf(request.getParameter("format").trim().toUpperCase()));
        }

        int result = requestUtility.insertFilm(f);

        if (result > 0) {
//                output.append("Inserted.");
            output.append(formatUtility.serialise(f));
        }

        response.setContentType(formatUtility.getContentType());
        response.getWriter().println(output);


    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (request.getParameterMap().containsKey("format")) {
            formatUtility.setReader(FormatType.valueOf(request.getParameter("format").trim().toUpperCase()));
        }

        StringBuilder output = new StringBuilder();

        if (request.getParameterMap().containsKey("id")) {
            int result = requestUtility.deleteFilm(Integer.parseInt(request.getParameter("id")));

            if (result > 0) {
                output.append("Deleted.");
            }
        }
        response.setContentType(formatUtility.getContentType());
        response.getWriter().println(output);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

        StringBuilder output = new StringBuilder();
        int id, year;
        String title,director,stars,review;
        Film f;

        if (ValidationUtility.validateParameters(request, List.of("id", "title", "year", "stars", "director", "review"))) {

            id = Integer.parseInt(request.getParameter("id"));
            title = request.getParameter("title");
            director = request.getParameter("director");
            stars = request.getParameter("stars");
            review = request.getParameter("review");
            year = Integer.parseInt(request.getParameter("year"));

            f = new Film(id, title, year, director, stars, review);
        } else {

            String body = request.getReader().lines().collect(Collectors.joining());
            formatUtility.setReader(request.getContentType());
            f = (Film) formatUtility.deserialise(body, Film.class);

        }

        if (request.getParameterMap().containsKey("format")) {
            formatUtility.setReader(FormatType.valueOf(request.getParameter("format").trim().toUpperCase()));
        }

        int result = requestUtility.updateFilm(f);

        if (result > 0) {
//                output.append("Updated.");
            output.append(formatUtility.serialise(f));
        }

        response.setContentType(formatUtility.getContentType());
        response.getWriter().println(output);

    }
}

