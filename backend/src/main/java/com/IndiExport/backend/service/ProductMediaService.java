package com.IndiExport.backend.service;

import com.IndiExport.backend.dto.ProductDto;
import com.IndiExport.backend.entity.Product;
import com.IndiExport.backend.entity.ProductMedia;
import com.IndiExport.backend.entity.User;
import com.IndiExport.backend.exception.ResourceNotFoundException;
import com.IndiExport.backend.exception.ProductExceptions;
import com.IndiExport.backend.repository.ProductMediaRepository;
import com.IndiExport.backend.repository.ProductRepository;
import com.IndiExport.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductMediaService {

    private final ProductMediaRepository productMediaRepository;
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    private final UserRepository userRepository;

    @Transactional
    public List<ProductDto.ProductMediaResponse> uploadMedia(UUID userId, UUID productId, List<MultipartFile> files) throws IOException {
        Product product = productRepository.findByIdAndDeletedAtIsNull(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId.toString()));

        if (!product.getSeller().getUser().getId().equals(userId)) {
            throw new ProductExceptions.UnauthorizedProductAccessException("You can only upload media to your own products");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));
        List<ProductMedia> mediaList = new ArrayList<>();
        
        // Get existing max order
        int currentMaxOrder = productMediaRepository.findByProductIdOrderByDisplayOrderAsc(productId)
                .stream()
                .mapToInt(ProductMedia::getDisplayOrder)
                .max()
                .orElse(-1);

        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            ProductMedia.MediaType type = ProductMedia.MediaType.IMAGE;
            String folder = "products/" + productId + "/images";

            if (contentType != null && contentType.startsWith("video/")) {
                type = ProductMedia.MediaType.VIDEO;
                folder = "products/" + productId + "/videos";
                fileStorageService.validateFile(file, new String[]{"video/mp4", "video/mpeg", "video/quicktime"}, 20); // 20MB for video
            } else {
                fileStorageService.validateFile(file, new String[]{"image/png", "image/jpeg", "image/webp"}, 5); // 5MB for images
            }

            String url = fileStorageService.uploadFile(file, folder);

            ProductMedia media = ProductMedia.builder()
                    .product(product)
                    .mediaUrl(url)
                    .mediaType(type)
                    .displayOrder(++currentMaxOrder)
                    .uploadedBy(user)
                    .build();
            
            mediaList.add(productMediaRepository.save(media));
        }

        return mediaList.stream()
                .map(m -> ProductDto.ProductMediaResponse.builder()
                        .id(m.getId())
                        .url(m.getMediaUrl())
                        .type(m.getMediaType())
                        .displayOrder(m.getDisplayOrder())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteMedia(UUID userId, UUID mediaId) {
        ProductMedia media = productMediaRepository.findById(mediaId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductMedia", mediaId.toString()));

        if (!media.getProduct().getSeller().getUser().getId().equals(userId)) {
            throw new ProductExceptions.UnauthorizedProductAccessException("You can only delete media from your own products");
        }

        productMediaRepository.delete(media);
    }
}
