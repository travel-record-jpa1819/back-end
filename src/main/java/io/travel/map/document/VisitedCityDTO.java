package io.travel.map.document;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisitedCityDTO {
    // 프론트에서 넘어오는 형태에 맞게 필드명 동일하게
    private String id;
    private String cityName;
    private String countryName;
    private String lat;
    private String lng;
    private String date;   // DTO에서는 편의상 String으로 받음 → Service 레벨에서 변환
    private String notes;
    private boolean liked;
}