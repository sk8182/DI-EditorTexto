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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 *
 * @author julio
 */
public class FXMLVistaController implements Initializable {

    private Label label;
    @FXML
    private MenuItem itemArial;
    @FXML
    private TextArea areaTexto;
    @FXML
    private MenuItem itemCourier;
    @FXML
    private MenuItem itemVerdana;
    @FXML
    private MenuItem itemNuevo;
    @FXML
    private MenuItem itemAbrir;
    @FXML
    private MenuItem itemGuardar;

    private File archivoGuardado;  // Mantén una referencia al archivo guardado
    @FXML
    private MenuItem itemGuardarComo;
    @FXML
    private MenuItem itemSalir;
    @FXML
    private MenuItem itemBuscar;
    @FXML
    private MenuItem itemReemplazar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void cambiarFuenteArial(ActionEvent event) {

        cambiarFuente("Arial");

    }

    @FXML
    private void cambiarFuenteCourier(ActionEvent event) {

        cambiarFuente("Courier");

    }

    @FXML
    private void cambiarFuenteVerdana(ActionEvent event) {

        cambiarFuente("Verdana");

    }

    private void cambiarFuente(String nombreFuente) {
        Font nuevaFuente = new Font(nombreFuente, areaTexto.getFont().getSize());
        areaTexto.setFont(nuevaFuente);
    }

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

        if (archivoSeleccionado != null) {
            // Lee el contenido del archivo y lo carga en el TextArea
            try (BufferedReader br = new BufferedReader(new FileReader(archivoSeleccionado))) {
                String linea;
                StringBuilder contenido = new StringBuilder();

                while ((linea = br.readLine()) != null) {
                    contenido.append(linea).append("\n");
                }

                areaTexto.setText(contenido.toString());
            } catch (IOException e) {
                e.printStackTrace();

            }

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

    @FXML
    private void buscarCoincidencias(ActionEvent event) {
        
        
        //A ESTO DARLE UNA VUELTA, PUEDO HACER UNA BARRA QUE SE MUESTRE ABAJO Y SI NO HAY PALABRA A BUSCAR QUE SE OCULTE O CERRARLA CON UNA X O ALGO 
        
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Buscar Coincidencias");
        dialog.setHeaderText("Ingrese el texto a buscar:");

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(textoABuscar -> {
            String contenido = areaTexto.getText();
            int index = contenido.indexOf(textoABuscar);
      
            if (index != -1) {
                areaTexto.selectRange(index, index + textoABuscar.length());
            } else {
                mostrarAlerta("Texto no encontrado", "El texto especificado no se encontró en el documento.");
            }
        });
        
        //A ESTO DARLE UNA VUELTA, PUEDO HACER UNA BARRA QUE SE MUESTRE ABAJO Y SI NO HAY PALABRA A BUSCAR QUE SE OCULTE O CERRARLA CON UNA X O ALGO 
     
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

}
