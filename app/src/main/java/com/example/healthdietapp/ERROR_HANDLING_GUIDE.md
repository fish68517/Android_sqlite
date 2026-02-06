# 错误处理和用户提示实现指南

## 概述

本应用实现了全面的错误处理和用户提示系统，包括：
- 集中式错误处理工具类
- 输入验证工具类
- 自定义异常类
- 在所有关键操作中应用错误处理

## 核心组件

### 1. ErrorHandler 工具类
位置: `app/src/main/java/com/example/healthdietapp/utils/ErrorHandler.java`

提供的方法：
- `showShortToast(Context, String)` - 显示短时间提示
- `showLongToast(Context, String)` - 显示长时间提示
- `showErrorDialog(Context, String, String)` - 显示错误对话框
- `showConfirmDialog(...)` - 显示确认对话框
- `handleDatabaseException(Context, Exception)` - 处理数据库异常
- `handleAuthenticationException(Context, String)` - 处理认证异常
- `handleValidationException(Context, String)` - 处理验证异常
- `handleNetworkException(Context, Exception)` - 处理网络异常
- `handleConflictException(Context, String)` - 处理冲突异常
- `handleGenericException(Context, Exception)` - 处理通用异常
- `logException(String, Exception)` - 记录异常日志

### 2. ValidationUtils 工具类
位置: `app/src/main/java/com/example/healthdietapp/utils/ValidationUtils.java`

提供的验证方法：
- `validateUsername(String)` - 验证用户名（3-20字符，字母数字下划线）
- `validatePassword(String)` - 验证密码（6-50字符）
- `validatePasswordConfirmation(String, String)` - 验证密码确认
- `validateNickname(String)` - 验证昵称（1-30字符）
- `validateEmail(String)` - 验证邮箱格式
- `validatePhoneNumber(String)` - 验证电话号码
- `validateTextInput(String, String)` - 验证文本输入
- `validateTextInput(String, String, int, int)` - 验证文本输入（带长度限制）
- `validateNumericInput(String, String)` - 验证数字输入
- `validateNumericInput(String, String, float, float)` - 验证数字输入（带范围限制）

所有验证方法返回 `ValidationResult` 对象，包含验证状态和错误消息。

### 3. 自定义异常类

#### AuthenticationException
用于认证失败（登录、注册错误）
```java
throw new AuthenticationException("用户名或密码错误");
```

#### ValidationException
用于数据验证失败
```java
throw new ValidationException("输入格式不正确");
```

#### DatabaseException
用于数据库操作失败
```java
throw new DatabaseException("数据库操作失败");
```

#### ConflictException
用于数据冲突（如食谱时间冲突）
```java
throw new ConflictException("该时间段已有食谱安排");
```

#### NetworkException
用于网络操作失败（预留）
```java
throw new NetworkException("网络连接失败");
```

## 实现模式

### 模式 1: 输入验证
```java
// 验证用户名
ValidationUtils.ValidationResult result = ValidationUtils.validateUsername(username);
if (!result.isValid()) {
    ErrorHandler.handleValidationException(this, result.getMessage());
    return;
}
```

### 模式 2: 数据库操作异常处理
```java
new Thread(() -> {
    try {
        // 执行数据库操作
        boolean success = userDAO.createUser(user);
        runOnUiThread(() -> {
            if (success) {
                ErrorHandler.showShortToast(this, "操作成功");
            } else {
                ErrorHandler.showShortToast(this, "操作失败");
            }
        });
    } catch (Exception e) {
        ErrorHandler.logException("ActivityName", e);
        runOnUiThread(() -> {
            ErrorHandler.handleDatabaseException(this, e);
        });
    }
}).start();
```

### 模式 3: 按钮状态管理
```java
button.setEnabled(false);
new Thread(() -> {
    try {
        // 执行操作
        runOnUiThread(() -> {
            button.setEnabled(true);
            // 显示结果
        });
    } catch (Exception e) {
        runOnUiThread(() -> {
            button.setEnabled(true);
            ErrorHandler.handleDatabaseException(this, e);
        });
    }
}).start();
```

## 已更新的活动

### 1. LoginActivity
- 使用 `ValidationUtils` 验证用户名和密码
- 使用 `ErrorHandler` 显示错误消息
- 后台线程执行数据库操作
- 异常处理和日志记录

### 2. RegisterActivity
- 验证用户名、密码和确认密码
- 检查用户名唯一性
- 密码加密失败处理
- 数据库操作异常处理

### 3. AddRecipeActivity
- 验证食谱加载
- 冲突检测和处理
- 后台线程操作
- 详细的错误消息

### 4. CreatePostActivity
- 验证帖子标题和内容长度
- 图片保存异常处理
- 发布操作异常处理
- 按钮状态管理

### 5. HealthRecordActivity
- 验证数字输入范围
- 至少一项数据验证
- 记录加载和保存异常处理
- 按钮状态管理

### 6. PreferencesSetupActivity
- 验证所有偏好选项已选择
- 后台线程保存偏好
- 异常处理和日志记录

### 7. EditProfileActivity
- 验证昵称长度
- 用户信息加载异常处理
- 个人资料更新异常处理
- 按钮状态管理

### 8. FeedbackActivity
- 验证反馈内容长度
- 反馈提交异常处理
- 按钮状态管理

## 错误消息本地化

所有错误消息都使用中文，提供友好的用户提示：
- "用户名不能为空"
- "密码至少需要6个字符"
- "用户名已存在，请选择其他用户名"
- "数据库操作失败，请重试"
- 等等

## 最佳实践

1. **始终验证用户输入** - 在提交前验证所有用户输入
2. **使用后台线程** - 所有数据库操作都在后台线程中执行
3. **管理按钮状态** - 禁用按钮防止重复提交
4. **记录异常** - 使用 `ErrorHandler.logException()` 记录异常
5. **显示友好消息** - 使用 `ErrorHandler` 显示用户友好的错误消息
6. **处理所有异常** - 在 try-catch 块中处理所有可能的异常

## 网络操作预留

`NetworkException` 和 `handleNetworkException()` 已为未来的网络操作预留。
当实现网络功能时，可以使用这些类处理网络超时和连接错误。

## 测试建议

1. 测试所有验证规则
2. 测试数据库异常处理
3. 测试网络超时处理（预留）
4. 测试冲突处理
5. 测试按钮状态管理
6. 测试异常日志记录
