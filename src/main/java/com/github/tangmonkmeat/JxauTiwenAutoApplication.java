package com.github.tangmonkmeat;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.tangmonkmeat.request.JxauRequest;

@SpringBootApplication
public class JxauTiwenAutoApplication {
	
	@Autowired
	private JxauRequest request; 

	public static void main(String[] args) {
		SpringApplication.run(JxauTiwenAutoApplication.class, args);
	}
	
	//	从Java EE5规范开始，Servlet中增加了两个影响Servlet生命周期的注解，@PostConstruct和@PreDestroy，这两个注解被用来修饰一个非静态的void（）方法。
	//	@PostConstruct会在所在类的构造函数执行之后执行，在init()方法执行之前执行。(@PreDestroy注解的方法会在这个类的destory()方法执行之后执行。)
	
	// 构造函数之后执行，启动定时任务
	@PostConstruct
	public void startJob() {
		request.init();
		request.timerClockIn();
	}

}
