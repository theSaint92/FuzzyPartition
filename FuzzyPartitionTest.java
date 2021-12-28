import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Klasa testująca bazowe funkcjonalności klasy 'FuzzyPartition'.
 */
class FuzzyPartitionTest {

    /**
     * Ilość iteracji w testach wykonywanych na losowych danych.
     */
    public static final int NUMBER_OF_RANDOM_TESTS = 10000;

    /**
     * Dokładność, z jaką porównujemy liczby zmiennoprzecinkowe.
     */
    public static final double EPSILON = 0.00000001;

    /**
     * Ustawienie dokładności, z jaką porównujemy liczby zmiennoprzecinkowe
     * w klasie FuzzyPartition.
     */
    @BeforeAll
    static void initializeTest() {
        FuzzyPartition.setEpsilon(EPSILON);
    }

    /**
     * Test sprawdzający, czy konstruktor wpisujący losowe dane zwraca
     * poprawny podział rozmyty.
     */
    @Test
    @DisplayName("Test konstruktora generującego losowe dane")
    void FuzzyPartitionRandomConstructorTest() {
        // Tworzymy pewną ilość podziałów rozmytych i zliczamy te, które nie
        // przechodzą metody validate.
        int fails = 0;
        for (int i = 0; i < NUMBER_OF_RANDOM_TESTS; i++) {
            FuzzyPartition current = new FuzzyPartition(5, 5);
            if (!current.validate())
                fails++;
        }
        assertEquals(0, fails);
    }

    /**
     * Test sprawdzający poprawność działania metody 'calculateAlphaLevel'
     */
    @Test
    @DisplayName("Test metody 'calculateAlphaLevel'")
    void testCalculateAlphaLevel() {
        FuzzyPartition partition = new FuzzyPartition(new double[][]{
                {0.5, 0.7, 0.3, 0.0},
                {0.4, 0.2, 0.4, 0.1},
                {0.1, 0.1, 0.3, 0.9}
        });

        // Podział rozmyty będący alfa-cięciem wzorcowego podziału.
        FuzzyPartition partitionALevel = new FuzzyPartition(new double[][]{
                {0.5, 1.0, 1.0 / 3.0, 0.0},
                {0.5, 0.0, 1.0 / 3.0, 0.0},
                {0.0, 0.0, 1.0 / 3.0, 1.0}
        });

        assertEquals(
                partitionALevel,
                partition.calculateAlphaLevel(0.25)
        );

        // Sprawdzamy błędnie wprowadzone dane
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> partition.calculateAlphaLevel(0.0)
        );
        assertEquals(
                "Value of alpha must be bigger than zero",
                exception.getMessage()
        );

        Exception exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> partition.calculateAlphaLevel(0.4)
        );
        assertEquals(
                "Value of alpha must be lower than maximum " +
                        "value in any column",
                exception2.getMessage()
        );
    }

    /**
     * Test sprawdzający poprawność działania metody
     * 'calculateComplementAlphaLevel'
     */
    @Test
    @DisplayName("Test metody 'calculateComplementAlphaLevel'")
    void testCalculateComplementAlphaLevel() {
        FuzzyPartition partition = new FuzzyPartition(new double[][]{
                {0.5, 0.7, 0.3, 0.0},
                {0.4, 0.2, 0.4, 0.1},
                {0.1, 0.1, 0.3, 0.9}
        });

        // Podział rozmyty będący dopełnieniem alfa-cięcia wzorcowego
        // podziału.
        FuzzyPartition partitionCALevel = new FuzzyPartition(new double[][]{
                {0.0, 0.0, 1.0 / 3.0, 0.5},
                {0.0, 0.5, 1.0 / 3.0, 0.5},
                {1.0, 0.5, 1.0 / 3.0, 0.0}
        });

        assertEquals(
                partitionCALevel,
                partition.calculateComplementAlphaLevel(0.25)
        );

        // Sprawdzamy błędnie wprowadzone dane
        Exception exception = assertThrows(
                IllegalArgumentException.class,
                () -> partition.calculateAlphaLevel(0.0)
        );
        assertEquals(
                "Value of alpha must be bigger than zero",
                exception.getMessage()
        );

        Exception exception2 = assertThrows(
                IllegalArgumentException.class,
                () -> partition.calculateAlphaLevel(0.4)
        );
        assertEquals(
                "Value of alpha must be lower than maximum " +
                        "value in any column",
                exception2.getMessage()
        );
    }

    /**
     * Test sprawdzający poprawność działania metody 'alphaApproximate'
     */
    @Test
    @DisplayName("Test metody 'alphaApproximate'")
    void testAlphaApproximate() {
        FuzzyPartition U = new FuzzyPartition(new double[][]{
                {0.5, 0.7, 0.3, 0.0},
                {0.4, 0.2, 0.4, 0.1},
                {0.1, 0.1, 0.3, 0.9}
        });

        FuzzyPartition V = new FuzzyPartition(new double[][]{
                {0.4, 1.0, 0.3, 0.1},
                {0.4, 0.0, 0.3, 0.2},
                {0.2, 0.0, 0.4, 0.7}
        });

        // Podziały U i V mają identyczne 0.3-cięcia.
        assertEquals(
                1.0,
                U.alphaApproximate(0.3, V)
        );
        assertNotEquals(
                1.0,
                U.alphaApproximate(
                        0.3,
                        V.calculateComplementAlphaLevel(0.3)
                )
        );

        FuzzyPartition U2 = new FuzzyPartition(new double[][]{
                {1.0, 1.0, 1.0, 1.0},
                {0.0, 0.0, 0.0, 0.0},
                {0.0, 0.0, 0.0, 0.0}
        });

        FuzzyPartition V2 = new FuzzyPartition(new double[][]{
                {0.0, 0.0, 0.0, 0.0},
                {0.0, 0.0, 0.0, 0.0},
                {1.0, 1.0, 1.0, 1.0}
        });

        // Przykład gdzie alphaApproximate powinno wynosić 0.
        assertEquals(
                0.0,
                U2.alphaApproximate(1.0, V2)
        );
    }

    /**
     * Test sprawdzający poprawność działania metody 'calculateLS'
     */
    @Test
    @DisplayName("Test metody 'calculateLS'")
    void testCalculateLS() {
        FuzzyPartition partition = new FuzzyPartition(new double[][]{
                {0.50, 0.7, 0.25, 0.1},
                {0.40, 0.1, 0.25, 0.1},
                {0.05, 0.1, 0.25, 0.7},
                {0.05, 0.1, 0.25, 0.1}
        });

        FuzzyPartition partitionLS = new FuzzyPartition(new double[][]{
                {0.5625, 0.8125, 0.25, 0.0625},
                {0.4375, 0.0625, 0.25, 0.0625},
                {0.0000, 0.0625, 0.25, 0.8125},
                {0.0000, 0.0625, 0.25, 0.0625}
        });

        assertEquals(
                partitionLS,
                partition.calculateLS()
        );

        // Ponieważ zbiór ten jest już maksymalnie wyostrzony, to kolejne
        // poddanie go tej metodzie nie powinno spowodować kolejnych zmian
        // w otrzymanych wartościach.
        assertEquals(
                partitionLS,
                partition.calculateLS().calculateLS()
        );

    }

    /**
     * Test sprawdzający poprawność działania metody
     * 'calculateComplementLS'
     */
    @Test
    @DisplayName("Test metody 'calculateComplementLS'")
    void testCalculateComplementLS() {
        FuzzyPartition partition = new FuzzyPartition(new double[][]{
                {0.5, 0.5, 0.25, 0.1},
                {0.3, 0.2, 0.25, 0.2},
                {0.1, 0.2, 0.25, 0.5},
                {0.1, 0.1, 0.25, 0.2}
        });

        FuzzyPartition partitionCLS = new FuzzyPartition(new double[][]{
                {0.0, 0.0, 0.25, 0.4},
                {0.2, 0.3, 0.25, 0.3},
                {0.4, 0.3, 0.25, 0.0},
                {0.4, 0.4, 0.25, 0.3}
        });

        assertEquals(
                partitionCLS,
                partition.calculateComplementLS()
        );

        // Ponieważ zbiór ten jest już maksymalnie wyostrzony, to poddanie
        // go metodzie calculateLS nie powinno spowodować kolejnych zmian
        // w otrzymanych wartościach.
        assertEquals(
                partitionCLS,
                partition.calculateComplementLS().calculateLS()
        );
    }

    /**
     * Test sprawdzający poprawność działania metody 'calculateMLS'
     */
    @Test
    @DisplayName("Test metody 'calculateMLS'")
    void testCalculateMLS() {
        FuzzyPartition partition = new FuzzyPartition(new double[][]{
                {0.50, 0.7, 0.25, 0.0},
                {0.40, 0.1, 0.25, 0.1},
                {0.05, 0.1, 0.25, 0.8},
                {0.05, 0.1, 0.25, 0.1}
        });

        FuzzyPartition partitionMLS = new FuzzyPartition(new double[][]{
                {0.5625, 1.0, 0.25, 0.0},
                {0.4375, 0.0, 0.25, 0.1},
                {0.0000, 0.0, 0.25, 0.8},
                {0.0000, 0.0, 0.25, 0.1}
        });

        assertEquals(
                partitionMLS,
                partition.calculateMLS()
        );

        // Ponieważ zbiór ten jest już maksymalnie wyostrzony, to kolejne
        // poddanie go tej metodzie nie powinno spowodować kolejnych zmian
        // w otrzymanych wartościach.
        assertEquals(
                partitionMLS,
                partition.calculateMLS().calculateMLS()
        );

    }

    /**
     * Test sprawdzający poprawność działania metody
     * 'calculateComplementMLS'
     */
    @Test
    @DisplayName("Test metody 'calculateComplementMLS'")
    void testCalculateComplementMLS() {
        FuzzyPartition partition = new FuzzyPartition(new double[][]{
                {0.50, 0.7, 0.25, 0.1},
                {0.40, 0.1, 0.25, 0.2},
                {0.05, 0.1, 0.25, 0.5},
                {0.05, 0.1, 0.25, 0.2}
        });

        FuzzyPartition partitionCMLS = new FuzzyPartition(new double[][]{
                {0.00, 0.0 / 3.0, 0.25, 0.4},
                {0.10, 1.0 / 3.0, 0.25, 0.3},
                {0.45, 1.0 / 3.0, 0.25, 0.0},
                {0.45, 1.0 / 3.0, 0.25, 0.3}
        });

        assertEquals(
                partitionCMLS,
                partition.calculateComplementMLS()
        );

        // Ponieważ zbiór ten jest już maksymalnie wyostrzony, to poddanie
        // go metodzie calculateMLS nie powinno spowodować kolejnych zmian
        // w otrzymanych wartościach.
        assertEquals(
                partitionCMLS,
                partition.calculateComplementMLS().calculateMLS()
        );
    }

    /**
     * Test sprawdzający poprawność działania metody
     * 'calculateSharpnessDegree'
     */
    @Test
    @DisplayName("Test metody 'calculateSharpnessDegree'")
    void testCalculateSharpnessDegree() {
        FuzzyPartition partition = new FuzzyPartition(new double[][]{
                {0.50, 0.7, 0.25, 0.0},
                {0.40, 0.1, 0.25, 0.1},
                {0.05, 0.1, 0.25, 0.8},
                {0.05, 0.1, 0.25, 0.1}
        });

        // Poniższy podział rozmyty jest wyostrzeniem wzorca
        FuzzyPartition partitionSharp = new FuzzyPartition(new double[][]{
                {0.6, 1.0, 0.25, 0.0},
                {0.4, 0.0, 0.25, 0.1},
                {0.0, 0.0, 0.25, 0.8},
                {0.0, 0.0, 0.25, 0.1}
        });

        assertEquals(
                1.0,
                partition.calculateSharpnessDegree(partitionSharp)
        );

        // Poniższe podziały rozmyte mają stopień podobieństwa równy 0
        FuzzyPartition partition2 = new FuzzyPartition(new double[][]{
                {1.0, 1.0, 1.0},
                {0.0, 0.0, 0.0},
                {0.0, 0.0, 0.0}
        });

        FuzzyPartition partitionNSharp = new FuzzyPartition(new double[][]{
                {0.0, 0.0, 0.0},
                {0.0, 0.0, 0.0},
                {1.0, 1.0, 1.0}
        });

        assertEquals(
                0.0,
                partition2.calculateSharpnessDegree(partitionNSharp)
        );
    }

    /**
     * Poniższy test sprawdza, czy metoda `complement` zwraca prawidłowe
     * wyniki.
     */
    @Test
    @DisplayName("Test metody 'complement'")
    void testComplement() {
        FuzzyPartition partition = new FuzzyPartition(new double[][]{
                {0.8, 0.2, 0.1, 0.5},
                {0.1, 0.6, 0.6, 0.2},
                {0.1, 0.2, 0.3, 0.3}
        });

        FuzzyPartition partitionC = new FuzzyPartition(new double[][]{
                {0.10, 0.4, 0.5375, 0.20},
                {0.45, 0.2, 0.1000, 0.44},
                {0.45, 0.4, 0.3625, 0.36}
        });

        assertEquals(
                partitionC,
                partition.complement()
        );

        // Ten podział jest swoim własnym dopełnieniem
        FuzzyPartition partition2 = new FuzzyPartition(new double[][]{
                {0.25, 0.25, 0.25, 0.25},
                {0.25, 0.25, 0.25, 0.25},
                {0.25, 0.25, 0.25, 0.25},
                {0.25, 0.25, 0.25, 0.25}
        });

        assertEquals(
                partition2,
                partition2.complement()
        );
    }

    /**
     * Poniższy test sprawdza, czy metoda `toString` zwraca prawidłowe
     * wyniki.
     */
    @Test
    @DisplayName("Test metody 'toString'")
    void testToString() {
        FuzzyPartition partition = new FuzzyPartition(new double[][]{
                {0.5, 0.7, 0.3, 0.0},
                {0.4, 0.2, 0.4, 0.1},
                {0.1, 0.1, 0.3, 0.9}
        });

        String expected = "   0,5000    0,7000    0,3000    0,0000 "
                + System.lineSeparator()
                + "   0,4000    0,2000    0,4000    0,1000 " +
                System.lineSeparator()
                + "   0,1000    0,1000    0,3000    0,9000 ";
        assertEquals(
                expected,
                partition.toString()
        );

        String unexpected = "   0,5000    0,7000    0,3000    0,0000 error"
                + System.lineSeparator()
                + "   0,4000    0,2000    0,4000    0,1000 "
                + System.lineSeparator()
                + "   0,1000    0,1000    0,3000    0,9000 ";
        assertNotEquals(
                unexpected,
                partition.toString()
        );
    }

    /**
     * Poniższy test sprawdza, czy metoda `validate` zwraca prawidłowe
     * wyniki.
     */
    @Test
    @DisplayName("Test metody 'validate'")
    void testValidate() {

        // Poniższy zestaw danych spełnia wszystkie założenia, aby być
        // prawidłowym podziałem rozmytym. Walidacja powinna zakończyć się
        // sukcesem.
        FuzzyPartition goodData = new FuzzyPartition(new double[][]{
                {0.5, 0.7, 0.3, 0.0},
                {0.4, 0.2, 0.4, 0.1},
                {0.1, 0.1, 0.3, 0.9}
        });
        assertTrue(goodData.validate());

        // Poniższy zestaw danych nie spełnia założeń, aby być prawidłowym
        // podziałem rozmytym. (jedna z wartości jest ujemna). Walidacja
        // powinna zwrócić negatywny wynik.
        FuzzyPartition badData = new FuzzyPartition(new double[][]{
                {0.5, 0.7, 0.3, 0.8},
                {0.4, 0.2, 0.4, 0.3},
                {0.1, 0.1, 0.3, -0.1}
        });
        assertFalse(badData.validate());

        // Poniższy zestaw danych nie spełnia założeń, aby być prawidłowym
        // podziałem rozmytym. (Suma w ostatniej kolumnie jest większa niż
        // 1). Walidacja powinna zwrócić negatywny wynik.
        FuzzyPartition badData2 = new FuzzyPartition(new double[][]{
                {0.5, 0.7, 0.3, 0.0},
                {0.4, 0.2, 0.4, 0.1},
                {0.1, 0.1, 0.3, 0.95}
        });
        assertFalse(badData2.validate());

        // Poniższy zestaw danych jest prawie dobry, w zależności od
        // tolerowanej dokładności, walidacja powinna zwracać różne wyniki.
        FuzzyPartition goodDataSmallErr = new FuzzyPartition(new double[][]{
                {0.5, 0.7, 0.3, 0.0},
                {0.4, 0.2, 0.4, 0.1},
                {0.1, 0.1, 0.3, 0.9001}
        });

        FuzzyPartition.setEpsilon(0.01);
        assertTrue(goodDataSmallErr.validate());

        FuzzyPartition.setEpsilon(EPSILON);
        assertFalse(goodDataSmallErr.validate());
    }

    /**
     * Poniższy test sprawdza, czy metoda equals zwraca prawidłowe wyniki.
     */
    @Test
    @DisplayName("Test metody 'equals'")
    void testEquals() {
        FuzzyPartition partition = new FuzzyPartition(new double[][]{
                {0.5, 0.7, 0.3, 0.0},
                {0.4, 0.2, 0.4, 0.1},
                {0.1, 0.1, 0.3, 0.9}
        });

        // Obiekt reprezentujący identyczny podział rozmyty. Porównanie
        // powinno zwrócić wynik pozytywny.
        FuzzyPartition partitionEqual = new FuzzyPartition(new double[][]{
                {0.5, 0.7, 0.3, 0.0},
                {0.4, 0.2, 0.4, 0.1},
                {0.1, 0.1, 0.3, 0.9}
        });
        assertEquals(
                partitionEqual,
                partition
        );

        // Obiekt reprezentujący inny podział rozmyty. Porównanie powinno
        // zwrócić wynik negatywny.
        FuzzyPartition partitionOther = new FuzzyPartition(new double[][]{
                {0.5, 0.7, 0.3, 0.0},
                {0.4, 0.2, 0.4, 0.2},
                {0.1, 0.1, 0.3, 0.8}
        });
        assertNotEquals(
                partitionOther,
                partition
        );

        // Obiekt reprezentujący podział rozmyty różniący się nieznacznie
        // modelu. Porównanie powinno zwrócić wynik pozytywny tylko przy
        // dostatecznie dużym marginesie błędu.
        FuzzyPartition partitionSimilar = new FuzzyPartition(new double[][]{
                {0.5, 0.7, 0.3, 0.0},
                {0.4, 0.2, 0.4, 0.0999},
                {0.1, 0.1, 0.3, 0.9001}
        });

        FuzzyPartition.setEpsilon(0.01);
        assertEquals(
                partitionSimilar,
                partition
        );

        FuzzyPartition.setEpsilon(EPSILON);
        assertNotEquals(
                partitionSimilar,
                partition
        );
    }
}