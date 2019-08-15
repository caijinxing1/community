# 社区搭建
community of study

## github授权登陆模块
1. 用户点击登陆按钮，请求github提供的authorize接口，需要提供的参数
   - client_id: github生成的
   - redirect_uri: 自己设置的回调接口地址
   - state=1
   - scope=user
2. github自动跳转到我们设置的callback接口中，并且携带参数code
3. 我们继续调用github提供的access_token接口，需要提供参数，将数据穿的的参数封装为AccessTokenDTO对象
   - Client_id: github生成的
   - Client_secret: github生成的
   - Code: github: 回调提供的参数
   - Redirect_uri: 自己设置的回调接口地址
   - State: 1
   注意请求url: https://github.com/login/oauth/access_token   post方式
4. github响应请求返回access_token
5. 利用access_token请求github提供的user接口
   https://api.github.com/user?access_token=xxxxx   Get方式
6. 返回Git用户，存入数据，更新登陆状态

## Redis实现用户三天免登陆
1. 用户登陆成功后,利用UUID生成token,将token放入cookie中并设置3天有效期
```java
String token = UUID.randomUUID().toString();
Cookie cookie = new Cookie("token", token);
cookie.setMaxAge(60*60*24*3);
response.addCookie(cookie);
```
2. 将用户信息存入redis中，设置有效时间为3天
```java
redisTemplate.opsForValue().set(user.getToken(), user,60*60*24*3, TimeUnit.SECONDS);
```
3. 编写拦截器,查看本地是否有token,若存在,则从redis中查询出用户信息存入session,若无则返回登陆页面
```java
Cookie[] cookies = request.getCookies();
if (cookies == null){
   return true;
}
for (Cookie cookie : cookies) {
   if("token".equals(cookie.getName())){
       String token = cookie.getValue();
       Object o = redisTemplate.opsForValue().get(token);
       System.out.println(o);
       if(o != null){
           request.getSession().setAttribute("user", o);
       }
       break;
   }
}
```