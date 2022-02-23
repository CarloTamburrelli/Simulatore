import java.net.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


class Surface extends JPanel {
    int x1;
    int y1;
    int x2;
    int y2;
    int fl;
    int stamp;

    Surface(int x_1, int y_1, int x_2, int y_2) {
        x1 = x_1;
        y1 = y_1;
        x2 = x_2;
        y2 = y_2;
        fl = 0;
        stamp = 0;
    }
    private void doDrawing(Graphics g) {
        if (fl == 1) {
            g.setColor(Color.BLUE);
        }
        Graphics2D g2d = (Graphics2D) g;
        g2d.drawLine(x1, y1, x2, y2); //queste coordinate fanno riferimento al panel appena aggiunto !
    }

    @
    Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    public void impostatutto(int x_1, int y_1, int x_2, int y_2) {
        x1 = x_1;
        y1 = y_1;
        x2 = x_2;
        y2 = y_2;
    }
    public void flagg() {
        fl = 1 - fl; //not
    }
    public int get_flag() {
        return fl;
    }
    public void conn() {
        stamp = 1; //not
    }
    public int get_conn() {
        return stamp;
    }
}




class FrameConComponenti extends JFrame {

    private JButton btn;
    private JTextField txt;

    public FrameConComponenti(int sec, int nodi, int dim, int velox, int trasmiss) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        super("Simulatore"); // costruzione finestra

        // from a wave File
        /*
URL url = this.getClass().getClassLoader().getResource("fatto.wav");
AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
// from a URL
// can read from a disk file and also a file contained inside a JAR (used for distribution)
// recommended
Clip clip = AudioSystem.getClip();
clip.open(audioIn);
clip.start();  // play once     
*/
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400); // dimensionamento della finestra 
        setLocation(200, 100); // posizionamento della finestra

        JPanel pan = new JPanel(); //creazione pannello
        setVisible(true); // la finestra e il suo contenuto diventano visibili
        add(pan, BorderLayout.CENTER); // inserimento pannello nella finestra     
        pan.setLayout(null); // layout del pannello: posizionamento assoluto


        /* assumiamo che i nodi siano persone che camminano,
		e che vanno ad una velocità di 1 m/s (che in questa simulazione equivale ad un passo) 
		in questo modo abbiamo sia i secondi che i metri
		*/
        int tempo = sec;
        int n; //verra' azzerato nel while
        int i = 0; //contatore del tempo
        float cooX1; //coordinate iniziali di tutti i nodi
        float cooY1;
        String prendi = "";
        String[] parts = null;
        String[] nodes = new String[nodi]; //conterrà i percorsi di ogni nodo
        /*formato
		2;4;3;6;6;7;.... percorso del 1° nodo (X1;Y1;X2;Y2;X3;Y3...)
		1;3;4;5;8;5;.... percorso del 2° nodo
		............ percorso dell' n-esimo nodo
		*/
        String[] c_nodes = new String[nodi]; //conterrà i contatori dei passi associati ai percorsi dei nodi dell'array nodes
        /*formato
		5; contatore del 1° nodo (si riferisce alla quinta cella della stringa dell'array nodes associato a questa posizione)
		2; contatore del 2° nodo
		............ contatore dell' n-esimo nodo
		*/
        String[] discovery = new String[dim]; //conterrà le distanze fra tutti i nodi
        /*formato
		1;3;metri;  coppia 1
		2;4;metri;  coppia 2
		....        coppia i-esima 
		*/
        String[] d_ved = new String[nodi]; //indici da vedere per la discovery per ogni singolo nodo
        /* formato caso di 4 nodi
		;0;1;2;finish; cella1
		;3;4;finish;   cella2
		;5;finish;     cella3
		;finish; ....  cella4
		*/

        JPanel pan2 = new JPanel(); //creazione pannello

        BoxLayout layout = new BoxLayout(pan2, BoxLayout.Y_AXIS);
        pan2.setLayout(layout);
        Color h = new Color((int)(255 * Math.random()) + 1, (int)(255 * Math.random()) + 1, (int)(255 * Math.random()) + 1);
        pan2.setBackground(h);
        JScrollPane scrollPane = new JScrollPane(pan2);
        scrollPane.setBounds(1210, 20, 155, 675);
        pan.add(scrollPane);

        ArrayList < Surface > segment = new ArrayList < Surface > ();
        n = 0;
        while (n < dim) {
            Surface xox = new Surface(0, 0, 0, 0);
            xox.setBounds(0, 0, 1220, 750);
            xox.setLayout(null); // layout del pannello: posizionamento assoluto
            xox.setOpaque(false);
            pan.add(xox);
            segment.add(xox);
            n = n + 1;
        }
        /* per reimpostare la posizione del segmento bisogna chiamare questi due metodi 
		xox2.impostatutto((int)(((200*Math.random())+1)*6),(int)(((110*Math.random())+1)*6),(int)(((200*Math.random())+1)*6),(int)(((110*Math.random())+1)*6));
		xox2.repaint();
		*/
        n = 0;
        int s;
        i = 1;
        int k = 0;
        if (nodi == 2) {
            discovery[0] = "0;1;no;30;";
            discovery[1] = "-";
            d_ved[0] = ";0;finish;";
            d_ved[1] = ";finish;";
        } else if (nodi == 1) {
            d_ved[0] = ";finish;";
        } else {
            while (n < dim) {
                s = i;
                d_ved[k] = ";";
                while (s < nodi) {
                    discovery[n] = "" + k + ";" + s + ";no;30;"; //il "no" finale rappresenta l'instaurazione della discovery, se "yes" e' inizializzata altrimenti e' "no".
                    // il 30 finale e' un timer standard... (se 30 va a 0, la discovery viene completata!)
                    d_ved[k] = d_ved[k] + "" + n + ";";
                    n = n + 1;
                    s = s + 1;
                }
                d_ved[k] = d_ved[k] + "finish;";
                k = k + 1; //cambia nodo
                i = i + 1;
            }
            d_ved[k] = ";finish;";
        }
        i = 0;
        s = 0;
        k = 0;
        n = 0;
        i = 0;


        ArrayList < JLabel > labels = new ArrayList < JLabel > ();
        String xx = "" + 1250;
        String yy = "" + 690;
        JLabel ao = new JLabel("200 metri");
        ao.setBounds(200 * 6, Integer.parseInt(yy), 120, 20);
        pan.add(ao);
        ao = new JLabel("0 metri");
        ao.setBounds(0, Integer.parseInt(yy), 120, 20);
        pan.add(ao);
        ao = new JLabel("110 metri");
        ao.setBounds(0, 0, 120, 20);
        pan.add(ao);
        int metti = 20;
        while (metti <= Integer.parseInt(yy)) {
            ao = new JLabel("|");
            ao.setBounds(201 * 6, Integer.parseInt(yy) - metti, 120, 20);
            pan.add(ao);
            metti = metti + 20;
        }
        while (n < nodi) { //posiziona tutti i nodi nell'area in modo random
            JLabel holder = new JLabel("(" + n + ")");
            cooX1 = (int)(200 * Math.random()) + 1; /* punto iniziale del nodo */
            cooY1 = (int)(110 * Math.random()) + 1; //piano cartesiano 110x200 metri
            Color c = new Color((int)(255 * Math.random()) + 1, (int)(255 * Math.random()) + 1, (int)(255 * Math.random()) + 1);
            holder.setForeground(c);
            Font f = holder.getFont();
            holder.setFont(f.deriveFont(f.getStyle() | Font.BOLD));
            holder.setBounds((int)(cooX1 * 6), (690 - (int)(cooY1 * 6)), 20, 20); // *20 per rendere comprensibili gli spostamenti dei nodi
            pan.add(holder);
            labels.add(holder);
            nodes[n] = "" + cooX1 + ";" + cooY1 + ";fine;"; //inizialmente resteranno un secondo nella posizione iniziale
            c_nodes[n] = "0"; //azzera posizioni array c_nodes
            n = n + 1;
        }
        JLabel timet = new JLabel("Timer: " + (tempo - i));
        int j; //contatore generale
        long inizio = System.currentTimeMillis();
        timet.setBounds(1250, 0, 120, 20);
        pan.add(timet);
        long ip; //per quantificare meglio il secondo
        long ip2;
        JButton btn;
        while (i < tempo) {
            n = 0;
            ip = (System.currentTimeMillis());
            while (n < nodi) {
                parts = null;
                parts = nodes[n].split(";");
                j = Integer.parseInt(c_nodes[n]); //conta passi
                if (parts[j].equals("fine")) {
                    cooX1 = Float.parseFloat(parts[j - 2]);
                    cooY1 = Float.parseFloat(parts[j - 1]);
                    /* nell'if flag si entra solamente quando una persona ha raggiunto un punto di destinazione
						  e quindi cerca un nuovo percorso */
                    nodes[n] = "" + (taker(cooX1, cooY1, velox)); //trova punti intermedi tra le 2 coordinate x1 y1 e x2 y2 (x2 e y2 si decidono dentro la funzione taker)
                    //System.out.println("risultato: "+prendi);
                    parts = nodes[n].split(";");
                    c_nodes[n] = "0";
                    j = 0;
                } //fine flag
                String x = parts[j];
                j = j + 1;
                String y = parts[j];
                k = 1;
                s = 0;
                String p[] = d_ved[n].split(";");
                while (!(p[k].equals("finish"))) { //processo di connessione con i dispositivi distanti a un raggio <= 60
                    s = Integer.parseInt(p[k]);
                    String[] ved = discovery[s].split(";"); //bisogna prelevare il nodo associato
                    s = Integer.parseInt(c_nodes[Integer.parseInt(ved[1])]); //indice del nodo associato alla suo percorso nella stringa "nodes"
                    String x_him = "";
                    String y_him = "";
                    parts = null;
                    parts = nodes[Integer.parseInt(ved[1])].split(";");
                    if (parts[s].equals("fine")) {
                        x_him = parts[s - 2];
                        y_him = parts[s - 1];
                    } else {
                        x_him = parts[s];
                        y_him = parts[s + 1];
                    }
                    double distance = Math.sqrt(Math.pow((Float.parseFloat(x_him) - Float.parseFloat(x)), 2) + Math.pow((Float.parseFloat(y_him) - Float.parseFloat(y)), 2));
                    /* distance contiene la distanza tra i due nodi */
                    String fineg = String.format("%.1f", (float) distance);
                    fineg = fineg.replaceAll(",", ".");
                    if (Float.parseFloat(fineg) <= 60) {
                        Surface linea = segment.get(Integer.parseInt(p[k]));
                        linea.impostatutto((int)((Float.parseFloat(x_him)) * 6) + 7, (690 - (int)((Float.parseFloat(y_him)) * 6) + 12), (int)((Float.parseFloat(x)) * 6) + 7, (690 - (int)((Float.parseFloat(y)) * 6) + 12));
                        linea.repaint();
                        if (ved[3].equals("" + (-(trasmiss)))) {
                            Surface linea2 = segment.get(Integer.parseInt(p[k]));
                            linea2.impostatutto(0, 0, 0, 0);
                            linea2.repaint();
                            btn = new JButton("Trasmissione " + n + " e " + Integer.parseInt(ved[1]));
                            btn.setBackground(Color.BLUE);
                            System.out.println("Trasmissione completata tra " + n + " e " + Integer.parseInt(ved[1]));
                            pan2.add(btn);
                            linea2.flagg();
                            d_ved[n] = d_ved[n].replaceAll(";" + (Integer.parseInt(p[k])) + ";", ";"); // rimuove la cella della discovery da visitare, in questo modo non eseguirà piu' la connessione per lo stesso dispositivo.

                        } else if (ved[3].equals("1")) { //discovery riuscita !
                            btn = new JButton("Connessi " + n + " e " + Integer.parseInt(ved[1]));
                            btn.setBackground(Color.GREEN);
                            pan2.add(btn);
                            linea.flagg();
                            /*if(k==0){
							d_ved[n] = d_ved[n].replaceAll((Integer.parseInt(p[k]))+";",""); // rimuove la cella della discovery da visitare, in questo modo non eseguirà piu' la connessione per lo stesso dispositivo.
							}else{
							d_ved[n] = d_ved[n].replaceAll(";"+(Integer.parseInt(p[k]))+";",";"); // rimuove la cella della discovery da visitare, in questo modo non eseguirà piu' la connessione per lo stesso dispositivo.
							} */
                            discovery[Integer.parseInt(p[k])] = n + ";" + Integer.parseInt(ved[1]) + ";yes;" + (Integer.parseInt(ved[3]) - 1) + ";";
                            //azzera dati
                            //discovery[Integer.parseInt(p[k])]=n+";"+Integer.parseInt(ved[1])+";no;30;"; 
                        } else if (ved[2].equals("yes")) { //diminuisci di 1!!
                            discovery[Integer.parseInt(p[k])] = n + ";" + Integer.parseInt(ved[1]) + ";yes;" + (Integer.parseInt(ved[3]) - 1) + ";";
                        } else { //se i due nodi si trovano ad una distanza minore uguale a 60, partirà la discovery
                            int ti = (int)(27 * Math.random()) + 10;
                            discovery[Integer.parseInt(p[k])] = n + ";" + Integer.parseInt(ved[1]) + ";yes;" + (ti) + ";"; //l'ultimo intero rappresentano i secondi che mancano alla connessione
                            if (linea.get_conn() == 0) {
                                System.out.println("Processo di Connessione inizializzata tra " + n + " e " + Integer.parseInt(ved[1]) + ", da aspettare " + ti);
                                linea.conn(); //questo ulteriore flag serve per non scrivere nuovamente la stessa printl 
                            }
                        }
                    } else if (ved[2].equals("yes")) {
                        Surface linea = segment.get(Integer.parseInt(p[k]));
                        if (linea.get_flag() == 1) {
                            linea.flagg();
                            //System.out.println("Trasmissione annullata tra "+n+" e "+Integer.parseInt(ved[1])+", troppo distanti.");
                            btn = new JButton("TRASMIS-ANN " + n + " e " + Integer.parseInt(ved[1]));
                            btn.setBackground(Color.RED);
                            pan2.add(btn);
                        } else {
                            //	System.out.println("Connessione annullata tra "+n+" e "+Integer.parseInt(ved[1])+", troppo distanti.");
                            btn = new JButton("ANNULLATA " + n + " e " + Integer.parseInt(ved[1]));
                            btn.setBackground(Color.RED);
                            pan2.add(btn);
                        }

                        linea.impostatutto(0, 0, 0, 0);
                        linea.repaint();
                        // i nodi sono troppo distanti, non e' possibile eseguire una discovery
                        discovery[Integer.parseInt(p[k])] = n + ";" + Integer.parseInt(ved[1]) + ";no;30;";
                    }
                    //System.out.println("distanza tra "+n+" e "+ved[1]+" : "+distance);
                    k = k + 1;
                }
                cooX1 = Float.parseFloat(x); //aggiorna posizione
                cooY1 = Float.parseFloat(y);
                JLabel holder = labels.get(n);
                holder.setBounds((int)(cooX1 * 6), (690 - (int)(cooY1 * 6)), 20, 20);
                j = j + 1; // per evitare l'eccezione ArrayIndexOut
                c_nodes[n] = "" + j; //aggiorna il contare del n-esimo nodo
                n = n + 1; // vedi il prossimo nodo!
            }
            i = i + 1;
            /* ip2 = (System.currentTimeMillis())- ip;
		  if((1000-ip2)>=0){
		try {
                    Thread.sleep(1000-ip2); 
                } catch (InterruptedException e) {	
                    e.printStackTrace();
                }
				}
			  timet.setText("Timer: "+(tempo-i)); */
        } //fine while

        // fine= (System.currentTimeMillis()-inizio)/1000;

        //System.out.println("Tempo totale: "+fine+" secondi.");

    }



    public String taker(float cooX1, float cooY1, int velox) {
        /* trova i punti intermedi nella retta che collega i due punti */
        /* tutti i punti sono distanti = velox */
        float cooX2 = (int)(200 * Math.random()) + 1; //piano cartesiano 110x200 metri
        float cooY2 = (int)(110 * Math.random()) + 1; //piano cartesiano 110x200 metri

        //System.out.println("x1="+cooX1+"\n"+"y1="+cooY1+"\n"+"x2="+cooX2+"\n"+"y2="+cooY2+"\n");
        float f = 0;
        float min = 0;
        //cooY1= (int)(30*Math.random())+1; //piano cartesiano 30x30 metri
        //cooY2= (int)(30*Math.random())+1; //piano cartesiano 30x30 metri
        /*equazione della retta fra due punti */
        float den_y = cooY2 - cooY1; //puo' essere anche negativo, nessun problema.
        float den_x = cooX2 - cooX1;
        int tipo_f = 0; //identificherà il tipo di equazione che risulterà dopo i calcoli (4 tipi)
        float somma = 0;
        if (den_y == 0) { //la retta è parallela all'asse delle ascisse
            tipo_f = 1;
            //System.out.println("0.1RISULTATO FINALE: y= "+cooY1);
        } else if (den_x == 0) { //ordinate
            tipo_f = 2;
            //System.out.println("0.2RISULTATO FINALE: x= "+cooX1);
        } else {
            //moltiplicazioni incrociate
            float num_s = (-cooY1) * (den_x); //il secondo numero potrebbe essere negativo
            float num_d = (-cooX1) * (den_y);
            num_s = -(num_s); //si sposta dall'altra parte dell'uguale
            somma = (num_s) + (num_d);
            //den_x sarebbe il coefficiente di Y (colui che deciderà il segno!)
            if (den_x < 0) {
                somma = -(somma);
                den_x = -(den_x);
                den_y = -(den_y);
            }
            String agg = "";
            if (somma > 0) {
                agg = "+";
            }
            if (den_x == 1) {
                tipo_f = 3;
                //System.out.println("1RISULTATO FINALE: y="+den_y+"x"+agg+somma);
            } else if (somma != 0) {
                tipo_f = 4;
                //System.out.println("2RISULTATO FINALE: y="+den_y+"x/"+den_x+agg+somma+"/"+den_x);
            } else {
                tipo_f = 4; //tipo uguale al precednete cambia solo "somma"
                //System.out.println("3RISULTATO FINALE: y="+den_y+"x/"+den_x);
            }
        } //fine if se denominatori diversi da 0
        f = cooX1 - cooX2; //x1-x2
        min = cooX2;
        String se = "y"; //devo trovare le y!
        if (f < (cooX2 - cooX1)) {
            f = cooX2 - cooX1; //x2-x1
            min = cooX1;
            se = "y";
        }
        if (f < (cooY2 - cooY1)) {
            f = cooY2 - cooY1; //y2-y1
            min = cooY1;
            se = "x";
        }
        if (f < (cooY1 - cooY2)) {
            f = cooY1 - cooY2; //y1-y2
            min = cooY2;
            se = "x";
        }

        //System.out.println("risultato da dove partire per poi scendere "+(f+min-1)+ " > "+min + " devo trovare le "+se);

        float it = f + min - 1;
        String salva = "";
        while ((it) > min) { //trova coordinate centrali !
            if (tipo_f == 1) { // tipo y = c (costante)
                //trovare la x
                // solo nelle equazione di tipo 1 e 2 non è necessario fare il controllo con la variabile "se" 
                salva = salva + it + ";" + cooY1 + ";";
            } else if (tipo_f == 2) { //tipo x= c
                //trovare le y
                salva = salva + cooX1 + ";" + it + ";";
            } else if ((tipo_f == 3) && (se.equals("y"))) { //tipo y="+den_y+"x"+agg+somma);
                //significa che it contiene tutti le coordinate x, devo trovare le y !!
                float wow = (den_y) * (it) + (somma);
                salva = salva + it + ";" + wow + ";";
            } else if ((tipo_f == 3) && (se.equals("x"))) {
                //devo trovare le x!! ho le coordinate di y!!
                float wow1 = -(it); //porto la y dall'altra parte
                float wow2 = (wow1) + (somma);
                float den = -(den_y); //porto den dall'altra parte insieme a x
                if (den < 0) {
                    wow2 = -(wow2);
                    den = -(den);
                }
                float finale = 0;
                if (den > 1) {
                    finale = wow2 / den;
                } else {
                    finale = wow2;
                }
                String fine = String.format("%.1f", finale);
                salva = salva + fine + ";" + it + ";";
            } else if ((tipo_f == 4) && (se.equals("y"))) { //tipo y="+den_y+"x/"+den_x+agg+somma+"/"+den_x);

                float wow = ((float)((den_y) * (it)) / (float) den_x) + ((float) somma / (float) den_x);
                String fine = String.format("%.1f", wow);
                salva = salva + it + ";" + fine + ";";
            } else if ((tipo_f == 4) && (se.equals("x"))) {
                float wow1 = -(it); //porto la y dall'altra parte
                float wow2 = (float)(somma) / (float)(den_x); //puo' essere anche negativo, con virgola...
                wow2 = (wow2) + (wow1);
                float wow3 = (float)(den_y) / (float)(den_x);
                wow3 = -(wow3); //porto la x dall'altra parte: e' quella che devo calcolare
                if (wow3 < 0) {
                    wow3 = -(wow3);
                    wow2 = -(wow2);
                }
                float finale = 0;
                if (wow3 > 1) {
                    finale = (float) wow2 / (float) wow3;
                } else {
                    finale = wow2;
                }
                String fine = String.format("%.1f", finale);
                salva = salva + fine + ";" + it + ";";
            }
            it = it - velox;
        } //fine del trova coordinate centrali
        salva = salva + "fin;";
        salva = salva.replaceAll(",", ".");
        String[] parts = salva.split(";");
        int z = 0;
        //da questo momento in poi parts contiene tutte le coordinate...
        /* parts.length -2 per andare all'ultima y */
        String percorso = ""; //formato di percorso 3;5;4;6...x1;y1;x2;y2...
        if (parts.length == 1) {
            percorso = "" + cooX2 + ";" + cooY2 + ";fine;";
        } else {
            float comx1 = Float.parseFloat(parts[0]);
            float comy1 = Float.parseFloat(parts[1]);
            float comx2 = Float.parseFloat(parts[parts.length - 3]);
            float comy2 = Float.parseFloat(parts[parts.length - 2]);

            double val1 = Math.sqrt(Math.pow((cooX1 - comx1), 2) + Math.pow((cooY1 - comy1), 2)); //dall'inizio
            double val2 = Math.sqrt(Math.pow((cooX1 - comx2), 2) + Math.pow((cooY1 - comy2), 2));
            //System.out.println("ordine punti di andata:"); //questo inizia da y
            // La stringa "percorso" conterrà tutte le coordinate che "l'individuo dovrà percorrere".
            if (val1 < val2) {
                while (!(parts[z].equals("fin"))) {
                    String x = parts[z];
                    z = z + 1;
                    String y = parts[z];
                    z = z + 1;
                    //System.out.println("x="+x+" y="+y); //questo inizia da y
                    percorso = percorso + x + ";" + y + ";";
                }
            } else {
                z = (parts.length - 2);
                while (z >= 0) {
                    String y = parts[z];
                    z = z - 1;
                    String x = parts[z];
                    z = z - 1;
                    //System.out.println("x="+x+" y="+y); //questo inizia da y
                    percorso = percorso + x + ";" + y + ";";
                }

            }
            percorso = percorso + "" + cooX2 + ";" + cooY2 + ";fine;"; //stringa ordinata
        }
        return percorso;
    }

}



public class qualcosa {
    public static void main(String args[]) throws Exception {
        System.out.println("Durata test in secondi:");
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); //funzione usata per ricevere l'input
        String sentence = inFromUser.readLine();
        int foo = Integer.parseInt("" + sentence);
        System.out.println("Numero di persone:");
        inFromUser = new BufferedReader(new InputStreamReader(System.in)); //funzione usata per ricevere l'input
        sentence = null;
        sentence = inFromUser.readLine();
        int foo2 = Integer.parseInt("" + sentence);
        System.out.println("Velocita': (1 = 1 m/s, 2 = 2 m/s....)");
        inFromUser = new BufferedReader(new InputStreamReader(System.in)); //funzione usata per ricevere l'input
        sentence = null;
        sentence = inFromUser.readLine();
        int velox = Integer.parseInt("" + sentence);
        if (velox <= 0) {
            System.out.println("Parametro della velocita' errato.");
            return;
        }
        System.out.println("Numeri di pacchetti da iniziare: (possibili input: 100,500,1000,5000,9000,15000)");
        inFromUser = new BufferedReader(new InputStreamReader(System.in)); //funzione usata per ricevere l'input
        sentence = null;
        sentence = inFromUser.readLine();
        int pkt = Integer.parseInt("" + sentence);
        if (!((pkt == 100) || (pkt == 500) || (pkt == 1000) || (pkt == 5000) || (pkt == 9000) || (pkt == 15000))) {
            System.out.println("Parametro del numero di pacchetti ERRATO.");
            return;
        } else if (pkt == 100) {
            pkt = 0;
        } else if (pkt == 500) {
            pkt = 1;
        } else if (pkt == 1000) {
            pkt = 2;
        } else if (pkt == 5000) {
            pkt = 3;
        } else if (pkt == 9000) {
            pkt = 4;
        } else {
            pkt = 5;
        }
        String[] tem_trasmiss = new String[6]; //array contenente i tempi di trasmissione in base al pacchetto che scegli
        tem_trasmiss[0] = "1"; //1 secondo per 100 pacck
        tem_trasmiss[1] = "2"; //1,2   per 500 pack	
        tem_trasmiss[2] = "3"; //2,35  per 1000
        tem_trasmiss[3] = "10"; //10,25 per 5000
        tem_trasmiss[4] = "18"; //18,22 per 9000
        tem_trasmiss[5] = "30"; //30 per 15000
        int dim = 0; //dimensione array discovery
        if (foo2 > 3) {
            int a = 3;
            int b = 4;
            int i = 3;
            while (i < foo2) {
                a = a + b - 1; //6
                b = b + 1; //5
                i = i + 1;
            }
            dim = a;
        } else {
            dim = foo2;
        }
        FrameConComponenti p = new FrameConComponenti(foo, foo2, dim, velox, Integer.parseInt(tem_trasmiss[pkt]));

    }
}
