package com.springbatchprocessing.tasklet;

import lombok.NonNull;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataCleansingTasklet implements Tasklet {

    private final JdbcTemplate jdbcTemplate;

    public DataCleansingTasklet(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RepeatStatus execute(@NonNull StepContribution contribution, @NonNull ChunkContext chunkContext) {
        String deleteDuplicateSql = """
                WITH CTE AS (
                        SELECT id, email,
                        ROW_NUMBER() OVER (PARTITION BY id ORDER BY id) AS row_num
                        FROM customer
                         )
                        DELETE FROM customer
                        WHERE (id, email) IN (
                        SELECT id, email
                        FROM CTE
                        WHERE row_num > 1
                    );
                """;
        jdbcTemplate.update(deleteDuplicateSql);
        return RepeatStatus.FINISHED;
    }

}
