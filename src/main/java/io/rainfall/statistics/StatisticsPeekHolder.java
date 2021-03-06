package io.rainfall.statistics;

import io.rainfall.statistics.collector.StatisticsCollector;
import io.rainfall.statistics.exporter.Exporter;
import jsr166e.LongAdder;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Aurelien Broszniowski
 */
public class StatisticsPeekHolder<E extends Enum<E>> {
  public final static String ALL = "ALL";
  private final Enum<E>[] resultsReported;
  private final ConcurrentHashMap<String, LongAdder> assertionsErrors;

  private Map<String, StatisticsPeek<E>> statisticsPeeks = new ConcurrentHashMap<String, StatisticsPeek<E>>();
  private Map<String, Exporter> extraCollectedStatistics = new ConcurrentHashMap<String, Exporter>();
  private StatisticsPeek<E> totalStatisticsPeeks = null;
  private long timestamp;

  public StatisticsPeekHolder(final Enum<E>[] resultsReported, final Map<String, Statistics<E>> statisticsMap,
                              final Set<StatisticsCollector> statisticsCollectors,
                              final ConcurrentHashMap<String, LongAdder> assertionsErrors) {
    this.resultsReported = resultsReported;
    this.assertionsErrors = assertionsErrors;
    this.timestamp = System.currentTimeMillis();
    for (String name : statisticsMap.keySet()) {
      statisticsPeeks.put(name, statisticsMap.get(name).peek(timestamp));
    }
    this.totalStatisticsPeeks = new StatisticsPeek<E>(ALL, this.resultsReported, this.timestamp);
    totalStatisticsPeeks.addAll(statisticsPeeks);

    for (StatisticsCollector statisticsCollector : statisticsCollectors) {
      extraCollectedStatistics.put(statisticsCollector.getName(), statisticsCollector.peek());
    }
  }

  public StatisticsPeek<E> getStatisticsPeeks(String name) {
    return statisticsPeeks.get(name);
  }

  public Long getAssertionsErrorsCount(String name) {return assertionsErrors.get(name).longValue();}

  public Set<String> getStatisticsPeeksNames() {
    return statisticsPeeks.keySet();
  }

  public StatisticsPeek<E> getTotalStatisticsPeeks() {
    return totalStatisticsPeeks;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public Enum<E>[] getResultsReported() {
    return resultsReported;
  }

  public Map<String, Exporter> getExtraCollectedStatistics() {
    return extraCollectedStatistics;
  }

  public Long getTotalAssertionsErrorsCount() {
    Long totalAssertionsErrorsCount = 0L;
    for (LongAdder count : assertionsErrors.values()) {
      totalAssertionsErrorsCount += count.longValue();
    }
    return totalAssertionsErrorsCount;
  }
}
