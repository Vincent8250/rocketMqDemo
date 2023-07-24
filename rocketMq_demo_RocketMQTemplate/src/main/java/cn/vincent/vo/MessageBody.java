package cn.vincent.vo;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class MessageBody {
    // 消息id
    private String messageId;
    // body组装时间
    @Builder.Default
    private long timestamp = System.currentTimeMillis();
    // 来源 附加信息
    private String msgSource;
    // overload
    private Object data;
}