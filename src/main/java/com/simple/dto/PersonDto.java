package com.simple.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class PersonDto {

    private String firstName;

    private String lastName;
}
