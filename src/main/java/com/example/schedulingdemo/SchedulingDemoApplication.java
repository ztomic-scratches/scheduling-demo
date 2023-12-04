package com.example.schedulingdemo;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class SchedulingDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchedulingDemoApplication.class, args);
	}
	
	@Component
	static class Demo {

		private static final Logger log = LoggerFactory.getLogger(Demo.class);
		
		private final ApplicationEventPublisher applicationEventPublisher;

		private final AtomicInteger publishEventCounter = new AtomicInteger();
		private final AtomicInteger asyncHandleEventCounter = new AtomicInteger();
		private final AtomicInteger syncHandleEventCounter = new AtomicInteger();
		private final AtomicInteger scheduledCounter = new AtomicInteger();

		Demo(ApplicationEventPublisher applicationEventPublisher) {
			this.applicationEventPublisher = applicationEventPublisher;
		}

		@Scheduled(fixedDelay = 5_000)
		public void scheduleEvent() {
			log.info("Publish event {}...", publishEventCounter.get());
			applicationEventPublisher.publishEvent(new DemoEvent(publishEventCounter.getAndIncrement(), Thread.currentThread().getName(), Thread.currentThread().isVirtual()));
		}

		@Scheduled(fixedDelay = 5_000)
		public void scheduled() {
			boolean b = scheduledCounter.get() % 2 == 1;
			log.info("Scheduled {} virtual:{}, throw exception:{}!!!", scheduledCounter.getAndIncrement(), Thread.currentThread().isVirtual(), b);
			if (b) {
				throw new RuntimeException("Test scheduling exception virtual thread: " + Thread.currentThread().isVirtual());
			}
		}

		@EventListener
		@Async
		public void onDemoEventAsync(DemoEvent event) {
			boolean b = asyncHandleEventCounter.get() % 2 == 1;
			log.info("Async event {} virtual:{}, throw exception:{} -> {}!!!", asyncHandleEventCounter.getAndIncrement(), Thread.currentThread().isVirtual(), b, event);
			if (b) {
				throw new RuntimeException("Test async exception virtual thread: " + Thread.currentThread().isVirtual());
			}
		}

		@EventListener
		public void onDemoEvent(DemoEvent event) {
			boolean b = syncHandleEventCounter.get() % 2 == 1;
			log.info("Sync event {} virtual:{}, throw exception:{} -> {}!!!", syncHandleEventCounter.getAndIncrement(), Thread.currentThread().isVirtual(), b, event);
			if (b) {
				throw new RuntimeException("Test sync exception virtual thread: " + Thread.currentThread().isVirtual());
			}
		}
	}
	
	record DemoEvent(int counter, String threadName, boolean threadVirtual) {}

}
