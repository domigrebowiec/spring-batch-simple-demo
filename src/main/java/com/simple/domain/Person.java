package com.simple.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "People")
@Data
@NoArgsConstructor
public class Person {

    @Id
    @GeneratedValue
    private Long person_id;

    private String first_name;

    private String last_name;
}
