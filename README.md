#### 介绍
某某农业大学的某某大学工小程序的体温登记程序

#### 软件架构
> JDK 1.8

* springboot
* fastJson
* httpclient（apache）

#### 使用说明

1. 下载: [jxau-tiwen-auto-bin.tar.xz](https://gitee.com/TANGMONK-MEAT/jxau-tiwen-auto/attach_files/687569/download/jxau-tiwen-auto-bin.tar.xz)
2. 解压
3. 运行：`java -jar jxau-tiwen-auto-0.0.1-SNAPSHOT.jar.jar`
4. 浏览器访问：[http://127.0.0.1:7999](http://127.0.0.1:7999)

####

如果需要修改相关配置，解压 jar 包，修改 `BOOT-INF/class/system.properties` 即可

```properties
# 定时登记配置

# 时
hourOfDay=8

# 分
minute=29

# 秒
second=0

# 间隔（默认每天一次）
period=24

# 登记失败的重试次数
retry=10

# 瞄提醒码
m=tnDqznH

# https://wlz.dev.yunwucm.com/
domainName=https://jxnydxxgcprod.zxhnzq.com/
```

