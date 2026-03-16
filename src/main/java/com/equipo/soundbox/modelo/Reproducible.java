package com.equipo.soundbox.modelo;

public interface Reproducible {
    void reproducir();      // método que cada álbum implementará a su manera
    int getDuracionTotal(); // duración total en segundos
}

