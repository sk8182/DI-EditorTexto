package editortexto;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
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

    /////////////
    //BARRA UTILIDADES BAJA
    @FXML
    private ToolBar toolBarBot;
    @FXML
    private ToolBar toolbarBuscar;

    @FXML
    private Button btnSiguiente;
    @FXML
    private Button btnAnterior;
    

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos de Texto (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Muestra el diálogo y espera a que el usuario seleccione un archivo
        File archivoSeleccionado = fileChooser.showOpenDialog(new Stage());
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

        // Si es la primera vez que se guarda, o si el archivo guardado anteriormente ha sido eliminado o cambiado de ubicación
        if (archivoGuardado == null || !archivoGuardado.exists()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Archivo de Texto");

            // Establece el filtro para mostrar solo archivos de texto
            ExtensionFilter extFilter = new ExtensionFilter("Archivos de Texto (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);

            // Muestra el diálogo y espera a que el usuario seleccione la ubicación y el nombre del archivo
            archivoGuardado = fileChooser.showSaveDialog(new Stage());
        }

        if (archivoGuardado != null) {
            // Guarda el contenido del TextArea en el archivo seleccionado
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoGuardado))) {
                writer.write(areaTexto.getText());
            } catch (IOException e) {
                e.printStackTrace();
                // Manejo de errores al guardar el archivo
            }
        }
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

    ///////////////FIN FUNCIONES MENU VER//////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
}
// Obtener el texto seleccionado
//            String selectedText = textArea.getSelectedText();
//
//            // Verificar si se seleccionó algún texto
//            if (!selectedText.isEmpty()) {
//                // Cortar el texto seleccionado al portapapeles
//                Clipboard clipboard = Clipboard.getSystemClipboard();
//                ClipboardContent content = new ClipboardContent();
//                content.putString(selectedText);
//                clipboard.setContent(content);
//
//                // Borrar el texto seleccionado del área de texto
//                int start = textArea.getSelection().getStart();
//                int end = textArea.getSelection().getEnd();
//                textArea.replaceText(start, end, "");
//            }
