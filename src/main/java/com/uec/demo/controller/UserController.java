package com.uec.demo.controller;

import com.uec.demo.util.ResultBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @PostMapping("/register")
    public ResultBean register(){
        return null;
    }

    @PostMapping("/login")
    public ResultBean login(){
        return null;
    }

}
