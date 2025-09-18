# DNSPod查询可用三级域名接口

## 基本信息

- **接口标识**：`DNSPOD_CHECK_SUBDOMAIN`
- **请求路径**：`GET /api/dnspod/available-subdomain` 或 `POST /api/dnspod/available-subdomain`
- **接口描述**：查询指定三级域名是否可用（未被注册）
- **认证要求**：需要JWT认证
- **适用业务单元**：域名注册流程

## 请求方式

### GET 请求参数

| 参数名    | 类型   | 必填 | 描述                                 | 示例        |
|-----------|--------|------|--------------------------------------|-------------|
| subDomain | string | 是   | 三级域名前缀（不含主域名部分）       | test        |
| domain    | string | 否   | 主域名，默认为"cblog.eu"             | cblog.eu    |

### POST 请求参数 (JSON格式)

```json
{
  "subDomain": "test",  // 必填，三级域名前缀
  "domain": "cblog.eu"  // 可选，主域名，默认为"cblog.eu"
}
```

## 响应示例

### 成功响应（域名可用）

```json
{
  "code": 200,
  "message": "域名 test.cblog.eu 可用，未被注册",
  "data": null,
  "timestamp": "2025-08-21T13:30:00Z"
}
```

### 失败响应（域名已被注册）

```json
{
  "code": 409,
  "message": "域名 test.cblog.eu 已被注册",
  "data": null,
  "timestamp": "2025-08-21T13:30:00Z"
}
```

### 服务器错误

```json
{
  "code": 500,
  "message": "查询域名可用性失败: API调用异常",
  "data": null,
  "timestamp": "2025-08-21T13:30:00Z"
}
```

## 调试说明

- **测试用例1（域名可用）**：
  - 请求：`GET /api/dnspod/available-subdomain?subdomain=available123&domain=cblog.eu`
  - 预期响应：code=200，message表示域名可用

- **测试用例2（域名已注册）**：
  - 请求：`GET /api/dnspod/available-subdomain?subdomain=www&domain=cblog.eu`
  - 预期响应：code=409，message表示域名已被注册

## 使用示例

### curl命令

```bash
curl -X GET "http://localhost:8080/api/dnspod/available-subdomain?subdomain=test&domain=cblog.eu" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 单个域名测试脚本

可以使用项目根目录下的`test_available_subdomains.sh`脚本进行单个域名测试：

```bash
./test_available_subdomains.sh test
```

### 批量域名测试脚本

可以使用项目根目录下的`test_batch_subdomains.sh`脚本进行批量域名测试：

```bash
./test_batch_subdomains.sh domain_list.txt
```

其中`domain_list.txt`是一个包含多个三级域名前缀的文本文件，每行一个域名前缀。例如：

```
www
mail
blog
test123
random123456789
```

批量测试脚本会输出每个域名的可用状态，并在最后提供统计信息。

## 实现原理

该接口通过调用DNSPod的`DescribeRecordFilterList`接口，查询指定子域名是否已有解析记录。如果没有解析记录，则认为该三级域名可用（未被注册）。

## 注意事项

1. 该接口仅检查是否存在解析记录，不代表域名已被实际使用
2. 默认主域名为"cblog.eu"，可通过参数指定其他主域名
3. 需要有效的JWT令牌才能访问此接口