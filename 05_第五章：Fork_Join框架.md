# Fork/Join框架

本章将学习如下内容：

- 创建fork/join池
- 连接任务结果
- 异步运行任务
- 任务中抛出异常
- 取消任务

## 引言

通常情况下，当开发简单的并发Java应用时，实现Runnable对象和对应的Thread对象，在程序中控制这些线程的创建、执行和状态。Java 5引入Executor和ExecutorService及其实现类（例如ThreadPoolExecutor类）进行改进。

**执行器**框架将任务创建和执行分离，使用此框架只需要实现Runnable对象，以及使用Executor对象。发送Runnable任务到执行器，框架用来创建、管理、和结束必要的线程来执行这些任务。

Java 7 进一步完善且提供ExecutorService接口的附加实现，针对于特定问题，这就是**fork/join框架**。

这个框架被设计成用来解决的问题能够使用分治法拆分成许多小任务。在每个任务中，检查想要分解的问题规模，如果大于已确定的规模，则使用此框架将问题拆分成更小的任务。如果问题规模小于已确定的规模，则直接在任务 中解决问题。然后，视情况返回结果。如下图所示，描述此框架概念：

没有一个准则来决定是否细分的问题的参照规模，而是取决问题的特性。可以使用任务中需要处理的元素数量，以及执行时间的估值来决定参照规模。测试不同的参照规模选择对问题最合适的解决方案，可以将ForkJoinPool看成一种特殊的Executor。

这个框架基于如下两种操作：

- **Fork操作**：当使用此框架将任务拆分成许多小任务，并且执行时。
- **Join操作**：当任务等待其拆分的任务结束时，用来结合这些任务的结果。

fork/join和执行器框架的主要区别是**工作窃取算法**。与执行器框架不同，当任务等待其拆分的子任务结束时，使用join操作，用来执行任务的线程（称之为**工作线程**）寻找还没有被执行的任务，并且开始执行。因此，线程在运行期间得到充分利用，因此提高应用性能。

为达到此目的，通过fork/join框架执行的任务有如下局限性：

- 任务只能将fork()和join()操作作为同步机制。如果任务使用了其它的同步机制，工作线程在同步操作中无法执行其它任务。例如，如果在fork/join框架中设置任务休眠，正在执行的工作线程痐在休眠期间执行其它任务。
- 任务不能用作I/O操作，例如在一个文件中读写数据。
- 任务无法抛出已选中的异常，需要附带必要代码来处理它们。

fork/join框架核心由如下两个类组成：

- ForkJoinPool：这个类实现ExecutorService接口和工作窃取算法。用来管理工作线程，并且提供任务的状态和执行信息。
- ForkJoinTask：这个类是将在ForkJoinPool中执行的任务的基类。它提供了在任务内执行fork()和join()操作的机制，以及控制任务状态的方法。通常为了实现fork/join任务，需要实现此类中三个子类中的其中一个：RecursiveAction处理任务且没有结果，RecursiveTask处理任务且有一个结果，以及当所有的子任务已经完成时，CountedCompleter处理任务用来加载一个完成动作。

Java 7中包含了此框架提供的绝大多数特性，然而Java 8中此框架提供了次要特性，包括默认的ForkJoinPool对象，使用ForkJoinPool类的静态方法commonPool()方法获得此对象。默认的fork/join执行器使用计算机可用处理器的数量来作为默认的线程数。可以通过改变系统属性来改变此方法的默认表现，java.util.concurrent.ForkJoinPool.common.parallelism，这个默认池可以被并发API其它类内部使用，例如**平行流**。Java 8 还包括之前提到的CountedCompleter类。

本章分为五小节，展现如何高效的使用fork/join框架进行开发。

## 创建fork/join池

本节中，学习如何使用fork/join框架基本元素，包括如下：

- 创建ForkJoinPool对象执行任务
- 创建在池中被执行的ForkJoinTask子类


在本范例中用到的fork/join框架主要特征如下所示：

- 使用默认构造函数创建ForkJoinPool。

- 在任务内部，使用Java API文档中退件的程序结构：

  ```java
  if (problem size > default size){
      tasks=divide(task);
      execute(tasks);
  } else {
  	resolve problem using another algorithm;
  }
  ```

- 用同步方式执行任务，当一个任务执行两个或多个子任务时，主任务等待子任务的结束。通过这种方式，执行任务的线程（称之为为工作线程）将寻找其它任务来运行，充分利用它们的执行时间。

- 将要实现的任务不会返回任何结果，所以在实现过程中把RecursiveAction作为基类。


### 准备工作

本范例通过Eclipse开发工具实现。如果使用诸如NetBeans的开发工具，打开并创建一个新的Java项目。

### 实现过程

在本节中，将要实现一个更新产品列表价格的任务。初始任务将负责更新列表中的所有元素，这里使用长度10作为参照规模。如果任务更新超过10个元素，它将列表分成两个部分，并且创建两个子任务分别更新各自部分的产品价格。

通过如下步骤实现范例：

1. 创建名为Product的类，用来存储产品名称和价格：

   ```java
   public class Product {
   ```

2. 定义名为name的私有String属性，名为price的私有double属性：

   ```java
   	private String name;
   	private double price;
   ```

3. 实现两个属性的getter和setter方法，由于实现非常简单，这里不展现源码：

4. 创建名为ProductListGenerator的类，生成随机产品列：

   ```java
   public class ProductListGenerator {
   ```

5. 实现generate()方法，接收int参数作为列表长度，并且返回List<Product> ，作为生成的产品列表：

   ```java
   	public List<Product> generate(int size) {
   ```

6. 创建返回产品列表的对象：

   ```java
   		List<Product> ret = new ArrayList<Product>();
   ```

7. 生成产品列表，赋予所有产品相同价格，例如，10块，来检查程序是否工作正常：

   ```java
   		for(int i = 0 ; i < size ; i ++) {
   			Product product = new Product();
   			product.setName("Product " + i);
   			product.setPrice(10);
   			ret.add(product);
   		}
   		
   		return ret;
   	}
   ```

8. 创建名为Task的类，继承RecursiveAction类：

   ```java
   public class Task extends RecursiveAction{
   ```

9. 定义名为products的私有List<Product> 属性：

   ```java
   	private List<Product> products;
   ```

10. 定义名为first和last的两个私有int属性，用来确定任务需要处理的产品数量：

    ```java
    	private int first;
    	private int last;
    ```

11. 定义名为increment的私有double属性，存储产品价格的增量：

    ```java
    	private double increment;
    ```

12. 实现类构造函数，初始化类所有属性：

    ```java
    	public Task(List<Product> products, int first, int last, double increment) {
    		this.products = products;
    		this.first = first;
    		this.last = last;
    		this.increment = increment;
    	}
    ```

13. 实现compute()方法，用来实现任务的逻辑操作：

    ```java
    	@Override
    	protected void compute() {	
    ```

14. 如果last和first属性差小于10（任务只能更新小于10个产品的价格），使用updatePrices()方法增加这组产品的价格：

    ```java
    		if( last - first < 10) {
    			updatePrices();
    ```

15. 如果last和first属性差大于或等于10，创建两个新的Task对象，一个处理产品列表的前半部分，另一个处理第二部分，然后使用invokeAll()方法在ForkJoinPool中执行这两个任务：

    ```java
    		}else{
    			int middle = (last + first) / 2;
    			System.out.printf("Task : Pending tasks : %s\n", getQueuedTaskCount());
    			Task t1 = new Task(products, first, middle + 1, increment);
    			Task t2 = new Task(products, middle + 1, last, increment);
    			invokeAll(t1, t2);
    		}
    	}
    ```

16. 实现updatePrices()方法，这个方法更新产品列表中位置占据在first和last属性值之间的产品价格：

    ```java
    	private void updatePrices() {
    		for(int i = first ; i < last ; i ++){
    			Product product = products.get(i);
    			product.setPrice(product.getPrice() * (1 + increment));
    		}
    	}
    ```

17. 实现范例的主方法，创建一个包含main()方法的Main类：

    ```java
    public class Main {
    	public static void main(String[] args) {
    ```

18. 使用ProductListGenerator类创建数量为10000的产品列表：

    ```java
    		ProductListGenerator generator = new ProductListGenerator();
    		List<Product> products = generator.generate(10000);
    ```

19. 创建新的Task对象用来更新列表中所有产品的prices。参数first赋值0，参数last赋值10000（产品列表长度）：

    ```java
    		Task task = new Task(products, 0, products.size(), 0.2);
    ```

20. 使用无参构造函数创建ForkJoinPool()：

    ```java
    		ForkJoinPool pool = new ForkJoinPool();
    ```

21. 在线程池中使用execute()方法执行任务：

    ```java
    		pool.execute(task);
    ```

22. 实现一段代码，每隔五毫秒展示线程池中的变化并输出部分参数值到控制台，直到任务结束执行：

    ```java
    		do {
    			System.out.printf("************************************\n");
    			System.out.printf("Main : Thread Count : %d\n", pool.getActiveThreadCount());
    			System.out.printf("Main : Thread Steal : %d\n", pool.getStealCount());
    			System.out.printf("Main : Parallelism : %d\n", pool.getParallelism());
    			System.out.printf("************************************\n");
    			try {
    				TimeUnit.MILLISECONDS.sleep(5);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}while (!task.isDone());
    ```

23. 使用shutdown()方法关闭线程池：

    ```java
    		pool.shutdown();
    ```

24. 使用isCompletedNormally()方法检查任务是否已经完成且没有错误，这种情况下，输出信息到控制台：

    ```java
    		if(task.isCompletedNormally()){
    			System.out.printf("Main : The process has completed normally.\n");
    		}
    ```

25. 所有产品提高后的预期价格是12。输出价格不等于12的所有产品名称和价格，来检查所有的产品是否已经正确地提高价格：

    ```java
    		for(int i = 0 ; i < products.size() ; i ++){
    			Product product = products.get(i);
    			if(product.getPrice() != 12) {
    				System.out.printf("Product %s : %f\n", product.getName(), product.getPrice());
    			}
    		}
    ```

26. 输出指明程序结束的信息到控制台：

    ```java
    		System.out.printf("Main : End of the program.\n");
    ```


###工作原理

在范例中，创建在池中执行的ForkJoinPool对象和ForkJoinTask类的子类。为了创建ForkJoinPool对象，用到无参构造函数，所以它将在默认配置下执行。创建的线程池线程数量等于计算机的处理核心数，当ForkJoinPool对象被创建时，这些线程也被创建，并且它们在池中等待直到一些任务进来执行。

因为Task类不返回结果，所以继承RecursiveAction类。在本节中用到推荐的程序结构来实现任务，如果任务需要更新超过10个产品，它将元素集拆分成两部分，创建两个任务，并且每个部分分配一个任务。在Task类中使用first和last属性了解产品列表中此任务需要更新价格的产品位置范围。只能在产品列表的一个副本中使用first和last属性，不能为每个任务创建不同的列表。

为了执行一个任务创建的子任务，这个任务调用invokeAll()方法。这是一个同步调用，在任务继续（也可能结束）执行之前，它等到所有子任务的结束。当任务等待其子任务时，执行任务的工作线程等待另一个任务并且执行。基于这种特性，fork/join框架在任务管理上比Runnable和Callable对象本身更高效。

ForkJoinTask类的invokeAll()方法是执行器与fork/join框架之间一个主要的不同点。在执行器框架中，这种情况下所有的任务都必须发送到执行器，这些任务包括在线程池中执行和控制任务的方法。范例中用到Task类中的invokeAll()方法，Task类继承RecursiveAction类，接着继承ForkJoinTask类。

使用execute()方法发送唯一任务到线程池来更新整个产品列表。这种情况属于异步调用，主线程继续执行。

范例中用到ForkJoinPool类的一些方法来检查在运行任务的状态和进展。ForkJoinPool类还提供跟多此类用途的方法，查看第九章“测试并发应用”中的“监控fork/join池”小节，包括这些方法的完整列表。

最后，与执行器框架相同，必须使用shutdown()方法结束ForkJoinPool。

下图显示本范例在控制台输出的部分执行信息：

![pics/05_01.jpg](pics/05_01.jpg)

可以看到任务结束其工作，且产品价格已更新。

### 扩展学习

ForkJoinPool类还提供其它执行任务的方法，如下所示：

- execute(Runnable task)：这是本范例中execute()方法的另一种形式，这种情况下，发送Runnable任务到ForkJoinPool类。切记ForkJoinPool类不在Runnable对象上使用工作窃取算法，这个算法只用在ForkJoinTask对象上。
- invoke(ForkJoinTask<T> task)：在本范例中学到当execute()方法进行异步调用时，invoke()方法对ForkJoinPool类进行同步调用。此方法调用直到作为参数传递的任务结束执行之后才返回。
- 也可以使用ExecutorService接口中定义的invokeAll()和invokeAny()方法，这些方法接收Callable对象为参数。ForkJoinPool类无法使用Callable对象的工作窃取算法，所以最好使用ThreadPoolExecutor来执行这些对象。

ForkJoinTask类中还包括invokeAll()方法的其它形式，在本范例中也使用到，如下所示：

- invokeAll(ForkJoinTask<?>... tasks) ：此方法接受一个可变的参数列表，可以将尽可能多的ForkJoinTask对象作为参数传递。
- invokeAll(Collection<T> tasks) ：此方法接受一个泛型T对象的集合（例如，ArrayList对象，LinkedList对象，或者TreeSet对象），这个泛型T必须是ForkJoinTask类或其子类。

虽然ForkJoinPool类设计成用来执行ForkJoinTask的对象，但也能直接执行Runnable和Callable对象。也可以使用ForkJoinTask类的adapt()方法来接受Callable对象或者Runnable对象，并且返回ForkJoinTask对象来执行这个任务。

### 更多关注

- 第九章“测试并发应用”中的“监控fork/join池”小节。


##连接任务结果

fork/join框架能够执行返回结果的任务，这种任务由RecursiveTask类实现，此类继承ForkJoinTask类并且实现由执行器框架提供的Future接口。 

在任务中，需要使用Java API文档推荐的程序结构：

```java
if (problem size > size){
    tasks=Divide(task);
    execute(tasks);
    joinResults()
    return result;
} else {
    resolve problem;
    return result;
}
```

如果任务需要解决的问题规模比预先定义的大，则将问题拆分成很多子任务，并且使用fork/join框架执行这些子任务。当完成执行时，初始任务得到所有子任务生成的结果，将它们进行分组，并返回最终结果。最后，当初始任务在线程池中完成执行时，能够高效的得到整个问题的最终结果。

在本节中，将通过开发在文档中检索指定词的应用来学习如何使用fork/join框架来解决这种问题。本范例中需要实现如下两种任务：

- 文件任务，用来在文档的行集合中寻找指定词
- 行任务，用来在文档特定部分中寻找指定词

所有任务将返回在处理的部分文档或行中指定词出现的次数。本节将使用Java并发API提供的默认fork/join池。

### 实现过程

通过如下步骤实现范例：

1. 创建名为DocumentMock的类，生成一个字符串矩阵来模拟文件：

   ```java
   public class DocumentMock {
   ```

2. 用一些词语创建字符串数组，此数组将在字符串矩阵生成中用到：

   ```java
   	private String words[] = {"the", "hello", "goodbye", "packet", "java", "thread", "pool", "random", "class", "main"};
   ```

3. 实现generateDocument()方法，接收三个参数，分别是行数，每行的字数，以及本范例中将要检索的词语，返回一个字符串矩阵：

   ```java
   	public String[][] generateDocument(int numLines, int numWords, String word){
   ```

4. 首先，创建必要的对象来生成文件--String矩阵和Random对象来生成随机数：

   ```java
   		int counter = 0 ;
   		String document[][] = new String[numLines][numWords];
   		Random random = new Random();
   ```

5. 向数组中填充字符串，将给定的词语数组中随机位置上的元素存储到定义的字符串矩阵中，并且在生成的数组中计算检索指定词出现的次数。通过这个结果来检查范例是否运行正常：

   ```java
   		for(int i = 0 ; i < numLines ; i ++){
   			for (int j = 0 ; j < numWords ; j ++) {
   				int index = random.nextInt(words.length);
   				document[i][j] = words[index];
   				if(document[i][j].equals(word)) {
   					counter ++;
   				}
   			}
   		}
   ```

6. 输出检索词出现的次数到控制台，并返回生成的矩阵：

   ```java
   		System.out.printf("DocumentMock : The word --"+ word+" -- appears " + counter + " times in the document\n");
   		return document;
   	}
   ```

7. 创建名为DocumentTask的类，继承Integer类参数化的RecursiveTask类。用来实现在行集合中检索词出现次数的任务：

   ```java
   public class DocumentTask extends RecursiveTask<Integer>{
   ```

8. 定义名为document的私有String矩阵，以及两个名为start和end的私有int属性，定义名为word的私有String属性：

   ```java
   	private String document[][];
   	private int start, end;
   	private String word;
   ```

9. 实现类构造函数，初始化这些属性：

   ```java
   	public DocumentTask(String[][] document, int start, int end, String word) {
   		this.document = document;
   		this.start = start;
   		this.end = end;
   		this.word = word;
   	}
   ```

10. 实现compute()方法，如果属性end和start差小于10，此任务通过调用processLines()方法在这两个属性之间位置的行中计算检索词出现的次数：

    ```java
    	@Override
    	protected Integer compute() {
    		Integer result = null;
    		if(end - start < 10){
    			result = processLines(document, start, end, word);
    ```

11. 否则，将行集合分解成两个对象，创建两个新的DocumentTask对象来处理这两个行集合，并且在线程池中使用invokeAll()方法来执行它们：

    ```java
    		}else {
    			int mid = (start + end) / 2;
    			DocumentTask task1 = new DocumentTask(document, start, mid, word);
    			DocumentTask task2 = new DocumentTask(document, mid, end, word);
    			invokeAll(task1, task2);
    ```

12. 然后，使用groupResults()方法添加两个任务的返回值。最后，返回任务计算出的结果：

    ```java
    		try {
    				result = groupResults(task1.get(), task2.get());
    			} catch (InterruptedException | ExecutionException e) {
    				e.printStackTrace();
    			}
    		}
    		return result;
    	}
    ```

13. 实现processLines()方法，参数包括字符串矩阵，start属性，end属性，以及任务检索词word属性：

    ```java
    	private Integer processLines(String[][] document, int start, int end, String word) {
    ```

14. 对任务需要处理的每一行，创建LineTask对象来处理完整行并且将它们存储到任务列表中：

    ```java
    		List<LineTask> tasks = new ArrayList<LineTask>();
    		for ( int i = start ; i < end ; i ++){
    			LineTask task = new LineTask(document[i], 0, document[i].length, word);
    			tasks.add(task);
    		}
    ```

15. 使用invokeAll()方法执行列表中的所有任务：

    ```java
    		invokeAll(tasks);
    ```

16. 相加所有任务返回的值，并且返回结果：

    ```java
    		int result = 0 ;
    		for(int i = 0 ; i < tasks.size() ; i ++) {
    			LineTask task = tasks.get(i);
    			try {
    				result = result + task.get();
    			} catch (InterruptedException | ExecutionException e) {
    				e.printStackTrace();
    			}
    		}
    		return result;
    	}
    ```

17. 实现groupResults()方法，两个数相加并返回结果：

    ```java
    	private Integer groupResults(Integer number1, Integer number2) {
    		Integer result ; 
    		result = number1 + number2;
    		return result;
    	}
    ```

18. 创建名为LineTask的类，继承Integer类参数化的RecursiveTask类。此类实现在一行中计算检索词出现次数的任务：

    ```java
    public class LineTask extends RecursiveTask<Integer>{
    ```

19. 定义名为line的私有String数组属性，和两个名为start和end的私有int属性。最后，定义名为word的私有String属性：

    ```java
    	private String line[];
    	private int start, end;
    	private String word;
    ```

20. 实现类构造函数，初始化这些属性：

    ```java
    	public LineTask(String[] line, int start, int end, String word) {
    		this.line = line;
    		this.start = start;
    		this.end = end;
    		this.word = word;
    	}
    ```

21. 实现compute()方法，如果属性end和start差小于100，此任务通过调用count()方法在这两个属性之间位置的行中寻找检索词：

    ```java
    	@Override
    	protected Integer compute() {
    		Integer result = null;
    		if(end - start < 100) {
    			result = count(line, start, end, word);
    ```

22. 否则，将行中的词语分成两组，创建两个LineTask对象来处理这两组词语，并且在线程池中使用invokeAll()方法来执行它们：

    ```java
    		}else{
    			int mid = (start + end) / 2;
    			LineTask task1 = new LineTask(line, start, mid, word);
    			LineTask task2 = new LineTask(line, mid, end, word);
    			invokeAll(task1, task2);
    ```

23. 然后，使用groupResults()方法添加两个任务的返回值。最后，返回任务计算出的结果：

    ```java
    		try {
    				result = groupResults(task1.get(), task2.get());
    			} catch (InterruptedException | ExecutionException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
    		return result;
    	}
    ```

24. 实现count()方法，参数包括字符串数组，start属性，end属性，以及任务检索词word属性：

    ```java
    	private Integer count(String[] line, int start, int end, String word) {
    ```

25. 将字符串数组中在start和end属性位置之间存储的词语与检索词word属性比较，如果相等的话，增加counter变量值：

    ```java
            int counter = 0;
            for(int i = start ; i < end ; i ++){
                if(line[i].equals(word)){
                    counter ++;
                }
            }
    ```

26. 设置任务休眠10毫秒来减慢范例执行：

    ```java
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    ```

27. 返回couter变量值：

    ```java
    		return counter;
    	}
    ```

28. 实现groupResults()方法，两个数相加并返回结果：

    ```java
    	private Integer groupResults(Integer number1, Integer number2) {
    		Integer result;
    		result = number1 + number2;
    		return result;
    	}
    ```

29. 实现范例的主方法，创建一个包含main()方法的Main类：

    ```java
    public class Main {
    	public static void main(String[] args) {
    ```

30. 使用DocumentMock类创建100行每行1000词的Document对象：

    ```java
    		DocumentMock mock = new DocumentMock();
    		String word = "java";
    		String[][] document = mock.generateDocument(100, 1000, word);
    ```

31. 创建DocumentTask对象更新整个文件的产品，start参数赋值0，end参数赋值100：

    ```java
    		DocumentTask task = new DocumentTask(document, 0, 100, word);
    ```

32. 使用commonPool()得到默认的ForkJoinPool执行器，然后使用execute()方法在此执行器中运行任务：

    ```java
    		ForkJoinPool commonPool = ForkJoinPool.commonPool();
    		commonPool.execute(task);
    ```

33. 实现展示线程池处理进度信息的代码块，在任务完成执行之前，每隔一秒输出线程池的参数值到控制台：

    ```java
    		do{
    			System.out.printf("************************************\n");
    			System.out.printf("Main : Active Threads : %d\n", commonPool.getActiveThreadCount());
    			System.out.printf("Main : Task Count : %d\n", commonPool.getQueuedTaskCount());
    			System.out.printf("Main : Steal Count : %d\n", commonPool.getStealCount());
    			System.out.printf("************************************\n");
    			
    			try {
    				TimeUnit.SECONDS.sleep(1);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}while (!task.isDone());
    ```

34. 使用shutdown()方法关闭线程池：

    ```java
    		commonPool.shutdown();
    ```

35. 使用awaitTermination()方法等待任务结束：

    ```java
    		try {
    			commonPool.awaitTermination(1, TimeUnit.DAYS);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    ```

36. 输出文件中检索词出现的次数到控制台，检查此数字与通过DocumentMock类写入的数字是否相同：

    ```java
    		try {
    			System.out.printf("Main : The word  --"+ word+" --  appears %d in the document", task.get());
    		} catch (InterruptedException | ExecutionException e) {
    			e.printStackTrace();
    		}
    	}
    ```

### 工作原理

本范例中，实现两个不同的任务：

- DocumentTask：此类的一个任务需要处理文件中由start和end属性确定的行集合。如果行集合长度小于10，则为每行创建LineTask，当所有任务结束执行时，将这些任务的结果相加并返回求和结果。如果任务需要处理的行集长度大于等于10，则拆分成两个集合，并且创建两个DocumentTask对象分别处理这两个新的集合。当这些任务结束执行时，将各自结果相加并返回求和结果。
- LineTask：此类的一个任务需要处理文件中指定行的一组词。如果数量小于100，任务直接检索这组词并返回检索词的出现次数。否则，拆分成两组词并且创建两个LineTask对象分别处理这两个集合。当这些任务结束执行时，将各自结果相加并返回求和结果。

在Main类中，用到默认的ForkJoinPool并且在其中执行DocumentTask类来处理一个包含100行，每行1000个词的文件。此任务将问题拆分成其它的DocumentTask对象和LineTask对象，当所有任务结束执行时，使用初始任务得到整个文件中检索词出现的总次数。由于这些任务返回了结果，所以继承RecursiveTask类。

为了获得通过Task返回的结果，用到了get()方法，此方法在RecursiveTask类实现的Future接口中定义。

当执行本范例时，可以比较输出控制台第一行和最后一行的信息。第一行显示文件生成时计算出的检索词出现次数，最后一行显示通过fork/join任务计算出的相同结果。

### 扩展学习

complete()方法是ForkJoinTask类提供的另一种用来结束任务执行并返回结果的方法。此方法接受一个在RecursiveTask类参数化中用到的类型对象，并且当调用join()方法时，将这个对象作为任务结果返回。建议在异步认为中用此方法提供结果。

因为RecursiveTask类实现了Future接口，所以get()方法还有另一种形式：

- get(long timeout, TimeUnit unit)：在这个方法中，如果任务的结果无效，则等待指定的时间，如果已过指定时间且结果依然无效，此方法返回null值。TimeUnit是一个枚举类型的类，包含如下常量：DAYS、HOURS、MICROSECONDS、MILLISECONDS、MINUTES、NANOSECONDS、和SECONDS。

### 更多关注

- 本章“创建fork/join池”小节

- 第九章“测试并发应用”中的“监控fork/join池”小节。

## 异步运行任务

在ForkJoinPool中可以使用同步或者异步的方式来执行ForkJoinTask。当使用同步方式时，发送任务到线程池中的方法直到此任务发送结束执行时才返回。当使用异步方式时，发送任务到执行器的方法立即返回，所以任务能够继续执行。

你应该意识到这两种方法之间的巨大差异。 当使用同步方法时，调用这些方法之一（例如invokeAll()方法）的任务将暂停直到发送到线程池的任务结束执行，这允许ForkJoinPool类使用工作窃取算法来分配新的任务到执行休眠任务的工作线程。与之相反，当使用异步方法（例如fork()方法）时，任务将继续执行，因此ForkJoinPool类无法使用工作窃取算法来提升应用性能。这种情况下，只有调用join()或者get()方法来等待任务结束，ForkJoinPool类才能使用工作窃取算法。

除了RecursiveAction和RecursiveTask类，Java 8 引入新的支持CountedCompleter类的ForkJoinTask类，在这种任务中，当任务被加载并且没有待定子任务时，能够包含一个完成操作。此机制基于包含在类中的方法（onCompletion()方法）和待定任务的计数器。

计数器初始化默认为零，当需要在原子方式时可以递增它。通常地，当加载一个子任务时，逐次递增计数器。租后，当任务结束执行时，尝试完成任务执行并因此执行onCompletion()方法。如果待定数大于零，计数器加一。如果计数器为零，执行onCompletion()方法，然后完成父任务。

在本节中，学习使用ForkJoinPool和CountedCompleter类提供的异步方法来管理任务，实现用来在指定文件夹及子文件夹中寻找文件的程序。实现的CountedCompleter类用来处理文件夹目录，对文件夹中的每个子文件夹，以异步方式发送一个新的任务到ForkJoinPool类。对文件夹下的每个文件，如果任务继续执行，将检查文件的后缀名并将文件添加到结果列表中。当任务完成时，所有子任务的结果列表将插入到结果任务中。

### 实现过程

通过如下步骤实现范例：

1. 创建名为FolderProcessor的类，继承List<String>参数化的CountedCompleter类：

   ```java
   public class FolderProcessor extends CountedCompleter<List<String>>{
   ```

2. 定义名为path的私有String属性，用来存储准备处理任务的文件夹完整路径：

   ```java
   	private String path;
   ```

3. 定义名为extension的私有String属性，用来存储准备检索任务的文件后缀名：

   ```java
   	private String extension;
   ```

4. 定义两个名为tasks和resultList的私有List属性，第一个用来存储任务加载的所有子任务，第二个用来存储任务的结果列表：

   ```java
   	private List<FolderProcessor> tasks;
   	private List<String> resultList;
   ```

5. 实现类构造函数，初始化属性和父类。因为只在内部使用，所以定义此构造函数为protected类型：

   ```java
   	public FolderProcessor(CountedCompleter<?> completer, String path, String extension) 	 {
   		super(completer);
   		this.path = path;
   		this.extension = extension;
   	}
   ```

6. 实现外部使用的公共构造函数，由于此构造函数创建的任务没有父任务，所以参数中不需要此对象：

   ```java
   	public FolderProcessor(String path, String extension) {
   		this.path = path;
   		this.extension = extension;
   	}
   ```

7. 实现compute()方法，由于CountedCompleter类是任务的基类，所以此方法返回类型是void：

   ```java
   	@Override
   	public void compute() {
   ```

8. 首先，初始化两个列表属性：

   ```java
   		resultList = new ArrayList<>();
   		tasks = new ArrayList<>();
   ```

9. 获得文件夹目录：

   ```java
   		File file = new File(path);
   		File content[] = file.listFiles();
   ```

10. 对文件夹的每个元素，如果有子文件夹，则创建新的FolderProcessor对象，使用fork()方法异步执行此对象。这里用到第一个类构造函数并且将当前任务作为新的完整任务传递，以及使用addToPendingCount()方法增加待定任务计数器的值：

    ```java
    		if (content != null) {
    			for (int i = 0; i < content.length; i++) {
    				if (content[i].isDirectory()) {
    					FolderProcessor task=new FolderProcessor(this, content[i].getAbsolutePath(), extension);
    					task.fork();
    					addToPendingCount(1);
    					tasks.add(task);
    ```

11. 否则，使用checkFile()方法比较文件与检索文件的后缀名，如果相同，存储文件完整路径到先前定义的字符串列表中：

    ```java
    				}else{
    					if (checkFile(content[i].getName())){
    						resultList.add(content[i].getAbsolutePath());
    					}
    				}
    			}
    ```

12. 如果FolderProcessor子任务列表超过50个元素，输出指明此情况的信息到控制台：

    ```java
    			if (tasks.size()>50) {
    				System.out.printf("%s: %d tasks ran.\n",
    				file.getAbsolutePath(),tasks.size());
    			}
    		}
    ```

13. 最后，使用tryComplete()方法尝试完成当前任务：

    ```java
    		tryComplete();
    	}
    ```

14. 实现onCompletion()方法，此方法将在所有子任务（从当前任务分支出的所有任务）已经完成运行时执行。将所有子任务的结果列表添加到当前任务的结果列表中：

    ```java
    	@Override
    	public void onCompletion(CountedCompleter<?> completer) {
    		for (FolderProcessor childTask : tasks) {
    			resultList.addAll(childTask.getResultList());
    		}
    	}
    ```

15. 实现checkFile()方法，此方法比较作为参数传进来的的文件是否与检索文件的后缀名相同，如果是，此方法返回true值，否则返回false值：

    ```java
    	private boolean checkFile(String name) {
    		return name.endsWith(extension);
    	}
    ```

16. 最后，实现getResultList()方法返回任务的结果列表，代码很简单，不在此列出。

17. 实现范例的主方法，创建一个包含main()方法的Main类：

    ```java
    public class Main {
    	public static void main(String[] args) {
    ```

18. 使用默认构造函数创建ForkJoinPool：

    ```java
    		ForkJoinPool pool = new ForkJoinPool();
    ```

19. 创建三个FolderProcessor任务，分别初始化不同的文件夹路径：

    ```java
    		String prefix = "log";
    		FolderProcessor system = new FolderProcessor("C:\\Windows", prefix);
    		FolderProcessor apps = new FolderProcessor("C:\\Program Files", prefix);
    		FolderProcessor documents = new FolderProcessor("C:\\Documents And Settings", prefix);
    ```

20. 使用execute()方法在线程池中执行这三个任务：

    ```java
    		pool.execute(system);
    		pool.execute(apps);
    		pool.execute(documents);
    ```

21. 每隔1秒输出线程池状态信息到控制台，直到这三个任务结束执行：

    ```java
    		do {
    			System.out.printf("******************************************\n");
    			System.out.printf("Main: Active Threads: %d\n",
    			pool.getActiveThreadCount());
    			System.out.printf("Main: Task Count: %d\n",
    			pool.getQueuedTaskCount());
    			System.out.printf("Main: Steal Count: %d\n",
    			pool.getStealCount());
    			System.out.printf("******************************************\n");
    			try {
    				TimeUnit.SECONDS.sleep(1);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		} while ((!system.isDone()) || (!apps.isDone()) || (!documents.isDone()));	
    ```

22. 使用shutdown()方法关闭ForkJoinPool：

    ```java
    		pool.shutdown();
    ```

23. 输出每个任务生成的结果数量到控制台：

    ```java
    		List<String> results;
    		results=system.join();
    		System.out.printf("System: %d files found.\n",results.size());
    		results=apps.join();
    		System.out.printf("Apps: %d files found.\n",results.size());
    		results=documents.join();
    		System.out.printf("Documents: %d files found.\n",
    		results.size());
    	}
    ```

### 工作原理

下图显示本范例在控制台输出的部分执行信息：

![pics/05_02.jpg](pics/05_02.jpg)

FolderProcessor类是此范例的关键之处，每个任务处理一个文件夹的目录，目录包括如下两种元素：

- 文件
- 其它文件夹

如果任务遍历到一个文件夹，则创建另一个FolderProcessor对象来处理这个文件，并且使用fork()方法将此对象发送到线程池中，此方法将任务发送到线程池中，如果池中有空闲的工作线程则执行任务，或者创建一个新的线程。此方法立即返回，所以任务能够继续处理文件夹目录。对每个文件，一个任务用来比较其后缀名和检索文件后缀名，如果相同，将文件名添加到results列表中。

一旦任务处理完指派文件夹下的所有目录，尝试完成当前任务。在本节的介绍中解释到，当我们尝试完成任务时，CountedCompleter源码检索待定任务计数器的值，如果大于0，则减少计数器的值。与之相反，如果值等于0，任务执行onCompletion()方法，然后尝试完成父任务。本范例中，当任务处理文件夹且找到子文件夹时，创建一个新的子任务，使用fork()方法加载此任务，且增加待定任务的计数器值。所以当任务已经处理所有目录时，待定任务的计数器值将与加载的子任务数相同。当调用tryComplete()方法时，如果当前任务的文件夹有子文件夹，这个调用将减少待定任务的计数器值。只有当任务的所有子任务已经完成时，才执行其onCompletion()方法。如果当前任务的文件夹中没有任何子文件夹，待定任务的计数器值将为零，onComplete()方法会被立即调用，然后将尝试完成父任务。通过这种方式，我们创建了一个从头到尾的任务树，这些任务从尾到头完成。在onComplete()方法中，我们处理子任务的所有结果列表，并将这些元素添加到当前任务的结果列表中。

ForkJoinPool类也允许任务以异步方式执行。通过使用execute()方法发送三个初始任务到线程池。在Main类中，使用shutdown()方法结束池操作，且输出正在池中运行的任务状态和进展信息。针对异步方式，ForkJoinPool类还包括很多有用的方法。学习第九章“测试并发应用”中的“监控fork/join池”小节，了解这些方法的完整列表。

### 扩展学习

本范例中用到addToPendingCount()方法增加待定任务的计数器值，我们也可以使用其它方法来改变这个值：

- setPendingCount()：此方法给待定任务计数器赋值。
- compareAndSetPendingCount()：此方法接收两个参数，第一个是预期值，第二个是新的数值。如果待定任务计数器值等于预期值，则将计数器值设置成第二个值。
- decrementPendingCountUnlessZero()：此方法减少待定任务计数器的值，直到等于零。

CountedCompleter类也包含其它方法来管理任务的完成，如下是最重要的两个方法：

- complete()：此方法独立于待定任务计数器的值来执行onCompletion()方法，并且尝试完成其完整（父）任务。
- onExceptionalCompletion()：当completeExceptionally()已经被调用或者compute()方法已经跑出一个Exception时，调用此方法。用处理这种异常的代码来重写此方法。

本范例中，用到join()方法等待任务的结束且获得结果，也可以用如下的get()方法达到这个目的：

- get(long timeout, TimeUnit unit)：在这个方法中，如果任务的结果无效，则等待指定的时间，如果已过指定时间且结果依然无效，此方法返回null值。TimeUnit是一个枚举类型的类，包含如下常量：DAYS、HOURS、MICROSECONDS、MILLISECONDS、MINUTES、NANOSECONDS、和SECONDS。
- join()方法无法被中断，如果中断调用join()方法的线程，此方法会跑出InterruptedException异常。

###更多关注

- 本章“创建fork/join池”小节
- 第九章“测试并发应用”中的“监控fork/join池”小节。

##任务中抛出异常

Java中有两种异常：

- **受检异常：**这些异常必须指定在方法的throws子句中或者内部抓取。例如，IOException或者ClassNotFoundException。
- **非受检异常：**这些异常不需要被指定或抓取，例如NumberFormatException。

在ForkJoinTask类的compute()方法中不能抛出任何受检异常，因为此方法的实现中不包含任何throws定义，需要加入必要的代码来处理受检异常。另一方面，此方法能够抛出（或者通过此方法内部使用的任何方法或对象抛出）非受检异常。ForkJoinTask和ForkJoinPool类的特性与所期待的不同。程序不会结束执行且不在控制台中看到任何异常信息，只是简单的接受好似异常不会被抛出。只有当调用初始任务的get()方法时，异常才会被抛出。当然，也可以使用ForkJoinTask类的一些方法去辨别任务是或否已经抛出异常，如果是，知道抛出什么类型的异常。在本节中，讲学习如何获取这些信息。

### 准备工作

本范例通过Eclipse开发工具实现。如果使用诸如NetBeans的开发工具，打开并创建一个新的Java项目。

### 实现过程

通过如下步骤实现范例：

1. 创建名为Task的类，继承Integer类参数化的RecursiveTask类：

   ```java
   public class Task extends RecursiveTask<Integer> {
   ```

2. 定义名为array的私有int数组，用来模拟范例中将要处理的数据数组：

   ```java
   	private int array[];
   ```

3. 定义两个名为start和end的私有int属性，用来确定任务需要处理的数组元素：

   ```java
   	private int start, end;
   ```

4. 实现类构造函数，初始化这些属性：

   ```java
   	public Task(int[] array, int start, int end) {
   		this.array = array;
   		this.start = start;
   		this.end = end;
   	}
   ```

5. 实现任务的compute()方法，因为已经使用Integer类初始化RecursiveTask类，此方法返回Integer对象。首先，输出start和end属性值的信息到控制台：

   ```java
   	@Override
   	protected Integer compute() {
   		System.out.printf("Task: Start from %d to %d\n",start,end);
   ```

6. 如果任务需要处理的元素集合，由start和end属性确定的范围小于10，则判断数组中第四个元素（索引数是3）是否在集合中。如果是，抛出RuntimeException。然后，设置任务休眠一秒钟：

   ```java
   		if (end-start<10) {
   			if ((3>start)&&(3<end)){
   				throw new RuntimeException("This task throws an"+"Exception: Task from "+start+" to "+end);
   			}
   			try {
   				TimeUnit.SECONDS.sleep(1);
   			} catch (InterruptedException e) {
   				e.printStackTrace();
   			}
   ```

7. 否则（任务需要处理的元素集合数量大于等于10），将元素集合拆分成两块，创建两个Task对象来处理它们，并且在线程池中使用invokeAll()方法执行。然后输出这些任务的结果到控制台：

   ```java
   		} else {
   			int mid=(end+start)/2;
   			Task task1=new Task(array,start,mid);
   			Task task2=new Task(array,mid,end);
   			invokeAll(task1, task2);
   			System.out.printf("Task: Result form %d to %d: %d\n",start,mid,task1.join());
   			System.out.printf("Task: Result form %d to %d: %d\n",mid,end,task2.join());
   		}	
   ```

8. 输出指明任务结束的信息到控制台，包括start和end属性值：

   ```java
   		System.out.printf("Task: End form %d to %d\n",start,end);
   ```

9. 返回数值0作为任务结果：

   ```java
   		return 0;
   	}
   ```

10. 实现范例的主方法，创建一个包含main()方法的Main类：

    ```java
    public class Main {
    	public static void main(String[] args) {
    ```

11. 创建100个整型数组：

    ```java
    		int array[] = new int[100];
    ```

12. 创建Task对象处理这个数组：

    ```java
    		Task task = new Task(array, 0, 100);
    ```

13. 使用默认构造函数创建ForkJoinPool对象：

    ```java
    		ForkJoinPool pool = new ForkJoinPool();
    ```

14. 在线程池中使用execute()方法执行任务：

    ```java
    		pool.execute(task);
    ```

15. 使用shutdown()方法关闭ForkJoinPool类：

    ```java
    		pool.shutdown();
    ```

16. 使用awaitTermination()方法等待任务结束。因为需要足够长的时间等待任务结束，将数值1和TimeUnit.DAYS作为参数传给此方法：

    ```java
    		try{
    			pool.awaitTermination(1, TimeUnit.DAYS);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    ```

17. 使用isCompletedAbnormally()判断任务或者其子任务是否已经抛出异常。如果是，输出抛出的异常信息到控制台。使用ForkJoinTask类的getException()方法得到这个异常：

    ```java
    		if (task.isCompletedAbnormally()) {
    			System.out.printf("Main: An exception has ocurred\n");
    			System.out.printf("Main: %s\n",task.getException());
    		}
    		System.out.printf("Main: Result: %d",task.join());
    	}
    ```

### 工作原理

本节中实现的Task类处理数字数组。任务判断其处理的数字集合数量是否大于等于10，如果是，拆分成两部分，并且创建两个新的Task对象分别处理它们。否则寻找数组中的第四个元素（索引数是3），如果此元素在任务处理的集合中，抛出RuntimeException异常。

当执行范例时抛出了异常，但程序不会停止。在Main类中，包含使用初始任务调用ForkJoinTask类的isCompletedAbnormally()方法。如果任务或其子任务已经抛出异常，方法返回true。还用到了同一个对象的getException()方法得到任务抛出的Exception对象。

当在任务中抛出非受检异常时，也会影响到父任务（发送到ForkJoinPool类的任务）以及父任务的父任务，以此类推。如果查看范例的全部输出，就会发现缺失一些任务结束的输出信息。如下所示这些任务的开始信息：

```sql
    Task: Start from 0 to 100
    Task: Start from 0 to 50
    Task: Start from 50 to 100
    Task: Start from 0 to 25
    Task: Start from 50 to 75
    Task: Start from 25 to 50
    Task: Start from 62 to 75
```

这些是抛出异常的任务及其父任务，都已经非正常结束。考虑到在开发过程中，如果不想要这种特性的话，使用能够抛出异常的ForkJoinPool和ForkJoinTask对象。

下图显示本范例在控制台输出的部分执行信息：

![pics/05_03.jpg](pics/05_03.jpg)

### 扩展学习

本范例中，用到join()方法等待任务结束，且得到任务结果。也可以使用如下两种形式的get()方法达到此目的：

- get()：如果ForkJoinTask已经结束执行，此方法返回compute()方法返回的值，或者等待直到任务结束。
- get(long timeout, TimeUnit unit)：在这个方法中，如果任务的结果无效，则等待指定的时间，如果已过指定时间且结果依然无效，此方法返回null值。TimeUnit是一个枚举类型的类，包含如下常量：DAYS、HOURS、MICROSECONDS、MILLISECONDS、MINUTES、NANOSECONDS、和SECONDS。

get()和join()方法有两个主要的不同点：

- join()方法不能被中断，如果中断调用join()方法的线程，此方法抛出InterruptedException异常。
- 如果任务抛出任何非受检异常，get()方法将返回ExecutionException异常，然而join()方法将返回RuntimeException异常。

如果本范例中使用ForkJoinTask类的completeExceptionally()方法代替抛出异常，能够得到相同的结果。代码如下所示：

```java
	Exception e=new Exception("This task throws an Exception: "+
"Task from "+start+" to "+end);
	completeExceptionally(e);
```

###更多关注

- 本章“创建fork/join池”小节

## 取消任务

当在ForkJoinPool类中执行ForkJoinTask对象时，使用ForkJoinTask类提供的的cancel()方法，在对象开始执行之前取消。当取消任务时，需要关注如下两点：

- ForkJoinPool类不提供任何方法来取消在线程池中运行或等待的所有任务。
- 当取消一个任务时，不能取消这个任务已经执行的任务。

本节中，通过范例实现ForkJoinTask对象的取消。遍历数字数组，找到指定数字的第一个任务将取消剩余任务。由于fork/join框架不提供这种功能，需要实现辅助类来处理取消操作。

### 准备工作

本范例通过Eclipse开发工具实现。如果使用诸如NetBeans的开发工具，打开并创建一个新的Java项目。

### 实现过程

通过如下步骤实现范例：

1. 创建名为ArrayGenerator的类，用来生成指定长度的随机整数数组。实现名为generateArray() 的方法，生成数字数组，接收参数为数组长度：

   ```java
   public class ArrayGenerator {
   	public int[] generateArray(int size) {
   		int array[] = new int[size];
   		Random random = new Random();
   		for(int i = 0 ; i < size ; i ++) {
   			array[i] = random.nextInt(10);
   		}
   		return array;
   	}
   }
   ```

2. 创建名为TaskManager的类，用来存储在本范例ForkJoinPool中执行的所有任务。由于ForkJoinPool和ForkJoinTask类的局限性，将要使用此类取消ForkJoinPool类的所有任务：

   ```java
   public class TaskManager {
   ```

3. 使用参数化的ForkJoinTask类和Integer类定义名为tasks的对象列表：

   ```java
   	private final ConcurrentLinkedDeque<SearchNumberTask> tasks;
   ```

4. 实现类构造函数，初始化任务列表：

   ```java
   	public TaskManager(){
   		tasks=new ConcurrentLinkedDeque<>();
   	}
   ```

5. 实现addTask()方法，添加ForkJoinTask对象到任务列表中：

   ```java
   	public void addTask(SearchNumberTask task){
   		tasks.add(task);
   	}
   ```

6. 实现cancelTasks()方法，使用cancel()方法取消存储在列表中的所有ForkJoinTask对象。此方法接收ForkJoinTask对象作为参数来计划取消其它任务：

   ```java
   	public void cancelTasks(SearchNumberTask cancelTask){
   		for (SearchNumberTask task :tasks) {
   			if (task!=cancelTask) {
   				task.cancel(true);
   				task.logCancelMessage();
   			}
   		}
   	}
   ```

7. 实现SearchNumberTask类，继承Integer类参数化的RecursiveTask类。此类在整型数组的一部分元素中检索数字：

   ```java
   public class SearchNumberTask extends RecursiveTask<Integer> {
   ```

8. 定义名为numbers的私有整型数字数组：

   ```java
   	private int numbers[];
   ```

9. 定义两个名为start和end的私有整型属性，确定任务需要处理的数组元素集：

   ```java
   	private int start, end;
   ```

10. 定义名为number的私有整型属性，存储将要检索的数字：

    ```java
    	private int number;
    ```

11. 定义名为manager的私有TaskManager属性，使用此对象来取消所有任务：

    ```java
    	private TaskManager manager;
    ```

12. 定义初始值为-1的私有整型常量，当任务没有找到检索数字时，返回此常量：

    ```java
    	private final static int NOT_FOUND=-1;
    ```

13. 实现类构造函数，初始化属性：

    ```java
    	public SearchNumberTask(int numbers[], int start, int end, int number, TaskManager manager){
    			this.numbers=numbers;
    			this.start=start;
    			this.end=end;
    			this.number=number;
    			this.manager=manager;
    	}
    ```

14. 实现compute()方法，首先输出指明start和end属性值的信息到控制台：

    ```java
    	@Override
    	protected Integer compute() {
    		System.out.println("Task: "+start+":"+end);
    ```

15. 如果start和end属性相差大于10（任务需要处理超过10个数组中的元素），调用launchTasks()方法将任务工作拆分成两个子任务：

    ```java
    		int ret;
    		if (end-start>10) {
    			ret=launchTasks();
    ```

16. 否则，在任务需要处理的数组块中检索指定数字，调用lookForNumber()方法：

    ```java
    		} else {
    			ret=lookForNumber();
    		}
    ```

17. 返回任务结果：

    ```java
    		return ret;
    	}
    ```

18. 实现lookForNumber()方法：

    ```java
    	private int lookForNumber() {
    ```

19. 遍历任务需要处理的所有元素，将元素中存储的数字与检索数字比较，如果相等，输出相应信息到控制台，然后使用TaskManager对象的cancelTasks()方法取消所有任务，并且返回找到的元素在数组中的位置：

    ```java
    		for (int i=start; i<end; i++){
    			if (numbers[i]==number) {
    				System.out.printf("Task: Number %d found in position %d\n",number,i);
    				manager.cancelTasks(this);
    				return i;
    			}
    ```

20. 在循环中，设置任务休眠一秒钟：

    ```java
    			try {
    				TimeUnit.SECONDS.sleep(1);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    ```

21. 最后，返回-1：

    ```java
    		return NOT_FOUND;
    	}
    ```

22. 实现launchTasks()方法。首先，将任务需要处理的数字集合分成两部分，创建两个Task任务分别处理：

    ```java
    	private int launchTasks() {
    		int mid=(start+end)/2;
    		SearchNumberTask task1=new SearchNumberTask(numbers,start,mid,number,manager);
    		SearchNumberTask task2=new SearchNumberTask(numbers,mid,end,number,manager);
    ```

23. 添加任务到TaskManage对象中：

    ```java
    		manager.addTask(task1);
    		manager.addTask(task2);
    ```

24. 使用fork()方法异步执行这两个任务：

    ```java
    		task1.fork();
    		task2.fork();
    ```

25. 等待任务结束，如果第一个任务结果不等于-1则返回第一个任务结果，否则返回第二个任务结果：

    ```java
    		int returnValue;
    		returnValue=task1.join();
    		if (returnValue!=-1) {
    			return returnValue;
    		}
    		returnValue=task2.join();
    		return returnValue;
    	}
    ```

26. 实现writeCancelMessage()方法，当任务被取消时输出信息到控制台：

    ```java
    	public void logCancelMessage(){
    		System.out.printf("Task: Canceled task from %d to %d\n",start,end);
    	}
    ```

27. 实现范例的主方法，创建一个包含main()方法的Main类：

    ```java
    public class Main {
    	public static void main(String[] args) {
    ```

28. 使用ArrayGenerator创建包含1000个数字的数组：

    ```java
    		ArrayGenerator generator=new ArrayGenerator();
    		int array[]=generator.generateArray(1000);
    ```

29. 创建TaskManager对象：

    ```java
    		TaskManager manager=new TaskManager();
    ```

30. 使用默认构造函数创建ForkJoinPool对象：

    ```java
    		ForkJoinPool pool=new ForkJoinPool();
    ```

31. 创建Task对象来出来之前生成的数组：

    ```java
    		SearchNumberTask task=new SearchNumberTask (array,0,1000, 5,manager);
    ```

32. 使用execute()方法在线程池中异步处理任务：

    ```java
    		pool.execute(task);
    ```

33. 使用shutdown()方法关闭线程池：

    ```java
    		pool.shutdown();
    ```

34. 使用ForkJoinPool类的awaitTermination()方法等待任务结束：

    ```java
    		try {
    			pool.awaitTermination(1, TimeUnit.DAYS);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    ```

35. 输出指明程序结束的信息到控制台：

    ```java
    		System.out.printf("Main: The program has finished\n");
    	}
    ```

### 工作原理

如果任务还没有被执行的话，ForkJoinTask类提供cancel()方法来取消此任务。这一点很重要，如果任务已经开始执行，调用cancel()方法则无效。此方法接收名为mayInterruptIfRunning的布尔值作为参数。通过名称能够看出，如果此方法传true值，任务即便正在运行也被取消。Java API文档中指出，在ForkJoinTask类的默认实现中，此属性无效。任务只有在还未开始执行的情况下能被取消，取消操作对已取消任务发送到线程池的任务无效，它们将继续执行。

fork/join框架的局限性是不允许在ForkJoinPool中的所有任务取消，为了解决这个问题，需要实现TaskManager类，存储已经被送到线程池中的所有任务，类中有取消已经存储的所有任务的方法。如果任务因为正在运行或者已经完成而无法取消，cancel()方法返回false值，所以可以尝试取消所有任务，不用担心可能出现的副作用。

本范例中，实现了在数字数组中寻找数字的任务，用fork/join框架推荐的方式将问题拆分成子任务。只需要检索数字出现一次，所以当找到时，就取消其它任务。

下图显示本范例在控制台输出的部分执行信息：

![pics/05_04.jpg](pics/05_04.jpg)

### 更多关注

- 本章“创建fork/join池”小节