package com.IndiExport.backend.dto;

import java.util.UUID;

public class CategoryDto {

    public static class CategoryResponse {
        private UUID id;
        private String name;
        private String slug;

        public CategoryResponse() {}

        public CategoryResponse(UUID id, String name, String slug) {
            this.id = id;
            this.name = name;
            this.slug = slug;
        }

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
    }
}
