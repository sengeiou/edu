# 动作服务sdk构建

## 构建

* 打包jar
```shell
  ./gradlew jarSdk
```


## 发布命令

* 发布release 版:

```shell
 ./gradlew :alpha1sLib:jarSdk :alpha1sLib:artifactoryPublish -Pshapshot=false
 ```

* 发布SNAPSHOT版：

```
  ./gradlew :motionsdk:jarSdk :motionsdk:artifactoryPublish -Pshapshot=true
```

或者

```
  ./gradlew :alpha1sLib:jarSdk :alpha1sLib:artifactoryPublish
```
