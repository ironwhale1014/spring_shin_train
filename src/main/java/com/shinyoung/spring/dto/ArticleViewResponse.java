package com.shinyoung.spring.dto;

import com.shinyoung.spring.domain.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ArticleViewResponse {


    private Long id;
    private String title;
    private String content;
    private LocalDateTime createAt;

    public ArticleViewResponse(Article article) {
        this.id = article.getId();
        this.title = article.getTitle();
        this.content = article.getContent();
        this.createAt = article.getCreateAt();
    }
}



