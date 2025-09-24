package com.example.PetHome;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.PetHome.mapper") // 替换成你自己的Mapper包名
public class PetHomeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetHomeApplication.class, args);
	}

}
