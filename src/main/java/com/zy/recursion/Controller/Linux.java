package com.zy.recursion.Controller;

import com.alibaba.fastjson.JSONArray;
import com.zy.recursion.entity.address;
import com.zy.recursion.entity.device;
import com.zy.recursion.service.device.deviceService;
import com.zy.recursion.util.ConnectLinuxCommand;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "/Linux", method = RequestMethod.GET)

public class Linux {

    @Autowired
    private address address;

    @Autowired
    private deviceService deviceService;

    @Autowired
    public ConnectLinuxCommand connectLinuxCommand;

    @CrossOrigin
    @PostMapping(value = "/nodeDiskUtilization")
    public String nodeDiskUtilization(@RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject1 = new JSONObject(requestBody);
        String nodeName = jsonObject1.getString("nodeName");
        List<device> list = deviceService.selectIpByNodeName(nodeName);
        float disk = 0f;
        if (list.size() != 0) {
            for (device o : list) {
                String ip = o.getDeviceIp();
                String name = o.getDeviceUserName();
                String pwd = o.getDevicePwd();
                String type = o.getDeviceType();
                String[] cmd = {"df -k"};
                String[] result = ConnectLinuxCommand.execute(ip, name, pwd, cmd);
                if (result != null) {
                    float disk1 = new ConnectLinuxCommand().disk_utilization(result[0]);
                    disk = disk1 + disk;
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("disk_utilization", disk / list.size());
                System.out.println(jsonObject.toString());
                return jsonObject.toString();
            }
        }
        return null;
    }


    @CrossOrigin
    @PostMapping(value = "/nodeFlow")
    public String nodeFlow(@RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject1 = new JSONObject(requestBody);
        String nodeName = jsonObject1.getString("nodeName");
        List<device> list = deviceService.selectIpByNodeName(nodeName);
        float rxkb = 0f;
        float txkb = 0f;
        if (list.size() != 0) {
            for (device o : list) {
                String ip = o.getDeviceIp();
                String name = o.getDeviceUserName();
                String pwd = o.getDevicePwd();
                String type = o.getDeviceType();
                String[] cmd = {"sar -n DEV 1 1"};
                String[] result = ConnectLinuxCommand.execute(ip, name, pwd, cmd);
                if (result != null) {
                    JSONObject jsonObject = new ConnectLinuxCommand().networkCard(result[0], address.getNetworkCard());
                    if (jsonObject != null) {
                        rxkb = rxkb + Float.parseFloat(jsonObject.getString("rxkB"));
                        txkb = txkb + Float.parseFloat(jsonObject.getString("txkB"));
                    }
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("txkb", txkb);
                jsonObject.put("rxkb", rxkb);
                jsonObject.put("bandwidth_utilization", (rxkb + txkb) / (address.getBandwidthSet() * 1024));
                System.out.println(jsonObject.toString());
                return jsonObject.toString();
            }
        }
        return null;
    }


    @CrossOrigin
    @PostMapping(value = "/nodeMemoryUtilization")
    public String nodeMemoryUtilization(@RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject1 = new JSONObject(requestBody);
        String nodeName = jsonObject1.getString("nodeName");
        List<device> list = deviceService.selectIpByNodeName(nodeName);
        long start = System.currentTimeMillis();
        float memory = 0f;
        if (list.size() != 0) {
            for (device o : list) {
                String ip = o.getDeviceIp();
                String name = o.getDeviceUserName();
                String pwd = o.getDevicePwd();
                String type = o.getDeviceType();
                String[] cmd = {"sar -r 1 1"};
                String[] result = ConnectLinuxCommand.execute(ip, name, pwd, cmd);
                if (result != null) {
                    float memory1 = new ConnectLinuxCommand().memory_utilization(result[0]);
                    memory = memory1 + memory;
                }
                long end = System.currentTimeMillis();
                System.out.println(end - start);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("memory_utilization", memory / list.size());
                return jsonObject.toString();
            }
        }
        return null;
    }


    @CrossOrigin
    @PostMapping(value = "/nodeCpuUtilization")
    public String nodeCpuUtilization(@RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject1 = new JSONObject(requestBody);
        String nodeName = jsonObject1.getString("nodeName");
        List<device> list = deviceService.selectIpByNodeName(nodeName);
        long start = System.currentTimeMillis();
        float cpu = 0f;
        if (list.size() != 0) {
            for (device o : list) {
                String ip = o.getDeviceIp();
                String name = o.getDeviceUserName();
                String pwd = o.getDevicePwd();
                String[] cmd = {"sar -u 1 1"};
                String[] result = ConnectLinuxCommand.execute(ip, name, pwd, cmd);
                if (result != null) {
                    float cpu1 = new ConnectLinuxCommand().cpu_utilization(result[0]);
                    cpu = cpu1 + cpu;
                }
            }
            long end = System.currentTimeMillis();
            System.out.println(end - start);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cpu_utilization", cpu / list.size());
            System.out.println(jsonObject.toString());
            return jsonObject.toString();
        }
        return null;
    }


    @CrossOrigin
    @PostMapping(value = "/nodeData")
    public String ConnectLinux(@RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject1 = new JSONObject(requestBody);
        String nodeName = jsonObject1.getString("nodeName");
        List<device> list = deviceService.selectIpByNodeName(nodeName);
        long start = System.currentTimeMillis();
        int serious1 = 0;
        int imp1 = 0;
        int common1 = 0;
        float disk = 0f;
        float memory = 0f;
        float cpu = 0f;
        float rxkb = 0f;
        float txkb = 0f;
        int serious = 0;
        int imp = 0;
        int common = 0;
        int recursion = 0;
        int cache = 0;
        if (list.size() != 0) {
            for (device o : list) {
                String ip = o.getDeviceIp();
                String name = o.getDeviceUserName();
                String pwd = o.getDevicePwd();
                String type = o.getDeviceType();
                if (type.equals("缓存")) {
                    cache++;
                } else if (type.equals("递归")) {
                    recursion++;
                }
                String[] cmd = {"df -k", "sar -r 1 1", "sar -u 1 1", "sar -n DEV 1 1"};
                String[] result = ConnectLinuxCommand.execute(ip, name, pwd, cmd);
                if (result != null) {
                    float disk1 = new ConnectLinuxCommand().disk_utilization(result[0]);
                    float memory1 = new ConnectLinuxCommand().memory_utilization(result[1]);
                    float cpu1 = new ConnectLinuxCommand().cpu_utilization(result[2]);
                    JSONObject jsonObject = new ConnectLinuxCommand().networkCard(result[3], address.getNetworkCard());
                    if (jsonObject != null) {
                        rxkb = rxkb + Float.parseFloat(jsonObject.getString("rxkB"));
                        txkb = txkb + Float.parseFloat(jsonObject.getString("txkB"));
                    }
                    disk = disk1 + disk;
                    memory = memory1 + memory;
                    cpu = cpu1 + cpu;
                    if (disk1 > 0.9 | memory1 > 90) {
                        if (o.getDeviceType().equals("缓存")) {
                            imp++;
                        } else {
                            imp1++;
                        }
                    }
                    if (cpu1 > 90) {
                        if (o.getDeviceType().equals("缓存")) {
                            common++;
                        } else {
                            common1++;
                        }
                    }
                } else if (o.getDeviceType().equals("缓存")) {
                    serious++;
                } else {
                    serious1++;
                }
            }
            long end = System.currentTimeMillis();
            System.out.println(end - start);
//        System.out.println(disk + "  " + memory + "  " + cpu + "  " + list.size());
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("disk_utilization", disk / list.size());
            jsonObject.put("memory_utilization", memory / list.size());
            jsonObject.put("cpu_utilization", cpu / list.size());
            jsonObject.put("serious", serious);
            jsonObject.put("imp", imp);
            jsonObject.put("common", common);
            jsonObject.put("serious1", serious1);
            jsonObject.put("imp1", imp1);
            jsonObject.put("common1", common1);
            jsonObject.put("cache", cache);
            jsonObject.put("recursion", recursion);
            jsonObject.put("txkb", txkb);
            jsonObject.put("rxkb", rxkb);
            jsonObject.put("bandwidth_utilization", (rxkb + txkb) / (address.getBandwidthSet() * 1024));
            System.out.println(jsonObject.toString());
            return jsonObject.toString();
        } else {
            return null;
        }
    }


    @CrossOrigin
    @PostMapping(value = "/deviceData")
    public String deviceData(@RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject1 = new JSONObject(requestBody);
        float rxkb = 0f;
        float txkb = 0f;
        String deviceIp = jsonObject1.getString("deviceIp");
        device device = deviceService.selectByIp1(deviceIp);
        String name = device.getDeviceUserName();
        String pwd = device.getDevicePwd();
        String[] cmd = {"df -k", "sar -r 1 1", "sar -u 1 1", "sar -n DEV 1 1"};
        String[] result = ConnectLinuxCommand.execute(deviceIp, name, pwd, cmd);
        if (result != null) {
            float disk = new ConnectLinuxCommand().disk_utilization(result[0]);
            float memory = new ConnectLinuxCommand().memory_utilization(result[1]);
            float cpu = new ConnectLinuxCommand().cpu_utilization(result[2]);
            JSONObject jsonObject2 = new ConnectLinuxCommand().networkCard(result[3], address.getNetworkCard());
            if (jsonObject2 != null) {
                rxkb = rxkb + Float.parseFloat(jsonObject2.getString("rxkB"));
                txkb = txkb + Float.parseFloat(jsonObject2.getString("txkB"));
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("disk_utilization", disk);
            jsonObject.put("memory_utilization", memory);
            jsonObject.put("cpu_utilization", cpu);
            jsonObject.put("rxkb", rxkb);
            jsonObject.put("txkb", txkb);
            jsonObject.put("bandwidth_utilization", (rxkb + txkb) / (address.getBandwidthSet() * 1024));
            return jsonObject.toString();
        } else {
            return "服务器连接失败";
        }
    }

    @CrossOrigin
    @PostMapping(value = "/deviceDiskUtilization")
    public String deviceDiskUtilization(@RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject1 = new JSONObject(requestBody);
        long start = System.currentTimeMillis();
        String deviceIp = jsonObject1.getString("deviceIp");
        device device = deviceService.selectByIp1(deviceIp);
        String name = device.getDeviceUserName();
        String pwd = device.getDevicePwd();
        String[] cmd = {"df -k"};
        String[] result = ConnectLinuxCommand.execute(deviceIp, name, pwd, cmd);
        if (result != null) {
            float disk = new ConnectLinuxCommand().disk_utilization(result[0]);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("disk_utilization", disk);
            long end = System.currentTimeMillis();
            System.out.println(end - start);
            return jsonObject.toString();
        } else {
            return "服务器连接失败";
        }
    }

    @CrossOrigin
    @PostMapping(value = "/deviceMemoryUtilization")
    public String deviceMemoryUtilization(@RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject1 = new JSONObject(requestBody);
        String deviceIp = jsonObject1.getString("deviceIp");
        device device = deviceService.selectByIp1(deviceIp);
        String name = device.getDeviceUserName();
        String pwd = device.getDevicePwd();
        String[] cmd = {"sar -r 1 1"};
        String[] result = ConnectLinuxCommand.execute(deviceIp, name, pwd, cmd);
        if (result != null) {
            float memory = new ConnectLinuxCommand().memory_utilization(result[0]);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("memory_utilization", memory);
            return jsonObject.toString();
        } else {
            return "服务器连接失败";
        }
    }


    @CrossOrigin
    @PostMapping(value = "/deviceCpuUtilization")
    public String deviceCpuUtilization(@RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject1 = new JSONObject(requestBody);
        String deviceIp = jsonObject1.getString("deviceIp");
        device device = deviceService.selectByIp1(deviceIp);
        String name = device.getDeviceUserName();
        String pwd = device.getDevicePwd();
        String[] cmd = {"sar -u 1 1"};
        String[] result = ConnectLinuxCommand.execute(deviceIp, name, pwd, cmd);
        if (result != null) {
            float cpu = new ConnectLinuxCommand().cpu_utilization(result[0]);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("cpu_utilization", cpu);
            return jsonObject.toString();
        } else {
            return "服务器连接失败";
        }
    }


    @CrossOrigin
    @PostMapping(value = "/deviceFlow")
    public String deviceFlow(@RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject1 = new JSONObject(requestBody);
        float rxkb = 0f;
        float txkb = 0f;
        String deviceIp = jsonObject1.getString("deviceIp");
        device device = deviceService.selectByIp1(deviceIp);
        String name = device.getDeviceUserName();
        String pwd = device.getDevicePwd();
        String[] cmd = {"sar -n DEV 1 1"};
        String[] result = ConnectLinuxCommand.execute(deviceIp, name, pwd, cmd);
        if (result != null) {
            JSONObject jsonObject2 = new ConnectLinuxCommand().networkCard(result[0], address.getNetworkCard());
            if (jsonObject2 != null) {
                rxkb = rxkb + Float.parseFloat(jsonObject2.getString("rxkB"));
                txkb = txkb + Float.parseFloat(jsonObject2.getString("txkB"));
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("rxkb", rxkb);
            jsonObject.put("txkb", txkb);
            jsonObject.put("bandwidth_utilization", (rxkb + txkb) / (address.getBandwidthSet() * 1024));
            return jsonObject.toString();
        } else {
            return "服务器连接失败";
        }
    }

    @CrossOrigin
    @PostMapping(value = "/nodeDataSelect")
    public String nodeDataSelect(@RequestBody(required = false) String requestBody) throws IOException {
        JSONObject jsonObject1 = new JSONObject(requestBody);
        List list1 = new ArrayList();
        String nodeName = jsonObject1.getString("nodeName");
        List<device> list = deviceService.selectIpByNodeName(nodeName);
        float disk = 0f;
        float memory = 0f;
        float cpu = 0f;
        if (list.size() != 0) {
            for (device o : list) {
                JSONObject jsonObject3 = new JSONObject();
                String ip = o.getDeviceIp();
                String name = o.getDeviceUserName();
                String pwd = o.getDevicePwd();
                String type = o.getDeviceType();
                String[] cmd = {"df -k", "sar -r 1 1", "sar -u 1 1"};
                String[] result = ConnectLinuxCommand.execute(ip, name, pwd, cmd);
                List list2 = new ArrayList();
                if (result != null) {
                    float disk1 = new ConnectLinuxCommand().disk_utilization(result[0]);
                    float memory1 = new ConnectLinuxCommand().memory_utilization(result[1]);
                    float cpu1 = new ConnectLinuxCommand().cpu_utilization(result[2]);
                    disk = disk1 + disk;
                    memory = memory1 + memory;
                    cpu = cpu1 + cpu;
                    if (disk1 > 0.9) {
                        list2.add("硬盘利用率过高");
                        jsonObject3.put("status1","一般");
                    }
                    if (memory1 > 90) {
                        list2.add("内存利用率过高");
                        jsonObject3.put("status1","一般");
                    }
                    if (cpu1 > 90) {
                        list2.add("内存利用率过高");
                        jsonObject3.put("status1","重要");
                    }
                    if(disk1<=0.9 && memory1<=90 && cpu1<=90){
                        list2.add("正常");
                        jsonObject3.put("status1","正常");
                    }
                } else {
                    list2.add("服务器连接失败");
                    jsonObject3.put("status1","严重");
                }
                jsonObject3.put("ip",ip);
                jsonObject3.put("type",type);
                jsonObject3.put("status",list2.toString());
                list1.add(jsonObject3);
            }
            return list1.toString();
        } else {
            return null;
        }
    }


}
