package com.univision;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 */
@Controller
public class DisplayController {

    @RequestMapping("/")
    String index() {
        return "default";
    }

    @RequestMapping("/event/{eventId:[\\d]+}")
    String event(Model model, @PathVariable final long eventId) {
        model.addAttribute("eventId", eventId);
        return "event";
    }

}
