package io.travel.map.document;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String email;
    @Field("name")
    private String userName;
    private String photoUrl;
    private LocalDate accountCreatedDate;

    private List<VisitedCity> visitedCities = new ArrayList<>();




}