# 消息队列



## 基础概念

- 消息队列的定义：消息队列是一种按照一定规则存储和转发消息的中间件，它将消息发送方和接收方解耦，实现异步通信和削峰填谷等功能。
- 消息队列的使用场景：消息队列可以应用于很多场景，例如异步通信、解耦服务、削峰填谷、实现事务、实现分布式锁等。
- 消息队列的基本特性：消息队列具有多种基本特性，例如可靠性、异步性、顺序性、持久性、实时性等。
- 消息队列的应用模式：消息队列的应用模式包括点对点模式和发布订阅模式，它们分别适用于不同的场景。
- 消息队列的产品选型：市面上有很多消息队列产品可供选择，例如 RabbitMQ、Kafka、ActiveMQ、RocketMQ 等，选择合适的产品需要考虑多个因素，例如可靠性、性能、易用性等。



## RocketMQ

### 入门基础

##### 安装

###### Windows

启动

~~~bash
# 启动nameServer服务
start D:\jobsoft\cloud\rocketmq\bin\mqnamesrv.cmd

# 启动broker服务
start D:\jobsoft\cloud\rocketmq\bin\mqbroker.cmd -n 127.0.0.1:9876 autoCreateTopicEnable=true
~~~

###### Linux



### 基础概念

#### 基础概念

##### 特点

- 是一个队列模型的消息中间件，**具有高性能、高可靠、高实时、分布式**等特点
- Producer、Consumer、队列都可以分布式
- Producer 向一些队列轮流发送消息，队列集合称为 Topic，Consumer 如果做广播消费，则一个 Consumer 实例消费这个 Topic 对应的所有队列，**如果做集群消费，则多个 Consumer 实例平均消费这个 Topic 对应的队列集合**
- 能够保证严格的消息顺序
- **支持拉（pull）和推（push）两种消息模式**
- 高效的订阅者水平扩展能力
- 实时的消息订阅机制
- 亿级消息堆积能力
- 支持多种消息协议，如 JMS、OpenMessaging 等
- 较少的依赖



##### 组成模块

- Name Server：Name Server 是 RocketMQ 的命名服务，它充当着路由控制器的角色。它维护了 Broker 的路由信息，可以根据 Topic 和 Consumer Group 查找到对应的 Broker 地址。
- Broker：Broker 是 RocketMQ 的消息存储和分发节点。它接收来自 Producer 发送的消息，存储消息到磁盘，并将消息分发给 Consumer。
- Producer：Producer 是 RocketMQ 的消息生产者，它负责将消息发送到 Broker。Producer 可以指定发送消息的 Topic、消息内容和消息属性等信息。
- Consumer：Consumer 是 RocketMQ 的消息消费者，它订阅 Broker 中指定 Topic 的消息，并按照指定的消费模式（如集群模式或广播模式）消费消息。
- Message：Message 是 RocketMQ 的消息体，包括 Topic、Tag、消息内容和消息属性等信息。
- Topic：Topic 是 RocketMQ 的消息主题，它是 Producer 和 Consumer 之间消息传递的逻辑概念，每个 Topic 对应着多个消息。
- Tag：Tag 是 Topic 的子分类，它可以用来区分同一 Topic 下的不同类型消息。
- Consumer Group：Consumer Group 是一组 Consumer 的集合，它们共同消费同一个 Topic 下的消息。RocketMQ 支持集群消费和广播消费两种消费模式。





##### rocketMQ模型

![image-20230717103322273](img/image-20230717103322273.png)

1. NameServer：提供轻量级的服务发现和路由
   每个 NameServer 记录完整的路由信息  提供等效的读写服务  并支持快速存储扩展
2. Broker：通过提供轻量级的 Topic 和 Queue 机制来处理消息存储
   同时支持推（push）和拉（pull）模式以及主从结构的容错机制
3. Producer：生产者 产生消息的实例
   拥有相同 Producer Group 的 Producer 组成一个集群
4. Consumer：消费者 接收消息进行消费的实例
   拥有相同 Consumer Group 的Consumer 组成一个集群



##### Topic Broker Queue

三者之间的关系

![image-20230717135347897](img/image-20230717135347897.png)



##### 发布订阅流程

1. producer生产者连接nameserver  产生数据放入不同的topic
2. 在RocketMQ中Topic可以分布在各个Broker上
   Topic分布在一个Broker上的子集 可以成为Topic分片
3. 将Topic分片再切分为若干等分  其中的一份就是一个Queue
   每个Topic分片等分的Queue的数量可以不同 由用户在创建Topic时指定
4. consumer消费者连接nameserver  根据broker分配的Queue来消费数据

![image-20230717140653331](img/image-20230717140653331.png)

##### 消息重试

默认重试16次



##### 死信队列

重试16次后 加入死信队列



##### 消息幂等

主要可能因为网络波动、业务重试等原因 造成消息重复问题

###### 解决方案

- 生产者：
- 消费者：





#### 集群概念

##### 心跳机制

- broker每30s向NameServer发送一次心跳
  - 源码中，心跳即重新发送了一次注册
  - nameserver内部维护了一个ConcurrentHashMap储存注册的broker
- nameserver每10s中检查一次心跳
- 120s未接到心跳，则认为该broker 宕机了

##### 数据同步

- Producer向nameserver拉取broker信息 不是推送
- 主从同步 : slave从master拉取数据
- consumer从broker拉取数据消费

##### 高可用 - 故障切换

- master挂掉需要运维工程师手动调整配置 把slave切换成master 不支持自动主备切换
- **version 4.5之后引入了Dledger 实现了高可用自动切换**















### 消息种类

#### 按照发送分类

- 同步消息：发送方发出数据后  **会阻塞直到MQ服务方发回响应消息**
  应用场景：例如重要通知邮件、报名短信通知、营销短信系统等（）
- 异步消息：发送方发出数据后  **不等接收方发回响应**  (异步消息需要实现回调方法 通过回调方法接受响应)
  应用场景：例如用户视频上传后通知启动转码服务  转码完成后通知推送转码结果等
- 单向消息：只负责发送消息  不等待服务器回应且没有回调函数触发
  应用场景：适用于某些耗时非常短  但对可靠性要求并不高的场景  例如日志收集

#### 按照功能分类

- 普通消息
- 顺序消息
- 广播消息
- 延时消息
- 批量消息
- 事务消息



### 发布订阅

#### 消息发布

Java中所有的发布方法

![image-20230717142932391](img/image-20230717142932391.png)

#### 消息订阅

消息订阅分为 push和pull 两种模式

- Push模式：即MQServer主动向消费端推送
- Pull模式：即消费端在需要时  主动到MQServer拉取

但是本质上rocketMQ中都是Pull模式 (Push模式是通过长轮询实现的)

#### 消费模式

- 集群消费：集群消费模式下 rocketMQ认为消息只要被集群内的一个消费者消费即可
  每条消息只被处理一次

  <img src="img/image-20230717144727721.png" alt="image-20230717144727721" style="zoom:80%;float:left;" />

- 广播消费：广播消费模式下 队列中的每条消息都会推送给所有注册过的消费者
  每条消息都需要被相同逻辑的多台机器处理
  广播消费模式下不支持顺序消息

  <img src="img/image-20230717145031982.png" alt="image-20230717145031982" style="zoom:80%;float:left;" />



可以用集群模式模拟广播模式







阶段一：入门基础

1. 学习消息队列基础知识，了解消息队列的概念和应用场景。
2. 了解 RocketMQ 的整体架构和核心组件，学习 RocketMQ 的设计理念。
3. 安装和配置 RocketMQ，学习如何启动和停止 RocketMQ 集群。
4. 使用 RocketMQ 控制台创建 topic、producer 和 consumer，学习如何发送和接收消息。

阶段二：进阶学习

1. 学习如何使用 Java 客户端 API 发送和接收消息。
2. 学习 RocketMQ 的高级特性，例如事务消息、延迟消息、顺序消息等。
3. 学习如何使用 RocketMQ 的重试机制来保证消息的可靠性。
4. 学习如何使用 RocketMQ 的过滤功能来消费指定类型的消息。
5. 学习如何使用 RocketMQ 的监控和报警功能，进行消息队列的监控和管理。
6. 学习如何使用 RocketMQ 的批量发送和消费功能，提高消息发送和消费的效率。

阶段三：实战应用

1. 学习如何在 Spring Boot 中集成 RocketMQ。
2. 实现一个简单的分布式任务调度系统，并使用 RocketMQ 作为消息队列。
3. 实现一个简单的电商系统，使用 RocketMQ 实现订单系统和库存系统之间的消息交互。
4. 学习如何在 RocketMQ 中使用消息过滤、重试机制和事务消息等高级特性，提高消息的可靠性和稳定性





### SpringBoot整合

#### 基础案例

##### 生产者

~~~java
@Service
public class ProducerService {
    private DefaultMQProducer producer = null;
    // 生产者初始化
    @PostConstruct
    public void initMQProducer() throws MQClientException {
        producer = new DefaultMQProducer("defaultGroup");// 设置生产组名称
        producer.setNamesrvAddr("localhost:9876");// 设置nameserver地址
        producer.setRetryTimesWhenSendFailed(3);
        producer.start();// 开启资源
    }
    // 发送消息
    public boolean send(String topic, String tags, String content) {
        Message msg = new Message(topic, tags, "", content.getBytes());
        try {
            producer.send(msg);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 销毁前关闭资源
    @PreDestroy
    public void shutDownProducer() {
        if(producer != null) {
            producer.shutdown();
        }
    }
}
~~~

##### 消费者

~~~java
@Service
public class ConsumerService {
    private DefaultMQPushConsumer consumer = null;
    // 初始化消费者
    @PostConstruct
    public void initMQConsumer() {
        consumer = new DefaultMQPushConsumer("defaultGroup");// 设置消费组名称
        consumer.setNamesrvAddr("localhost:9876");// 设置nameserver地址
        try {
            consumer.subscribe("TEST-0717-TOPIC", "TAG_0717");// 设置topic 和 tag过滤
            // 设置监听方法
            consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                for (MessageExt msg : msgs) {
                    System.out.println("TAG:TAG_0717 => Message Received: " + new String(msg.getBody()));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }
    // 关闭资源
    @PreDestroy
    public void shutDownConsumer() {
        if (consumer != null) {
            consumer.shutdown();
        }
    }
}
~~~



#### RocketMQTemplate 整合

##### 配置文件

~~~yaml
rocketmq:
  name-server: 127.0.0.1:9876
  producer:
    group: producer-test-group
    retry-times-when-send-failed: 5 # 消息发送失败重试次数,默认为2
    retry-times-when-send-async-failed: 5 # 异步消息发送失败重试次数,默认为2
  consumer:
    group: consumer-test-group
~~~

##### 生产者

###### 消息体

~~~java
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
    // 数据
    private Object data;
}
~~~

###### 同步消息

~~~java
@Autowired
private RocketMQTemplate rocketMQTemplate;
private String TOPIC = "my-topic";
//同步消息     
@GetMapping("/syncSend/{message}")
public String syncSend(@PathVariable("message") String message) {
    MessageBody msg = MessageBody.builder()
            .messageId(IdUtil.simpleUUID())
            .msgSource(message)
            .build();
    SendResult sendResult = rocketMQTemplate.syncSend("my-topic", msg);
    return "Message sent successfully!";
}
~~~

###### 异步消息

~~~java
@Autowired
private RocketMQTemplate rocketMQTemplate;
private String TOPIC = "my-topic";
// 异步消息     
@GetMapping("/asyncSend/{message}")
public String asyncSend(@PathVariable("message") String message) {
    MessageBody msg = MessageBody.builder()
            .messageId(IdUtil.simpleUUID())
            .msgSource(message)
            .build();
    rocketMQTemplate.asyncSend(TOPIC, msg, new SendCallback() {
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
~~~

###### 单向消息

~~~java
@Autowired
private RocketMQTemplate rocketMQTemplate;
private String TOPIC = "my-topic";
//单向消息
@GetMapping("/sendOneWay/{message}")
public String sendOneWay(@PathVariable("message") String message) {
    MessageBody msg = MessageBody.builder()
            .messageId(IdUtil.simpleUUID())
            .msgSource(message)
            .build();
    rocketMQTemplate.sendOneWay(TOPIC, msg);
    return "Message sent successfully!";
}
~~~

###### 顺序消息

顺序消息的关键是参数key - 参数key决定了消息发向那个队列
rocketmq中消息的无序性 就是因为消息发向了不同的队列

切需要在消费端设置为有序消费

~~~java
@Autowired
private RocketMQTemplate rocketMQTemplate;
private String TOPIC = "my-topic";
//顺序消息
@GetMapping("/syncSendOrderly")
public String syncSendOrderly() {
    String allKey = "all";// 使用allKey保证全局顺序 都使用一个消息队列
    for (int i = 0; i < 10; i++) {
        String key = String.valueOf(i);// 使用key保证单个顺序
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
~~~

###### 延时消息

~~~java
@Autowired
private RocketMQTemplate rocketMQTemplate;
private String TOPIC = "my-topic";
//延时消息
@GetMapping("/syncSendDelay")
public String syncSendDelay() {
    Message<MessageBody> meg = MessageBuilder.withPayload(
            MessageBody.builder()
                    .messageId(IdUtil.simpleUUID())
                    .msgSource("---延时消息测试---")
                    .build()
    ).build();
    // 开源版本只支持固定的时间 这里level-3 对应的是10s
    rocketMQTemplate.syncSend(TOPIC, meg, 5000, 3);
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    log.info(dateFormat.format(new Date()));

    // 下面的写法应该是只针对付费版的 开源版调用 没有延时效果
    //rocketMQTemplate.syncSendDelayTimeSeconds(TOPIC, msg, 20);
    return "Message sent successfully!";
}
~~~

测试结果：实际好像不止10s   ![image-20230724142501377](img/image-20230724142501377.png)

###### 批量消息

~~~java
@Autowired
private RocketMQTemplate rocketMQTemplate;
private String TOPIC = "my-topic";
//批量消息
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
    rocketMQTemplate.syncSend(TOPIC, msgs);
    return "message batch!";
}
~~~

###### 消息过滤

关于消息过滤 Tag的设置 需要注意 在rocketmq中 一个消费组只能有一个Tag
所以要实现消息过滤 需要在不同的消费组中设置不同的Tag

PS：rocketMQTemplate中Tag的设置是通过topic设置的 格式为：topic:tag
默认tag是* 代表所有 不过滤

~~~java
@Autowired
private RocketMQTemplate rocketMQTemplate;
private String TOPIC = "my-topic";
private String[] TAGS = {"Message_01", "Message_02"};
@GetMapping("/tagSend/{msg}")
public String tagSend(@PathVariable String msg) {
    for (int i = 0; i < 10; i++) {
        String topicTag = TOPIC + ":" + TAGS[i % 2];
        rocketMQTemplate.syncSend(topicTag, msg + i);
    }
    return "message tagSend";
}
~~~

###### 事务消息

RocketMQTemplate中没有实现事务消息 所以下面使用TransactionMQProducer实现事务消息

~~~java
throw new RuntimeException("sendMessageInTransaction not implement, please use TransactionMQProducer class");
~~~

事务消息中最重要的是事务监听器的实现 -> TransactionListener
executeLocalTransaction - > 在发送half消息后调用 执行本地事务  如果本地事务执行成功 RocketMQ再提交消息
checkLocalTransaction - > 用于检查事务是否执行成功 RocketMQ 依赖此方法做补偿

事务消息原理：**通过内部的两个 Topic 来实现对消息的两阶段支持**

- prepare：将消息（消息带有事务标识）投递到一个名为 **RMS_SYS_TRANS_HALF_TOPIC** 的topic中
  而不是投递到真正的 topic 中
- commit rollback：生产者再通过TransactionListener的executeLocalTransaction()执行本地事务
  事务处理成功或失败后  生产者producer会向broker发送commit或者rollback命令
  - 如果是commit会将消息投递到真实的topic中
    然后再投递一个表示删除的消息到RMS_SYS_TRANS_HALF_TOPIC中表示当前事务完成
  - 如果是 rollback 则只需投递表示删除的消息即可

~~~java
@GetMapping("/transactionSend")
public String transactionSend() throws MQClientException {
    //自定义接收RocketMQ回调的监听接口
    TransactionListener transactionListener = new TransactionListener() {
        //如果half消息发送成功了，就会回调这个方法，执行本地事务
        @Override
        public LocalTransactionState executeLocalTransaction(org.apache.rocketmq.common.message.Message message, Object o) {
            // 执行订单本地业务，并根据结构返回commit/rollback
            try {
                // 本地事务执行异常
                //throw new Exception();

                // 本地事务执行成功 返回commit
                // 本地事务逻辑 添加订单 发货之类
                return LocalTransactionState.COMMIT_MESSAGE;
            } catch (Exception e) {
                // 本地事务执行失败，返回rollback,作废half消息
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        }

        //如果没有正确返回commit或rollback，会执行此方法
        @Override
        public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
            // 查询本地事务是否已经成功执行了,再次根据结果返回commit/rollback
            try {
                // 本地事务执行成功，返回commit
                System.out.println("查询本地事务 事务成功 返回成功");
                return LocalTransactionState.COMMIT_MESSAGE;
            } catch (Exception e) {
                System.out.println("查询本地事务 事务失败 返回成功");
                // 本地事务执行失败，返回rollback,作废half消息
                return LocalTransactionState.ROLLBACK_MESSAGE;
            }
        }
    };

    TransactionMQProducer transactionMQProducer = new TransactionMQProducer();
    transactionMQProducer.setNamesrvAddr("127.0.0.1:9876");// nameserver
    transactionMQProducer.setProducerGroup("producer-test-group");// 生产组
    transactionMQProducer.setTransactionListener(transactionListener);// 设置监听
    transactionMQProducer.start();

    // 消息主体
    org.apache.rocketmq.common.message.Message message = new org.apache.rocketmq.common.message.Message();
    message.setTopic(TOPIC);
    message.setTags("String");
    message.setBody("-----测试事务消息----".getBytes());

    // 投递事务消息
    transactionMQProducer.sendMessageInTransaction(message, null);

    transactionMQProducer.shutdown();
    return "message transactionSend";
}
~~~





##### 消费者

~~~java
@Component
@RocketMQMessageListener(
        topic = "my-topic",// topic
        selectorExpression = "MessageBody",// tag
        consumerGroup = "consumer-group-messagebody",// 消费者组
        nameServer = "${rocketmq.name-server}",// nameserver
        messageModel = MessageModel.CLUSTERING,// 设置消息模式 广播模式||集群模式
        consumeMode = ConsumeMode.ORDERLY // 设置消费模型 并发接受||有序接受
)
public class RocketMessageBodyListener implements RocketMQListener<MessageBody> {
    @Override
    public void onMessage(MessageBody messageBody) {
        log.info("MsgText：" + messageBody.getMsgSource());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info(dateFormat.format(new Date()));
    }
}
~~~







### SpringCloud整合

#### SpringCloudStream 3.0之前









#### SpringCloudStream 3.0之后



### 集群搭建

#### 高可用 - Dledger





### 高级特性

#### 工作流程

1. 启动NameServer,NameServer启动后开始监听端口 等待broker、producer、consumer连接

2. 启动broker时 broker会和所有的NameServer建立并保持长链接  然后每隔30s向NameServer定时发送心跳包

3. 发送消息前 可以先创建Topic
   创建Topic时需要指定该topic要存储在哪些broker上 同时也会将topic和broker的关系写入到NameServer
   不过这步并不是必需的 也可以在发送消息时自动创建topic

   手动创建Topic有两种模式：

      1）集群模式：该模式下创建的Topic在该集群中，所有Broker的Queue数量是相同的。

      2）Broker模式：该模式下创建的Topic在该集群中，每个Broker的Queue数量可以不同。

   自动创建Topic时，默认采用Broker模式，会为每个Broker默认创建4个Queue

4. producer发送消息 启动时与NameServer集群中的其中一台建立长链接 并从NameServer中获取路由信息 即Topic和Queue与Broker的地址(IP+Port)的映射关系 
   然后根据算法策略从队列选择一个Queue与队列所在的broker建立长链接从而向broker发送消息 
   在获取到路由信息后 Producer会首先将路由信息缓存到本地 再每隔30s从NameServer更新一个路由信息

5. Consumer和NameServer建立长链接 获取其所订阅的Topic信息 然后根据算法策略从路由信息中获取到所要消费的Queue然后和broker建立长链接 开始消费其中的消息 Consumer在获取到路由信息后 也会每隔30s从NameServer更新一次路由信息 
   不过和Producer不同的是 Consumer还会向Broker发送心跳 以确保Broker的存活状态·







#### 存储结构

![image-20230725135801904](img/image-20230725135801904.png)







### 面试整理

- RocketMQ Broker中的消息被消费后会立即删除吗？
  不会 每条消息都会持久化到CommitLog中 每个Consumer连接到Broker后会维持消费进度信息
  当有消息消费后 只是当前Consumer的消费进度（CommitLog的offset）更新了 不会删除
  清理机制是默认72小时后会删除不再使用的CommitLog文件
- RocketMQ消费模式有几种？
  集群消费：一条消息只会被同Group中的一个Consumer消费 多个Group可以多次消费
  广播消费：Group中所有订阅的Consumer实例都消费一遍
- 消费消息是push还是pull？
  RocketMQ没有真正意义的push 都是pull
  虽然有push类 但实际底层实现采用的是长轮询机制 即拉取方式
- 为什么要主动拉取消息而不使用事件监听方式？
  如果broker主动推送消息的话有可能push速度快 消费速度慢的情况 那么就会造成消息在consumer端堆积过多 同时又不能被其他consumer消费的情况
  而pull的方式可以根据当前自身情况来pull 不会造成过多的压力而造成瓶颈 所以采取了pull的方式
- broker如何处理拉取请求的？
  Consumer首次请求Broker Broker中是否有符合条件的消息
  - 有 ->响应Consumer 等待下次Consumer的请求
  - 没有 ->PullRequestHoldService来Hold连接 每个5s执行一次检查pullRequestTable有没有消息 有的话立即推送
    每隔1ms检查commitLog中是否有新消息 有的话写入到pullRequestTable 当有新消息的时候返回请求
    挂起consumer的请求 即不断开连接也不返回数据使用consumer的offset
- RocketMQ如何做负载均衡？
  - producer发送消息的负载均衡：默认会轮询向Topic的所有queue发送消息 以达到消息平均落到不同的queue上
    而由于queue可以落在不同的broker上 就可以发到不同broker上（当然也可以指定发送到某个特定的queue上）
  - consumer订阅消息的负载均衡：假设有5个队列 两个消费者 则第一个消费者消费3个队列 第二个则消费2个队列 以达到平均消费的效果
    而需要注意的是 当consumer的数量大于队列的数量的话 根据rocketMq的机制 多出来的Consumer不会去消费数据
    因此建议consumer的数量小于或者等于queue的数量避免不必要的浪费
- 消息重复消费？
  影响消息正常发送和消费的重要原因是网络的不确定性
  - 原因
    - ACK问题：正常情况下在consumer真正消费完消息后应该发送ack，通知broker该消息已正常消费，从queue中剔除
      当ack因为网络原因无法发送到broker，broker会认为此条消息没有被消费，此后会开启消息重投机制把消息再次投递到consumer
    - 消费模式：在CLUSTERING模式下，消息在broker中会保证相同group的consumer消费一次，但是针对不同group的consumer会推送多次
  - 解决方法：
    - 去重操作直接放在了消费端 消费端处理消息的业务逻辑保持幂等性
    - 数据库表记录消息主键
- 如何让RocketMQ保证消息的顺序消费？
  多个queue只能保证单个queue里的顺序
- 怎么保证消息发到同一个queue？
  如果是rocketMQTemplate的话直接设置key就行
  如果是原生的api的话 就要自己实现选择算法
- RocketMQ如何保证消息不丢失？
  - Producer端如何保证消息不丢失
    采取send()同步发消息 发送结果是同步感知的 发送失败后可以重试 设置重试次数 默认3次
    producer.setRetryTimesWhenSendFailed(10);
    集群部署 比如发送失败了的原因可能是当前Broker宕机了 重试的时候会发送到其他Broker上
  - Broker端如何保证消息不丢失
    修改刷盘策略为同步刷盘 默认情况下是异步刷盘的
    flushDiskType = SYNC_FLUSH
    集群部署 主从模式 高可用
  - Consumer端如何保证消息不丢失
    完全消费正常后在进行手动ack确认
- 堆积时间过长消息超时了？
  RocketMQ中的消息只会在commitLog被删除的时候才会消失 不会超时 也就是说未被消费的消息不会存在超时删除这情况
- 堆积的消息会不会进死信队列？
  不会 消息在消费失败后会进入重试队列（%RETRY%+ConsumerGroup）16次 才会进入死信队列（%DLQ%+ConsumerGroup）
- RocketMQ在分布式事务支持这块机制的底层原理?
  Half Message：预处理消息 当broker收到半消息后 会存储到RMQ_SYS_TRANS_HALF_TOPIC的topic消息队列中
  本地事务逻辑处理成功 消息会从RMQ_SYS_TRANS_HALF_TOPIC中放到真正的目标topic消息队列中





































































## RabbitMQ

### 基础知识

#### 组件：

Virtual host

> 虚拟主机，每一个虚拟主机中包含所有的AMQP基本组件，用户、队里、交换器等都是在虚拟主机里面创建。典型的用法是，如果公司的多个产品只想用一个服务器，就可以把他们划分到不同的虚拟主机中，里面的任何信息都是独立存在，互不干扰。

Connection

> 连接，应用程序和服务器之间的TCP连接。

Channel

> 通道，当你的应用程序和服务器连接之后，就会创建TCP连接。一旦打开了TCP连接，就可以创建一个Channel通道，所以说Channel通道是一个TCP连接的内部逻辑单元。 这是因为，创建和销毁TCP连接是比较昂贵的开销，每一次访问都建立新的TCP连接的话，不仅是巨大浪费，而且还容易造成系统性能瓶颈。

Queue

> 队列，所有的消息最终都会被送到这里，等待着被感兴趣的人取走。

Exchange

> 交换器，消息到达服务的第一站就是交换器，然后根据分发规则，匹配路由键，将消息放到对应队列中。值得注意的是，交换器的类型不止一种。
>
> - Direct 直连交换器，只有在消息中的路由键和绑定关系中的键一致时，交换器才把消息发到相应队列
> - Fanout 广播交换器，只要消息被发送到广播交换器，它会将消息发到所有的队列
> - Topic 主题交换器，根据路由键，通配规则(*和#)，将消息发到相应队列

Binding

> 绑定，交换器和队列之间的绑定关系，绑定中就包含路由键，绑定信息被保存到交换器的查询表中，交换器根据它分发消息。



### 消息收发方式

#### hello word 简单模式

> 一个生产者 一个消费者（一条消息只能被一个消费者消费一次）
>
> **生产者直接将消息传入队列（其实是有默认的交换机的）**



#### work queues 工作模式

> 一个生产者 多个消费者
>
> **由队列对消息进行分配 会分配到不同的消费者手中**



#### publish/subscribe 发布订阅模式

> 一个生产者 多个消费者
>
> **生产者将消息发送到交换机 而不是队列  每个消费者绑定自己的队列  每个队列绑定交换机**
> **生产者的消息将从交换机到达队列 实现消息被多个消费者消费的目的（如果交换机没有和队列进性绑定  那么消息会丢失 交换机不具备存储消息的能力 只有队列具备存储消息的能力）**



Direct 直连交换机

> 需要将 =》交换机、队列、routing_key 三者绑定 **交换机会将消息根据routing_key发送到指定队列**

Fanout 扇形交换机

> 需要将 =》交换机、队列、routing_key 三者绑定 **交换机会将消息发送到所有与交换机进性绑定的队列 这里的routing_key的作用几乎没有**

Topic 主题交换机

> 需要将 =》交换机、队列、routing_key三者绑定 **交换机会将消息根据routing_key的路由规则发送到指定队列（这里并非direct的直接匹配可以进行模糊匹配）**
>
> 在实际使用中topic交换机使用的较多

Header 头部交换机

> 需要将 =》交换机、队列、routing_key三者绑定 **交换机会将消息根据消息头部中的数据进性匹配判断发送到指定队列 这里的routing_key的作用几乎没有**



#### Routing 路由模式



#### Topics 主题模式



#### RPC 远程调用



#### Publisher/confirms 消息的确认机制



#### 总结：

需要明白的是 这七种消息的收发方式 并不是平行的 而是可以交叉使用的 他们描述的是不同对象之间的收发关系

发布订阅模式中的四种交换机其实描述的是交换机到队列的方式 工作模式描述的是队列到消费者的方式













































