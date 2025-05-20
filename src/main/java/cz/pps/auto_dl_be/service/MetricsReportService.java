package cz.pps.auto_dl_be.service;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.search.Search;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MetricsReportService {

    private static final Logger logger = LoggerFactory.getLogger(MetricsReportService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final MeterRegistry meterRegistry;
    private final ProductService productService;
    
    /**
     * Generates a daily report of product metrics at 6:00 AM every day.
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void generateDailyReport() {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            
            // Get product metrics
            double productsCreated = getCounterValue("product.created.count");
            double productsUpdated = getCounterValue("product.updated.count");
            int dailyCreated = getGaugeValue("product.daily.created");
            int dailyUpdated = getGaugeValue("product.daily.updated");
            int totalProducts = productService.getProductCount();
            
            // Get error metrics
            double productErrors = getCounterValue("product.error.count");
            
            // Log the daily report
            StringBuilder report = new StringBuilder();
            report.append("\n======== DAILY PRODUCT METRICS REPORT: ").append(yesterday.format(DATE_FORMATTER)).append(" ========\n");
            report.append("Total products in database: ").append(totalProducts).append("\n");
            report.append("Products created yesterday: ").append(dailyCreated).append("\n");
            report.append("Products updated yesterday: ").append(dailyUpdated).append("\n");
            report.append("Total products created since start: ").append(formatMetricValue(productsCreated)).append("\n");
            report.append("Total products updated since start: ").append(formatMetricValue(productsUpdated)).append("\n");
            report.append("Total product operation errors: ").append(formatMetricValue(productErrors)).append("\n");
            report.append("======== END OF REPORT ========");
            
            logger.info(report.toString());
            
            // Reset daily counters
            productService.resetDailyCounters();
            
        } catch (Exception e) {
            logger.error("Error generating daily metrics report: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Gets the value of a counter metric, or 0 if not found.
     */
    private double getCounterValue(String metricName) {
        return Search.in(meterRegistry).name(metricName).counter()
               .map(Counter::count)
               .orElse(0.0);
    }
    
    /**
     * Gets the value of a gauge metric, or 0 if not found.
     */
    private int getGaugeValue(String metricName) {
        return Search.in(meterRegistry).name(metricName).gauge()
               .map(gauge -> gauge.value())
               .map(Double::intValue)
               .orElse(0);
    }
    
    private String formatMetricValue(double value) {
        return String.format("%.0f", value);
    }
}