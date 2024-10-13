package com.intuit.inventory.management.utils;

import java.util.*;
import java.util.concurrent.TimeUnit;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MetricsUtil {

    @Autowired
    private MeterRegistry meterRegistry;

    private static final Logger logger = LoggerFactory.getLogger(MetricsUtil.class);

    public enum CustomTimerMetric {
        GET_ALL_PRODUCTS("get_all_products"),
        GET_ALL_PRODUCTS_PAGED("get_all_products_paged"),
        SAVE_PRODUCT("save_product"),
        UPDATE_PRODUCT_QUANTITY("update_product_quantity"),
        DELETE_A_PRODUCT("delete_a_product"),
        DELETE_PRODUCT_FROM_SHELF_BY_VENDOR("delete_product_from_shelf_by_vendor");

        private final String timeMetricName;

        CustomTimerMetric(String metricName) {
            this.timeMetricName = metricName;
        }

        public String value() {
            return this.timeMetricName;
        }

        @Override
        public String toString() {
            return this.timeMetricName;
        }

    }

    private static Map<CustomTimerMetric, String> endPointMap = ImmutableMap.<CustomTimerMetric, String>builder()
            .put(CustomTimerMetric.GET_ALL_PRODUCTS,"/list")
            .put(CustomTimerMetric.GET_ALL_PRODUCTS_PAGED, "/list/page")
            .put(CustomTimerMetric.SAVE_PRODUCT, "/save")
            .put(CustomTimerMetric.UPDATE_PRODUCT_QUANTITY, "/updateQuantity")
            .put(CustomTimerMetric.DELETE_A_PRODUCT, "/deleteProduct")
            .put(CustomTimerMetric.DELETE_PRODUCT_FROM_SHELF_BY_VENDOR, "/deleteProduct/shelf/vendor")
            .build();

    public MetricsUtil() {}

    public void recordTimer(CustomTimerMetric timerMetric, long duration) {
        Timer timer = Timer.builder("custom_time_recorder")
                .tags("endpoint", endPointMap.get(timerMetric))
                .description("Time required for the api")
                .register(meterRegistry);

        timer.record(duration, TimeUnit.NANOSECONDS);

    }

    public void recordCounter(CustomTimerMetric timerMetric) {
        Counter counter = Counter.builder("custom_count_recorder")
                .tags("endpoint", endPointMap.get(timerMetric))
                .description("Number of times api was called")
                .register(meterRegistry);

        logger.info("Counter metrics triggered!");
        logger.info(counter.toString());

        counter.increment();
    }

}
