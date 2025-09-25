# 获取cloudflare单个域名全部dns记录
## 真实请求示例
curl --request GET \
  --url https://api.cloudflare.com/client/v4/zones/$ZONE_ID/dns_records \
  --header 'X-Auth-Email: xianhuawork@163.com' \
  --header 'X-Auth-Key: 2ece4a2665a0219c10dd7174638d4133a4f77'

其中$ZONE_ID是数据表cloudflare_zone的zone_id字段

## 真实响应示例
{
	"result": [
		{
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
		}
	],
	"success": true,
	"errors": [],
	"messages": [],
	"result_info": {
		"page": 1,
		"per_page": 100,
		"count": 1,
		"total_count": 1,
		"total_pages": 1
	}
}