package com.crossoverjie.cim;

import com.crossoverjie.cim.service.LoginService;
import com.crossoverjie.cim.thread.ConnectJob;
import com.crossoverjie.cim.vo.req.LoginReqVO;
import com.crossoverjie.cim.vo.res.CIMServerResVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.ApplicationPidFileWriter;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020-06-14 18:59
 * @since JDK 1.8
 */
@SpringBootApplication
public class PressureApplication implements CommandLineRunner {

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor ;

    @Autowired
    private LoginService loginService ;

    @Value("${cim.client.count}")
    private int clientCount ;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(PressureApplication.class);
        application.addListeners(new ApplicationPidFileWriter());
        application.run(args);


    }


    @Override
    public void run(String... args) throws Exception {
        CIMServerResVO.ServerInfo serverInfo = loginService.userLogin();
        for (int i = 0; i < clientCount; i++) {
            ConnectJob connectJob = new ConnectJob(new LoginReqVO(Long.valueOf(i), "abc" + i), serverInfo);
            threadPoolExecutor.execute(connectJob);
        }
    }
}
