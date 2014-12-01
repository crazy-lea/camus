# Intro
Camus is LinkedIn's [Kafka](http://kafka.apache.org "Kafka")->HDFS pipeline. It is a mapreduce job that does distributed data loads out of Kafka. 

The original README is [Here](./README.orig.md)

In this file we maintain our own README for internal project usage


## Our Usage model

在基本保持原版camus的原有功能下, 结合mogujie的需求对camus做了一些定制. 主要如下:

* 支持camus的动态配置, 可通过后台管理.
* 新增mapreduce限速, 以防止kafka负载过大.
* 根据我们的需求定制MessageDecoder, Partitioner和WriteProvider.
* 新增lzo压缩.
* 新增监控接口.


## Config
配置大体上和原版camus一致, 完整的配置列表可以参考 [这里](https://github.com/linkedin/camus/wiki/Configuration-Parameters). 在camus的基础上, 新增了几个配置.

#### 需要注意的配置列表为:

| 配置key | 含义 | 是否新增 |
| --- | --- | --- |
| groupName | 用于给配置分组. 通过groupName获取配置, 同时也会用来生成 camus.job.name, kafka.client.name 这2个配置 | 是 |
| kafka.mapper.max.qps | 每个mapper的最大qps, 单位为KB/s | 是 |
| etl.camus.monitor.class | 用于监控的类名. 当任务失败时, 或者达到pullTime而终止时, 会调用该类中的方法 | 是 |
| etl.partitioner.class | 用于分区的类名 |
| etl.record.writer.provider.class | 用于写入hdfs的类名 | 
| camus.message.decoder.class | 解析kafka消息的类名 |
| etl.destination.path | 日志文件的写入目录
| etl.execution.base.path | 执行文件目录, 存放正在运行的job的offset, error等信息 |
| etl.execution.history.path | 历史执行文件目录, 存放已运行job的offset, error等信息. 一般为 etl.execution.base.path 下的子目录 |
| fs.default.name | hdfs集群namenode
| kafka.whitelist.topics | topic的白名单, 在该名单之内的topic才会生效. topic间以","隔开, 支持正则表达式 |
| etl.output.codec | 压缩类型 |
| kafka.max.pull.minutes.per.task | mapper的最大pull时间, 单位为分钟. 当达到最大时间之后, mapper会停止从kafka pull消息, 并调用 etl.camus.monitor.class 中的监控接口 |
| mapred.map.tasks | 最大的mapper数. 如果该值小于 topic-partition 数, 会多个分区共用一个mapper |
| kafka.move.to.last.offset.list | 将指定的topic列表的offset移动到最新处, 为all时表示所有topic |


#### etl.camus.monitor.class
目前只有一个可选值, com.linkedin.camus.etl.kafka.monitor.MoguCamusMonitor. 当任务失败, 或者达到最大pullTime而终止时, 会发送报警短信.

#### etl.partitioner.class

推荐使用 com.linkedin.camus.etl.kafka.coders.MoguPartitioner, 该类是为mogujie定制的分区类, 会将文件名设置为 yyyyMMdd_HH_partition_offset. 比如 20141201_19_002_016195751326.lzo.

#### camus.message.decoder.class 

除了原版camus内置的 com.linkedin.camus.etl.kafka.coders.JsonStringMessageDecoder 的之外, 还增加了2个定制的decoder:

| 类名 | 说明 | 如何获取时间戳
| --- | --- | --- |
com.linkedin.camus.etl.kafka.coders.JsonStringMessageDecoder | 解析json格式的数据 | 通过 camus.message.timestamp.field 配置时间戳的字段, 通过 timestamp 配置时间戳的类型
com.linkedin.camus.etl.kafka.coders.RawMessageDecoder | 不对日志进行解析, 直接传给 WriteProvider | 读取该消息的时间
com.linkedin.camus.etl.kafka.coders.MoguCrondDecoder | 为mogujie crond日志专门定制的decoder | 读取crond日志最开头的 "yyyy-MM-dd HH:mm:ss" 部分, 作为消息的时间戳

#### etl.record.writer.provider.class

目前可用 com.linkedin.camus.etl.kafka.common.StringRecordWriterProvider, 将消息原样写入hdfs. 支持压缩.

#### etl.output.codec

支持的压缩列表: snappy, lzo, lzop. 目前推荐适用lzop.

#### 新增定制类

如果感觉现有的 etl.camus.monitor.class, etl.partitioner.class, camus.message.decoder.class, etl.record.writer.provider.class无法满足需求, 完全可以自己新增定制类. 只要实现相应的接口即可.

#### 配置后台

camus的配置后台地址为 http://data.mogujie.org/exch/camus/list.htm, 目前只有数据平台的几个铁蛋有权限进入!!!

每次执行mapreduce之前, 会根据groupName取出该group对应的配置, 写入 /etc/camusconfig/ 目录. 如果侦测到配置有问题, 报警后继续使用之前的配置. 当前修改后的配置, 会在下次执行mapreduce时生效.

获取group配置的api的url为 http://data.mogujie.org/exch/api/camusconfig/requestone.htm?groupName=, 例如 [datacenter](http://data.mogujie.org/exch/api/camusconfig/requestone.htm?groupName=datacenter).


#### 其他要注意的点

* 如果要同一时间启动多个group的camus job, 注意要给它们配置不同的 etl.execution.base.path, etl.execution.history.path. 以免混在一起.
* 如果同一个topic出现在多个group中, 这些group之间不仅 etl.execution.base.path, etl.execution.history.path 要不同, etl.destination.path 的配置也要不同, 否则会互相影响.
* lzop的压缩格式, 其文件的后缀名为".lzo".

## Running Camus

在工程下有 datacenter.sh 和 items_action.sh 这2个shell文件. qihe2191 上的crontab每隔10分钟调用执行一次这2个文件.

## TODO List


