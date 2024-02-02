package modelo;

/**
 * La clase EditorModelo se encarga de la lógica de la aplicación, como la
 * gestión de archivos y la edición de texto. Esta clase sigue el patrón de
 * diseño Modelo-Vista-Controlador (MVC), donde esta clase representa el
 * "modelo".
 *
 * @author julio
 */
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class EditorModelo {

    private List<File> archivosAbiertos = new ArrayList<>();
    private int maxTamanoHistorial = 10;
    private File archivoGuardado;
    private List<Integer> indiceCoincidencias = new ArrayList<>();
    private String textoABuscar;
    private int indiceActual = -1;

    /**
     * Lee el contenido de un archivo y lo devuelve como una cadena de texto.
     *
     * @param archivo El archivo a leer.
     * @return El contenido del archivo como una cadena de texto.
     * @throws IOException Si ocurre un error al leer el archivo.
     */
    public String leerArchivo(File archivo) throws IOException {
        StringBuilder contenido = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                contenido.append(linea).append("\n");
            }
        }
        return contenido.toString();
    }

    /**
     * Agrega un archivo al historial de archivos abiertos. Elimina duplicados,
     * agrega al principio de la lista y mantiene el historial dentro del límite
     * establecido por {@code maxTamanoHistorial}.
     *
     * @param archivo El archivo a agregar al historial.
     */
    public void agregarAlHistorial(File archivo) {
        archivosAbiertos.remove(archivo); // Eliminar duplicados
        archivosAbiertos.add(0, archivo); // Agregar al principio de la lista
        if (archivosAbiertos.size() > maxTamanoHistorial) {
            archivosAbiertos.remove(archivosAbiertos.size() - 1); // Eliminar elementos más antiguos
        }

    }

    /**
     * Obtiene una copia de la lista actual del historial de archivos abiertos.
     *
     * @return Una nueva lista que representa el historial de archivos abiertos.
     */
    public List<File> obtenerHistorialArchivos() {
        return new ArrayList<>(archivosAbiertos);
    }

    /**
     * Guarda el contenido en un archivo.
     *
     * @param archivo El archivo en el que se guardará el contenido.
     * @param contenido El contenido a guardar en el archivo.
     * @throws IOException Si ocurre un error al guardar el contenido en el archivo.
     */
    public void guardarEnArchivoModelo(File archivo, String contenido) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write(contenido);
        }
    }

    /**
     * Actualiza el archivo guardado.
     *
     * @param archivo El archivo a actualizar.
     */
    public void actualizarArchivos(File archivo) {
        archivoGuardado = archivo; // Actualiza el archivo guardado
    }

    /**
     * Busca coincidencias del texto ingresado en el contenido y devuelve una lista de índices de las coincidencias.
     *
     * @param contenido El contenido en el que se buscarán las coincidencias.
     * @param textoABuscar El texto a buscar en el contenido.
     * @return Una lista de índices de las coincidencias.
     */
    public List<Integer> buscarCoincidencias(String contenido, String textoABuscar) {
        List<Integer> indiceCoincidencias = new ArrayList<>();
        int index = contenido.indexOf(textoABuscar);

        while (index != -1) {
            indiceCoincidencias.add(index);
            index = contenido.indexOf(textoABuscar, index + 1);
        }

        return indiceCoincidencias;
    }

    /**
     * Busca y selecciona la siguiente coincidencia en el contenido.
     *
     * @param indiceCoincidencias La lista de índices de las coincidencias.
     * @param indiceActual El índice de la coincidencia actual.
     * @return El índice de la siguiente coincidencia.
     */
    public int buscarSiguiente(List<Integer> indiceCoincidencias, int indiceActual) {
        if (!indiceCoincidencias.isEmpty() && indiceActual < indiceCoincidencias.size() - 1) {
            indiceActual++;
        }
        return indiceActual;
    }

    /**
     * Busca y selecciona la coincidencia anterior en el contenido.
     *
     * @param indiceCoincidencias La lista de índices de las coincidencias.
     * @param indiceActual El índice de la coincidencia actual.
     * @return El índice de la coincidencia anterior.
     */
    public int buscarAnterior(List<Integer> indiceCoincidencias, int indiceActual) {
        if (!indiceCoincidencias.isEmpty() && indiceActual > 0) {
            indiceActual--;
        }
        return indiceActual;
    }

    /**
     * Reemplaza las ocurrencias del texto ingresado por el usuario con el texto nuevo.
     *
     * @param contenido El contenido en el que se realizará el reemplazo.
     * @param textoABuscar El texto a buscar en el contenido.
     * @param textoReemplazo El texto nuevo que reemplazará al texto ingresado.
     * @return El contenido con las ocurrencias del texto ingresado reemplazadas por el texto nuevo.
     */
    public String reemplazar(String contenido, String textoABuscar, String textoReemplazo) {
        return contenido.replace(textoABuscar, textoReemplazo);
    }

    /**
     * Obtiene la codificación de caracteres de un archivo. Lee los primeros
     * bytes del archivo para analizar y determinar la codificación.
     *
     * @param archivo El archivo del cual se va a determinar la codificación.
     * @return La codificación de caracteres del archivo o un mensaje de error
     * si no se puede determinar.
     */
    public String obtenerCodificacionArchivo(File archivo) {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), Charset.defaultCharset()))) {
            br.mark(4096); // Marco el inicio del flujo para volver a él si es necesario

            //analizar la codificación
            char[] buffer = new char[4096];
            int bytesRead = br.read(buffer);

            String codificacion = Charset.defaultCharset().name();
            br.reset(); // Volver al inicio del flujo

            return codificacion;
        } catch (IOException e) {
            e.printStackTrace();
            return "No se pudo determinar la codificación";
        }
    }

}
