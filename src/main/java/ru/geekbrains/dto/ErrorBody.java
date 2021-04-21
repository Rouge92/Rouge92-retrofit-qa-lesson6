package ru.geekbrains.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class ErrorBody {
    private Integer status;
    private String message;
    private String timestamp;
    private String path;
    private String error;

}
