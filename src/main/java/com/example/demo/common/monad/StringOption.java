package com.example.demo.common.monad;

/**
 * Created by alen on 17-9-27.
 */
class StringOption extends Option<String> {

    public StringOption(String object) {
        super(object);
    }

    @Override
    public boolean isNothing() {
        return this.object == null || "".equals(this.object);
    }
}
