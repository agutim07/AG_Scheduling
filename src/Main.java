import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author Alberto Gutiérrez Morán
 */

public class Main {

    public static void main(String[] args){

//        JSS uno = new JSS(1); uno.addRutas(new int[][] {{1,1},{2,1}});
//        JSS dos = new JSS(2); dos.addRutas(new int[][] {{1,1},{2,1}});
//        JSS tres = new JSS(3); tres.addRutas(new int[][] {{2,2},{1,2}});
        JSS uno = new JSS(1); uno.addRutas(new int[][] {{1,1},{2,1},{3,3}});
        JSS dos = new JSS(2); dos.addRutas(new int[][] {{1,3},{2,2},{3,1}});
        JSS tres = new JSS(3); tres.addRutas(new int[][] {{3,2},{2,1},{1,3}});
        JSS cuatro = new JSS(4); cuatro.addRutas(new int[][] {{3,1},{2,4},{1,2}});
        ArrayList<JSS> jss = new ArrayList<>(Arrays.asList(uno,dos,tres,cuatro));

        ArrayList<Integer[]> cromosoma = new ArrayList<>();
        cromosoma.add(new Integer[]{1,2,4,3});
        cromosoma.add(new Integer[]{4,1,3,2});
        cromosoma.add(new Integer[]{4,3,1,2});

//        cromosoma.add(new Integer[]{1,2,3});
//        cromosoma.add(new Integer[]{3,2,1});

        int len = sBuilder(cromosoma,jss,true);
        System.out.println(len);
        ////////


        //MODELADO DEL PROBLEMA
        int s = 3; //Nº DE SIMBOLOS DEL ALFABETO
        int r = 5; //LONG DE LA CADENA

        //PARAMETROS DEL ALGORITMO
        int n = 50; //TAMAÑO DE LA POBLACION
        double pc = 0.8; //PROBABILIDAD DE CRUCE
        double pm = 1.0 / (r*n); //PROBABILIDAD DE MUTACION
        int t_max = 25;
        int num = 1000; //Nº ORIGINAL DE CASILLAS DE LA RULETA
        double pextraccion = 0.05; //PORCENTAJE DE LAS MUESTRAS QUE EXTRAEMOS AL INICIO DEL BUCLE Y LUEGO IMPORTAMOS
        boolean convergencia=false;

        //INICIO DEL ALGORITMO
//        int t=0;
//        int[] w = new int[n];   //INICIALIZAMOS LAS CADENAS
//        for(int i=0; i<n; i++){
//            int[] wj = new int[r]; String wjS="";
//            for(int j=0; j<r; j++){
//                wj[j] = (int) Math.floor(Math.random()*s);
//                wjS+=wj[j];
//            }
//            w[i] = Integer.parseInt(wjS);
//        }
//
//        int[] apt = new int[n];
//        for(int i=0; i<n; i++){apt[i] = funcionAptitud(w,i);}
//
//        int[] apt_gen = new int[t_max+1];
//        apt_gen[0]=0;
//        for(int i=0; i<n; i++){apt_gen[0]+=apt[i];}
//
//        double[] apt_m_gen = new double[t_max+1];
//        apt_m_gen[0] = apt_gen[0]/n;
//
//        //IMPRESION INICIAL:
//        System.out.println("Población antes del algoritmo: ");
//        for(int j=0; j<n; j++){
//            System.out.println(w[j]);
//        }
//        System.out.println("----------------------------------");
//
//        //CUERPO DEL ALGORITMO
//        while(t<t_max && !convergencia){
//            //GUARDAMOS LA POBLACION INICIAL PARA COMPARARLA CON LA FINAL
//            int[] w_inicial = new int[n];
//            for(int i=0; i<n; i++){w_inicial[i]=w[i];}
//
//            //EXTRAEMOS EL %x CON MAYOR APTITUD
//            int extraccion = (int) Math.floor(pextraccion*n);
//            int[] cadenaExtraida = extraerMayorAptitud(w,extraccion,n);
//
//            //SELECCION
//            int[] p = new int[n];
//            p[0] = apt[0] / apt_gen[t];
//            int[] c = new int[n];
//            c[0] = (int) Math.floor(p[0]*num) + 1;
//            int[] alfa = new int[n];
//            alfa[0] = 0;
//            int[] beta = new int[n];
//            beta[0] = alfa[0]+c[0]-1;
//
//            for(int i=1; i<n; i++){
//                p[i] = apt[i] / apt_gen[t];
//                c[i] = (int) Math.floor(p[i]*num) + 1;
//                alfa[i] = alfa[i-1] + c[i-1];
//                beta[i] = alfa[i] + c[i] -1;
//            }
//
//            int num_real_casillas = beta[n-1] + 1;
//
//            //SELECCION DE INDIVIDUOS
//            int[] w_new = new int[n];
//            for(int j=0; j<n; j++){
//                int cas = (int) Math.floor(Math.random()*num_real_casillas);
//                int i=-1;
//                for(int x=0; x<n; x++){
//                    if(cas>=alfa[x] && cas<=beta[x]){i=x;}
//                }
//                w_new[j] = w[i];
//            }
//            for(int i=0; i<n; i++){
//                w[i] = w_new[i];
//            }
//            //FIN SELECCION
//
//            //CROSSOVER
//            int[][] parejas = getCrossoverParejas(n,pc);
//            //intercambiamos el material genetico de las parejas
//            for(int i=0; i<(parejas[0].length); i++){
//                int posA = parejas[0][i]; int posB = parejas[1][i];
//                int genes = (int) Math.floor(Math.random()*(r-1)) + 1; //nº aleatorio entre 1 y r-1
//                int[] nuevosValores = recombinar(w[posA],w[posB],genes,r);
//                w[posA] = nuevosValores[0];
//                w[posB] = nuevosValores[1];
//            }
//            //FIN CROSSOVER
//
//            //MUTACION
//            for(int i=0; i<n; i++){
//                String cadena = String.valueOf(w[i]);
//                while(cadena.length()!=r) cadena = '0'+cadena;
//                for(int j=0; j<r; j++){
//                    double randomnum = Math.random();
//                    if(randomnum<pm){
//                        int randomdigit = (int) Math.floor(Math.random()*s);
//                        while(randomdigit==Character.getNumericValue(cadena.charAt(j))){
//                            randomdigit = (int) Math.floor(Math.random()*s);
//                        }
//                        cadena = cadena.substring(0,j)+randomdigit+cadena.substring(j+1);
//                    }
//                }
//                w[i] = Integer.valueOf(cadena);
//            }
//            //FIN MUTACION
//
//            //EXTRAEMOS UN %x ALEATORIO Y AÑADIMOS EL %x CON MAYOR APTITUD EXTRAIDO ANTES
//            w = extraerImportar(w,cadenaExtraida,n);
//
//            //COMPROBAMOS SI EL ALGORITMO SE HA ESTANCADO (LA POBLACION HA CONVERGIDO)
//            convergencia = compararDiferencia(w_inicial,w);
//            System.out.println(convergencia);
//
//            t++;
//            for(int i=0; i<n; i++){apt[i] = funcionAptitud(w,i);}
//            apt_gen[t]=0;
//            for(int i=0; i<n; i++){apt_gen[t]+=apt[i];}
//            apt_m_gen[t] = apt_gen[t]/n;
//        }
//
//        //IMPRESION FINAL
//        System.out.println("Población después del algoritmo: ");
//        for(int j=0; j<n; j++){
//            System.out.println(w[j]);
//        }
//        System.out.println("----------------------------------");
//
//        //SOLUCION (MEJOR INDIVIDUO)
//        int mejorPos = max(apt);
//        int mejor_a = apt[mejorPos];
//        int mejor_w = w[mejorPos];
//        System.out.println("Mejor aptitud: "+mejor_a + " - Mejor cadena: " + mejor_w);
    }

    private static boolean compararDiferencia(int[] wOld, int[] wNew){
        double porcentajeIgualdad = 0.97; //SI HAY UN 97% DE IGUALDAD ENTRE UNO Y OTRO, CONSIDERAMOS QUE HAY CONVERGENCIA
        int poblacion = wNew.length;
        int igualdad = 0;

        for(int i=0; i<poblacion; i++){
            for(int j=0; j<poblacion; j++){
                if(wNew[j]==wOld[i]){
                    igualdad++;
                    break;
                }
            }
        }

        double porcentaje = ((double) igualdad)/poblacion;

        if(porcentaje>=porcentajeIgualdad) return true;

        return false;
    }

    private static int[] extraerMayorAptitud(int w[], int num, int n){
        int[] apt = new int[n];
        for(int i=0; i<n; i++){apt[i] = funcionAptitud(w,i);}
        int[] mejores = order(apt);

        int[] out = new int[num];
        for(int i=0; i<num; i++){
            out[i] = w[mejores[i]];
        }

        return out;
    }

    private static int[] extraerImportar(int w[], int cadena[], int n){
        int num = cadena.length;
        int[] posAleatorias = new int[num];

        for(int i=0; i<num; i++){
            int posAleatoria = (int) Math.floor(Math.random()*n);
            while(checkExists(posAleatorias, posAleatoria)) posAleatoria = (int) Math.floor(Math.random()*n);
            w[posAleatoria] = cadena[i];
        }

        return w;
    }

    private static int[] recombinar(int a, int b, int genes, int r){
        String as = String.valueOf(a);
        while(as.length()!=r) as = '0'+as;
        String bs = String.valueOf(b);
        while(bs.length()!=r) bs = '0'+bs;

        String sub_a = as.substring(genes);
        String sub_b = bs.substring(genes);

        String finalas = as.substring(0,genes)+sub_b;
        String finalbs = bs.substring(0,genes)+sub_a;
        return new int[]{Integer.valueOf(finalas),Integer.valueOf(finalbs)};
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

    private static int funcionAptitud(int[] w, int x){
        //FUNCION DE APTITUD: LONGUITUD DE 2'S EN LA CADENA
        int out=0; String wS = String.valueOf(w[x]);
        for(int i=0; i<wS.length(); i++){
            if(wS.charAt(i)=='2') out++;
        }
        return out;
    }

    private static int[] order(int[] x){
        int[] maxList = new int[x.length];

        for(int i=0; i<x.length; i++){
            int maxPos = max(x);
            maxList[i] = maxPos;
            x[maxPos] = -1;
        }

        return maxList;
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
        int[] jobEnMaquina = new int[nummaquinas];
        boolean[] maquinaFinish = new boolean[nummaquinas];
        ArrayList<ArrayList<Integer>> maquinas = new ArrayList<>();

        for(int i=0; i<nummaquinas; i++){
            maquinas.add(new ArrayList<>());
            jobEnMaquina[i] = cromosoma.get(i)[0];
            maquinaFinish[i] = false;
        }

        //RECORREMOS EL BUCLE HASTA EL FIN DE TODOS LOS JOBS
        while(!checkFinish(instancia)){
            //RECORREMOS MAQUINA A MAQUINA Y SUS JOBS DE PREFERENCIA, SI SE PUEDE SE AÑADEN, SINO SE ESPERA
            for(int i=0; i<nummaquinas; i++){
                if(!maquinaFinish[i] && maquinas.get(i).size()==tiempo){
                    int next = checkJobForMachine(instancia,i,jobEnMaquina[i]);
                    if(next>0){
                        JSS job = getJob(instancia,jobEnMaquina[i]);
                        job.setOcupado(true);
                        for(int j=0; j<next; j++) {
                            maquinas.get(i).add(jobEnMaquina[i]);
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
                    for(int x=0; x<cromosoma.get(i).length; x++){
                        if(jobEnMaquina[i]==cromosoma.get(i)[x]){
                            if(x==(cromosoma.get(i).length-1)){
                                maquinaFinish[i]=true;
                            }else{
                                jobEnMaquina[i]=cromosoma.get(i)[x+1];
                            }
                            break;
                        }
                    }
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
                    if (maquinas.get(i).get(j) == -1) {
                        System.out.print(" |  ");
                    } else {
                        System.out.print(" | " + maquinas.get(i).get(j));
                    }
                }
                System.out.println();
            }
        }

        return tiempo;
    }

    public static int checkJobForMachine(ArrayList<JSS> jss, int m, int jobnum){
        m++;
        JSS job = getJob(jss,jobnum);
        if(job.ocupado==true || job.maquinaSolicitada!=m){
            return -1;
        }

        return job.getNextRutaLen();
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