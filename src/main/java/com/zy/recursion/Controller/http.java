package com.zy.recursion.Controller;

import com.zy.recursion.config.annotation;
import com.zy.recursion.entity.address;
import com.zy.recursion.entity.device;
import com.zy.recursion.entity.returnMessage;
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

    @annotation.UserLoginToken
    @CrossOrigin
    @PostMapping(value = "/sendPostDataByJson",produces = {"text/html;charset=UTF-8"})
    public String sendPostDataByJson(HttpServletResponse response, @RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject = new JSONObject(requestBody);
        List<device> list = deviceService.selectDeviceByNodeName(jsonObject.getString("nodeName"));
        String logCache = new ConnectLinuxCommand().logRead(list,address.getAddress());
//        return logCache;
        return logCache;
    }

    @annotation.UserLoginToken
    @CrossOrigin
    @PostMapping(value = "/sendPostDataByJson1",produces = {"text/html;charset=UTF-8"})
    public String sendPostDataByJson1(HttpServletResponse response, @RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject = new JSONObject(requestBody);
        device device= deviceService.selectDeviceByIp(jsonObject.getString("ip"));
        if(device.getDeviceType().equals("缓存")){
            String logCache = new ConnectLinuxCommand().logRead1(device,address.getAddress());
            return logCache;
        }
        return null;
    }

    @annotation.UserLoginToken
    @CrossOrigin
    @PostMapping(value = "/handleResolve")
    public returnMessage handleResolve(HttpServletResponse response, @RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject = new JSONObject(requestBody);
        String prefixType = jsonObject.getString("prefixType");
        String ip =jsonObject.getString("ip");
        String prefix = jsonObject.getString("prefix");
        if (prefixType.equals("Handle")){
            if (ip.equals("39.107.238.25") || ip.equals("172.171.1.80")){
                return httpPost.testSendPostDataByJson(jsonObject);
            }else{
                return null;
            }
        }else if (prefixType.equals("DNS")){
            if (ip.equals("172.171.1.80")){
                device device = deviceService.selectByIp1(ip);
                return new ConnectLinuxCommand().dns(device,ip,prefix);
            }else{
                return null;
            }
        }else if (prefixType.equals("Oid") || prefixType.equals("GS1") || prefixType.equals("Ecode")){
            if (ip.equals("172.171.1.80")){
                device device = deviceService.selectByIp1(ip);
                return new ConnectLinuxCommand().oid(device,ip,prefix);
            }else{
                return null;
            }
        }
        return null;
    }
}
