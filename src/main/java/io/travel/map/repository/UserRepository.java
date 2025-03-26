package io.travel.map.repository;



import io.travel.map.entity.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String > {
    Optional<User> findByEmail(String email); // 이메일로 유저 검색
}
