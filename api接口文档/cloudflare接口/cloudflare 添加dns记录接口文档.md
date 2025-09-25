## 真实接口示例
curl https://api.cloudflare.com/client/v4/zones/$ZONE_ID/dns_records \
    -H 'Content-Type: application/json' \
    -H "X-Auth-Email: xianhuawork@163.com" \
    -H "X-Auth-Key: 2ece4a2665a0219c10dd7174638d4133a4f77"
    -d '{
          "name": "f.f.f.f.a.2.8.f.0.7.4.0.1.0.0.2.ip6.arpa",
          "ttl": 1,
          "type": "NS",
          "comment": "Domain verification record",
          "content": "test.com"
 }'

其中，$ZONE_ID 等于


## 添加ns记录真实响应示例：
{
	"result": {
		"id": "869bb3fe8be216947d506c633c24db49",
		"name": "f.f.f.f.a.2.8.f.0.7.4.0.1.0.0.2.ip6.arpa",
		"type": "NS",
		"content": "test.com",
		"proxiable": false,
		"proxied": false,
		"ttl": 1,
		"settings": {},
		"meta": {},
		"comment": "Domain verification record",
		"tags": [],
		"created_on": "2025-09-25T14:25:05.957696Z",
		"modified_on": "2025-09-25T14:25:05.957696Z",
		"comment_modified_on": "2025-09-25T14:25:05.957696Z"
	},
	"success": true,
	"errors": [],
	"messages": []
}
