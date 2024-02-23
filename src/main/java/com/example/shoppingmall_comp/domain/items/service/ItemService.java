package com.example.shoppingmall_comp.domain.items.service;

import com.example.shoppingmall_comp.domain.items.dto.ItemRequest;
import com.example.shoppingmall_comp.domain.items.dto.ItemResponse;
import com.example.shoppingmall_comp.domain.items.dto.SellerItemsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ItemService {
    ItemResponse create(ItemRequest itemRequest, List<MultipartFile> multipartFiles, User user);
    ItemResponse update(ItemRequest itemRequest, List<MultipartFile> multipartFiles, User user);
    void delete(Long itemId, User user);
    List<SellerItemsResponse> getSellerAll(Pageable pageable, User user);

}
