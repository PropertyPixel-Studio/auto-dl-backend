package cz.pps.auto_dl_be.controller;

import cz.pps.auto_dl_be.model.Item;
import cz.pps.auto_dl_be.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public Page<Item> getAllItems(Pageable pageable) {
        return itemService.getItems(pageable);
    }
}