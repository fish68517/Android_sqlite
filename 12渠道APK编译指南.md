# KnowledgeLabs 12 渠道 APK 编译指南

## 1. 工程说明

12 个知识点实验全部位于同一个 Android Studio 工程 `KnowledgeLabs` 中。工程使用 `app/build.gradle` 的 `productFlavors` 定义 `ch01`、`ch02`、`ch03`、`ch04`、`ch05`、`ch06`、`ch07`、`ch08`、`ch09`、`ch10`、`ch11` 和 `ch15` 共 12 个渠道。

每个渠道编译后都具有：

- 独立的应用名称；
- 独立的 `applicationId`，所以 12 个 APK 可以同时安装；
- 独立的实验启动入口；
- 相同的最低系统版本和工程依赖。

## 2. 编译环境

- Android Studio：支持 Android Gradle Plugin 8.1.1 的版本；
- JDK：17；
- Android SDK：API 34；
- Gradle Wrapper：8.0；
- 网络：首次同步 Gradle 和下载 AndroidX 依赖时需要联网。

用 Android Studio 打开以下目录，不要只打开 `app` 子目录：

```text
E:\bishe27\springboot-vue-mysql-android\Android作业\KnowledgeLabs
```

等待右下角 Gradle Sync 完成。如果 Android Studio 要求选择 Gradle JDK，请选择 JDK 17。

## 3. 在 Android Studio 中逐个切换渠道编译

1. 打开菜单 `Build -> Select Build Variant`；部分版本也可以通过左侧/右侧的 **Build Variants** 工具窗口打开。
2. 在 `app` 模块的 **Active Build Variant** 中选择一个渠道，例如 `ch01Debug`。
3. 选择菜单 `Build -> Build Bundle(s) / APK(s) -> Build APK(s)`。
4. 编译完成后点击提示框中的 `locate` 查看 APK。
5. 将 Active Build Variant 依次改为下一项，并重复编译。

应依次选择的 12 个 Debug 变体是：

```text
ch01Debug
ch02Debug
ch03Debug
ch04Debug
ch05Debug
ch06Debug
ch07Debug
ch08Debug
ch09Debug
ch10Debug
ch11Debug
ch15Debug
```

Debug APK 已由 Android 构建工具使用调试证书签名，适合本次作业安装和演示。

## 4. 使用命令行逐个编译

在 Windows PowerShell 或 CMD 中进入工程根目录：

```powershell
cd "E:\bishe27\springboot-vue-mysql-android\Android作业\KnowledgeLabs"
```

逐个执行以下命令，即可分别生成 12 个 APK：

```powershell
.\gradlew.bat assembleCh01Debug
.\gradlew.bat assembleCh02Debug
.\gradlew.bat assembleCh03Debug
.\gradlew.bat assembleCh04Debug
.\gradlew.bat assembleCh05Debug
.\gradlew.bat assembleCh06Debug
.\gradlew.bat assembleCh07Debug
.\gradlew.bat assembleCh08Debug
.\gradlew.bat assembleCh09Debug
.\gradlew.bat assembleCh10Debug
.\gradlew.bat assembleCh11Debug
.\gradlew.bat assembleCh15Debug
```

如果希望一次编译全部 12 个 Debug APK，可以运行工程中已经配置好的聚合任务：

```powershell
.\gradlew.bat assembleAllLabsDebug
```

## 5. APK 输出路径与包名

| 序号 | 渠道 | Debug APK 路径 | Debug `applicationId` |
| ---: | --- | --- | --- |
| 1 | `ch01` | `app\build\outputs\apk\ch01\debug\app-ch01-debug.apk` | `com.example.knowledgelabs.ch01.debug` |
| 2 | `ch02` | `app\build\outputs\apk\ch02\debug\app-ch02-debug.apk` | `com.example.knowledgelabs.ch02.debug` |
| 3 | `ch03` | `app\build\outputs\apk\ch03\debug\app-ch03-debug.apk` | `com.example.knowledgelabs.ch03.debug` |
| 4 | `ch04` | `app\build\outputs\apk\ch04\debug\app-ch04-debug.apk` | `com.example.knowledgelabs.ch04.debug` |
| 5 | `ch05` | `app\build\outputs\apk\ch05\debug\app-ch05-debug.apk` | `com.example.knowledgelabs.ch05.debug` |
| 6 | `ch06` | `app\build\outputs\apk\ch06\debug\app-ch06-debug.apk` | `com.example.knowledgelabs.ch06.debug` |
| 7 | `ch07` | `app\build\outputs\apk\ch07\debug\app-ch07-debug.apk` | `com.example.knowledgelabs.ch07.debug` |
| 8 | `ch08` | `app\build\outputs\apk\ch08\debug\app-ch08-debug.apk` | `com.example.knowledgelabs.ch08.debug` |
| 9 | `ch09` | `app\build\outputs\apk\ch09\debug\app-ch09-debug.apk` | `com.example.knowledgelabs.ch09.debug` |
| 10 | `ch10` | `app\build\outputs\apk\ch10\debug\app-ch10-debug.apk` | `com.example.knowledgelabs.ch10.debug` |
| 11 | `ch11` | `app\build\outputs\apk\ch11\debug\app-ch11-debug.apk` | `com.example.knowledgelabs.ch11.debug` |
| 12 | `ch15` | `app\build\outputs\apk\ch15\debug\app-ch15-debug.apk` | `com.example.knowledgelabs.ch15.debug` |

由于包名不同，可以把 12 个 APK 同时安装在同一台测试手机上，不会互相覆盖。

## 6. Release APK 与正式签名

如果老师只要求可安装 APK，使用上面的 Debug APK 即可。如果需要练习第 15 章中的正式签名概念，可在 Android Studio 中执行：

1. `Build -> Generate Signed Bundle / APK`；
2. 选择 `APK`；
3. 新建或选择自己的 `.jks` 密钥库；
4. 在 Build Variant 中选择对应渠道的 Release 变体，例如 `ch15Release`；
5. 对每个渠道分别生成签名 APK。

不要把密钥库密码提交到源码中。直接执行 `assembleCh15Release` 等命令时，如果没有配置签名，通常得到的是未签名 Release APK，不能直接替代已签名 APK 提交。

## 7. 运行前注意事项

- `ch08` 第一次读取联系人时，需要允许联系人权限；若测试设备没有联系人，列表会显示无数据提示。
- `ch09` 拍照依赖设备已安装的相机应用；网络视频需要联网。
- `ch10` 在 Android 13 及以上系统会申请通知权限，允许后才能完整观察前台下载通知。
- `ch11` 的 JSON 列表和网页页签需要联网。
- `ch15` 默认使用济南经纬度调用 Open-Meteo；也可以修改城市显示名和经纬度后刷新。该公共接口的本实验调用不要求 API Key。
- 12 个 APK 的实现目标是课程小实验演示，不包含服务器，也不按生产项目考虑安全和性能。

## 8. 清理与重新编译

如果修改源码后出现缓存问题，可先执行：

```powershell
.\gradlew.bat clean
```

然后重新执行单渠道或全部渠道的编译命令。`clean` 会删除 `app\build` 中已生成的 APK，提交前请确认已把最终 APK 复制到自己的作业提交目录。
