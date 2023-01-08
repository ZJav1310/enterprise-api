package com.enterpriseproject.enterpriseapi.APIControllers;

import Controllers.Commands;
import Models.Film;

import javax.xml.parsers.ParserConfigurationException;
import java.sql.SQLException;
import java.util.Collection;

public class RequestUtility {
    Commands commands;
    public RequestUtility(){
        try {
            this.commands = new Commands();
        } catch (ParserConfigurationException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Film getById(int id){
        return commands.getById(id);
    }

    public Collection<Film> searchByTitle(String title){
        return commands.searchByTitle(title);
    }

    public Collection<Film> getAll(){
        return commands.getAllFilms();
    }

    public int insertFilm(Film f) {
        return commands.insertFilm(f);
    }

    public int deleteFilm(int id) {
        return commands.deleteFilm(id);
    }

    public int updateFilm(Film f) {
        return commands.updateFilm(f);
    }

}
