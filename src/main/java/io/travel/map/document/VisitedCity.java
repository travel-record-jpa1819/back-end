package io.travel.map.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;


@Getter
@Setter
public class VisitedCity {

    @Id
    private String id;

    private String cityName; // 참조
    private String lat;
    private String lng;
    private LocalDateTime date;
    private String notes;
    private String countryName;
    private boolean liked;
}