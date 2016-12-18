package com.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestThreads {

	public static void main(String[] args) {
		//Test.testImmutable();
		//Test.testRunnable();	
		//Test.testCallable();
		Test.testCallableExecutorCompletionService();
	}
	

}

class Test{
	public static void testCallableExecutorCompletionService(){
		ExecutorService service = Executors.newFixedThreadPool(20);
		ExecutorCompletionService<String> completionService = new ExecutorCompletionService<>(service);
		List<Future<String>> list = new ArrayList<>();
		long time = 6000;
		for(int i=0; i < 10; i++){
			Callable<String> worker = new MyCallable(time);
			Future<String> future = completionService.submit(worker);
			list.add(future);
			time = time - 500;
		}
		
		for(int i= 0; i < list.size(); i++ ){
			try {
				//futures returned in completion order
				Future<String> future = completionService.take();
				System.out.println(future.get());
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		service.shutdown();
		
	}
	
	public static void testCallable(){
		ExecutorService service = Executors.newFixedThreadPool(20);
		List<Future<String>> list = new ArrayList<>();
		long time = 6000;
		for(int i=0; i < 10; i++){
			Callable<String> worker = new MyCallable(time);
			Future<String> future = service.submit(worker);
			list.add(future);
			time = time - 500;
		}
		
		for(Future<String> f : list){
			try {
				//futures retrieved in the list order  
				System.out.println(f.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		service.shutdown();
	}
	
	public static void testRunnable(){
		ExecutorService executor = Executors.newFixedThreadPool(2);
		for(int i=0; i< 10; i++){
			executor.execute(new MyRunnable());
			if(i == 5){
				executor.shutdown();
			}
		}
	} 
	
	public static void testImmutable(){
		ImmutableCollectiion i = new ImmutableCollectiion();
		i.test();
	}
}

class MyCallable implements Callable<String>{
	long sleepTime;
	public MyCallable(long time) {
		this.sleepTime = time;
	}
	@Override
	public String call() throws Exception {
		System.out.println(Thread.currentThread().getName() + " sleeping for "+ sleepTime);
		Thread.sleep(sleepTime);
		return Thread.currentThread().getName();
	}
}

class MyRunnable implements Runnable{
	@Override
	public void run() {
		System.out.println(Thread.currentThread().getName()+" my runnable");
	}
}

class ImmutableCollectiion {
	List<String> list = null;
	
	public ImmutableCollectiion() {
		list = new ArrayList<>();
		list.add("abc");
	}
	
	public List<String> getList() {
		return Collections.unmodifiableList(list);
	}

	public void setList(List<String> list) {
		this.list = list;
	}
	
	public void test(){
		this.getList().forEach(item -> System.out.println(item));
		this.getList().add("cannot add in unmodifiable");
		this.getList().forEach(item -> System.out.println(item));
	}
	
}
