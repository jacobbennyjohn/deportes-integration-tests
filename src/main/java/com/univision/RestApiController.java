package com.univision;

import com.univision.storage.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

        Map<String, Object> recordMap = new HashMap<>();
        Iterable<Record> records = storage.findAll();
        for (Record record : records) {
            String key = record.getId();
            if (key == null) {
                key = record.getEventId();
            }
            recordMap.put(key, record.toString());
        }
        model.put("records", recordMap);
        storage.deleteAll();

        return model;
    }

}
