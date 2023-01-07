package com.enterpriseproject.enterpriseapi;

import Controllers.Commands;
import Models.Film;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet(name = "FilmAPIServlet", value = "/FilmAPIServlet")
public class FilmAPIServlet extends HttpServlet {

    Commands commands;

    {
        try {
            commands = new Commands();
        } catch (ParserConfigurationException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        var writer = response.getWriter();

        if (request.getParameterMap().containsKey("id")) {
            var film = commands.getById(Integer.parseInt(request.getParameter("id")));
            System.out.println(film);
            writer.println("GET ID.");
        } else if (request.getParameterMap().containsKey("title")) {
            var films = commands.searchByTitle(request.getParameter("title"));
            for (Film f : films) {
                System.out.println(f);
            }
            writer.println("GET TITLES.");
        } else {
            var films = (ArrayList<Film>) commands.getAllFilms();
            for (Film f : films) {
                System.out.println(f);
            }
            writer.println("GET ALL.");

            if (request.getParameter("format").equals("JSON")) {
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var writer = response.getWriter();
        String message = "Failed";

        if (request.getParameterMap().containsKey("title")
                && request.getParameterMap().containsKey("year")
                && request.getParameterMap().containsKey("stars")
                && request.getParameterMap().containsKey("director")
                && request.getParameterMap().containsKey("review")) {
            String title = request.getParameter("title");
            String director = request.getParameter("director");
            String stars = request.getParameter("stars");
            String review = request.getParameter("review");
            int year = Integer.parseInt(request.getParameter("year"));

            Film f = new Film(title, year, director, stars, review);

            int result = commands.insertFilm(f);

            if (result > 0) {
                message = "Inserted.";
            }
        }

        writer.println(message);

    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var writer = response.getWriter();
        String message = "Failed";

        if (request.getParameterMap().containsKey("id")) {
            int result = commands.deleteFilm(Integer.parseInt(request.getParameter("id")));

            if (result > 0) {
                message = "Deleted.";
            }
        }

        writer.println(message);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var writer = response.getWriter();
        String message = "Failed";

        if (request.getParameterMap().containsKey("title")
                && request.getParameterMap().containsKey("year")
                && request.getParameterMap().containsKey("stars")
                && request.getParameterMap().containsKey("director")
                && request.getParameterMap().containsKey("review")
                && request.getParameterMap().containsKey("id")) {
            String title = request.getParameter("title");
            String director = request.getParameter("director");
            String stars = request.getParameter("stars");
            String review = request.getParameter("review");
            int year = Integer.parseInt(request.getParameter("year"));
            int id = Integer.parseInt(request.getParameter("id"));


            Film f = new Film(id, title, year, director, stars, review);

            int result = commands.updateFilm(f);

            if (result > 0) {
                message = "Updated.";
            }
        }

        writer.println(message);
    }

    enum format {
        JSON, XML, TEXT
    }
}
