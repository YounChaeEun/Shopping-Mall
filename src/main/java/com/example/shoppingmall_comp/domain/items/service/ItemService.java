package com.example.shoppingmall_comp.domain.items.service;

import com.example.shoppingmall_comp.domain.items.dto.ItemPageResponse;
import com.example.shoppingmall_comp.domain.items.dto.ItemRequest;
import com.example.shoppingmall_comp.domain.items.dto.ItemResponse;
import com.example.shoppingmall_comp.domain.items.dto.SellerItemsResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {
    ItemResponse create(ItemRequest itemRequest, List<MultipartFile> multipartFiles, User user);
    List<String> update(Long itemId, ItemRequest itemRequest, List<MultipartFile> multipartFiles, User user);
    void delete(Long itemId, User user);
    List<SellerItemsResponse> getSellerAll(Pageable pageable, User user);
    ItemResponse getOne(Long itemId);
    ItemPageResponse getAll(Pageable pageable, Long categoryId);
}
