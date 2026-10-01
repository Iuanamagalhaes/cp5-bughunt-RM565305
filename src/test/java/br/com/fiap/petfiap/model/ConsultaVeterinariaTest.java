package br.com.fiap.petfiap.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

// Testes unitarios do model: sem banco, sem Spring (Aula 15).
public class ConsultaVeterinariaTest {

    private ConsultaVeterinaria consultaDaMimi() {
        return new ConsultaVeterinaria(1, "Mimi", "MEDIO", "Bruno", LocalDateTime.of(2026, 10, 1, 14, 0));
    }

    @Test
    public void deveAcumular50PontosDeFidelidade() {
        // Act
        int pontos = consultaDaMimi().calcularPontosFidelidade();

        // Assert
        assertEquals(50, pontos);
    }

    @Test
    public void deveDurar30Minutos() {
        // Act
        int duracao = consultaDaMimi().getDuracaoMinutos();

        // Assert
        assertEquals(30, duracao);
    }
    @Test
    public void deveCustar150ReaisIndependenteDoPorte() {
        // Arrange
        ConsultaVeterinaria pequena = new ConsultaVeterinaria(1, "Maria Fifi", "PEQUENO", "Maria Luisa",
                LocalDateTime.of(2026, 10, 1, 14, 0));
        ConsultaVeterinaria grande = new ConsultaVeterinaria(2, "Princeso", "GRANDE", "Gustavo",
                LocalDateTime.of(2026, 10, 1, 15, 0));

        // Act
        double precoPequena = pequena.calcularPreco();
        double precoGrande = grande.calcularPreco();

        // Assert: o porte NAO muda o preco da consulta
        assertEquals(150.0, precoPequena, 0.001);
        assertEquals(150.0, precoGrande, 0.001);
    }
}
