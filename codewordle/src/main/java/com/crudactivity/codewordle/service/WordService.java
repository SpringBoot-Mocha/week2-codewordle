package com.crudactivity.codewordle.service;

import com.crudactivity.codewordle.repository.WordRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
@Service
public class WordService {






        private final WordRepository wordRepository;

        public WordService(WordRepository wordRepository) {
            this.wordRepository = wordRepository;
        }

        // Devuelve una palabra al azar según el tema
        public Optional<String> obtenerPalabraPorTema(String topic) {
            return wordRepository.findRandomWord(topic);
        }


}
