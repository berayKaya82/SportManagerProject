package application;

import java.util.List;

/**
 * This class provides data ONLY (Dumb Class).
 *There is no logic, randomness or calculation in it.
 */
public class NamePool {
    public static final List<String> MALE_NAMES =
            """
            Ahmet Yılmaz
            Mehmet Demir
            Can Kaya
            Emre Çelik
            Burak Şahin
            Ali Yıldız
            Kerem Aydın
            Arda Kılıç
            Yusuf Koç
            Mert Aslan
            Ozan Kurt
            Efe Özdemir
            Kaan Arslan
            Berk Doğan
            Hakan Yalçın
            James Smith
            John Johnson
            Michael Brown
            David Wilson
            Daniel Taylor
            Matthew Anderson
            Christopher Thomas
            Joshua Moore
            Andrew Martin
            Ryan Jackson
            Nathan White
            Samuel Harris
            Benjamin Clark
            Jack Lewis
            Henry Walker
            Carlos García
            Luis Martínez
            Juan Rodríguez
            Pedro Sánchez
            Diego Fernández
            Javier López
            Miguel Gómez
            Fernando Díaz
            Antonio Ruiz
            Rafael Torres
            Alejandro Navarro
            Sergio Romero
            Pablo Castro
            Andrés Ortega
            Manuel Delgado
            Hans Müller
            Lukas Schmidt
            Leon Schneider
            Finn Fischer
            Jonas Weber
            Paul Wagner
            Max Becker
            Tim Hoffmann
            Felix Schäfer
            Noah Koch
            Erik Bauer
            Nico Richter
            David Klein
            Simon Wolf
            Oliver Brandt
            Ahmet Karaca
            Emre Polat
            Can Aksoy
            Mert Güneş
            Burak Acar
            Ali Öztürk
            Kerem Avcı
            Arda Bozkurt
            Yusuf Duman
            Ozan Tekin
            Efe Karataş
            Kaan Yıldırım
            Berk Erdem
            Hakan Güler
            James Cooper
            John Ward
            Michael Cox
            David Reed
            Daniel Cook
            Matthew Bell
            Christopher Murphy
            Joshua Bailey
            Andrew Rivera
            Ryan Cooper
            Nathan Richardson
            Samuel Cox
            Benjamin Howard
            Jack Ward
            Henry Peterson
            Carlos Herrera
            Luis Medina
            Juan Vega
            Pedro Morales
            Diego Vargas
            Javier Rojas
            Miguel Castro
            Fernando Ortiz
            Antonio Ramos
            Rafael Guerrero
            """
                    .lines().toList();

    public static final List<String> FEMALE_NAMES =
            """  
            Ayşe Yılmaz
            Zeynep Demir
            Elif Kaya
            Merve Çelik
            Fatma Şahin
            Seda Yıldız
            Ece Aydın
            Derya Kılıç
            Selin Koç
            Buse Aslan
            Ceren Kurt
            Melis Özdemir
            Naz Arslan
            İrem Doğan
            Deniz Yalçın
            Emily Smith
            Olivia Johnson
            Emma Brown
            Sophia Wilson
            Isabella Taylor
            Mia Anderson
            Charlotte Thomas
            Amelia Moore
            Harper Martin
            Evelyn Jackson
            Abigail White
            Ella Harris
            Scarlett Clark
            Grace Lewis
            Lily Walker
            María García
            Lucía Martínez
            Sofía Rodríguez
            Paula Sánchez
            Elena Fernández
            Carmen López
            Ana Gómez
            Laura Díaz
            Marta Ruiz
            Sara Torres
            Clara Navarro
            Irene Romero
            Julia Castro
            Nuria Ortega
            Patricia Delgado
            Anna Müller
            Lena Schmidt
            Laura Schneider
            Mia Fischer
            Lea Weber
            Clara Wagner
            Sophie Becker
            Emma Hoffmann
            Hannah Schäfer
            Lina Koch
            Nina Bauer
            Paula Richter
            Marie Klein
            Sara Wolf
            Julia Brandt
            Ayşe Karaca
            Zeynep Polat
            Elif Aksoy
            Merve Güneş
            Fatma Acar
            Seda Öztürk
            Ece Avcı
            Derya Bozkurt
            Selin Duman
            Buse Tekin
            Ceren Karataş
            Melis Yıldırım
            Naz Erdem
            İrem Güler
            Deniz Yıldız
            Emily Cooper
            Olivia Ward
            Emma Cox
            Sophia Reed
            Isabella Cook
            Mia Bell
            Charlotte Murphy
            Amelia Bailey
            Harper Rivera
            Evelyn Richardson
            Abigail Cox
            Ella Howard
            Scarlett Ward
            Grace Peterson
            Lily Cooper
            María Herrera
            Lucía Medina
            Sofía Vega
            Paula Morales
            Elena Vargas
            Carmen Rojas
            Ana Castro
            Laura Ortiz
            Marta Ramos
            Sara Guerrero
            """
                    .lines().toList();

    public static final List<String> TEAM_NAMES =
            """
                    İstanbul Yıldızları
                    Anadolu Kartalları
                    Boğazın Yargıçları
                    Karadeniz Fırtınası
                    Mezopotamya Güneşi
                    Başkent Gücü
                    Ege Efeleri
                    Toros Kaplanları
                    Marmara Birlik
                    Altın Ordu SK
                    Körfez United
                    Bozkır Ateşi
                    Hisar Gençlik
                    Çınarspor
                    Kuzey Işıkları FK
                    London Royals
                    Manchester Ironclads
                    Silver City United
                    North Star FC
                    Ocean Side Athletics
                    Golden Gate Warriors
                    Iron Valley Rovers
                    Sky High City
                    Emerald Coast FC
                    Blackwood Rangers
                    Stormy Peaks United
                    Phoenix Rising
                    Shadow River SC
                    Liberty Lions
                    Titan City FC
                    Red Desert Wanderers
                    Mystic Hollow FC
                    Cyber City 2077
                    Velocity United
                    Noble Knights FC
                    Olympique de Lumière
                    Paris Étoile
                    Lion de Lyon
                    AS Marseille Bleu
                    Bordeaux Vignes
                    Renaissance de Lille
                    Côte d'Azur United
                    Sporting Club du Nord
                    FC Versailles Blanc
                    Les Dragons de Nice
                    Éclair de Nantes
                    Saint-Germain Pirates
                    AS Montagne
                    Victoire de Toulouse
                    Union de Strasbourg
                    Real Madrid Azul
                    Valencia Naranjas
                    Atlético Sol
                    Deportivo de la Luna
                    Sevilla Guerreros
                    Barcelona Blaugrana
                    Inter Milan Rosso
                    Juventus de Turín
                    AC Roma Gladiators
                    Napoli Azzurri
                    Fiorentina Viola
                    Lazio Aquile
                    Palermo Aquila d'Oro
                    San Sebastian United
                    Bilbao Leones
                    Berlin Mauer FC
                    München Giganten
                    Dortmund Bienen
                    Hamburg Hafen SC
                    Wolfsburg Jäger
                    Frankfurt Adler
                    Leipzig Bullen
                    Stockholm Vikings
                    Copenhagen Wolves
                    Oslo Fjords FC
                    Helsinki Blizzard
                    Reykjavik Icebergs
                    Amsterdam Tulips
                    Rotterdam Port FC
                    Brussels Red Devils
                    Tokyo Samurai FC
                    Rio Samba United
                    Cairo Pharaohs
                    Seoul Dragons
                    Sydney Sharks
                    Cape Town Diamonds
                    Casablanca Lions
                    Mumbai Tigers
                    Toronto Raptors SC
                    Mexico City Aztecs
                    Aurora Borealis FC
                    Zenith Zenith
                    Nebula Wanderers
                    Gravity Zero FC
                    Midnight Sun SC
                    Volcano Peak United
                    Desert Mirage FC
                    Neon Night Raiders
                    Alpha Centauri FC
                    Omega Force United        
                    """
                    .lines().toList();

}
