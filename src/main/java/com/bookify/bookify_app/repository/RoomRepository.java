package com.bookify.bookify_app.repository;

import com.bookify.bookify_app.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface RoomRepository extends MongoRepository<Room, String> {
    List<Room> findByClinicId(String clinicId);
}
