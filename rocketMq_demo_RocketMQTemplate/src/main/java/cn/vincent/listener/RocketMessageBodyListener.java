package cn.vincent.listener;

import cn.vincent.vo.MessageBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
@RocketMQMessageListener(
        topic = "my-topic",
        selectorExpression = "MessageBody",
        consumerGroup = "consumer-group-messagebody",
        nameServer = "${rocketmq.name-server}",
        consumeMode = ConsumeMode.ORDERLY // 设置消费模型
)
public class RocketMessageBodyListener implements RocketMQListener<MessageBody> {
    @Override
    public void onMessage(MessageBody messageBody) {
        log.info("MsgText：" + messageBody.getMsgSource());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info(dateFormat.format(new Date()));
    }
}
