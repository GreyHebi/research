package ru.hebi.research.mapping;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
public class MapBenchmark {

    @Param({"10", "100", "1000", "10000", "1000000"})
    private int count;

    private record First(String a, int b){}
    private record Second(long c, String d){}

    private Function<First, Second> mapping;
    private List<First> data;

    @Setup
    public void setup() {
        data = IntStream.range(0, count)
                .mapToObj(i -> new First("abc", i))
                .toList();
        mapping = first -> new Second(first.b(), "fi " + first.a());
    }

    @Benchmark
    public void stream(Blackhole bh) {
        bh.consume(
                data.stream()
                        .map(mapping)
                        .toList()
        );
    }

    @Benchmark
    public void forEach(Blackhole bh) {
        final List<Second> r = new ArrayList<>(data.size());

        for (First in : data) {
            r.add(mapping.apply(in));
        }
        bh.consume(r);
    }

    @Benchmark
    public void iterator_toArray(Blackhole bh) {
        final int size = data.size();
        final Second[] r = new Second[size];

        final Iterator<First> iterator = data.iterator();
        int cursor = 0;
        while (iterator.hasNext()) {
            r[cursor++] = mapping.apply(iterator.next());
        }
        bh.consume(Arrays.asList(r));
    }

    @Benchmark
    public void iterator_toList(Blackhole bh) {
        final List<Second> r = new ArrayList<>(data.size());
        final Iterator<First> iterator = data.iterator();

        while (iterator.hasNext()) {
            r.add(mapping.apply(iterator.next()));
        }
        bh.consume(r);
    }

}
