package org.songbai.loan.user.news.service;

import org.springframework.web.multipart.MultipartFile;

public interface FeedBackService {

    public void commitBack(String content, MultipartFile[] files, Integer vestId);
}
