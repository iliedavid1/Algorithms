import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

class Registre extends Task{
    int N, M, K;
    int [][]graph;
    String formula;
    String ans = "";

    public static void main(String[] args) throws IOException, InterruptedException {
        Registre solver = new Registre();

        solver.solve();
    }

    @Override
    public void solve() throws IOException, InterruptedException {

        readProblemData();

        formulateOracleQuestion();

        File myFile = new File("sat.cnf");
        myFile.createNewFile();

        FileWriter writer = new FileWriter("sat.cnf");
        writer.write(formula);
        writer.close();

        askOracle();

        decipherOracleAnswer();

        writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        int u, v;
        Scanner in = new Scanner(System.in);

        N = in.nextInt();
        M = in.nextInt();
        K = in.nextInt();

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

        /* Caut variabilele care au legatura intre ele si verific
         * daca sunt asignate in acelasi registru
         */
        for (int i = 0; i < N - 1; i++) {
            int firstIndex = -i - 1;
            for (int j = i + 1; j < N; j++) {
                int secondIndex = -j - 1;
                if (graph[i][j] == 1) {
                    for (int ii = 0; ii < K; ii++) {
                        int aux1 = firstIndex - ii * N;
                        int aux2 = secondIndex - ii * N;
                        sb.append(aux1).append(" ").append(aux2).append(" 0\n");
                        nr++;
                    }
                }
            }
        }

        /* Verific daca o variabila este pusa in mai multi registri
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

        /* Verific daca fiecare variabila este pusa pe un registru
         */
        for (int i = 0; i < N; i++) {
            int index = i + 1;
            for (int j = 0; j < K; j++) {
                int aux = index + j * N;
                sb.append(aux).append(" ");
            }
            sb.append("0\n");
            nr++;
        }

        sb.insert(0, "p cnf " + N * K + " " + nr + "\n");
        formula = sb.toString();
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        File answer = new File("sat.sol");
        Scanner scanner = new Scanner(answer);
        StringBuilder ansBuilder = new StringBuilder();

        if(scanner.nextLine().equals("True")) {
            ansBuilder.append("True\n");
            int n = scanner.nextInt();
            int []output = new int[n];

            for (int i = 0; i < n; i++) {
                output[i] = scanner.nextInt();
            }

            /* iau, pe rand, fiecare variabila si gasesc
             * registru in care este pusa
             */
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < K; j++) {
                    if (output[i + j * N] > 0) {
                        int x;
                        if (output[i + j * N] % N == 0) {
                            x = output[i + j * N] / N - 1;
                        } else {
                            x = output[i + j * N] / N;
                        }

                        ansBuilder.append(x + 1).append(" ");
                    }
                }
            }
        } else {
            ansBuilder.append("False");
            ans = ansBuilder.toString();
            return;
        }
        ans = ansBuilder.toString();
    }

    @Override
    public void writeAnswer() throws IOException {
        System.out.println(ans);
    }
}