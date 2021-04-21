package ru.geekbrains.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@With
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @JsonProperty("id")
    private Integer id;
    @JsonProperty("title")
    private  String title;
    @JsonProperty("products")
    private  List<Product> products = new ArrayList<>();

}