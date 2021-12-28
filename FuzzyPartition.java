import java.util.Arrays;
import java.util.Random;

/**
 * Klasa reprezentująca podział rozmyty i udostępniająca operacje, które
 * można na nim wykonywać.
 */
public class FuzzyPartition {

    /**
     * Liczba wierszy w macierzy reprezentującej podział rozmyty
     */
    public final int M;

    /**
     * Liczba kolumn w macierzy reprezentującej podział rozmyty
     */
    public final int N;

    /**
     * Macierz reprezentująca podział rozmyty.
     */
    public final double[][] data;

    /**
     * Dopuszczalny zakres błędu dla metod 'validate' oraz 'equals'.
     */
    static double epsilon = 0.00000001;

    /**
     * Konstruktor zwracający podział rozmyty o podanych wartościach.
     * Konstruktor ten nie sprawdza poprawności wpisywanych danych.
     *
     * @param data Macierz, na podstawie której tworzony jest podział
     *             rozmyty.
     */
    public FuzzyPartition(double[][] data) {
        M = data.length;
        N = data[0].length;

        this.data = new double[M][N];
        for (int i = 0; i < M; i++)
            System.arraycopy(data[i], 0, this.data[i], 0, N);
    }

    /**
     * Konstruktor zwracający podział rozmyty rozmiaru MxN o losowych
     * wartościach. Losowość otrzymana jest w taki sposób, że każda
     * z wartości losowana jest z rozkładu jednostajnego na przedziale
     * [0,1], a następnie wartości te są przeskalowane w taki sposób, aby
     * kolumny sumowały się do 1.
     *
     * @param M Liczba wierszy
     * @param N Liczba kolumn
     */
    public FuzzyPartition(int M, int N) {
        this.M = M;
        this.N = N;
        this.data = new double[M][N];

        double[] sum = new double[N];
        Arrays.fill(sum, 0.0);

        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                data[j][i] = random.nextDouble();
                sum[i] += data[j][i];
            }
        }

        // Przeskalujemy otrzymane losowe wartości w taki sposób, aby
        // kolumny sumowały się do 1.
        for (int i = 0; i < M; i++)
            for (int j = 0; j < N; j++)
                this.data[i][j] /= sum[j];
    }

    /**
     * Metoda zwracająca nowy podział rozmyty, będący alfa-cięciem bieżącego
     * podziału.
     *
     * @param alpha poziom, dla którego tworzymy alfa-cięcie
     * @return Nowy podział rozmyty, będący alfa-cięciem bieżącego
     */
    public FuzzyPartition calculateAlphaLevel(double alpha) {
        if (alpha <= 0.0) throw new IllegalArgumentException(
                "Value of alpha must be bigger than zero"
        );
        double[][] newData = new double[M][N];
        for (int j = 0; j < N; j++) {
            int cj = 0;
            double max = 0.0;
            for (int i = 0; i < M; i++) {
                if (data[i][j] >= alpha) cj++;
                if (data[i][j] >= max) max = data[i][j];
            }
            if (max <= alpha) {
                throw new IllegalArgumentException(
                        "Value of alpha must be lower than maximum " +
                                "value in any column"
                );
            }
            for (int i = 0; i < M; i++) {
                if (data[i][j] >= alpha) newData[i][j] = 1.0 / cj;
            }
        }
        return new FuzzyPartition(newData);
    }

    /**
     * Metoda zwracająca nowy podział rozmyty, będący dopełnieniem
     * alfa-cięcia bieżącego podziału.
     *
     * @param alpha poziom, dla którego tworzymy alfa-cięcie
     * @return Nowy podział rozmyty, będący alfa-cięciem bieżącego
     */
    public FuzzyPartition calculateComplementAlphaLevel(double alpha) {
        if (alpha <= 0.0) throw new IllegalArgumentException(
                "Value of alpha must be bigger than zero"
        );
        double[][] newData = new double[M][N];
        for (int j = 0; j < N; j++) {
            int cj = 0;
            double max = 0.0;

            for (int i = 0; i < M; i++) {
                if (data[i][j] >= alpha) cj++;
                if (data[i][j] >= max) max = data[i][j];
            }
            if (max <= alpha) {
                throw new IllegalArgumentException(
                        "Value of alpha must be lower than maximum " +
                                "value in any column"
                );
            }
            for (int i = 0; i < M; i++) {
                if (cj == M) {
                    newData[i][j] = 1.0 / M;
                } else if (cj < M && data[i][j] < alpha) {
                    newData[i][j] = 1.0 / (M - cj);
                }
            }
        }
        return new FuzzyPartition(newData);
    }

    /**
     * Metoda sprawdzająca podobieństwo alfa-cięć podziału bieżącego
     * i podziału podanego jako argument V. Jeżeli podobieństwo to wynosi 1,
     * oznacza to, że podziały są alfa-równoważne.
     *
     * @param alpha zadany poziom alfa
     * @param V     Podział rozmyty, do którego porównujemy zbiór bieżący
     * @return Podobieństwo pomiędzy alfa-cięciami podziałów w skali od
     * 0 do 1
     */
    public double alphaApproximate(double alpha, FuzzyPartition V) {
        double M1 = 0.0;
        double M2 = 0.0;
        int cardM1 = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (data[j][i] >= alpha) {
                    cardM1++;
                    M1 += Math.max(0.0, alpha - V.data[j][i]);
                } else {
                    M2 += Math.max(0.0, V.data[j][i] - alpha);
                }

            }
        }
        return 1.0 - ((M1 + M2)
                / (cardM1 * alpha + (M * N - cardM1) * (1 - alpha)));
    }

    /**
     * Prywatna metoda do obliczania liniowego wyostrzenia bieżącego
     * podziału lub jego dopełnienia.
     *
     * @param complement Przy pomocy tego parametru ustawiamy czy chcemy
     *                   obliczyć liniowe wyostrzenie, czy jego dopełnienie
     * @return Podział rozmyty będący liniowym wyostrzeniem bieżącego
     * podziału lub jego dopełnienie
     */
    private FuzzyPartition calculateLSOrComplementLS(boolean complement) {
        double max = 0.0;
        double min = 1.0;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (max < data[j][i]) max = data[j][i];
                if (min > data[j][i]) min = data[j][i];
            }
        }

        double[][] result = new double[M][N];
        if (max == min) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    result[j][i] = 1.0 / M;
                }
            }
        } else {
            double minOrMax = complement ? max : min;
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    result[j][i] = 1.0 / M + (this.data[j][i] - 1.0 / M)
                            / (1 - M * minOrMax);
                }
            }
        }

        return new FuzzyPartition(result);
    }

    /**
     * Metoda zwracająca podział rozmyty będący liniowym wyostrzeniem
     * bieżącego podziału.
     *
     * @return Podział rozmyty będący liniowym wyostrzeniem bieżącego
     * podziału
     */
    public FuzzyPartition calculateLS() {
        return calculateLSOrComplementLS(false);
    }

    /**
     * Metoda zwracająca podział rozmyty będący dopełnieniem liniowego
     * wyostrzenia bieżącego podziału.
     *
     * @return Podział rozmyty będący dopełnieniem liniowego wyostrzenia
     * bieżącego podziału
     */
    public FuzzyPartition calculateComplementLS() {
        return calculateLSOrComplementLS(true);
    }

    /**
     * Prywatna metoda do obliczania MLS bieżącego podziału lub jego
     * dopełnienia.
     *
     * @param complement Przy pomocy tego parametru ustawiamy czy chcemy
     *                   obliczyć MLS, czy jego dopełnienie
     * @return Podział rozmyty będący MLS bieżącego podziału lub jego
     * dopełnienie
     */
    private FuzzyPartition calculateMLSOrComplementMLS(boolean complement) {
        double[] max = new double[N];
        double[] min = new double[N];
        Arrays.fill(max, 0.0);
        Arrays.fill(min, 1.0);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (max[i] < data[j][i]) max[i] = data[j][i];
                if (min[i] > data[j][i]) min[i] = data[j][i];
            }
        }

        double[][] result = new double[M][N];
        for (int i = 0; i < N; i++) {
            double minOrMax = complement ? max[i] : min[i];
            if (max[i] == min[i]) {
                for (int j = 0; j < M; j++) {
                    result[j][i] = 1.0 / M;
                }
            } else {
                for (int j = 0; j < M; j++) {
                    result[j][i] = 1.0 / M + (this.data[j][i] - 1.0 / M)
                            / (1 - M * minOrMax);
                }
            }
        }

        return new FuzzyPartition(result);
    }

    /**
     * Metoda zwracająca podział rozmyty będący MLS bieżącego podziału.
     *
     * @return Podział rozmyty będący MLS bieżącego podziału
     */
    public FuzzyPartition calculateMLS() {
        return calculateMLSOrComplementMLS(false);
    }

    /**
     * Metoda zwracająca podział rozmyty będący dopełnieniem MLS bieżącego
     * podziału.
     *
     * @return Podział rozmyty będący dopełnieniem MLS bieżącego podziału
     */
    public FuzzyPartition calculateComplementMLS() {
        return calculateMLSOrComplementMLS(true);
    }

    /**
     * Metoda zwracająca w jakim stopniu zbiór V, jest wyostrzeniem
     * bieżącego zbioru.
     *
     * @param V zbiór, do którego się porównujemy
     * @return Stopień, w jakim zbiór V, jest wyostrzeniem bieżącego zbioru.
     */
    public double calculateSharpnessDegree(FuzzyPartition V) {
        double K1 = 0.0;
        double K2 = 0.0;
        for (int j = 0; j < N; j++) {
            for (int i = 0; i < M; i++) {
                if (data[i][j] >= 1.0 / M) {
                    K1 += Math.max(0.0, this.data[i][j] - V.data[i][j]);
                } else if (data[i][j] <= 1.0 / M) {
                    K2 += Math.max(0.0, V.data[i][j] - this.data[i][j]);
                }

            }
        }
        return 1 - (K1 + K2) / (2 * N);
    }

    /**
     * Metoda ta zwraca podział rozmyty będący dopełnieniem bieżącego
     * podziału.
     *
     * @return Podział rozmyty będący dopełnieniem bieżącego podziału
     */
    public FuzzyPartition complement() {
        double[] max = new double[N];
        double[] min = new double[N];
        double[] lambda = new double[N];

        Arrays.fill(max, 0.0);
        Arrays.fill(min, 1.0);

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (max[i] < data[j][i]) max[i] = data[j][i];
                if (min[i] > data[j][i]) min[i] = data[j][i];
            }
            if (max[i] == min[i]) lambda[i] = 0.0;
            else lambda[i] = M * (max[i] - min[i]) / (1 - M * min[i]);
        }

        double[][] result = new double[M][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                result[j][i] = (this.data[j][i] - lambda[i] / M)
                        / (1 - lambda[i]);
            }
        }

        return new FuzzyPartition(result);
    }

    /**
     * Zwraca zmienną typu String reprezentującą podział rozmyty w formie
     * macierzy o wymiarach MxN.
     *
     * @return String reprezentujący podział rozmyty.
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                result.append(String.format("%9.4f ", data[i][j]));
            }
            // Nowa linia po każdym wierszu z wyjątkiem ostatniego.
            if (i != M - 1) {
                result.append(System.lineSeparator());
            }
        }
        return result.toString();
    }


    /**
     * Metoda pozwalająca zmienić margines błędu dla metod 'validate' oraz
     * 'equals'.
     *
     * @param newEpsilon Nowa wartość epsilon.
     */
    public static void setEpsilon(double newEpsilon) {
        epsilon = newEpsilon;
    }

    /**
     * Metoda sprawdzająca, czy wprowadzone dane spełniają założenia
     * podziału rozmytego. Każda z wartości w macierzy musi być z przedziału
     * [0,1], oraz kolumny muszą sumować się do 1. W związku
     * z niedokładnością reprezentacji zmiennoprzecinkowej funkcja
     * pozwala na drobną niedokładność, nie większą niż zadany epsilon.
     *
     * @return true, jeżeli dane spełniają założenia podziału rozmytego
     */
    public boolean validate() {

        for (int i = 0; i < N; i++) {
            double sum = 0.0;
            for (int j = 0; j < M; j++) {
                if (data[j][i] + epsilon < 0) return false;
                else if (data[j][i] - epsilon > 1) return false;
                else sum += data[j][i];
            }
            if (Math.abs(1.0 - sum) > epsilon) return false;
        }

        return true;
    }

    /**
     * Metoda sprawdzająca, czy dwa obiekty reprezentują ten sam podział
     * rozmyty. Ze względu na niedokładność reprezentacji liczby
     * zmiennoprzecinkowej, dopuszczalne są drobne różnice nie większe niż
     * zadany epsilon.
     *
     * @param o Obiekt, do którego się porównujemy
     * @return true, jeżeli obiekty reprezentują ten sam podział rozmyty
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FuzzyPartition that = (FuzzyPartition) o;
        if (M == that.M && N == that.N) {
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    if (Math.abs(data[j][i] - that.data[j][i]) > epsilon)
                        return false;
                }
            }
            return true;
        } else return false;
    }

}
