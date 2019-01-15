package com.dongqilin.api;

/**
 * @description:
 * @author: dongql
 * @date: 2017/10/24 13:34
 */
public interface IUserProvider {
    void findRedisLock();
    void findZkLock();
}
