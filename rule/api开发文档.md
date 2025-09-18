
# api开发规则
接口只使用get请求或post请求,不使用delete、put、patch请求。
api接口开发严格遵循以下要求。
## 一、API 合约概述

### 1.1 合约目标

定义前后端交互的标准接口规范，明确接口设计、参数格式、响应逻辑等核心要素，确保前后端开发的一致性、可测试性及协作效率。

### 1.2 合约原则

- **接口优先**：API 设计先行于代码实现，所有开发基于已确认的接口合约开展
- **版本管理**：支持 API 版本迭代，版本变更需同步更新合约并通知相关方
- **标准化**：统一请求 / 响应格式、数据类型、状态码等基础规范
- **可测试**：每个接口需包含明确的测试用例（输入、预期输出），支持独立验证
- **可追溯**：合约变更需记录版本号、修改内容、责任人及时间，便于回溯

## 二、通用规范

### 2.1 基础信息

| 项目             | 内容                           | 说明                                                         |
| ---------------- | ------------------------------ | ------------------------------------------------------------ |
| **Base URL**     | `http://localhost:8080/api/v1` | 接口基础路径，版本号（v1）需随重大变更升级                   |
| **Content-Type** | `application/json`             | 统一采用 JSON 格式传输数据                                   |
| **字符编码**     | `UTF-8`                        | 确保多语言字符兼容                                           |
| **时间格式**     | `ISO 8601`                     | 示例：`2025-01-09T10:30:00Z`（UTC 时间）、`2025-01-09T18:30:00+08:00`（带时区） |
| **请求方法**     | 遵循 RESTful 规范              | GET（查询）、POST（创建）、PUT（全量更新）、PATCH（部分更新）、DELETE（删除） |

### 2.2 统一响应格式



```typescript
interface ApiResponse<T> {
  code: number;           // 业务状态码（非HTTP状态码，与下表对应）
  message: string;        // 响应描述信息（成功/失败原因）
  data: T | null;         // 响应数据（成功时为业务数据，失败时可为null或错误详情）
  timestamp: string;      // 服务器响应时间戳（ISO 8601格式）
}
```

### 2.3 状态码定义

| 状态码 | 含义       | 适用场景                     | 前端处理建议                                |
| ------ | ---------- | ---------------------------- | ------------------------------------------- |
| 200    | 成功       | 请求处理完成且结果正常       | 解析 data 展示业务数据                      |
| 201    | 创建成功   | 资源（如用户、订单）创建完成 | 可跳转至详情页或提示创建结果                |
| 400    | 请求错误   | 参数格式错误、缺失必填项等   | 展示具体参数错误信息（如`data.errors`）     |
| 401    | 未授权     | 未登录、token 过期或无效     | 跳转至登录页                                |
| 403    | 禁止访问   | 登录状态下无操作权限         | 提示 "无权限"，可跳转至首页                 |
| 404    | 资源不存在 | 请求的 URL 或资源 ID 不存在  | 提示 "资源不存在"                           |
| 409    | 资源冲突   | 资源已存在（如用户名重复）   | 提示冲突原因（如 "用户名已被注册"）         |
| 423    | 账户锁定   | 账户因安全原因被临时锁定     | 提示锁定原因及解锁方式                      |
| 500    | 服务器错误 | 后端逻辑异常、数据库错误等   | 提示 "系统繁忙，请稍后重试"，不展示技术细节 |

## 三、API 接口设计示例
api接口开发完成需生成api接口文档.md 保存在文件夹 ‘api接口文档’目录下
### 3.1 用户注册接口

#### 基本信息

- **接口标识**：`AUTH_REGISTER`（全局唯一，用于文档索引和开发沟通）
- **请求路径**：`POST /auth/register`（完整路径：Base URL + 此路径）
- **接口描述**：新用户通过用户名、邮箱和密码创建账户
- **认证要求**：无需认证（公开接口）
- **适用业务单元**：用户注册流程（独立业务单元，需同步开发前端注册页面）

#### 请求参数

```typescript
interface RegisterRequest {
  username: string;        // 用户名，3-20字符，仅含字母、数字、下划线
  email: string;           // 邮箱地址，需符合标准格式（如xxx@xx.com）
  password: string;        // 密码，8-32字符，必须包含大小写字母和数字
  confirmPassword: string; // 确认密码，需与password完全一致
}
```

#### 参数验证规则

```json
{
  "username": {
    "required": true,
    "type": "string",
    "minLength": 3,
    "maxLength": 20,
    "pattern": "^[a-zA-Z0-9_]+$",
    "message": "用户名只能包含字母、数字、下划线，长度3-20字符"
  },
  "email": {
    "required": true,
    "type": "email",
    "message": "请输入有效的邮箱地址（如xxx@xx.com）"
  },
  "password": {
    "required": true,
    "type": "string",
    "minLength": 8,
    "maxLength": 32,
    "pattern": "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)", // 至少1个小写、1个大写、1个数字
    "message": "密码必须包含大小写字母和数字，长度8-32字符"
  },
  "confirmPassword": {
    "required": true,
    "type": "string",
    "custom": "mustMatchPassword", // 自定义验证：与password字段值一致
    "message": "确认密码必须与密码一致"
  }
}
```

#### 响应示例

##### 成功响应（状态码 201）

```typescript
interface RegisterSuccessResponse {
  code: 201;
  message: "注册成功，已发送验证邮件";
  data: {
    userId: number;        // 系统生成的唯一用户ID
    username: string;      // 注册时提交的用户名
    email: string;         // 注册时提交的邮箱（用于后续登录）
    createdAt: string;     // 账户创建时间（ISO 8601格式）
  };
  timestamp: string;
}
```

##### 失败响应

- **用户名已存在（409）**

  

  ```json
  {
    "code": 409,
    "message": "用户名已被注册，请更换",
    "data": null,
    "timestamp": "2025-01-09T10:30:00Z"
  }
  ```

- **参数验证失败（400）**

  ```json
  {
    "code": 400,
    "message": "参数验证失败，请检查输入",
    "data": {
      "errors": [
        {
          "field": "username",
          "message": "用户名只能包含字母、数字、下划线"
        },
        {
          "field": "password",
          "message": "密码必须包含大小写字母和数字"
        }
      ]
    },
    "timestamp": "2025-01-09T10:31:00Z"
  }
  ```

#### 调试说明

- **测试用例 1（成功场景）**：
  - 请求体：`{"username":"test_user1","email":"test@example.com","password":"Test1234","confirmPassword":"Test1234"}`
  - 预期响应：code=201，data 包含 userId 等信息
- **测试用例 2（失败场景 - 用户名重复）**：
  - 请求体：`{"username":"existing_user","email":"new@example.com","password":"Test1234","confirmPassword":"Test1234"}`
  - 预期响应：code=409，message="用户名已被注册"
- **跨域配置**：后端需允许前端域名的跨域请求（Access-Control-Allow-Origin）

## 四、API接口测试
### api测试报告保存在文件夹 ‘api测试报告’ 下，
### 使用curl工具进行API接口测试
### api测试请覆盖所有场景，包括接口基本信息、请求参数、响应示例、

