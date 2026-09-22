# KnowledgeLabs：12 个知识点实验单工程版

本目录是一个 Android Studio 工程，通过 Gradle `productFlavors` 提供 12 个独立渠道。每个渠道具有独立应用名称、包名和启动功能，因此可以从同一套工程源码分别生成 12 个 APK。

## 技术范围

- Android 原生应用，`minSdk 26`、`targetSdk 34`、JDK 17。
- 主要业务代码采用 Java。
- 第 2 章为了覆盖课程要求中的 Kotlin 语法，保留一个 Kotlin Activity；其余 11 个实验均采用 Java。
- 不使用服务器、MySQL、Spring Boot 或 Vue。
- 第 15 章天气数据来自 Open-Meteo Forecast API，无需在源码中配置 Token。

## 渠道与功能

| 渠道 | 实验入口 | 已实现内容 |
| --- | --- | --- |
| `ch01` | `HelloStartActivity` | HelloWorld、SDK/设备诊断、Activity 生命周期和 Log |
| `ch02` | `KotlinToolboxActivity` | Kotlin 单位换算、成绩集合统计、空安全、类与函数式 API |
| `ch03` | `ActivityNotebookActivity` | 两个 Activity、显式/隐式 Intent、数据传递、结果回传和 Toast |
| `ch04` | `ComponentGalleryActivity` | 常用控件、RecyclerView、ImageView、进度条和 AlertDialog |
| `ch05` | `FragmentNewsActivity` | Fragment 动态加载、Activity 回调、手机单栏和平板双栏 |
| `ch06` | `BroadcastSessionActivity` | 系统电量广播、自定义强制下线广播和动态注册 |
| `ch07` | `LocalVaultActivity` | SharedPreferences 账号记忆、SQLite 备忘录增删改查 |
| `ch08` | `ContactViewerActivity` | 危险权限、ContentResolver 和联系人 URI 查询 |
| `ch09` | `MediaCenterActivity` | 相机/相册 Intent、通知渠道、提示音和 VideoView |
| `ch10` | `BackgroundDownloaderActivity` | 前台 Service、工作线程、Handler、通知和进度广播 |
| `ch11` | `NetworkJsonClientActivity` | HttpURLConnection、JSON 列表和 WebView 页签切换 |
| `ch15` | `WeatherLiteActivity` | Open-Meteo GET/JSON、天气图标、三日预报和下拉刷新 |

统一入口为 `LabLauncherActivity`。编译时生成的 `BuildConfig.LAB_ID` 决定该 APK 启动哪个实验页面，其余页面不会出现在桌面入口中。

具体编译方法见 [12渠道APK编译指南.md](./12渠道APK编译指南.md)。
