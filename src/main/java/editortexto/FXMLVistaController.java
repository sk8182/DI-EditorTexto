package editortexto;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import modelo.EditorModelo;

/**
 * Controlador para la interfaz gráfica de usuario del editor de texto. Utiliza
 * JavaFX para la construcción y gestión de la GUI.
 *
 * @author julio
 */
public class FXMLVistaController implements Initializable {

    private EditorModelo modelo = new EditorModelo();
    
    @FXML
    private VBox contenedorTop;

    //MENU TOP
    @FXML
    private MenuBar menuTop;

    //MENU ARCHIVO
    @FXML
    private Menu menuArchivo;
    @FXML
    private MenuItem itemNuevo;
    @FXML
    private MenuItem itemAbrir;

    private File archivoSeleccionado;  // Declaración de la variable

    @FXML
    private Menu menuRecientes;

    @FXML
    private MenuItem itemGuardar;

    @FXML
    private MenuItem itemGuardarComo;
    @FXML
    private MenuItem itemSalir;

    //MENU EDICION
    @FXML
    private MenuItem itemBuscar;
    @FXML
    private MenuItem itemReemplazar;

    private List<Integer> indiceCoincidencias = new ArrayList<>();
    private String textoABuscar;
    private int indiceActual = -1;

    //MENU ESTILO
    @FXML
    private Menu itemEstilo;
    @FXML
    private MenuItem itemNegrita;
    @FXML
    private MenuItem itemCursiva;

    //MENU FORMATO
    @FXML
    private MenuItem itemMay;
    @FXML
    private MenuItem itemMin;
    @FXML
    private MenuItem itemArial;
    @FXML
    private MenuItem itemCourier;
    @FXML
    private MenuItem itemVerdana;
    @FXML
    private MenuItem itemTamDoce;
    @FXML
    private MenuItem itemTamCatorce;
    @FXML
    private MenuItem itemTamDieciseis;
    @FXML
    private MenuItem itemTamDieciocho;

    //MENU VER
    @FXML
    private MenuItem itemBarraEstado;
    @FXML
    private MenuItem itemBarraBotones;

    //MENU AYUDA
    @FXML
    private MenuItem itemAcercaDe;

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
    @FXML
    private ComboBox<String> comboBoxFuente;
    @FXML
    private ComboBox<String> comboBoxTamano;

    //AREA TEXTO
    @FXML
    private TextArea areaTexto;

    //ZONA BAJA
    //BARRA DE ESTADO
    @FXML
    private VBox contenedorBot;
    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnSiguiente;
    @FXML
    private Label labelCursor;
    @FXML
    private Label labelCuentaPalabras;
    @FXML
    private Label labelCuentaLineas;
    @FXML
    private Label labelCodific;

    /**
     * Inicializa la interfaz de usuario y configura los elementos iniciales.
     *
     * @param url La ubicación relativa de la raíz del objeto a inicializar.
     * @param rb El objeto ResourceBundle que contiene los recursos específicos
     * del local.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        comboBoxFuente.setValue("Arial");

        comboBoxFuente.getItems().addAll("Arial", "Courier", "Verdana");

        comboBoxTamano.setValue("12");

        comboBoxTamano.getItems().addAll("12", "14", "16", "18");

        // Agregar un listener al contenido del TextArea para detectar cambios
        areaTexto.textProperty().addListener((observable, oldValue, newValue) -> {
            // Actualizar la barra de estado cuando el contenido cambia
            actualizarBarraDeEstado();
        });

    }

    /////////////////////////////////////////////////////////////////////
    ///////////////////////FUNCIONES PARA EL MENU ARCHIVO
    /**
     * Abre una nueva ventana del editor de texto.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void nuevaVentana(ActionEvent event) {

        try {

            Parent root = FXMLLoader.load(getClass().getResource("FXMLVista.fxml"));

            Stage nuevaVentanaStage = new Stage();
            nuevaVentanaStage.setScene(new Scene(root));
            nuevaVentanaStage.show();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Abre un archivo de texto y carga su contenido en el área de texto.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void abrirArchivo(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Archivo de Texto");

        // Establece el filtro para mostrar archivos 
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Archivos de Texto (*.txt, *.csv, *.log)", "*.txt", "*.csv", "*.log");
        fileChooser.getExtensionFilters().add(extFilter);

        // Muestra el diálogo y espera a que el usuario seleccione un archivo
        archivoSeleccionado = fileChooser.showOpenDialog(new Stage());

        //agregarAlHistorial(archivoSeleccionado);
        if (archivoSeleccionado != null) {
            try {
                String contenido = modelo.leerArchivo(archivoSeleccionado);
                areaTexto.setText(contenido);
                modelo.agregarAlHistorial(archivoSeleccionado);
                mostrarHistorialArchivos();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Muestra el historial de archivos abiertos en el menú de documentos
     * recientes. Limpia el menú y agrega elementos del historial como opciones
     * de menú, cada uno asociado con un manejador de eventos que carga el
     * contenido del archivo seleccionado.
     */
    public void mostrarHistorialArchivos() {

        // Limpia el menú de documentos recientes
        menuRecientes.getItems().clear();

        // Agrega los elementos del historial al menú de documentos recientes
        for (File archivo : modelo.obtenerHistorialArchivos()) {
            MenuItem menuItem = new MenuItem(archivo.getName());

            // Asigna el EventHandler al hacer clic en el elemento del historial
            menuItem.setOnAction(e -> cargarContenidoArchivo(archivo));

            menuRecientes.getItems().add(menuItem);
        }

    }

    /**
     * Carga el contenido de un archivo en el área de texto. Actualiza el
     * historial al cargar el archivo.
     *
     * @param archivo El archivo cuyo contenido se cargará en el área de texto.
     */
    public void cargarContenidoArchivo(File archivo) {

        try {
            String contenido = modelo.leerArchivo(archivo);
            areaTexto.setText(contenido);
            modelo.agregarAlHistorial(archivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Guarda el contenido actual en el archivo seleccionado o muestra un cuadro
     * de diálogo para elegir la ubicación y el nombre del archivo.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void guardar(ActionEvent event) {

        // Si hay un archivo abierto, guardar en ese archivo directamente
        if (archivoSeleccionado != null) {
            try {
                modelo.guardarEnArchivoModelo(archivoSeleccionado, areaTexto.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Archivo de Texto");
            ExtensionFilter extFilter = new ExtensionFilter("Archivos de Texto (*.txt)", "*.txt");
            fileChooser.getExtensionFilters().add(extFilter);
            File archivoGuardado = fileChooser.showSaveDialog(new Stage());

            if (archivoGuardado != null) {
                try {
                    modelo.guardarEnArchivoModelo(archivoGuardado, areaTexto.getText());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Guarda el contenido del área de texto en el archivo especificado.
     * Actualiza las referencias a los archivos abierto y guardado.
     *
     * @param archivo El archivo en el que se guardará el contenido del área de
     * texto.
     */
    private void guardarEnArchivo(File archivo) {

        // Guarda el contenido del TextArea en el archivo seleccionado
        try {
            modelo.guardarEnArchivoModelo(archivo, areaTexto.getText());
            modelo.actualizarArchivos(archivo);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Muestra un cuadro de diálogo para seleccionar la ubicación y el nombre
     * del archivo, y guarda el contenido del área de texto en el archivo
     * seleccionado.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void guardarComo(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Archivo de Texto");
        ExtensionFilter extFilter = new ExtensionFilter("Archivos de Texto (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File archivoGuardar = fileChooser.showSaveDialog(new Stage());

        if (archivoGuardar != null) {
            try {
                modelo.guardarEnArchivoModelo(archivoGuardar, areaTexto.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Cierra la aplicación JavaFX
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void salir(ActionEvent event) {

        Platform.exit();// Método preferido para cerrar una aplicación JavaFX en vez de System.exit()

    }

    ///////////////FIN FUNCIONES MENU ARCHIVO//////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    ///////////////////////FUNCIONES PARA EL MENU EDICIóN
    /**
     * Busca coincidencias del texto ingresado en el área de texto y resalta la
     * primera ocurrencia. Muestra un cuadro de diálogo para ingresar el texto a
     * buscar. Actualiza la lista de índices de coincidencias. Si se encuentran
     * coincidencias, selecciona la primera coincidencia y la resalta. Si no se
     * encuentran coincidencias, muestra una alerta indicando que el texto no
     * fue encontrado.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void buscarCoincidencias(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buscar Coincidencias");
        dialog.setHeaderText("Ingrese el texto a buscar:");

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(texto -> {
            textoABuscar = texto;
            String contenido = areaTexto.getText();
            indiceCoincidencias = modelo.buscarCoincidencias(contenido, textoABuscar);

            if (!indiceCoincidencias.isEmpty()) {
                indiceActual = 0;
                seleccionarCoincidenciaActual();
            } else {
                mostrarAlerta("Texto no encontrado", "El texto especificado no se encontró en el documento.");
            }
        });
    }

    /**
     * Busca y selecciona la siguiente coincidencia en el área de texto.
     * Actualiza el índice de coincidencia actual.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void buscarSiguiente(ActionEvent event) {
        indiceActual = modelo.buscarSiguiente(indiceCoincidencias, indiceActual);
        seleccionarCoincidenciaActual();
    }

    /**
     * Busca y selecciona la coincidencia anterior en el área de texto.
     * Actualiza el índice de coincidencia actual.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void buscarAnterior(ActionEvent event) {
        indiceActual = modelo.buscarAnterior(indiceCoincidencias, indiceActual);
        seleccionarCoincidenciaActual();
    }

    /**
     * Selecciona la coincidencia actual en el área de texto. Utiliza los
     * índices almacenados en la lista de coincidencias.
     */
    private void seleccionarCoincidenciaActual() {
        if (!indiceCoincidencias.isEmpty() && indiceActual < indiceCoincidencias.size()) {
            areaTexto.selectRange(indiceCoincidencias.get(indiceActual), indiceCoincidencias.get(indiceActual) + textoABuscar.length());
        }
    }

    /**
     * Reemplaza las ocurrencias del texto ingresado por el usuario con el texto
     * nuevo. Muestra cuadros de diálogo para ingresar el texto a buscar y el
     * texto de reemplazo. Realiza el reemplazo en el contenido del área de
     * texto.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void reemplazar(ActionEvent event) {
        TextInputDialog dialogBuscar = new TextInputDialog();
        dialogBuscar.setTitle("Reemplazar");
        dialogBuscar.setHeaderText("Ingrese el texto que desea reemplazar:");
        Optional<String> resultadoBuscar = dialogBuscar.showAndWait();

        resultadoBuscar.ifPresent(textoABuscar -> {
            TextInputDialog dialogReemplazar = new TextInputDialog();
            dialogReemplazar.setTitle("Reemplazar");
            dialogReemplazar.setHeaderText("Ingrese el texto nuevo:");
            Optional<String> resultadoReemplazar = dialogReemplazar.showAndWait();

            resultadoReemplazar.ifPresent(textoReemplazo -> {
                String contenido = areaTexto.getText();
                String nuevoContenido = modelo.reemplazar(contenido, textoABuscar, textoReemplazo);
                areaTexto.setText(nuevoContenido);
            });
        });
    }

    /**
     * Muestra una alerta con el título y contenido proporcionados. Utilizado
     * para alertar al usuario cuando no se encuentran coincidencias durante la
     * búsqueda.
     *
     * @param titulo El título de la alerta.
     * @param contenido El contenido de la alerta.
     */
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
    /**
     * Aplica el estilo de negrita al texto seleccionado en el área de texto.
     * Obtiene el estilo actual y le agrega el estilo de negrita.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void estiloNegrita(ActionEvent event) {

        String estiloActual = areaTexto.getStyle();
        areaTexto.setStyle(estiloActual + "-fx-font-weight: bold;");

    }

    /**
     * Aplica el estilo de cursiva al texto seleccionado en el área de texto.
     * Obtiene el estilo actual y le agrega el estilo de cursiva.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void estiloCursiva(ActionEvent event) {

        String estiloActual = areaTexto.getStyle();
        areaTexto.setStyle(estiloActual + "-fx-font-weight: normal; -fx-font-style: italic;");

    }

    /**
     * Convierte a mayúsculas el texto seleccionado en el área de texto. Si no
     * hay texto seleccionado, no realiza ninguna acción.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void textoMayusculas(ActionEvent event) {

        String selecTexto = areaTexto.getSelectedText();

        // Verificar si se seleccionó algún texto
        if (!selecTexto.isEmpty()) {

            areaTexto.replaceSelection(selecTexto.toUpperCase());
        }

    }

    /**
     * Convierte a minúsculas el texto seleccionado en el área de texto. Si no
     * hay texto seleccionado, no realiza ninguna acción.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void textoMinusculas(ActionEvent event) {

        String selecTexto = areaTexto.getSelectedText();

        if (!selecTexto.isEmpty()) {

            areaTexto.replaceSelection(selecTexto.toLowerCase());
        }

    }

    /**
     * Cambia la fuente del texto en el área de texto a Arial.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void cambiarFuenteArial(ActionEvent event) {

        cambiarFuente(event);

    }

    /**
     * Cambia la fuente del texto en el área de texto a Courier.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void cambiarFuenteCourier(ActionEvent event) {

        cambiarFuente(event);

    }

    /**
     * Cambia la fuente del texto en el área de texto a Verdana.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void cambiarFuenteVerdana(ActionEvent event) {

        cambiarFuente(event);

    }

    /**
     * Cambia la fuente del texto en el área de texto según la fuente
     * seleccionada en el ComboBox. Agrega un listener al ComboBox para manejar
     * el cambio de selección y aplicar la nueva fuente.
     */
    @FXML
    private void cambiarFuenteCombo() {

        // Agrega un listener al ComboBox para manejar el cambio de selección
        comboBoxFuente.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                areaTexto.setStyle("-fx-font-family: " + newValue + ";");
            }
        });
    }

    /**
     * Cambia la fuente del texto en el área de texto según el MenuItem que
     * desencadena el evento.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    private void cambiarFuente(ActionEvent event) {

        MenuItem menuItem = (MenuItem) event.getSource(); // Obteniene el MenuItem que desencadena el evento
        String fuente = menuItem.getText(); // Obtiene el texto del MenuItem, que debería ser el nombre de la fuente
        areaTexto.setStyle("-fx-font-family: " + fuente + ";");
    }

    /**
     * Cambia el tamaño del texto en el área de texto a 12 puntos.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void cambiarTamanio12(ActionEvent event) {
        cambiarTamanioTexto(12);
    }

    /**
     * Cambia el tamaño del texto en el área de texto a 14 puntos.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void cambiarTamanio14(ActionEvent event) {
        cambiarTamanioTexto(14);
    }

    /**
     * Cambia el tamaño del texto en el área de texto a 16 puntos.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void cambiarTamanio16(ActionEvent event) {
        cambiarTamanioTexto(16);
    }

    /**
     * Cambia el tamaño del texto en el área de texto a 18 puntos.
     *
     * @param event El evento de acción que desencadena esta operación.
     */
    @FXML
    private void cambiarTamanio18(ActionEvent event) {
        cambiarTamanioTexto(18);
    }

    /**
     * Cambia el tamaño del texto en el área de texto según el tamaño
     * proporcionado.
     *
     * @param tamanio El tamaño del texto en puntos.
     */
    private void cambiarTamanioTexto(int tamanio) {
        areaTexto.setStyle("-fx-font-size: " + tamanio + "px;");
    }

    /**
     * Agrega un listener al ComboBox de tamaños de fuente para manejar el
     * cambio de selección. Cuando se selecciona un tamaño de fuente en el
     * ComboBox, actualiza el tamaño del texto en el área de texto según el
     * nuevo valor seleccionado.
     */
    @FXML
    private void cambiarTamanoCombo() {

        // Agrega un listener al ComboBox para manejar el cambio de selección
        comboBoxTamano.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                areaTexto.setStyle("-fx-font-size: " + newValue + "px;");
            }
        });
    }

    ///////////////FIN FUNCIONES MENU FORMATO//////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    ///////////////////////FUNCIONES PARA EL MENU VER
    /**
     * Maneja el evento de acción para mostrar u ocultar la barra de botones. Si
     * la barra de botones está visible, la oculta; de lo contrario, la hace
     * visible. Este método está vinculado a la acción del menú correspondiente.
     *
     * @param event El evento de acción que desencadena este método.
     */
    @FXML
    private void verUocultarBotones(ActionEvent event) {

        if (menuBotones.isVisible()) {

            menuBotones.setVisible(false);

        } else {

            menuBotones.setVisible(true);

        }
    }

    /**
     * Maneja el evento de acción para mostrar u ocultar la barra de estado. Si
     * la barra de estado está visible, la oculta; de lo contrario, la hace
     * visible. Este método está vinculado a la acción del menú correspondiente.
     *
     * @param event El evento de acción que desencadena este método.
     */
    @FXML
    private void verUocultarBarraEstado(ActionEvent event) {

        if (contenedorBot.isVisible()) {

            contenedorBot.setVisible(false);

        } else {

            contenedorBot.setVisible(true);

        }

    }

    ///////////////FIN FUNCIONES MENU VER//////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////
    ///////////////////////FUNCIONES PARA EL MENU AYUDA
    /**
     * Maneja el evento de acción para abrir la ventana "Acerca de". Carga y
     * muestra una nueva ventana que contiene información sobre la aplicación.
     * Este método está vinculado a la acción del menú correspondiente.
     *
     * @param event El evento de acción que desencadena este método.
     */
    @FXML
    private void abrirAcercaDe(ActionEvent event) {

        try {
            Parent root = FXMLLoader.load(getClass().getResource("FXMLVistaAcercaDe.fxml"));

            Stage acercaDeStage = new Stage();
            Scene scene = new Scene(root);
            acercaDeStage.setTitle("Acerca de");
            acercaDeStage.setScene(scene);

            // Configura la ventana como modal
            acercaDeStage.initModality(Modality.APPLICATION_MODAL);

            acercaDeStage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    ///////////////FIN FUNCIONES MENU AYUDA//////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    ////////////////FUNCIONES BARRA ESTADO/////////////////////////////////////////////////
    /**
     * Actualiza la barra de estado con información relevante, como la posición
     * del cursor, el recuento de palabras y líneas, y la codificación del
     * archivo.
     */
    private void actualizarBarraDeEstado() {

        // obtener la posición del cursor desde el area de texto
        int posicionCursor = areaTexto.getCaretPosition();
        labelCursor.setText("Posición del Cursor: " + posicionCursor);

        // obtener el recuento de palabras y lineas desde el contenido del area de texto
        String contenido = areaTexto.getText();
        int recuentoPalabras = obtenerRecuentoPalabras();
        int recuentoLineas = obtenerRecuentoLineas();

        labelCuentaLineas.setText("Recuento de Líneas: " + recuentoLineas);

        labelCuentaPalabras.setText("Recuento de Palabras: " + recuentoPalabras);

        // obtener la información de codificacin desde el archivo abierto
        String codificacion = obtenerCodificacion();
        labelCodific.setText("Codificación: " + codificacion);
    }

    /**
     * Obtiene el recuento de palabras en el texto actual del área de texto.
     *
     * @return El recuento de palabras.
     */
    private int obtenerRecuentoPalabras() {
        String texto = areaTexto.getText();
        if (texto == null || texto.isEmpty()) {
            return 0;
        }
        //dividir el texto en palabras
        String[] palabras = texto.split("\\s+");
        return palabras.length;
    }

    /**
     * Obtiene el recuento de líneas en el texto actual del área de texto.
     *
     * @return El recuento de líneas.
     */
    private int obtenerRecuentoLineas() {
        String texto = areaTexto.getText();
        if (texto == null || texto.isEmpty()) {
            return 0;
        }

        // dividir el texto en líneas y contar la cantidad de lineas
        String[] lineas = texto.split("\\r?\\n");
        return lineas.length;
    }

    /**
     * Obtiene la codificación del archivo actual o "UTF-8" si no hay archivo
     * abierto.
     *
     * @return La codificación del archivo actual.
     */
    private String obtenerCodificacion() {
        if (archivoSeleccionado != null) {
            return modelo.obtenerCodificacionArchivo(archivoSeleccionado);
        } else {
            return "UTF-8";
        }
    }

    ///////////////////////////FIN FUNCIONES BARRA ESTADO//////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////
}
