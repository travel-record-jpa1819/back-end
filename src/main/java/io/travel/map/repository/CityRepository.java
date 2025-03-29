package io.travel.map.repository;

import io.travel.map.document.City;
import io.travel.map.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends MongoRepository<City, String> {
    City findByCityName(String name);
}
