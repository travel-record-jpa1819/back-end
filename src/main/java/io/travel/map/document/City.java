package io.travel.map.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Setter
@Getter
@Document(collection = "cities")
public class City {

    @Id
    private String id;

    private String cityName;
    private String countryName;
    private String countryEmoji;

    private Position position;

    @Setter
    @Getter
    public static class Position{
        private double lat; //latitude
        private double lng; //longitude

    }

}
