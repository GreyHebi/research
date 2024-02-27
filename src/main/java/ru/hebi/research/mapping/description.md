# Оптимальное преобразование (маппинг)

**Задача:** поиск оптимального способа преобразования неизвестной коллекции, будь-то множество (_Set_), список (_List_)
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
result = List.of(r);
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

|                     | 10            | 100           | 1 000          | 10 000          | 1 000 000              |
|---------------------|---------------|---------------|----------------|-----------------|------------------------|
| stream              | 0,222 ± 0,011 | 1,830 ± 0,030 | 17,338 ± 0,577 | 167,762 ± 3,864 | 76 732,611 ± 1 973,248 |
| forEach             | 0,164 ± 0,006 | 1,522 ± 0,023 | 14,691 ± 0,169 | 151,930 ± 2,827 | 69 077,307 ± 1 757,279 |
| iterator (to Array) | 0,176 ± 0,003 | 1,564 ± 0,028 | 15,197 ± 0,106 | 157,680 ± 2,764 | 81 658,413 ± 2 675,659 |
| iterator (to List)  | 0,145 ± 0,003 | 1,464 ± 0,014 | 13,782 ± 0,203 | 144,082 ± 1,887 | 67 914,718 ± 3 000,870 |



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

iterator
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