package com.licenta.bustravel.stubs;

import com.licenta.bustravel.model.UserEntity;

public class UserEntityStub extends UserEntity {
    private boolean valid;

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @Override
    public boolean isValid(String phone, String email) {
        return valid;
    }
}
