package com.example.demo;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

public class DojoReactiveTest {

    @Test
    void converterData(){
        List<Player> list = CsvUtilFile.getPlayers();
        assert list.size() == 18207;
    }

    @Test
    void jugadoresMayoresA35() {
        List<Player> list = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(list);

        observable.filter(jugador -> jugador.getAge() > 35)
                .subscribe(System.out::println);
    }


    @Test
    void jugadoresMayoresA35SegunClub(){
        List<Player> readCsv = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(readCsv);

        observable.filter(player -> player.getAge() > 35)
                .distinct()
                .groupBy(Player::getClub)
                .flatMap(groupedFlux -> groupedFlux
                        .collectList()
                        .map(list -> {
                            Map<String, List<Player>> map = new HashMap<>();
                            map.put(groupedFlux.key(), list);
                            return map;
                        }))
                .subscribe(map -> {
                    map.forEach((key, value) -> {
                        System.out.println("\n");
                        System.out.println(key + ": ");
                        value.forEach(System.out::println);
                    });
                });

    }


    @Test
    void mejorJugadorConNacionalidadFrancia(){

        List<Player> readCsv = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(readCsv);

        observable
                .filter(player -> "France".equalsIgnoreCase(player.getNational()))
                .reduce((player1, player2) -> {
                    if (player1.getWinners() > player2.getWinners()) {
                        return player1;
                    } else {
                        return player2;
                    }
                })
                .subscribe(player -> {
                    System.out.println("Jugador con más winners de Francia: " + player.getName() +
                            " con " + player.getWinners() + " victorias."+ player);
                });
    }

    @Test
    void clubsAgrupadosPorNacionalidad(){


        List<Player> readCsv = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(readCsv);
        observable
                .groupBy(Player::getNational)
                .flatMap(groupedFlux -> groupedFlux
                        .map(Player::getClub)
                        .collectList()
                        .map(clubs -> new AbstractMap.SimpleEntry<>(groupedFlux.key(), clubs)))
                .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                .subscribe(map -> {
                    map.forEach((key, value) -> {
                        System.out.println("\n");
                        System.out.println(key + ": ");
                        value.forEach(System.out::println);
                    });
                });
    }

    @Test
    void clubConElMejorJugador(){


        List<Player> readCsv = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(readCsv);
        observable
                .collectMultimap(Player::getClub)
                .flatMap(clubsMap -> {
                    Mono<String> bestClubMono = Mono.just("");
                    int bestRating = Integer.MIN_VALUE;

                    for (String club : clubsMap.keySet()) {
                        List<Player> clubPlayers = (List<Player>) clubsMap.get(club);
                        for (Player player : clubPlayers) {
                            if (player.getWinners() > bestRating) {
                                bestRating = player.getWinners();
                                bestClubMono = Mono.just(club);
                            }
                        }
                    }

                    return bestClubMono;
                })
                .subscribe(System.out::println);
    }

    @Test
    void clubConElMejorJugador2() {
    }

    @Test
    void ElMejorJugador() {
        List<Player> readCsv = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(readCsv);
        observable
        .reduce((player1, player2) -> {
            if (player1.getWinners() > player2.getWinners()) {
                return player1;
            } else {
                return player2;
            }
        }).subscribe(bestPlayer -> {
            System.out.println("Mejor jugador: " + bestPlayer);
        });
    }

    @Test
    void ElMejorJugador2() {

        List<Player> readCsv = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(readCsv);
        observable
        .collectList()
                .map(playerList -> {
                    playerList.sort(Comparator.comparingInt(Player::getWinners).reversed());
                    if (playerList.size() >= 2) {
                        return playerList.get(2); // El segundo mejor jugador
                    } else {
                        return null; // No hay suficientes jugadores para determinar al segundo mejor
                    }
                }).subscribe(secondBestPlayer -> {
                    if (secondBestPlayer != null) {
                        System.out.println("Segundo mejor jugador: " + secondBestPlayer);
                    } else {
                        System.out.println("No hay suficientes jugadores para determinar al segundo mejor.");
                    }
                });
    }

    @Test
    void mejorJugadorSegunNacionalidad(){

        List<Player> readCsv = CsvUtilFile.getPlayers();
        Flux<Player> observable = Flux.fromIterable(readCsv);
        observable
                .collectMultimap(Player::getNational, player -> player)
                .flatMap(nationalityToPlayers -> {
                    Map<String, Player> result = new HashMap<>();
                    nationalityToPlayers.forEach((nationality, playerList) -> {
                        Player playerWithMostVictories = playerList.stream()
                                .max(Comparator.comparingInt(Player::getWinners))
                                .orElse(null);
                        if (playerWithMostVictories != null) {
                            result.put(nationality, playerWithMostVictories);
                        }
                    });
                    return Mono.just(result);
                })
                .subscribe(map -> {
                    map.forEach((key, value) -> {
                        System.out.println("\n");
                        System.out.println(key + ": ");
                        System.out.println(value);
                    });
                });
    }



}
