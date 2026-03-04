package com.betting.service;

import com.betting.entity.User;

public interface IUserService {

    User findByCode(String code);
}
