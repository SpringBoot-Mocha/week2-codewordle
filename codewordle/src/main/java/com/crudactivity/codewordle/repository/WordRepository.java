package com.crudactivity.codewordle.repository;

import java.util.List;
import java.util.Optional;

public interface WordRepository {
    public List<String> findByTopic(String topic);
    public Optional<String> findRandomWord(String topic);

}
