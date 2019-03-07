# Shiro
> Apache的强大灵活的开源框架  
> 认证、授权、企业会话管理、安全加密、缓存管理。


## Shiro整体架构图
# 1

### 用户认证流程&授权流程
# 2
1.	创建Security Manager.
2.	主体提交请求到Security Manager。
3.	Security Manager调用Authenticator去做权限认证。
4.	Authenticator调用Realms获取数据库中数据进行比对。
5.	将比对后的结果返回给用户。

~~~

private SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();

    @Before
    public void addUser(){
        //设置用户信息，参数分别为：用户名、密码、角色
        simpleAccountRealm.addAccount("test","test","admin");
    }

    @Test
    public void testAuthentication(){
        //1.构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(simpleAccountRealm);
        //2.主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        //设置用户名和密码
        UsernamePasswordToken token = new UsernamePasswordToken("test", "test");
        
        subject.login(token);

        System.out.println(subject.isAuthenticated()); //true

        subject.checkRole("admin");//认证成功返回null,认证失败抛异常
    }
~~~

## Realm
shiro除了SimpleAccountRealm还提供了iniRealm和jdbcRealm

### IniRealm
通过ini配置文件的形式存储用户的信息
~~~
    @Test
    public void testAuthentication(){
        //指定配置文件的位置
        IniRealm iniRealm = new IniRealm("classpath:user.ini");

        //1.构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(iniRealm);
        //2.主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("test", "test");

        subject.login(token);

        System.out.println(subject.isAuthenticated());

        subject.checkRole("admin");
        subject.checkPermission("user:delete");
    }
~~~
ini配置文件内容
>[users]  
 test=test,admin  
 [roles]  
 admin=user:delete,user:update  

### JdbcRealm
通过访问数据库的形式获取用户的信息，该模式下，shiro提供了默认的sql语句，但是，在数据库中创建的表还要符合shiro的规范。  
当然了，也可以自己执行sql。

~~~
    //配置数据源
    DruidDataSource dataSource = new DruidDataSource();

    {
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/spring_shiro?useUnicode=true&characterEncoding=utf8&serverTimezone=GMT");
        dataSource.setPassword("root");
        dataSource.setUsername("root");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    }

    @Test
    public void testAuthentication(){
        JdbcRealm jdbcRealm = new JdbcRealm();
        jdbcRealm.setDataSource(dataSource);

        String authenticationSql = "select password from user where name = ?";
        jdbcRealm.setAuthenticationQuery(authenticationSql);

        //1.构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(jdbcRealm);
        //2.主体提交认证请求
        SecurityUtils.setSecurityManager(defaultSecurityManager);
        Subject subject = SecurityUtils.getSubject();

        UsernamePasswordToken token = new UsernamePasswordToken("test", "123456");

        subject.login(token);

        System.out.println(subject.isAuthenticated());


    }
~~~
除了shiro给我们创建的几种默认的Realm,我们还可以自定义Realm.  
我们自定义的Realm首先要继承AuthorizingRealm类，并实现抽象方法
~~~
    /**
     * 用于验证权限使用
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //获取用户名
        String userName = (String) principals.getPrimaryPrincipal();
        
        //通过用户名获取用户的所有的角色
        Set<String> roles ...
        
        //获取用户具备的权限
        Set<String> permission ...
        
        //创建返回对象
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setStringPermissions(permission);
        authorizationInfo.setRoles(roles);
        return authorizationInfo;
    }

    
    /**
     * 用于验证用户名密码是是否匹配
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        //1.从主体传过来的认证信息中，获得用户名
        String userName = (String)token.getPrincipal();
        
        //通过用户名获取用户密码
        String password ...
        
        //用户不存在返回null
        if(password==null){
            return null;
        }
        
        //用户存在创建返回对象
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName,password,"customRealm");
        
        //加盐，此处的盐以写死为例，实战时可以对每个用户随机生成，存到数据库中
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes("salt"));
        
        return authenticationInfo;
    }
~~~
## 加密
设置好加密规则交给Realm即可
~~~
    {
        //加密
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        //选取hash散列算法
        matcher.setHashAlgorithmName("md5");
        //设置算法执行次数
        matcher.setHashIterations(1);

        //将设置好的加密规则交给customRealm
        this.setCredentialsMatcher(matcher);
    }
~~~
## Shiro集成springBoot
- 导入依赖：
~~~
   <dependency>
       <groupId>org.apache.shiro</groupId>
       <artifactId>shiro-spring</artifactId>
       <version>1.4.0</version>
   </dependency>
   <dependency>
       <groupId>org.apache.shiro</groupId>
       <artifactId>shiro-core</artifactId>
       <version>1.4.0</version>
   </dependency>
   <dependency>
       <groupId>org.apache.shiro</groupId>
       <artifactId>shiro-web</artifactId>
       <version>1.4.0</version>
   </dependency>

~~~
- 创建一个Config类用来生成shiro相关的对象，交给spring管理。
~~~

~~~





 





























