package ru.hebi.research;

import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Runner {
    public static void main(String[] args) throws Exception {
        final String directory = "target/jmh-report/";
        Files.createDirectories(Paths.get(directory));

        final Options options = new OptionsBuilder()
                //разогрев
                .warmupIterations(5)
                .warmupTime(TimeValue.seconds(5))
                //замеры
                .measurementIterations(10)
                .measurementTime(TimeValue.seconds(10))
                .forks(10)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                //отчет
                .resultFormat(ResultFormatType.JSON)
                .result(directory + "report.json")
                .build();
        new org.openjdk.jmh.runner.Runner(options).run();
    }
}
