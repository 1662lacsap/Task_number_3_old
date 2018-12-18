/*
        Zadanie 3
        Podlancuch Alfa nazywamy powtorzonym prefiksem lancucha S, jesli Alfa jest prefiksem
        S oraz ma on postac BetaBeta dla pewnego lancucha Beta. Zaproponuj i zaimplementuj
        liniowy algorytm znajdujacy najdluzszy powtorzony prefiks dla zadanego lancucha S.
        */

//Definicje:
//s - tekst, ciąg symboli s = s1,s2,...sn nalezacych do alfabetu
//n - Dlugosc tekstu (liczba jego elementów)
//p - pattern (wzorzec)

import java.util.*;


public class SuffixTree {

    private static CharSequence s = "";

    public static class Node {

        int begin;
        int end;
        int depth; //distance in characters from root to this node
        Node parent;
        Node suffixLink;

        Map<Character, Node> children;  //zamiast Node[] children

        //Sluzy do wyznaczenia liczby osiagalnych wierzcholkow reprezentujacych sufiksy dla kazdego wezla
        // drzewa sufiksowego
        int numberOfLeaves;             //zliczamy liscie

        Node(int begin, int end, int depth, int noleaf, Node parent) {

            this.begin = begin;
            this.end = end;
            this.depth = depth;
            this.parent = parent;

            children = new HashMap<>();
            numberOfLeaves = noleaf;


        }
    }

    private static Node buildSuffixTree(CharSequence s) {

        SuffixTree.s = s;

        int n = s.length();

        Node root = new Node(0, 0, 0, 0, null);
        Node node = root;

        for (int i = 0, tail = 0; i < n; i++, tail++) {

            //ustaw ostatni stworzony wezel wewnętrzny na null przed rozpoczeciem kazdej fazy.
            Node last = null;

            while (tail >= 0) {
                Node ch = node.children.get(s.charAt(i - tail));
                while (ch != null && tail >= ch.end - ch.begin) {

                    //liscie
                    node.numberOfLeaves++;

                    tail -= ch.end - ch.begin;
                    node = ch;
                    ch = ch.children.get(s.charAt(i - tail));
                }

                if (ch == null) {
                    // utworz nowy Node z biezacym znakiem
                    node.children.put(s.charAt(i),
                            new Node(i, n, node.depth + node.end - node.begin, 1, node));

                    //liscie
                    node.numberOfLeaves++;

                    if (last != null) {
                        last.suffixLink = node;
                    }
                    last = null;
                } else {
                    char t = s.charAt(ch.begin + tail);
                    if (t == s.charAt(i)) {
                        if (last != null) {
                            last.suffixLink = node;
                        }
                        break;
                    } else {
                        Node splitNode = new Node(ch.begin, ch.begin + tail,
                                node.depth + node.end - node.begin, 0, node);
                        splitNode.children.put(s.charAt(i),
                                new Node(i, n, ch.depth + tail, 1, splitNode));

                        //liscie
                        splitNode.numberOfLeaves++;

                        splitNode.children.put(t, ch);

                        //liscie
                        splitNode.numberOfLeaves += ch.numberOfLeaves;

                        ch.begin += tail;
                        ch.depth += tail;
                        ch.parent = splitNode;
                        node.children.put(s.charAt(i - tail), splitNode);

                        //liscie
                        node.numberOfLeaves++;

                        if (last != null) {
                            last.suffixLink = splitNode;
                        }
                        last = splitNode;
                    }
                }
                if (node == root) {
                    --tail;
                } else {
                    node = node.suffixLink;
                }
            }
        }
        return root;
    }


    private static void print(CharSequence s, int i, int j) {
        for (int k = i; k < j; k++) {
            System.out.print(s.charAt(k));
        }
    }

    //Jesli chcemy wydrukowac drzewo nalezy odkomentowac w main
    private static void printTree(Node n, CharSequence s, int spaces) {
        int i;
        for (i = 0; i < spaces; i++) {
            System.out.print("␣");
        }
        print(s, n.begin, n.end);
        System.out.println("␣" + (n.depth + n.end - n.begin)+ " leaf: " + n.numberOfLeaves);

        for (Node child : n.children.values()) {
            if (child != null) {
                printTree(child, s, spaces + 4);
            }
        }

    }

    /*##########################################################################################*/

    //Przechodząc od pierwszej litery do liscia najdluzszego Suffix-u.
    // Ograniczamy s (text) dzieki (subSequence) od początku do głebokosci jaką ma ten lisc

    //Wyznaczamy w drzewie najglebszy wierzcholek(lisc) (najbardziej oddalony od korzenia)
    private static CharSequence depthLeafLongestSuffix(Node root) {
        Node actualNode = root;

        int index_s = 0;
        while (index_s < s.length()) {
            actualNode = actualNode.children.get(s.charAt(index_s));
            index_s = actualNode.end;
        }
        return s.subSequence(0, actualNode.depth);
    }

    /*##########################################################################################*/

    // 1. Iterujemy po wyzej znalezionym tekscie od poczatku do polowy tekstu i sprawdzamy
    // czy te znaki pokrywaja sie z tymi od polowy do konca

    // 2. w  przypadku braku pokrywania zostaje usuniety ostatni znak

    // 3. Powtarzamy az skonczy sie tekst i nie znajdziemy powtorzonego prefiksu
    // lub nie bedzie trzeba usuwac ostatniego znaku, bo znalezlismy powtorzony prefiks

    // 4. Zwracamy ten najdluzszy powtorzony prefiks -  subSequence(0, Lenght_dLLS * 2)


    private static CharSequence Search_depthLeafLongestSuffix(CharSequence s, CharSequence dLLS) {

        int Lenght_dLLS = dLLS.length();

        do {

            dLLS = dLLS.subSequence(0, Lenght_dLLS);

            for (int dLLSIndex = 0, sIndex = Lenght_dLLS; sIndex < dLLS.length() * 2; sIndex++, dLLSIndex++)
            {
                if (dLLS.charAt(dLLSIndex) != s.charAt(sIndex))

                {
                    Lenght_dLLS--;
                    break;
                }
            }

        } while
        (Lenght_dLLS != dLLS.length());

        return s.subSequence(0, Lenght_dLLS * 2);

    }

    /*##########################################################################################*/

    //main - Test
    public static void main(String[] args) {

        // 1 Test wynik to AniaAnia a nie AniaAniaAnia
        String s = "AniaAniazrobilemAniaAniaAniahuraAniaAniaAnia$";


        Node root = buildSuffixTree(s);

        //Jesli chcemy wydrukowac drzewo nalezy odkomentowac
        //printTree(root, s, 0);

        CharSequence depthLLS = depthLeafLongestSuffix(root);
        System.out.println(" ");
        System.out.println("Najdluzszy powtorzony prefiks dla tekstu s = " + s + " to: ");
        System.out.println(" ");



        System.out.println(Search_depthLeafLongestSuffix(s, depthLLS));
        // koniec testu





        // 2 Skaner do testow
        System.out.println(" ");
        System.out.println("Sprawdz inne lancuchy s");
        System.out.println("Podaj lancuch s w ktorym znajdziemy najdluzszy powtorzony prefiks, " +
                "wynik pusty oznacza brak powtarzajacego się prefiksu: ");

        Scanner lancuch_s = new Scanner(System.in); //obiekt do odebrania danych od użytkownika
        String s1 = lancuch_s.nextLine()+"$";


        Node root1 = buildSuffixTree(s1);

        //Jesli chcemy wydrukowac drzewo nalezy odkomentowac
        //printTree(root1, s1, 0);

        CharSequence depthLLS1 = depthLeafLongestSuffix(root1);
        System.out.println(" ");
        System.out.println("Najdluzszy powtorzony prefiks dla tekstu s = " + s1 + " to: ");
        System.out.println(" ");


        System.out.println(Search_depthLeafLongestSuffix(s1, depthLLS1));



    }

}

/*
Wyjaśnienie:
"Pokażę to na przykładzie słowa s = ababa$. W utworzonym
drzewie sufiksowym mamy dziesięć wierzchołków od q0 do q9.
Liśćmi są: q2, q6, q1, q4, q8 i q9. Korzeniem jest q0.
Krawędzie wraz z etykietami:
(q0, q5): ba
(q0, q7): a
(q0, q9): $
(q3, q1): ba$
(q3, q4): $
(q5, q2): ba$
(q5, q6): $
(q7, q3): ba
(q7, q8): $

W każdym liściu zapamiętujemy indeks początku całego
sufiksu: q2: 1, q6: 3, q1: 0 (czyli q1 jest wierzchołkiem
od którego za chwilę będziemy startowali i pójdziemy
w drzewie w górę), q4: 2, q8: 4, q9: 5.

Tworzymy tablicę o długości równej długości całego tekstu,
w naszym przypadku t[0..5]. Tablica ta - początkowo wypełniona
samymi zerami - będzie reprezentowała zbiór osiągalnych w danej
chwili liści. Jesteśmy w q1, wpisujemy t[0] := 1.
Analizujemy krawędź (q3, q1), której etykieta reprezentuje
podłańcuch s[3..5]. Analiza ta polega na tym, że sprawdzamy
tablicę t od indeksu 5 do indeksu 3 w poszukiwaniu jedynki.
Indeks pierwszej napotkanej jedynki jest długością prefiksu,
który został w łańcuchu s powtórzony. Ale od 5 do 3 nie ma
jedynki, więc bieżącym wierzchołkiem staje się q3. Przeglądamy
poddrzewo którego korzeniem jest q3 (wyłączając z przeglądu to,
co już odwiedziliśmy czyli q1 i jego ew. potomków) w celu
odwiedzenia każdego liścia. Gdy natrafiamy na liścia - w naszym
przypadku będzie to tylko q4 - to do tablicy t pod indeks,
który został tam zapamiętany - czyli 2 - wpisujemy jedynkę:
t[2] := 1. Idąc w górę analizujemy teraz krawędź (q7, q3),
której etykieta reprezentuje podłańcuch s[1..2]. W tablicy
t idąc od 2 do 1 natrafiamy na jedynkę pod indeksem 2, co
oznacza, że prefiks, który został powtórzony ma długość 2."

 */