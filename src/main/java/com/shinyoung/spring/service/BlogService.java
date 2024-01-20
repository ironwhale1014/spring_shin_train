package com.shinyoung.spring.service;

import com.shinyoung.spring.domain.Article;
import com.shinyoung.spring.dto.AddArticleRequest;
import com.shinyoung.spring.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request) {
        return blogRepository.save(request.toEntity());
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }
}
