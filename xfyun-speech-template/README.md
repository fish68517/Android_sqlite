# 讯飞语音服务模块模板

这是一个独立的 Android 讯飞语音识别模块模板，用于下一个项目直接集成。

## 一、适用范围

本模板面向“讯飞开放平台 Android 语音听写 SDK”场景，适合常规移动端语音转文字。

如果你要接的是讯飞星火链路或实时转写 WebSocket 服务，则鉴权方式通常是：

- `appId`
- `apiKey`
- `apiSecret`

如果你接的是经典 Android 语音听写 SDK，初始化通常只需要：

- `APPID`

## 二、你需要准备的文件

根据讯飞官方 Android SDK 集成方式，你需要把官方 SDK 文件放到本模块中：

1. 把官方提供的 `Msc.jar` 放到：

```text
libs/Msc.jar
```

2. 把官方提供的 `libmsc.so` 等动态库放到：

```text
src/main/jniLibs/armeabi-v7a/
src/main/jniLibs/arm64-v8a/
```

3. 如果 SDK 附带 `assets/iflytek` 资源目录，也放到：

```text
src/main/assets/iflytek/
```

## 三、如何放到下一个项目

1. 把整个 `xfyun-speech-template` 目录复制到你的下一个 Android 工程。
2. 重命名目录，例如改成 `speech-xfyun`。
3. 在 `settings.gradle` 中加入模块：

```groovy
include ':speech-xfyun'
```

4. 在 `app/build.gradle` 中依赖它：

```groovy
implementation project(':speech-xfyun')
```

5. 把讯飞官方 SDK 文件按本 README 的目录要求放好。
6. 在 `XfYunSpeechConfig` 中填入你的 `APPID`。
7. 在 `Application` 或首次使用前调用初始化方法。

## 四、初始化方式

经典 Android SDK 常见初始化方式：

```kotlin
SpeechUtility.createUtility(
    context,
    SpeechConstant.APPID + "=" + appId
)
```

## 五、当前模板提供的内容

- `build.gradle` 模块脚手架
- `AndroidManifest.xml` 所需权限声明
- `XfYunSpeechConfig` 配置类
- `XfYunSpeechModule` 初始化入口
- `XfYunSpeechRecognizer` 语音识别封装
- `XfYunSpeechResultParser` 结果 JSON 解析器
- `XfYunSpeechListener` 回调接口

## 六、官方文档依据

我参考的是讯飞官方文档中的 Android 语音听写 SDK 接入说明和 Android 识别器 API 文档，核心点包括：

- Android SDK 初始化使用 `SpeechUtility.createUtility(...)`
- 识别对象使用 `SpeechRecognizer.createRecognizer(...)`
- 启动识别使用 `startListening(...)`
- 听写结果为 JSON，可按 `ws -> cw -> w` 结构解析

如果你下一条消息把你现有的讯飞示例代码、SDK 包结构或“服务码”实际字段发给我，我可以继续把这个模板精确改成与你账号完全一致的版本。
