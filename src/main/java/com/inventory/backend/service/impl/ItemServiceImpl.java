package com.inventory.backend.service.impl;

import com.inventory.backend.dto.ItemDto;
import com.inventory.backend.entity.Item;
import com.inventory.backend.entity.ItemType;
import com.inventory.backend.repository.ItemRepository;
import com.inventory.backend.repository.ItemTypeRepository;
import com.inventory.backend.service.ItemService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemTypeRepository itemTypeRepository;

    @Override
    public ItemDto createItem(ItemDto itemDto) {
        Item item = new Item();
        return getItemDto(itemDto, item);
    }

    @Override
    public List<ItemDto> getAllItems() {
        return itemRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ItemDto> getItemById(Long id) {
        return itemRepository.findById(id).map(this::convertToDto);
    }

    @Override
    public ItemDto updateItem(Long id, ItemDto itemDto) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        return getItemDto(itemDto, item);
    }

    @Override
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        itemRepository.delete(item);
    }

    private ItemDto convertToDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setTypeId(item.getItemType().getId());
        itemDto.setQuantity(item.getQuantity());
        itemDto.setAvailableQuantity(item.getAvailableQuantity());
        return itemDto;
    }

    @NotNull
    private ItemDto getItemDto(ItemDto itemDto, Item item) {
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setQuantity(itemDto.getQuantity());
        item.setAvailableQuantity(itemDto.getAvailableQuantity());

        ItemType itemType = itemTypeRepository.findById(itemDto.getTypeId())
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setItemType(itemType);

        Item savedItem = itemRepository.save(item);
        return convertToDto(savedItem);
    }
}