package com.movie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping
public class IndexController {

    @RequestMapping(method = RequestMethod.GET, value = "/")
    public String index() {
        System.out.println("index");
        return "index.html";
    }
}
