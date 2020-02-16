package com.styz.new1.lambda;

import com.styz.new1.Employer;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class EmployeeTestTest {
    private List<Employer> employers;
    @Before
    public void setUp() throws Exception {
        employers = new ArrayList<>();
        employers.addAll(
                Arrays.asList(
                        new Employer("zhangsan",20,5733.34),
                        new Employer("lisi",17,2733.34),
                        new Employer("wangwu",32,2333.45),
                        new Employer("lili",35,6703.4),
                        new Employer("hanmei",45,4543.89),
                        new Employer("lili",15,9033.23),
                        new Employer("xilz",35,3567.45)
                )
        );
    }

    @Test
    public void test1() {
         //1.获取Stream流
        /**
         * 获取流的几种方式
         */
        //方式一：Collection中的Stream方法
        Collection col = new ArrayList();
        Stream stream = col.stream();
        Stream parallStream = col.parallelStream();

        //方式二：Arrays.stream获取Stream流
        String[] strings = {"1", "2", "3", "4"};
        Stream<String> numstream = Arrays.stream(strings);//从数组中获取流
        Stream<String> stream1 = Arrays.stream(strings, 1, 2);//从数组中获取部分流
        Stream<Integer> integerStream = Arrays.stream(new Integer[]{1, 2, 3, 4, 5});
        Stream<Long> longStream = Arrays.stream(new Long[]{1L, 2L, 3L, 4L, 5L});
        //方式三:Stream.of方法获取流
        Stream<String> strings1 = Stream.of(strings);
        //方式四:Stream中的其它方法
        Stream<Double> generate = Stream.generate(Math::random);//获取一个随机的序列流
        Stream<String> generate1 = Stream.generate(() -> "test");//产生一个Test的流
        Stream<Integer> iterate = Stream.iterate(0, x -> x + 1);//产生一个从零开始步长为1的序列流 无限流
        employers.stream().forEach(System.out::println);
        //获取员工年纪大于30的员工
        employers.stream().filter(x->x.getAge()>30).forEach(System.out::println);
        //获取员工年纪大于30的两位员工
        employers.stream().filter(x->x.getAge()>30).limit(2).forEach(System.out::println);
        //获取员工年纪大于30的员工并跳过两位员工
        employers.stream().filter(x->x.getAge()>30).skip(2).forEach(System.out::println);
        //获取员工年纪大于30的员工并去除重复数据
        employers.stream().filter(x->x.getAge()>30).distinct().forEach(System.out::println);
    }

    @Test
    public void test2() {
        //利用map操作获取所有员工的名字
        //1.
        employers.stream().map(x->x.getName()).forEach(System.out::println);
        //2 利用方法引用获取名字
        employers.stream().map(Employer::getName).forEach(System.out::println);
        employers.stream().filter(x->x.getAge()>30).peek(x-> System.out.println(x.getAge())).forEach(System.out::println);
        //判断员工是否全部大于20岁
        boolean ret = employers.stream().allMatch(x -> x.getAge() > 20);
        System.out.println("员工是否都满足大于20岁："+ ret);
        boolean ret1 = employers.stream().anyMatch(x -> x.getAge() > 20);
        System.out.println("员工是否存在大于20岁："+ ret1);
        boolean ret2 = employers.stream().noneMatch(x -> x.getAge() > 60);
        System.out.println("员工是否存在大于60岁："+ ret2);
        //满足大于20岁员工的第一位员工
        Optional<Employer> first = employers.stream().filter(x -> x.getAge() > 20).findFirst();

        // 获取年龄最大的员工
        Optional<Employer> max = employers.stream().max((x, y) -> Math.max(x.getAge(), y.getAge()));
        //找出员工中名字最长的
        Optional<Employer> reduce = employers.stream().reduce((s1, s2) -> s1.getName().length() >= s2.getName().length() ? s1 : s2);

    }

    @Test
    public void test7() {
        Stream<Integer> nums = Stream.iterate(0, x -> x + 1).limit(10);
        Integer reduce = nums.reduce(0, (x, y) -> x + y);
        System.out.println(reduce);
        Stream<Integer> nums1 = Stream.iterate(0, x -> x + 1).limit(10);
        Integer reduce1 = nums1.reduce(0, (x,y)->x+1+y ,(x, y) -> x + y+10);
        System.out.println(reduce1);
        List<Integer> integerList = new ArrayList<>(100);
        for(int i = 1;i <= 10;i++) { integerList.add(i);
        }
        Integer reduce2 = integerList.parallelStream().reduce(0, (x, y) -> x + 1 + y, (x, y) -> x + y);
        System.out.println(reduce2);
    }

    @Test
    public void test3() {
        //获取年龄最大的员工
        Optional<Employer> max = employers.stream().max(Comparator.comparingInt(Employer::getAge));
        Employer employer = max.get();
        System.out.println(employer);

        Optional<Employer> max2 = employers.stream().max(Comparator.comparingInt(Employer::getAge));
        System.out.println(max2.get());

        //获取年龄最小的员工
        Optional<Employer> min = employers.stream().max((x, y) -> Math.min(x.getAge(), y.getAge()));
        System.out.println(min.get());


    }

    @Test
    public void test4() {
        //输出薪水最低的两名员工
        employers.stream().sorted(Comparator.comparingDouble(Employer::getSalary)).limit(2).forEach(System.out::println);
        //输出薪水最高的两名员工
        employers.stream().sorted((x,y)->{
            if(x.getSalary()>=y.getSalary())return -1;else return 1;
        }).limit(2).forEach(System.out::println);

    }

    @Test
    public void Test8() {
        List<Employer> list = employers.stream().filter(x -> x.getAge() > 30).collect(Collectors.toList());
        list.stream().forEach(System.out::println);
        //获取大于30岁员工不同的人
        Set<String> set = employers.stream().filter(x -> x.getAge() > 30).map(Employer::getName).collect(Collectors.toSet());
        System.out.println(set);
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        Map<String, Employer> collect = employers.stream().filter(x -> {
            return seen.add(x.getName());
        }).collect(Collectors.toMap(x -> x.getName(), x -> x));
//        Map<String, Employer> collect1 = employers.stream().filter(x -> x.getAge() > 30).collect(Collectors.toMap(x -> x.getName(), Function.identity()));
        //公司员工的平均年纪
        Double average = employers.stream().collect(Collectors.averagingDouble(Employer::getAge));
        //公司年纪最大的员工年纪
        Optional<Integer> maxAge2 = employers.stream().map(Employer::getAge).collect(Collectors.maxBy(Integer::compareTo));
        Optional<Integer> minAge2 = employers.stream().map(Employer::getAge).collect(Collectors.minBy(Integer::compareTo));
        //求总人数
        Long number = employers.stream().collect(Collectors.counting());
        //求总薪水
        Double salarys = employers.stream().collect(Collectors.summingDouble(Employer::getSalary));
        //名字相同的的员工分为一组
        Map<String, List<Employer>> map = employers.stream().collect(Collectors.groupingBy(x -> x.getName()));
        //成年人分到一组，未成年分到一组
        Map<Boolean, List<Employer>> collect1 = employers.stream().collect(Collectors.partitioningBy(x -> x.getAge() > 18));

        String collect2 = employers.stream().map(Employer::getName).collect(Collectors.joining(","));
    }

    @Test
    public void test5() {
        //获取年级大于30岁的员工
        employers.stream().filter(x->x.getAge()>30).forEach(System.out::println);
        //获取年级大于30岁员工名字
        employers.stream().filter(x->x.getAge()>30).map(Employer::getName).forEach(System.out::println);
        //统计年级大于30岁员工总薪水
        Optional<Double> reduce = employers.stream().filter(x -> x.getAge() > 30).map(Employer::getSalary).reduce((x, y) -> x + y);
        System.out.println(reduce.get());
        //统计年级大于30岁员工总薪水
        Double reduce1 = employers.stream().filter(x -> x.getAge() > 30).map(Employer::getSalary).reduce(0.0, Double::sum);
        System.out.println(reduce1);
        double sum = employers.stream().filter(x -> x.getAge() > 30).mapToDouble(Employer::getSalary).summaryStatistics().getSum();
        System.out.println(sum);
        //三十岁之后薪水最高的
        double max = employers.stream().filter(x -> x.getAge() > 30).mapToDouble(Employer::getSalary).summaryStatistics().getMax();
        System.out.println(max);
        //三十岁之后薪水最少的
        double min = employers.stream().filter(x -> x.getAge() > 30).mapToDouble(Employer::getSalary).summaryStatistics().getMin();
        System.out.println(min);
        //获取年纪大于30岁员工平均薪水
        double average = employers.stream().filter(x -> x.getAge() > 30).mapToDouble(Employer::getSalary).summaryStatistics().getAverage();
        System.out.println(average);
    }

    @Test
    public void test6() {
        //将员工的名字字母大小进行转换
        employers.stream().map((x)->x.getName().toUpperCase()).forEach(System.out::println);
        //统计员工数量
        employers.stream().count();
    }

    @Test
    public void toInt() {
        IntSummaryStatistics statistics = Stream.of(1L, 2L, 3L, 4L).mapToInt(Long::intValue).summaryStatistics();
        System.out.println("最大值：" + statistics.getMax());
        System.out.println("最小值：" + statistics.getMin());
        System.out.println("平均值：" + statistics.getAverage());
        System.out.println("求和：" + statistics.getSum());
        System.out.println("计数：" + statistics.getCount());
    }

    @Test
    public void toLong() {
        LongSummaryStatistics statistics = Stream.of(1L, 2L, 3L, 4000000000000000000L).mapToLong(Long::longValue).summaryStatistics();
        System.out.println("最大值：" + statistics.getMax());
        System.out.println("最小值：" + statistics.getMin());
        System.out.println("平均值：" + statistics.getAverage());
        System.out.println("求和：" + statistics.getSum());
        System.out.println("计数：" + statistics.getCount());
    }

    @Test
    public void toDouble() {
        DoubleSummaryStatistics statistics = Stream.of(1, 2, 3.0, 5.2).mapToDouble(Number::doubleValue).summaryStatistics();
        System.out.println("最大值：" + statistics.getMax());
        System.out.println("最小值：" + statistics.getMin());
        System.out.println("平均值：" + statistics.getAverage());
        System.out.println("求和：" + statistics.getSum());
        System.out.println("计数：" + statistics.getCount());
    }


}