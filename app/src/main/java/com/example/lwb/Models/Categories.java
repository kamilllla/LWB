package com.example.lwb.Models;

import java.util.List;
public class Categories {
    private String nameOfCategorie;
    private List<Guid> guids;

    public Categories(String nameOfCategorie,List<Guid> guids) {
        this.nameOfCategorie = nameOfCategorie;
        this.guids = guids;
    }

    public String getNameOfCategorie() {
        return nameOfCategorie;
    }

    public void setNameOfCategorie(String nameOfCategorie) {
        this.nameOfCategorie = nameOfCategorie;
    }

    public List<Guid> getGuids() {
        return guids;
    }

    public void setGuids(List<Guid> guids) {
        this.guids = guids;
    }
}

