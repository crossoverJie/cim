

# Build in local
```shell
docker build -t cim-allin1:latest -f allin1-ubuntu.Dockerfile .
docker run -p 2181:2181 -p 6379:6379 -p 8083:8083 --rm --name cim-allin1  cim-allin1
```

# Client

```shell
java -jar target/cim-client-1.0.0-SNAPSHOT.jar --server.port=8084 --cim.user.id={userId} --cim.user.userName={userName} --cim.route.url=http://127.0.0.1:8083
```