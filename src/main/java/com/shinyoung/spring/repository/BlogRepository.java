package com.shinyoung.spring.repository;

import com.shinyoung.spring.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article,Long> {
}
