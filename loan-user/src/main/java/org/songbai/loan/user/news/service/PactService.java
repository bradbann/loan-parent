package org.songbai.loan.user.news.service;

import org.songbai.loan.model.news.PactModel;
import org.springframework.stereotype.Component;

@Component
public interface PactService {
    PactModel getPactInfoById(String pactId);
}
