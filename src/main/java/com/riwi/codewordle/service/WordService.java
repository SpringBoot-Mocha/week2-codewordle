package com.riwi.codewordle.service;

import com.riwi.codewordle.domain.entities.Word;
import com.riwi.codewordle.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordService {

    private final WordRepository wordRepository;

    /**
     * Get a random word from a specific theme
     * @param themeId Theme ID
     * @return A random word from the theme
     */
    public Optional<Word> getRandomWordByTheme(Long themeId) {
        return wordRepository.findRandomWordByThemeId(themeId);
    }

    /**
     * Get all words from a theme
     * @param themeId Theme ID
     * @return List of words from the theme
     */
    public List<Word> getWordsByTheme(Long themeId) {
        return wordRepository.findByThemeId(themeId);
    }

    /**
     * Get a specific word
     * @param id Word ID
     * @return The found word
     */
    public Optional<Word> getWordById(Long id) {
        return wordRepository.findById(id);
    }

    /**
     * Get all words
     * @return List of all words
     */
    public List<Word> getAllWords() {
        return wordRepository.findAll();
    }

    /**
     * Save a new word
     * @param word The word to save
     * @return The saved word
     */
    public Word saveWord(Word word) {
        return wordRepository.save(word);
    }

    /**
     * Delete a word
     * @param id ID of the word to delete
     */
    public void deleteWord(Long id) {
        wordRepository.delete(id);
    }
}
