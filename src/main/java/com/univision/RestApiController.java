package com.univision;

import com.univision.properties.NotificationProperties;
import com.univision.storage.Information;
import com.univision.storage.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Autowired
    private InformationRepository infoRepo;

    @Autowired
    private NotificationProperties notification;

    @RequestMapping("/resource")
    public Map<String, Object> home() {
        Map<String, Object> model = new HashMap<>();
        model.put("title", notification.getTitle());
        model.put("content", notification.getDescription());
        model.put("ttl", notification.getTtl());

        ArrayList<Record> recordList = new ArrayList<>();
        PageRequest request = new PageRequest(0, 30, Sort.Direction.DESC, "docDate");
        Iterable<Record> records = storage.findAll(request);
        for (Record record : records) {
            recordList.add(record);
        }
        model.put("records", recordList);

        return model;
    }

    @RequestMapping("/event/{eventId:[\\d]+}")
    public Map<String, Object> event(@PathVariable final long eventId) {
        Map<String, Object> model = new HashMap<>();
        ArrayList<Record> recordList = new ArrayList<>();
        Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "docDate"));
        Iterable<Record> records = storage.findByEventId(Long.toString(eventId), sort);
        for (Record record : records) {
            recordList.add(record);
        }
        model.put("records", recordList);

        return model;
    }

    @RequestMapping("/information")
    public Map<String, Object> info(@RequestParam(value="id", defaultValue="all") String id, @RequestParam(value="type", defaultValue="all") String type) {
        Map<String, Object> model = new HashMap<>();
        ArrayList<Information> recordList = new ArrayList<>();

        Iterable<Information> Info = null;
        if (id.equals("all") && type.equals("all")) {
            Info = infoRepo.findAll();
        } else if (!id.equals("all") && type.equals("all")) {
            Info = infoRepo.findById(id);
        } else {
            Info = infoRepo.findByIdAndType(id, type);
        }

        for (Information information : Info) {
            recordList.add(information);
        }
        model.put("infoList", recordList);

        return model;
    }

}
