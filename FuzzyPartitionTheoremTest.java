import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Klasa testująca teorię związaną z podziałami rozmytymi
 */
class FuzzyPartitionTheoremTest {
    
    /**
     * Podziały rozmyte, które będą poddane testom.
     */
    public static FuzzyPartition[] testSet;

    /**
     * Ustawienie dokładności, z jaką porównujemy liczby zmiennoprzecinkowe
     * w klasie FuzzyPartition. Utworzenie różnych podziałów rozmytych,
     * które będą poddane testom.
     */
    @BeforeAll
    static void initializeTest() {
        // Ustawienie epsilon
        FuzzyPartition.setEpsilon(0.00000001);

        // Utworzenie zbioru testowego
        testSet = new FuzzyPartition[1 + 1 + 1 + 1000 + 100];
        testSet[0] = new FuzzyPartition(new double[][]{
                {1.0}
        });
        testSet[1] = new FuzzyPartition(new double[][]{
                {1.0, 0.0, 0.0},
                {0.0, 1.0, 0.0},
                {0.0, 0.0, 1.0}
        });
        testSet[2] = new FuzzyPartition(new double[][]{
                {0.25, 0.25, 0.25},
                {0.25, 0.25, 0.25},
                {0.25, 0.25, 0.25},
                {0.25, 0.25, 0.25}
        });
        for (int i = 3; i < 1003; i++) {
            testSet[i] = new FuzzyPartition(5, 5);
        }
        for (int i = 1003; i < 1103; i++) {
            testSet[i] = new FuzzyPartition(100, 100);
        }
    }

    /**
     * Zgodnie z twierdzeniem dopełnienie dopełnienia podziału rozmytego
     * powinno być równe temu samemu podziałowi. W tym teście sprawdzimy,
     * czy twierdzenie to zachodzi dla naszego testowego zbioru podziałów
     * rozmytych.
     */
    @Test
    @DisplayName("Test twierdzenia o dopełnieniu dopełnienia")
    void testUComplementComplement() {

        for (FuzzyPartition U : testSet) {
            assertEquals(
                    U,
                    U.complement().complement()
            );
        }
    }

    /**
     * Zgodnie z twierdzeniem o relacjach MLS z dopełnieniem prawdziwe są
     * następujące równości:
     * (>>U)^c = >>(U^c) = <<U
     * W tym teście sprawdzimy, czy twierdzenie to zachodzi dla naszego
     * testowego zbioru podziałów rozmytych.
     */
    @Test
    @DisplayName("Test twierdzenia o relacji MLS z dopełnieniem")
    void testUComplementMLSRelation() {
        for (FuzzyPartition U : testSet) {

            // >>(U^c) = (>>U)^c
            assertEquals(
                    U.complement().calculateMLS(),
                    U.calculateMLS().complement()
            );

            // >>(U^c) = <<U"
            assertEquals(
                    U.complement().calculateMLS(),
                    U.calculateComplementMLS()
            );

            // (>>U)^c = <<U"
            assertEquals(
                    U.calculateMLS().complement(),
                    U.calculateComplementMLS()
            );

        }
    }

    /**
     * Zgodnie z wnioskiem z twierdzenia o relacjach MLS z dopełnieniem
     * prawdziwe są następujące równości:
     * (<<U)^c = <<(U^c) = >>U.
     * W tym teście sprawdzimy, czy wniosek ten zachodzi dla naszego
     * testowego zbioru podziałów rozmytych.
     */
    @Test
    @DisplayName("Test wniosku z twierdzenia o relacji MLS z dopełnieniem")
    void testUComplementMLSRelationCorollary() {
        for (FuzzyPartition U : testSet) {

            // <<(U^c) = (<<U)^c
            assertEquals(
                    U.complement().calculateComplementMLS(),
                    U.calculateComplementMLS().complement()
            );

            // <<(U^c) = >>U"
            assertEquals(
                    U.complement().calculateComplementMLS(),
                    U.calculateMLS()
            );

            // (<<U)^c = >>U"
            assertEquals(
                    U.calculateComplementMLS().complement(),
                    U.calculateMLS()
            );
        }
    }

    /**
     * Test sprawdzający relacje alpha-cięcia, dopełnienia tego alpha-cięcia
     * oraz ich odpowiednich dopełnień. Tzn. sprawdzamy, czy
     * 1. (U^a)^c = ~U^a
     * 2. (~U^a)^c = U^a
     */
    @Test
    @DisplayName("Test relacji alpha-cięć z dopełnieniem")
    void testUComplementAlphaLevelRelation() {
        for (FuzzyPartition U : testSet) {

            // (U^a)^c = ~U^a
            assertEquals(
                    U.calculateAlphaLevel(0.005).complement(),
                    U.calculateComplementAlphaLevel(0.005)
            );

            //(~U^a)^c = U^a
            assertEquals(
                    U.calculateComplementAlphaLevel(0.005).complement(),
                    U.calculateAlphaLevel(0.005)
            );
        }
    }
}