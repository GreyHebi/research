package ru.hebi.research.mapping;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.IntStream;

@BenchmarkMode({Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3, time = 5)
@Measurement(iterations = 8, time = 10)
public class MapBenchmark {

    @Param({"10", "100", "1000", "10000", "1000000"})
    private int count;

    private record First(String a, int b){}
    private record Second(long c, String d){}

    private Function<First, Second> mapping;
    private List<First> data;
    private List<Second> result;

    @Setup
    public void setup() {
        data = IntStream.range(0, count)
                .mapToObj(i -> new First("abc", i))
                .toList();
        mapping = first -> new Second(first.b(), "fi " + first.a());
    }

    @Benchmark
    public void stream() {
        result = data.stream()
                .map(mapping)
                .toList();
    }

    @Benchmark
    public void forEach() {
        List<Second> r = new ArrayList<>(data.size());

        for (First in : data) {
            r.add(mapping.apply(in));
        }
        result = r;
    }

    @Benchmark
    public void iterator_toArray() {
        final int size = data.size();
        final Second[] r = new Second[size];

        final Iterator<First> iterator = data.iterator();
        int cursor = 0;
        while (iterator.hasNext()) {
            r[cursor++] = mapping.apply(iterator.next());
        }
        result = List.of(r);
    }

    @Benchmark
    public void iterator_toList() {
        List<Second> r = new ArrayList<>(data.size());
        final Iterator<First> iterator = data.iterator();

        while (iterator.hasNext()) {
            r.add(mapping.apply(iterator.next()));
        }
        result = r;
    }

}
