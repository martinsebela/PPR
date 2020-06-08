# Parallel programming (PPR)

This project is about how we have solved Levenshtein distance algorithm in serial and parallel way.
We used ExecutorService and AtomicInteger classes for parallel version.

## Levenshtein distance algorithm

```java
private static int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    public static int levenshtein(String a, String b) {
        int aLen = a.length();
        int bLen = b.length();
        if (Math.min(aLen, bLen) == 0) {
            return Math.max(aLen, bLen);
        }

        int k = 1;
        if (a.charAt(0) == b.charAt(0)) {
            k = 0;
        }

        return minimum(levenshtein(a.substring(1), b) + 1,
                levenshtein(a, b.substring(1)) + 1,
                levenshtein(a.substring(1), b.substring(1)) + k);
    }
```

## Atomic integer
It used to synchronize and store value (the shortest distance between 2 strings).
```java
private static final AtomicInteger minValue = new AtomicInteger(Integer.MAX_VALUE);
```

## Executor Service

```java
ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUMBER);

words.forEach(
    item -> {
        executor.submit(() -> {
            int c = Algorithm.levenshtein(item.getFirst(), item.getSecond());
            if (c < minValue.get()) {
                minValue.set(c);
            }
        });
     });
```

Calc.java and CalcParallel.java shows how we have solved this problem.
