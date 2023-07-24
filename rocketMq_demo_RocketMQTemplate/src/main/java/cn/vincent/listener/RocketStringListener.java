package cn.vincent.listener;

import cn.vincent.vo.MessageBody;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        topic = "my-topic",
        selectorExpression = "String",
        consumerGroup = "consumer-group-string",
        nameServer = "${rocketmq.name-server}",
        consumeMode = ConsumeMode.ORDERLY // 设置消费模型
)
public class RocketStringListener implements RocketMQListener<String> {
    @Override
    public void onMessage(String messageBody) {
        System.out.println(messageBody);
    }
}
