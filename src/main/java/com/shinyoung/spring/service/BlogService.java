package com.shinyoung.spring.service;

import com.shinyoung.spring.domain.Article;
import com.shinyoung.spring.dto.AddArticleRequest;
import com.shinyoung.spring.dto.UpdateArticleRequest;
import com.shinyoung.spring.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request, String userName) {
        return blogRepository.save(request.toEntity(userName));
    }

    public List<Article> findAll() {
        return blogRepository.findAll();
    }

    public Article findById(Long id) {
        return blogRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("not found: " + id)
        );
    }

    public void delete(long id) {
        Article article = blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        blogRepository.deleteById(id);
    }


    @Transactional
    public Article update(long id, UpdateArticleRequest request) {
        Article article = blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found" + id));


        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());

        return article;
    }

    private static void authorizeArticleAuthor(Article article) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!article.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("not authorized");
        }
    }

}
