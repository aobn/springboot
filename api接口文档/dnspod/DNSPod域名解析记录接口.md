# DNSPod域名解析记录接口文档

## 一、接口概述

### 1.1 接口目标

提供腾讯云DNSPod域名解析记录查询功能，支持获取指定域名的解析记录筛选列表，包括A记录、CNAME记录、MX记录等各种类型的DNS解析记录。

### 1.2 接口特点

- **安全认证**：使用腾讯云SecretId和SecretKey进行身份验证
- **灵活筛选**：支持多种筛选条件，包括子域名、记录类型、备注等
- **分页查询**：支持分页获取大量解析记录
- **统一响应**：遵循项目统一的ApiResponse响应格式

## 二、基础信息

| 项目             | 内容                           | 说明                                                         |
| ---------------- | ------------------------------ | ------------------------------------------------------------ |
| **Base URL**     | `http://localhost:8080/api/dnspod` | DNSPod接口基础路径                                          |
| **Content-Type** | `application/json`             | 统一采用 JSON 格式传输数据                                   |
| **认证方式**     | JWT Token                      | 需要在请求头中包含有效的JWT令牌                              |
| **腾讯云认证**   | SecretId + SecretKey           | 通过环境变量配置腾讯云API密钥                                |

## 三、API接口详情

### 3.1 获取域名解析记录筛选列表（JSON格式）

#### 基本信息

- **接口标识**：`DNSPOD_GET_RECORD_FILTER_LIST_JSON`
- **请求路径**：`POST /api/dnspod/records/list`
- **接口描述**：获取指定域名的解析记录筛选列表，支持多种筛选条件，使用JSON格式传输数据
- **认证要求**：需要JWT认证
- **适用业务单元**：域名管理模块
- **Content-Type**：`application/json`

#### 请求参数（JSON格式）

```json
{
  "domain": "example.com",
  "remark": "测试记录",
  "subDomain": "www",
  "recordType": "A",
  "limit": 50,
  "offset": 0,
  "keyword": "搜索关键字",
  "sortField": "name",
  "sortType": "ASC"
}
```

| 参数名     | 是否必须 | 类型    | 默认值 | 描述                                           | 示例值        |
| ---------- | -------- | ------- | ------ | ---------------------------------------------- | ------------- |
| domain     | 是       | String  | -      | 要查询的域名                                   | example.com   |
| remark     | 否       | String  | -      | 根据备注筛选解析记录                           | 测试记录      |
| subDomain  | 否       | String  | -      | 根据子域名筛选，如www                          | www           |
| recordType | 否       | String  | -      | 记录类型，如A、CNAME、MX、TXT等                | A             |
| limit      | 否       | Integer | 100    | 限制返回记录数量，最大3000                     | 50            |
| offset     | 否       | Integer | 0      | 偏移量，用于分页                               | 0             |
| keyword    | 否       | String  | -      | 关键字搜索，支持搜索主机头和记录值             | 搜索关键字    |
| sortField  | 否       | String  | -      | 排序字段（name,type,value,ttl,updated_on）     | name          |
| sortType   | 否       | String  | ASC    | 排序方式（ASC或DESC）                          | ASC           |

#### 响应示例

##### 成功响应（状态码 200）

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "RecordCountInfo": {
      "ListCount": 2,
      "SubdomainCount": 5,
      "TotalCount": 5
    },
    "RecordList": [
      {
        "RecordId": 123456,
        "Name": "@",
        "Type": "A",
        "Value": "192.168.1.1",
        "Line": "默认",
        "LineId": "0",
        "TTL": 600,
        "Status": "ENABLE",
        "Weight": null,
        "MX": 0,
        "Remark": "主域名A记录",
        "UpdatedOn": "2025-08-16 09:00:00",
        "MonitorStatus": "",
        "DefaultNS": false
      },
      {
        "RecordId": 123457,
        "Name": "www",
        "Type": "CNAME",
        "Value": "example.com.",
        "Line": "默认",
        "LineId": "0",
        "TTL": 600,
        "Status": "ENABLE",
        "Weight": null,
        "MX": 0,
        "Remark": "www子域名CNAME记录",
        "UpdatedOn": "2025-08-16 09:00:00",
        "MonitorStatus": "",
        "DefaultNS": false
      }
    ],
    "RequestId": "12345678-1234-1234-1234-123456789012"
  },
  "timestamp": "2025-08-16T09:00:00+08:00"
}
```

##### 失败响应（状态码 500）

```json
{
  "code": 500,
  "message": "获取域名解析记录失败: 域名不存在或无权限访问",
  "data": null,
  "timestamp": "2025-08-16T09:00:00+08:00"
}
```

#### 调试说明

- **测试用例 1（成功场景）**：
  ```bash
  curl -X POST -H "Authorization: Bearer YOUR_JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"domain": "example.com", "limit": 10}' \
    "http://localhost:8080/api/dnspod/records/list"
  ```
  - 预期响应：code=200，data包含解析记录列表

- **测试用例 2（筛选场景）**：
  ```bash
  curl -X POST -H "Authorization: Bearer YOUR_JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"domain": "example.com", "recordType": "A", "subDomain": "www"}' \
    "http://localhost:8080/api/dnspod/records/list"
  ```
  - 预期响应：code=200，返回符合条件的A记录

### 3.2 添加域名解析记录（JSON格式）

#### 基本信息

- **接口标识**：`DNSPOD_CREATE_RECORD_JSON`
- **请求路径**：`POST /api/dnspod/records`
- **接口描述**：添加新的域名解析记录，使用JSON格式传输数据
- **认证要求**：需要JWT认证
- **适用业务单元**：域名管理模块
- **Content-Type**：`application/json`

#### 请求参数（JSON格式）

```json
{
  "domain": "example.com",
  "recordType": "A",
  "value": "192.168.1.1",
  "subDomain": "www",
  "recordLine": "默认",
  "ttl": 600,
  "remark": "测试记录"
}
```

| 参数名     | 是否必须 | 类型    | 默认值 | 描述                                           | 示例值        |
| ---------- | -------- | ------- | ------ | ---------------------------------------------- | ------------- |
| domain     | 是       | String  | -      | 要添加记录的域名                               | example.com   |
| recordType | 是       | String  | -      | 记录类型（A、CNAME、MX、TXT等）                | A             |
| value      | 是       | String  | -      | 记录值（如IP地址、域名等）                     | 192.168.1.1   |
| subDomain  | 否       | String  | @      | 主机记录（如www、mail等，@表示主域名）         | www           |
| recordLine | 否       | String  | 默认   | 记录线路                                       | 默认          |
| ttl        | 否       | Long    | 600    | TTL值（生存时间）                              | 600           |
| mx         | 否       | Long    | -      | MX优先级（仅MX记录需要）                       | 10            |
| weight     | 否       | Long    | -      | 权重（负载均衡）                               | 5             |
| status     | 否       | String  | ENABLE | 记录状态（ENABLE或DISABLE）                    | ENABLE        |
| remark     | 否       | String  | -      | 备注信息                                       | 测试记录      |

### 3.3 修改域名解析记录（JSON格式）

#### 基本信息

- **接口标识**：`DNSPOD_MODIFY_RECORD_JSON`
- **请求路径**：`POST /api/dnspod/records/modify`
- **接口描述**：修改现有的域名解析记录，使用JSON格式传输数据
- **认证要求**：需要JWT认证
- **适用业务单元**：域名管理模块
- **Content-Type**：`application/json`

#### 请求参数（JSON格式）

```json
{
  "domain": "example.com",
  "recordId": 123456,
  "recordType": "A",
  "value": "192.168.1.2",
  "subDomain": "www",
  "recordLine": "默认",
  "ttl": 300,
  "remark": "修改后的记录"
}
```

### 3.4 删除域名解析记录（JSON格式）

#### 基本信息

- **接口标识**：`DNSPOD_DELETE_RECORD_JSON`
- **请求路径**：`POST /api/dnspod/records/delete`
- **接口描述**：删除指定的域名解析记录，使用JSON格式传输数据
- **认证要求**：需要JWT认证
- **适用业务单元**：域名管理模块
- **Content-Type**：`application/json`

#### 请求参数（JSON格式）

```json
{
  "domain": "example.com",
  "recordId": 123456
}
```

| 参数名   | 是否必须 | 类型   | 描述           | 示例值      |
| -------- | -------- | ------ | -------------- | ----------- |
| domain   | 是       | String | 域名           | example.com |
| recordId | 是       | Long   | 要删除的记录ID | 123456      |

### 3.5 获取指定域名的所有解析记录（简化版）

#### 基本信息

- **接口标识**：`DNSPOD_GET_RECORDS_BY_DOMAIN`
- **请求路径**：`GET /api/dnspod/records/{domain}`
- **接口描述**：获取指定域名的所有解析记录，使用默认参数（兼容性接口）
- **认证要求**：需要JWT认证
- **适用业务单元**：域名管理模块

#### 路径参数

| 参数名 | 类型   | 描述     | 是否必须 | 示例值      |
| ------ | ------ | -------- | -------- | ----------- |
| domain | String | 域名     | 是       | example.com |

#### 响应示例

##### 成功响应（状态码 200）

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "RecordCountInfo": {
      "ListCount": 5,
      "SubdomainCount": 8,
      "TotalCount": 8
    },
    "RecordList": [
      {
        "RecordId": 123456,
        "Name": "@",
        "Type": "A",
        "Value": "192.168.1.1",
        "Line": "默认",
        "LineId": "0",
        "TTL": 600,
        "Status": "ENABLE",
        "Weight": null,
        "MX": 0,
        "Remark": "",
        "UpdatedOn": "2025-08-16 09:00:00",
        "MonitorStatus": "",
        "DefaultNS": false
      }
    ],
    "RequestId": "12345678-1234-1234-1234-123456789012"
  },
  "timestamp": "2025-08-16T09:00:00+08:00"
}
```

#### 调试说明

- **测试用例**：
  - 请求：`curl -X GET -H "Authorization: Bearer YOUR_JWT_TOKEN" "http://localhost:8080/api/dnspod/records/example.com"`
  - 预期响应：code=200，data包含该域名的所有解析记录

## 四、环境配置

### 4.1 腾讯云API密钥配置

在`application.properties`中配置腾讯云API密钥：

```properties
# 腾讯云API配置
tencent.cloud.secret-id=${TENCENT_CLOUD_SECRET_ID:your_secret_id_placeholder}
tencent.cloud.secret-key=${TENCENT_CLOUD_SECRET_KEY:your_secret_key_placeholder}
tencent.cloud.region=ap-guangzhou
```

### 4.2 环境变量设置

设置以下环境变量：

```bash
# Linux/Mac
export TENCENT_CLOUD_SECRET_ID=your_actual_secret_id
export TENCENT_CLOUD_SECRET_KEY=your_actual_secret_key

# Windows
set TENCENT_CLOUD_SECRET_ID=your_actual_secret_id
set TENCENT_CLOUD_SECRET_KEY=your_actual_secret_key
```

## 五、错误处理

### 5.1 常见错误类型

1. **认证失败**
   - 错误码：401
   - 原因：JWT令牌无效或过期
   - 解决方案：重新登录获取有效令牌

2. **腾讯云API调用失败**
   - 错误码：500
   - 原因：腾讯云密钥配置错误或API调用异常
   - 解决方案：检查密钥配置和网络连接

3. **域名不存在或无权限**
   - 错误码：500
   - 原因：指定域名不存在或当前账户无权限访问
   - 解决方案：确认域名正确且账户有相应权限

### 5.2 错误响应示例

```json
{
  "code": 500,
  "message": "调用腾讯云DNSPod API失败: AuthFailure.SecretIdNotFound",
  "data": null,
  "timestamp": "2025-08-16T09:00:00+08:00"
}
```

## 六、接口测试方法

### 6.1 使用curl命令测试（JSON格式）

```bash
# 1. 先登录获取JWT令牌
curl -X POST -H "Content-Type: application/json" \
  -d '{"email":"12345678@example.com","password":"12345678"}' \
  http://localhost:8080/api/auth/login

# 2. 获取解析记录列表（JSON格式）
curl -X POST -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "example.com",
    "limit": 10,
    "recordType": "A"
  }' \
  "http://localhost:8080/api/dnspod/records/list"

# 3. 添加解析记录（JSON格式）
curl -X POST -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "example.com",
    "recordType": "A",
    "value": "192.168.1.1",
    "subDomain": "www",
    "ttl": 600,
    "remark": "测试记录"
  }' \
  "http://localhost:8080/api/dnspod/records"

# 4. 修改解析记录（JSON格式）
curl -X POST -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "example.com",
    "recordId": 123456,
    "recordType": "A",
    "value": "192.168.1.2",
    "subDomain": "www",
    "ttl": 300,
    "remark": "修改后的记录"
  }' \
  "http://localhost:8080/api/dnspod/records/modify"

# 5. 删除解析记录（JSON格式）
curl -X POST -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "domain": "example.com",
    "recordId": 123456
  }' \
  "http://localhost:8080/api/dnspod/records/delete"

# 6. 获取域名列表
curl -X GET -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  "http://localhost:8080/api/dnspod/domains"

# 7. 获取指定域名的所有解析记录（简化版）
curl -X GET -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  "http://localhost:8080/api/dnspod/records/example.com"
```

### 6.2 使用Postman测试（JSON格式）

#### 6.2.1 获取解析记录列表
1. 创建新的POST请求
2. 设置URL：`http://localhost:8080/api/dnspod/records/list`
3. 在Headers中添加：
   - `Authorization: Bearer YOUR_JWT_TOKEN`
   - `Content-Type: application/json`
4. 在Body中选择raw，格式选择JSON，输入：
   ```json
   {
     "domain": "example.com",
     "recordType": "A",
     "limit": 10
   }
   ```
5. 发送请求查看响应结果

#### 6.2.2 添加解析记录
1. 创建新的POST请求
2. 设置URL：`http://localhost:8080/api/dnspod/records`
3. 在Headers中添加：
   - `Authorization: Bearer YOUR_JWT_TOKEN`
   - `Content-Type: application/json`
4. 在Body中输入：
   ```json
   {
     "domain": "example.com",
     "recordType": "A",
     "value": "192.168.1.1",
     "subDomain": "www",
     "ttl": 600,
     "remark": "测试记录"
   }
   ```

#### 6.2.3 修改解析记录
1. 创建新的POST请求
2. 设置URL：`http://localhost:8080/api/dnspod/records/modify`
3. 在Body中输入：
   ```json
   {
     "domain": "example.com",
     "recordId": 123456,
     "recordType": "A",
     "value": "192.168.1.2",
     "subDomain": "www",
     "ttl": 300
   }
   ```

#### 6.2.4 删除解析记录
1. 创建新的POST请求
2. 设置URL：`http://localhost:8080/api/dnspod/records/delete`
3. 在Body中输入：
   ```json
   {
     "domain": "example.com",
     "recordId": 123456
   }
   ```

## 七、版本历史

| 版本号 | 更新日期   | 更新内容                           | 责任人    |
| ------ | ---------- | ---------------------------------- | --------- |
| v1.0.0 | 2025-08-16 | 初始版本，集成腾讯云DNSPod API     | CodeBuddy |

## 八、注意事项

1. **API频率限制**：腾讯云DNSPod API有频率限制（默认20次/秒），请合理控制调用频率
2. **密钥安全**：请妥善保管腾讯云API密钥，不要在代码中硬编码
3. **域名权限**：只能查询当前腾讯云账户下的域名解析记录
4. **记录延迟**：新添加的解析记录可能存在短暂的索引延迟（约30秒）
5. **记录数量**：API获取的记录总数可能比控制台显示的多2条NS记录