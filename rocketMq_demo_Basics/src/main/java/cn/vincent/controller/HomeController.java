package cn.vincent.controller;

import cn.vincent.service.ProducerService;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mq")
public class HomeController {

    @Autowired
    ProducerService producerService;

    String[] TAGS = {"TAG_0717", "TAG_0721"};

    @GetMapping("/home")
    public String home() {
        return "welcome to rocketMq!!!";
    }

    @GetMapping("/send/{msg}")
    public String send(@PathVariable String msg) {
        Boolean flag = producerService.send("TEST-0717-TOPIC", "TAG_0717", "Topic：TEST-0717-TOPIC " + msg);
        return flag.toString();
    }

    /**
     * TAG消息过滤
     * @param msg
     * @return
     */
    @GetMapping("/tagSend/{msg}")
    public String batchSend(@PathVariable String msg) {
        Boolean flag = true;
        for (int i = 0; i < 10; i++) {
            int a = i % 2;
            System.out.println(TAGS[a]);
            flag = producerService.send("TEST-0717-TOPIC", TAGS[a], "Topic：TEST-0717-TOPIC " + msg);
        }
        return flag.toString();
    }
}