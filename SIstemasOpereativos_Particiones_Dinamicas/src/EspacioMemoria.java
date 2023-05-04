public class EspacioMemoria {

        int inicio;
        int fin;
        int tamaño;

        public EspacioMemoria(int inicio, int fin) {
            this.inicio = inicio;
            this.fin = fin;
            this.tamaño = fin - inicio + 1;
        }

}
