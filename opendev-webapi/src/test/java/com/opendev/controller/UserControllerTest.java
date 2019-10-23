package com.opendev.controller;


import com.opendev.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Deque;
import java.util.LinkedList;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserControllerTest {

    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void test() {
        Deque<String> deque = new LinkedList<String>();
        for (int i = 0; i < 10; i++) {
            deque.push("" + i);
        }
        log.info(deque.getFirst());
        log.info(deque.getLast());
        log.info(deque.removeFirst());
        log.info(deque.getLast());
    }

    @Test
    public void test1() {

        redisUtil.setex("haoaaaa","shuai",60);
    }

}
