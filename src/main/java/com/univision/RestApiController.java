package com.univision;

import com.univision.storage.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 */
@RestController
@RequestMapping(value = "/rest", produces = {"application/json"})
public class RestApiController {

    @Autowired
    private EventRepository storage;

    @RequestMapping("/resource")
    public Map<String, Object> home() {
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Deportes Integrations");
        model.put("content", "Deportes integration tests monitoring tool");

        ArrayList<Record> recordList = new ArrayList<>();
        PageRequest request = new PageRequest(0, 30, Sort.Direction.DESC, "docDate");
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "docDate"));
        Iterable<Record> records = storage.findAll(request);
        for (Record record : records) {
            recordList.add(record);
        }
        model.put("records", recordList);

        return model;
    }

}
