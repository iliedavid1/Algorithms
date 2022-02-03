import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class Reclame extends Task{
    int N, M;
    int K = 1;
    int [][]graph;
    String formula;
    String ans = "";

    public static void main(String[] args) throws IOException, InterruptedException {
        Reclame solver = new Reclame();

        solver.solve();
    }

    @Override
    public void solve() throws IOException, InterruptedException {

        readProblemData();

        File myFile = new File("sat.cnf");
        myFile.createNewFile();

        /* Rezolv problema pentru diferite K-uri,
         * K-ul de la 1 la N - 1, si iau K-ul minim pentru care
         * se rezolva problema
         */
        for (int i = 0; i < N - 1; i++) {
            formula = "";
            formulateOracleQuestion();

            FileWriter writer = new FileWriter("sat.cnf");
            writer.write(formula);
            writer.close();

            askOracle();

            File answer = new File("sat.sol");
            Scanner scanner = new Scanner(answer);

            if(scanner.nextLine().equals("True")) {
                break;
            }
            K++;
        }

        decipherOracleAnswer();

        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        int u, v;
        Scanner in = new Scanner(System.in);

        N = in.nextInt();
        M = in.nextInt();

        graph = new int[N][N];

        /* citesc legaturile din input si le pun in
         * in matricea graph
         */
        for (int i = 0; i < M; i++) {
            u = in.nextInt();
            v = in.nextInt();

            graph[u - 1][v - 1] = 1;
        }
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        StringBuilder sb = new StringBuilder();
        int nr = 0;

        /* Verific daca pentru fiecare pozitie din grupul de persoane
         * este asignata o persoana
         */
        for (int i = 0; i < K; i++) {
            for (int j = 0; j < N; j++) {
                int aux = i * N + j + 1;
                sb.append(aux).append(" ");
            }
            sb.append("0\n");
            nr++;
        }

        /* Verific daca este asignata o singura persoana pentru
         * fiecare index din grup
         */
        for (int i = 0; i < K; i++) {
            int aux = -i * N;
            for (int j = 0; j < N - 1; j++) {
                int firstIndex = -j - 1 + aux;
                for (int k = j + 1; k < N; k++) {
                    int secondIndex = -k - 1 + aux;
                    sb.append(firstIndex).append(" ").append(secondIndex).append(" 0\n");
                    nr++;
                }
            }
        }

        /* Verific daca fiecare persoana este asignata
         * pe o singura pozitie in grup
         */
        for (int i = 0; i < N; i++) {
            int index_var = -i - 1;
            for (int j = 0; j < K - 1; j++) {
                int aux = index_var - j * N;
                for (int k = j + 1; k < K; k++) {
                    int aux2 = index_var - k * N;
                    sb.append(aux).append(" ").append(aux2).append(" 0\n");
                    nr++;
                }
            }
        }

        /* Verific daca fiecare relatie are o persoana care
         * e asignata pe o pozitie din grup
         */
        for (int i = 0; i < N - 1; i++) {
            for (int j = i + 1; j < N; j++) {
                if (graph[i][j] == 1) {
                    int firstIndex = i + 1;
                    int secondIndex = j + 1;
                    for (int ii = 0; ii < K; ii++) {
                        int aux1 = firstIndex + ii * N;
                        int aux2 = secondIndex + ii * N;
                        sb.append(aux1).append(" ").append(aux2).append(" ");
                    }
                    nr++;
                    sb.append("0\n");
                }
            }
        }

        sb.insert(0, "p cnf " + N * K + " " + nr + "\n");
        formula = sb.toString();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        File answer = new File("sat.sol");
        Scanner scanner = new Scanner(answer);

        if(scanner.nextLine().equals("True")) {
            int n = scanner.nextInt();

            /* Caut numerele pozitive care imi indica
             * indexul persoanei din grup
             */
            for (int i = 0; i < n; i++) {
                int x = scanner.nextInt();
                if (x > 0) {
                    x = x % N;
                    if (x == 0) {
                        x = N;
                    }
                    ans = ans + x + " ";
                }
            }
        }
    }

    @Override
    public void writeAnswer() throws IOException {
        System.out.println(ans);
    }
}