package org.xinghe.xinghehis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 星河HIS — 医院信息系统启动类
 *
 * @SpringBootApplication 是一个组合注解，包含：
 *   - @SpringBootConfiguration：标记为配置类
 *   - @EnableAutoConfiguration：自动配置（根据 classpath 中的 jar 依赖自动配置 Spring 组件）
 *   - @ComponentScan：扫描当前包及子包的 @Component、@Service、@Controller 等注解
 *
 * @MapperScan 告诉 MyBatis 扫描 mapper 包下的接口并生成代理实现类。
 * 每个 @Mapper 接口都会被注册为 Spring Bean，可以直接 @Autowired 注入到 Service 中。
 */
@SpringBootApplication
@MapperScan("org.xinghe.xinghehis.mapper")
public class XingheHisApplication {

    public static void main(String[] args) {
        SpringApplication.run(XingheHisApplication.class, args);
    }

}
