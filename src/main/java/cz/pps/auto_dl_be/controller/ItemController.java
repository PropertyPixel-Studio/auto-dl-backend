package cz.pps.auto_dl_be.controller;

import cz.pps.auto_dl_be.dto.detail.Article;
import cz.pps.auto_dl_be.model.Item;
import cz.pps.auto_dl_be.service.ItemService;
import cz.pps.auto_dl_be.service.TecDocService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final TecDocService tecDocService;

    @GetMapping
    public ResponseEntity<Page<Item>> getAllItems(Pageable pageable) {
        return ResponseEntity.ok(itemService.getItems(pageable));
    }

    @GetMapping("/detail")
    public ResponseEntity<List<Article>> getDetail(@RequestParam String tecDocId) {
        Optional<Item> item = itemService.getItemByTecDocId(tecDocId);
        if (item.isPresent()) {
            List<Article> articles = tecDocService.fetchDetail(
                    item.get().getTecDocld(),
                    item.get().getTecDocSupplierID())
                    .block();
            return ResponseEntity.ok(articles);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}