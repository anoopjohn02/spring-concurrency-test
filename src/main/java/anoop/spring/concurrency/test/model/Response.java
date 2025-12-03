package anoop.spring.concurrency.test.model;

import java.util.List;

public record Response<T> (
        List<T> result,
        String timeInString
){}
