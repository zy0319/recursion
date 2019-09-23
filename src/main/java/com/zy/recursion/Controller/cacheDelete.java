package com.zy.recursion.Controller;

import com.zy.recursion.entity.cache;
import com.zy.recursion.entity.device;
import com.zy.recursion.entity.returnMessage;
import com.zy.recursion.service.cache.cacheService;
import com.zy.recursion.service.device.deviceService;
import com.zy.recursion.util.ConnectLinuxCommand;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping(value = "/cache", method = RequestMethod.GET)
public class cacheDelete {

    @Autowired
    private deviceService deviceService;

    @Autowired
    private com.zy.recursion.service.node.nodeService nodeService;

    @Autowired
    private cacheService cacheService;

    @CrossOrigin
    @PostMapping(value = "/handleDelete")
    public String cacheDelete(@RequestBody(required = false) String requesyBody) throws IOException {
        Logger logger = LoggerFactory.getLogger(cacheDelete.class);
        cache cache = new cache();
        JSONObject jsonObject = new JSONObject(requesyBody);
        String handle = jsonObject.getString("handle");
        String ip = jsonObject.getString("ip");
        device device = deviceService.selectDeviceByIp(ip);
        String usrName = device.getDeviceUserName();
        String pwd = device.getDevicePwd();
        String type = device.getDeviceType();
        if(type.equals("缓存")){
            String[] cmd = {"./../home/fnii/handle_cache/bin/cmdsh3 127.0.0.1 15000 2 \"cache delete " + handle + "\""};
            logger.info("在设备" + ip + "上执行" + cmd[0]);
            String[] result = ConnectLinuxCommand.execute(ip, usrName, pwd, cmd);
            if (result != null) {
                String result2 = result[0].split(" ")[3];
                String result1 = result2.substring(0, result2.length() - 1);
                logger.info("删除单条记录结果：" + result1);
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String deleteTime = dateFormat.format(date);
                Timestamp ts = Timestamp.valueOf(deleteTime);
                cache.setDeleteTime(ts);
                cache.setDeleteOperation("删除单条标识");
                cache.setHandle(handle);
                cache.setDeleteResult(result1);
                cacheService.addCache(cache);
                logger.info("记录存储成功");
                return result1;
            } else {
                return null;
            }
        }else {
            return null;
        }
    }


    @CrossOrigin
    @PostMapping(value = "/multiHandleDelete")
    public String multiHandleDelete(@RequestBody(required = false) String requesyBody) throws IOException {
        Logger logger = LoggerFactory.getLogger(cacheDelete.class);
        cache cache = new cache();
        JSONObject jsonObject = new JSONObject(requesyBody);
        String handle = jsonObject.getString("handle");
        String[] handle1 = handle.split(",");
        String ip = jsonObject.getString("ip");
        device device = deviceService.selectDeviceByIp(ip);
        String usrName = device.getDeviceUserName();
        String pwd = device.getDevicePwd();
        String type = device.getDeviceType();
        if(type.equals("缓存")){
            List result1 = new ArrayList();
            JSONObject jsonObject1 = new JSONObject();
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String deleteTime = dateFormat.format(date);
            Timestamp ts = Timestamp.valueOf(deleteTime);
            String[] cmd = new String[handle1.length];
            for (int i = 0; i < handle1.length; i++) {
                cmd[i] = "./../home/fnii/handle_cache/bin/cmdsh3 127.0.0.1 15000 2 \"cache delete " + handle1[i] + "\"";
            }
            String[] result = ConnectLinuxCommand.execute(ip, usrName, pwd, cmd);
            if (result != null) {
                for (int i = 0; i < handle1.length; i++) {
                    String m = result[i].split(" ")[3];
                    if (m.substring(0, m.length() - 1).equals("ok")) {
                        jsonObject1.put(handle1[i], "ok");
                    } else {
                        jsonObject1.put(handle1[i], "false");
                    }
                }
                logger.info(result1.toString());
                cache.setDeleteTime(ts);
                cache.setDeleteOperation("删除多条标识");
                cache.setHandle(handle);
                cache.setDeleteResult(result1.toString());
                cacheService.addCache(cache);
                logger.info("记录存储成功");
                return jsonObject1.toString();
            } else {
                return null;
            }
        }else {
            return null;
        }

    }

    @CrossOrigin
    @PostMapping(value = "/deviceHandleDelete")
    public returnMessage deviceHandleDelete(@RequestBody(required = false) String requesyBody) throws IOException {
        returnMessage returnMessage = new returnMessage();
        Logger logger = LoggerFactory.getLogger(cacheDelete.class);
        JSONObject jsonObject = new JSONObject(requesyBody);
        String ip = jsonObject.getString("ip");
        device device = deviceService.selectDeviceByIp(ip);
        String usrName = device.getDeviceUserName();
        String pwd = device.getDevicePwd();
        String type = device.getDeviceType();
        if(type.equals("缓存")){
            String[] cmd = {"./../home/fnii/handle_cache/bin/cmdsh3 127.0.0.1 15000 2 \"cache flush_all\""};
            logger.info("在设备" + ip + "上执行" + cmd[0]);
            String result = ConnectLinuxCommand.execute(ip, usrName, pwd, cmd)[0];
            if (result != null) {
                logger.info("删除设备" + ip + "缓存：" + result);
                Date date = new Date();
                cache cache = new cache();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String deleteTime = dateFormat.format(date);
                Timestamp ts = Timestamp.valueOf(deleteTime);
                cache.setHandle("全部");
                cache.setDeleteOperation("删除" + ip + "对应设备的缓存");
                cache.setDeleteTime(ts);
                cache.setDeleteResult(result);
                cacheService.addCache(cache);
                returnMessage.setStatus(0);
                returnMessage.setMessage("删除成功");
                return returnMessage;
            } else {
                returnMessage.setStatus(1);
                returnMessage.setMessage("服务器连接失败");
                return returnMessage;
            }
        }else {
            return null;
        }

    }

    @CrossOrigin
    @PostMapping(value = "/nodeHandleDelete")
    public String nodeHandleDelete(@RequestBody(required = false) String requesyBody) throws IOException {
        Logger logger = LoggerFactory.getLogger(cacheDelete.class);
        JSONObject jsonObject = new JSONObject(requesyBody);
        String nodeName = jsonObject.getString("nodeName");
        List<device> list = deviceService.selectIpByNodeName(nodeName);
        List falseList = new ArrayList();
        List sucList = new ArrayList();
        for (device device : list) {
            String ip = device.getDeviceIp();
            String usrName = device.getDeviceUserName();
            String pwd = device.getDevicePwd();
            String type = device.getDeviceType();
            if (type.equals("缓存")){
                String[] cmd = {"./../home/fnii/handle_cache/bin/cmdsh3 127.0.0.1 15000 2 \"cache flush_all\""};
                String[] result = ConnectLinuxCommand.execute(ip, usrName, pwd, cmd);
                if (result != null) {
                    sucList.add(ip);
                    logger.info("删除设备" + ip + "缓存结果：" + result[0]);
                } else {
                    falseList.add(ip);
                    logger.info(ip + "连接失败");
                }
            }else {
                falseList.add(ip);
                logger.info(ip + "不是缓存服务器");
            }
        }
        Date date = new Date();
        cache cache = new cache();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String deleteTime = dateFormat.format(date);
        Timestamp ts = Timestamp.valueOf(deleteTime);
        cache.setHandle("全部");
        cache.setDeleteOperation("删除" + nodeName + "节点的缓存");
        cache.setDeleteTime(ts);
        String result = "删除成功的设备：" + sucList.toString() + " 删除失败的设备" + falseList.toString();
        cache.setDeleteResult(result);
        cacheService.addCache(cache);
        return result;
    }

    @CrossOrigin
    @PostMapping(value = "/cacheSelect")
    public List<cache> cacheSelect(@RequestBody(required = false) String requesyBody) throws IOException {
        JSONObject jsonObject = new JSONObject(requesyBody);
        String startTime = jsonObject.getString("startTime");
        String endTime = jsonObject.getString("endTime");
        return cacheService.selectCache(startTime,endTime);
    }


}
