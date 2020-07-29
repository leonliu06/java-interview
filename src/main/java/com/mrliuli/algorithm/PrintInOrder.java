package com.mrliuli.algorithm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liuli on 2020/07/19.
 */
public class PrintInOrder implements Runnable {

    private AtomicInteger firstJobDone = new AtomicInteger(0);
    private AtomicInteger secondJobDone = new AtomicInteger(0);

    @Override
    public void run() {

        firstJobDone.incrementAndGet();

    }

    public void first(Runnable printFirst) throws InterruptedException {

        // printFirst.run() outputs "first". Do not change or remove this line.
        printFirst.run();
    }

    public void second(Runnable printSecond) throws InterruptedException {

        // printSecond.run() outputs "second". Do not change or remove this line.
        printSecond.run();
    }

    public void third(Runnable printThird) throws InterruptedException {

        // printThird.run() outputs "third". Do not change or remove this line.
        printThird.run();
    }

    public static void main(String[] args) throws InterruptedException {

        System.out.println(Arrays.toString(args));

        PrintInOrder printInOrder = new PrintInOrder();

        Thread a = new Thread(printInOrder);
        Thread b = new Thread(printInOrder);
        Thread c = new Thread(printInOrder);

        HashMap<String, String> fun = new HashMap<>();
        fun.put("1", "first");
        fun.put("2", "second");
        fun.put("3", "third");

        for(int i = 0; i < args.length; i++) {
            if(args[i].equalsIgnoreCase("1")) {
                printInOrder.first(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println();
                    }
                });
            }
        }


        // TODO: 2020/07/29  





    }

}
