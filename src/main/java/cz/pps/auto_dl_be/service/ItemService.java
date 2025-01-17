package cz.pps.auto_dl_be.service;

import cz.pps.auto_dl_be.dao.ItemDao;
import cz.pps.auto_dl_be.model.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemDao itemDao;

    public List<Item> getAllItems() {
        return itemDao.findAll();
    }

    public Page<Item> getItems(Pageable pageable) {
        return itemDao.findAll(pageable);
    }
}