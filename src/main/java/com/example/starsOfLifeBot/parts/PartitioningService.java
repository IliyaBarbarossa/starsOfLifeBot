package com.example.starsOfLifeBot.parts;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartitioningService {

    private final JdbcTemplate jdbcTemplate;

    public PartitioningService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String createPartitionForDay(LocalDate td) {
        LocalDate now = td;
        LocalDate tomorrow = now.plusDays(1);
        String tableName = "prognoz";
        String partName = getTableWithSuffix(tableName, now);

        String sql = "CREATE TABLE IF NOT EXISTS " + partName +
                " PARTITION OF " + tableName +
                " FOR VALUES FROM ('" + getSuffix(now) +
                "') TO (' " + getSuffix(tomorrow) +
                "');";

        return sql;
    }

    public void removePartitionBeforeDay(LocalDate td) {
        LocalDate now = td;
        String tableName = "prognoz";
        String partName = getTableWithSuffix(tableName, now);

        String getAllPartitions = "SELECT * FROM pg_partition_tree('first.prognoz')";
        List<String> query = jdbcTemplate.query(getAllPartitions, (rs, num) -> rs.getString(1));
        String sql = query.stream()
//                .filter("first.prognoz"::equals)
                .filter(s -> s.contains("_"))
                .map(s -> s.split("_", 2)[1])
                .map(s -> LocalDate.of(Integer.parseInt(s.substring(0, 4)),
                        Integer.parseInt(s.substring(4, 6)),
                        Integer.parseInt(s.substring(6, 8))))
                .filter(d -> d.isBefore(now))
                .map(d -> getTableWithSuffix(tableName, d))
                .map(s -> "DROP TABLE IF EXISTS " + s + ";")
                .collect(Collectors.joining("\n"));


        jdbcTemplate.execute(sql);
    }

    public void createAllPartitions() {
        LocalDate now = LocalDate.now();
        LocalDate tomorrow = now.plusDays(1);
        String partitionNow = createPartitionForDay(now);
        execute(partitionNow);

        String partitionTomorrow = createPartitionForDay(tomorrow);
        execute(partitionTomorrow);
    }


    @EventListener(ApplicationReadyEvent.class)
    public void combine() {
        removePartitionBeforeDay(LocalDate.now());
        createAllPartitions();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void schedule() {
        combine();

    }


    public void execute(String sql) {
        jdbcTemplate.execute(sql);
    }


    private String getTableWithSuffix(String tableName, LocalDate dt) {
        return tableName + "_" + getSuffix(dt);
    }

    private String getSuffix(LocalDate dt) {
        String year = String.valueOf(dt.getYear());
        String month = dt.getMonthValue() < 10 ? "0" + dt.getMonthValue() : String.valueOf(dt.getMonthValue());
        String day = dt.getDayOfMonth() < 10 ? "0" + dt.getDayOfMonth() : String.valueOf(dt.getDayOfMonth());

        return year + month + day;
    }


}
