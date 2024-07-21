package com.inventory.backend.service;

import com.inventory.backend.dto.ItemDto;

import java.util.List;
import java.util.Optional;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto);
    List<ItemDto> getAllItems();
    Optional<ItemDto> getItemById(Long id);
    ItemDto updateItem(Long id, ItemDto itemDto);
    void deleteItem(Long id);
}