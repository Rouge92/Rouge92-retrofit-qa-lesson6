package ru.geekbrains.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.ArrayList;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class Products {

    @JsonProperty("products")
    private List<Product> products = new ArrayList<>();

}