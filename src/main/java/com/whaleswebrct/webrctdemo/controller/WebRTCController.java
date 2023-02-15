package com.whaleswebrct.webrctdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuk
 * @description:
 * @menu
 * @date 2023/1/26 21:32
 */
@Controller
public class WebRTCController {

    @GetMapping("/index")
    public String getIndex(){
        return "WebRtc.html";
    }
}
