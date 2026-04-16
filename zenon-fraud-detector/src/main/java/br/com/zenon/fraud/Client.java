package br.com.zenon.fraud;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class Client {
    String name;
    BigDecimal oldBalance;
    BigDecimal newBalance;
}
