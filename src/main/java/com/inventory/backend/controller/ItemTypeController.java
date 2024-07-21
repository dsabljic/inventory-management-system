package com.inventory.backend.controller;

import com.inventory.backend.dto.ItemTypeDto;
import com.inventory.backend.service.ItemTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin/itemTypes")
public class ItemTypeController {

    @Autowired
    private ItemTypeService itemTypeService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ItemTypeDto> createItemType(@RequestBody ItemTypeDto itemTypeDto) {
        log.info("Received request to create item type: " + itemTypeDto.getName());
        return ResponseEntity.ok(itemTypeService.createItemType(itemTypeDto));
    }

    @GetMapping
    public ResponseEntity<List<ItemTypeDto>> getAllItemTypes() {
        return ResponseEntity.ok(itemTypeService.getAllItemTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemTypeDto> getItemTypeById(@PathVariable Long id) {
        return itemTypeService.getItemTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ItemTypeDto> updateItemType(@PathVariable Long id, @RequestBody ItemTypeDto itemTypeDto) {
        return ResponseEntity.ok(itemTypeService.updateItemType(id, itemTypeDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteItemType(@PathVariable Long id) {
        itemTypeService.deleteItemType(id);
        return ResponseEntity.noContent().build();
    }
}