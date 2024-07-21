package com.inventory.backend.service;

import com.inventory.backend.dto.RoomDto;

import java.util.List;

public interface RoomService {
    RoomDto createRoom(RoomDto roomDto);
    RoomDto updateRoom(Long id, RoomDto roomDto);
    void deleteRoom(Long id);
    RoomDto getRoomById(Long id);
    List<RoomDto> getAllRooms();
}