package com.nihal.split.repository;

import com.nihal.split.models.Group;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface GroupRepo extends MongoRepository<Group,String> {
    @Query("{_id :?0}")
    Group findByGroupId(String id);
}
