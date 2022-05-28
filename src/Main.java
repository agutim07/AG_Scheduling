import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author Alberto Gutiérrez Morán
 */

public class Main {

    public static void main(String[] args){
//        ArrayList<Integer[]> cromosoma = new ArrayList<>();
//        cromosoma.add(new Integer[]{1,2,4,3});
//        cromosoma.add(new Integer[]{4,1,3,2});
//        cromosoma.add(new Integer[]{4,3,1,2});

//        cromosoma.add(new Integer[]{1,2,3});
//        cromosoma.add(new Integer[]{3,2,1});
//        int len = sBuilder(cromosoma,jss,true);

        //CREAMOS LA INSTANCIA JSS Y AÑADIMOS LAS RUTAS DE LOS JOBS
        JSS uno = new JSS(1); uno.addRutas(new int[][] {{1,1},{2,1},{3,3}});
        JSS dos = new JSS(2); dos.addRutas(new int[][] {{1,3},{2,2},{3,1}});
        JSS tres = new JSS(3); tres.addRutas(new int[][] {{3,2},{2,1},{1,3}});
        JSS cuatro = new JSS(4); cuatro.addRutas(new int[][] {{3,1},{2,4},{1,2}});
        ArrayList<JSS> jss = new ArrayList<>(Arrays.asList(uno,dos,tres,cuatro));

        //MODELADO DEL PROBLEMA
        int s = jss.size(); //Nº DE SIMBOLOS DEL ALFABETO = Nº DE JOBS
        int m = 3; //NUMERO DE VECTORES EN EL CROMOSOMA = Nº DE MAQUINAS
        int rm = jss.size(); //NUMERO DE GENES EN CADA VECTOR = Nº DE JOBS
        int r = rm*m; //LONG TOTAL DE LA CADENA

        //PARAMETROS DEL ALGORITMO
        int n = 5; //TAMAÑO DE LA POBLACION
        double pc = 0.8; //PROBABILIDAD DE CRUCE
        double pm = 1.0 / (r*n); //PROBABILIDAD DE MUTACION
        int t_max = 10;
        int num = 1000; //Nº ORIGINAL DE CASILLAS DE LA RULETA
        double pextraccion = 0.1; //PORCENTAJE DE LAS MUESTRAS QUE EXTRAEMOS AL INICIO DEL BUCLE Y LUEGO IMPORTAMOS
        boolean elitismo = true;

        //INICIO DEL ALGORITMO
        int t=0;
        ArrayList<Integer[]>[] w = new ArrayList[n];   //INICIALIZAMOS LAS CADENAS
                                                        //CONDICION: NO SE PUEDEN REPETIR JOBS EN EL MISMO VECTOR DE LA MISMA CADENA

        for(int i=0; i<n; i++){
            ArrayList<Integer[]> cromosoma = new ArrayList<>();
            for(int j=0; j<m; j++){
                cromosoma.add(generarVector(s));
            }
            w[i] = cromosoma;
        }

        int[] apt = new int[n];
        for(int j=0; j<n; j++){apt[j] = sBuilder(w[j],jss,false);}

        int[] apt_gen = new int[t_max+1];
        apt_gen[0]=0;
        for(int i=0; i<n; i++){apt_gen[0]+=apt[i];}

        double[] apt_m_gen = new double[t_max+1];
        apt_m_gen[0] = apt_gen[0]/(double) n;

        //IMPRESION INICIAL:
        System.out.println("Población antes del algoritmo: [Media aptitud: "+apt_m_gen[0]+"]");
        for(int j=0; j<n; j++){
            for(int i=0; i<m; i++){
                System.out.print("{");
                for(int x=0; x<s; x++){
                    System.out.print(w[j].get(i)[x]+" ");
                }
                System.out.print("}");
            }
            System.out.print("  Tiempo de completación: "+apt[j]);
            System.out.println();
        }
        System.out.println("----------------------------------");

        //CUERPO DEL ALGORITMO
        while(t<t_max){
            //EXTRAEMOS EL %x CON MAYOR APTITUD
            int extraccion = (int) Math.floor(pextraccion*n);
            ArrayList<Integer[]>[] cadenaExtraida = extraerMayorAptitud(w,extraccion,n,jss);

            //SELECCION
            double[] p = getPorcentajes(apt);
            int[] c = new int[n];
            c[0] = (int) Math.floor(p[0]*num) + 1;
            int[] alfa = new int[n];
            alfa[0] = 0;
            int[] beta = new int[n];
            beta[0] = alfa[0]+c[0]-1;

            for(int i=1; i<n; i++){
                c[i] = (int) Math.floor(p[i]*num) + 1;
                alfa[i] = alfa[i-1] + c[i-1];
                beta[i] = alfa[i] + c[i] -1;
            }

            int num_real_casillas = beta[n-1] + 1;

            //SELECCION DE INDIVIDUOS
            ArrayList<Integer[]>[] w_new = new ArrayList[n];
            for(int j=0; j<n; j++){
                int cas = (int) Math.floor(Math.random()*num_real_casillas);
                int i=-1;
                for(int x=0; x<n; x++){
                    if(cas>=alfa[x] && cas<=beta[x]){i=x;}
                }
                w_new[j] = new ArrayList<>(w[i]);
            }
            for(int i=0; i<n; i++){
                w[i] = new ArrayList<>(w_new[i]);
            }
            //FIN SELECCION

            //CROSSOVER
            int[][] parejas = getCrossoverParejas(n,pc);
            //intercambiamos el material genetico de las parejas
            for(int i=0; i<(parejas[0].length); i++){
                int posA = parejas[0][i]; int posB = parejas[1][i];
                int borde1 = (int) Math.floor(Math.random()*(s-1)) + 1; //nº aleatorio entre 1 y s-1
                int borde2 = (int) Math.floor(Math.random()*(s-1)) + 1; //nº aleatorio entre 1 y s-1
                while (borde1==borde2){borde2 = (int) Math.floor(Math.random()*(s-1)) + 1;}
                ArrayList<Integer[]>[] nuevosValores = recombinar(w[posA],w[posB],borde1,borde2);

                w[posA] = new ArrayList<>(nuevosValores[0]);
                w[posB] = new ArrayList<>(nuevosValores[1]);
            }
            //FIN CROSSOVER

            //MUTACION
            for(int i=0; i<n; i++){
                for(int j=0; j<m; j++){
                    for(int x=0; x<rm; x++){
                        double randomnum = Math.random();
                        if(randomnum<pm){
                            w[i].set(j, vectorMutado(i,j,x,w));
                        }
                    }
                }
            }
            //FIN MUTACION

            //EXTRAEMOS UN %x ALEATORIO Y AÑADIMOS EL %x CON MAYOR APTITUD EXTRAIDO ANTES
            if(elitismo) w = extraerImportar(w,cadenaExtraida,n);

            t++;
            for(int i=0; i<n; i++){apt[i] = sBuilder(w[i],jss,false);}
            apt_gen[t]=0;
            for(int i=0; i<n; i++){apt_gen[t]+=apt[i];}
            apt_m_gen[t] = apt_gen[t]/(double) n;
        }

        //IMPRESION FINAL
        System.out.println("Población después del algoritmo: [Media aptitud: "+apt_m_gen[t]+"]");
        for(int j=0; j<n; j++){
            for(int i=0; i<m; i++){
                System.out.print("{");
                for(int x=0; x<s; x++){
                    System.out.print(w[j].get(i)[x]+" ");
                }
                System.out.print("}");
            }
            System.out.print("  Tiempo de completación: "+apt[j]);
            System.out.println();
        }
        System.out.println("----------------------------------");

        //SOLUCION (MEJOR INDIVIDUO)
        int mejorPos = min(apt);
        int mejor_a = apt[mejorPos];
        ArrayList<Integer[]> mejor_w = w[mejorPos];
        System.out.print("Mejor aptitud: "+mejor_a + " - Mejor cadena: ");
        for(int i=0; i<m; i++){
            System.out.print("{");
            for(int x=0; x<s; x++){
                System.out.print(mejor_w.get(i)[x]+" ");
            }
            System.out.print("} ");
        }
        System.out.println();
        System.out.println("Representacion: "); sBuilder(mejor_w,jss,true);
    }

    private static double[] getPorcentajes(int[] apt){
        double[] p = new double[apt.length];
        int aptmin = apt[min(apt)] - 1;
        int aptmax = apt[max(apt)] - aptmin;

        int[] newapt = new int[apt.length];
        int sumapt = 0;
        for(int i=0; i<apt.length; i++){
            int valor = apt[i] - aptmin;
            newapt[i] = (aptmax-valor) + 1;
            sumapt+=newapt[i];
        }

        for(int i=0; i<apt.length; i++){
            p[i] = newapt[i]/(double) sumapt;
        }

        return p;

    }

    private static Integer[] vectorMutado(int i, int j, int x, ArrayList<Integer[]>[] w){
        Integer[] vector = w[i].get(j);
        int job = vector[x];
        int randompos = (int) Math.floor(Math.random()*vector.length);
        while(randompos==x){
            randompos = (int) Math.floor(Math.random()*vector.length);
        }
        vector[x] = vector[randompos];
        vector[randompos] = job;

        return vector;
    }

    private static Integer[] generarVector(int s){
        Integer[] vector = new Integer[s];

        for(int i=0; i<s; i++){
            int alt = (int) Math.floor(Math.random()*s) + 1;
            boolean presente = true;
            while (presente) {
                presente = false;
                for(int j=0; j<i; j++){
                    if(alt==vector[j]){
                        presente=true;
                        alt = (int) Math.floor(Math.random()*s) + 1;
                    }
                }
            }
            vector[i] = alt;
        }

        return vector;
    }

    private static ArrayList<Integer[]>[] extraerMayorAptitud(ArrayList<Integer[]>[] w, int num, int n, ArrayList<JSS> jss){
        int[] apt = new int[n];
        for(int i=0; i<n; i++){apt[i] = sBuilder(w[i],jss,false);}
        int[] mejores = order(apt);

        ArrayList<Integer[]>[] out = new ArrayList[num];
        for(int i=0; i<num; i++){
            out[i] = new ArrayList<>(w[mejores[i]]);
        }

        return out;
    }

    private static ArrayList<Integer[]>[] extraerImportar(ArrayList<Integer[]>[] w, ArrayList<Integer[]>[] cadena, int n){
        int num = cadena.length;
        int[] posAleatorias = new int[num];

        for(int i=0; i<num; i++){
            int posAleatoria = (int) Math.floor(Math.random()*n);
            while(checkExists(posAleatorias, posAleatoria)) posAleatoria = (int) Math.floor(Math.random()*n);
            posAleatorias[i] = posAleatoria;
            w[posAleatoria] = cadena[i];
        }

        return w;
    }

    private static ArrayList<Integer[]>[] recombinar(ArrayList<Integer[]> a, ArrayList<Integer[]> b, int borde1, int borde2){
        int genes = a.get(0).length;
        if(borde1>borde2){
            int temp = borde1;
            borde1 = borde2;
            borde2 = temp;
        }

        for(int i=0; i<a.size(); i++){
            Integer[] vectorA = a.get(i); Integer[] nuevoA = new Integer[genes];
            Integer[] vectorB = b.get(i); Integer[] nuevoB = new Integer[genes];
            for(int j=0; j<genes; j++){
                if(j>=borde1 && j<=borde2){
                    nuevoA[j] = vectorA[j];
                    nuevoB[j] = vectorB[j];
                }else{
                    nuevoA[j] = -1;
                    nuevoB[j] = -1;
                }
            }

            int posB=0; int posA=0;
            for(int x=0; x<genes; x++){
                if(vectorB[x]==vectorA[borde2]) posB=x;
                if(vectorA[x]==vectorB[borde2]) posA=x;
            }

            int j = borde2+1;
            while(j!=borde1){
                if(j==genes){j=0;}

                posB++; if(posB==genes){posB=0;}
                boolean alreadyInA = true;
                while(alreadyInA) {
                    alreadyInA = false;
                    for (int x = 0; x < genes; x++) {
                        if (vectorB[posB] == nuevoA[x]) alreadyInA = true;
                    }
                    if(alreadyInA){
                        posB++; if(posB==genes){posB=0;}
                    }
                }

                posA++; if(posA==genes){posA=0;}
                boolean alreadyInB = true;
                while(alreadyInB) {
                    alreadyInB = false;
                    for (int x = 0; x < genes; x++) {
                        if (vectorA[posA] == nuevoB[x]) alreadyInB = true;
                    }
                    if(alreadyInB){
                        posA++; if(posA==genes){posA=0;}
                    }
                }

                nuevoA[j] = vectorB[posB];
                nuevoB[j] = vectorA[posA];
                j++;
            }

            a.set(i,nuevoA);
            b.set(i,nuevoB);
        }

        return new ArrayList[]{a, b};
    }

    private static int[][] getCrossoverParejas(int n, double pc){
        ArrayList<Integer> recombinacionesPos = new ArrayList<>();
        for(int i=0; i<n; i++){
            double randomnum = Math.random();
            if(randomnum<=pc){recombinacionesPos.add(i);}
        }

        //Si los individuos a recombinar no son pares eliminamos uno aleatorio del conjunto
        if(recombinacionesPos.size()%2!=0){
            int randompos = (int) Math.floor(Math.random()*recombinacionesPos.size());
            recombinacionesPos.remove(randompos);
        }
        int recombinaciones = recombinacionesPos.size();

        //Emparejamos aleatoriamente
        int[][] parejas = new int[2][recombinaciones/2];
        for(int i=0; i<(recombinaciones/2); i++){
            int random1 = (int) Math.floor(Math.random()*recombinacionesPos.size());
            int random2 = (int) Math.floor(Math.random()*recombinacionesPos.size());

            while(random1==random2){
                random1 = (int) Math.floor(Math.random()*recombinacionesPos.size());
                random2 = (int) Math.floor(Math.random()*recombinacionesPos.size());
            }
            parejas[0][i] = recombinacionesPos.get(random1);
            parejas[1][i] = recombinacionesPos.get(random2);

            if(random2>random1){
                recombinacionesPos.remove(random2); recombinacionesPos.remove(random1);
            }else{
                recombinacionesPos.remove(random1); recombinacionesPos.remove(random2);
            }
        }

        return parejas;
    }

    private static boolean checkExists(int[] list, int nuevo){
        for(int i=0; i< list.length; i++){
            if(list[i]==nuevo) return true;
        }

        return false;
    }

    private static int[] order(int[] x){
        int[] minList = new int[x.length];

        for(int i=0; i<x.length; i++){
            int minPos = min(x);
            minList[i] = minPos;
            x[minPos] = 9999999;
        }

        return minList;
    }

    private static int min(int[] x){
        int min = 99999999; int pos = -1;
        for(int i=0; i<x.length; i++){
            if(min>x[i]){
                min = x[i];
                pos=i;
            }
        }
        return pos;
    }

    private static int max(int[] x){
        int max = -1; int pos = -1;
        for(int i=0; i<x.length; i++){
            if(max<x[i]){
                max = x[i];
                pos=i;
            }
        }
        return pos;
    }

    public static int sBuilder(ArrayList<Integer[]> cromosoma, ArrayList<JSS> jss, boolean print){
        ArrayList<JSS> instancia = copyJSS(jss);

        int nummaquinas = cromosoma.size();
        int tiempo = 0;
        ArrayList<ArrayList<Integer>> maquinas = new ArrayList<>();

        for(int i=0; i<nummaquinas; i++){
            maquinas.add(new ArrayList<>());
        }

        //RECORREMOS EL BUCLE HASTA EL FIN DE TODOS LOS JOBS
        while(!checkFinish(instancia)){
            //RECORREMOS MAQUINA A MAQUINA Y SUS JOBS DE PREFERENCIA, SI SE PUEDE SE AÑADEN, SINO SE ESPERA
            for(int i=0; i<nummaquinas; i++){
                if(maquinas.get(i).size()==tiempo){
                    int[] next = checkJobForMachine(instancia,i,cromosoma.get(i));
                    if(next[0]!=-1){
                        JSS job = getJob(instancia,next[0]);
                        job.setOcupado(true);
                        for(int j=0; j<next[1]; j++) {
                            maquinas.get(i).add(next[0]);
                        }
                    }else{
                        maquinas.get(i).add(-1);
                    }
                }
            }

            tiempo++;

            //VEMOS LOS JOBS QUE HAN TERMINADO EN LAS MAQUINAS Y LOS MARCAMOS COMO DISPONIBLES
            //A SU VEZ VEMOS EL SIGUIENTE JOB QUE INTERESA A LA MAQUINA
            //ESTO SOLO SI EL ALGORITMO LLEVA UNA PASADA AL MENOS
            for(int i=0; i<nummaquinas; i++){
                if(maquinas.get(i).size()==tiempo && maquinas.get(i).get(tiempo-1)>0){
                    JSS job = getJob(instancia,maquinas.get(i).get(tiempo-1));
                    job.setOcupado(false);
                    job.nextMachine();
                }
            }
        }

        if(print) {
            System.out.print("Tiempo   ");
            for (int i = 0; i < tiempo; i++) {
                System.out.print(" | " + (i + 1));
            }
            System.out.println();
            for (int i = 0; i < nummaquinas; i++) {
                System.out.print("Maquina " + (i + 1));
                for (int j = 0; j < maquinas.get(i).size(); j++) {
                    int length = String.valueOf(j).length();
                    for(int x=0; x<length; x++) System.out.print(" ");
                    if (maquinas.get(i).get(j) == -1) {
                        System.out.print("|  ");
                    } else {
                        System.out.print("| " + maquinas.get(i).get(j));
                    }
                }
                System.out.println();
            }
        }

        return tiempo;
    }

    public static int[] checkJobForMachine(ArrayList<JSS> jss, int m, Integer[] jobpreferences){
        m++;

        for(int i=0; i<jobpreferences.length; i++) {
            JSS job = getJob(jss,jobpreferences[i]);
            if(!job.finish && job.ocupado==false && job.maquinaSolicitada==m){
                return new int[]{job.getJob(),job.getNextRutaLen()};
            }
        }

        return new int[]{-1};
    }

    public static JSS getJob(ArrayList<JSS> jss, int job){
        for(int i=0; i<jss.size(); i++){
            if(job==jss.get(i).getJob()){
                return jss.get(i);
            }
        }

        return null;
    }

    public static boolean checkFinish(ArrayList<JSS> jss){
        for(int i=0; i<jss.size(); i++){
            if(!jss.get(i).finish) return false;
        }

        return true;
    }

    public static ArrayList<JSS> copyJSS(ArrayList<JSS> lista){
        ArrayList<JSS> nuevo = new ArrayList<>();
        for(int i=0; i<lista.size(); i++){
            JSS jss = new JSS(lista.get(i));
            nuevo.add(jss);
        }
        return nuevo;
    }

    public static class JSS{
        private int job;
        private ArrayList<ruta> rutas;
        public int maquinaSolicitada;
        private int rutaActual;
        public boolean ocupado;
        public boolean finish;

        JSS(int j){
            this.job = j;
            rutas = new ArrayList<>();
        }

        public JSS(JSS copia){
            this.job = copia.job;
            this.rutas = copia.rutas;
            this.maquinaSolicitada = copia.maquinaSolicitada;
            this.rutaActual = copia.rutaActual;
            this.ocupado = copia.ocupado;
            this.finish = copia.finish;
        }

        public void addRutas(int[][] rut){
            this.maquinaSolicitada = rut[0][0];
            this.rutaActual = 0;
            this.ocupado = false;
            this.finish = false;

            for(int i=0; i<rut.length; i++){
                rutas.add(new ruta(rut[i][0],rut[i][1]));
            }
        }

        public void nextMachine(){
            rutaActual++;
            if(rutaActual!=rutas.size()){
                this.maquinaSolicitada = rutas.get(rutaActual).maquina;
            }else{
                this.finish=true;
            }
        }

        public int getNextRutaLen(){
            return rutas.get(rutaActual).tiempo;
        }

        public void setOcupado(boolean b){
            this.ocupado = b;
        }

        public int getJob(){return this.job;}

    }

    public static class ruta{
        public int maquina;
        public int tiempo;

        ruta(int m, int t){
            this.maquina = m; this.tiempo = t;
        }
    }
}