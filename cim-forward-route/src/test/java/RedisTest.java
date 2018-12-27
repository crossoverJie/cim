import com.crossoverjie.cim.route.RouteApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/23 21:40
 * @since JDK 1.8
 */
@SpringBootTest(classes = RouteApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {

    @Autowired
    private RedisTemplate<String,String> redisTemplate ;

    @Test
    public void test(){
        redisTemplate.opsForValue().set("test","test") ;
        String test = redisTemplate.opsForValue().get("test");
        System.out.println("====" + test);
    }
}
