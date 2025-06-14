package com.crossoverjie.cim.route.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author zhongcanyu
 * @date 2025/5/31
 * @description
 */
@Configuration
@ConditionalOnProperty(prefix = "offline.store", name = "mode", havingValue = "mysql")
@MapperScan(basePackages = "com.crossoverjie.cim.persistence.mysql.offlinemsg.mapper")
@Import(DataSourceAutoConfiguration.class)
public class MySqlPersistenceConfig {

}
