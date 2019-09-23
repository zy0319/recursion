package com.zy.recursion.Controller;


import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = "/login", method = RequestMethod.GET)
public class login {
    @CrossOrigin
    @PostMapping(value = "")
    public int cacheDelete(@RequestBody(required = false) String requesyBody) throws IOException {
        JSONObject jsonObject = new JSONObject(requesyBody);
        String username = jsonObject.getString("userName");
        String pwd = jsonObject.getString("password");
        if(username.equals("admin") && pwd.equals("123456")){
            return 0;
        }else
        {
            return 1;
        }
    }
}
