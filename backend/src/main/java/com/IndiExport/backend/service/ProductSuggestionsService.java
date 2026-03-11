package com.IndiExport.backend.service;

import com.IndiExport.backend.repository.ProductSuggestionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductSuggestionsService {

    private final ProductSuggestionsRepository productSuggestionsRepository;

    @Transactional(readOnly = true)
    public List<String> getSuggestions(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return new ArrayList<>();
        }
        return productSuggestionsRepository.findSuggestions(keyword, PageRequest.of(0, 10));
    }
}
