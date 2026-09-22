# 校园导览助手（Java Android）

一个可离线运行的校园导览综合实训项目。项目基于附件给出的 Android Gradle Plugin 8.1.1、Gradle 8.4、compileSdk 34、minSdk 25 配置，并使用 Java 8 源码兼容级别。

## 功能与作业要求对应

| 作业要求 | 项目实现 |
| --- | --- |
| 登录功能 | `LoginActivity`，含输入校验、登录状态保存与退出登录 |
| 导航栏 | `BottomNavigationView`，首页、地点、路线、收藏、我的 5 个栏目 |
| 列表展示 | `RecyclerView` 展示 6 个校园地点 |
| 图文混合 | 每个地点卡片包含本地矢量插画、名称、分类和介绍 |
| Intent 页面跳转 | 地点详情、路线详情、关于页面均通过显式 Intent 跳转 |
| 轮播图 | 首页使用 `ViewPager2 + TabLayout`，每 3.5 秒自动轮播 |
| 页面不少于 6 个 | 登录、首页、地点、路线、收藏、我的、地点详情、路线详情、关于，共 9 类页面 |

附加功能：地点关键词搜索、收藏持久化、三条精选路线、模拟导航提示、自动登录、本地离线数据。

## 运行方式

推荐使用 Android Studio 打开项目根目录，等待 Gradle 同步后运行 `app`。环境要求：

- Android Studio（JDK 17）
- Android SDK Platform 34
- Windows PowerShell 5.1 或 PowerShell 7

也可在项目根目录右键使用 PowerShell 执行：

```powershell
Set-ExecutionPolicy -Scope Process Bypass
.\运行校园导览.ps1
```

只构建 APK：

```powershell
.\运行校园导览.ps1 -仅构建
```

演示账号：`20260001`，密码：`123456`。实际上任意非空用户名与不少于 6 位的密码均可进入，便于课堂验收。

Debug APK 输出位置：`app\build\outputs\apk\debug\app-debug.apk`。交付包根目录还附带了一份已构建并完成签名校验的 `校园导览助手-debug.apk`，可直接安装到 Android 7.1（API 25）及以上设备。
