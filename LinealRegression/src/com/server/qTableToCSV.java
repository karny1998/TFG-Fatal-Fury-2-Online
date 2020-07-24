package com.server;

import java.io.*;
import java.net.InetAddress;

public class qTableToCSV {
    private String training;
    private String qtable;
    private double table[][];
    private state visited[][];
    private double times[][];
    private boolean fromTraining = false;

    public qTableToCSV(String t, String q, boolean fromTraining){
        this.fromTraining = fromTraining;
        training = t;
        qtable = q;
        stateCalculator.initialize();
        int x = stateCalculator.getMax();
        int y = stateCalculator.getnActions();
        table = new double[x][y];
        visited = new state[x][y];
        times = new double[x][y];
        for (int a = 0; a < stateCalculator.getnActions(); ++a) {
            for (int s = 0; s < stateCalculator.getMax(); ++s) {
                times[s][a] = 0;
            }
        }
    }

    /*public void generateCSV(){
        loadQtable();
        loadTraining(training);
        String path =  System.getProperty("user.dir") + "/.files/" + qtable.substring(0,qtable.length()-4) + ".csv";
        File f = new File(path);
        f.delete();
        f = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write("Action,Reward,Jumping,Enemy state,Distance,Life,EnemyLife\n");
            for (int a = 0; a < stateCalculator.getnActions(); ++a) {
                for (int s = 0; s < stateCalculator.getMax(); ++s) {
                    if (visited[s][a] != null) {
                        state aux = visited[s][a];
                        int j = 0;
                        if (aux.isJumping()) {
                            j = 1;
                        }
                        String line = a + "," + table[s][a] + "," + j + "," + stateCalculator.getIdMov().get(aux.getPlayerState()) + "," + aux.getSimpleDistance()
                                + "," + aux.getSimpleLife() + "," + aux.getSimplePlayerLife() +"\n";
                        bw.write(line);
                    }
                }
            }
            bw.close();
        }catch (Exception e){e.printStackTrace();};
    }*/


    public void generateCSV(int grade){
        if(!fromTraining) {
            loadQtable();
        }
        loadTraining(training, fromTraining);
        String path =  System.getProperty("user.dir") + "/.files/" + qtable.substring(0,qtable.length()-4) + ".csv";
        File f = new File(path);
        f.delete();
        f = new File(path);
        try {
            FileOutputStream fos = new FileOutputStream(f);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            String head = "Action,Reward,Jumping,Enemy state,Distance,Life,EnemyLife";
            for(int i = 2; i <= grade; ++i){
                head += (",Action" + i + ",Jumping" + i + ",Enemy state" + i + ",Distance" + i + ",Life" + i + ",EnemyLife" + i);
            }
            bw.write(head+"\n");
            for (int a = 0; a < stateCalculator.getnActions(); ++a) {
                for (int s = 0; s < stateCalculator.getMax(); ++s) {
                    if (visited[s][a] != null) {
                        state aux = visited[s][a];
                        int j = 0;
                        if (aux.isJumping()) {
                            j = 1;
                        }
                        String line = a + "," + table[s][a] + "," + j + "," + stateCalculator.getIdMov().get(aux.getPlayerState()) + "," + aux.getSimpleDistance()
                                + "," + aux.getSimpleLife() + "," + aux.getSimplePlayerLife();
                        for(int i = 2; i <= grade; ++i){
                            line += (","+Math.pow(a,i) +  "," + Math.pow(j,i) + "," + Math.pow(stateCalculator.getIdMov().get(aux.getPlayerState()),i) + "," + Math.pow(aux.getSimpleDistance(),i)
                                    + "," + Math.pow(aux.getSimpleLife(),i) + "," + Math.pow(aux.getSimplePlayerLife(),i));
                        }
                        bw.write(line+"\n");
                    }
                }
            }
            bw.close();
        }catch (Exception e){e.printStackTrace();};
    }

    /**
     * Carga la tabla q del fichero correspondiente.
     */
    public boolean loadQtable(){
        String path =  System.getProperty("user.dir") + "/.files/" +qtable;
        try {
            File f = new File(path);
            BufferedReader b = new BufferedReader(new FileReader(f));
            String aux = "";
            int i = 0;
            while((aux = b.readLine()) != null){
                String values[] = aux.split(" ");
                for(int j = 0; j < values.length; ++j){
                    table[i][j] = Double.parseDouble(values[j]);
                }
                ++i;
            }
            b.close();
        }catch (Exception e){
            System.out.println("No se ha encontrado la q table, por lo que se empezará de 0");
            return false;
        }
        return true;
    }

    /**
     * Load training.
     */
    public void loadTraining(String file, boolean generateQtable){
        String path =  System.getProperty("user.dir") + "/.files/" + file;
        try {
            File f = new File(path);
            BufferedReader b = new BufferedReader(new FileReader(f));
            String aux = "";
            boolean nextFight = false;
            aux = b.readLine();
            while(aux != null) {
                nextFight = false;
                aux = b.readLine();
                aux = b.readLine();
                while (!nextFight) {
                    aux = b.readLine();
                    if(aux == null || aux.equals("#-----------#")){nextFight = true;}
                    else {
                        aux = b.readLine();
                        String estado[] = aux.split(",");
                        state s = new state(Integer.parseInt(estado[0]), Integer.parseInt(estado[1]), Movement.valueOf(estado[2]),
                                Integer.parseInt(estado[3]), Integer.parseInt(estado[4]), Integer.parseInt(estado[6]),
                                Integer.parseInt(estado[7]), Integer.parseInt(estado[8]), Boolean.parseBoolean(estado[9]));
                        aux = b.readLine();
                        aux = b.readLine();
                        Movement action = Movement.valueOf(aux);
                        aux = b.readLine();
                        aux = b.readLine();
                        double reward = Double.parseDouble(aux);
                        aux = b.readLine();
                        aux = b.readLine();
                        estado = aux.split(",");
                        state s2 = new state(Integer.parseInt(estado[0]), Integer.parseInt(estado[1]), Movement.valueOf(estado[2]),
                                Integer.parseInt(estado[3]), Integer.parseInt(estado[4]), Integer.parseInt(estado[6]),
                                Integer.parseInt(estado[7]), Integer.parseInt(estado[8]), Boolean.parseBoolean(estado[9]));

                        visited[s.getStateNum()][stateCalculator.getIdAction().get(action)] = s;

                        if(generateQtable) {
                            ++times[s.getStateNum()][stateCalculator.getIdAction().get(action)];
                            table[s.getStateNum()][stateCalculator.getIdAction().get(action)] += reward;
                        }
                    }
                }
            }

            for (int a = 0; a < stateCalculator.getnActions(); ++a) {
                for (int s = 0; s < stateCalculator.getMax(); ++s) {
                    if (times[s][a] > 0) {
                        table[s][a] /= times[s][a];
                    }
                }
            }

            b.close();
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("No se ha encontrado el fichero de entrenamiento");
        }
    }
}
