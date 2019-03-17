# Shiro
> Apache的强大灵活的开源框架  
> 认证、授权、企业会话管理、安全加密、缓存管理。

##  :fire: Shiro整体架构图

![Shiro整体架构图](https://github.com/xiao-ren-wu/shiro-stu/blob/master/src/main/resources/img/Shiro%E6%9E%B6%E6%9E%84.png)

## :fire: 用户认证流程&授权流程

![](https://github.com/xiao-ren-wu/shiro-stu/blob/master/src/main/resources/img/%E7%94%A8%E6%88%B7%E8%AE%A4%E8%AF%81%E6%B5%81%E7%A8%8B.png)
![](https://github.com/xiao-ren-wu/shiro-stu/blob/master/src/main/resources/img/%E6%8E%88%E6%9D%83.png)

1.	创建Security Manager.
2.	主体提交请求到Security Manager。
3.	Security Manager调用Authenticator去做权限认证。
4.	Authenticator调用Realms获取数据库中数据进行比对。
5.	将比对后的结果返回给用户。

:tada:Demo

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

## :fire:Realm

shiro除了SimpleAccountRealm还提供了iniRealm和jdbcRealm

### :truck:IniRealm

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
> test=test,admin  
> [roles]  
> admin=user:delete,user:update  

**【解释】**
[users]
test=test,admin----------->等号左面的为用户名，右面的第一个字段为用户密码，逗号后面的为用户拥有的角色,如果用户具备多个角色，每个角色之间用户逗号隔开。  
[roles]  
admin=user:delete,user:update----------->等号左面的为角色名称，右面的为该角色具备的权限，不同的权限用逗号分隔。   

### :truck:JdbcRealm

通过访问数据库的形式获取用户的信息，该模式下，shiro提供了默认的sql语句，但是，在数据库中创建的表还要符合shiro的规范。   

- JdbcRealm

~~~
public class JdbcRealm extends AuthorizingRealm {
    protected static final String DEFAULT_AUTHENTICATION_QUERY = "select password from users where username = ?";
    protected static final String DEFAULT_SALTED_AUTHENTICATION_QUERY = "select password, password_salt from users where username = ?";
    protected static final String DEFAULT_USER_ROLES_QUERY = "select role_name from user_roles where username = ?";
    protected static final String DEFAULT_PERMISSIONS_QUERY = "select permission from roles_permissions where role_name = ?";
    private static final Logger log = LoggerFactory.getLogger(JdbcRealm.class);
    protected DataSource dataSource;
    protected String authenticationQuery = "select password from users where username = ?";
    protected String userRolesQuery = "select role_name from user_roles where username = ?";
    protected String permissionsQuery = "select permission from roles_permissions where role_name = ?";
    protected boolean permissionsLookupEnabled = false;
    protected JdbcRealm.SaltStyle saltStyle;
    ...
}
~~~

- 通过查看JdbcRealm源码可以看出,shiro设计的数据库结构如下：

![](C:\Users\yuwenbo9\Desktop\数据库uml.png)

#### :truck:[sql文件]()

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
:tada:如果数据设计的和shiro提供的默认的不一致，我们可以通过更换自己的sql实现。  

eg:此处以更换认证sql为例

~~~
//设置好自己的sql语句
String authenticationSql = "select password from user where name = ?";
//通过jdbcRealm对象设置自定义的sql
jdbcRealm.setAuthenticationQuery(authenticationSql);
~~~

### :truck:自定义Realm

除了shiro给我们创建的几种默认的Realm,我们还可以自定义Realm.  
我们自定义的Realm首先要继承`AuthorizingRealm`，并实现抽象方法

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
## :fire:加密

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
## :fire:Shiro集成springBoot2.x
- 导入依赖：
~~~
<properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <springboot.version>2.1.2.RELEASE</springboot.version>
        <shiro-spring.version>1.3.2</shiro-spring.version>
        <druid.version>1.1.12</druid.version>
        <mybatis-spring-boot-starter.version>2.0.0</mybatis-spring-boot-starter.version>
        <mybatis-spring.version>2.0.0</mybatis-spring.version>
        <jedis.version>2.9.0</jedis.version>
        <commons-lang3.version>3.8.1</commons-lang3.version>
        <spring-web.version>5.1.5.RELEASE</spring-web.version>
        <commons-codec.version>1.11</commons-codec.version>
        <lombok.version>1.18.2</lombok.version>
    </properties>

        <dependencies>
            <!--springboot全家桶-->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-autoconfigure</artifactId>
                <version>${springboot.version}</version>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
                <version>${springboot.version}</version>
            </dependency>
            <dependency>
                <groupId>org.mybatis.spring.boot</groupId>
                <artifactId>mybatis-spring-boot-starter</artifactId>
                <version>${mybatis-spring-boot-starter.version}</version>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-jdbc</artifactId>
                <version>2.1.2.RELEASE</version>
            </dependency>
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-web</artifactId>
                <version>${spring-web.version}</version>
                <scope>compile</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-data-redis</artifactId>
                <version>2.1.2.RELEASE</version>
            </dependency>
    
            <!--shiro-->
            <dependency>
                <groupId>org.apache.shiro</groupId>
                <artifactId>shiro-spring</artifactId>
                <version>${shiro-spring.version}</version>
            </dependency>
          
            <!--database-->
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>druid</artifactId>
                <version>${druid.version}</version>
            </dependency>
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
                <version>5.1.46</version>
                <scope>runtime</scope>
            </dependency>
            <dependency>
                <groupId>org.mybatis</groupId>
                <artifactId>mybatis-spring</artifactId>
                <version>${mybatis-spring.version}</version>
            </dependency>
            <!--redis-->
            <dependency>
                <groupId>redis.clients</groupId>
                <artifactId>jedis</artifactId>
                <version>${jedis.version}</version>
            </dependency>
            <dependency>
                <groupId>org.springframework</groupId>
                <artifactId>spring-test</artifactId>
                <version>5.1.5.RELEASE</version>
                <scope>test</scope>
            </dependency>
        </dependencies>
~~~
- 将session存储到redis中
1. 创建jedis工具类
~~~
@Component
public class JedisUtil {

    @Resource
    private JedisPool jedisPool;

    private final String shiro_session_prefix = "tea-session";


    public void set(String key, String value) {
        try(Jedis jedis = jedisPool.getResource()){
            jedis.set(key,value);
        }
    }

    public void expire(byte[] key, int i) {
        try(Jedis jedis = jedisPool.getResource()){
            jedis.expire(key,i);
        }
    }

    public String get(String key){
        try(Jedis jedis = jedisPool.getResource()){
            return jedis.get(key);
        }
    }

    public void set(byte[] key, byte[] value) {
        try(Jedis jedis = jedisPool.getResource()){
            jedis.set(key,value);
        }
    }

    public byte[] get(byte[] key) {
        try(Jedis jedis = jedisPool.getResource()){
            return jedis.get(key);
        }
    }

    public void delete(byte[] key) {
        try(Jedis jedis = jedisPool.getResource()){
            jedis.del(key);
        }
    }

    public Set<byte[]> keys() {
        try(Jedis jedis = jedisPool.getResource()){
            return jedis.keys((shiro_session_prefix+"*").getBytes());
        }
    }
}
~~~

2. 继承AbstractSessionDAO实现相关方法

~~~
@Component
public class RedisSessionDao extends AbstractSessionDAO {

    @Resource
    private JedisUtil jedisUtil;

    private final String shiro_session_prefix = "tea-session";

    private byte[] getKey(String key){
        return (shiro_session_prefix+key).getBytes();
    }

    private void saveSession(Session session){
        if(session==null||session.getId()==null){
            return;
        }
        byte[] key = this.getKey(session.toString());
        byte[] value = SerializationUtils.serialize(session);
        jedisUtil.set(key,value);
        jedisUtil.expire(key,600);
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session,sessionId);
        this.saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable serializable) {
        if(serializable==null){
            return null;
        }
        byte[] key = getKey(serializable.toString());
        byte[] value = jedisUtil.get(key);
        return (Session)SerializationUtils.deserialize(value);
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        this.saveSession(session);
    }

    @Override
    public void delete(Session session) {
        if (session==null||session.getId()==null){
            return;
        }
        byte[] key = getKey(session.getId().toString());
        jedisUtil.delete(key);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<byte[]> keySet = jedisUtil.keys();
        Set<Session> sessions = new HashSet<>();
        if(CollectionUtils.isEmpty(keySet)){
            return sessions;
        }
        for(byte[] key:keySet){
            Session session = (Session) SerializationUtils.deserialize(jedisUtil.get(key));
            sessions.add(session);
        }
        return sessions;
    }
}
~~~

- 缓存权限数据

~~~
@Component
public class CustomRedisCache<K,V> implements Cache<K,V> {

    @Resource
    private JedisUtil jedisUtil;

    private final String shiro_cache_prefix = "tea-cache";

    private byte[] getKey(K k){
        if(k==null){
            return null;
        }
        if(k instanceof String){
            return (shiro_cache_prefix+k).getBytes();
        }
        return SerializationUtils.serialize(k);
    }

    @Override
    public V get(K k) throws CacheException {
        byte[] value = jedisUtil.get(getKey(k));
        if(value!=null){
            return (V)SerializationUtils.deserialize(value);
        }
        return null;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        byte[] key = getKey(k);
        byte[] value = SerializationUtils.serialize(v);
        jedisUtil.set(key,value);
        jedisUtil.expire(key,600);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        byte[] key = getKey(k);
        byte[] value = jedisUtil.get(key);
        jedisUtil.delete(key);
        if(value!=null){
            return (V)SerializationUtils.deserialize(value);
        }
        return null;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<K> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }
}
~~~

~~~
@Component
public class RedisCacheManager implements CacheManager {

    @Resource
    private CustomRedisCache redisCache;

    @Override
    public <K, V> Cache<K, V> getCache(String s) throws CacheException {
        return redisCache;
    }
}

~~~



- 实现自定义Realm

~~~
@Component
public class CustomRealm extends AuthorizingRealm {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRolesMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;


    /**
     * 用于授权使用
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //获取用户名
        String userName = (String) principals.getPrimaryPrincipal();
        Set<String> roles = getRolesByUserName(userName);
        Set<String> permission = getPermissionByRole(roles);
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setStringPermissions(permission);
        authorizationInfo.setRoles(roles);
        return authorizationInfo;
    }

    private Set<String> getPermissionByRole(Set<String> roles) {
        if(roles==null){
            return null;
        }
        Set<String> permissions = new HashSet<>();
        roles.forEach(roleName->permissions.addAll(permissionMapper.findPermissionByRoleName(roleName)));
        return permissions;
    }

    private Set<String> getRolesByUserName(String userName) {
        return roleMapper.getRolesByUserName(userName);
    }

    /**
     * 用于做认证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        //1.从主体传过来的认证信息中，获得用户名
        String userName = (String)token.getPrincipal();
        //通过用户名称到数据库中获取用户凭证
        User user = getUserByUserName(userName);
        //用户不存在返回null
        if(user==null){
            return null;
        }
        //用户存在创建返回对象
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(userName,user.getPassword(),"customRealm");
        //加盐
        authenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(user.getSalt()));
        return authenticationInfo;
    }

    private User getUserByUserName(String userName) {
         return userMapper.findPasswordByUserName(userName);
    }
}
~~~

:notebook:下面的类实现比较简单，不做展示，查看源码请点击下面的链接:

[Usermapper]()

[UserRolesMapper]()

[PermissionMapper]()

- 创建一个Config类用来生成shiro相关的对象，交给spring管理。
~~~
@Configuration
public class ShiroConfig {

    @Resource
    private RedisSessionDao sessionDao;

    @Resource
    private RedisCacheManager redisCacheManager;

    @Resource
    private CustomRealm customRealm;

    @Resource
    private IniRealm iniRealm;

    @Resource
    private RolesOrFilter rolesOrFilter;

    @Resource
    private PermFilter permFilter;

    @Bean
    public ShiroFilterFactoryBean shiroFilter(WebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        //拦截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // authc:所有url都必须认证(登录)通过才可以访问; anon:所有url都可以匿名访问
        filterChainDefinitionMap.put("/user/login", "anon");
        filterChainDefinitionMap.put("/**", "authc");
        shiroFilterFactoryBean.setLoginUrl("/user/need-login");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);

        //添加自己的过滤器
        HashMap<Object, Object> customFilterMap = new LinkedHashMap<>(16);
        customFilterMap.put("rolesOrFilter", rolesOrFilter);
        customFilterMap.put("permFilter", permFilter);
        
        /*
         * 设置自定义拦截器拦截路径
         * 如果多个拦截器拦截的路径相同，记得让拦截器的路径有点小差别
         * 因为map这个数据结构一个key只能对应一个value
         */
        filterChainDefinitionMap.put("/**/**","rolesOrFilter");
        filterChainDefinitionMap.put("/**/test","permFilter");
        return shiroFilterFactoryBean;
    }

    /**
     * 凭证匹配器
     * 由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     *
     * @return HashedCredentialsMatcher
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        //散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        //散列的次数，比如散列两次，相当于 md5(md5(""));
        hashedCredentialsMatcher.setHashIterations(2);
        return hashedCredentialsMatcher;
    }

    /**
     * 自定义Realm
     *
     * @param hashedCredentialsMatcher md5
     * @return customRealm
     */
    @Bean
    public CustomRealm customRealm(HashedCredentialsMatcher hashedCredentialsMatcher) {
        CustomRealm customRealm = new CustomRealm();
        //使用加密
        customRealm.setCredentialsMatcher(hashedCredentialsMatcher);
        return customRealm;
    }

    @Bean
    public WebSecurityManager securityManager(CustomSessionManager sessionManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        sessionManager.setSessionDAO(sessionDao);
        sessionManager.setSessionIdCookie(getSimpleCookie());
///        securityManager.setRealm(iniRealm);
        securityManager.setRealm(customRealm);
        securityManager.setSessionManager(sessionManager);
        securityManager.setCacheManager(redisCacheManager);
        return securityManager;
    }


    @Bean
    public IniRealm getIniRealm() {
        return new IniRealm("classpath:user.ini");
    }

    /**
     * 重新设置cookieId避免和系统自带的冲突
     *
     * @return cookie
     */
    private SimpleCookie getSimpleCookie() {
        SimpleCookie simpleCookie = new SimpleCookie("shiro-sessionId");
        simpleCookie.setPath("/");
        return simpleCookie;
    }

    /**
     * 开启shiro注解生效
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(WebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;

    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator app = new DefaultAdvisorAutoProxyCreator();
        app.setProxyTargetClass(true);
        return app;

    }

}
~~~

### :fire:如何让同一时刻，一个用户只能在一处登录？

Shiro用户登录成功之后，使用的session+cookie的会话机制，所以我们只需要控制一个用户只能有一个session即可。  

因为usename设定是唯一的，所以我们在redis中维护一个字典，key为userName,value为sessionId

当用户登录的时候冲字典中检索是否有和用户民对应的sessionId如果有，将该sessionId对应的session对应清除即可。





























