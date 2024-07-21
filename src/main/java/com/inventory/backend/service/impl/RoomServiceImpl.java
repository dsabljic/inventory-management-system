package com.inventory.backend.service.impl;

import com.inventory.backend.dto.RoomDto;
import com.inventory.backend.entity.Room;
import com.inventory.backend.repository.RoomRepository;
import com.inventory.backend.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public RoomDto createRoom(RoomDto roomDto) {
        Room room = new Room();
        room.setName(roomDto.getName());
        room.setBuilding(roomDto.getBuilding());
        room.setFloor(roomDto.getFloor());
        room.setTotalDesks(roomDto.getTotalDesks());

        Room savedRoom = roomRepository.save(room);
        return convertToDto(savedRoom);
    }

    @Override
    public RoomDto updateRoom(Long id, RoomDto roomDto) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
        room.setName(roomDto.getName());
        room.setBuilding(roomDto.getBuilding());
        room.setFloor(roomDto.getFloor());
        room.setTotalDesks(roomDto.getTotalDesks());

        Room updatedRoom = roomRepository.save(room);
        return convertToDto(updatedRoom);
    }

    @Override
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    @Override
    public RoomDto getRoomById(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new RuntimeException("Room not found"));
        return convertToDto(room);
    }

    @Override
    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private RoomDto convertToDto(Room room) {
        RoomDto roomDto = new RoomDto();
        roomDto.setId(room.getId());
        roomDto.setName(room.getName());
        roomDto.setBuilding(room.getBuilding());
        roomDto.setFloor(room.getFloor());
        roomDto.setTotalDesks(room.getTotalDesks());
        return roomDto;
    }
}