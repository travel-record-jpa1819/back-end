package io.travel.map.service;

import io.travel.map.document.City;
import io.travel.map.repository.CityRepository;
import org.springframework.stereotype.Service;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public City saveCity(City city){
        return cityRepository.save(city); // 저장 시 MongoDB가 자동으로 cities 컬렉션 생성
    }
}