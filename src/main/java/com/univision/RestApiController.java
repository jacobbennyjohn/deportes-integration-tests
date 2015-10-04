package com.univision;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 */
@RestController
@RequestMapping(value = "/rest", produces = {"application/json"})
public class RestApiController {

    @RequestMapping("/resource")
    public Map<String,Object> home() {
        Map<String, Object> model = new HashMap<>();
        model.put("title", "Deportes Integrations");
        model.put("content", "Deportes integration tests monitoring tool");
        return model;
    }

}
