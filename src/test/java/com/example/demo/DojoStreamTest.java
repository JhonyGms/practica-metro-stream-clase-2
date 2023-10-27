package com.example.demo;


import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DojoStreamTest {

    @Test
    void converterData() {
        List<Player> list = CsvUtilFile.getPlayers();
        assert list.size() == 18207;
    }

    @Test
    void jugadoresMayoresA35() {

//        Predicate evaluador = (Object jugador) -> {
//            Player jugadorCasteado = (Player)jugador;
//            return jugadorCasteado.getAge() > 35;
//        };

        List<Player> list = CsvUtilFile.getPlayers();
        Set<Player> result = (Set<Player>) list.stream()
                .filter(player -> player.getAge() > 35)
                .collect(Collectors.toSet());
        result.forEach(System.out::println);
    }

    @Test
    void jugadoresMayoresA35SegunClub() {
        List<Player> list = CsvUtilFile.getPlayers();
        Map<String, List<Player>> result = list.stream()
                .filter(player -> player.getAge() > 35)
                .distinct()
                .collect(Collectors.groupingBy(Player::getClub));

        result.forEach((key, jugadores) -> {
            System.out.println("\n");
            System.out.println(key + ": ");
            jugadores.forEach(System.out::println);
        });
    }

    @Test
    void mejorJugadorConNacionalidadFrancia() {

        List<Player> list = CsvUtilFile.getPlayers();
        Optional<Player> result = list.stream()
                .filter(jugador -> "France".equals(jugador.getNational()))
                .max((j1, j2) -> Integer.compare(j1.getWinners(), j2.getWinners()));

        if (result.isPresent()) {
            Player maxWinner = result.get();
            System.out.println("Jugador con más winners de Francia: " + maxWinner.getName() +
                    " con " + maxWinner.getWinners() + " victorias.");
        } else {
            System.out.println("No se encontró ningún jugador de Francia.");
        }
    }


    @Test
    void clubsAgrupadosPorNacionalidad() {
        List<Player> list = CsvUtilFile.getPlayers();
        /*
        Map<String, List<String>> result = list.stream()
                .distinct()
                .collect(Collectors.groupingBy(Player::getNational, Collectors.mapping(Player::getClub, Collectors.toList())));

        result.forEach((key, jugadores) -> {
            System.out.println("\n");
            System.out.println(key + ": ");
            jugadores.forEach(System.out::println);
        });*/


        // Agrupar jugadores por nacionalidad
        Map<String, List<Player>> clubesAgrupadosPorNacionalidad = list.stream()
                .collect(Collectors.groupingBy(Player::getNational));

        // Mostrar los clubes de cada nacionalidad
        clubesAgrupadosPorNacionalidad.forEach((nacionalidad, listaJugadores) -> {
            System.out.println("Nacionalidad: " + nacionalidad);
            System.out.println("Clubes: " + listaJugadores.stream().map(Player::getClub).collect(Collectors.toList()));
        });
    }

    @Test
    void clubConElMejorJugador() {

        //nop ni idea xd
        List<Player> list = CsvUtilFile.getPlayers();
        String clubConMasWinners = list.stream()
                .collect(Collectors.groupingBy(Player::getClub, Collectors.summingInt(Player::getWinners)))
                .entrySet().stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);

        System.out.println("Club con más winners: " + clubConMasWinners);

    }

    @Test
    void ElMejorJugador() {


        List<Player> list = CsvUtilFile.getPlayers();
        Optional<Player> result = list.stream()
                .max((j1, j2) -> Integer.compare(j1.getWinners(), j2.getWinners()));

            Player maxWinner = result.get();
            System.out.println("Jugador con más winners: " + maxWinner.getName() +
                    " con " + maxWinner.getWinners() + " victorias.");

    }

    @Test
    void mejorJugadorSegunNacionalidad() {


        List<Player> list = CsvUtilFile.getPlayers();

        Map<String, Optional<Player>> maxWinnersPorNacionalidad = list.stream()
                .collect(Collectors.groupingBy(Player::getNational,
                        Collectors.maxBy(Comparator.comparing(Player::getWinners))));

        // Mostrar el jugador con más winners por cada nacionalidad
        maxWinnersPorNacionalidad.forEach((nacionalidad, jugador) -> {
            jugador.ifPresent(j -> {
                System.out.println("Nacionalidad: " + nacionalidad);
                System.out.println("Jugador con más winners: " + j.getName() + " con " + j.getWinners() + " victorias.");
            });
        });
    }


}
