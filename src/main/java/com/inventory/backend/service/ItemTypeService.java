package com.inventory.backend.service;

import com.inventory.backend.dto.ItemTypeDto;

import java.util.List;
import java.util.Optional;

public interface ItemTypeService {
    ItemTypeDto createItemType(ItemTypeDto itemTypeDto);
    List<ItemTypeDto> getAllItemTypes();
    Optional<ItemTypeDto> getItemTypeById(Long id);
    ItemTypeDto updateItemType(Long id, ItemTypeDto itemTypeDto);
    void deleteItemType(Long id);
}