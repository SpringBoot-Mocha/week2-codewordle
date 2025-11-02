package com.riwi.codewordle.service;

import com.riwi.codewordle.domain.entities.Theme;
import com.riwi.codewordle.repository.ThemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ThemeService {

    private final ThemeRepository themeRepository;

    /**
     * Get a specific theme by ID
     * @param id Theme ID
     * @return The found theme
     */
    public Optional<Theme> getThemeById(Long id) {
        return themeRepository.findById(id);
    }

    /**
     * Get a theme by name
     * @param name Theme name
     * @return The found theme
     */
    public Optional<Theme> getThemeByName(String name) {
        return themeRepository.findByName(name);
    }

    /**
     * Get all themes
     * @return List of all themes
     */
    public List<Theme> getAllThemes() {
        return themeRepository.findAll();
    }

    /**
     * Save a new theme
     * @param theme The theme to save
     * @return The saved theme
     */
    public Theme saveTheme(Theme theme) {
        return themeRepository.save(theme);
    }

    /**
     * Delete a theme
     * @param id ID of the theme to delete
     */
    public void deleteTheme(Long id) {
        themeRepository.delete(id);
    }

    /**
     * Check if a theme exists
     * @param id Theme ID
     * @return true if exists, false otherwise
     */
    public boolean themeExists(Long id) {
        return themeRepository.findById(id).isPresent();
    }
}
