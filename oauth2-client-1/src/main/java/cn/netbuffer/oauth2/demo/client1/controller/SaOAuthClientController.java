package cn.netbuffer.oauth2.demo.client1.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ejlchina.okhttps.OkHttps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * Sa-OAuth2 Client端 控制器
 *
 * @author kong
 */
@Slf4j
@RestController
public class SaOAuthClientController {

    /**
     * client应用配置
     */
    @Value("${oauth2.client.clientId}")
    private String clientId;
    @Value("${oauth2.client.clientSecret}")
    private String clientSecret;
    @Value("${oauth2.client.serverUrl}")
    private String serverUrl;
    @Value("${info.client.url}")
    private String clientUrl;

    @ModelAttribute
    public void mode(Model model) {
        model.addAttribute("clientId", clientId);
        model.addAttribute("serverUrl", serverUrl);
        model.addAttribute("clientUrl", clientUrl);
    }

    // 进入首页
    @RequestMapping("/")
    public Object index(Model model) {
        Object id = StpUtil.getLoginIdDefaultNull();
        model.addAttribute("uid", id);
        log.debug("get uid={}", id);
        return new ModelAndView("index.html");
    }

    // 根据Code码进行登录，获取 Access-Token 和 openid
    @RequestMapping("/codeLogin")
    public SaResult codeLogin(String code) {
        // 调用Server端接口，获取 Access-Token 以及其他信息
        String str = OkHttps.sync(serverUrl + "/oauth2/token")
                .addBodyPara("grant_type", "authorization_code")
                .addBodyPara("code", code)
                .addBodyPara("client_id", clientId)
                .addBodyPara("client_secret", clientSecret)
                .post()
                .getBody()
                .toString();
        JSONObject data = JSON.parseObject(str);
        log.debug("/oauth2/token return {}", data);

        // code不等于200  代表请求失败
        if (data.getInteger("code") != 200) {
            return SaResult.error(data.getString("msg"));
        }

        long uid = getUserIdByOpenid(data.getString("openid"));

        //写入Cookie登录态
        StpUtil.login(uid);
        JSONObject jsonData=data.getJSONObject("data");
        jsonData.put("uid",uid);
        return SaResult.data(jsonData);
    }

    // 根据 Refresh-Token 去刷新 Access-Token
    @RequestMapping("/refresh")
    public SaResult refresh(String refreshToken) {
        // 调用Server端接口，通过 Refresh-Token 刷新出一个新的 Access-Token
        String str = OkHttps.sync(serverUrl + "/oauth2/refresh")
                .addBodyPara("grant_type", "refresh_token")
                .addBodyPara("client_id", clientId)
                .addBodyPara("client_secret", clientSecret)
                .addBodyPara("refresh_token", refreshToken)
                .post()
                .getBody()
                .toString();
        JSONObject data = JSON.parseObject(str);
        return SaResult.data(data);
    }

    // 模式三：密码式-授权登录
    @RequestMapping("/passwordLogin")
    public SaResult passwordLogin(String username, String password) {
        // 模式三：密码式-授权登录
        String str = OkHttps.sync(serverUrl + "/oauth2/token")
                .addBodyPara("grant_type", "password")
                .addBodyPara("client_id", clientId)
                .addBodyPara("username", username)
                .addBodyPara("password", password)
                .post()
                .getBody()
                .toString();
        JSONObject data = JSON.parseObject(str);
        long uid = getUserIdByOpenid(data.getString("openid"));
        // 返回相关参数
        StpUtil.login(uid);
        return SaResult.data(data);
    }

    // 模式四：获取应用的 Client-Token
    @RequestMapping("/clientToken")
    public SaResult clientToken() {
        // 调用Server端接口
        String str = OkHttps.sync(serverUrl + "/oauth2/client_token")
                .addBodyPara("grant_type", "client_credentials")
                .addBodyPara("client_id", clientId)
                .addBodyPara("client_secret", clientSecret)
                .post()
                .getBody()
                .toString();
        JSONObject data = JSON.parseObject(str);
        return SaResult.data(data);
    }

    // 注销登录
    @RequestMapping("/logout")
    public SaResult logout() {
        StpUtil.logout();
        return SaResult.ok();
    }

    // 根据 Access-Token 置换相关的资源: 获取账号昵称、头像、性别等信息
    @RequestMapping("/getUserinfo")
    public SaResult getUserinfo(String accessToken) {
        // 调用Server端接口，查询开放的资源
        String str = OkHttps.sync(serverUrl + "/oauth2/userinfo")
                .addBodyPara("access_token", accessToken)
                .post()
                .getBody()
                .toString();
        JSONObject data = JSON.parseObject(str);
        return SaResult.data(data);
    }

    // 全局异常拦截
    @ExceptionHandler
    public SaResult handlerException(Exception e) {
        log.error("method exec error:", e);
        return SaResult.error(e.getMessage());
    }


    // ------------ 模拟方法 ------------------
    // 模拟方法：根据openid获取userId
    private long getUserIdByOpenid(String openid) {
        // 此方法仅做模拟，实际开发要根据具体业务逻辑来获取userId
        return 10001;
    }

}
