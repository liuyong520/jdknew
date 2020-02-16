package com.styz.new1.lambda;

import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author hp
 * @date 2020/2/9
 */
public interface MyWorker {
    /**
     * 做工作的方法，
     *
     * @param sth 所做的工作
     * @return 返回工作的报酬
     */
     void doWork(String sth);
}


class MyWorkerImpl implements MyWorker{

    @Override
    public void doWork(String sth) {
        System.out.println("做了一天的" + sth);
    }
}

class demo{
    public static void main(String[] args) {
        MyWorker myWorker = new MyWorkerImpl();
        myWorker.doWork("偷菜");
        MyWorker myWorker1 = new MyWorker() {
            @Override
            public void doWork(String sth) {
                System.out.println("做了一天的" + sth);
            }
        };
        Consumer<String> tConsumer = (sth) -> System.out.println("做了一天的" + sth);

        Function function = (sth) ->{
            System.out.println("做了一天的" + sth);
            return 10;
        };
        function.apply("偷菜");
        myWorker1.doWork("偷菜");

        MyWorker myWorker2 = (sth) -> System.out.println("做了一天的" + sth);
        myWorker2.doWork("偷菜");

        BiFunction<Integer, Integer, Boolean> function1 = (Integer x, Integer y) -> {
            if (x > y) {
                return true;
            } else {
                return false;
            }
        };

        Predicate<Integer> predicate = (x)->x>0?true:false;
        predicate.test(100);

        Optional<MyWorkerImpl> myWorker3 = Optional.ofNullable(new MyWorkerImpl());

    }


    private static boolean test(Integer x) {
        if (x > 0) {
            return true;
        }
        return false;
    }
}