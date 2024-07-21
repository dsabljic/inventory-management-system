package com.inventory.backend.service.impl;

import com.inventory.backend.dto.ItemTypeDto;
import com.inventory.backend.entity.ItemType;
import com.inventory.backend.repository.ItemTypeRepository;
import com.inventory.backend.service.ItemTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemTypeServiceImpl implements ItemTypeService {

    @Autowired
    private ItemTypeRepository itemTypeRepository;

    @Override
    public ItemTypeDto createItemType(ItemTypeDto itemTypeDto) {
        ItemType itemType = new ItemType();
        itemType.setName(itemTypeDto.getName());
        ItemType savedItemType = itemTypeRepository.save(itemType);
//        itemTypeDto.setId(savedItemType.getId());
//        return itemTypeDto;
        return convertToDto(savedItemType);
    }

    @Override
    public List<ItemTypeDto> getAllItemTypes() {
        return itemTypeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ItemTypeDto> getItemTypeById(Long id) {
        return itemTypeRepository.findById(id).map(this::convertToDto);
    }

    @Override
    public ItemTypeDto updateItemType(Long id, ItemTypeDto itemTypeDto) {
        ItemType itemType = itemTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item type not found"));
        itemType.setName(itemTypeDto.getName());
        ItemType updatedItemType = itemTypeRepository.save(itemType);
//        itemTypeDto.setId(updatedItemType.getId());
//        return itemTypeDto;
        return convertToDto(updatedItemType);
    }

    @Override
    public void deleteItemType(Long id) {
        ItemType itemType = itemTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ItemType not found"));
        itemTypeRepository.delete(itemType);
    }

    private ItemTypeDto convertToDto(ItemType itemType) {
        ItemTypeDto itemTypeDto = new ItemTypeDto();
        itemTypeDto.setId(itemType.getId());
        itemTypeDto.setName(itemType.getName());
        return itemTypeDto;
    }
}