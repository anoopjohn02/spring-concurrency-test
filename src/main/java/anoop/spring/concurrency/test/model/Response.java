package anoop.spring.concurrency.test.model;

import java.util.List;

public record Response<T> (
        T result,
        String timeInString,
        boolean parallel
){}
