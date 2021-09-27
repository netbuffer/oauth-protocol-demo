# oauth-protocol-demo
![](https://img.shields.io/static/v1?label=jdk&message=1.8&color=blue)
![](https://img.shields.io/static/v1?label=sppring-boot&message=2.5.4&color=blue)
![](https://img.shields.io/static/v1?label=sa-token&message=1.26.0&color=green)
> 基于 [sa-token](https://sa-token.dev33.cn/) 实现oauth2协议

* [oauth2-server](https://sa-token.dev33.cn/doc/index.html#/oauth2/oauth2-server)
* https://github.com/netbuffer/oauth-protocol-demo
* https://gitee.com/netbuffer/oauth-protocol-demo

### help
编辑`C:\Windows\System32\drivers\etc\hosts`增加如下配置
```
127.0.0.1 oauth-server.com
127.0.0.1 oauth-client.com
```

1. 访问 [/oauth2/authorize](http://oauth-server.com:19000/oauth2/authorize?response_type=code&client_id=10001&redirect_uri=http://sa-token.dev33.cn/&scope=userinfo) 进行登录授权
2. 访问 [/oauth2/token](http://oauth-server.com:19000/oauth2/token?grant_type=authorization_code&client_id=10001&client_secret=aaaa-bbbb-cccc-dddd-eeee&code={code}) 获取access_token等信息