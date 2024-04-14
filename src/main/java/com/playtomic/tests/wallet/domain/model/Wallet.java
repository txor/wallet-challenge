package com.playtomic.tests.wallet.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Wallet implements Persistable<String> {

    @Id
    private String id;
    private Double balance;

    @Override
    public boolean isNew() {
        return true;
    }
}
