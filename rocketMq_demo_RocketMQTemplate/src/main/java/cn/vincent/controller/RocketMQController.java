package cn.vincent.controller;

import cn.hutool.core.util.IdUtil;
import cn.vincent.vo.MessageBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.DelayMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/mq")
public class RocketMQController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private String TOPIC = "my-topic";

    // region 消息发送

    /**
     * 同步消息
     *
     * @param message
     * @return
     */
    @GetMapping("/syncSend/{message}")
    public String syncSend(@PathVariable("message") String message) {
        MessageBody msg = MessageBody.builder()
                .messageId(IdUtil.simpleUUID())
                .msgSource(message)
                .build();
        SendResult sendResult = rocketMQTemplate.syncSend(TOPIC + ":MessageBody", msg);
        return "Message sent successfully!";
    }

    /**
     * 异步消息
     *
     * @param message
     * @return
     */
    @GetMapping("/asyncSend/{message}")
    public String asyncSend(@PathVariable("message") String message) {
        MessageBody msg = MessageBody.builder()
                .messageId(IdUtil.simpleUUID())
                .msgSource(message)
                .build();
        rocketMQTemplate.asyncSend(TOPIC + ":MessageBody", msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                //    成功处理
            }

            @Override
            public void onException(Throwable throwable) {
                //    异常处理
            }
        });
        return "Message sent successfully!";
    }

    /**
     * 单向消息
     *
     * @param message
     * @return
     */
    @GetMapping("/sendOneWay/{message}")
    public String sendOneWay(@PathVariable("message") String message) {
        MessageBody msg = MessageBody.builder()
                .messageId(IdUtil.simpleUUID())
                .msgSource(message)
                .build();
        rocketMQTemplate.sendOneWay(TOPIC + ":MessageBody", msg);
        return "Message sent successfully!";
    }

    /**
     * 顺序消息
     *
     * @return
     */
    @GetMapping("/syncSendOrderly")
    public String syncSendOrderly() {
        String allKey = "all";
        for (int i = 0; i < 10; i++) {
            String key = String.valueOf(i);
            MessageBody msg1 = MessageBody.builder()
                    .messageId(IdUtil.simpleUUID())
                    .msgSource("订单  key：" + key)
                    .build();
            rocketMQTemplate.syncSendOrderly(TOPIC + ":MessageBody", msg1, allKey);

            MessageBody msg2 = MessageBody.builder()
                    .messageId(IdUtil.simpleUUID())
                    .msgSource("发货  key：" + key)
                    .build();
            rocketMQTemplate.syncSendOrderly(TOPIC + ":MessageBody", msg2, allKey);
        }
        return "Message sent successfully!";
    }

    /**
     * 延时消息
     *
     * @return
     */
    @GetMapping("/syncSendDelay")
    public String syncSendDelay() {
        GenericMessage<MessageBody> genericMessage = new GenericMessage<>(
                MessageBody.builder()
                        .messageId(IdUtil.simpleUUID())
                        .msgSource("---延时消息测试---")
                        .build()
        );
        Message<MessageBody> meg = MessageBuilder.withPayload(
                MessageBody.builder()
                        .messageId(IdUtil.simpleUUID())
                        .msgSource("---延时消息测试---")
                        .build()
        ).build();

        // 开源版本只支持固定的时间
        rocketMQTemplate.syncSend(TOPIC + ":MessageBody", meg, 5000, 3);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info(dateFormat.format(new Date()));

        // 下面的写法应该是只针对付费版的 开源版调用 没有延时效果
        //rocketMQTemplate.syncSendDelayTimeSeconds(TOPIC, msg, 20);
        return "Message sent successfully!";
    }

    /**
     * 批量消息
     *
     * @return
     */
    @GetMapping("/syncBatchSend")
    public String syncBatchSend() {
        ArrayList<Message> msgs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Message<MessageBody> msg = MessageBuilder.withPayload(
                    MessageBody.builder()
                            .messageId(IdUtil.simpleUUID())
                            .msgSource("---批量消息测试：" + i)
                            .build())
                    .build();
            msgs.add(msg);
        }
        rocketMQTemplate.syncSend(TOPIC + ":MessageBody", msgs);
        return "message batch!";
    }

    // 事务消息

    // 流量控制

    // 消息过滤
    String[] TAGS = {"Message_01", "Message_02"};

    //关于消息过滤 Tag的设置 需要注意 在rocketmq中 一个消费组只能有一个Tag
    //所以要实现消息过滤 需要在不同的消费组中设置不同的Tag
    //PS：rocketMQTemplate中Tag的设置是通过topic设置的 格式为：topic:tag
    //默认tag是* 代表所有 不过滤
    @GetMapping("/tagSend/{msg}")
    public String tagSend(@PathVariable String msg) {
        for (int i = 0; i < 10; i++) {
            String topicTag = TOPIC + ":" + TAGS[i % 2];
            rocketMQTemplate.syncSend(topicTag, msg + i);
        }
        return "message tagSend";
    }

    // 消息重试

    // 秒杀设计

    // 消息零丢失

    // endregion
}
