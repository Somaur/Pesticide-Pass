package com.example.pesticide_pass.data;

import java.io.Serializable;

/**
 * 用于标记采样点的类
 */
public class ImageTag implements Serializable {
    public final static class Dot extends ImageTag {
        public Dot(int x, int y) {
            super();
            this.x = x;
            this.y = y;
        }
        public int x, y;
    }
    // TODO: 计划中有 单点 涂抹 选区 三种Tag
}