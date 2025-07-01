package org.example.api_classification_vehicle.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageResponse<T> {
    private List<T> content;
    private PageMetadata page;

    // Constructeur
    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = new PageMetadata(
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber()
        );
    }

    // Getters (obligatoires pour la sérialisation JSON)
    public List<T> getContent() {
        return content;
    }

    public PageMetadata getPage() {
        return page;
    }

    // Classe interne pour les métadonnées
    public static class PageMetadata {
        private int size;
        private long totalElements;
        private int totalPages;
        private int number;

        public PageMetadata(int size, long totalElements, int totalPages, int number) {
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
            this.number = number;
        }

        // Getters
        public int getSize() { return size; }
        public long getTotalElements() { return totalElements; }
        public int getTotalPages() { return totalPages; }
        public int getNumber() { return number; }
    }
}