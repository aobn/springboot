#!/bin/bash

# DNS记录参数校验测试脚本
# 测试用户修改DNS记录接口的参数校验功能

BASE_URL="http://localhost:8080"
JWT_TOKEN="YOUR_JWT_TOKEN_HERE"

echo "=== DNS记录参数校验测试 ==="
echo

# 测试1: 有效的A记录
echo "测试1: 有效的A记录 (192.168.1.1)"
curl -s -X POST "$BASE_URL/api/user/dns-records/modify" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 123,
    "type": "A",
    "value": "192.168.1.1"
  }' | jq '.'
echo

# 测试2: 无效的A记录 - 包含字母
echo "测试2: 无效的A记录 - 包含字母 (a.1.1.1)"
curl -s -X POST "$BASE_URL/api/user/dns-records/modify" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 123,
    "type": "A",
    "value": "a.1.1.1"
  }' | jq '.'
echo

# 测试3: 无效的A记录 - 包含空格
echo "测试3: 无效的A记录 - 包含空格 (new s.1.1.1)"
curl -s -X POST "$BASE_URL/api/user/dns-records/modify" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 123,
    "type": "A",
    "value": "new s.1.1.1"
  }' | jq '.'
echo

# 测试4: 无效的A记录 - 超出范围
echo "测试4: 无效的A记录 - 超出范围 (256.1.1.1)"
curl -s -X POST "$BASE_URL/api/user/dns-records/modify" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 123,
    "type": "A",
    "value": "256.1.1.1"
  }' | jq '.'
echo

# 测试5: 无效的A记录 - 前导零
echo "测试5: 无效的A记录 - 前导零 (01.1.1.1)"
curl -s -X POST "$BASE_URL/api/user/dns-records/modify" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 123,
    "type": "A",
    "value": "01.1.1.1"
  }' | jq '.'
echo

# 测试6: 有效的CNAME记录
echo "测试6: 有效的CNAME记录 (example.com)"
curl -s -X POST "$BASE_URL/api/user/dns-records/modify" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 124,
    "type": "CNAME",
    "value": "example.com"
  }' | jq '.'
echo

# 测试7: 无效的CNAME记录
echo "测试7: 无效的CNAME记录 (.example.com)"
curl -s -X POST "$BASE_URL/api/user/dns-records/modify" \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 124,
    "type": "CNAME",
    "value": ".example.com"
  }' | jq '.'
echo

echo "=== 测试完成 ==="