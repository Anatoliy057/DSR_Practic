package ru.org.dsr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class SearchController {

    @GetMapping("/search")
    public ModelAndView toSearch() {
        return new ModelAndView("search");
    }

    @GetMapping("/")
    public ModelAndView toMain() {
        return new ModelAndView("redirect:/search");
    }

}
