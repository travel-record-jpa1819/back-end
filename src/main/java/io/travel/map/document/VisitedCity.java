package io.travel.map.document;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
public class VisitedCity {

    private String id = UUID.randomUUID().toString(); // 💡 자동 생성 ID

    private String cityName; // 참조
    private String lat;
    private String lng;
    private LocalDateTime date;
    private String notes;
    private String countryName;
    private boolean liked;
}