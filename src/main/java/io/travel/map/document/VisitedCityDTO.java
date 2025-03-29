package io.travel.map.document;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VisitedCityDTO {
    private String cityId;
    private LocalDate visitedAt;
    private String description;
    private java.util.List<String> photos;
    private boolean liked;
}