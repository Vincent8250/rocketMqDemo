package cn.vincent.listener;

import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        topic = "my-topic",
        selectorType = SelectorType.TAG,
        selectorExpression = "Message_01",
        consumerGroup = "consumer-group-message_01",
        nameServer = "${rocketmq.name-server}",
        messageModel = MessageModel.CLUSTERING,// 设置消息模式 广播模式||集群模式
        consumeMode = ConsumeMode.ORDERLY // 设置消费模型 并发接受||有序接受
)
public class RocketMessage1Listener implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        System.out.println("TAG：Message_01 => Message：" + message);
    }
}
