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
                    .lines().filter(s -> !s.isBlank()).toList();

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
                    .lines().filter(s -> !s.isBlank()).toList();

    public static final List<String> TEAM_NAMES =
            """
            Fenerbahçe
            Galatasaray
            Beşiktaş
            Ankaragücü
            Altay
            Karşıyaka
            Göztepe
            Real Madrid
            Barcelona
            Olympiacos
            Panathinaikos
            AEK Athens
            PAOK
            Red Star Belgrade
            Partizan
            Dinamo Zagreb
            Hajduk Split
            CSKA Moscow
            Spartak Moscow
            Dinamo Moscow
            Lokomotiv Moscow
            Benfica
            Sporting CP
            Porto
            Ajax
            Feyenoord
            PSV Eindhoven
            Bayern Munich
            Schalke 04
            Hamburger SV
            Eintracht Frankfurt
            Stuttgart
            Anderlecht
            Club Brugge
            Standard Liege
            Rapid Vienna
            Austria Vienna
            Ferencvaros
            Slavia Prague
            Sparta Prague
            Legia Warsaw
            Lech Poznan
            Wisla Krakow
            Steaua Bucharest (FCSB)
            Dinamo Bucharest
            Universitatea Craiova
            Ludogorets Razgrad
            Partizan Tirana
            Vllaznia Shkoder
            Rabotnicki
            Vardar Skopje
            Olimpija Ljubljana
            Maribor
            Zalgiris Vilnius
            FK Sarajevo
            Zeljeznicar Sarajevo
            Buducnost Podgorica
            Sutjeska Niksic
            APOEL
            Omonia Nicosia
            Anorthosis Famagusta
            Maccabi Tel Aviv
            Hapoel Tel Aviv
            Maccabi Haifa
            Al Ahly
            Zamalek
            Esperance Tunis
            Wydad Casablanca
            Raja Casablanca
            TP Mazembe
            AS Vita Club
            Kaizer Chiefs
            Orlando Pirates
            Al Hilal
            Al Nassr
            Al Ittihad
            Al Sadd
            Al Duhail
            Persepolis
            Esteghlal
            Sepahan
            Al Ain
            Sharjah FC
            Sydney Olympic
            South Melbourne
            Kitchee SC
            Eastern SC
            Tai Po FC
            Johor Darul Ta'zim
            Selangor FA
            Persija Jakarta
            Persib Bandung
            Arema FC
            Muangthong United
            Buriram United
            Chonburi FC
            """
                    .lines().filter(s -> !s.isBlank()).toList();
}
