import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class GestionMemoria {
    static int memoriaTotal;
    static int memoriaSO;
    static ArrayList<Proceso> procesos;
    static ArrayList<EspacioMemoria> espaciosMemoria;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Solicitar el total de la memoria y la parte asignada al SO
        System.out.print("Ingrese el total de memoria: ");
        memoriaTotal = sc.nextInt();
        System.out.print("Ingrese el tamaño de memoria asignado al SO: ");
        memoriaSO = sc.nextInt();

        // Calcular la memoria disponible para los procesos
        int memoriaProcesos = memoriaTotal - memoriaSO;

        // Solicitar información de cada proceso
        System.out.print("Ingrese la cantidad de procesos: ");
        int n = sc.nextInt();
        procesos = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            System.out.println("Proceso #" + (i + 1));
            System.out.print("Nombre: ");
            String nombre = sc.next();
            System.out.print("Tamaño: ");
            int tamaño = sc.nextInt();
            System.out.print("Tiempo de llegada: ");
            int llegada = sc.nextInt();
            System.out.print("Duración: ");
            int duracion = sc.nextInt();
            Proceso proceso = new Proceso(nombre, tamaño, llegada, duracion);
            procesos.add(proceso);
        }

        // Ordenar los procesos por tiempo de llegada
        Collections.sort(procesos, new Comparator<Proceso>() {
            @Override
            public int compare(Proceso p1, Proceso p2) {
                return p1.llegada - p2.llegada;
            }
        });

        // Inicializar la lista de espacios de memoria

        espaciosMemoria = new ArrayList<>();
        EspacioMemoria espacioInicial = new EspacioMemoria(memoriaSO, memoriaTotal - 1);
        espaciosMemoria.add(espacioInicial);

        // Ejecutar el algoritmo de asignación de memoria en cada instante
        System.out.println("Presione F9 para ejecutar el algoritmo de asignación de memoria.");
        while (true) {
            String tecla = sc.next();
            if (tecla.equals("F")) {

                boolean todosFinalizados = true;
                for (Proceso proceso : procesos) {
                    if (proceso.duración > 0) {
                        todosFinalizados = false;
                        break;
                    }
                }
                if (todosFinalizados) {
                    System.out.println("Todos los procesos han finalizado.");
                    break;
                }
                System.out.printf("%50s \n","--------------------------------------------------------------");
                System.out.printf("%1s %47s \n","| Instante: " + getInstanteActual(),"|");
                System.out.printf("%1s %53s \n","| SO: " + memoriaSO,"|");
                System.out.printf("%50s \n","--------------------------------------------------------------");
                for (Proceso proceso : procesos) {
                    if (proceso.llegada <= getInstanteActual() && proceso.duración > 0 && !proceso.asignado) {
                        int espacioAsignado = asignarMemoria(proceso.tamaño);
                        if (espacioAsignado >= 0) {
                            proceso.espacioAsignado = espacioAsignado;
                            proceso.asignado = true;
                            System.out.println(" Proceso " + proceso.nombre + " asignado al espacio de memoria " + espacioAsignado);

                            System.out.println("--------------------------------------------------------------");
                        } else {
                            System.out.println("--------------------------------------------------------------");
                            System.out.println("No hay suficiente memoria disponible para el proceso " + proceso.nombre);
                            System.out.println("--------------------------------------------------------------");
                        }
                    }
                    if (proceso.asignado) {
                        proceso.ejecutar();
                        if (proceso.tiempoFinalización > 0) {
                            liberarMemoria(proceso.espacioAsignado);
                            System.out.println("Proceso " + proceso.nombre + " ha finalizado y se ha liberado el espacio de memoria " + proceso.espacioAsignado);
                        }
                    }
                }
            }
        }
    }

    static int getInstanteActual() {
        int instanteActual = 0;
        for (Proceso proceso : procesos) {
            if (proceso.tiempoEjecución > instanteActual) {
                instanteActual = proceso.tiempoEjecución;
            }
        }
        return instanteActual;
    }

    static int asignarMemoria(int tamaño) {
        for (EspacioMemoria espacio : espaciosMemoria) {
            if (espacio.tamaño >= tamaño) {
                int inicio = espacio.inicio;
                int fin = inicio + tamaño - 1;
                if (espacio.tamaño == tamaño) {
                    espaciosMemoria.remove(espacio);
                } else {
                    espacio.inicio = fin + 1;
                    espacio.tamaño = espacio.tamaño - tamaño;
                }
                EspacioMemoria nuevoEspacio = new EspacioMemoria(inicio, fin);
                espaciosMemoria.add(nuevoEspacio);
                return inicio;
            }
        }
        return -1;
    }

    static void liberarMemoria(int espacio) {
        EspacioMemoria espacioLiberado = null;
        for (EspacioMemoria e : espaciosMemoria) {
            if (e.inicio == espacio) {
                espacioLiberado = e;
                break;
            }
        }

        if (espacioLiberado != null) {
            espaciosMemoria.remove(espacioLiberado);
            if (espaciosMemoria.size() > 0) {
                EspacioMemoria espacioAnterior =null;
                EspacioMemoria espacioSiguiente = null;
                for (EspacioMemoria e : espaciosMemoria) {
                    if (e.inicio < espacioLiberado.inicio && (espacioAnterior == null || e.inicio > espacioAnterior.inicio)) {
                        espacioAnterior = e;
                    }
                    if (e.inicio > espacioLiberado.inicio && (espacioSiguiente == null || e.inicio < espacioSiguiente.inicio)) {
                        espacioSiguiente = e;
                    }
                }
                if (espacioAnterior != null && espacioAnterior.inicio + espacioAnterior.tamaño == espacioLiberado.inicio) {
                    espacioAnterior.tamaño += espacioLiberado.tamaño;
                    espacioLiberado = espacioAnterior;
                }
                if (espacioSiguiente != null && espacioLiberado.inicio + espacioLiberado.tamaño == espacioSiguiente.inicio) {
                    espacioLiberado.tamaño += espacioSiguiente.tamaño;
                    espaciosMemoria.remove(espacioSiguiente);
                }
            }
        }
    }
}
