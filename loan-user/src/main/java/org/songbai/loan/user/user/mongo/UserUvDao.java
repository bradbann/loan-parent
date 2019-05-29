package org.songbai.loan.user.user.mongo;

import org.songbai.loan.model.user.UserUvModel;

public interface UserUvDao {
    void saveUv(UserUvModel userUvModel);
}
