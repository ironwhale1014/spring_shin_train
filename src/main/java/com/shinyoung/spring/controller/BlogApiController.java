package com.shinyoung.spring.controller;

import com.shinyoung.spring.domain.Article;
import com.shinyoung.spring.dto.AddArticleRequest;
import com.shinyoung.spring.dto.ArticleResponse;
import com.shinyoung.spring.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BlogApiController {

    private final BlogService blogService;

    @PostMapping("api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request) {
        Article savedArticle = blogService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }

    @GetMapping("api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles() {
        List<ArticleResponse> articles = blogService.findAll().stream().map(ArticleResponse::new).toList();
        return ResponseEntity.ok().body(articles);
    }
}
