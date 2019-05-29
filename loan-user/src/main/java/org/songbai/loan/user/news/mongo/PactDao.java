package org.songbai.loan.user.news.mongo;

import org.songbai.loan.model.news.PactModel;

public interface PactDao {
    PactModel getPactInfoById(String pactId);
}
