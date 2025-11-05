import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;
import org.tartarus.snowball.ext.romanianStemmer;
import cz.cuni.mff.ufal.udpipe.Model;
import cz.cuni.mff.ufal.udpipe.Pipeline;

public class FirClient extends Thread {

    private Socket cs;
    private BufferedReader bfr;
    private PrintWriter pw;
    private static Model model;
    private static Pipeline pipeline;
    public static List<String> listaCuvinte = Collections.synchronizedList(new ArrayList<>());

    // Lista cuvintelor distincte pentru fiecare client


    public FirClient(Socket clientsock) {
        try {
            cs = clientsock;
            pw = new PrintWriter(cs.getOutputStream());
            bfr = new BufferedReader(new InputStreamReader(cs.getInputStream()));
            String modelPath = "resources/romanian-ud-1.2-160523.udpipe";
            model = Model.load(modelPath);
            pipeline = new Pipeline(
                    model,               // modelul încărcat
                    "tokenize",          // tokenizare
                    Pipeline.getDEFAULT(), // tagger
                    Pipeline.getDEFAULT(), // parser
                    "conllu"             // output format
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String textIn;
        try {
            if (!listaCuvinte.isEmpty()) {
                pw.println("Cuvinte existente pe server (" + listaCuvinte.size() + "):");
                pw.println(String.join(", ", listaCuvinte));
                pw.println("<END>");
                pw.flush();
            } else {
                pw.println("Lista de cuvinte este momentan goală.");
                pw.println("<END>");
                pw.flush();
            }
            for (;;) {
                textIn = bfr.readLine();
                if (textIn == null || textIn.equals("")) break;

                textIn = textIn.trim();

                // --- 1️⃣ Cazul: Căutare cuvânt ---
                if (textIn.toLowerCase().startsWith("cauta ")) {
                    String cuvant = textIn.substring(6).trim().toLowerCase();
                    if (listaCuvinte.contains(cuvant)) {
                        pw.println("Cuvantul \"" + cuvant + "\" exista in lista.");
                    } else {
                        pw.println("Cuvantul \"" + cuvant + "\" NU exista in lista.");
                    }
                    pw.println("<END>");
                    pw.flush();
                    continue;
                }

                // --- 2️⃣ Cazul: Adăugare cuvânt ---
                if (textIn.toLowerCase().startsWith("adauga ")) {
                    String cuvant = textIn.substring(7).trim().toLowerCase();
                    if (listaCuvinte.contains(cuvant)) {
                        pw.println("Cuvantul \"" + cuvant + "\" exista deja in lista.");
                    } else {
                        listaCuvinte.add(cuvant);
                        Collections.sort(listaCuvinte);
                        pw.println("Cuvantul \"" + cuvant + "\" a fost adaugat.");

                    }
                    pw.println(String.join(", ", listaCuvinte));
                    pw.println("<END>");
                    pw.flush();
                    continue;
                }

                // --- Cazul 3:
                if (textIn.toLowerCase().startsWith("sterge ")) {
                    String cuvant = textIn.substring(7).trim().toLowerCase();
                    if (listaCuvinte.remove(cuvant)) {
                        pw.println("Cuvantul \"" + cuvant + "\" a fost sters din lista.");
                    } else {
                        pw.println("Cuvantul \"" + cuvant + "\" nu exista in lista.");
                    }
                    pw.println(String.join(", ", listaCuvinte));
                    pw.println("<END>");
                    pw.flush();
                    continue;
                }

                //Cazul 4:
                if (textIn.toLowerCase().startsWith("modifica ")) {
                    String[] parti = textIn.substring(9).trim().toLowerCase().split("\\s+", 2);
                    if (parti.length < 2) {
                        pw.println("Format invalid. Foloseste: modifica <vechiul> <noul>");
                    } else {
                        String vechi = parti[0];
                        String nou = parti[1];
                        if (listaCuvinte.contains(vechi)) {
                            listaCuvinte.remove(vechi);
                            listaCuvinte.add(nou);
                            Collections.sort(listaCuvinte);
                            pw.println("Cuvantul \"" + vechi + "\" a fost inlocuit cu \"" + nou + "\".");
                        } else {
                            pw.println("Cuvantul \"" + vechi + "\" nu exista in lista.");
                        }
                    }
                    pw.println(String.join(", ", listaCuvinte));
                    pw.println("<END>");
                    pw.flush();
                    continue;
                }


                // --- 3️⃣ Cazul: Text normal, procesare ---
                List<String> distincte = proceseazaText(textIn);

                // Adaugăm noile cuvinte în lista globală
                for (String cuv : distincte) {
                    if (!listaCuvinte.contains(cuv)) {
                        listaCuvinte.add(cuv);
                    }
                }
                Collections.sort(listaCuvinte);


                // Obținem info despre client
                String clientInfo = cs.getInetAddress().getHostAddress() + ":" + cs.getPort();

                //Trimitem la server:
                System.out.println("Clientul " + clientInfo + " a adăugat:");
                System.out.println("Lista de cuvinte actualizată (" + listaCuvinte.size() + "):");
                System.out.println(String.join(", ", listaCuvinte));

                // Trimitem rezultatul clientului
                if (listaCuvinte.isEmpty()) {
                    pw.println("Nu au fost gasite cuvinte.");
                } else {
                    pw.println("Cuvinte distincte (" + listaCuvinte.size() + "):");
                    pw.println(String.join(", ", listaCuvinte));
                }

                pw.println("<END>");
                pw.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int levenshtein(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= s2.length(); j++) dp[0][j] = j;

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        return dp[s1.length()][s2.length()];
    }

    private List<String> unificaFormeSimilare(List<String> cuvinte) {
        List<String> rezultat = new ArrayList<>();
        boolean[] vizitat = new boolean[cuvinte.size()];
        int prag = 1; // distanță Levenshtein maximă

        for (int i = 0; i < cuvinte.size(); i++) {
            if (vizitat[i]) continue;

            String baza = cuvinte.get(i);
            List<String> grup = new ArrayList<>();
            grup.add(baza);
            vizitat[i] = true;

            for (int j = i + 1; j < cuvinte.size(); j++) {
                if (!vizitat[j] && levenshtein(baza, cuvinte.get(j)) <= prag) {
                    grup.add(cuvinte.get(j));
                    vizitat[j] = true;
                }
            }

            // alegem cea mai scurtă ca reprezentant (sau poți folosi cea mai frecventă)
            String reprezentant = grup.stream()
                    .min(Comparator.comparingInt(String::length))
                    .orElse(baza);

            rezultat.add(reprezentant);
        }

        return rezultat.stream().distinct().sorted().collect(Collectors.toList());
    }

    // --- Funcția de procesare a textului ---
    private List<String> proceseazaText(String text) {


        Set<String> distincte = new HashSet<>();
        romanianStemmer stemmer = new romanianStemmer(); // inițializat o singură dată
        try {
            String conllu = pipeline.process(text);
            String[] lines = conllu.split("\n");

            for (String line : lines) {
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\t");
                if (parts.length < 6) continue; // avem nevoie de POS și FEATURES

                String lemma = parts[2].toLowerCase().trim();
                String pos = parts[3];
                String feats = parts[5];

                //trebuie corectari personale, UDPIPE nu face bine, si stem cum UDPIPE nu le face bine, stemmer va gresi si el.
                //vom avea salvate intr-un fisier.
                if (lemma.equals("devremă")) lemma = "devreme";
                if (lemma.equals("ui")) lemma = "uita";
                if (lemma.equals("ved")) lemma = "vedea";

                // ignorăm stopwords, pronume, particule și AUX
                if (lemma.length() <= 1) continue;
                // vom face noi un vector cu aux si adp, fiindca uneori le ia gresit pos si nu ne putem baza pe asta
                if (pos.equals("PRON") || pos.equals("PART") || pos.equals("ADP") ||
                        pos.equals("CONJ") || pos.equals("SCONJ")  || pos.equals("AUX")  ||
                        pos.equals("ADV") || pos.equals("INTJ") || pos.equals("NUM") || pos.equals("DET"))  continue;
                // transformăm toate formele verbale și participiile la infinitiv
//                if (pos.equals("VERB") || pos.equals("NOUN") || feats.contains("VerbForm=Part") || feats.contains("VerbForm=Ger") || (feats.contains("VerbForm=Fin") && feats.contains("Number=Plur")) ) {
//                    stemmer.setCurrent(lemma);
//                    stemmer.stem();
//                    lemma = stemmer.getCurrent();
//                }

                distincte.add(lemma);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> listaFinala = new ArrayList<>(distincte);
        Collections.sort(listaFinala);

// Unificare fuzzy între forme similare (ex: merg ~ mers)
        listaFinala = unificaFormeSimilare(listaFinala);

        return listaFinala;
    }
}
