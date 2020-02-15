package com.styz.new1.lambda;

/**
 * @author hp
 * @date 2020/2/9
 */
public interface Person {
    /**
     * 赚钱的接口
     * @param worker 做什么工作
     */
    void makeMoney(MyWorker worker);
}

class WokrerPerson implements Person {

    @Override
    public void makeMoney(MyWorker worker) {
        Integer money = worker.doWork("写代码");
        System.out.println("赚了多少钱" + money);
    }
}

class DemoTest{
    public static void main(String[] args) {


        MyWorker myWorker1 = sth-> {
            System.out.println("干了一天"+ sth);
            return 10;
        };

        Person person1 = worker ->{
                Integer money = worker.doWork("写代码");
                System.out.println("赚了多少钱" + money);
        };
        person1.makeMoney(myWorker1);
        Person person2 = worker -> worker.doWork("写代码");
        person2.makeMoney(myWorker1);
    }
}
