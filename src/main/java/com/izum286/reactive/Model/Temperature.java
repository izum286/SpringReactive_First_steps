package com.izum286.reactive.Model;

import lombok.*;


@NoArgsConstructor
@Getter
@Setter
@ToString
public class Temperature {
    private  double temperature;

    public Temperature(double temperature) {
        this.temperature = temperature;
    }
}
