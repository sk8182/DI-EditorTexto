package editortexto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author julio
 */
public class FXMLVistaController implements Initializable {

    @FXML
    private VBox contenedorTop;

    @FXML
    private MenuBar menuTop;
    //menuTop contiene:

    //ITEMS ARCHIVO
    @FXML
    private Menu menuArchivo;

    @FXML
    private MenuItem itemNuevo;
    @FXML
    private MenuItem itemAbrir;
    private File archivoSeleccionado;  // Declaración de la variable
    @FXML
    private Menu menuRecientes;

    private List<File> archivosAbiertos = new ArrayList<>();
    private int maxTamanoHistorial = 10; // Establece el tamaño máximo del historial

    @FXML
    private MenuItem itemGuardar;
    @FXML
    private MenuItem itemGuardarComo;
    @FXML
    private MenuItem itemSalir;

    private File archivoGuardado;  // Mantiene una referencia al archivo guardado
    ///////////////////////////////////

    //ITEMS EDICION
    @FXML
    private MenuItem itemBuscar;

    private List<Integer> indiceCoincidencias = new ArrayList<>();
    private String textoABuscar;
    private int indiceActual = -1;

    @FXML
    private MenuItem itemReemplazar;
    ////////////////////////////////////

    //ITEMS Estilo
    @FXML
    private Menu itemEstilo;

    @FXML
    private MenuItem itemNegrita;
    @FXML
    private MenuItem itemCursiva;
    ////////////////////////////////////

    //ITEMS FORMATO
    @FXML
    private MenuItem itemArial;
    @FXML
    private MenuItem itemCourier;
    @FXML
    private MenuItem itemVerdana;

    @FXML
    private MenuItem itemMay;
    @FXML
    private MenuItem itemMin;
    @FXML
    private MenuItem itemTamDoce;
    @FXML
    private MenuItem itemTamDieciseis;
    @FXML
    private MenuItem itemTamDieciocho;
    /////////////////////////////////////////////////

    //ITEMS VER///////////
    @FXML
    private MenuItem itemBarraBotones;

    /////////////////////////////
    //EMPIEZA EL MENU BOTONES
    @FXML
    private MenuBar menuBotones;

    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnAbrir;
    @FXML
    private MenuItem btnGuardar;
    @FXML
    private MenuItem btnGuardarComo;

    //AREA TEXTO
    @FXML
    private TextArea areaTexto;

    //ZONA BAJA
    @FXML
    private VBox contenedorBot;

    @FXML
    private Button btnSiguiente;
    @FXML
    private Button btnAnterior;
    @FXML
    private Label labelCursor;
    @FXML
    private Label labelCuentaPalabras;
    @FXML
    private Label labelCuentaLineas;
    @FXML
    private Label labelCodific;
    @FXML
    private MenuItem itemBarraEstado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Agregar un listener al contenido del TextArea para detectar cambios
        areaTexto.textProperty().addListener((observable, oldValue, newValue) -> {
            // Actualizar la barra de estado cuando el contenido cambia
            actualizarBarraDeEstado();
        });
    }

    /////////////////////////////////////////////////////////////////////
    ///////////////////////FUNCIONES PARA EL MENU ARCHIVO
    @FXML
    private void nuevaVentana(ActionEvent event) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXMLVista.fxml"));

            Stage nuevaVentanaStage = new Stage();
            nuevaVentanaStage.setScene(new Scene(root));
            nuevaVentanaStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            // Manejo de errores
        }
    }

    @FXML
    private void abrirArchivo(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Archivo de Texto");

        // Establece el filtro para mostrar solo archivos de texto
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos de Texto (*.txt, *.csv, *.log)", "*.txt", "*.csv", "*.log");
        fileChooser.getExtensionFilters().add(extFilter);

        // Muestra el diálogo y espera a que el usuario seleccione un archivo
        archivoSeleccionado = fileChooser.showOpenDialog(new Stage());

        agregarAlHistorial(archivoSeleccionado);

        if (archivoSeleccionado != null) {
            // Lee el contenido del archivo y lo carga en el TextArea
            try (BufferedReader br = new BufferedReader(new FileReader(archivoSeleccionado))) {
                String linea;
                StringBuilder contenido = new StringBuilder();

                while ((linea = br.readLine()) != null) {
                    contenido.append(linea).append("\n");
                }

                areaTexto.setText(contenido.toString());
                mostrarHistorialArchivos();

            } catch (IOException e) {
                e.printStackTrace();

            }

        }

    }

    private void agregarAlHistorial(File archivo) {
        archivosAbiertos.remove(archivo); // Eliminar duplicados
        archivosAbiertos.add(0, archivo); // Agregar al principio de la lista
        if (archivosAbiertos.size() > maxTamanoHistorial) {
            archivosAbiertos.remove(archivosAbiertos.size() - 1); // Eliminar elementos más antiguos
        }
        mostrarHistorialArchivos();
    }

    private List<File> obtenerHistorialArchivos() {
        return new ArrayList<>(archivosAbiertos);
    }

    private void mostrarHistorialArchivos() {

        // Limpia el menú de documentos recientes
        menuRecientes.getItems().clear();

        // Agrega los elementos del historial al menú de documentos recientes
        for (File archivo : obtenerHistorialArchivos()) {
            MenuItem menuItem = new MenuItem(archivo.getName());

            // Asigna el EventHandler al hacer clic en el elemento del historial
            menuItem.setOnAction(e -> cargarContenidoArchivo(archivo));

            menuRecientes.getItems().add(menuItem);
        }

    }

    private void cargarContenidoArchivo(File archivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            StringBuilder contenido = new StringBuilder();

            while ((linea = br.readLine()) != null) {
                contenido.append(linea).append("\n");
            }

            areaTexto.setText(contenido.toString());
            agregarAlHistorial(archivo); // Actualiza el historial al cargar un archivo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void guardar(ActionEvent event) {

        // Si hay un archivo abierto, guardar en ese archivo directamente
        if (archivoSeleccionado != null) {
            guardarEnArchivo(archivoSeleccionado);
        } else {
            // Si no hay archivo abierto, muestra el cuadro de diálogo para seleccionar la ubicación
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Archivo de Texto");

            // Establece el filtro para mostrar solo archivos de texto
            ExtensionFilter extFilter = new ExtensionFilter("Archivos de Texto (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);

            // Muestra el diálogo y espera a que el usuario seleccione la ubicación y el nombre del archivo
            File archivoGuardado = fileChooser.showSaveDialog(new Stage());

            if (archivoGuardado != null) {
                // Guarda el contenido del TextArea en el archivo seleccionado
                guardarEnArchivo(archivoGuardado);
            }
        }
    }

    private void guardarEnArchivo(File archivo) {
        // Guarda el contenido del TextArea en el archivo seleccionado
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo))) {
            writer.write(areaTexto.getText());
            archivoSeleccionado = archivo; // Actualiza el archivo abierto
            archivoGuardado = archivo; // Actualiza el archivo guardado
        } catch (IOException e) {
            e.printStackTrace();
            // Manejo de errores al guardar el archivo
        }
    

//        // Si es la primera vez que se guarda, o si el archivo guardado anteriormente ha sido eliminado o cambiado de ubicación
//        if (archivoGuardado == null || !archivoGuardado.exists()) {
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Guardar Archivo de Texto");
//
//            // Establece el filtro para mostrar solo archivos de texto
//            ExtensionFilter extFilter = new ExtensionFilter("Archivos de Texto (*.txt)", "*.txt");
//            fileChooser.getExtensionFilters().add(extFilter);
//
//            // Muestra el diálogo y espera a que el usuario seleccione la ubicación y el nombre del archivo
//            archivoGuardado = fileChooser.showSaveDialog(new Stage());
//        }
//
//        if (archivoGuardado != null) {
//            // Guarda el contenido del TextArea en el archivo seleccionado
//            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoGuardado))) {
//                writer.write(areaTexto.getText());
//            } catch (IOException e) {
//                e.printStackTrace();
//                // Manejo de errores al guardar el archivo
//            }
//        }
}

@FXML
private void guardarComo(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Archivo de Texto");

        // Establece el filtro para mostrar solo archivos de texto
        ExtensionFilter extFilter = new ExtensionFilter("Archivos de Texto (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Muestra el diálogo y espera a que el usuario seleccione la ubicación y el nombre del archivo
        File archivoGuardar = fileChooser.showSaveDialog(new Stage());

        if (archivoGuardar != null) {
            // Guarda el contenido del TextArea en el archivo seleccionado
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoGuardar))) {
                writer.write(areaTexto.getText());
            } catch (IOException e) {
                e.printStackTrace();
                // Manejo de errores al guardar el archivo
            }
        }

    }

    @FXML
private void salir(ActionEvent event) {

        Platform.exit();// Método preferido para cerrar una aplicación JavaFX

    }

    ///////////////FIN FUNCIONES MENU ARCHIVO//////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    ///////////////////////FUNCIONES PARA EL MENU EDICIóN
    @FXML
private void buscarCoincidencias(ActionEvent event) {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buscar Coincidencias");
        dialog.setHeaderText("Ingrese el texto a buscar:");

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(texto -> {
            textoABuscar = texto;
            indiceCoincidencias.clear();
            String contenido = areaTexto.getText();
            int index = contenido.indexOf(textoABuscar);

            while (index != -1) {
                indiceCoincidencias.add(index);
                index = contenido.indexOf(textoABuscar, index + 1);
            }

            if (!indiceCoincidencias.isEmpty()) {
                indiceActual = 0;
                seleccionarCoincidenciaActual();
            } else {
                mostrarAlerta("Texto no encontrado", "El texto especificado no se encontró en el documento.");
            }
        });

    }

    @FXML
private void buscarSiguiente(ActionEvent event) {

        if (!indiceCoincidencias.isEmpty() && indiceActual < indiceCoincidencias.size() - 1) {
            indiceActual++;
            seleccionarCoincidenciaActual();
        }

    }

    @FXML
private void buscarAnterior(ActionEvent event) {

        if (!indiceCoincidencias.isEmpty() && indiceActual > 0) {
            indiceActual--;
            seleccionarCoincidenciaActual();
        }
    }

    private void seleccionarCoincidenciaActual() {
        if (!indiceCoincidencias.isEmpty() && indiceActual < indiceCoincidencias.size()) {
            areaTexto.selectRange(indiceCoincidencias.get(indiceActual), indiceCoincidencias.get(indiceActual) + textoABuscar.length());
        }
    }

    @FXML
private void reemplazar(ActionEvent event) {
        TextInputDialog dialogBuscar = new TextInputDialog();
        dialogBuscar.setTitle("Reemplazar");
        dialogBuscar.setHeaderText("Ingrese el texto a buscar:");
        Optional<String> resultadoBuscar = dialogBuscar.showAndWait();

        resultadoBuscar.ifPresent(textoABuscar -> {
            TextInputDialog dialogReemplazar = new TextInputDialog();
            dialogReemplazar.setTitle("Reemplazar");
            dialogReemplazar.setHeaderText("Ingrese el texto de reemplazo:");
            Optional<String> resultadoReemplazar = dialogReemplazar.showAndWait();

            resultadoReemplazar.ifPresent(textoReemplazo -> {
                String contenido = areaTexto.getText();
                String nuevoContenido = contenido.replace(textoABuscar, textoReemplazo);
                areaTexto.setText(nuevoContenido);
            });
        });
    }

    private void mostrarAlerta(String titulo, String contenido) {

        //ESTE METODO ES DE BUSCAR, SI NO HAY COINCIDENCIA SALTA ALERTA
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    ///////////////FIN FUNCIONES MENU EDICIóN//////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    
    
    /////////////////////////////////////////////////////////////////////
    ///////////////////////FUNCIONES PARA EL MENU FORMATO
    @FXML
private void estiloNegrita(ActionEvent event) {

        String estiloActual = areaTexto.getStyle();
        areaTexto.setStyle(estiloActual + "-fx-font-weight: bold;");

    }

    @FXML
private void estiloCursiva(ActionEvent event) {

        String estiloActual = areaTexto.getStyle();
        areaTexto.setStyle(estiloActual + "-fx-font-weight: normal; -fx-font-style: italic;");

    }

    @FXML
private void textoMayusculas(ActionEvent event) {

        String selecTexto = areaTexto.getSelectedText();

        // Verificar si se seleccionó algún texto
        if (!selecTexto.isEmpty()) {

            areaTexto.replaceSelection(selecTexto.toUpperCase());
        }

    }

    @FXML
private void textoMinusculas(ActionEvent event) {

//        String textoActual = areaTexto.getText();
//        areaTexto.setText(textoActual.toLowerCase());
        String selecTexto = areaTexto.getSelectedText();

        // Verificar si se seleccionó algún texto
        if (!selecTexto.isEmpty()) {

            areaTexto.replaceSelection(selecTexto.toLowerCase());
        }

    }

    @FXML
private void cambiarFuenteArial(ActionEvent event) {

        areaTexto.setStyle("-fx-font-family: Arial;");

    }

    @FXML
private void cambiarFuenteCourier(ActionEvent event) {

        areaTexto.setStyle("-fx-font-family: Courier New;");

    }

    @FXML
private void cambiarFuenteVerdana(ActionEvent event) {

        areaTexto.setStyle("-fx-font-family: Verdana;");

    }

    @FXML
private void cambiarTamanio12(ActionEvent event) {
        cambiarTamanioTexto(12);
    }

    @FXML
private void cambiarTamanio16(ActionEvent event) {
        cambiarTamanioTexto(16);
    }

    @FXML
private void cambiarTamanio18(ActionEvent event) {
        cambiarTamanioTexto(18);
    }

    private void cambiarTamanioTexto(int tamanio) {
        areaTexto.setStyle("-fx-font-size: " + tamanio + "px;");
    }
    ///////////////FIN FUNCIONES MENU FORMATO//////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////
    ///////////////////////FUNCIONES PARA EL MENU VER
    @FXML
private void verUocultarBotones(ActionEvent event) {

        if (menuBotones.isVisible()) {

            menuBotones.setVisible(false);

        } else {
            // Los botones están ocultos, entonces los mostramos
            menuBotones.setVisible(true);

        }
    }
    
    @FXML
private void verUocultarBarraEstado(ActionEvent event) {
        
         if (contenedorBot.isVisible()) {

            contenedorBot.setVisible(false);

        } else {
            // Los botones están ocultos, entonces los mostramos
            contenedorBot.setVisible(true);

        }
         
    }

    ///////////////FIN FUNCIONES MENU VER//////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    
    /////////////////////////////////////////////////////////////////////////////////////////
    ////////////////FUNCIONES BARRA ESTADO/////////////////////////////////////////////////
    private void actualizarBarraDeEstado() {
        // Puedes obtener la posición del cursor desde el área de texto
        int posicionCursor = areaTexto.getCaretPosition();
        labelCursor.setText("Posición del Cursor: " + posicionCursor);

        // Puedes obtener el recuento de palabras y líneas desde el contenido del área de texto
        String contenido = areaTexto.getText();
        int recuentoPalabras = obtenerRecuentoPalabras();
        int recuentoLineas = obtenerRecuentoLineas();

        labelCuentaLineas.setText("Recuento de Líneas: " + recuentoLineas);

        labelCuentaPalabras.setText("Recuento de Palabras: " + recuentoPalabras);

        // Puedes obtener la información de codificación desde el archivo abierto actualmente
        // (Assumimos que el método obtenerCodificacion() devuelve la codificación actual)
        String codificacion = obtenerCodificacion();
        labelCodific.setText("Codificación: " + codificacion);
    }

    private int obtenerRecuentoPalabras() {
        String texto = areaTexto.getText();
        if (texto == null || texto.isEmpty()) {
            return 0;
        }

        // Utilizar expresiones regulares para dividir el texto en palabras
        String[] palabras = texto.split("\\s+");
        return palabras.length;
    }

    private int obtenerRecuentoLineas() {
        String texto = areaTexto.getText();
        if (texto == null || texto.isEmpty()) {
            return 0;
        }

        // Dividir el texto en líneas y contar la cantidad de líneas
        String[] lineas = texto.split("\\r?\\n");
        return lineas.length;
    }

    private String obtenerCodificacion() {
    if (archivoSeleccionado != null) {
        // Si hay un archivo asociado, utiliza la lógica anterior
        return obtenerCodificacionArchivo(archivoSeleccionado);
    } else {
        // Si no hay archivo asociado, asume que el texto en el TextArea está codificado en UTF-16
        return "UTF-16";
    }
}
    
    
    private String obtenerCodificacionArchivo(File archivo) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(archivo), Charset.defaultCharset()))) {
            br.mark(4096); // Marcar el inicio del flujo para volver a él si es necesario

            // Lee algunos bytes del archivo para analizar la codificación
            char[] buffer = new char[4096];
            int bytesRead = br.read(buffer);

            // Intenta adivinar la codificación
            String codificacion = Charset.defaultCharset().name();
            br.reset(); // Volver al inicio del flujo

            // Puedes personalizar este código para realizar una mejor adivinanza de la codificación si es necesario
            return codificacion;
        } catch (IOException e) {
            e.printStackTrace();
            return "No se pudo determinar la codificación";
        }
    }

    ///////////////////////////FIN FUNCIONES BARRA ESTADO//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
}
