package com.example.shoppingmall_comp.domain.items.service;

import com.example.shoppingmall_comp.domain.items.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {
    CreateItemResponse create(ItemRequest itemRequest, List<MultipartFile> multipartFiles, User user);
    UpdateItemResponse update(Long itemId, UpdateItemRequest itemRequest, List<MultipartFile> multipartFiles, User user);
    void delete(Long itemId, User user);
    SellerItemsResponse getSellerAll(Pageable pageable, User user);
    ItemResponse getOne(Long itemId);
    ItemPageResponse getAll(Pageable pageable, Long categoryId);
}
