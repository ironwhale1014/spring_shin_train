package com.shinyoung.spring.dto;

import com.shinyoung.spring.domain.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddArticleRequest {

    private String title;
    private String content;

    public Article toEntity(String author) {
        return Article.builder().title(title).content(content).author(author).build();
    }
}
