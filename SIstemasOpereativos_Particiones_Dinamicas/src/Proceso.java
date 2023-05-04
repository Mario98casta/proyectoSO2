public class Proceso {
    String nombre;
    int tamaño;
    int llegada;
    int duración;
    int tiempoEjecución;
    int tiempoFinalización;
    int espacioAsignado;
    boolean asignado;

    public Proceso(String nombre, int tamaño, int llegada, int duración) {
        this.nombre = nombre;
        this.tamaño = tamaño;
        this.llegada = llegada;
        this.duración = duración;
        this.tiempoEjecución = 0;
        this.tiempoFinalización = -1;
        this.espacioAsignado = -1;
        this.asignado = false;
    }

    public void ejecutar() {
        this.tiempoEjecución++;
        this.duración--;
        if (this.duración == 0) {
            this.tiempoFinalización = this.tiempoEjecución + this.llegada;
        }
    }
}
