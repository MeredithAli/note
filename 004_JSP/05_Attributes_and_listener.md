## Listener

| 类型 | 内容                                                                                                      |
|:---|:--------------------------------------------------------------------------------------------------------|
|属性监听者| ServletRequestAttributeListener <br/>ServletContextAttributeListener <br/> HttpSessionAttributeListener |
|其他生命周期监听者|ServletRequestListener<br/>ServletContextListener<br/>HttpSessionListener<br/>HttpSessionBindingListener<br/>HttpSessionActivationListener|
|所有属性监听者中的方法（除了绑定监听者）|attributeAdded() <br/>attributeRemoved() <br/>attributeReplaced()|
|与会话相关的生命周期事件<br/>（除了与属性相关的事件）|会话创建时和会话撤销时<br/>sessionCreated()<br/>sessionDestroyed()|
|与请求相关的生命周期事件<br/>（除了与属性相关的事件）|请求初始化或撤销<br/>requesetInitialized()<br/>requestDestroyed()|
|与servlet上下文相关的生命周期事件<br/>（除了与属性相关的事件）|上下文初始化或撤销时<br/>contextInitialized()<br/>contextDestroyed()|

|场景|监听者接口| 事件类型                         |
|:---|:---|:-----------------------------|
|你想知道一个Web应用上下文中是否增加、删除或替换了一个属性。|ServletContextAttributeListener<br/>attributeAdded<br/>attributeRemoved<br/>attributeReplaced| ServletContextAttributeEvent |
|你想知道有多少个并发用户。也就是说,你想跟踪活动的会话|http.HttpSessionListener<br/>sessionCreated<br/>sessionDestroyed| HttpSessionEvent             |
|每次请求到来时你都想知道,以便建立日志记录。|ServetRequestListener<br/>requestlnitialized<br/>requestDestroyed| ServletRequestEvent          |
|你想知道什么时候增加、删除或替换一个请求属性。|ServletRequestAttributeListener<br/>attributeAdded<br/>attributeRemoved<br/>attributeReplaced| ServletRequestAttributeEvent |
|你有一个属性类（这个类表示的对象将被放在一个属性中）,而且你希望这个类型的对象绑定到一个会话或从会话删除时得到通知。|HttpSessionBindinglListener<br/>valueBound<br>valueUnbound|HttpSessionBindingEvent|
|你想知道什么时候增加、删除或替换一个会话属性。|HttpSessionAttributeListener<br/>attributeAdded<br/>attributeRemoved<br/>attributeReplaced|HttpSessionBindinqEvent|
|你想知道是否创建或撤销了一个上下文。|ServetContextListener<br/>contextlnitialized<br/>contextDestroyed|ServletContextEvent|
|你有一个属性类,而且希望此类对象绑定的会话迁移到另一个JVM时得到通知。|HttpSessionActivationListener<br/>sessionDidActivate<br/>sessionWillPassivate|HttpSessionEvent|


## Attributes

|   |可访问性（谁能看到）|作用域（能存活多久）|适用于……|
|:---|:---|:---|:---|
|Context（上下文）<br/>（非线程安全）|Web应用的所有部分，包括servlet. JSP. SerletContextListener. ServletContextAttributeListener。|ServletContext的生命期，这意味着所部署应用的生命期。如果服务器或应用关闭，上下文则撤销(其属性也相应撤销)。|你希望整个应用共享的资源，包括数据库连接. JNDI查找名、email 地址等。|
|HttpSession (会话) <br/>（非线程安全）|访问这个特定会话的所有servlet或JSP。要记住，会话从一个客户请求扩展到可能跨同一个客户的多个请求。这些请求可能到达多个servlet.|会话的生命期。会话可以通过编程撤销，也可能只是因为超时而撒销|与客户会话有关的资源和数据，而不只是与一个请求相关的资源。它要与客户完成一个持续的会话。购物车就是一个典型的例子。|
|Request (请求)<br/>(线程安全)|应用中能直接访问请求对象的所有部分。基本说来，这意味着接收所转发请求的JSP和servlet (使用RequestDispateher).另外还有与请求相关的监听者。|请求的生命期，这说明会持续到servlet的service()方法结束。换句话说，就是线程 (栈)处理这个请求的整个生命期。|将模型信息从控制器传递到视....或者传递特定于客户请求的任何数据。|