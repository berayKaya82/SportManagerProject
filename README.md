# Sports Manager Project

[![Java](https://img.shields.io/badge/Java-26-007396?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-26-1F8DD6?logo=java&logoColor=white)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.9%2B-C71A36?logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![JUnit 5](https://img.shields.io/badge/JUnit-5.10-25A162?logo=junit5&logoColor=white)](https://junit.org/junit5/)

A multi-sport career management simulation written in Java with a JavaFX UI.

---

## Project Overview

Sports Manager is a turn-based career simulation where the user manages a team
across a full league season — picking tactics, training the squad, hiring
coaches, and competing for the championship. The engine supports multiple
sports through a shared abstraction; **Football** and **Handball** ship as
fully playable implementations.

## Features

- Two playable sports — Football (18 teams, 11 starters) and Handball (12 teams, 7 starters).
- Full league season with round-robin fixture generation and double-leg scheduling.
- Period-based match simulation (two halves) with tactic adjustment at half-time.
- Tactical system with three play styles: `DEFENSIVE`, `BALANCED`, `OFFENSIVE`.
- Training system with three intensities (`LIGHT`, `MEDIUM`, `HARD`) plus per-week recovery.
- Coach hierarchy that scales with manager reputation and relationship score.
- League standings driven by a sport-specific tie-breaker (points → goal difference → goals scored).
- Multi-slot JSON save/load via Gson.
- JavaFX UI with a responsive two-column main menu and dedicated screens for each game phase.
- JUnit 5 test suite covering domain rules, scoring, fixtures, and league logic.

## Architecture

The core is a sport-agnostic contract layer (`sport.*`) consisting of `ISport`,
`MatchFlow`, `MatchSimulator`, `RosterRule`, `ScoringRule`, `TieBreakerRule`
and `ITactic`. Football and Handball are independent packages implementing
these interfaces — the rest of the application (league management, fixture
generation, season cycle, UI) consumes only the abstractions and never
references a concrete sport. Adding a new sport means writing one package
that supplies the seven contracts; nothing else has to change.

## Technologies Used

- **Java 26** + **JavaFX 26**
- **Maven** (`maven-compiler-plugin`, `javafx-maven-plugin`, `surefire`)
- **Gson 2.13** for save/load serialization
- **JUnit Jupiter 5.10** for testing

## Project Structure

```text
src/main/java/
├── application/   # Game facade, managers, save/load, generators
├── domain/        # Player, Team, Coach, League, Fixture, Match, Season
├── sport/         # ISport + rule abstractions
├── football/      # Football implementation of the sport contracts
├── handball/      # Handball implementation of the sport contracts
└── ui/            # JavaFX layer (App, SceneManager, controllers)
```

## How It Works

1. The user starts a new game and picks a sport, gender, and team name.
2. `LeagueManager` builds an N-team league sized by `ISport.getTeamCount()`.
3. `Fixture.generate(...)` produces a shuffled double round-robin schedule.
4. Each in-game week the user trains the squad, picks a tactic, and plays a
   two-half match while AI matches are simulated by the sport's `MatchSimulator`.
5. Results feed `LeagueTable`, which sorts standings via the sport's `TieBreakerRule`.
6. Seasons end at the final week; reputation and progress can be saved to a
   slot and resumed later.

## Running the Project

Requires JDK 26 and Maven 3.9+.

```bash
mvn clean compile
mvn javafx:run
```

## Testing

JUnit 5 tests cover domain models, fixture generation, scoring rules,
tie-breakers, and league management.

```bash
mvn test
```
