[remark]:<class>(center, middle)
# Java Conncurency
## Executors a Future

[remark]:<slide>(new)
## ExecutorService
Concurrency API zavádí koncept `ExecutorService` pro vyšší úrovň pro práce s vlákny. 

Exekutoři jsou schopni spouštět asynchronní úlohy a spravat `Thread pool`
- vytvářejí nová vlákna,
- přiřazují vlákna úlohám 

Umožňují optimalizovat množství souběžných úkolů v v průběhu celého životního cyklu naší aplikace

[remark]:<slide>(wait)
*Tak vypadá první příklad vlákna jako použití exekutorů:*

```java
ExecutorService executor = Executors.newSingleThreadExecutor();
executor.submit(() -> {
    String threadName = Thread.currentThread().getName();
    System.out.println("Hello " + threadName);
});
```

```
Hello pool-1-thread-1
```

[remark]:<slide>(new)
## Vytváření pomocí třídy `Executors`
Třída `Executors` poskytuje tovární metody pro vytváření různých implementaci `ExecutorService`.
- **`newCachedThreadPool()`** : Vytvoří fond podprocesů, který podle potřeby vytvoří nové podprocesy, ale znovu použije dříve vytvořené podprocesy, pokud jsou k dispozici.
- **`newFixedThreadPool(int nThreads)`**: Vytvoří fond podprocesů, který opětovně používá pevný počet podprocesů, které fungují ze sdílené fronty.
- **`newScheduledThreadPool(int corePoolSize)`**: Vytvoří fond podprocesů, který může naplánovat spouštění příkazů po daném zpoždění nebo pravidelně provádět.
- **`newSingleThreadExecutor()`**: Vytvoří Executor, který používá jediný pracovní podproces.
- **`newSingleThreadScheduledExecutor()`**: Vytvoří jedno-vláknový exekutor, který může naplánovat spouštění příkazů po daném zpoždění nebo pravidelně provádět.
- **`newWorkStealingPool()`**: Vytváří executor, který plně využije výkon procesoru.

[remark]:<slide>(new)
## Ukončení `ExecutorService`

Narozdíl od `Threads` executortoři neustále očekávají nové úkoly.

**Exekutoři musí být výslovně zastavení**

Služba `ExecutorService` poskytuje pro tento účel dvě metody: 
- **`shutdown()`**: čeká na dokončení aktuálně spuštěných úloh, 
- **`shutdownNow`**: ukončí všechny spuštěné úlohy a okamžitě zavře executora.

```java
try {
    System.out.println("attempt to shutdown executor");
    executor.shutdown();
    executor.awaitTermination(5, TimeUnit.SECONDS);
}
catch (InterruptedException e) {
    System.err.println("tasks interrupted");
}
finally {
    if (!executor.isTerminated()) {
        System.err.println("cancel non-finished tasks");
    }
    executor.shutdownNow();
    System.out.println("shutdown finished");
}
```

### Notes
Executor se tiše zastaví tím, že čeká určitý čas na ukončení aktuálně spuštěných úloh. 
Po uplynutí maximálně pěti vteřin se příkazce konečně vypne přerušením všech běžících úloh.

[remark]:<slide>(new)
## Callables
Kromě `Runnable` exekutorů podporuje jiný druh úkolu s názvem `Callable`. 

Callables jsou funkční rozhraní stejně jako runnables, ale místo `void` vrátí hodnotu.

*Příklad Lambda výrazu, který vrací celé číslo po spánku na jednu sekundu:*

```java
Callable<Integer> task = () -> {
    try {
        TimeUnit.SECONDS.sleep(1);
        return 123;
    }
    catch (InterruptedException e) {
        throw new IllegalStateException("task interrupted", e);
    }
};
```
[remark]:<slide>(new)
### Odeslání úlohy
Callables mohou být předány `ExecutorService` stejně jako runnables.
 
Metoda `submit()` nečeká až do dokončení úkolu, nemůže být vracen výsledek. 

Namísto toho exekutor vrátí speciální výsledek typu `Future`, který může být použit k získání aktuálního výsledku později.

```java
ExecutorService executor = Executors.newFixedThreadPool(1);
Future<Integer> future = executor.submit(task);

System.out.println("future done? " + future.isDone());

Integer result = future.get();

System.out.println("future done? " + future.isDone());
System.out.print("result: " + result);
```

[remark]:<slide>(new)
### Získání výsledku
Po odeslání spoplatnění exekutorovi nejprve zkontrolujeme, zda je budoucnost dokončena metodou `isDone()`. 

V našme příkladu vrací `false`, jelikož task trvá jednu vteřinu, než vrátí celé číslo.

Volání metody `get()` zablokuje aktuální vlákno a čeká, dokud není výsledk:

```
future done? false
future done? true
result: 123
```

[remark]:<slide>(wait)
Futures jsou úzce spojeny s podkladovou exekuční službou. 
Každá neukončená `Future` vyhodí výjimku, pokud vypnete exekutora:

```java
executor.shutdownNow();
future.get();
```

[remark]:<slide>(new)
### Získání výsledku s parametrem `timeout`
Jakýkoli hovor do future.get () bude zablokován a počká, dokud nebude ukončeno podkladové vypovězení. V nejhorším případě je spuštěn po uplynutí výpovědní doby navždy - čímž se vaše aplikace nereaguje. Tyto scénáře můžete jednoduše vyvážit tím, že předáte časový limit:

```java
ExecutorService executor = Executors.newFixedThreadPool(1);

Future<Integer> future = executor.submit(() -> {
    try {
        TimeUnit.SECONDS.sleep(2);
        return 123;
    }
    catch (InterruptedException e) {
        throw new IllegalStateException("task interrupted", e);
    }
});

future.get(1, TimeUnit.SECONDS);
```

*Výsledná výjimka:*
```
Exception in thread "main" java.util.concurrent.TimeoutException
    at java.util.concurrent.FutureTask.get(FutureTask.java:205)
```

#### Notes
Možná jste již uhodli, proč je tato výjimka hodena: 
Zadali jsme maximální čekací dobu jedné vteřiny, ale splatná kalkulačka skutečně potřebuje dvě sekundy před vrácením výsledku.

[remark]:<slide>(new)
### Metoda `InvokeAll`
Exekutoři podporují dávkové odeslání více volacího kanálu najednou pomocí `invokeAll()`. 

Tato metoda přijímá kolekci `Callable` a vrátí `List<Futures>`.

```java
ExecutorService executor = Executors.newWorkStealingPool();

List<Callable<String>> callables = Arrays.asList(
        () -> "task1",
        () -> "task2",
        () -> "task3");

executor.invokeAll(callables)
    .stream()
    .map(future -> {
        try {
            return future.get();
        }
        catch (Exception e) {
            throw new IllegalStateException(e);
        }
    })
    .forEach(System.out::println);
```

### Notes
V tomto příkladu využíváme lambda stream z Java 8, abychom zpracovali všechny `Futures` vrácené vyvoláním metody `invokeAll()`. 
Nejprve mapujeme každou `Future` na její návratovou hodnotu a potom je včechny vypíčeme na konzoli. 
 
[remark]:<slide>(new)
### InvokeAny
Dalším způsobem dávkového odesílání volajících je metoda `invokeAny()`

Namísto vrácení `Future` tato metoda vrací výsledek první ukončené úlohy.

*Pro testování tohoto chování používáme tuto pomocnou metodu k simulaci volajících s různou dobou trvání. Metoda vrátí vytočení, které spí na určitou dobu, dokud nevrátí daný výsledek:*

```java
Callable<String> callable(String result, long sleepSeconds) {
    return () -> {
        TimeUnit.SECONDS.sleep(sleepSeconds);
        return result;
    };
}
```

[remark]:<slide>(new)
### InvokeAny

Tuto metodu používáme k vytvoření skupiny `Callable` s různou délkou trvání.
 
Odeslání této skupiny pomocí `invokeAny()` zíkáme výsledek nejreji zpracované metody:

```java
ExecutorService executor = Executors.newWorkStealingPool();

List<Callable<String>> callables = Arrays.asList(
    callable("task1", 2),
    callable("task2", 1),
    callable("task3", 3));

String result = executor.invokeAny(callables);
System.out.println(result);
```

*Výstup na konzoli:*
```
    task2
```

### Notes
Výše uvedený příklad používá jiný typ spouštěče vytvořeného pomocí `newWorkStealingPool()`. 
Tato tovární metoda je součástí Java 8 a vrací exekutorem typu `ForkJoinPool`, který pracuje poněkud odlišně od běžných executorů. 
Namísto použití fixní velikosti vlákna `ForkJoinPools` jsou vytvořeny podle typu CPU.

[remark]:<slide>(new)
## Scheduled Executors
`ScheduledExecutorService` je určen k:
- pravidelnému spoučtění úloh
- jednorázovému spuštění naplánovat úlohy

*Tento vzor kódu naplánuje úlohu, která má běžet po uplynutí počáteční prodlevy tří sekund:*

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());
ScheduledFuture<?> future = executor.schedule(task, 3, TimeUnit.SECONDS);

TimeUnit.MILLISECONDS.sleep(1337);

long remainingDelay = future.getDelay(TimeUnit.MILLISECONDS);
System.out.printf("Remaining Delay: %sms", remainingDelay);
```

[remark]:<slide>(new)
## Scheduled Executors: výsledky
Naplánování úkolu vracejí výsledek prostřednictvím `ScheduledFuture`.

- poskytuje metodu `getDelay()` pro získání zbývajícího zpoždění. 
- Po uplynutí této prodlevy bude úkol spuštěn.

Za účelem naplánování pravidelných úkolů provádějí exekutoři dvě metody:
1. **`scheduleAtFixedRate()`**
2. **`scheduleWithFixedDelay()`**

[remark]:<slide>(new)
### Scheduled Executors: `scheduleAtFixedRate()`
Pouští úlohy s periodicky, například jednou za sekundu:

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());

int initialDelay = 0;
int period = 1;
executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
```

Dále tato metoda akceptuje počáteční zpoždění, které popisuje počáteční čekací dobu před tím, než bude úkol poprvé spuštěn.

Metoda `scheduleAtFixedRate()` **nezohledňuje** skutečné trvání úkolu. 

Pokud tedy zadáte dobu jedné vteřiny, ale úloha potřebuje 2 sekundy k provedení, pak thread pool rychle vyčerpáme.

[remark]:<slide>(new)
### Scheduled Executors: `scheduleWithFixedDelay()`
Metoda `scheduleWithFixedDelay()` funguje stejně jako výše popsaný protějšek. 

Rozdíl spočívá v tom, že dodržuje dobu čekaní mezi koncem úlohy a začátkem dalšího úkolu. 

```java
ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

Runnable task = () -> {
    try {
        TimeUnit.SECONDS.sleep(2);
        System.out.println("Scheduling: " + System.nanoTime());
    }
    catch (InterruptedException e) {
        System.err.println("task interrupted");
    }
};

executor.scheduleWithFixedDelay(task, 0, 1, TimeUnit.SECONDS);
```

#### Notes
Tento příklad naplánuje úlohu s pevným zpožděním jedné sekundy mezi koncem spuštění a začátkem dalšího spuštění. 
Počáteční zpoždění je nula a trvání úkolů je dvě sekundy. Takže skončíme s prováděním intervalu 0s, 3s, 6s, 9s a tak dále. 
Jak můžete vidět `scheduleWithFixedDelay()` je užitečné, pokud nemůžete předvídat trvání naplánovaných úloh.
