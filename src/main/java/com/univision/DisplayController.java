package com.univision;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 */
@Controller
public class DisplayController {

    @RequestMapping("/")
    String index() {
        return "default";
    }

}
