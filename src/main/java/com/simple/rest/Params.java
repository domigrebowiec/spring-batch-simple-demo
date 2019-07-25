package com.simple.rest;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class Params {

    @NotEmpty
    private String fileName;

    @NotEmpty
    private String timestamp;
}
