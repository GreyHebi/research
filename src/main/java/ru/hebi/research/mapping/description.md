# Оптимальное преобразование (маппинг)

**Задача:** поиск оптимального способа преобразования неизвестной коллекции(`Collection<>`), будь-то множество (`Set<>`), список (`List<>`)
или др.

Рассмотрены 3 варианта:

1. Stream
```java
List<Second> result = data.stream()
    .map(mapping)
    .toList();
```

2. forEach
```java
List<Second> r = new ArrayList<>(data.size());
for (First first : data) {
    r.add(mapping.apply(first));
}
```

3. a. Iterator (в массив)
```java
int size = data.size();
Second[] r = new Second[size];

Iterator<First> iterator = data.iterator();
int cursor = 0;
while (iterator.hasNext()) {
        r[cursor++] = mapping.apply(iterator.next());
}
result = Arrays.asList(r);
```

3. b. Iterator (в список)
```java
List<Second> r = new ArrayList<>(data.size());
Iterator<First> iterator = data.iterator();
while (iterator.hasNext()) {
    r.add(mapping.apply(iterator.next()));
}
result = r;
```

Функция маппинг:
```java
mapping = first -> new Second(first.b(), "fi " + first.a());
```


Результат (в микросекундах)

|                     | 10            | 100           | 1 000          | 10 000          | 1 000 000            |
|---------------------|---------------|---------------|----------------|-----------------|----------------------|
| stream              | 0,143 ± 0,001 | 1,230 ± 0,019 | 10,902 ± 0,063 | 105,544 ± 0,570 | 47 945,815 ± 485,833 |
| forEach             | 0,118 ± 0,001 | 1,139 ± 0,020 | 10,993 ± 0,090 | 108,612 ± 0,389 | 47 674,533 ± 608,435 |
| iterator (to Array) | 0,115 ± 0,001 | 1,090 ± 0,009 | 10,393 ± 0,053 | 105,154 ± 0,374 | 48 073,174 ± 539,313 |
| iterator (to List)  | 0,115 ± 0,001 | 1,162 ± 0,015 | 10,997 ± 0,120 | 111,942 ± 1,261 | 47 837,873 ± 507,598 |



Почему же разница между `forEach` и `iterator (to List)` по времени есть, хотя они почти не отличаются?
Ответ (наверно) в байт-коде:

forEach
```text
L2
FRAME APPEND [java/util/List java/util/Iterator]
 ALOAD 2
 INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
 IFEQ L3
 ALOAD 2
 INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
 CHECKCAST ru/hebi/research/mapping/MapBenchmark$First
 ASTORE 3
L4
 LINENUMBER 49 L4
 ALOAD 1
 ALOAD 0
 GETFIELD ru/hebi/research/mapping/MapBenchmark.mapping : Ljava/util/function/Function;
 ALOAD 3
 INVOKEINTERFACE java/util/function/Function.apply (Ljava/lang/Object;)Ljava/lang/Object; (itf)
 CHECKCAST ru/hebi/research/mapping/MapBenchmark$Second
 INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z (itf)
 POP
L5
 LINENUMBER 50 L5
 GOTO L2
```

iterator (to List)
```text
L2
 LINENUMBER 72 L2
FRAME APPEND [java/util/List java/util/Iterator]
 ALOAD 2
 INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
 IFEQ L3
L4
 LINENUMBER 73 L4
 ALOAD 1
 ALOAD 0
 GETFIELD ru/hebi/research/mapping/MapBenchmark.mapping : Ljava/util/function/Function;
 ALOAD 2
 INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
 CHECKCAST ru/hebi/research/mapping/MapBenchmark$First
 INVOKEINTERFACE java/util/function/Function.apply (Ljava/lang/Object;)Ljava/lang/Object; (itf)
 CHECKCAST ru/hebi/research/mapping/MapBenchmark$Second
 INVOKEINTERFACE java/util/List.add (Ljava/lang/Object;)Z (itf)
 POP
 GOTO L2
```