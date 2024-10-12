//package com.intuit.inventory.management.utils;
//
//import java.util.*;
//import io.micrometer.core.instrument.Tag;
//import io.micrometer.core.instrument.Timer;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.ImmutableSet;
//
//public class MetricsUtil {
//    public enum CustomTimerMetric {
//        PERSISTENCE_EVENT_FAILED_TIMES("event_time_to_fail"),
//        PERSISTENCE_ORPHAN_CHECK_TIMES("orphancheck_time"),
//        PERSISTENCE_EVENT_TIMES("event_time_to_process");
//
//        private final String timeMetricName;
//
//        CustomTimerMetric(String metricName) {
//            this.timeMetricName = metricName;
//        }
//
//        public String value() {
//            return this.timeMetricName;
//        }
//
//        @Override
//        public String toString() {
//            return this.timeMetricName;
//        }
//
//    }
//
//    private static Map<CustomTimerMetric, Set<String>> metricTimerMap = ImmutableMap.<CustomTimerMetric, Set<String>>builder()
//            .put(CustomTimerMetric.PERSISTENCE_EVENT_FAILED_TIMES,ImmutableSet.of("type"))
//            .put(CustomTimerMetric.PERSISTENCE_ORPHAN_CHECK_TIMES, ImmutableSet.of("type"))
//            .put(CustomTimerMetric.PERSISTENCE_EVENT_TIMES, ImmutableSet.of("type"))
//            .build();
//
//    public MetricsUtil() {}
//
//    public static void recordTimer(CustomTimerMetric timerMetric, Map<String, String> tagValues, long duration) {
//        try {
//            List<Tag> tags = new ArrayList<>();
//            Set<String> allowedStatsForCurrentMetric = metricTimerMap.get(timerMetric);
//            allowedStatsForCurrentMetric.forEach(key -> tags.add(Tag.of(key, tagValues.get(key))));
//            Timer.builder(timerMetric.value()).tags(tags)
//                    .publishPercentiles(0.95)
//                    .register(ApplicationConfiguration.registry).record(duration,TimeUnit.MILLISECONDS);
//        }catch(Exception e) {
//            // do nothing
//        }
//
//    }
//}
