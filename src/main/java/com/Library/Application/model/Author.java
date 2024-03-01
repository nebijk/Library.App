package com.Library.Application.model;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Author {
    private int authorId;
    private String name;
    private Date birthdate;

    public Author(int authorId, String name, Date birthdate) {
        this.authorId = authorId;
        this.name = name;
        this.birthdate = birthdate;
    }

    public Author(String name, Date birthdate) {
        this.name = name;
        this.birthdate = birthdate;
    }

    public Author(String name) {
        this.name = name;

    }

    // Getters och Setters
    public int getAuthorId() {
        return authorId;
    }

    public String getName() {
        return name;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    @Override
    public String toString() {
        return name;
    }
}

