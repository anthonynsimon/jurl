package com.anthonynsimon.url;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

@Fork(1)
@Warmup(iterations = 3, time = 3000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 3000, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
public class BenchmarkJavaURLClass {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        String[] urls = new String[] {
                "https://github.com/anthonynsimon/jurl",
                "/video.download.akamai.com/2d2c1/Something_Something_(112344_ISMUSP)_v3.ism/QualityLevels(940000)/Fragments(video_eng=5880000000)"
        };
    }

    @Benchmark
    public void benchmarkParser(BenchmarkState state, Blackhole bh) throws MalformedURLException {
        for(int i = 0; i < state.urls.length; i++) {
            URL url = new URL(state.urls[i]);
            bh.consume(url);
        }
    }
}

