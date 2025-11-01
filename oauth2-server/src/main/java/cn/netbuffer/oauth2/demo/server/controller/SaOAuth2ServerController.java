
package cn.netbuffer.oauth2.demo.server.controller;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.context.model.SaRequest;
import cn.dev33.satoken.oauth2.config.SaOAuth2ServerConfig;
import cn.dev33.satoken.oauth2.consts.GrantType;
import cn.dev33.satoken.oauth2.data.model.loader.SaClientModel;
import cn.dev33.satoken.oauth2.processor.SaOAuth2ServerProcessor;
import cn.dev33.satoken.oauth2.strategy.SaOAuth2Strategy;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sa-OAuth2 Server端 控制器
 */
@Slf4j
@RestController
public class SaOAuth2ServerController {

    // OAuth2-Server 端：处理所有 OAuth2 相关请求
    @RequestMapping("/oauth2/*")
    public Object request() {
        SaRequest saRequest = SaHolder.getRequest();
        log.debug("{} {}", saRequest.getMethod(), saRequest.getUrl());
        return SaOAuth2ServerProcessor.instance.dister();
    }

    // Sa-Token OAuth2 定制化配置
    @Autowired
    public void configOAuth2Server(SaOAuth2ServerConfig oauth2Server) {
        // 添加 client 信息
        oauth2Server.addClient(
                new SaClientModel()
                        .setClientId("10001")    // client id
                        .setClientSecret("lzNjiJInCcWVSIQGHbX83uL5C7HSXaHX")    // client 秘钥
                        .addAllowRedirectUris("*")    // 所有允许授权的 url
                        .addContractScopes("openid", "userid", "userinfo")    // 所有签约的权限
                        .addAllowGrantTypes(     // 所有允许的授权模式
                                GrantType.authorization_code, // 授权码式
                                GrantType.implicit,  // 隐式式
                                GrantType.refresh_token,  // 刷新令牌
                                GrantType.password,  // 密码式
                                GrantType.client_credentials  // 客户端模式
                        )
        );
        // 配置：未登录时返回的View
        SaOAuth2Strategy.instance.notLoginView = () -> {
            // 简化模拟表单
            String doLoginCode =
                    "fetch(`/oauth2/doLogin?name=${document.querySelector('#name').value}&pwd=${document.querySelector('#pwd').value}`) " +
                            " .then(res => res.json()) " +
                            " .then(res => { if(res.code === 200) { location.reload() } else { alert(res.msg) } } )";
            String res =
                    "<h2>当前客户端在 OAuth-Server 认证中心尚未登录，请先登录</h2>" +
                            "用户：<input id='name' /> <br> " +
                            "密码：<input id='pwd' /> <br>" +
                            "<button onclick=\"" + doLoginCode + "\">登录</button>";
            return res;
        };
        // 配置：登录处理函数
        SaOAuth2Strategy.instance.doLoginHandle = (name, pwd) -> {
            if ("sa".equals(name) && "123456".equals(pwd)) {
                StpUtil.login(10001);
                return SaResult.ok();
            }
            return SaResult.error("账号名或密码错误");
        };
        // 配置：确认授权时返回的 view
        SaOAuth2Strategy.instance.confirmView = (clientId, scopes) -> {
            String scopeStr = SaFoxUtil.convertListToString(scopes);
            String yesCode =
                    "fetch('/oauth2/doConfirm?client_id=" + clientId + "&scope=" + scopeStr + "', {method: 'POST'})" +
                            ".then(res => res.json())" +
                            ".then(res => location.reload())";
            String res = "<p>应用 " + clientId + " 请求授权：" + scopeStr + "，是否同意？</p>"
                    + "<p>" +
                    "        <button onclick=\"" + yesCode + "\">同意</button>" +
                    "        <button onclick='history.back()'>拒绝</button>" +
                    "</p>";
            return res;
        };
    }

    // 全局异常拦截  
    @ExceptionHandler
    public SaResult handlerException(Exception e) {
        log.error("oauth error:", e);
        return SaResult.error(e.getMessage());
    }

}