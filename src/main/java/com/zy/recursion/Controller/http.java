package com.zy.recursion.Controller;

import com.zy.recursion.entity.address;
import com.zy.recursion.entity.device;
import com.zy.recursion.service.device.deviceService;
import com.zy.recursion.util.ConnectLinuxCommand;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.util.List;
import com.zy.recursion.util.httpPost;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/http", method = RequestMethod.GET)
@ResponseBody


public class http {

    @Autowired
    private address address;

    @Autowired
    private deviceService deviceService;

    @CrossOrigin
    @PostMapping(value = "/sendPostDataByJson")
    public String sendPostDataByJson(HttpServletResponse response, @RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject = new JSONObject(requestBody);
        List<device> list = deviceService.selectDeviceByNodeName(jsonObject.getString("nodeName"));
        String logCache = new ConnectLinuxCommand().logRead(list,address.getAddress());
//        return logCache;
        return logCache;
    }

    @CrossOrigin
    @PostMapping(value = "/sendPostDataByJson1")
    public String sendPostDataByJson1(HttpServletResponse response, @RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject = new JSONObject(requestBody);
        device device= deviceService.selectDeviceByIp(jsonObject.getString("ip"));
        if(device.getDeviceType().equals("缓存")){
            String logCache = new ConnectLinuxCommand().logRead1(device,address.getAddress());
            return logCache;
        }
        return null;
    }


    @CrossOrigin
    @PostMapping(value = "/handleResolve")
    public String handleResolve(HttpServletResponse response, @RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject = new JSONObject(requestBody);
        String ip =jsonObject.getString("ip");
        if (ip.equals("39.107.238.25")){
            return httpPost.testSendPostDataByJson(jsonObject);
        }else {
            return null;
        }
    }
}
