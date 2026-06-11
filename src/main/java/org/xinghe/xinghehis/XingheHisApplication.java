package org.xinghe.xinghehis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.xinghe.xinghehis.mapper")
public class XingheHisApplication {

    public static void main(String[] args) {
        SpringApplication.run(XingheHisApplication.class, args);
    }

}
