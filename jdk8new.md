# JDK8新特性介绍

## 十大新特性

1. **Lambda表达式**
2. **Stream函数式操作流元素集合**
3. **接口新增：默认方法与静态方法**
4. **方法引用,与Lambda表达式联合使用**
5. **引入重复注解**
6. **类型注解**
7. **最新的Date/Time API (JSR 310)**
8. **新增base64加解密API**
9. **数组并行（parallel）操作**
10. **JVM的PermGen空间被移除：取代它的是Metaspace（JEP 122）元空间**

## 一、Lambda表达式

 Lambda表达式是一个匿名函数（对于Java，不是100％正确，但是暂时假设它）。简而言之，它是没有声明的方法，即访问修饰符，返回值声明和名称。这是一种简写形式，可让您在将要使用的位置编写方法。在仅使用一种方法且方法定义简短的地方特别有用。它省去了为包含的类声明和编写单独的方法的工作 。其重要的一种用法是简化某些*匿名内部类*（`Anonymous Classes`）的写法。 

通俗点讲就是，**lambda表达式其实就是接口的匿名内部类的具体实现，它本质就是一个实例对象。**

为了引入介绍，我们先看一下其迭代过程，来具体感知一下Lambda表示的魅力。

jdk8之前，最开始的写法是这样的，直接通过new 接口的的具体实现来创建接口的实例。

```java
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
        //通过new 接口的的具体实现来创建接口的实例
        MyWorker myWorker = new MyWorkerImpl();
        myWorker.doWork("偷菜");
    }
}
```

2.演变为匿名内部类之后的写法

```java
class demo{
    public static void main(String[] args) {
        //通过匿名内部类之后的写法
        MyWorker myWorker1 = new MyWorker() {
            @Override
            public void doWork(String sth) {
                System.out.println("做了一天的" + sth);
            }
        };
        myWorker1.doWork("偷菜");
    }
}
```

jdk8之后，演变为Lambda表达式的写法如下

```java
class demo{
    public static void main(String[] args) {
       //Lambda表达式的写法
       MyWorker myWorker2 = (sth) -> System.out.println("做了一天的" + sth);
        myWorker2.doWork("偷菜");
    }
}
```

从直观感受上，给我的感觉是代码变得简洁了，虽然对不懂Lambda表达式的人来说，可能代码的可读性变差了，但是其实熟悉了之后，你会发现Lambda表达式其实并不复杂，学习成本也不高。

 完整的Lambda表达式由三部分组成：参数列表、箭头、声明语句； 

```java
(Type1 param1, Type2 param2, ..., TypeN paramN) -> { statment1; statment2; //............. return statmentM;}
```

1、绝大多数情况，编译器都可以从上下文环境中推断出lambda表达式的参数类型，所以参数可以省略：

```java
(param1,param2, ..., paramN) -> { statment1; statment2; //............. return statmentM;}
```

 

2、 当lambda表达式的参数个数只有一个，可以省略小括号：

```java
param1 -> { statment1; statment2; //............. return statmentM;}
```

 

3、 当lambda表达式只包含一条语句时，可以省略大括号、return和语句结尾的分号：

```java
param1 -> statment
```

示例：

```java
//需求比较两个数的大小，如果大于返回true，否则返回false
//接口抽象为：
  public interface Mycompare{
      public boolean bigger(int x,int y);
  } 
//lambda表达式完整的写法是
Mycompare compare = (int x,int y)->{
    if(x>y){
        return true;
    }else {
        return false;
    }
}
//先利用三元符号计算式优化如下
Mycompare compare = (int x,int y)->{
    return x>y?true:false;
}
//继续优化，根据上下文推断数据类型，可以优化去除掉数据类型
Mycompare compare = (x,y)->{
    return x>y?true:false;
}
//{}内的语句只有一条可以直接去掉括号，以及return字段
Mycompare compare = (x,y)-> x>y?true:false;

//如果参数只有一个Lambda表示可以去掉形参的小括号()
x->System.out.println(x);
```



### 2.引入函数式接口

函数式接口，也称为函数式编程。java中函数式接口必须满足如下条件

1**.接口中允许一个未实现的抽象方法。**如下接口就是函数式接口

```java
//java中常见的时间监听器接口
public interface ActionListener {
    //监听的具体事件
	void actionPerformed(ActionEvent e);
}
```

 这里并不需要专门定义一个类来实现 `ActionListener`，因为它只会在调用处被使用一次。用户一般会使用匿名类型把行为内联（inline） 

```java
button.addActionListener(new ActionListener() {
    public void actionPerformed(ActionEvent e) {
    ui.dazzle(e.getModifiers());
    }
});
```

尽管匿名内部类有着种种限制和问题，但是它有一个良好的特性，它和Java类型系统结合的十分紧密：每一个函数对象都对应一个接口类型。之所以说这个特性是良好的，是因为：

- 接口是 Java 类型系统的一部分
- 接口天然就拥有其运行时表示（Runtime representation）
- 接口可以通过 Javadoc 注释来表达一些非正式的协定（contract），例如，通过注释说明该操作应可交换（commutative）

但是是不是说函数式接口就只能有一个方法吗? 显然不是这样子的。

上面提到的 `ActionListener` 接口只有一个方法，大多数回调接口都拥有这个特征：比如 `Runnable`接口和 `Comparator` 接口。我们把这些只拥有一个方法的接口称为 *函数式接口*。（之前它们被称为 *SAM类型*，即 *单抽象方法类型*（Single Abstract Method））

我们并不需要额外的工作来声明一个接口是函数式接口：编译器会根据接口的结构自行判断（判断过程并非简单的对接口方法计数：一个接口可能冗余的定义了一个 `Object` 已经提供的方法，比如 `toString()`，或者定义了静态方法或默认方法，这些都不属于函数式接口方法的范畴）。不过API作者们可以通过 `@FunctionalInterface` 注解来显式指定一个接口是函数式接口（以避免无意声明了一个符合函数式标准的接口），加上这个注解之后，编译器就会验证该接口是否满足函数式接口的要求。

**函数式接口注解 @FunctionalInterface **

 [@FunctionalInterface](http://download.java.net/jdk8/docs/api/java/lang/FunctionalInterface.html)是Java 8中添加的新接口，用于指示接口类型声明旨在作为Java语言规范所定义的功能接口。Java 8还声明了Lambda表达式可以使用的功能接口的数量。当您注释的接口不是有效的Functional Interface时，@ FunctionalInterface可用于编译器级别的错误。以下是自定义定义的功能接口的示例。 

```java
@FunctionalInterface
public interface WorkerInterface {

public void doSomeWork();

}
```

 **函数式接口只能有一种抽象方法**。如果尝试在其中添加另一个抽象方法，则会引发编译时错误。例如： 

 ```java
@FunctionalInterface
public interface WorkerInterface {

public void doSomeWork();

public void doSomeMoreWork();

}
 ```

编译时会报错如下：

```java
Unexpected @FunctionalInterface annotation
@FunctionalInterface ^ WorkerInterface is not a functional interface multiple
non-overriding abstract methods found in interface WorkerInterface 1 error
```

实现函数式类型的另一种方式是引入一个全新的 *结构化* 函数类型，我们也称其为“箭头”类型。例如，一个接收 `String` 和 `Object` 并返回 `int` 的函数类型可以被表示为 `(String, Object) -> int`。我们仔细考虑了这个方式，但出于下面的原因，最终将其否定：

- 它会为Java类型系统引入额外的复杂度，并带来 结构类型（Structural Type） 和 指名类型（Nominal Type） 的混用。（Java 几乎全部使用指名类型）
- 它会导致类库风格的分歧——一些类库会继续使用回调接口，而另一些类库会使用结构化函数类型
- 它的语法会变得十分笨拙，尤其在包含受检异常（checked exception）之后
- 每个函数类型很难拥有其运行时表示，这意味着开发者会受到 类型擦除（erasure） 的困扰和局限。比如说，我们无法对方法 `m(T->U)` 和 `m(X->Y)` 进行重载（Overload）

所以我们选择了“使用已知类型”这条路——因为现有的类库大量使用了函数式接口，通过沿用这种模式，我们使得现有类库能够直接使用 lambda 表达式。例如下面是 Java SE 7 中已经存在的函数式接口：

- **java.lang.Runnable**
- **java.util.concurrent.Callable**
- **java.security.PrivilegedAction**
- **java.util.Comparator**
- **java.io.FileFilter**
- **java.beans.PropertyChangeListener**

**几种常见的函数式接口**

在**java.util.function**包里面有一下几种函数式接口。

- 消费型接口

  ```java
  public interface Consumer<T> {
  	//主要的方法是一个泛型参数，返回值是void，表示执行某种操作。
      void accept(T t);
  }
  ```

  

- 供给型接口

  ```java
  @FunctionalInterface
  public interface Supplier<T> {
  	//没有参数值，返回一个对象。表示通过此方法获取返回值
      T get();
  }
  ```

  

- 转换型接口

  ```java
  @FunctionalInterface
  public interface Function<T, R> {
  
      //转换接口，接受参数类型T的参数，返回R类型的数据，执行此方法相当于做了某种类型转换
      R apply(T t);
  }
  ```

  

- 断言型接口

  ```java
  @FunctionalInterface
  public interface Predicate<T> {
  	//主要方法是接受某个参数T，返回值为boolean类型，执行此方法相当于做了一次断言
      boolean test(T t);
  }
  ```

  java.util.function 此包中还有从这四个接口继承下来的其他函数式接口，由于基本差不多。这里我就介绍这几个接口的基本使用。

  上例中有一个接口

  ```java
  public interface MyWorker {
      /**
       * 做工作的方法，
       *
       * @param sth 所做的工作
       */
       void doWork(String sth);
  }
  ```

  此接口完全满足消费型接口的特点，那么此接口完全可以不用声明式定义。

  ```java
  Consumer<String> tConsumer = (sth) -> System.out.println("做了一天的" + sth);
  tConsumer.accept("偷菜");
  ```

  修改此例接口，改为有返回值就可以用Function接口接收.

  ```java
  public interface MyWorker {
      /**
       * 做工作的方法，
       *
       * @param sth 所做的工作
       * @return 返回工作的报酬
       */
       int doWork(String sth);
  }
  ```

  此接口完全满足转换型接口的特点，那么此接口完全可以不用声明式定义。

  ```java
   Function function = (sth) ->{
              System.out.println("做了一天的" + sth);
              return 10;
          };
  function.apply("偷菜");.
  ```

  继续，断言型接口

  ```java
  //断言型接口
  Predicate<Integer> predicate = (x)->x>0?true:false;
  predicate.test(100);
  ```

  继续转换型接口其中最为典型的就是Optional接口了

  ```java
  Optional<MyWorkerImpl> myWorker3 = Optional.ofNullable(new MyWorkerImpl());
  MyWorker myworker = myWorker3.get();
  ```

**函数编程非常关键的几个特性如下：**

#### 1、闭包与高阶函数

函数编程支持函数作为第一类对象，有时称为 闭包或者 仿函数（functor）对象。实质上，闭包是起函数的作用并可以像对象一样操作的对象。
与此类似，FP 语言支持 高阶函数。高阶函数可以用另一个函数（间接地，用一个表达式） 作为其输入参数，在某些情况下，它甚至返回一个函数作为其输出参数。这两种结构结合在一起使得可以用优雅的方式进行模块化编程，这是使用 FP 的最大好处。

#### 2、惰性计算

在惰性计算中，表达式不是在绑定到变量时立即计算，而是在求值程序需要产生表达式的值时进行计算。延迟的计算使您可以编写可能潜在地生成无穷输出的函数。因为不会计算多于程序的其余部分所需要的值，所以不需要担心由无穷计算所导致的 out-of-memory 错误。

#### 3、没有“副作用”

所谓"副作用"（side effect），指的是函数内部与外部互动（最典型的情况，就是修改全局变量的值），产生运算以外的其他结果。函数式编程强调没有"副作用"，意味着函数要保持独立，所有功能就是返回一个新的值，没有其他行为，尤其是不得修改外部变量的值。
综上所述，函数式编程可以简言之是： 使用不可变值和函数， 函数对一个值进行处理， 映射成另一个值。这个值在面向对象语言中可以理解为对象，另外这个值还可以作为函数的输入。

### 3.优势

1.代码更加简洁，程序员的开发效率会更高

2.延迟加载机制。提高程序性能

3.拥有函数式编程的优越特性

## 二、StreamAPI 函数式操作流元素集合

Java 8 中的 Stream 是对集合（Collection）对象功能的增强，它专注于对集合对象进行各种非常便利、高效的聚合操作，或者大批量数据操作 。

Stream API 借助于同样新出现的 Lambda 表达式，极大的提高编程效率和程序可读性。

同时它提供串行和并行两种模式进行汇聚操作，并发模式能够充分利用多核处理器的优势，使用 fork/join 并行方式来拆分任务和加速处理过程。

通常编写并行代码很难而且容易出错, 但使用 Stream API 无需编写一行多线程的代码，就可以很方便地写出高性能的并发程序。

（Stream 不是集合元素，它不是数据结构并不保存数据，它是有关算法和计算的，它更像一个高级版本的 Iterator。）

## 1、Stream的操作步骤

Stream有如下三个操作步骤：

**一、创建Stream**

从一个数据源，如集合、数组中获取流。

**二、中间操作**

一个操作的中间链，对数据源的数据进行操作。

**三、终止操作**

一个终止操作，执行中间操作链，并产生结果。

## 2、获取流的几种方式

假如有如下实体

```java
package com.styz.new1;

/**
 * @author hp
 * @date 2020/2/8
 */
public class Employer {
    private String name;
    private int age;
    private Double salary;

  	//get set tostring 方法省略
}

```

```java
private List<Employer> employers;
    @Before
    public void setUp() throws Exception {
        employers = new ArrayList<>();
        employers.addAll(
                Arrays.asList(
                        new Employer("zhangsan",20,5733.34),
                        new Employer("lisi",28,2733.34),
                        new Employer("wangwu",32,2333.45),
                        new Employer("lili",35,6703.4),
                        new Employer("hanmei",45,4543.89),
                        new Employer("lili",32,9033.23),
                        new Employer("xilz",35,3567.45)
                )
        );
    }
```



1. 通过Collections.stream 和Collections.parallerStream 方法获取

2. 通过Arrays.stream方法获取

3. 通过Stream.of方法获取流

4. 通过Stream中的其他方法

   ```java
   //第一种通过Collections.stream获取
   Stream stream = employers.stream();
   Stream parallStream = employers.parallelStream();
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
   Stream<Integer> iterate = Stream.iterate(0, x -> x + 1);//产生一个从零开始步长为1的序列流 无限
   
   Stream<Integer> iterate = Stream.iterate(0, x -> x + 1).limit(10)//产生10个数据
   ```

## 3、Stream中间操作

常见的*stream*接口继承关系如图：

![此图片来源于网络](C:\Users\hp\Pictures\Saved Pictures\Java_stream_Interfaces.png)

图中4种*stream*接口继承自`BaseStream`，其中`IntStream, LongStream, DoubleStream`对应三种基本类型（`int, long, double`，注意不是包装类型），`Stream`对应所有剩余类型的*stream*视图。为不同数据类型设置不同*stream*接口，可以

1. 提高性能，

2. 增加特定接口函数。

<img src="C:\Users\hp\Pictures\Saved Pictures\WRONG_Java_stream_Interfaces.png" alt="此图来源于网络" style="zoom:0%;" />

你可能会奇怪为什么不把`IntStream`等设计成`Stream`的子接口？毕竟这接口中的方法名大部分是一样的。答案是这些方法的名字虽然相同，但是返回类型不同，如果设计成父子接口关系，这些方法将不能共存，因为Java不允许只有返回类型不同的方法重载。

虽然大部分情况下*stream*是容器调用`Collection.stream()`方法得到的，但*stream*和*collections*有以下不同：

- **无存储**。*stream*不是一种数据结构，它只是某种数据源的一个视图，数据源可以是一个数组，Java容器或I/O channel等。
- **为函数式编程而生**。对*stream*的任何修改都不会修改背后的数据源，比如对*stream*执行过滤操作并不会删除被过滤的元素，而是会产生一个不包含被过滤元素的新*stream*。
- **惰式执行**。*stream*上的操作并不会立即执行，只有等到用户真正需要结果的时候才会执行。
- **可消费性**。*stream*只能被“消费”一次，一旦遍历过就会失效，就像容器的迭代器那样，想要再次遍历必须重新生成。

对*stream*的操作分为为两类，**中间操作(*intermediate operations*)和结束操作(*terminal operations*)**，二者特点是：

1. __中间操作总是会惰式执行__，调用中间操作只会生成一个标记了该操作的新*stream*，仅此而已。
2. __结束操作会触发实际计算__，计算发生时会把所有中间操作积攒的操作以*pipeline*的方式执行，这样可以减少迭代次数。计算完成之后*stream*就会失效。

如果你熟悉Apache Spark RDD，对*stream*的这个特点应该不陌生。

```java
//第一种通过Collections.stream获取
Stream stream = employers.stream();
//中间操作
stream.map(x->{x.getName();System.out.println(x.getName())}).limit(2).skip(2);
//如果没有结束操作，那么上面的中间操作将不会执行，那么打印语句将不会输出。
//结束操作
stream.forEach(System.out::println);
```

下表汇总了`Stream`接口的部分常见方法：

| 操作类型 | 接口方法                                                     |
| -------- | ------------------------------------------------------------ |
| 中间操作 | concat() distinct() filter() flatMap() limit() map() peek() <br> skip() sorted() parallel() sequential() unordered() |
| 结束操作 | allMatch() anyMatch() collect() count() findAny() findFirst() <br> forEach() forEachOrdered() max() min() noneMatch() reduce() toArray() |

区分中间操作和结束操作最简单的方法，就是看方法的返回值，返回值为*stream*的大都是中间操作，否则是结束操作。

总的说来中间操作可以分为，筛选与切片，映射，查找排序，聚合统计等。

**一、筛选于切片**

- `filter`：接收`Lambda`，从流中排除某些操作；

   函数原型为`Stream<T> filter(Predicate<? super T> predicate)`，作用是返回一个只包含满足`predicate`条件元素的`Stream`。 如图：

   <img src="https://github.com/kanwangzjm/JavaLambdaInternals/raw/master/Figures/Stream.filter.png" alt="Stream filter" style="zoom:50%;" /> 

  

  ```java
  // 保留长度等于3的字符串
  Stream<String> stream= Stream.of("I", "love", "you", "too");
  stream.filter(str -> str.length()==3).forEach(str->System.out.println(str));
  ```

  

- `limit`：截断流，使其元素不超过给定对象

  函数原型为` Stream<T> limit(long maxSize);`作用是截取流中的符合条件的`maxSize`的流。

  如下图：

  

  ```java
  Stream<String> stream= Stream.of("1", "2", "3", "4");
  stream.limit(2).forEach(str->System.out.println(str));
  ```

  

- `skip(n)`：跳过元素，返回一个扔掉了前n个元素的流，若流中元素不足n个，则返回一个空流，与limit(n)互补

  函数原型为`Stream<T> skip(long n);`作用是跳过n个元素，返回满足条件的流。

  如下图：

  

  ```java
  Stream<String> stream= Stream.of("1", "2", "3", "4");
  stream.skip(2).forEach(str->System.out.println(str));
  ```

  

- `distinct`：筛选，通过流所生成元素的hashCode()和equals()去除重复元素。

   函数原型为`Stream<T> distinct()`，作用是返回一个去除重复元素之后的`Stream`。

    <img src="https://github.com/kanwangzjm/JavaLambdaInternals/raw/master/Figures/Stream.distinct.png" alt="Stream distinct" style="zoom:50%;" /> 

  ```java
  Stream<String> stream= Stream.of("I", "love", "you", "too", "too");
  stream.distinct()
      .forEach(str -> System.out.println(str));
  ```

   上述代码会输出去掉一个`too`之后的其余字符串。 

### 示例

```java
//filter操作
//获取员工年纪大于30的员工
employers.stream().filter(x->x.getAge()>30).forEach(System.out::println);
//limit操作
//获取员工年纪大于30的两位员工
employers.stream().filter(x>x.getAge()>30).limit(2).forEach(System.out::println);
//skip操作
//获取员工年纪大于30的员工并跳过两位员工
employers.stream().filter(x>x.getAge()>30).skip(2).forEach(System.out::println);
//去重操作
//获取员工年纪大于30的员工并去除重复数据
employers.stream().filter(x>x.getAge()>30).distinct().forEach(System.out::println);
```



**二、映射**

- `map`--接收`Lambda`，将元素转换成其他形式或提取信息。接收一个函数作为参数，该函数会被应用到每个元素上，并将其映射成一个新的元素。

   函数原型为`<R> Stream<R> map(Function<? super T,? extend R> mapper)`，作用是返回一个对当前所有元素执行执行`mapper`之后的结果组成的`Stream`。直观的说，就是对每个元素按照某种操作进行转换，转换前后`Stream`中元素的个数不会改变，但元素的类型取决于转换之后的类型。 

 <img src="https://github.com/kanwangzjm/JavaLambdaInternals/raw/master/Figures/Stream.map.png" alt="Stream map" style="zoom:50%;" /> 

```java
Stream<String> stream　= Stream.of("I", "love", "you", "too");
//将字符串转化为大写
stream.map(str -> str.toUpperCase())
    .forEach(str -> System.out.println(str));
```

 上述代码将输出原字符串的大写形式。 

- `flatMap`--接收一个函数作为参数，将流中的每个值都换成另一个流，然后把所有流连接成一个流

   函数原型为` <R> Stream<R> flatMap(Function<? super T,? extend R> mapper)`，作用是对每个元素执行`mapper`指定的操作，并用所有`mapper`返回的`Stream`中的元素组成一个新的`Stream`作为最终返回结果。说起来太拗口，通俗的讲`flatMap()`的作用就相当于把原*stream*中的所有元素都"摊平"之后组成的`Stream`，转换前后元素的个数和类型都可能会改变  如下图

   <img src="https://github.com/kanwangzjm/JavaLambdaInternals/raw/master/Figures/Stream.flatMap.png" alt="Stream flatMap" style="zoom:50%;" /> 

  ```java
  Stream<List<Integer>> stream = Stream.of(Arrays.asList(1,2), Arrays.asList(3, 4, 5));
  //flatMap映射之后会生成一个新的stream
  stream.flatMap(list -> list.stream())
      .forEach(i -> System.out.println(i));
  ```

   上述代码中，原来的`stream`中有两个元素，分别是两个`List`，执行`flatMap()`之后，将每个`List`都“摊平”成了一个个的数字，所以会新产生一个由5个数字组成的`Stream`。所以最终将输出1~5这5个数字。

**三、排序**

- `sorted` 排序

   排序函数有两个，一个是用自然顺序排序，一个是使用自定义比较器排序，函数原型分别为`Stream<T>　sorted()`和`Stream<T>　sorted(Comparator<? super T> comparator)`。 

  ```java
  Stream<String> stream= Stream.of("I", "love", "you", "too");
  stream.sorted((str1, str2) -> str1.length()-str2.length())
      .forEach(str -> System.out.println(str));
  ```

   上述代码将输出按照长度升序排序后的字符串，结果完全在预料之中。 

  **示例:**

  ```java
  //定制排序，对前面的employlist按年龄从小到大排序,年龄相同，则再按姓名排序:
  final Stream<Employ> sorted = employlist.stream().sorted((p1, p2) -> {
  
      if (p1.getAge().equals(p2.getAge())) {
          return p1.getName().compareTo(p2.getName());
      } else {
          return p1.getAge().compareTo(p2.getAge());
      }
  });
  sorted.forEach(System.out::println);
  
  //输出薪水最高的两名员工
  employers.stream().sorted((x,y)->{
      if(x.getSalary()>=y.getSalary())return -1;else return 1;
  }).limit(2).forEach(System.out::println);
  ```

## 4、终止操作

**一，查找与匹配**

- `allMatch `--检查是否匹配所有元素

  函数原型:`boolean allMatch(Predicate<? super T> predicate);` 接受一个断言型接口。返回一个boolean类型。

  ```java
  //判断员工是否全部大于20岁    
  boolean ret = employers.stream().allMatch(x -> x.getAge() > 20);
  System.out.println("员工是否都满足大于20岁："+ ret);
  ```

  

- `anyMatch`--检查是否至少匹配一个元素

  函数原型`boolean anyMatch(Predicate<? super T> predicate);`如果匹配一个元素直接返回true

  ```java
  boolean ret = employers.stream().anyMatch(x -> x.getAge() > 20);
  System.out.println("员工是否存在大于20岁："+ ret);
  ```

  

- `noneMatch`--检查是否没有匹配所有元素

  函数原型`boolean noneMatch(Predicate<? super T> predicate);`如果没有一个元素匹配直接返回true

  ```java
  boolean ret2 = employers.stream().noneMatch(x -> x.getAge() > 60);
  System.out.println("员工是否存在大于60岁："+ ret2);
  ```

  

- `findFirst`--返回第一个元素

  函数原型`Optional<T> findFirst();` 获取匹配值的第一个元素。

  ```java
  //满足大于20岁员工的第一位员工
  Optional<Employer> first = employers.stream().filter(x -> x.getAge() > 20).findFirst();
  ```

  

- `findAny`--返回当前流中的任意元素

  函数原型`Optional<T> findAny();`遍历匹配条件，如果匹配到满足条件的直接返回。

  ```java
  //满足大于20岁员工的员工
  Optional<Employer> first = employers.stream().filter(x -> x.getAge() > 20).findAny();
  ```

  

- `count`--返回流中元素的总个数

  ```java
  //返回元素总数
  employers.stream().count()
  ```

  

- `max`--返回流中最大值

  函数原型`Optional<T> max(Comparator<? super T> comparator);`其中`Comparator`也是一个函数式接口。传入比较的条件，返回最大的值

  ```java
  // 获取年龄最大的员工
  Optional<Employer> max = employers.stream().max((x, y) -> Math.max(x.getAge(), y.getAge()));
  //获取年龄最大的员工
  Optional<Employer> max = employers.stream().max(Comparator.comparingInt(Employer::getAge));
  //如果Employer年龄属性为包装类型还可以这样写
  Optional<Employer> maxAge = employers.stream().max((p1, p2) -> p1.getAge().compareTo(p2.getAge()));
  ```

  

- `min`--返回流中最小值

- 函数原型`Optional<T> min(Comparator<? super T> comparator);`其中`Comparator`也是一个函数式接口。传入比较的条件，返回最小的值

  ```java
  // 获取年龄最小的员工
  Optional<Employer> max = employers.stream().min((x, y) -> Math.min(x.getAge(), y.getAge()));
  ```

  

**二、归约**

规约操作（*reduction operation*）又被称作折叠操作（*fold*），是通过某个连接动作将所有元素汇总成一个汇总结果的过程。元素求和、求最大值或最小值、求出元素总个数、将所有元素转换成一个列表或集合，都属于规约操作。*Stream*类库有通用的规约操作`reduce()`，也有一些为简化书写而设计的专用规约操作，比如`sum()`、`max()`、`min()`、`count()`等。

最大或最小值这类规约操作很好理解（至少方法语义上是这样），我们着重介绍`reduce()`，这是比较有魔法的地方。

- `reduce`操作

  函数原型有三个

  ```java
  //运算操作BinaryOperator
  /*等价于{@code
       *     boolean foundAny = false;
       *     T result = null;
       *     for (T element : this stream) {
       *         if (!foundAny) {
       *             foundAny = true;
       *             result = element;
       *         }
       *         else
       *             result = accumulator.apply(result, element);
       *     }
       *     return foundAny ? Optional.of(result) : Optional.empty();	
      */
  Optional<T> reduce(BinaryOperator<T> accumulator);
  
  //identity为初始值，运算操作BinaryOperator
  /*等价于T result = identity;
       *     for (T element : this stream)
       *         result = accumulator.apply(result, element)
       *     return result;
       */
  T reduce(T identity, BinaryOperator<T> accumulator);
  //identity为初始值,accumulator,combiner 分别为两个运算因子combiner只对并行流有效
   /*并行等价于U result = identity;
       *     for (T element : this stream)
       *         combiner.apply(u, accumulator.apply(result, t)) == accumulator.apply(u, t)
       *     return result;
       */
  //串行流等价于T reduce(T identity, BinaryOperator<T> accumulator);的等价描述
  <U> U reduce(U identity,
                   BiFunction<U, ? super T, U> accumulator,
                   BinaryOperator<U> combiner);
  ```

   虽然函数定义越来越长，但语义不曾改变，多的参数只是为了指明初始值（参数*identity*），或者是指定并行执行时多个部分结果的合并方式（参数*combiner*）。`reduce()`最常用的场景就是从一堆值中生成一个值。用这么复杂的函数去求一个最大或最小值，你是不是觉得设计者有病。其实不然，因为“大”和“小”或者“求和"有时会有不同的语义。 

  ```java
  //需求：从一组单词中找出最长的单词。这里“大”的含义就是“长”。
  // 找出最长的单词
  Stream<String> stream = Stream.of("I", "love", "you", "too");
  Optional<String> longest = stream.reduce((s1, s2) -> s1.length()>=s2.length() ? s1 : s2);
  
  //等价于Optional<String> longest = stream.max((s1, s2) -> s1.length()-s2.length());
  System.out.println(longest.get());
  ```

   上述代码会选出最长的单词*love*，其中*Optional*是（一个）值的容器，使用它可以避免*null*值的麻烦。当然可以使用`Stream.max(Comparator comparator)`方法来达到同等效果，但`reduce()`自有其存在的理由。 

   如果需求为：**求出一组单词的长度之和**。这是个**“求和”**操作，操作对象输入类型是`String`，而结果类型是`Integer`。

  ```java
  // 求单词长度之和
  Stream<String> stream = Stream.of("I", "love", "you", "too");
  Integer lengthSum = stream.reduce(0,　// 初始值　// (1)
          (sum, str) -> sum+str.length(), // 累加器 // (2)
          (a, b) -> a+b);　// 部分和拼接器，并行执行时才会用到 // (3)
  // 等价于 int lengthSum = stream.mapToInt(str -> str.length()).sum();
  System.out.println(lengthSum);
  ```

    上述代码标号(2)处将i. 字符串映射成长度，ii. 并和当前累加和相加。这显然是两步操作，使用`reduce()`函数将这两步合二为一，更有助于提升性能。如果想要使用`map()`和`sum()`组合来达到上述目的，也是可以的。 

  可以先map然后reduce。这就有点hadoop中mapReduce操作的样子了。

   `reduce()`擅长的是生成一个值，如果想要从*Stream*生成一个集合或者*Map*等复杂的对象该怎么办呢？那就是收集  `collect() `

  **示例**：

  ```java
  Stream<Integer> nums = Stream.iterate(0, x -> x + 1).limit(10);
  //统计1到10的和
  //T reduce(T identity, BinaryOperator<T> accumulator) 
  //结果45
  Integer reduce = nums.reduce(0, (x, y) -> x + y);
  //U reduce(U identity,BiFunction<U, ? super T, U> accumulator,BinaryOperator<U> combiner);
  //结果55 只执行了(x,y)->x+1+y
  Integer reduce1 = nums1.reduce(0, (x,y)->x+1+y ,(x, y) -> x + y);
  System.out.println(reduce1);
  //并行流 输出为65，两个计算因子都执行了
  List<Integer> integerList = new ArrayList<>(100);
  for(int i = 1;i <= 10;i++) { integerList.add(i);
  }
  Integer reduce2 = integerList.parallelStream().reduce(0, (x, y) -> x + 1 + y, (x, y) -> x + y);
  System.out.println(reduce2);
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
  ```

  

**三、收集**

 如果你发现某个功能在*Stream*接口中没找到，十有八九可以通过`collect()`方法实现。`collect()`是*Stream*接口方法中最灵活的一个，学会它才算真正入门Java函数式编程 

- collect操作  -将流转换为其他形式，接收一个Collector接口实现 ，用于给Stream中汇总的方法 

  ```java
  <R, A> R collect(Collector<? super T, A, R> collector);
   
  <R> R collect(Supplier<R> supplier,
                     BiConsumer<R, ? super T> accumulator,
                     BiConsumer<R, R> combiner);
  ```

   collect不光可以将流转换成其他集合等形式，还可以进行归约等操作，具体实现也很简单，主要是与Collectors类搭配使用。 

  ```java
  // 将Stream转换成容器或Map
  Stream<String> stream = Stream.of("I", "love", "you", "too");
  //转换成List
  List<String> list = stream.collect(Collectors.toList()); 
  //转换成Set
  Set<String> set = stream.collect(Collectors.toSet()); 
  //转换成Map
  Map<String, Integer> map = stream.collect(Collectors.toMap(Function.identity(), String::length));
  
  List<Employer> list = employers.stream().filter(x -> x.getAge() >30).collect(Collectors.toList());
  list.stream().forEach(System.out::println);
  //获取大于30岁员工不同的人
  Set<String> set = employers.stream().filter(x -> x.getAge() >30).map(Employer::getName).collect(Collectors.toSet());
  System.out.println(set);
  //由于名字相同，会抛出Key重复的异常。如果需要正常收集，需要先过滤掉相同名字的员工
  Map<String, Employer> collect = employers.stream().filter(x -> x.getAge() > 30).collect(Collectors.toMap(x -> x.getName(), x -> x));
  Map<String, Employer> collect1 = employers.stream().filter(x -> x.getAge() > 30).collect(Collectors.toMap(x -> x.getName(), Function.identity()));
  //所以要先过滤
  Set<Object> seen = ConcurrentHashMap.newKeySet();
  Map<String, Employer> collect = employers.stream().filter(x -> {
      return seen.add(x.getName());
  }).collect(Collectors.toMap(x -> x.getName(), x -> x));
  //公司员工的平均年纪
  Double average = employers.stream().collect(Collectors.averagingDouble(Employer::getAge));
  
  //公司年纪最大的员工年纪
  Optional<Integer> maxAge2 = employers.stream().map(Employer::getAge).collect(Collectors.maxBy(Integer::compareTo));
  Optional<Integer> minAge2 = employers.stream().map(Employer::getAge).collect(Collectors.minBy(Integer::compareTo));
   //求总人数
  Long number = employers.stream().collect(Collectors.counting());
  //求总薪水
  Double salarys = employers.stream().collect(Collectors.summingDouble(Employer::getSalary));
  //分组
  //名字相同的的员工分为一组
  Map<String, List<Employer>> map = employers.stream().collect(Collectors.groupingBy(x -> x.getName()));
  //分区 满足条件分到一组，不满足分到一组里
  //成年人分到一组，未成年分到一组
  Map<Boolean, List<Employer>> collect1 = employers.stream().collect(Collectors.partitioningBy(x -> x.getAge() > 18));
  //连接
  //把名字连接起来，用逗号分隔
  String collect2 = employers.stream().map(Employer::getName).collect(Collectors.joining(","));
  ```

  

## 三、默认方法与静态方法 

在JDK1.8之前，我们扩展一个接口只能通过继承来实现，但是这样会引入新的接口定义。如果直接在接口中加方法，所有的实现类都需要重写该方法，极为笨重， 否则向接口添加方法就会破坏现有的接口实现。

JDK1,8中引入**默认方法default修饰**  的目标即是解决这个问题，使得接口在发布之后仍能被逐步演化。 添加默认实现的方法，实现类无需再重写该方法。 当接口继承其它接口时，我们既可以为它所继承而来的抽象方法提供一个默认实现，也可以为它继承而来的默认方法提供一个新的实现，还可以把它继承而来的默认方法重新抽象化 。

```java
public interface Interface1 {
 
	void method1(String str);
	//默认方法
	default void log(String str){
		System.out.println("logging::"+str);
	}

}
```

 除了默认方法，Jdk1.8 还在允许在接口中定义 ***静态static修饰*** 方法。这使得我们可以从接口直接调用和它相关的辅助方法（Helper method），而不是从其它的类中调用（之前这样的类往往以对应接口的复数命名，例如 `Collections`）。比如，我们一般需要使用静态辅助方法生成实现 `Comparator` 的比较器，在Java SE 8中我们可以直接把该静态方法定义在 `Comparator` 接口中： 

```java
public static <T, U extends Comparable<? super U>>
    Comparator<T> comparing(Function<T, U> keyExtractor) {
    return (c1, c2) -> keyExtractor.apply(c1).compareTo(keyExtractor.apply(c2));
}
```



## 四、方法引用

## 五、引入重复注解

## 六、类型注解

## 七、新增时间操作相关API

## 八、新增Base64加解密API

## 九、数组并行（parallel）操作

## 十、JVM的PermGen空间被移除：取代它的是Metaspace（JEP 122）元空间