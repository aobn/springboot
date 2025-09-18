# 管理员获取用户全部DNS记录接口

## 接口基本信息

- **接口标识**: `ADMIN_GET_USER_DNS_RECORDS`
- **请求路径**: `POST /api/admin/users/dns-records`
- **接口描述**: 管理员获取用户全部DNS记录列表，支持分页查询、按记录类型查询和模糊搜索
- **认证要求**: 需要管理员权限
- **适用业务单元**: 管理员DNS记录管理功能

## 请求参数

### 请求方式
- **HTTP方法**: POST
- **Content-Type**: application/json

### 请求体参数

```typescript
interface AdminDnsRecordQueryRequest {
  page?: number;        // 页码，从1开始，默认1
  size?: number;        // 每页大小，最少20，最大3000，默认20
  sortBy?: string;      // 排序字段，默认create_time
  sortDir?: string;     // 排序方向，ASC或DESC，默认DESC
  keyword?: string;     // 搜索关键词，支持模糊查询用户名、邮箱、域名、记录名、记录值
  userId?: number;      // 用户ID，精确查询指定用户的DNS记录
  recordType?: string;  // 记录类型过滤，A、CNAME、MX、TXT等
  status?: string;      // 记录状态过滤，ENABLE、DISABLE
  syncStatus?: string;  // 同步状态过滤，PENDING、SUCCESS、FAILED
  domain?: string;      // 主域名过滤
}
```

### 参数验证规则

| 参数名     | 必填 | 类型    | 规则                                                    | 说明                                    |
| ---------- | ---- | ------- | ------------------------------------------------------- | --------------------------------------- |
| page       | 否   | integer | 大于等于1的整数，默认为1                                | 页码，从1开始                           |
| size       | 否   | integer | 20-3000之间的整数，默认为20                             | 每页大小，最少20条，最大3000条          |
| sortBy     | 否   | string  | 枚举值：create_time, update_time, name, type, status   | 排序字段，默认为create_time             |
| sortDir    | 否   | string  | 枚举值：ASC, DESC，默认为DESC                           | 排序方向，升序或降序                    |
| keyword    | 否   | string  | 最大长度100字符                                         | 搜索关键词，支持用户名、邮箱、域名、记录名、记录值模糊查询 |
| userId     | 否   | integer | 大于0的整数                                             | 用户ID，精确查询指定用户的DNS记录       |
| recordType | 否   | string  | 枚举值：A, AAAA, CNAME, MX, TXT, NS, SRV, CAA等        | DNS记录类型过滤                         |
| status     | 否   | string  | 枚举值：ENABLE, DISABLE                                 | DNS记录状态过滤                         |
| syncStatus | 否   | string  | 枚举值：PENDING, SUCCESS, FAILED                        | 同步状态过滤                            |
| domain     | 否   | string  | 最大长度100字符                                         | 主域名过滤                              |

## 响应格式

### 成功响应（状态码 200）

```typescript
interface AdminDnsRecordResponse {
  code: 200;
  message: "获取用户DNS记录列表成功";
  data: {
    content: UserDnsRecordInfo[];  // DNS记录列表
    page: number;                  // 当前页码
    size: number;                  // 每页大小
    total: number;                 // 总记录数
    totalPages: number;            // 总页数
    hasNext: boolean;              // 是否有下一页
    hasPrevious: boolean;          // 是否有上一页
  };
  timestamp: string;
}

interface UserDnsRecordInfo {
  id: number;                      // DNS记录ID
  userId: number;                  // 用户ID
  username: string;                // 用户名
  email: string;                   // 用户邮箱
  subdomainId: number;             // 子域名ID
  subdomain: string;               // 子域名前缀
  domain: string;                  // 主域名
  fullDomain: string;              // 完整域名
  recordId: number;                // DNSPod记录ID
  name: string;                    // 主机记录
  type: string;                    // 记录类型
  value: string;                   // 记录值
  line: string;                    // 记录线路
  lineId: string;                  // 线路ID
  ttl: number;                     // TTL值
  mx: number;                      // MX优先级
  weight: number;                  // 权重
  status: string;                  // 记录状态
  remark: string;                  // 备注信息
  monitorStatus: string;           // 监控状态
  updatedOn: string;               // DNSPod更新时间
  syncStatus: string;              // 同步状态
  syncError: string;               // 同步错误信息
  createTime: string;              // 创建时间
  updateTime: string;              // 更新时间
}
```

### 失败响应

#### 参数验证失败（400）
```json
{
  "code": 400,
  "message": "参数验证失败",
  "data": {
    "errors": [
      {
        "field": "size",
        "message": "每页大小必须在20-3000之间"
      }
    ]
  },
  "timestamp": "2025-09-13T04:00:00Z"
}
```

#### 未授权访问（401）
```json
{
  "code": 401,
  "message": "未授权访问，请先登录",
  "data": null,
  "timestamp": "2025-09-13T04:00:00Z"
}
```

#### 权限不足（403）
```json
{
  "code": 403,
  "message": "权限不足，需要管理员权限",
  "data": null,
  "timestamp": "2025-09-13T04:00:00Z"
}
```

#### 服务器错误（500）
```json
{
  "code": 500,
  "message": "服务器内部错误",
  "data": null,
  "timestamp": "2025-09-13T04:00:00Z"
}
```

## 请求示例

### 基本查询
```bash
curl -X POST http://localhost:8080/api/admin/users/dns-records \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "page": 1,
    "size": 50
  }'
```

### 按记录类型查询
```bash
curl -X POST http://localhost:8080/api/admin/users/dns-records \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "page": 1,
    "size": 50,
    "recordType": "A",
    "status": "ENABLE"
  }'
```

### 模糊搜索
```bash
curl -X POST http://localhost:8080/api/admin/users/dns-records \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "page": 1,
    "size": 50,
    "keyword": "example",
    "syncStatus": "SUCCESS"
  }'
```

### 查询指定用户DNS记录
```bash
curl -X POST http://localhost:8080/api/admin/users/dns-records \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "page": 1,
    "size": 100,
    "userId": 123,
    "sortBy": "create_time",
    "sortDir": "DESC"
  }'
```

### 按域名和记录类型过滤
```bash
curl -X POST http://localhost:8080/api/admin/users/dns-records \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "page": 1,
    "size": 20,
    "domain": "example.com",
    "recordType": "CNAME",
    "status": "ENABLE"
  }'
```

## 响应示例

### 成功响应示例
```json
{
  "code": 200,
  "message": "获取用户DNS记录列表成功",
  "data": {
    "content": [
      {
        "id": 1,
        "userId": 123,
        "username": "testuser",
        "email": "test@example.com",
        "subdomainId": 456,
        "subdomain": "blog",
        "domain": "example.com",
        "fullDomain": "blog.example.com",
        "recordId": 789,
        "name": "www",
        "type": "A",
        "value": "192.168.1.100",
        "line": "默认",
        "lineId": "0",
        "ttl": 600,
        "mx": null,
        "weight": null,
        "status": "ENABLE",
        "remark": "网站主页",
        "monitorStatus": null,
        "updatedOn": "2025-09-13T10:30:00",
        "syncStatus": "SUCCESS",
        "syncError": null,
        "createTime": "2025-09-13T10:30:00",
        "updateTime": "2025-09-13T10:30:00"
      },
      {
        "id": 2,
        "userId": 124,
        "username": "user2",
        "email": "user2@example.com",
        "subdomainId": 457,
        "subdomain": "api",
        "domain": "example.com",
        "fullDomain": "api.example.com",
        "recordId": 790,
        "name": "@",
        "type": "CNAME",
        "value": "api.server.com",
        "line": "默认",
        "lineId": "0",
        "ttl": 300,
        "mx": null,
        "weight": null,
        "status": "ENABLE",
        "remark": "API服务",
        "monitorStatus": null,
        "updatedOn": "2025-09-13T11:00:00",
        "syncStatus": "SUCCESS",
        "syncError": null,
        "createTime": "2025-09-13T11:00:00",
        "updateTime": "2025-09-13T11:00:00"
      }
    ],
    "page": 1,
    "size": 50,
    "total": 2,
    "totalPages": 1,
    "hasNext": false,
    "hasPrevious": false
  },
  "timestamp": "2025-09-13T04:00:00Z"
}
```

## 功能特性

### 1. 分页查询
- 支持自定义页码和每页大小
- 每页最少20条，最大3000条记录
- 返回完整的分页信息

### 2. 模糊搜索
- 支持按用户名模糊搜索
- 支持按用户邮箱模糊搜索
- 支持按子域名模糊搜索
- 支持按主域名模糊搜索
- 支持按完整域名模糊搜索
- 支持按主机记录模糊搜索
- 支持按记录值模糊搜索

### 3. 精确过滤
- 按用户ID精确查询
- 按DNS记录类型过滤
- 按记录状态过滤
- 按同步状态过滤
- 按主域名过滤

### 4. 排序功能
- 支持按创建时间排序
- 支持按更新时间排序
- 支持按主机记录排序
- 支持按记录类型排序
- 支持按记录状态排序
- 支持升序和降序

### 5. 权限控制
- 仅管理员可访问
- JWT令牌验证
- 角色权限检查

## 业务规则

1. **分页限制**: 每页最少20条，最大3000条记录
2. **搜索范围**: 关键词搜索覆盖用户信息、域名信息和DNS记录信息
3. **记录类型**: 支持所有标准DNS记录类型（A、AAAA、CNAME、MX、TXT、NS、SRV、CAA等）
4. **状态过滤**: 可按ENABLE、DISABLE状态过滤
5. **同步状态**: 可按PENDING、SUCCESS、FAILED同步状态过滤
6. **排序规则**: 默认按创建时间倒序排列
7. **权限要求**: 需要ADMIN角色权限

## 测试用例

### 测试用例1：基本分页查询
- **请求**: `{"page": 1, "size": 20}`
- **预期**: 返回第1页20条DNS记录

### 测试用例2：按记录类型查询
- **请求**: `{"recordType": "A", "page": 1, "size": 50}`
- **预期**: 返回所有A记录类型的DNS记录

### 测试用例3：模糊搜索
- **请求**: `{"keyword": "example", "page": 1, "size": 50}`
- **预期**: 返回包含"example"关键词的DNS记录

### 测试用例4：用户DNS记录查询
- **请求**: `{"userId": 123, "page": 1, "size": 50}`
- **预期**: 返回用户ID为123的所有DNS记录

### 测试用例5：多条件组合查询
- **请求**: `{"recordType": "CNAME", "status": "ENABLE", "syncStatus": "SUCCESS", "page": 1, "size": 100}`
- **预期**: 返回符合所有条件的DNS记录

### 测试用例6：参数验证
- **请求**: `{"size": 10}`
- **预期**: 返回400错误，提示每页大小不符合要求

## 注意事项

1. **性能考虑**: 大数据量查询时建议使用合适的分页大小和过滤条件
2. **索引优化**: 数据库应在相关字段上建立索引
3. **缓存策略**: 可考虑对热点查询结果进行缓存
4. **安全性**: 确保只有管理员能访问此接口
5. **日志记录**: 记录管理员的查询操作日志
6. **数据一致性**: 确保DNS记录与用户、域名信息的一致性