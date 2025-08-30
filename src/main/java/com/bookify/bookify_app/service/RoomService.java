package com.bookify.bookify_app.service;

import com.bookify.bookify_app.model.Room;
import com.bookify.bookify_app.repository.RoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {
    private final RoomRepository repo;

    public RoomService(RoomRepository repo) {
        this.repo = repo;
    }

    public List<Room> getAll(String clinicId) {
        return repo.findByClinicId(clinicId);
    }

    public Optional<Room> getById(String id) {
        return repo.findById(id);
    }

    public Room save(Room room) {
        return repo.save(room);
    }

    public void delete(String id) {
        repo.deleteById(id);
    }
}
