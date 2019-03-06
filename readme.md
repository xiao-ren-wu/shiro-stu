# Shiro
> Apache的强大灵活的开源框架  
> 认证、授权、企业会话管理、安全加密、缓存管理。


## Shiro整体架构图
# 1

### 用户认证流程
# 2
1.	创建Security Manager.
2.	主体提交请求到Security Manager。
3.	Security Manager调用Authenticator去做权限认证。
4.	Authenticator调用Realms获取数据库中数据进行比对。
5.	将比对后的结果返回给用户。
### 授权流程
# 3
