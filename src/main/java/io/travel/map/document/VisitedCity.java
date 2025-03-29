package io.travel.map.document;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
public class VisitedCity {

    private String cityID; // 참조
    private LocalDate visitedAT;
    private String description;
    private List<String> photos;
    private boolean liked;
}
