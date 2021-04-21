package ru.geekbrains.Parts;

import lombok.Getter;

public enum CategoryType {

    FOOD(1,"Food"),
    FURNITURE(3,"Furniture"),
    ELECTRONICS(2,"Electronic"),
    INVALID(4,"Toys");

    @Getter
    private final Integer id;
    @Getter
    private final String title;

    CategoryType (int id, String title){
        this.id = id;
        this.title = title;
    }


}
