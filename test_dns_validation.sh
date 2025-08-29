#!/bin/bash

# DNS记录参数校验测试脚本
# 测试用户修改DNS记录接口的参数校验功能

BASE_URL="http://localhost:8080"
JWT_TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhbGxub3RpY2VAcXEuY29tIiwiaWF0IjoxNzM1NTY4NDAwLCJleHAiOjE3MzU2NTQ4MDB9.example"

echo "=== DNS记录参数校验测试 ==="
echo "测试时间: $(date)"
echo "服务器: $BASE_URL"
echo ""

# 测试函数
test_dns_record() {
    local test_name="$1"
    local json_data="$2"
    local expected_code="$3"
    
    echo "测试: $test_name"
    echo "请求数据: $json_data"
    
    response=$(curl -s -X POST "$BASE_URL/api/user/dns-records/modify" \
        -H "Authorization: Bearer $JWT_TOKEN" \
        -H "Content-Type: application/json" \
        -d "$json_data")
    
    echo "响应: $response"
    
    # 提取响应中的code字段
    code=$(echo "$response" | grep -o '"code":[0-9]*' | cut -d':' -f2)
    
    if [ "$code" = "$expected_code" ]; then
        echo "✅ 测试通过 (期望code: $expected_code, 实际code: $code)"
    else
        echo "❌ 测试失败 (期望code: $expected_code, 实际code: $code)"
    fi
    echo "----------------------------------------"
}

# 等待服务启动
echo "等待服务启动..."
sleep 10

# 测试用例1: 有效的A记录
test_dns_record "有效的A记录" \
    '{"id": 1, "type": "A", "value": "192.168.1.100", "ttl": 600}' \
    "200"

# 测试用例2: 无效的A记录 - 包含字母
test_dns_record "无效的A记录-包含字母" \
    '{"id": 1, "type": "A", "value": "new s.1.1.1", "ttl": 600}' \
    "400"

# 测试用例3: 无效的A记录 - 域名格式
test_dns_record "无效的A记录-域名格式" \
    '{"id": 1, "type": "A", "value": "www.baidu.com", "ttl": 600}' \
    "400"

# 测试用例4: 无效的A记录 - 超出范围
test_dns_record "无效的A记录-超出范围" \
    '{"id": 1, "type": "A", "value": "256.256.256.256", "ttl": 600}' \
    "400"

# 测试用例5: 无效的A记录 - 不完整
test_dns_record "无效的A记录-不完整" \
    '{"id": 1, "type": "A", "value": "192.168.1", "ttl": 600}' \
    "400"

# 测试用例6: 无效的A记录 - 前导零
test_dns_record "无效的A记录-前导零" \
    '{"id": 1, "type": "A", "value": "192.168.001.001", "ttl": 600}' \
    "400"

# 测试用例7: 有效的CNAME记录
test_dns_record "有效的CNAME记录" \
    '{"id": 1, "type": "CNAME", "value": "example.com", "ttl": 1800}' \
    "200"

# 测试用例8: 无效的CNAME记录 - IP地址
test_dns_record "无效的CNAME记录-IP地址" \
    '{"id": 1, "type": "CNAME", "value": "192.168.1.1", "ttl": 1800}' \
    "400"

# 测试用例9: 空值
test_dns_record "空值测试" \
    '{"id": 1, "type": "A", "value": "", "ttl": 600}' \
    "400"

echo "=== 测试完成 ==="