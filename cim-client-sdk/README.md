
```java
    var auth1 = ClientConfigurationData.Auth.builder()
    .userId(id)
    .userName(cj)
    .build();
    
    Client client1 = Client.builder()
                    .auth(auth1)
                    .routeUrl(routeUrl)
                    .build();
    
    ClientState.State state = client1.getState();
    Awaitility.await().atMost(10, TimeUnit.SECONDS)
    .untilAsserted(() -> Assertions.assertEquals(ClientState.State.Ready, state));
    Optional<CIMServerResVO> serverInfo = client1.getServerInfo();
    Assertions.assertTrue(serverInfo.isPresent());
    
    // send msg
    String msg = "hello";
    client1.sendGroup(msg);
    
    // get oline user
    Set<CIMUserInfo> onlineUser = client1.getOnlineUser();
```