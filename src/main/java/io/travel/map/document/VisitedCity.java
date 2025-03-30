package io.travel.map.document;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class VisitedCity {

    private String cityName; // 참조
    private String lat;
    private String lng;
    private LocalDateTime date;
    private String notes;
    private boolean liked;
}