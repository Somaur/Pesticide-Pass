package com.example.pesticide_pass.data;

/**
 * 用于标记采样点的类
 */
public class ImageTag {
    public final static class Dot extends ImageTag {
        public Dot(int x, int y) {
            super();
            this.x = x;
            this.y = y;
        }
        public int x, y;
    }
}